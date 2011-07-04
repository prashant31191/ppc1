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

import com.smit.EasyLauncher.R;
import com.openims.model.chat.VCardDataBase;

public class MyApplication extends Application {
	
	private final HashMap<String, SoftReference<Drawable>> cache = 
		new HashMap<String,  SoftReference<Drawable>>();
	private int nIndexAvater = 0;
	private String userJid = "test2@smit";

	private XMPPConnection connection;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
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
		
		VCardDataBase vc = new VCardDataBase(MyApplication.this,userJid);
		Cursor c = vc.queryByJId(jid);
		if(nIndexAvater == 0){
			nIndexAvater = c.getColumnIndex(VCardDataBase.Avater);
		}
		// 2
		if(c.getCount() == 0){
			vc.close();
			return null;
		}
		c.moveToFirst();
		byte[] b = c.getBlob(nIndexAvater);
		// 3
		Drawable draw;
		if(b == null){
			vc.close();
			draw = MyApplication.this.getResources().getDrawable(R.drawable.icon);
		}else{
			draw = new BitmapDrawable(new ByteArrayInputStream(b));
		}		
		
		cache.put(jid, new SoftReference<Drawable>(draw));
		vc.close();
		return draw;
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}
	
}
