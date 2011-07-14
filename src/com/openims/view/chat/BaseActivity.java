package com.openims.view.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.smit.EasyLauncher.R;
import com.openims.model.MyApplication;
import com.openims.model.chat.MessageRecord;
import com.openims.model.chat.RosterDataBase;
import com.openims.service.IMService;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.MessageBoxAdapter.Account;
import com.openims.widgets.HorizontialListView;

public class BaseActivity extends FragmentActivity 
		implements OnAvater{

	private static final String TAG = LogUtil.makeLogTag(BaseActivity.class);
	private static final String PRE = "Class BaseActivity--";
	
	// for update avatar
	private MyApplication myApplication;
	private final HashMap<String,OnAvaterListener> avaterListeners = 
		new HashMap<String,OnAvaterListener>();
	
	// for activity communicate with service
	/** Messenger for communicating with service. */
	private Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    protected Messenger mMessenger;    
    
    
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		
		// set window property
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	

		myApplication = (MyApplication)getApplication();
		
		// connect to service
		doBindService();		
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
	protected void onStop() {
		Log.i(TAG, PRE + "onStop");		
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, PRE + "onDestroy");
		doUnbindService();
		super.onDestroy();
	}
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
          
            mService = new Messenger(service);
            Log.d(TAG, PRE + "Attached.");
           
            try {
                Message msg = Message.obtain(null,
                		PushServiceUtil.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                
            } catch (RemoteException e) {
            }            
        }

        public void onServiceDisconnected(ComponentName className) {           
            mService = null;
        }
    };
    
    
    void doBindService() {
       
        bindService(new Intent(BaseActivity.this, 
                IMService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(TAG,PRE + "Binding.");
    }
    
    void doUnbindService() {
        if (mIsBound) {
          
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                    		PushServiceUtil.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {                   
                }
            }            
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Log.d(TAG, PRE + "Unbinding.");
        }
    }
    
    @Override
	public Drawable getAvater(String avaterJid, OnAvaterListener listener) {
		
		Drawable d = myApplication.getAvater(avaterJid);
		if(d != null){
			return d;
		}
		if(avaterListeners.get(avaterJid) != null){
			return getResources().getDrawable(R.drawable.icon); 
		}
		// notify service to get VCARD
		d = getResources().getDrawable(R.drawable.icon);
		if(mService == null){
			return d;
		}
		avaterListeners.put(avaterJid, listener);
		
		Message msg = Message.obtain(null,
        		PushServiceUtil.MSG_REQUEST_VCARD);
        msg.replyTo = mMessenger;
        msg.obj = avaterJid;

        try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
			return d;
		} catch (Exception e){
			e.printStackTrace();
			return d;
		}
		return d;		
	}
}
