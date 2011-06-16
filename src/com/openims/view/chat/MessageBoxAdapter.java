package com.openims.view.chat;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openims.R;

public class MessageBoxAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Integer> accountIdList = new ArrayList<Integer>(10);
	private ArrayList<Integer> unReadNumList = new ArrayList<Integer>(10);
	private ArrayList<Integer> unTotalNumList = new ArrayList<Integer>(10);
	
	public MessageBoxAdapter(Context context){
		this.context = context;
	}
	public void addAccount(int accountId, int unReadNum){
		int nId = accountIdList.indexOf(accountId);
		if(nId == -1){
			accountIdList.add(0, accountId);
			unReadNumList.add(0, unReadNum);
		}else{
			accountIdList.set(nId, accountId);
			unReadNumList.set(nId, unReadNumList.get(nId) + unReadNum);
		}
		
	}	
	
	@Override
	public int getCount() {
		return accountIdList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return accountIdList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int accountId = accountIdList.get(position);
		int unReadNum = unReadNumList.get(position);
		//int totalNum = unTotalNumList.get(position);
		View retval = LayoutInflater.from(context).inflate(
				R.layout.multi_chat_account_viewitem, null);
		TextView title = (TextView) retval.findViewById(R.id.title);
		title.setText(accountId + "--" + unReadNum);
		retval.setTag(Integer.valueOf(unReadNum));
		return retval;
	}
	
}
