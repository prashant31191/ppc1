package com.openims.view.chat;


import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.widget.FriendListFragment;
import com.openims.view.chat.widget.FriendListFragment.EditDialogFragment;
import com.smit.EasyLauncher.R;

public class UserManageActivity extends BaseActivity implements OnClickListener{

	FriendListFragment mFriendList;
	@Override
	protected void onCreate(Bundle bundle) {
		mMessenger = new Messenger(new IncomingHandler());
		super.onCreate(bundle);
		
		setContentView(R.layout.im_user_manage_activity);
		
		mFriendList = (FriendListFragment)getSupportFragmentManager().findFragmentById(
				R.id.user_manage_fragment);
		mFriendList.setOnAvater(this);
		mFriendList.setEditable(true);
		findViewById(R.id.add_group).setOnClickListener(this);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		mFriendList = null;
		showAddGrouDialog(false);
		super.onDestroy();
		
	}

	/**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {               
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

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.add_group:
			showAddGrouDialog(true);
			break;
		default:
			break;
		}
	}   
	
	private void showAddGrouDialog(boolean bShow){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("add_group");
        
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        if(bShow == false){
        	return;
        }
		EditDialogFragment fragment = EditDialogFragment.newInstance(mFriendList,
				EditDialogFragment.REQCODE_ADD_GROUP,"");
		fragment.show(getSupportFragmentManager(), "add_group");
	}
}
