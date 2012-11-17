package com.missionhub.activity;

import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LicensesActivity extends BaseActivity {
	
	private WebView mWebView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mWebView = new WebView(this);
		mWebView.setBackgroundColor(0xFFEEEEEE);
		mWebView.setWebViewClient(new WebViewClient() {  
		  @Override  
		  public boolean shouldOverrideUrlLoading(WebView view, String url)  
		  {  
		    view.loadUrl(url);
		    return true;
		  }  
		}); 
		mWebView.loadUrl("file:///android_asset/licenses.html");
		
		setContentView(mWebView);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}