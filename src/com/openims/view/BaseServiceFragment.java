package com.openims.view;

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
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());  
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
		doBindService();		
	}	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, PRE + "onDestroy");
		doUnbindService();
		mConnection = null;
	}
	/**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	BaseServiceFragment.this.handleMessage(msg);
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
    	BaseServiceFragment.this.getActivity().bindService(
    			new Intent(BaseServiceFragment.this.getActivity(), 
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
        	BaseServiceFragment.this.getActivity().unbindService(mConnection);
            mIsBound = false;
            Log.d(TAG, PRE + "Unbinding.");
        }
    }	
   
   abstract public void handleMessage(Message msg);
   
   public void sendMsgService(int what,int arg1,int arg2,Object obj) throws RemoteException{
	   Message msg = Message.obtain(null, what, arg1, arg2, obj);
       msg.replyTo = mMessenger;
       mService.send(msg);		
   }
}
