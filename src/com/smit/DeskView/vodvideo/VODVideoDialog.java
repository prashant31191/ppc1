package com.smit.DeskView.vodvideo;

import com.smit.EasyLauncher.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VODVideoDialog extends DialogFragment{
	@Override
	public  void onActivityCreated(Bundle savedInstanceState) {
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

		return inflater.inflate(R.layout.vodvideo_widget_load_progess, container,
				false);
		
	}
	
	 public static VODVideoDialog newInstance(int num) {
		 	VODVideoDialog f = new VODVideoDialog();

	        Bundle args = new Bundle();
	        args.putInt("num", num);
	        f.setArguments(args);

	        return f;
	    }
	 
	 void showDialog() {

		    FragmentTransaction ft = getFragmentManager().beginTransaction();
		    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);

		    DialogFragment newFragment = VODVideoDialog.newInstance(0);
		    newFragment.show(ft, "dialog");
		}

	 

}
