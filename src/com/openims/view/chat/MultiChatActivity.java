package com.openims.view.chat;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.utility.LogUtil;

public class MultiChatActivity extends FragmentActivity {

	private static final String TAG = LogUtil.makeLogTag(MessageRecord.class);
	private static final String PRE = "Class MultiChatActivity--";
	
	private static final String TAG_HISTORY = "history";
	
	private ChatHistoryFragment history = null;
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		setContentView(R.layout.multi_chat);
		
		if(bundle == null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.multi_chat_content, getHistoryFragment(),TAG_HISTORY).commit();
		}
	}
	private Fragment getHistoryFragment(){
		if(history == null){
			history = new ChatHistoryFragment(); 
		}
		return history;
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Log.i(TAG, PRE + "onActivityResult");
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onAttachedToWindow() {
		Log.i(TAG, PRE + "onAttachedToWindow");
		super.onAttachedToWindow();
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		Log.i(TAG, PRE + "onAttachFragment");
		super.onAttachFragment(fragment);
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, PRE + "onBackPressed");
		super.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, PRE + "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(TAG, PRE + "onContextItemSelected");
		return super.onContextItemSelected(item);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		Log.i(TAG, PRE + "onContextMenuClosed");
		super.onContextMenuClosed(menu);
	}

	

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.i(TAG, PRE + "onCreateDialog");
		return super.onCreateDialog(id);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, PRE + "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, PRE + "onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Log.i(TAG, PRE + "onKeyLongPress");
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, PRE + "onLowMemory");
		super.onLowMemory();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, PRE + "onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, PRE + "onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, PRE + "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, PRE + "onResume");
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, PRE + "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		Log.i(TAG, PRE + "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, PRE + "onStop");
		super.onStop();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, PRE + "onTouchEvent");
		return super.onTouchEvent(event);
	}

}
