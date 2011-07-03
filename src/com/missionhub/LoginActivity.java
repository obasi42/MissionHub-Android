package com.missionhub;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class LoginActivity extends Activity {
	
	public static final String TAG = "LoginActivity";
	public static String token;
	public static boolean isLoggedIn = false;
	
	private ProgressDialog mProgressDialog;
	private WebView mWebView;
	private Button mCloseBtn;
	private String wvUrl = Config.oauthUrl + "/authorize?display=touch&simple=true&response_type=code&redirect_uri=" + Config.oauthUrl + "/done.json&client_id=" + Config.oauthClientId + "&scope=" + Config.oauthScope;
	private String wvLogoutUrl = Config.baseUrl + "/auth/facebook/logout";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.login);
		
		mProgressDialog = ProgressDialog.show(LoginActivity.this, "", LoginActivity.this.getString(R.string.alert_loading), true);
		
		mCloseBtn = (Button) findViewById(R.id.btn_logout_close);
		
		mWebView = (WebView) findViewById(R.id.webview_login);
		mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.getSettings().setSupportZoom(false);
	    mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    mWebView.setWebViewClient(new InternalWebViewClient());
	    mWebView.loadUrl(wvUrl);
	    
	}
	
	private void returnWithToken() {
		mWebView.stopLoading();
		mProgressDialog.dismiss();
		Intent i = new Intent();
		i.putExtra("token", token);
		this.setResult(RESULT_OK, i);
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	private class InternalWebViewClient extends WebViewClient {
		
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	    
	    @Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    	mProgressDialog.show();
	    }
	    
	    @Override
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	if (mProgressDialog.isShowing()) {
	    		mProgressDialog.hide();
	    	}
	    	//TODO Throw Error
	    }
	    
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	Log.e(TAG, "WebView Loaded: " + url);
	    	if (mProgressDialog.isShowing()) {
	    		mProgressDialog.hide();
	    	}
	    	Uri uri = Uri.parse(url);
	    	
	    	String authorization = uri.getQueryParameter("authorization");
	    	if (authorization != null && uri.getPath().equalsIgnoreCase("/oauth/authorize")) {
	    		mWebView.stopLoading();
	    		mWebView.setVisibility(View.GONE);
	    		mCloseBtn.setVisibility(View.GONE);
	    		mWebView.loadUrl(Config.oauthUrl + "/grant.json?authorization=" + authorization);
	    		return;
	    	}
	    	String code = uri.getQueryParameter("code");
	    	if (code != null && uri.getPath().equalsIgnoreCase("/oauth/done.json")) {
	    		mWebView.stopLoading();
	    		getTokenFromCode(code);
	    	}
	    }
	}
	
	private void getTokenFromCode(String code) {
		AsyncHttpClient client = new AsyncHttpClient();
		
		RequestParams params = new RequestParams();
		params.put("client_id", Config.oauthClientId);
		params.put("client_secret", Config.oauthClientSecret);
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		params.put("scope", Config.oauthScope);
		params.put("redirect_uri", Config.oauthUrl + "/done.json");
		
		client.post(Config.oauthUrl + "/access_token", params, new JsonHttpResponseHandler() {
			@Override
		     public void onStart() {
		         mProgressDialog.show();
		     }

		     @Override
		     public void onSuccess(JSONObject response) {
		         try {
					Log.i(TAG, "GET TOKEN: " + response.toString(4));
					
					token = response.getString("access_token");
					Log.e(TAG, "TOKEN: " + token);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		         //returnWithToken();
		     }
		 
		     @Override
		     public void onFailure(Throwable e) {
		         Log.i(TAG, "GET TOKEN FAIL: " + e.toString());
		     }

		     @Override
		     public void onFinish() {
		         mProgressDialog.hide();
		     }
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	finish();
	    	overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void clickClose(View view) {
		mWebView.stopLoading();
		mProgressDialog.dismiss();
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}	
}