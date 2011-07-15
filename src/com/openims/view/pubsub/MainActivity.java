package com.openims.view.pubsub;

import java.util.ArrayList;

import com.smit.EasyLauncher.R;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.pushContent.PushServiceReceiver;

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


public class MainActivity extends Activity{
	private static final String LOGTAG = "chenyz";
	private static final String TAG = "MainActivity--";
	
	private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();   
    private EditText mTopic;
    private EditText mSendText;
    private ListView mList;   
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.chat);
	    	    
	    mTopic = (EditText) this.findViewById(R.id.recipient);
	    mTopic.setText("qq");
        Log.i(LOGTAG, TAG+"mRecipient = " + mTopic);
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i(LOGTAG, TAG+"mSendText = " + mSendText);
        mSendText.setText("hello");
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i(LOGTAG, TAG+"mList = " + mList);
        setListAdapter();
        
        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = mTopic.getText().toString();
                String text = mSendText.getText().toString();

                Log.i(LOGTAG, TAG+"Sending text [" + text + "] to [" + to + "]");
                Intent intent = new Intent(PushServiceUtil.ACTION_SERVICE_PUBSUB);              
                intent.putExtra(PushServiceUtil.MESSAGE_TOWHOS, to);
                intent.putExtra(PushServiceUtil.MESSAGE_CONTENT, text);
                startService(intent);                
                messages.add("ฮาหต:" + text);
                setListAdapter();
            }
        });
        //listener = new PushServiceReceiver(); 
	}
	@Override
    protected void onStart(){
		super.onStart();       
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
			 messages.add(fromName + "หต:");
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