package com.jonathonfly.myfirstapp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jonathonfly.myfirstapp.utils.ContextUtil;
import com.jonathonfly.myfirstapp.webservice.site.model.SiteInfoModel;
import com.jonathonfly.myfirstapp.webservice.site.parser.SaxSiteParser;
import com.jonathonfly.myfirstapp.webservice.site.parser.SiteParser;

public class ResultActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_main);
		getBundle();
		new Thread(runnable).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_search:
			openSearch();
			return true;
		case R.id.action_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSettings() {

	}

	private void openSearch() {

	}

	private void getBundle() {
		Bundle bundle = this.getIntent().getExtras();
		custID = bundle.getString("custID");
		custName = bundle.getString("custName");
		siteName = bundle.getString("siteName");
		pageNo = bundle.getString("pageNo");
		pageSize = bundle.getString("pageSize");
	}

	// 新建一个线程用来访问webservice，防止主UI线程被阻塞
	Runnable runnable = new Runnable() {
		@Override
		public void run() {

			HttpTransportSE ht = new HttpTransportSE(SERVICE_URL, TIME_OUT);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			SoapObject request = new SoapObject(SERVICE_NS, "siteInfoQuery");
			request.addProperty("custID", custID);
			request.addProperty("custName", custName);
			request.addProperty("siteName", siteName);
			request.addProperty("pageIndex", pageNo);
			request.addProperty("pageSize", pageSize);
			envelope.bodyOut = request;

			// 添加HeaderProperty信息，解决调用call的时候报java.io.EOFException错误
			ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
			headerPropertyArrayList.add(new HeaderProperty("Connection",
					"close"));

			String name = "";
			try {
				// 调用webService
				ht.call(null, envelope, headerPropertyArrayList);
				if (envelope.getResponse() != null) {
					SoapObject result = (SoapObject) envelope.bodyIn;
					name = result.getProperty(0).toString();
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 将webservice结果传送到handler
			Message msg = myHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putString("siteInfoQueryResult", name);
			msg.setData(data);
			myHandler.sendMessage(msg);
		}
	};

	// handler接收新线程传送过来的结果，将其赋值到UI界面
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			Bundle data = msg.getData();
			String siteResult = data.getString("siteInfoQueryResult");

			List<SiteInfoModel> siteList = getSiteFromXmlString(siteResult);
			ListView listView = (ListView) findViewById(R.id.sitelistView);

			List<HashMap<String, Object>> data1 = new ArrayList<HashMap<String, Object>>();
			if (siteList != null && siteList.size() > 0) {
				
				for (Iterator<SiteInfoModel> iter = siteList.iterator(); iter.hasNext(); ) {  
					SiteInfoModel siteInfoModel=(SiteInfoModel)iter.next();
					if(siteInfoModel.getAddress()==null && siteInfoModel.getCity()==null &&siteInfoModel.getCountry()==null &&siteInfoModel.getSiteName()==null){
						siteList.remove(siteInfoModel);
					}
				}  
			}
			if (siteList != null && siteList.size() > 0) {
				int i = 1;
				for (SiteInfoModel siteInfoModel : siteList) {

					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put("num", i++);
					item.put("siteName", siteInfoModel.getSiteName());
					item.put("address", siteInfoModel.getAddress());
					item.put("country", siteInfoModel.getCountry());
					item.put("city", siteInfoModel.getCity());
					data1.add(item);
				}
			}

			// Context c = ContextUtil.getInstance();

			// 创建SimpleAdapter适配器将数据绑定到item显示控件上
			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
					data1, R.layout.site_items, new String[] { "num",
							"siteName", "address", "country", "city" },
					new int[] { R.id.siteItem_num, R.id.siteItem_siteName,
							R.id.siteItem_address, R.id.siteItem_country,
							R.id.siteItem_city });
			// 实现列表的显示
			listView.setAdapter(adapter);
			// 条目点击事件
			listView.setOnItemClickListener(new ItemClickListener());

		}
	};

	// 从xmlString中获取java bean
	public List<SiteInfoModel> getSiteFromXmlString(String xmlString) {
		List<SiteInfoModel> siteList = new ArrayList<SiteInfoModel>();
		if (xmlString != null && !xmlString.equals("")) {
			try {
				InputStream is = new ByteArrayInputStream(
						xmlString.getBytes("UTF-8"));
				parser = new SaxSiteParser(); // 创建SaxBookParser实例
				if (is != null) {
					siteList = parser.parse(is); // 解析输入流
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			siteList = null;
		}
		return siteList;
	}

	// 获取点击事件
	private final class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;
			HashMap<String, Object> data = (HashMap<String, Object>) listView
					.getItemAtPosition(position);
			if (data.containsKey("siteName") && data.get("siteName")!=null) {
				String siteName = data.get("siteName").toString();
				if (siteName != null && !siteName.equals("")) {
					Toast.makeText(getApplicationContext(), siteName, 1).show();
				}
			}
		}
	}

	private static final String TAG = "XML";
	private SiteParser parser;
	final static String SERVICE_NS = "http://service.ws.ideal.com/";
	final static String SERVICE_URL = "http://101.226.172.121/netcare-ws/siteWs";
	final static Integer TIME_OUT = 10000;
	private String custID;
	private String custName;
	private String siteName;
	private String pageNo;
	private String pageSize;
	private Handler myHandler = new MyHandler();

}
