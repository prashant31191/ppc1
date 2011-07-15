package com.openims.model;

import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.jivesoftware.smack.XMPPConnection;

import android.app.Application;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.openims.model.chat.VCardDataBase;
import com.smit.EasyLauncher.R;

public class MyApplication extends Application {
	
	private final HashMap<String, SoftReference<Drawable>> cache = 
		new HashMap<String,  SoftReference<Drawable>>();
	private int nIndexAvater = 0;
	private String mAdminJid = null;
	private String serveName = null;
	
	private XMPPConnection connection;
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {	
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {		
		super.onCreate();		
	}

	@Override
	public void onLowMemory() {		
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {		
		super.onTerminate();
	}	
	
	public Drawable getAvater(String jid){
		
		SoftReference<Drawable> d = cache.get(jid);
		// 1
		if(d != null){
			Drawable drawable = d.get();
			if(drawable != null){
				return drawable;
			}else{
				cache.remove(jid);
			}
		}
		if(mAdminJid == null){
			return null;
		}
		
		VCardDataBase vc = new VCardDataBase(MyApplication.this,mAdminJid);
		Cursor c = vc.queryByJId(jid);
		if(nIndexAvater == 0){
			nIndexAvater = c.getColumnIndex(VCardDataBase.Avater);
		}
		// 2
		if(c.getCount() == 0){
			vc.close();
			c.close();
			return null;
		}
		c.moveToFirst();
		byte[] b = c.getBlob(nIndexAvater);
		// 3
		Drawable draw;
		if(b == null){			
			draw = getResources().getDrawable(R.drawable.icon);
		}else{
			draw = new BitmapDrawable(getResources(),new ByteArrayInputStream(b));
		}		
		
		cache.put(jid, new SoftReference<Drawable>(draw));
		c.close();
		vc.close();
		return draw;
	}

	public XMPPConnection getConnection() {
		return connection;
	}
    public String getAdminJid(){
    	return mAdminJid;
    }
	public void setConnection(XMPPConnection connection,String adminJid) {
		this.connection = connection;
		this.mAdminJid = adminJid;
	}

	public String getServeName() {
		return serveName;
	}

	public void setServeName(String serveName) {
		this.serveName = serveName;
	}
	
	
}
