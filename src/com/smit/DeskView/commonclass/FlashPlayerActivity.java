package com.smit.DeskView.commonclass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
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

		/*
		 * String urlToLoad =
		 * "file:///data/data/com.smit.EasyLauncher/temp/fflvplayer.html";
		 * loadWidgetShellIndexHtml(urlToLoad);
		 */
		String string=getIntent().getExtras().getString("media");
		mWebView.loadUrl(getIntent().getExtras().getString("media"));
		
		//mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// mWebView.set
	}

	/*
	 * public boolean onKeyDown(int keyCode, KeyEvent event) { if(keyCode ==
	 * KeyEvent.KEYCODE_BACK) { if(mWebView != null) {
	 * 
	 * mWebView.loadUrl("about:blank"); finish(); } } return false; }
	 */

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// ∫·œÚ
		} else {
			//  ˙œÚ
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

	private void setBrowserAttribute() {
		if (mWebSetting == null) {
			mWebSetting = mWebView.getSettings();
		}

		/*
		 * mWebView.setWebViewClient(new WebViewClient(){ public boolean
		 * shouldOverrideUrlLoading(WebView view, String url) {
		 * view.loadUrl(url); return true; } });
		 */

		mWebSetting.setJavaScriptEnabled(true);
		mWebSetting.setSupportZoom(true);
		mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		// mWebSetting.setAllowFileAccess(true);
		mWebSetting.setPluginsEnabled(true);
	}

	/*public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}*/

	public void loadWidgetShellIndexHtml(String indexHTMLFile) {

		Log.i("LoadUrl", indexHTMLFile);
		if (mWebView != null) {
			mWebView.loadUrl(indexHTMLFile);
			// mWebView.loadUrl("http://www.g.cn");
		}
	}

	private WebView mWebView = null;
	private WebSettings mWebSetting = null;

}
