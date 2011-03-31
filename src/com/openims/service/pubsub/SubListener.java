package com.openims.service.pubsub;

 
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import com.openims.service.XmppManager;
import com.openims.utility.Constants;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

import android.content.Intent;
import android.util.Log;

public class SubListener implements ItemEventListener {
	private static final String LOGTAG = LogUtil.makeLogTag(SubListener.class);
    private static final String TAG = LogUtil.makeTag(SubListener.class);
    
	private XmppManager xmppManager;
	private String nodeName;
	public SubListener(XmppManager xmppManager,String nodeName){
		this.xmppManager = xmppManager;
		this.nodeName = nodeName;
	}
	public SubListener(XmppManager xmppManager){
		this.xmppManager = xmppManager;
	}
	@Override
	public void handlePublishedItems(ItemPublishEvent items) {
		
		Log.d(LOGTAG,TAG+"XMPPClient the items are: "+items.getItems().toString());
		List<PayloadItem> list = items.getItems();
		for(int i=0;i<list.size();i++){
			PayloadItem item = list.get(i);	
			Intent intent1 = new Intent();
	        intent1.setClassName("com.openims","com.openims.pubsub.MainActivity");
	        intent1.putExtra(PushServiceUtil.MESSAGE_FROM, items.getNodeId());
	        SimplePayload payload = (SimplePayload)item.getPayload();
	        StringBuilder text = new StringBuilder(payload.toXML());
	        text.delete(0, 4);
	        int n = text.lastIndexOf("null");
	        text.delete(n,n+4);
	        
	        intent1.putExtra(PushServiceUtil.MESSAGE_CONTENT,text.toString());
	        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        xmppManager.getContext().startActivity(intent1);
			
		}			
	}
}
