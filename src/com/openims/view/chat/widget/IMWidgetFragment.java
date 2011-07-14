package com.openims.view.chat.widget;

import java.util.HashMap;

import org.jivesoftware.smack.XMPPConnection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.openims.model.MyApplication;
import com.openims.service.IMService;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.OnAvater;
import com.openims.view.chat.UserManageActivity;
import com.openims.view.chat.UserSearchActivity;
import com.openims.view.setting.Setting.InnerReceiver;
import com.smit.EasyLauncher.R;

public class IMWidgetFragment extends Fragment 
						implements OnClickListener,OnAvater{

	private static final String TAG = LogUtil
					.makeLogTag(IMWidgetFragment.class);
	private static final String PRE = "IMWidgetFragment--";

	private Activity mActivity;
	
	private MyApplication myApplication;
	private final HashMap<String,OnAvaterListener> avaterListeners = 
		new HashMap<String,OnAvaterListener>();
	
	private static final String TAG_FRIEND = "TAG_FRIEND";
	
	private FriendListFragment mFriendFragment;
	private ToggleButton mBtnFriend;
	private ToggleButton mBtnRecent;
	
	/** Messenger for communicating with service. */
	private Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
   
    private IMStatusReceiver statusReceiver;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		Log.d(TAG, PRE + "onAttach");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, PRE + "onCreate");
		
		/*if(savedInstanceState == null){
			final FragmentTransaction ft = getFragmentManager().beginTransaction();
			mFriendFragment = new FriendListFragment();
	        ft.add(R.id.im_center, mFriendFragment,TAG_FRIEND).commit();
			
		}else{			
			mFriendFragment = (FriendListFragment)getFragmentManager()
				.findFragmentByTag(TAG_FRIEND);
		}*/
		
		myApplication = (MyApplication)mActivity.getApplication();
		
		statusReceiver = new IMStatusReceiver();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.im_widget_fragment, container, false);
		
		mBtnFriend = (ToggleButton)v.findViewById(R.id.btn_friend);
		mBtnFriend.setOnClickListener(this);
		mBtnFriend.setChecked(true);		
		//v.findViewById(R.id.btn_group).setOnClickListener(this);
		mBtnRecent = (ToggleButton)v.findViewById(R.id.btn_recent);
		mBtnRecent.setOnClickListener(this);
		View btnSetting = v.findViewById(R.id.btn_im_setting);
		btnSetting.setOnClickListener(this);
		View btnAddFriend = v.findViewById(R.id.btn_add_friend);
		btnAddFriend.setOnClickListener(this);
		
		View contentLayout = v.findViewById(R.id.im_center);
		View loginTips = v.findViewById(R.id.im_unlogin);
		
		if(isLogin()){
			btnSetting.setVisibility(View.VISIBLE);
			btnAddFriend.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.VISIBLE);
			loginTips.setVisibility(View.GONE);
		}else{
			btnSetting.setVisibility(View.GONE);
			btnAddFriend.setVisibility(View.GONE);
			contentLayout.setVisibility(View.GONE);
			
			loginTips.setVisibility(View.VISIBLE);
		}
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		mFriendFragment = (FriendListFragment)getFragmentManager()
			.findFragmentById(R.id.friend_list_fragment);
		mFriendFragment.setOnAvater(this);
		doBindService();		
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, PRE + "onStart");
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMStatusReceiver.ACTION);
        mActivity.registerReceiver(statusReceiver, intentFilter);   
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
		mActivity.unregisterReceiver(statusReceiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, PRE + "onDestroy");
		doUnbindService();
		mConnection = null;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach");
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_friend:
			if(mBtnFriend.isChecked() == false){
				mBtnFriend.setChecked(true);
				return;
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.im_center, mFriendFragment);
			//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
			mBtnFriend.setChecked(true);
			mBtnRecent.setChecked(false);			
			break;
		case R.id.btn_recent:
			if(mBtnRecent.isChecked() == false){
				mBtnRecent.setChecked(true);
				return;
			}
			FragmentTransaction ft1 = getFragmentManager().beginTransaction();
			ft1.replace(R.id.im_center, new RecentChatListFragment());
			//ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft1.addToBackStack(null);
			ft1.commit();
			mBtnFriend.setChecked(false);
			mBtnRecent.setChecked(true);	    	
			break;
		case R.id.btn_add_friend:
			Intent intent = new Intent(mActivity,UserSearchActivity.class);
			mActivity.startActivity(intent);			
			break;
		case R.id.btn_im_setting:
			mActivity.startActivity(new Intent(mActivity,UserManageActivity.class));	
			break;
		}		
	}
	
	
	/**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {                          
                case PushServiceUtil.MSG_REQUEST_VCARD:
                	String jid = (String)msg.obj;
                	OnAvaterListener listener = avaterListeners.get(jid);
                	if(listener != null){  
                		//myApplication.getAvater(jid)
                		listener.avater(jid, null);
                		avaterListeners.remove(jid);
                	}
                	break;
                case PushServiceUtil.MSG_ROSTER_UPDATED:                	
                	mFriendFragment.reRoadData();
                	break;
                case PushServiceUtil.MSG_ROSTER_DELETE:
                	mFriendFragment.reRoadData();
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
    	
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	
            mService = new Messenger(service);          
          
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
    
    
   private void doBindService() {
    	IMWidgetFragment.this.mActivity.bindService(
    			new Intent(IMWidgetFragment.this.mActivity, 
                IMService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(TAG,PRE + "Binding.");
    }
    
   private void doUnbindService() {
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
            IMWidgetFragment.this.mActivity.unbindService(mConnection);
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
		// notify jia zai
		Resources r = getResources();
		d = r.getDrawable(R.drawable.icon);
		
		if(avaterListeners.containsKey(avaterJid) == false){
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
			avaterListeners.put(avaterJid, listener);
		}
		
		return d;		
	}
	private boolean isLogin(){
		XMPPConnection connection = myApplication.getConnection();
		if(connection != null && connection.isAuthenticated()){
			return true;
		}
		return false;
	}
	public class IMStatusReceiver extends BroadcastReceiver{
	    
    	public final static String ACTION = "com.openims.setting.Receiver"; 
    	@Override
    	public void onReceive(Context context,Intent intent){
    		String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
    		Log.d(TAG,PRE+"STATUSE:"+status);
    		if(status.equals(PushServiceUtil.PUSH_STATUS_LOGIN_SUC)){
    			
    		}else{
    			
    		}
    	}
    }
}
