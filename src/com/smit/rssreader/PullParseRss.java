package com.smit.rssreader;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PullParseRss {
	private RSSFeed feed;
	private RSSItem item;

	public RSSFeed getFeed() {
		return feed;
	}

	public void ParseRss(InputStream in) throws IOException,
			XmlPullParserException {
        boolean flag = false ;
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(in, null); // null 为编码格式，如utf-8，null为所有的
		int eventType = xpp.getEventType();

		// 解析文件的头
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				String tag = xpp.getName();
				if (tag.equals("channel")) {
					feed = new RSSFeed();
					flag = true ;
				}else if (tag.equals("item")) {
					break ;
				} else if (tag.equals("title")&& flag==true) {
					xpp.next();
					feed.setTitle(xpp.getText().toString().trim());
					flag = false ;
				} else if (tag.equals("pubDate")) {
					xpp.next();
					feed.setPubDate(xpp.getText().toString().trim());
				}
			} else if (eventType == XmlPullParser.END_TAG) {

			}
			eventType = xpp.next();
		}

		// 解析文件的Item条目
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tag = null ;
			if (eventType == XmlPullParser.START_TAG) {
				tag = xpp.getName();
				if (tag.equals("item")) {
					xpp.next();
					item = new RSSItem();
				} else if (tag.equals("title")) {
					xpp.next();
					item.setTitle(xpp.getText().toString().trim());
				} else if (tag.equals("link")) {
					xpp.next();
					item.setLink(xpp.getText().toString().trim());
				} else if (tag.equals("pubDate")) {
					xpp.next();
					item.setPubDate(xpp.getText().toString().trim());
				} else if (tag.equals("description")) {
					xpp.next();
					item.setDescription(xpp.getText().toString().trim());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				tag = xpp.getName();
				if (tag.equals("item")) {
					feed.addItem(item);
				}
			}
			eventType = xpp.next();
		}

	}
}