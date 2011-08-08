package com.smit.DeskView.commonclass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.smit.EasyLauncher.R;

public class FlashPlayerActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.flash_player);

		initVariables();
		
		setWebViewClient();
		
		String string=getIntent().getExtras().getString("media");
		mWebView.loadUrl(getIntent().getExtras().getString("media"));	
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横向
		} else {
			// 竖向
		}
		// if(mWebView != null)
		// {
		// mWebView.loadUrl("javascript:resetJSAreas()");
		// }
	}

	// initialize some variables and some other things.
	private void initVariables() {
		mWebView = (WebView) this.findViewById(R.id.flashPlayerWebView);
		mWebSetting = mWebView.getSettings();
		if (mWebView == null || mWebSetting == null)
			return;

		setBrowserAttribute();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mWebView.loadUrl("about:blank");
	}
	
	private void setBrowserAttribute() {
		if (mWebSetting == null) {
			mWebSetting = mWebView.getSettings();
		}

		mWebSetting.setJavaScriptEnabled(true);
		mWebSetting.setSupportZoom(true);
		mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		// mWebSetting.setAllowFileAccess(true);
		mWebSetting.setPluginsEnabled(true);
	}
	
	 private void setWebViewClient()
	    {
		  
	    	WebViewClient wvc = new WebViewClient() {
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                mWebView.loadUrl(url);
	                // 记得消耗掉这个事件。给不知道的朋友再解释一下，Android中返回True的意思就是到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
	                return true;
	            }

	            @Override
	            public void onPageStarted(WebView view, String url, Bitmap favicon){
	                //Toast.makeText(getApplicationContext(), "WebViewClient.onPageStarted", Toast.LENGTH_SHORT).show();
	                super.onPageStarted(view, url, favicon);
	            }

	            @Override
	            public void onPageFinished(WebView view, String url) {
	                //Toast.makeText(getApplicationContext(), "WebViewClient.onPageFinished", Toast.LENGTH_SHORT).show();
	                super.onPageFinished(view, url);
	            }

	            @Override
	            public void onLoadResource(WebView view, String url) {
	                //Toast.makeText(getApplicationContext(), "WebViewClient.onLoadResource", Toast.LENGTH_SHORT).show();
	                super.onLoadResource(view, url);
	            }
	        };
	       
	        mWebView.setWebViewClient(wvc);
	    }

	public void loadWidgetShellIndexHtml(String indexHTMLFile) {

		Log.i("LoadUrl", indexHTMLFile);
		if (mWebView != null) {
			mWebView.loadUrl(indexHTMLFile);
		}
	}

	private WebView mWebView = null;
	private WebSettings mWebSetting = null;

}
