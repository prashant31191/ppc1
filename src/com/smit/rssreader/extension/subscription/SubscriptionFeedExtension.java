package com.smit.rssreader.extension.subscription;

import java.net.URL;

public class SubscriptionFeedExtension extends SubUnSubFeedExtension {

	public static final String ELEMENT_NAME = "subscribe";

	public SubscriptionFeedExtension(final String jid, final URL feedURL) {
		super(jid, feedURL);  //调用父类构造函数
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

}
