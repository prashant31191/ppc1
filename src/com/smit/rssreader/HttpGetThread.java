package com.smit.rssreader;

import java.net.URL;

import android.os.Handler;
import android.os.Message;

public class HttpGetThread extends Thread{
	private RSSFeed feed ; 
	private String urlToRssFeed ;
	private Handler handler ;
	private String cate ;

	public HttpGetThread(String strUrl,Handler handler,String cate){
		this.urlToRssFeed = strUrl ;
		this.handler = handler ;
		feed = new RSSFeed();
		this.cate = cate ;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		URL url;
		try {
			url = new URL(urlToRssFeed);
			PullParseRss pullParse = new PullParseRss();
			pullParse.ParseRss(url.openStream());
			feed = pullParse.getFeed();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			feed = null ;
			e.printStackTrace();
		}finally {
			Message mg = Message.obtain();  
			mg.obj = new FeedCategory(feed ,cate ,urlToRssFeed) ;  
			handler.sendMessage(mg);  
		}
	}

}
