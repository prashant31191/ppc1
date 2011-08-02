package com.smit.rssreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RSSFeed {

	private String title = null;
	private String pubDate = null;
	private int itemCount = 0;
	private List<RSSItem> itemList;

	@SuppressWarnings("unchecked")
	public RSSFeed() {
		itemList = new Vector(0);
	}

	public int addItem(RSSItem item) {
		itemList.add(item);
		itemCount++;
		return itemCount;
	}

	public RSSItem getItem(int location) {
		return itemList.get(location);
	}

	public List<RSSItem> getAllItems() {
		return itemList;
	}

	public List<Map<String, Object>> getAllItemsForListView() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		int size = itemList.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(RSSItem.TITLE, itemList.get(i).getTitle());
			item.put(RSSItem.PUBDATE, itemList.get(i).getPubDate());
			data.add(item);
		}
		return data;
	}

	int getItemCount() {
		return itemCount;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	String getTitle() {
		return title;
	}

	String getPubDate() {
		return pubDate;
	}

}
