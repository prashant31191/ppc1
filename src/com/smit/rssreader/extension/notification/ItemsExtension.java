package com.smit.rssreader.extension.notification;

import java.util.Iterator;
import java.util.List;

public class ItemsExtension extends DefaultSuperfeerExtension {
	
	private String node;
	
	private List<ItemExtension> items;
	
	public ItemsExtension(String node, List<ItemExtension> items){
		this.node = node;
		this.items = items;
	}
	
	public Iterator<ItemExtension> getItems(){
		return items == null ? new Iterator<ItemExtension>() {

			public boolean hasNext() {
				return false;
			}

			public ItemExtension next() {
				return null;
			}

			public void remove() {
			}
		} : items.iterator();
	}
	
	public String getNode(){
		return node;
	}

	public int getItemsCount() {
		return items != null ? items.size() : 0;
	}
}
