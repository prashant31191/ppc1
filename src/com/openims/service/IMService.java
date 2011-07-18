package com.openims.service;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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


public class IMService extends Service  {

    private static final String TAG = LogUtil.makeLogTag(IMService.class);
    private static final String PRE = LogUtil.makeTag(IMService.class);
    public static final String SERVICE_NAME = "com.openims.service.IMService";
    
    private BroadcastReceiver connectivityReceiver;
    
    private ExecutorService executorService;
    private TaskSubmitter taskSubmitter;
    private TaskTracker taskTracker;
    private XmppManager xmppManager;
    private SharedPreferences sharedPrefs;
   

    /** Keeps track of all current registered clients. */
    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    private View mPopupView;
    private int mCurrentY;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWmlp;
    
    public IMService(){    	
    	
    	executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);        
    }
    
    @Override
    public void onCreate(){
    	Log.d(TAG, PRE + "onCreate()..."); 
    	
    	initSetting();   
    	initDeviceId();
    	xmppManager = new XmppManager(this);
    	
        //alertbox(null,null);
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
        }else if(PushServiceUtil.ACTION_SERVICE_CONNECT.equals(action)){
        	if(isAutoLogin()){
        		login();
        	}
        }else if(PushServiceUtil.ACTION_SERVICE_LOGIN.equals(action)){
        	//TODO get login information and write to preference
        	
    		String username = intent.getStringExtra(PushServiceUtil.XMPP_USERNAME);
    		String password = intent.getStringExtra(PushServiceUtil.XMPP_PASSWORD);
    		boolean auto_login = intent.getBooleanExtra(PushServiceUtil.XMPP_AUTO_LOGIN,false);
            
    		Log.d("username=",   username);
            Log.d("password=",   password);
            setLogin(username,password,auto_login);
//    		xmppManager.setUsername(username);
//    		xmppManager.setPassword(password);
        	login();
        }else if(PushServiceUtil.ACTION_SERVICE_REGISTER_USER.equals(action)){
        	xmppManager.registerAccount("t", "123456");
        }
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, PRE + "onDestroy()...");
        logout();
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
    /**
     * read default data and write to shared preference
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
    private boolean isAutoLogin(){
    	return sharedPrefs.getBoolean(PushServiceUtil.XMPP_AUTO_LOGIN,false);
    }
    private void setLogin(String userName,String password,boolean bAutoLogin){
    	Editor editor = sharedPrefs.edit();
    	editor.putString(PushServiceUtil.XMPP_USERNAME, userName);
        editor.putString(PushServiceUtil.XMPP_PASSWORD, password);
        editor.putBoolean(PushServiceUtil.XMPP_AUTO_LOGIN, bAutoLogin);
        editor.commit();
        xmppManager.initUserInf();
    }
    private void initSetting(){    	
    	
    	sharedPrefs = getSharedPreferences(PushServiceUtil.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    	
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
    	
    	 String deviceId = DeviceFun.getDeviceID();
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
    
    private void login() {
        Log.d(TAG, PRE + "start()...");        
        
        connectivityReceiver = new ConnectivityReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);        
        connect();
    }

    private void logout() {            
        
        if(connectivityReceiver != null){
        	unregisterReceiver(connectivityReceiver);
        }
        disconnect();
        try {
			executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //executorService.shutdown();
        
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
        		Log.i(TAG, PRE + TAG + "已经注册" + pushNameKey + " " + xmppManager.getUserNameWithHostName());
        		pushInfo.close();
        		return;
        	}    		
    	}else{
    		if(!pushInfo.isRegPush(pushNameKey,xmppManager.getUserNameWithHostName())){
        		sendRegisterBroadcast(packageName,className,null,
        				PushServiceUtil.PUSH_STATUS_NOTREGISTER,
        				PushServiceUtil.PUSH_TYPE_UNREG,this);
        		Log.i(TAG, PRE + TAG + "已经销注 " + pushNameKey + " " + xmppManager.getUserNameWithHostName());
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
    	
    	// 等待超时！
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
                case PushServiceUtil.MSG_REQUEST_VCARD:
                	loadVCard((String)msg.obj);
                	break;                
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private void sendMessageChat(Intent intent){
    	if(xmppManager.isAuthenticated() == false){
    		return;
    	}
    	
    	Bundle bundle = intent.getExtras();
    	String to = bundle.getString(PushServiceUtil.MESSAGE_TOWHOS);
    	String mesContent = bundle.getString(PushServiceUtil.MESSAGE_CONTENT);  
    	xmppManager.sendChatMessage(to, mesContent); 
    }
    private void sendTopic(Intent intent){
    	if(xmppManager.isAuthenticated() == false){
    		return;
    	}
    	
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
    
    private void loadVCard(final String jid){
    	if(xmppManager.isAuthenticated() == false){
    		return;
    	}
    	taskSubmitter.submit(new Runnable() {
            public void run() {
            	Log.e(TAG, PRE + "BEGIN LOAD CARD");
            	try {
        			xmppManager.getVCard(jid);
        		} catch (Exception e) {
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
    	mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);    	
    	
    	WindowManager.LayoutParams wmParams =new WindowManager.LayoutParams(); 
    	wmParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; 
    	wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    	wmParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
    	wmParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    	wmParams.width=100; 
    	wmParams.height=100; 
    	wmParams.alpha=0.5f;
    	wmParams.dimAmount = 0.5f;
    	//wmParams.gravity = Gravity.NO_GRAVITY;
    	wmParams.gravity = Gravity.TOP|Gravity.LEFT;
    	wmParams.x = 0;
    	wmParams.y = 0;
    	Button b = new Button(getApplicationContext());
    	b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {				
			}
    		
    	});;
    	b.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent me) {	
				int action = me.getAction();
				Log.d(TAG, PRE + "action = " + action);
				switch(action){
				case MotionEvent.ACTION_OUTSIDE:
					break;
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				
				return false;
			}
    		
    	});
    	b.setText("hello");
    	mWindowManager.addView(b, wmParams);
    	
    }
    
    private void initPopupWindow(){
    	  // 获取屏幕宽度
    	  DisplayMetrics outMetrics = new DisplayMetrics();
    	  mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
    	  int width = outMetrics.widthPixels;

    	  //mlp = new WindowManager.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	  mWmlp.alpha = 0.5f;
    	  mWmlp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
    	  mWmlp.flags = mWmlp.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
    	  mWmlp.flags = mWmlp.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制
    	  mWmlp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型
    	  mWmlp.width = width;
    	  mWmlp.height = 30;
    	  mWmlp.gravity = 2;
    	  mWmlp.format = -1;
    	  mWmlp.token = null;
    	  mWmlp.x = 0;
    	  mWmlp.y = 200;

    	  mCurrentY = mWmlp.y;

    }
    
    
}
