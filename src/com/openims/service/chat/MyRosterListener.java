package com.openims.service.chat;

import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;

import com.openims.service.XmppManager;
import com.openims.utility.LogUtil;

public class MyRosterListener implements RosterListener {
	 private static final String LOGTAG = LogUtil.makeLogTag(MyRosterListener.class);
	 
	 private static final String TAG = LogUtil.makeTag(MyRosterListener.class);
	    
	 private XmppManager xmppManager;
	 
	 public MyRosterListener(XmppManager xmppManager){
		 this.xmppManager = xmppManager;
	 }
	 
	 @Override
	 public void entriesAdded(Collection<String> addresses) {
	
		Log.e(LOGTAG,TAG+"entriesAdded");
		Iterator<String> it = addresses.iterator();
		while(it.hasNext()){
			String jid = it.next();
			Log.i(LOGTAG,TAG+jid);
			xmppManager.updateRoster(jid);
			xmppManager.notifyRosterUpdated(jid);
		}
	 }

	 @Override
	 public void entriesDeleted(Collection<String> addresses) {
		
		Log.e(LOGTAG,TAG+"entriesDeleted");
		Iterator<String> it = addresses.iterator();
		
		while(it.hasNext()){
			String jid = it.next();
			Log.i(LOGTAG,TAG+jid);
			xmppManager.deleteRoster(jid);
			xmppManager.notifyRosterUpdated(jid);
		}
	}

	 @Override
	 public void entriesUpdated(Collection<String> addresses) {
		
		Log.e(LOGTAG,TAG+"entriesUpdated");
		Iterator<String> it = addresses.iterator();
		xmppManager.notifyRosterUpdated(null);
		while(it.hasNext()){
			Log.i(LOGTAG,TAG+it.next());
			String jid = it.next();
			xmppManager.updateRoster(jid);
			xmppManager.notifyRosterUpdated(jid);
		}

	 }

	 @Override
	 public void presenceChanged(Presence presence) {
		// TODO Auto-generated method stub
		Log.e(LOGTAG,TAG+"presenceChanged have not deal with" + presence.getFrom());

	 }

}
