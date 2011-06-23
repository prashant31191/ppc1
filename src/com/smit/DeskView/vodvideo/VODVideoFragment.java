package com.smit.DeskView.vodvideo;

import java.security.PublicKey;

import com.smit.EasyLauncher.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class VODVideoFragment extends Fragment {
	private LayoutInflater mInflater = null;
	private Button moreButton;
	private AlertDialog.Builder mBuilderpass;
	private AlertDialog mAlertpass;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//setupView();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;

		return inflater.inflate(R.layout.vodvideo_widget_home_page, container,
				false);
		
		// return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void setupView() {
		moreButton = (Button) getActivity().findViewById(R.id.vodvideo_more);
		moreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
	}


}