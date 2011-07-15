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

public class MultiChatActivity extends FragmentActivity 
		implements OnItemSelectedListener,OnAvater{

	private static final String TAG = LogUtil.makeLogTag(MultiChatActivity.class);
	private static final String PRE = "Class MultiChatActivity--";
	
	private static final String TAG_CHAT_MAIN = "chatMain";
	private static final String TAG_HISTORY = "history";
	private static final String TAG_ACCOUNT_INF = "information";
	public  static final String ACCOUNT_JID = "ACCOUNT_JID";	

	private HorizontialListView chatUserListview;
	private MessageBoxAdapter mMessageBoxAdapter;	
	private ChatMainFragment mChatMainFragment;
	
	private String mMyJid;
	private String mYourJid;
	
	// for update avatar
	private MyApplication myApplication;
	private final HashMap<String,OnAvaterListener> avaterListeners = 
		new HashMap<String,OnAvaterListener>();
	
	// for activity communicate with service
	/** Messenger for communicating with service. */
	private Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());    
    
    
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		
		// set window property
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		setContentView(R.layout.multi_chat);		
		View v = this.findViewById(R.id.layout_root);
		v.setDrawingCacheEnabled(true);
		
		// initial global data		
		Intent intent = getIntent();		
		mYourJid  = intent.getStringExtra(ACCOUNT_JID);
		
		myApplication = (MyApplication)getApplication();
		mMyJid = myApplication.getAdminJid();
		
		// initial main chat fragment
		if(bundle == null){
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			mChatMainFragment = new ChatMainFragment();			
	        ft.add(R.id.multi_chat_content, mChatMainFragment,TAG_CHAT_MAIN).commit();        
			
		}else{	
			mYourJid = bundle.getString(ACCOUNT_JID);
			mChatMainFragment = (ChatMainFragment)getSupportFragmentManager().findFragmentByTag(TAG_CHAT_MAIN);
			initAccountInfFragment((ChatAccountInfFragment)getSupportFragmentManager()
					.findFragmentByTag(TAG_ACCOUNT_INF));		
			initHistoryFragment((ChatHistoryFragment)getSupportFragmentManager()
					.findFragmentByTag(TAG_HISTORY));
		}
		
		// initial chat user list
		chatUserListview = (HorizontialListView)findViewById(R.id.multi_chat_user);
		chatUserListview.setOnItemSelectedListener(this);
		
		mMessageBoxAdapter = new MessageBoxAdapter(this);
		mMessageBoxAdapter.setOnAvater(this);
		mMessageBoxAdapter.setSelectedJid(mYourJid);
		int nstartId = mMessageBoxAdapter.initAdapter(mMyJid);
		
		chatUserListview.setAdapter(mMessageBoxAdapter);
		
		// initial chat fragment
		String tableName = MessageRecord.getMessageRecordTableName(mMyJid, mYourJid);
		mChatMainFragment.setTableName(tableName, nstartId, mYourJid,mMyJid);
		mChatMainFragment.setOnAvater(this);
		addListener();
		
		// connect to service
		doBindService();		
	}

	private void initAccountInfFragment(ChatAccountInfFragment f){
		if(f == null){
			return;
		}
		f.setOnAvater(MultiChatActivity.this);
		f.setInf(mMyJid, mYourJid);
	}
	
	private void initHistoryFragment(ChatHistoryFragment history){
		if(history == null){
			return;
		}
		history.setOnAvater(MultiChatActivity.this);
		history.setDataTableName(
				MessageRecord.getMessageRecordTableName(mMyJid,mYourJid)
				,mMyJid,mYourJid);
	}
	private void addListener(){
		
		mChatMainFragment.setOnClickAccountInf(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ChatAccountInfFragment inf = new ChatAccountInfFragment();
				initAccountInfFragment(inf);
				ft.replace(R.id.multi_chat_content, inf,TAG_ACCOUNT_INF);
			    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);			  
			    ft.addToBackStack(null);
				ft.commit();		       
			}
			
		});
		mChatMainFragment.setOnClickHistory(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ChatHistoryFragment history = new ChatHistoryFragment();
				initHistoryFragment(history);
				ft.replace(R.id.multi_chat_content, history,TAG_HISTORY);
			    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			    ft.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_fast);
			    ft.addToBackStack(null);
				ft.commit();
			}	
			
		});	
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
	public void onLowMemory() {
		Log.i(TAG, PRE + "onLowMemory");
		super.onLowMemory();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, PRE + "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		outState.putString(ACCOUNT_JID, mYourJid);
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
		mMessageBoxAdapter.close();
		super.onDestroy();
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {		

		Account account = (Account)view.getTag();
		// delete item
		if(mMessageBoxAdapter.getSelectedJid().equals(account.jId)){
			RosterDataBase roster = new RosterDataBase(this, mMyJid);
			roster.updateColumn(account.jId, RosterDataBase.NEW_MSG_TIME, 0);
			roster.close();
			account = mMessageBoxAdapter.deleteAccount(position);
			if(account == null){
				this.finish();
				return;
			}
		}
		
		mYourJid = account.jId;
		mMessageBoxAdapter.setSelectedJid(mYourJid);
		RosterDataBase roster = new RosterDataBase(this, mMyJid);
		roster.updateColumn(account.jId, RosterDataBase.NEW_MSG_UREAD, 0);
		roster.close();
		mMessageBoxAdapter.initAdapter(mMyJid);
		mMessageBoxAdapter.notifyDataSetChanged();
		
		mChatMainFragment.setTableName(
				MessageRecord.getMessageRecordTableName(mMyJid,mYourJid),
				account.msgStartId,mYourJid,mMyJid);	
		// show main Fragment
		if(getSupportFragmentManager().findFragmentByTag(TAG_HISTORY) != null ||
		getSupportFragmentManager().findFragmentByTag(TAG_ACCOUNT_INF) != null){
			getSupportFragmentManager().popBackStack();
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {		
		Log.e(TAG, PRE + "onNothingSelected");
	}
    
    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {               
                case PushServiceUtil.MSG_NEW_MESSAGE:
                	recUnReadMessage(msg);                	
                	break;
                case PushServiceUtil.MSG_ROSTER_UPDATED:                	
                	mMessageBoxAdapter.initAdapter(mMyJid);
                	mMessageBoxAdapter.notifyDataSetChanged();
                	if(mYourJid.equals(msg.obj)){            			
                		mChatMainFragment.updatePresence();
                		mChatMainFragment.notifyDataSetChanged();
                		ChatHistoryFragment history = (ChatHistoryFragment)getSupportFragmentManager()
                					.findFragmentByTag(TAG_HISTORY);
                		if(history != null){
                			history.updatePresence();
                			history.notifyDataSetChanged();
                		}
                		ChatAccountInfFragment inf = (ChatAccountInfFragment)getSupportFragmentManager()
    					.findFragmentByTag(TAG_ACCOUNT_INF);
                		if(inf != null){
                			inf.updatePresence();
                		}
            		}
                	break;
                case PushServiceUtil.MSG_REQUEST_VCARD:
                	String jid = (String)msg.obj;
                	OnAvaterListener listener = avaterListeners.get(jid);
                	if(listener != null){                		
                		listener.avater(jid, myApplication.getAvater(jid));
                		avaterListeners.remove(jid);
                	}
                	mMessageBoxAdapter.initAdapter(mMyJid);
            		mMessageBoxAdapter.notifyDataSetChanged();
            		if(mYourJid.equals(jid)){            			
            			mChatMainFragment.avater(jid, myApplication.getAvater(jid));
            			ChatHistoryFragment history = (ChatHistoryFragment)getSupportFragmentManager()
    									.findFragmentByTag(TAG_HISTORY);
			    		if(history != null){
			    			history.avater(jid, myApplication.getAvater(jid));
			    		}
			    		ChatAccountInfFragment inf = (ChatAccountInfFragment)getSupportFragmentManager()
										.findFragmentByTag(TAG_ACCOUNT_INF);
			    		if(inf != null){
			    			inf.avater(jid, myApplication.getAvater(jid));
			    		}
            		}
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private void recUnReadMessage(Message msg){
    	mMessageBoxAdapter.initAdapter(mMyJid);
    	mMessageBoxAdapter.notifyDataSetChanged();
    	if(mYourJid.equals(msg.obj)){
    		RosterDataBase roster = new RosterDataBase(this, mMyJid);
    		roster.updateColumn(mYourJid, RosterDataBase.NEW_MSG_UREAD, 0);
    		roster.close();
        	mChatMainFragment.updateList();
        }
    	ChatHistoryFragment history = (ChatHistoryFragment)getSupportFragmentManager().
    			findFragmentByTag(TAG_HISTORY);
    	if(history != null){
    		history.newMsgCome();
    	} 
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
            	e.printStackTrace();
            }         
        }
        public void onServiceDisconnected(ComponentName className) {           
            mService = null; 
        }
    };
    
    
    void doBindService() {
       
        bindService(new Intent(MultiChatActivity.this, 
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
		}
		return d;		
	}
}
