package com.openims.service;

import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;

import com.openims.utility.LogUtil;

public class MyRosterListener implements RosterListener {
	 private static final String LOGTAG = LogUtil.makeLogTag(MyRosterListener.class);
	 
	 private static final String TAG = LogUtil.makeTag(MyRosterListener.class);
	    
	@Override
	public void entriesAdded(Collection<String> addresses) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG,TAG+"entriesAdded");
		Iterator<String> it = addresses.iterator();
		while(it.hasNext()){
			Log.i(LOGTAG,TAG+it.next());
		}

	}

	@Override
	public void entriesDeleted(Collection<String> addresses) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG,TAG+"entriesDeleted");
		Iterator<String> it = addresses.iterator();
		while(it.hasNext()){
			Log.i(LOGTAG,TAG+it.next());
		}

	}

	@Override
	public void entriesUpdated(Collection<String> addresses) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG,TAG+"entriesUpdated");
		Iterator<String> it = addresses.iterator();
		while(it.hasNext()){
			Log.i(LOGTAG,TAG+it.next());
		}

	}

	@Override
	public void presenceChanged(Presence presence) {
		// TODO Auto-generated method stub

	}

}
