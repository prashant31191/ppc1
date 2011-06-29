package com.openims.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smit.EasyLauncher.R;

public class MenuFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			View viewer = (View) inflater
	                .inflate(R.layout.menu, null);
			
			Button btnSetting = (Button)viewer.findViewById(R.id.btnsetting);
			if(btnSetting != null){
				btnSetting.setOnClickListener(new View.OnClickListener() {				
					@Override
					public void onClick(View v) {
						PushActivity pa = (PushActivity)getActivity();
						pa.changeDetail(R.id.btnsetting);
					}
				});
			}
			Button btnPushInf = (Button)viewer.findViewById(R.id.btnpushInfo);
			if(btnPushInf != null){
				btnPushInf.setOnClickListener(new View.OnClickListener() {				
					@Override
					public void onClick(View v) {
						PushActivity pa = (PushActivity)getActivity();
						pa.changeDetail(R.id.btnpushInfo);
					}
				});
			}
			Button btnAbout = (Button)viewer.findViewById(R.id.btnabout);
			if(btnAbout != null){
				btnAbout.setOnClickListener(new View.OnClickListener() {				
					@Override
					public void onClick(View v) {
						PushActivity pa = (PushActivity)getActivity();
						pa.changeDetail(R.id.btnabout);
					}
				});
			}
			
	        return viewer;
	}
	
	
	
	
}
