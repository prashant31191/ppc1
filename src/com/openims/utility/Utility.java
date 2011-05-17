package com.openims.utility;

import android.database.Cursor;
import android.util.Log;

public class Utility {

	private static final String LOGTAG = LogUtil.makeLogTag(Utility.class);
    private static final String TAG = Utility.class.getSimpleName()+"--";
    
    
	static public void printCursor(Cursor c){
		if(c.getCount()==0)
			return;
		c.moveToFirst();
		while(c.isLast() == false){
			int nCount = c.getColumnCount();
			StringBuilder sb = new StringBuilder();
			
			for(int i=0; i<nCount; i++){
				sb.append(c.getString(i) + " ");
			}
			c.moveToNext();
			Log.d(LOGTAG,TAG+sb.toString());
		}
	}
}
