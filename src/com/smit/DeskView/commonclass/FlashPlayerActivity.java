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
        setContentView(R.layout.flash_player);
        
        initVariables();
        
       /* String urlToLoad = "file:///data/data/com.smit.EasyLauncher/temp/fflvplayer.html";
        loadWidgetShellIndexHtml(urlToLoad);*/
        mWebView.loadUrl(getIntent().getExtras().getString("media"));
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		if(mWebView != null)
    		{
    			//STOP FLASH PLAYER playing
    			//mWebView.loadUrl("javascript:sendToActionScript();");
    			mWebView.loadUrl("about:blank");
    			finish();
    		}
    		//return true;
    	}
    	return false;
    } 
    
    
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
        	//横向
      	}else{
           //竖向
    	}
		//if(mWebView != null)
		//{
			//mWebView.loadUrl("javascript:resetJSAreas()");
		//}
    }
    
    //initialize some variables and some other things.
    private void initVariables()
    {
    	mWebView = (WebView) this.findViewById(R.id.flashPlayerWebView); 
    	mWebSetting = mWebView.getSettings();
    	if(mWebView == null || mWebSetting == null)
    		return;

    	setBrowserAttribute();
    }
    
    private void setBrowserAttribute()
    {
    	if(mWebSetting == null)
    	{
    		mWebSetting = mWebView.getSettings();
    	}
    	
    	/*
    		mWebView.setWebViewClient(new WebViewClient(){  
    	    public boolean shouldOverrideUrlLoading(WebView view, String url) {  
    	        view.loadUrl(url);  
    	        return true;  
    	    }  
    	}); 
    	*/ 
    	
    	mWebSetting.setJavaScriptEnabled(true);
    	mWebSetting.setSupportZoom(true);
    	mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
    	mWebSetting.setAllowFileAccess(true);
    	mWebSetting.setPluginsEnabled(true);
    	//mWebSetting.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
    }
    
    public void loadWidgetShellIndexHtml(String indexHTMLFile)
    {
    	//mWebView.loadUrl("file:///sdcard/widgetshell_android/index.html");
    	//mWebView.loadUrl("file:///sdcard/testJsCall.html");
    	//mWebView.loadUrl("file:///sdcard/testJsCall_1.html");
    	//mWebView.loadUrl("file:///sdcard/testJSMessageBox.html");
    	//mWebView.loadUrl("file:///android_asset/TEST_HTML/test_pop_out_webkit.html");
    	//mWebView.loadUrl("file:///android_asset/JSLib/HorizontalList/testHorizontalList.html");
    	//mWebView.loadUrl("file:///android_asset/TEST_HTML/test.html");
    	//mWebView.loadUrl("file:///android_asset/JSLib/PicGridView/testPicGridView.html");
    	//mWebView.loadUrl("file:///android_asset/JSLib/EpisodeGridView/testEpisodeGridView.html");
    	//mWebView.loadUrl("file:///android_asset/JSLib/JSMessageBox/testJSMessageBox.html");
    	//mWebView.loadUrl("file:///android_asset/TEST_HTML/testWindow.html");
    	
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_133/index.html"); //BTV北京
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_134/index.html"); //BTV财经
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_135/index.html"); //BTV公共
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_136/index.html"); //BTV科教
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_137/index.html"); //BTV青少
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_138/index.html"); //BTV生活
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_139/index.html"); //BTV食全食美
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_140/index.html"); //BTV时尚装苑
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_142/index.html"); //BTV养生堂
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_62/index.html");  //酷六动漫
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_63/index.html");  //酷六生活
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_64/index.html");  //酷六影院
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_66/index.html");  //酷六剧场
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_67/index.html");  //酷六综艺
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_49/index.html");  //搜狐传奇
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_51/index.html");  //搜狐纪录片
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_42/index.html");  //优酷影院
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_43/index.html");  //优酷剧场
    	//mWebView.loadUrl("file:///android_asset/Widget/Video/ETWidgets_55/index.html");  //黑豆剧场
    	//;
    	Log.i("LoadUrl", indexHTMLFile);
    	if(mWebView != null)
    	{
    		mWebView.loadUrl(indexHTMLFile);
    		//mWebView.loadUrl("http://www.g.cn");
    	}
    }

    private WebView mWebView = null;
    private WebSettings mWebSetting = null;
    


  
}

