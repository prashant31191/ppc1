package com.openims.onlineHelper;

import java.util.ArrayList;


import com.openims.R;
import com.openims.demo.PushServiceReceiver;
import com.openims.demo.PushServiceUtil;
import com.openims.service.Constants;
import com.openims.utility.LogUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class ChatActivity extends Activity{
	private static final String LOGTAG = "chenyz";
	private static final String TAG = "ChatActivity--";
	
	private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();   
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;   
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.chat);
	    	    
        mRecipient = (EditText) this.findViewById(R.id.recipient);
        Log.i(LOGTAG, TAG+"mRecipient = " + mRecipient);
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i(LOGTAG, TAG+"mSendText = " + mSendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i(LOGTAG, TAG+"mList = " + mList);
        setListAdapter();
        
        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = mRecipient.getText().toString();
                String text = mSendText.getText().toString();

                Log.i(LOGTAG, TAG+"Sending text [" + text + "] to [" + to + "]");
                Intent intent = new Intent(PushServiceUtil.ACTION_SERVICE_MESSAGE);
                intent.putExtra(PushServiceUtil.MESSAGE_TYPE, "chat");
                intent.putExtra(PushServiceUtil.MESSAGE_TOWHOS, to);
                intent.putExtra(PushServiceUtil.MESSAGE_CONTENT, text);
                startService(intent);
                
                messages.add("我说" + ":");
                messages.add(text);
                setListAdapter();
            }
        });
        //listener = new PushServiceReceiver(); 
	}
	@Override
    protected void onStart(){
		super.onStart();
		messages.add("欢迎来到国微在线客服系统，我们竭诚为您服务");
        
        IntentFilter filter = new IntentFilter();  
        filter.addAction("com.openims.CONNECT_STATUS"); 
        filter.addAction("com.openims.pushService.REGISTRATION"); 
        filter.addAction("com.openims.pushService.RECEIVE"); 
       
    }
	@Override
	protected void onNewIntent (Intent intent){
		setIntent(intent);
	}
	@Override 
	protected void onResume (){
		super.onResume();
		Intent intent=getIntent();
		 String fromName = intent.getStringExtra(PushServiceUtil.MESSAGE_FROM);
		 String message = intent.getStringExtra(PushServiceUtil.MESSAGE_CONTENT);
		 String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
		 if(fromName != null){
			 messages.add(fromName + ":");
			 messages.add(message);
		 }
		 if(status != null){
			 messages.add(status);
		 }
		 updateUI();
		 
	}
	@Override
	protected void onStop(){
		super.onStop();
		String to = mRecipient.getText().toString();
        String text = "用户退出会话啦";
        
        Intent intent = new Intent(PushServiceUtil.ACTION_SERVICE_MESSAGE);
        intent.putExtra(PushServiceUtil.MESSAGE_TYPE, "chat");
        intent.putExtra(PushServiceUtil.MESSAGE_TOWHOS, to);
        intent.putExtra(PushServiceUtil.MESSAGE_CONTENT, text);
        //startService(intent);
		
	}
	private void setListAdapter() {
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        R.layout.multi_line_list_item,
		        messages);
		mList.setAdapter(adapter);
	}
	private void updateUI(){
		// Add the incoming message to the list view
		 mHandler.post(new Runnable() {
		     public void run() {
		         setListAdapter();
		     }
		 });
	}
	
}