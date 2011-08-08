package com.smit.rssreader;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

import android.os.Handler;
import android.os.Message;

public class HttpGetThread extends Thread{
	private SyndFeed feed ; 
	private String urlToRssFeed ;
	private Handler handler ;
	private String cate ;

	public HttpGetThread(String strUrl,Handler handler,String cate){
		this.urlToRssFeed = strUrl ;
		this.handler = handler ;
		feed = null;
		this.cate = cate ;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		RssAtomFeedRetriever feedRetriever = new RssAtomFeedRetriever();
        try {
			feed = feedRetriever.getMostRecentNews(urlToRssFeed);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			feed = null;
			e.printStackTrace();
		}finally {
			Message mg = Message.obtain();  
			mg.obj = new FeedCategory(feed ,cate ,urlToRssFeed) ;  
			handler.sendMessage(mg); 
		}
//		URL url;
//		try {
//			url = new URL(urlToRssFeed);
//			PullParseRss pullParse = new PullParseRss();
//			pullParse.ParseRss(url.openStream());
//			feed = pullParse.getFeed();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			feed = null ;
//			e.printStackTrace();
//		}finally {
//			Message mg = Message.obtain();  
//			mg.obj = new FeedCategory(feed ,cate ,urlToRssFeed) ;  
//			handler.sendMessage(mg);  
//		}
	}

}
