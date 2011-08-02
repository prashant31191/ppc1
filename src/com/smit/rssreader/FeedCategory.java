package com.smit.rssreader;

public class FeedCategory {
	private RSSFeed rFeed;
	private String category ;
	private String urlFeed ;
	
	public FeedCategory(RSSFeed feed , String cate ,String urlFeed){
		this.rFeed = feed ;
		this.category = cate ;
		this.urlFeed = urlFeed ;
	}

	public RSSFeed getRssFeed(){
		return rFeed;
	}
	
	public String getRssCategory(){
		return category ;
	}
	
	public String getRssUrl(){
		return urlFeed;
	}
}
