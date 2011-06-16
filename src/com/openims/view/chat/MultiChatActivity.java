package com.openims.view.chat;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.openims.widgets.HorizontialListView;

public class MultiChatActivity extends FragmentActivity implements OnItemSelectedListener{

	private static final String TAG = LogUtil.makeLogTag(MultiChatActivity.class);
	private static final String PRE = "Class MultiChatActivity--";
	
	private static final String TAG_CHAT_MAIN = "chatMain";
	public  static final String ACCOUNT_ID = "ACCOUNT_ID";

	private HorizontialListView chatUserListview;
	private long mSelectedAccountId = 0;
	private ChatMainFragment mChatMainFragment;
	
	/** Messenger for communicating with service. */
    Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    private MessageBoxAdapter mAdapter;
    
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.multi_chat);		
		
		chatUserListview = (HorizontialListView)findViewById(R.id.multi_chat_user);
		chatUserListview.setOnItemSelectedListener(this);		
		
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
		
		Intent intent = getIntent();
		int accountId = intent.getIntExtra(ACCOUNT_ID, 0);
		mAdapter = new MessageBoxAdapter(this);
		
		// TODO INIT 
		mAdapter.addAccount(accountId, 5);
		chatUserListview.setAdapter(mAdapter);
		
		mChatMainFragment.setTableName(
				MessageRecord.getMessageRecordTableName("555", "66"), 5, 66);
		
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
		mSelectedAccountId = id;
		Log.e(TAG, PRE + "position=" + position);
		/*ChatMainFragment mChatMainFragment = 
			(ChatMainFragment)getSupportFragmentManager()
			.findFragmentByTag(TAG_CHAT_MAIN);*/
		mChatMainFragment.setTableName(
				MessageRecord.getMessageRecordTableName("555",String.valueOf(id)),
						(Integer)view.getTag(),(int)id);
		
		
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
                    Log.i(TAG,PRE + "Received from service: arg1--" + 
                    		msg.arg1 + ", arg2--" + msg.arg2);
                    mAdapter.addAccount(msg.arg1, msg.arg2);
                    if(msg.obj != null){
                    	mAdapter.notifyDataSetChanged();
                    }
                    if(mSelectedAccountId == msg.arg1){
                    	mChatMainFragment.updateList();
                    }
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
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            Log.d(TAG, PRE + "Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
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
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            
            // As part of the sample, tell the user what happened.
            Toast.makeText(MultiChatActivity.this, "connect",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            
            // As part of the sample, tell the user what happened.
            Toast.makeText(MultiChatActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(MultiChatActivity.this, 
                IMService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(TAG,PRE + "Binding.");
    }
    
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                    		PushServiceUtil.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }
            
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Log.d(TAG, PRE + "Unbinding.");
        }
    }
}
