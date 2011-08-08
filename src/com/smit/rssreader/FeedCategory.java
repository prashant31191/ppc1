package com.smit.rssreader;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

public class FeedCategory {
	private SyndFeed rFeed;
	private String category ;
	private String urlFeed ;
	
	public FeedCategory(SyndFeed feed , String cate ,String urlFeed){
		this.rFeed = feed ;
		this.category = cate ;
		this.urlFeed = urlFeed ;
	}

	public SyndFeed getRssFeed(){
		return rFeed;
	}
	
	public String getRssCategory(){
		return category ;
	}
	
	public String getRssUrl(){
		return urlFeed;
	}
}
