package com.openims.utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
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
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	 
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 12;
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	  }
}
