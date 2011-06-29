package com.openims.view.chat;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smit.EasyLauncher.R;
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.OnAvater.OnAvaterListener;

public class MessageBoxAdapter extends BaseAdapter implements OnAvaterListener  {	
	
	private Context context;
	private ArrayList<Account> accountList = new ArrayList<Account>(10);
	private RosterDataBase mRosterDataBase;

	private int indexJId = -1;
    private int indexId = -1;
    private int indexUserName = -1;
    private int indexPresence = -1;	
    private int indexUnread = -1;
    private int indexMsgStartId = -1;
    
    private String mMyJid;
    private String mSelectedJid;		// 
	
    private OnAvater mOnAvater;    
    
	public class Account{
		public Integer id;
		public String jId;
		public String userName;
		public Integer unReadNum;
		public Integer msgStartId;
		public boolean isOnline;
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof Account){
				Account a = (Account)o;
				return jId.equals(a.jId);
			}
			return super.equals(o);
		}		
	}
	
	public MessageBoxAdapter(Context context){
		this.context = context;
		mRosterDataBase = new RosterDataBase(context,null);
	}
	
	
	
	@Override
	public int getItemViewType(int position) {
		if(accountList.get(position).jId.equals(mSelectedJid)){
			return 2;
		}
		return 1;
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public int getCount() {
		return accountList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return accountList.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Account account = accountList.get(position);
		
		View view;
		boolean isSelected = false;		
		if(account.jId.equals(mSelectedJid)){
			isSelected = true;
		}
		if(convertView == null){
			int srcId = R.layout.multi_chat_account_viewitem;
			if(isSelected){
				srcId = R.layout.multi_chat_account_viewitem_select;
			}
			view = LayoutInflater.from(context).inflate(
					srcId, null);
		}else{
			view = convertView;
		}
				
		if(isSelected == false){
			TextView title = (TextView) view.findViewById(R.id.title);
			if(account.unReadNum != 0){	
				title.setVisibility(View.VISIBLE);
				title.setText(String.valueOf(account.unReadNum));			
			}else{
				title.setVisibility(View.GONE);
			}
		}
		
		ImageView head = (ImageView)view.findViewById(R.id.imageView1);
        Drawable avater = mOnAvater.getAvater(account.jId, this);
        
        head.setImageDrawable(avater);
        
        if(account.isOnline){	            	
        	head.setColorFilter(null);
        }else{	            	
        	head.setColorFilter(PushServiceUtil.GREY_COLOR_FILTER);
        }
        
        view.setTag(account);
		return view;
	}
	
	public void close(){
		mRosterDataBase.close();
	}
	
	public void setSelectedJid(String jid){
		mSelectedJid = jid;
	}
	public String getSelectedJid(){
		return mSelectedJid;
	}
	/**
	 * delete this account form chat list
	 * @param position
	 * @return if list is empty after delete return null
	 */
	public Account deleteAccount(int position){
		accountList.remove(position);
		int nSize = accountList.size();
		if(nSize == 0){
			return null;
		}
		if(position + 1 > nSize){
			position--;
		}
		return accountList.get(position);
	}
	// ·µ»Ø
	public int initAdapter(String myJid){
		this.mMyJid = myJid;
		RosterDataBase roster = new RosterDataBase(context, mMyJid);
		int startId = 0;
		Cursor c = roster.queryHaveNewMsgRoster();
		if(indexJId == -1){
			indexJId = c.getColumnIndex(RosterDataBase.JID);
    		indexId = c.getColumnIndex(RosterDataBase.ID);
    		indexUserName = c.getColumnIndex(RosterDataBase.USER_NAME);
    		indexPresence = c.getColumnIndex(RosterDataBase.PRESENCE);
    		indexUnread = c.getColumnIndex(RosterDataBase.NEW_MSG_UREAD);
    		indexMsgStartId = c.getColumnIndex(RosterDataBase.NEW_MSG_START_ID);
        }
		c.moveToFirst();
		accountList.clear();
		while(c.isAfterLast() == false){
			Account account = new Account();
			account.id = c.getInt(indexId);
			account.userName = c.getString(indexUserName);	
			account.unReadNum = c.getInt(indexUnread);
			account.msgStartId = c.getInt(indexMsgStartId);
			account.jId = c.getString(indexJId);
			
			String presence = c.getString(indexPresence);
	    	if(presence.equals(Presence.Type.available.name())){
	    		account.isOnline = true;
	    	}else{
	    		account.isOnline = false;
	    	}
	    	
			accountList.add(account);
			c.moveToNext();
			if(account.jId.equals(mSelectedJid)){
				startId = account.msgStartId;
			}
		}		
		return startId;
	}
	
	public String addAccount(long accountId, int unReadNum,int a){

		Cursor cursor = mRosterDataBase.queryById(accountId);
		
        if(indexJId == -1){
        	indexJId = cursor.getColumnIndex(RosterDataBase.JID);
        	indexId = cursor.getColumnIndex(RosterDataBase.ID);
    		indexUserName = cursor.getColumnIndex(RosterDataBase.USER_NAME);
    		indexPresence = cursor.getColumnIndex(RosterDataBase.PRESENCE);
        }
        
        cursor.moveToFirst();
		Account account = new Account();
		account.jId = cursor.getString(indexJId);
		
		int nId = accountList.indexOf(account);
		if(nId == -1){
			account.id = cursor.getInt(indexId);
			account.userName = cursor.getString(indexUserName);;
			accountList.add(0, account);
		}else{
			account = accountList.get(nId);
			account.unReadNum = account.unReadNum + unReadNum;
			accountList.set(nId, account);
		}
		
		return account.jId;
		
	}
	
	public void setOnAvater(OnAvater onAvater){
		mOnAvater = onAvater;
	}
	@Override
	public void avater(String avaterJid, Drawable avater) {
		initAdapter(mMyJid);
		notifyDataSetChanged();
	}
}
