package com.openims.view.chat;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class ProgressFragment extends DialogFragment {

	private static String MESSAGE = "message";
	
	public static ProgressFragment newInstance(Fragment targetFragment,int reqCode,
			String message){
		ProgressFragment fragment = new ProgressFragment();
		Bundle args = new Bundle();
		args.putString(MESSAGE, message);
		fragment.setArguments(args);
		fragment.setTargetFragment(targetFragment, reqCode);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		String message = args.getString(MESSAGE);
		ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		return progressDialog;
	}
	
}
