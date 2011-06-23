package com.openims.view.chat;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.service.IMService;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.MessageBoxAdapter.Account;
import com.openims.widgets.HorizontialListView;

public class MultiChatActivity extends FragmentActivity implements OnItemSelectedListener{

	private static final String TAG = LogUtil.makeLogTag(MultiChatActivity.class);
	private static final String PRE = "Class MultiChatActivity--";
	
	private static final String TAG_CHAT_MAIN = "chatMain";
	public  static final String ACCOUNT_JID = "ACCOUNT_JID";

	private HorizontialListView chatUserListview;
	private MessageBoxAdapter mMessageBoxAdapter;	
	private ChatMainFragment mChatMainFragment;
	private String myJid = "test2@smit";
	private String mChatJid;
	
	/** Messenger for communicating with service. */
	private Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    
    
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.multi_chat);
		
			
		// init main chat fragment
		if(bundle == null){
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			mChatMainFragment = new ChatMainFragment();			
	        ft.add(R.id.multi_chat_content, mChatMainFragment,TAG_CHAT_MAIN).commit();
			
		}else{			
			mChatMainFragment = (ChatMainFragment)getSupportFragmentManager().findFragmentByTag(TAG_CHAT_MAIN);
		}
		mChatMainFragment.setOnClickAccountInf(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.multi_chat_content, new AccountInfFragment());
			    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			    ft.addToBackStack(null);
				ft.commit();
			}
			
		});
		mChatMainFragment.setOnClickHistory(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.multi_chat_content, new ChatHistoryFragment());
			    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			    ft.addToBackStack(null);
				ft.commit();				
			}	
			
		});	
		
		SharedPreferences sharedPrefs = getSharedPreferences(
				PushServiceUtil.SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		myJid = sharedPrefs.getString(PushServiceUtil.XMPP_USERNAME, null)+"@smit";
		
		// init chat user list
		chatUserListview = (HorizontialListView)findViewById(R.id.multi_chat_user);
		chatUserListview.setOnItemSelectedListener(this);	
		
		Intent intent = getIntent();		
		mChatJid  = intent.getStringExtra(ACCOUNT_JID);
		
		mMessageBoxAdapter = new MessageBoxAdapter(this);
		mMessageBoxAdapter.addAccount(mChatJid, 0,0);
		mMessageBoxAdapter.setSelectedJid(mChatJid);
		chatUserListview.setAdapter(mMessageBoxAdapter);
		
		String tableName = MessageRecord.getMessageRecordTableName(myJid, mChatJid);
		mChatMainFragment.setTableName(tableName, 0, mChatJid,myJid);		
		
	}

	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {		
		View v = super.onCreateView(name, context, attrs);		
		return v;
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
		// connect to service
		doBindService();
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
		doUnbindService();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, PRE + "onDestroy");
		mMessageBoxAdapter.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, PRE + "onTouchEvent");
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {		

		Account account = (Account)view.getTag();
		if(mMessageBoxAdapter.getSelectedJid().equals(account.jId)){
			account = mMessageBoxAdapter.deleteAccount(position);
			if(account == null){
				this.finish();
				return;
			}
		}
		mChatJid = account.jId;
		mMessageBoxAdapter.setSelectedJid(mChatJid);
		mMessageBoxAdapter.notifyDataSetChanged();
		
		mChatMainFragment.setTableName(
				MessageRecord.getMessageRecordTableName(myJid,mChatJid),
				account.unTotalNum,mChatJid,myJid);		
		
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
                case PushServiceUtil.MSG_UNREAD_NUMBBER:
                	recUnReadMessage(msg);
                    break;
                case PushServiceUtil.MSG_NEW_MESSAGE:
                	recUnReadMessage(msg);
                	mMessageBoxAdapter.notifyDataSetChanged();
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private void recUnReadMessage(Message msg){
    	Log.i(TAG,PRE + "Received from service: arg1--" + 
        		msg.arg1 + ", arg2--" + msg.arg2);
    	
    	if(msg.obj == null){
         	mMessageBoxAdapter.notifyDataSetChanged();
         	return;
         }
        mMessageBoxAdapter.addAccount((String)msg.obj, msg.arg2, msg.arg1);        
        if(mChatJid.equals(msg.obj)){
        	mChatMainFragment.updateList();
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
                
                // Give it some value as an example.
                msg = Message.obtain(null,
                		PushServiceUtil.MSG_UNREAD_NUMBBER,0, 0);
                mService.send(msg);
            } catch (RemoteException e) {
            }
            
            // As part of the sample, tell the user what happened.
            Toast.makeText(MultiChatActivity.this, "connect",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
           
            mService = null;            
            // As part of the sample, tell the user what happened.
            Toast.makeText(MultiChatActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();
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
}
