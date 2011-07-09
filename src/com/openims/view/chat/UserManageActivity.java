package com.openims.view.chat;


import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.widget.FriendListFragment;
import com.smit.EasyLauncher.R;

public class UserManageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		mMessenger = new Messenger(new IncomingHandler());
		super.onCreate(bundle);
		
		setContentView(R.layout.im_user_manage_activity);
		
		FriendListFragment mFriendList = (FriendListFragment)getSupportFragmentManager().findFragmentById(
				R.id.user_manage_fragment);
		mFriendList.setOnAvater(this);
		mFriendList.setEditable(true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PushServiceUtil.MSG_UNREAD_NUMBBER:                	
                    break;
                case PushServiceUtil.MSG_NEW_MESSAGE:                	             	
                	break;
                case PushServiceUtil.MSG_ROSTER_UPDATED: 
                	break;
                case PushServiceUtil.MSG_REQUEST_VCARD:                	
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }    
	
}
