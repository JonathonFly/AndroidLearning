package com.jonathonfly.myfirstapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE = "com.jonathonfly.myfirstapp.MESSAGE";

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
		// TODO Auto-generated method stub

	}

	private void openSearch() {
		// TODO Auto-generated method stub

	}

/*	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}*/

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
