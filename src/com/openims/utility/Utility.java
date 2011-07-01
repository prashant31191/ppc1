package com.openims.utility;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.openims.widgets.BigToast;

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
	
	static public void showToast(Context context,int text,int duration){
		Toast t = BigToast.makeText(context,context.getResources().getString(text), 
				duration);						
		t.setGravity(Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
		t.setMargin(PushServiceUtil.HORIZONTAL_MARGIN, PushServiceUtil.VERTICAL_MARGIN);
		t.show();
	}
}
