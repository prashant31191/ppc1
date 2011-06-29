package com.openims.view;

import java.util.zip.Inflater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.smit.EasyLauncher.R;
import com.openims.widgets.CustomPopupWindow;

public class MenuPopupWindow extends CustomPopupWindow {

	public MenuPopupWindow(View anchor, final PushActivity PushActivity) {
		super(anchor);
		Context context = anchor.getContext();
		LayoutInflater inflater 	= (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		View viewer = (View) inflater.inflate(R.layout.menu, null);
		setContentView(viewer);
		
		Button btnSetting = (Button)viewer.findViewById(R.id.btnsetting);
		if(btnSetting != null){
			btnSetting.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					PushActivity.changeDetail(R.id.btnsetting);
				}
			});
		}
		Button btnPushInf = (Button)viewer.findViewById(R.id.btnpushInfo);
		if(btnPushInf != null){
			btnPushInf.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					PushActivity.changeDetail(R.id.btnpushInfo);
				}
			});
		}
		Button btnAbout = (Button)viewer.findViewById(R.id.btnabout);
		if(btnAbout != null){
			btnAbout.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					PushActivity.changeDetail(R.id.btnabout);
				}
			});
		}	
	
	}

}
