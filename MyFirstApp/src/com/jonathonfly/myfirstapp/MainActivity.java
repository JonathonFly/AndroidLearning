package com.jonathonfly.myfirstapp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnSuggestionListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	private SearchView searchView;
	private Context context;
	private Handler myHandler = new MyHandler();
	// schedule executor
	private ScheduledExecutorService scheduledExecutor = Executors
			.newScheduledThreadPool(10);
	private String currentSearchTip;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		/*
		 * if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy
		 * policy = new StrictMode.ThreadPolicy.Builder() .permitAll().build();
		 * StrictMode.setThreadPolicy(policy); }
		 */
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		Tab tab = actionBar
				.newTab()
				.setText(R.string.artist)
				.setTabListener(
						new MyTabListener<ArtistFragment>(this, "artist",
								ArtistFragment.class));
		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText(R.string.album)
				.setTabListener(
						new MyTabListener<AlbumFragment>(this, "album",
								AlbumFragment.class));
		actionBar.addTab(tab);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(
					"tab", 0));
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		searchView.setQueryHint("查询");

		/*Cursor cursor =null;
		if(currentSearchTip!=null && !currentSearchTip.equals("")){
			cursor =getTestCursor(currentSearchTip);
		}*/
		
		final Cursor cursor =getTestCursor(currentSearchTip);

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.mytextview, cursor, new String[] { "tb_name" },
				new int[] { R.id.textview });
		
		searchView.setSuggestionsAdapter(adapter);
		
		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			public boolean onSuggestionSelect(int i) {
/*				Toast.makeText(getApplicationContext(),
						"select:" + i+" of "+ cursor.getString(1) , Toast.LENGTH_SHORT).show();*/
				searchView.setQuery(cursor.getString(1), true);
				return true;
			}

			public boolean onSuggestionClick(int i){
/*				Toast.makeText(getApplicationContext(),
						"select: " + i+" of "+ cursor.getString(1), Toast.LENGTH_SHORT).show();*/
				searchView.setQuery(cursor.getString(1), true);
				return true;
			}
		});

		/*
		 * 表示输入框文字listener，包括public boolean onQueryTextSubmit(String
		 * query)开始搜索listener，public boolean onQueryTextChange(String
		 * newText)输入框内容变化listener，两个函数
		 */
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			public boolean onQueryTextSubmit(String query) {
				Toast.makeText(getApplicationContext(),
						"begin search:" + query, Toast.LENGTH_SHORT).show();
				return true;
			}

			public boolean onQueryTextChange(String newText) {
				if (newText != null && newText.length() > 0) {
					currentSearchTip = newText;
//					cursor=getTestCursor(currentSearchTip);
//					showSearchTip(newText);
				}
				return true;
			}
		});

		// 编辑框内容为空点击取消的x按钮，编辑框收缩
		searchView.setOnCloseListener(new OnCloseListener() {
			@Override
			public boolean onClose() {
				return true;
			}
		});

		// show keyboard
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		// 在内容为空时不显示取消的x按钮，内容不为空时显示
		searchView.onActionViewExpanded();

		// 编辑框后显示search按钮
		searchView.setSubmitButtonEnabled(true);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * private void hideSoftInput() { // 隐藏输入法键盘 InputMethodManager
	 * inputMethodManager; inputMethodManager = (InputMethodManager)
	 * getSystemService(Context.INPUT_METHOD_SERVICE);
	 * 
	 * if (inputMethodManager != null) { View v =
	 * MainActivity.this.getCurrentFocus(); if (v == null) { return; }
	 * 
	 * inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
	 * InputMethodManager.HIDE_NOT_ALWAYS); searchView.clearFocus(); } }
	 */

	// 添加suggestion需要的数据
	public Cursor getTestCursor(String name) {

		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
				this.getFilesDir() + "/my.db3", null);

		Cursor cursor = null;
//		String querySql = "select * from tb_test where tb_name like '"+name+"%'";
		String querySql = "select * from tb_test";
		cursor = db.rawQuery(querySql, null);
		return cursor;
	}

	public void showSearchTip(String newText) {
		// excute after 500ms, and when excute, judge current search tip and
		// newText
		schedule(new SearchTipThread(newText), 500);
	}

	class SearchTipThread implements Runnable {
		String newText;

		public SearchTipThread(String newText) {
			this.newText = newText;
		}

		public void run() {
			// keep only one thread to load current search tip, u can get data
			// from network here
			if (newText != null && newText.equals(currentSearchTip)) {
				List<String> relativeWord = getRelativeWord(currentSearchTip);
				myHandler.sendMessage(myHandler.obtainMessage(1, relativeWord));
			}
		}
	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				List<String> msgObj = (List<String>) msg.obj;
				if (msgObj != null && msgObj.size() > 0) {
					Toast.makeText(getApplicationContext(), msgObj.get(0),
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}

	public ScheduledFuture<?> schedule(Runnable command, long delayTimeMills) {
		return scheduledExecutor.schedule(command, delayTimeMills,
				TimeUnit.MILLISECONDS);
	}

	private List<String> getRelativeWord(String currentSearchTip) {
		List<String> allWords = new ArrayList<String>();
		allWords.add("relative");
		allWords.add("notrelative");
		allWords.add("tall");
		allWords.add("short");

		List<String> relativeWords = new ArrayList<String>();
		for (int i = 0; allWords.size() > 0 && i < allWords.size(); i++) {
			if (allWords.get(i).startsWith(currentSearchTip)) {
				relativeWords.add(allWords.get(i));
			}
		}
		return relativeWords;
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
		case R.id.setting_first:
			setting_first_click();
			return true;
		case R.id.setting_second:
			setting_second_click();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSettings() {
		/*
		 * Toast toast = Toast.makeText(getApplicationContext(), "settings",
		 * Toast.LENGTH_LONG); toast.setGravity(Gravity.CENTER, 0, 0);
		 * toast.show();
		 */
	}

	private void setting_first_click() {
		Toast toast = Toast.makeText(getApplicationContext(),
				"setting first click!", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void setting_second_click() {
		Toast toast = Toast.makeText(getApplicationContext(),
				"setting second click!", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void openSearch() {
		/*
		 * Toast toast = Toast.makeText(getApplicationContext(), "search",
		 * Toast.LENGTH_LONG); toast.setGravity(Gravity.CENTER, 0, 0);
		 * toast.show();
		 */
	}

	/*
	 * public void sendMessage(View view) { Intent intent = new Intent(this,
	 * DisplayMessageActivity.class); EditText editText = (EditText)
	 * findViewById(R.id.edit_message); String message =
	 * editText.getText().toString(); intent.putExtra(EXTRA_MESSAGE, message);
	 * startActivity(intent); }
	 */

	public void queryWebservice(View view) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ResultActivity.class);

		EditText editText1 = (EditText) findViewById(R.id.custID_content);
		String custID = editText1.getText().toString();
		custID = custID.trim();

		EditText editText2 = (EditText) findViewById(R.id.custName_content);
		String custName = editText2.getText().toString();
		custName = custName.trim();

		EditText editText3 = (EditText) findViewById(R.id.siteName_content);
		String siteName = editText3.getText().toString();
		siteName = siteName.trim();

		EditText editText4 = (EditText) findViewById(R.id.pageNo_content);
		String pageNo = editText4.getText().toString();
		pageNo = pageNo.trim();

		EditText editText5 = (EditText) findViewById(R.id.pageSize_content);
		String pageSize = editText5.getText().toString();
		pageSize = pageSize.trim();

		Bundle bundle = new Bundle();
		bundle.putString("custID", custID);
		bundle.putString("custName", custName);
		bundle.putString("siteName", siteName);
		bundle.putString("pageNo", pageNo);
		bundle.putString("pageSize", pageSize);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
