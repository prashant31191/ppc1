package com.openims.view.chat;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openims.R;
import com.openims.model.chat.RosterDataBase;

public class MessageBoxAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Account> accountList = new ArrayList<Account>(10);
	private RosterDataBase mRosterDataBase;

	private int columnIndexJId = -1;
    private int columnIndexId = -1;
    private int indexUserName = -1;
    private int indexPresence = -1;	
    
    private String mSelectedJid;		// 
	
	public class Account{
		public Integer id;
		public String jId;
		public String userName;
		public Integer unReadNum;
		public Integer unTotalNum;
		public Drawable headerDrawable;
		@Override
		public boolean equals(Object o) {
			if(o instanceof Account){
				Account a = (Account)o;
				return jId.equals(a.jId);
			}
			return super.equals(o);
		}
		
		public void setId(Integer id){
			this.id = id;			
		}
		
	}
	
	public MessageBoxAdapter(Context context){
		this.context = context;
		mRosterDataBase = new RosterDataBase(context,null);
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
	public void addAccount(String jId, int unReadNum, int totalNum){
        
		Account account = new Account();
		account.jId = jId;
		int nId = accountList.indexOf(account);
		if(nId == -1){
			Cursor cursor = mRosterDataBase.queryByJId(jId);			
	        if(columnIndexJId == -1){
	        	columnIndexJId = cursor.getColumnIndex(RosterDataBase.JID);
	    		columnIndexId = cursor.getColumnIndex(RosterDataBase.ID);
	    		indexUserName = cursor.getColumnIndex(RosterDataBase.USER_NAME);
	    		indexPresence = cursor.getColumnIndex(RosterDataBase.PRESENCE);
	        }
	        cursor.moveToFirst();
			account.id = cursor.getInt(columnIndexId);
			account.userName = cursor.getString(indexUserName);	
			account.unReadNum = unReadNum;
			account.unTotalNum = totalNum;
			accountList.add(0, account);
		}else{
			account = accountList.get(nId);
			if(totalNum!=0){
				account.unReadNum = unReadNum;
				account.unTotalNum = totalNum;
			}			
			accountList.set(nId, account);
		}		
	}	
	public String addAccount(long accountId, int unReadNum,int a){

		Cursor cursor = mRosterDataBase.queryById(accountId);
		
        if(columnIndexJId == -1){
        	columnIndexJId = cursor.getColumnIndex(RosterDataBase.JID);
    		columnIndexId = cursor.getColumnIndex(RosterDataBase.ID);
    		indexUserName = cursor.getColumnIndex(RosterDataBase.USER_NAME);
    		indexPresence = cursor.getColumnIndex(RosterDataBase.PRESENCE);
        }
        
        cursor.moveToFirst();
		Account account = new Account();
		account.jId = cursor.getString(columnIndexJId);;
		int nId = accountList.indexOf(account);
		if(nId == -1){
			account.id = cursor.getInt(columnIndexId);
			account.userName = cursor.getString(indexUserName);;
			accountList.add(0, account);
		}else{
			account = accountList.get(nId);
			account.unReadNum = account.unReadNum + unReadNum;
			accountList.set(nId, account);
		}
		return account.jId;
		
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
		
		View retval;
		boolean isSelected = false;		
		if(account.jId.equals(mSelectedJid)){
			isSelected = true;
		}
		if(convertView == null){
			int srcId = R.layout.multi_chat_account_viewitem;
			if(isSelected){
				srcId = R.layout.multi_chat_account_viewitem_select;
			}
			retval = LayoutInflater.from(context).inflate(
					srcId, null);
		}else{
			retval = convertView;
		}
				
		if(isSelected == false){
			TextView title = (TextView) retval.findViewById(R.id.title);
			if(account.unReadNum != 0){	
				title.setVisibility(View.VISIBLE);
				title.setText(String.valueOf(account.unReadNum));			
			}else{
				title.setVisibility(View.GONE);
			}
		}
		
		retval.setTag(account);
		return retval;
	}
	
	public void close(){
		mRosterDataBase.close();
	}
}
