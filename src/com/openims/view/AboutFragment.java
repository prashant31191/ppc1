package com.openims.view;

import com.openims.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			View viewer = (View) inflater
	                .inflate(R.layout.servicesetting, container, false);
	        return viewer;
	}

}
