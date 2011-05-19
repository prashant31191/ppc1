package com.openims.view;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PushInfFragment extends ListFragment {
	
	private final static String[] TITLES = {"1111","2222"};  
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_checked, TITLES));
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

}
