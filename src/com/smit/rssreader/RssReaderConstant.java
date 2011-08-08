package com.smit.rssreader;

public class RssReaderConstant {
	
	public static final int FEEDONSERVER = 1;
	public static final int FEEDNOTONSERVER = 0;
	public static final int ISREAD = 1;
	public static final int NOTREAD = 0 ;
	public static final int INFAVORITE = 1;
	public static final int NOTINFAVORITE = 0 ;
	/**
     * broadcast subscribed(unsubscribed) successfully 
     */
	public static final String IQRESPONSEYES = "com.smit.rssreader.action.IQ_YES_BROADCAST";
	
	/**
     * broadcast Subscribe(unsubscribe) to failure
     */
	public static final String IQRESPONSENO = "com.smit.rssreader.action.IQ_NO_BROADCAST";
	
	/**
     * broadcast add to favorite
     */
	public static final String ADDFAVORITE = "com.smit.rssreader.action.MARKEDSTAR_BROADCAST";
	
	/**
     * broadcast the item is read
     */
	public static final String READED_BROADCAST = "com.smit.rssreader.action.READED_BROADCAST";
	
	/**
	 * broadcast got new contents from server
	 */
	public static final String NEWCONTENT =  "com.smit.rssreader.action.NEWCONTENT_BROADCAST";
}
