package com.openims.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.openims.service.IMService;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public abstract class BaseServiceFragment extends ListFragment{

	private static final String TAG = LogUtil.makeLogTag(BaseServiceFragment.class);					
	private static final String PRE = "BaseServiceFragment--";

	
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
    boolean mIsBound = false;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());  
	boolean mIsShow = false;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, PRE + "doBindService");
		doBindService();	
	}

	@Override
	public void onStart() {
		
		super.onStart();
		mIsShow = true;
	}

	@Override
	public void onStop() {
		mIsShow = false;
		super.onStop();
	}

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, PRE + "onDestroy");	
		Log.d(TAG, PRE + "doUnbindService");
		doUnbindService();
		mConnection = null;
		super.onDestroy();		
	}
	/**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	if(mIsShow){
        		BaseServiceFragment.this.handleMessage(msg);        	
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
       if(mIsBound == false){
    	   mIsBound = true;
		   BaseServiceFragment.this.getActivity().bindService(
	    			new Intent(BaseServiceFragment.this.getActivity(), 
	                IMService.class), mConnection, Context.BIND_AUTO_CREATE);
	        
	        Log.e(TAG,PRE + "Binding.");
       }
    }
    
   private void doUnbindService() {
        if (mIsBound == true) { 
        	mIsBound = false;
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
        	BaseServiceFragment.this.getActivity().unbindService(mConnection);
            
            Log.e(TAG, PRE + "Unbinding.");
        }
    }	
   
   abstract public void handleMessage(Message msg);
   
   public void sendMsgService(int what,int arg1,int arg2,Object obj) throws RemoteException{
	   Message msg = Message.obtain(null, what, arg1, arg2, obj);
       msg.replyTo = mMessenger;
       mService.send(msg);		
   }
}
