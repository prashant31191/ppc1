package com.openims.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.openims.R;

public class BigToast {
	
	 static public Toast makeText(Context context, CharSequence text, int duration){
		 
		 Toast t = Toast.makeText(context, text, duration);
		 LayoutInflater inflator =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		 View view = inflator.inflate(R.layout.toast, null);
		 TextView tv = (TextView)view.findViewById(R.id.toast_text);
		 tv.setText(text);
		 t.setView(view);
		 
		 return t;
	 }

}
