package com.openims.service;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.openims.model.pushService.PushInfoManager;
import com.openims.service.connection.ConnectivityReceiver;
import com.openims.service.notificationPacket.RegPushIQ;
import com.openims.utility.DeviceFun;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;


public class IMService extends Service {

    private static final String TAG = LogUtil.makeLogTag(IMService.class);
    private static final String PRE = LogUtil.makeTag(IMService.class);
    public static final String SERVICE_NAME = "com.openims.service.IMService";
    
    private BroadcastReceiver connectivityReceiver;
    
    private ExecutorService executorService;
    private TaskSubmitter taskSubmitter;
    private TaskTracker taskTracker;
    private XmppManager xmppManager;
    private SharedPreferences sharedPrefs;
    
    private String deviceId;    
   

    /** Keeps track of all current registered clients. */
    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    public IMService(){
    	connectivityReceiver = new ConnectivityReceiver(this);
    	
    	executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);        
    }
    
    @Override
    public void onCreate(){
    	Log.d(TAG, PRE + "onCreate()...");
    	
    	initSetting();
    	sharedPrefs = getSharedPreferences(PushServiceUtil.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    	
    	// ��ʼ���豸��ID
    	initDeviceId();
    	
    	xmppManager = new XmppManager(this);

        taskSubmitter.submit(new Runnable() {
            public void run() {
                IMService.this.start();
            }
        });        
    }
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, PRE + "onStart()...");        
        String action = intent.getAction();
        
        if(PushServiceUtil.ACTION_SERVICE_STATUS.equals(action)){
        	getStatus();
        }else if(PushServiceUtil.ACTION_SERVICE_REGISTER.equals(action)){
        	registerPush(intent);
        }else if(PushServiceUtil.ACTION_SERVICE_MESSAGE.equals(action)){
        	sendMessageChat(intent);
        }else if(PushServiceUtil.ACTION_SERVICE_PUBSUB.equals(action)){
        	sendTopic(intent);
        }
    }

    
    @Override
    public void onDestroy() {
        Log.d(TAG, PRE + "onDestroy()...");
        stop();
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
    	return mMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, PRE + "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, PRE + "onUnbind()...");
        return true;
    }
    
    
    public void connect() {
        Log.d(TAG, PRE + "connect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                IMService.this.getXmppManager().startReconnectionThread();
            }
        });
    }

    public void disconnect() {
        Log.d(TAG, PRE + "disconnect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                IMService.this.getXmppManager().disconnect();
            }
        });
    }
    //----------------getter--------------------
    public static Intent getIntent() {
        return new Intent(SERVICE_NAME);
    }
    public ExecutorService getExecutorService() {
        return executorService;
    }
    public TaskSubmitter getTaskSubmitter() {
        return taskSubmitter;
    }
    public TaskTracker getTaskTracker() {
        return taskTracker;
    }
    public XmppManager getXmppManager() {
        return xmppManager;
    }
    public SharedPreferences getSharedPreferences() {
        return sharedPrefs;
    }
    
    /**
     * Class for summiting a new runnable task.
     */
    public class TaskSubmitter {

        final IMService imService;

        public TaskSubmitter(IMService notificationService) {
            this.imService = notificationService;
        }

        @SuppressWarnings("unchecked")
        public Future submit(Runnable task) {
            Future result = null;
            if (!imService.getExecutorService().isTerminated()
                    && !imService.getExecutorService().isShutdown()
                    && task != null) {
                result = imService.getExecutorService().submit(task);
            }
            return result;
        }
    }
    /**
     * Class for monitoring the running task count.
     */
    public class TaskTracker {

        final IMService imService;

        public int count;

        public TaskTracker(IMService notificationService) {
            this.imService = notificationService;
            this.count = 0;
        }

        public void increase() {
            synchronized (imService.getTaskTracker()) {
            	imService.getTaskTracker().count++;
                Log.d(TAG, PRE + "Incremented task count to " + count);
            }
        }

        public void decrease() {
            synchronized (imService.getTaskTracker()) {
            	imService.getTaskTracker().count--;
                Log.d(TAG, PRE + "Decremented task count to " + count);
            }
        }
    }
    private void initSetting(){
    	
    	SharedPreferences sharedPrefs;
    	Properties props;
    	String version = "0.5.0";
    	String apiKey;
    	String xmppHost;
    	String xmppPort;
    	String userName;
    	String password;
    	
    	props = loadProperties();
        apiKey = props.getProperty("apiKey", "");
        xmppHost = props.getProperty("xmppHost", "127.0.0.1");
        xmppPort = props.getProperty("xmppPort", "5222");
        userName = props.getProperty("userName","");
        password = props.getProperty("password","");
        
        Log.i(TAG, PRE + "apiKey=" + apiKey);
        Log.i(TAG, PRE + "xmppHost=" + xmppHost);
        Log.i(TAG, PRE + "xmppPort=" + xmppPort);

        sharedPrefs = this.getSharedPreferences(
                PushServiceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPrefs.edit();
        editor.putString(PushServiceUtil.API_KEY, apiKey);
        editor.putString(PushServiceUtil.VERSION, version);
        editor.putString(PushServiceUtil.XMPP_HOST, xmppHost);
        editor.putInt(PushServiceUtil.XMPP_PORT, Integer.parseInt(xmppPort));
        editor.putString(PushServiceUtil.XMPP_USERNAME, userName);
        editor.putString(PushServiceUtil.XMPP_PASSWORD, password);        

        editor.commit();
    }
    private void initDeviceId(){
    	deviceId = DeviceFun.getDeviceID();
    	Editor editor = sharedPrefs.edit();
        editor.putString(PushServiceUtil.DEVICE_ID, deviceId);
        editor.commit();
        
        if (deviceId == null 
        		|| deviceId.trim().length() == 0
                || deviceId.matches("0+")) {
            if (sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
                deviceId = sharedPrefs.getString(PushServiceUtil.EMULATOR_DEVICE_ID,
                        "");
            } else {
                deviceId = (new StringBuilder("EMU")).append(
                        (new Random(System.currentTimeMillis())).nextLong())
                        .toString();
                editor.putString(PushServiceUtil.EMULATOR_DEVICE_ID, deviceId);
                editor.commit();
            }
        }
        Log.d(TAG, PRE + "deviceId=" + deviceId);
    }
    
    private void registerConnectivityReceiver() {
        Log.d(TAG, PRE + "registerConnectivityReceiver()...");

        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
        Log.d(TAG, PRE + "unregisterConnectivityReceiver()...");
        
        unregisterReceiver(connectivityReceiver);
    }
    
    private void start() {
        Log.d(TAG, PRE + "start()...");        
        registerConnectivityReceiver(); 
        connect();
    }

    private void stop() {
        Log.d(TAG, PRE + "stop()...");        
        unregisterConnectivityReceiver();
        xmppManager.disconnect();
        executorService.shutdown();
        disconnect();
    }
    
    private void registerPush(Intent intent){
    	Bundle bundle = intent.getExtras();
    	String developer = bundle.getString(PushServiceUtil.PUSH_DEVELOPER);
    	String pushNameKey = bundle.getString(PushServiceUtil.PUSH_NAME_KEY);
    	String packageName = bundle.getString(PushServiceUtil.PUSH_PACKAGENAME);
    	String className = bundle.getString(PushServiceUtil.PUSH_CLASSNAME);
    	String regType = bundle.getString(PushServiceUtil.PUSH_TYPE);
    	
    	// IM service has not connected
    	if(xmppManager.isAuthenticated() == false){
    		sendRegisterBroadcast(packageName,className,null,
    				PushServiceUtil.PUSH_STATUS_UNCONNECT,
    				PushServiceUtil.PUSH_TYPE_REG,this);
    		return;
    	}
    	PushInfoManager pushInfo = new PushInfoManager(this);
    	if(regType.equals(PushServiceUtil.PUSH_TYPE_REG)){
    		if(pushInfo.isRegPush(pushNameKey,xmppManager.getUserNameWithHostName())){
        		sendRegisterBroadcast(packageName,className,null,
        				PushServiceUtil.PUSH_STATUS_HAVEREGISTER,
        				PushServiceUtil.PUSH_TYPE_REG,this);
        		Log.i(TAG, PRE + TAG + "�Ѿ�ע��" + pushNameKey + " " + xmppManager.getUserNameWithHostName());
        		pushInfo.close();
        		return;
        	}    		
    	}else{
    		if(!pushInfo.isRegPush(pushNameKey,xmppManager.getUserNameWithHostName())){
        		sendRegisterBroadcast(packageName,className,null,
        				PushServiceUtil.PUSH_STATUS_NOTREGISTER,
        				PushServiceUtil.PUSH_TYPE_UNREG,this);
        		Log.i(TAG, PRE + TAG + "�Ѿ���ע " + pushNameKey + " " + xmppManager.getUserNameWithHostName());
        		pushInfo.close();
        		return;
        	}
    	}
    	
    	// write information to database
    	pushInfo.insertPushInfotoDb(xmppManager.getUserNameWithHostName(),developer, pushNameKey, packageName, className);
    	pushInfo.close();
    	// send packet
    	RegPushIQ regPushIQ = new RegPushIQ();
    	regPushIQ.setUserName(developer);
    	regPushIQ.setPushServiceName(pushNameKey);
    	
    	if(regType.equals(PushServiceUtil.PUSH_TYPE_REG)){
    		regPushIQ.setRegOrUnreg(true);
    	}else if(regType.equals(PushServiceUtil.PUSH_TYPE_UNREG)){
    		regPushIQ.setRegOrUnreg(false);
    	}
    	
    	xmppManager.sendPacket(regPushIQ);
    	
    	// �ȴ���ʱ��
    	Thread thread = new Thread(new WaitThread(pushNameKey,
    			packageName,className,regType,this));
    	thread.start();
    	
    }
    public static void sendRegisterBroadcast(String packageName,
			   String className,
			   String pushID,
			   String status,
			   String type,
			   Context context){
    	
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("sendRegisterBroadcast to packageName:")
		.append(packageName).append("className:").append(className)
		.append("pushID:").append(pushID).append("status:").append(status);
		Log.d(TAG, PRE + TAG + stringBuilder);
		
		Intent intentSend = new Intent(PushServiceUtil.ACTION_REGISTRATION);
		intentSend.setClassName(packageName, className);    	
		intentSend.putExtra(PushServiceUtil.PUSH_STATUS, status);
		intentSend.putExtra(PushServiceUtil.PUSH_ID, pushID);
		intentSend.putExtra(PushServiceUtil.PUSH_TYPE, type);
		context.sendBroadcast(intentSend);
	}
    private class WaitThread extends Thread {      

    	String pushName = null;
    	String packageName = null;
    	String className = null;
    	String type = null;
    	Context context = null;
    	
        private WaitThread(String pushName,
        				   String packageName,
        				   String className,
        				   String type,
        				   Context context) {
        	this.pushName = pushName;
        	this.packageName = packageName;
        	this.className = className;
        	this.type = type;
        	this.context = context;
        }

        public void run() {
            Log.i(TAG, PRE + TAG + "WaitThread.run()..."); 
            try {
				sleep(PushServiceUtil.PUSH_TIMEOUT_TIME);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
			PushInfoManager pushInfo = new PushInfoManager(IMService.this);
			
			if(type.equals(PushServiceUtil.PUSH_TYPE_REG)){
				if(pushInfo.isRegPush(pushName,xmppManager.getUserNameWithHostName()) == false){
					Log.e(TAG, PRE + TAG + "register time out");
					sendRegisterBroadcast(packageName,className,null,
		    				PushServiceUtil.PUSH_STATUS_FAIL,type,context);
				}
	    	}else if(type.equals(PushServiceUtil.PUSH_TYPE_UNREG)){
	    		if(pushInfo.isRegPush(pushName,xmppManager.getUserNameWithHostName()) == true){
					Log.e(TAG, PRE + TAG + "unregister time out");
					sendRegisterBroadcast(packageName,className,null,
		    				PushServiceUtil.PUSH_STATUS_FAIL,type,context);
				}
	    	}
			pushInfo.close();
			
        }
    }
    /**
     * ��ȡlaw�ļ��������Ϣ
     * @createtime 2011��5��17��9:50:28
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            int id = this.getResources().getIdentifier("androidpn", "raw",
                    this.getPackageName());
            props.load(this.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(TAG, PRE + "Could not find the properties file.", e);
            e.printStackTrace();
        }
        return props;
    }
    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PushServiceUtil.MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case PushServiceUtil.MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case PushServiceUtil.MSG_UNREAD_NUMBBER:                    
                    break;
                case PushServiceUtil.MSG_REQUEST_VCARD:
                	loadVCard((String)msg.obj);
                	break;                
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private void sendMessageChat(Intent intent){
    	Bundle bundle = intent.getExtras();
    	String to = bundle.getString(PushServiceUtil.MESSAGE_TOWHOS);
    	String mesContent = bundle.getString(PushServiceUtil.MESSAGE_CONTENT);  
    	xmppManager.sendChatMessage(to, mesContent); 
    }
    private void sendTopic(Intent intent){
    	String topic = intent.getStringExtra(PushServiceUtil.MESSAGE_TOWHOS);
    	String message = intent.getStringExtra(PushServiceUtil.MESSAGE_CONTENT);
    	xmppManager.sendTopic(topic, message);
    }
    private void getStatus(){
    	if(xmppManager.isAuthenticated()){
    		xmppManager.broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_SUC);
    	}else{
    		//connect();
    		xmppManager.broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_FAIL);
    	}
    }
    public void notifyRosterUpdated(String jid){
    	for (int i=mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(Message.obtain(null,
                		PushServiceUtil.MSG_ROSTER_UPDATED, 0, 1,jid));
            } catch (RemoteException e) {               
                mClients.remove(i);
            }
		}    	
    }
    public void setOneUnreadMessage(String jid){    			
    	
		for (int j=mClients.size()-1; j>=0; j--) {
            try {
                mClients.get(j).send(Message.obtain(null,
                		PushServiceUtil.MSG_NEW_MESSAGE, 
                		0, 0, jid));
            } catch (RemoteException e) {               
                mClients.remove(j);
            }
		} 
		
    }
    // TODO using thread
    private void loadVCard(final String jid){
    	taskSubmitter.submit(new Runnable() {
            public void run() {
            	Log.e(TAG, PRE + "BEGIN LOAD CARD");
            	try {
        			xmppManager.getVCard(jid);
        		} catch (XMPPException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			return;
        		} 
        		
        		for (int j=mClients.size()-1; j>=0; j--) {
                    try {
                        mClients.get(j).send(Message.obtain(null,
                        		PushServiceUtil.MSG_REQUEST_VCARD, 
                        		0, 0, jid));
                    } catch (RemoteException e) {               
                        mClients.remove(j);
                    }
        		}
        		Log.e(TAG, PRE + "END LOAD CARD");
            }
        });
    	
    }
    
    protected void alertbox(String title, String mymessage)
    {
    	WindowManager wm=(WindowManager)getApplicationContext()
    	.getSystemService("window"); 
    	
    	WindowManager.LayoutParams wmParams =new WindowManager.LayoutParams(); 
    	wmParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; 
    	wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    	wmParams.width=WindowManager.LayoutParams.WRAP_CONTENT; 
    	wmParams.height=WindowManager.LayoutParams.WRAP_CONTENT; 
    	Button b = new Button(getApplicationContext());
    	b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {				
			}
    		
    	});;
    	b.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent me) {				
				return false;
			}
    		
    	});
    	b.setText("hello");
    	wm.addView(b, wmParams);
    	
    }
    
    
}
