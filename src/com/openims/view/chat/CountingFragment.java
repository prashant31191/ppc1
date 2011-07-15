package com.openims.view.chat;

import com.smit.EasyLauncher.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CountingFragment extends Fragment {
	
	private static final String TAG = "OpenIMS";
	private static final String PRE = "fragment";
	int mNum = 0;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static CountingFragment newInstance(int num) {
        CountingFragment f = new CountingFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
        Log.d(TAG, PRE + "onCreate" + mNum);
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hello_world, container, false);
        View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText("Fragment #" + mNum);
        tv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.gallery_thumb));
        Log.d(TAG, PRE + "onCreateView" + mNum);
        return v;
    }
    
    @Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, PRE + "onStart" + mNum);
	}
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, PRE + "onResume" + mNum);
	}
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, PRE + "onPause" + mNum);
	}
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, PRE + "onStop" + mNum);
	}	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, PRE + "onDestroy" + mNum);
	}
	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach" + mNum);
	}
}
