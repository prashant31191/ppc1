package com.openims.service;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import org.jivesoftware.smack.packet.Message;
import com.openims.model.pushService.PushInfoManager;
import com.openims.service.connection.ConnectivityReceiver;
import com.openims.service.notificationPacket.RegPushIQ;
import com.openims.utility.DeviceFun;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class IMService extends Service {

    private static final String LOGTAG = LogUtil.makeLogTag(IMService.class);
    private static final String TAG = LogUtil.makeTag(IMService.class);
    public static final String SERVICE_NAME = "com.openims.service.IMService";
    
    private BroadcastReceiver connectivityReceiver;
    
    private ExecutorService executorService;
    private TaskSubmitter taskSubmitter;
    private TaskTracker taskTracker;
    private XmppManager xmppManager;
    private SharedPreferences sharedPrefs;
    
    private String deviceId;
    
    public IMService(){
    	connectivityReceiver = new ConnectivityReceiver(this);
    	
    	executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);
        
    }
    @Override
    public void onCreate(){
    	Log.d(LOGTAG, "onCreate()...");
    	
    	initSetting();
    	sharedPrefs = getSharedPreferences(PushServiceUtil.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    	
    	// 初始化设备的ID
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
        Log.d(LOGTAG, "onStart()...");        
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
        Log.d(LOGTAG, "onDestroy()...");
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOGTAG, "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOGTAG, "onUnbind()...");
        return true;
    }
    
    
    public void connect() {
        Log.d(LOGTAG, "connect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                IMService.this.getXmppManager().startReconnectionThread();
            }
        });
    }

    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                IMService.this.getXmppManager().disconnect();
            }
        });
    }
    
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
                Log.d(LOGTAG, "Incremented task count to " + count);
            }
        }

        public void decrease() {
            synchronized (imService.getTaskTracker()) {
            	imService.getTaskTracker().count--;
                Log.d(LOGTAG, "Decremented task count to " + count);
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
        
        Log.i(LOGTAG, "apiKey=" + apiKey);
        Log.i(LOGTAG, "xmppHost=" + xmppHost);
        Log.i(LOGTAG, "xmppPort=" + xmppPort);

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
        Log.d(LOGTAG, "deviceId=" + deviceId);
    }
    
    private void registerConnectivityReceiver() {
        Log.d(LOGTAG, "registerConnectivityReceiver()...");

        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
        Log.d(LOGTAG, "unregisterConnectivityReceiver()...");
        
        unregisterReceiver(connectivityReceiver);
    }
    
    private void start() {
        Log.d(LOGTAG, "start()...");        
        registerConnectivityReceiver();        
        //xmppManager.connect();
        connect();
    }

    private void stop() {
        Log.d(LOGTAG, "stop()...");        
        unregisterConnectivityReceiver();
        //xmppManager.disconnect();
        //executorService.shutdown();
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
    		if(pushInfo.isRegPush(pushNameKey,xmppManager.getUsername())){
        		sendRegisterBroadcast(packageName,className,null,
        				PushServiceUtil.PUSH_STATUS_HAVEREGISTER,
        				PushServiceUtil.PUSH_TYPE_REG,this);
        		Log.i(LOGTAG, TAG + "已经注册" + pushNameKey + " " + xmppManager.getUsername());
        		return;
        	}    		
    	}else{
    		if(!pushInfo.isRegPush(pushNameKey,xmppManager.getUsername())){
        		sendRegisterBroadcast(packageName,className,null,
        				PushServiceUtil.PUSH_STATUS_NOTREGISTER,
        				PushServiceUtil.PUSH_TYPE_UNREG,this);
        		Log.i(LOGTAG, TAG + "已经销注 " + pushNameKey + " " + xmppManager.getUsername());
        		return;
        	}
    	}
    	
    	// write information to database
    	pushInfo.insertPushInfotoDb(xmppManager.getUsername(),developer, pushNameKey, packageName, className);
    	
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
		Log.d(LOGTAG, TAG + stringBuilder);
		
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
            Log.i(LOGTAG, TAG + "WaitThread.run()..."); 
            try {
				sleep(PushServiceUtil.PUSH_TIMEOUT_TIME);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
			PushInfoManager pushInfo = new PushInfoManager(IMService.this);
			
			if(type.equals(PushServiceUtil.PUSH_TYPE_REG)){
				if(pushInfo.isRegPush(pushName,xmppManager.getUsername()) == false){
					Log.e(LOGTAG, TAG + "register time out");
					sendRegisterBroadcast(packageName,className,null,
		    				PushServiceUtil.PUSH_STATUS_FAIL,type,context);
				}
	    	}else if(type.equals(PushServiceUtil.PUSH_TYPE_UNREG)){
	    		if(pushInfo.isRegPush(pushName,xmppManager.getUsername()) == true){
					Log.e(LOGTAG, TAG + "unregister time out");
					sendRegisterBroadcast(packageName,className,null,
		    				PushServiceUtil.PUSH_STATUS_FAIL,type,context);
				}
	    	}
			
        }
    }
    /**
     * 读取law文件里面的信息
     * @createtime 2011年5月17日9:50:28
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            int id = this.getResources().getIdentifier("androidpn", "raw",
                    this.getPackageName());
            props.load(this.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(LOGTAG, "Could not find the properties file.", e);
            e.printStackTrace();
        }
        return props;
    }
    
    private void sendMessageChat(Intent intent){
    	Bundle bundle = intent.getExtras();
    	String to = bundle.getString(PushServiceUtil.MESSAGE_TOWHOS);
    	String mesContent = bundle.getString(PushServiceUtil.MESSAGE_CONTENT);
    	Message msg = new Message(to, Message.Type.chat);
        msg.setBody(mesContent);
               
        
        Log.i(LOGTAG, to +" send msg:" + mesContent);
    	xmppManager.sendPacket(msg); 
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
}
