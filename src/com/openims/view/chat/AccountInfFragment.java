package com.openims.view.chat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.openims.R;
import com.openims.model.chat.VCardDataBase;
import com.openims.utility.LogUtil;

public class AccountInfFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
				.makeLogTag(AccountInfFragment.class);
	private static final String PRE = "ChatMainFragment--";

	private OnClickListener onClickAccountInf = null;
	private OnClickListener onClickHistory = null;
	
	private String admin = "test2@smit";
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, PRE + "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, PRE + "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.multi_chat_account_inf, container, false);
		
		addListener(v);
		
		VCardDataBase vc = new VCardDataBase(getActivity(),admin);
		Cursor c = vc.queryByJId(admin);
		int nIndexAvater = c.getColumnIndex(VCardDataBase.Avater);
		int nIndexNickName = c.getColumnIndex(VCardDataBase.NICK);
		int nIndexSex = c.getColumnIndex(VCardDataBase.SEX);
		int nIndexJID = c.getColumnIndex(VCardDataBase.JID);
		int nIndexBirthday = c.getColumnIndex(VCardDataBase.BIRTHDAY);
		int nIndexMobile = c.getColumnIndex(VCardDataBase.MOB);
		int nIndexEMAIL = c.getColumnIndex(VCardDataBase.EMAIL);
		int nIndexWEIBO = c.getColumnIndex(VCardDataBase.WEIBO);
		if(c.getCount() != 1){
			return v;
		}
		c.moveToFirst();
		byte[] a = c.getBlob(nIndexAvater);
		if(a != null){
			InputStream in = new ByteArrayInputStream(a);
			BitmapDrawable drawable = new BitmapDrawable(in);	
			ColorMatrix cm1 = new ColorMatrix(new float[]{0.5f,0.5f,0.5f,0,0, 
                    0.5f,0.5f,0.5f,0,0, 
                    0.5f,0.5f,0.5f,0,0, 
                    0,0,0,1,0,0, 
                    0,0,0,0,1,0 
                    }); 
			drawable.setColorFilter(new ColorMatrixColorFilter(cm1));
			ImageView avater = (ImageView)v.findViewById(R.id.avater);
			avater.setImageDrawable(drawable);
			
		}
		String jid = c.getString(nIndexJID);
		
		TextView tvjid = (TextView)v.findViewById(R.id.jid);
		tvjid.setText(jid);		
		
		String nickName = c.getString(nIndexNickName);
		if(nickName!=null && nickName.equals("null")==false){
			TextView tvnick = (TextView)v.findViewById(R.id.nickName);
			tvnick.append(nickName);
		}	
		
		String sex = c.getString(nIndexSex);
		if(sex!=null && sex.equals("null")==false){
			TextView tvsex = (TextView)v.findViewById(R.id.sex);
			tvsex.append(sex);
		}
		
		String state = c.getString(nIndexSex);
		if(state!=null && state.equals("null")==false){
			TextView tvstate = (TextView)v.findViewById(R.id.state);
			tvstate.append(state);
		}
		
		String phone = c.getString(nIndexSex);
		if(phone!=null && phone.equals("null")==false){
			TextView tvphone = (TextView)v.findViewById(R.id.phone);
			tvphone.append(phone);
		}
		
		String note = c.getString(nIndexSex);
		if(note!=null && note.equals("null")==false){
			TextView tvnote = (TextView)v.findViewById(R.id.note);
			tvnote.append(note);
		}
		
		return v;
	}
	
	public void addListener(View v){
		View btn = v.findViewById(R.id.header_left);
		btn.setOnClickListener(this);
		v.findViewById(R.id.header_right).setOnClickListener(this);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, PRE + "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, PRE + "onResume");
	}	

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, PRE + "onPause");
	}

	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, PRE + "onStop");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, PRE + "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach");
	}

	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.header_left:
			getFragmentManager().popBackStack();
			break;
		case R.id.header_right:
			getFragmentManager().popBackStack();
			break;
		}
		
	}

	public void setOnClickAccountInf(OnClickListener onClickAccountInf) {
		this.onClickAccountInf = onClickAccountInf;
	}

	public void setOnClickHistory(OnClickListener onClickHistory) {
		this.onClickHistory = onClickHistory;
	}
	
	
	
}
