package com.openims.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Type;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.IBBProviders;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

import com.openims.model.MyApplication;
import com.openims.model.chat.RosterDataBase;
import com.openims.model.chat.VCardDataBase;
import com.openims.service.chat.ChatPacketListener;
import com.openims.service.chat.MyRosterListener;
import com.openims.service.chat.PresenceListener;
import com.openims.service.connection.PersistentConnectionListener;
import com.openims.service.connection.ReconnectionThread;
import com.openims.service.fileTransfer.FileReceiver;
import com.openims.service.notificationPacket.NotificationIQ;
import com.openims.service.notificationPacket.NotificationIQProvider;
import com.openims.service.notificationPacket.NotificationPacketListener;
import com.openims.service.notificationPacket.RegPushIQ;
import com.openims.service.notificationPacket.RegPushPacketListener;
import com.openims.service.notificationPacket.RegPushProvider;
import com.openims.service.notificationPacket.UserQueryIQ;
import com.openims.service.pubsub.SubListener;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.smit.EasyLauncher.R;
/**
 * This class deal with login logout send message and broadcast
 * it is smack center
 * @author ANDREW CHAN (chenyzpower@gmail.com)
 *
 */
public class XmppManager{
	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);
	private static final String TAG = LogUtil.makeTag(XmppManager.class);
	
	/**
	 * login information
	 */
	private String xmppHost;
	private int xmppPort;	
    private String username;
    private String password;
    private	String resource = null;
    private String mAdminJid;
    private SharedPreferences sharedPrefs;
    
    private String mRegUsername;
    private String mRegPassword;
    
    private XMPPConnection connection;
    private IMService imservice;
    private IMService.TaskSubmitter taskSubmitter;
    private IMService.TaskTracker taskTracker;
    
    // TODO 需要优化离线和待机的情况，释放一下资源
    private List<Runnable> taskList;
    private boolean running = false;
    private Future<?> futureTask;
    private Thread reconnection = null;
    int reconnectTime = 0;
    private Handler handler;
    
    /**
     * register XMPP handle listener
     */
    private ConnectionListener connectionListener;
    private PacketListener notificationPacketListener;
    private PacketListener regPushPacketListener;
    private FileTransferManager fileTransferManager;   
    
    /**
     * publish subscription
     */
    private Map<String,LeafNode> topicMap = null;
    
    public XmppManager(IMService imservice){
    	this.imservice = imservice;
    	taskSubmitter = imservice.getTaskSubmitter();
        taskTracker = imservice.getTaskTracker();
        sharedPrefs = imservice.getSharedPreferences();
        
        initUserInf();
        
        connectionListener = new PersistentConnectionListener(this);
        notificationPacketListener = new NotificationPacketListener(this);
        regPushPacketListener = new RegPushPacketListener(this);

        handler = new Handler();
        taskList = new ArrayList<Runnable>();
        
    	
        topicMap = new HashMap<String,LeafNode>();
        
        configure(ProviderManager.getInstance());        
    }
    public void initUserInf(){
    	xmppHost = sharedPrefs.getString(PushServiceUtil.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(PushServiceUtil.XMPP_PORT, 5222);
        username = sharedPrefs.getString(PushServiceUtil.XMPP_USERNAME, "");
        password = sharedPrefs.getString(PushServiceUtil.XMPP_PASSWORD, "");
        resource = sharedPrefs.getString(PushServiceUtil.XMPP_RESOURCE, "");
    }
    
    /**
     * dynamic create resource
     * @return
     */
    public String getResource(){
    	if(resource == null || resource.length()==0){
    		resource = newRandomUUID();
    		 Editor editor = sharedPrefs.edit();
             editor.putString(PushServiceUtil.XMPP_RESOURCE,
            		 resource);
             editor.commit();
    	}    		
    	return this.resource;
    }
    public void connect() {
        Log.d(LOGTAG, "connect()...");
        submitLoginTask();
    }
    /**
     * log out
     */
    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        RosterDataBase rosterDataBase = new RosterDataBase(this.imservice,mAdminJid);
    	rosterDataBase.removeAll();  // delete all
    	rosterDataBase.close();
        if (getConnection() != null && 
            	getConnection().isConnected()) 
        {
            getConnection().disconnect();
        }
        stopReconnectionThread();
        terminatePersistentConnection();
        broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGOUT);
    }
    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
        runTask();
    }    

    private void terminatePersistentConnection() {
        Log.d(LOGTAG, "terminatePersistentConnection()...");
        Runnable runnable = new Runnable() {

            final XmppManager xmppManager = XmppManager.this;

            public void run() {
                if (xmppManager.isConnected()) {
                    Log.d(LOGTAG, "terminatePersistentConnection()... run()");
                    xmppManager.getConnection().removePacketListener(
                            xmppManager.getNotificationPacketListener());
                    xmppManager.getConnection().disconnect();
                }
                // TODO-ANDREW should end reconnection thread
                xmppManager.runTask();
            }

        };
        addTask(runnable);
    }
    public void sendChatMessage(String to, String mesContent){
    	Message msg = new Message(to, Message.Type.chat);
        msg.setBody(mesContent);
        Log.i(LOGTAG, to +" send msg:" + mesContent);
    	sendPacket(msg); 
    }
    public void sendPacket(Packet packet){
    	if(this.isAuthenticated())
    	connection.sendPacket(packet);
    	else
    		broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_FAIL);
    }
    public void startReconnectionThread() {
        /*synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
            }
        }*/    	
    	//reconnection = new ReconnectionThread(this);
    	//reconnection.start();
    	//increaseReconnectTime();
    }
    private void stopReconnectionThread(){
    	if(reconnection==null)
    		return;
    	synchronized (reconnection) {    		
    		reconnection.interrupt();
    	}
    }
    private void increaseReconnectTime(){
    	reconnectTime =+ 3;
    	if(reconnectTime > 30){
    		reconnectTime = 30;
    	}
    }
    private void resetReconnectTime(){
    	reconnectTime = 0;
    }
    private void clearTask(){
    	taskList.clear();
    	taskTracker.count = 0; 
    	running = false;
    }
    private void runTask() {
        Log.d(LOGTAG, "runTask()...");
        synchronized (taskList) {
            running = false;
            futureTask = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                taskList.remove(0);
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            }
        }
        taskTracker.decrease();
        Log.d(LOGTAG, "runTask()...done");
    }
    public PacketListener getNotificationPacketListener() {
        return notificationPacketListener;
    }
    public PacketListener getRegPushPacketListener(){
    	return regPushPacketListener;
    }
    public Context getContext(){
    	return imservice;
    }
    public XMPPConnection getConnection() {
        return connection;
    }
    private void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }  
    public String getUserNameWithHostName(){
    	return mAdminJid;
    }
    private void setUsername(String username) {
        this.username = username;
    }
    private String getPassword() {
        return password;
    }
    private void setPassword(String password) {
        this.password = password;
    }
    /**
     * for reconnection
     * @return
     */
    public Handler getHandler() {
        return handler;
    }
    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }
    
    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }
    
    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

    private boolean isRegistered() {
        return sharedPrefs.contains(PushServiceUtil.XMPP_USERNAME)
                && sharedPrefs.contains(PushServiceUtil.XMPP_PASSWORD);
    }
    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(PushServiceUtil.XMPP_USERNAME);
        editor.remove(PushServiceUtil.XMPP_PASSWORD);
        editor.commit();
    }   
    
    private void submitConnectTask() {
        Log.d(LOGTAG, "submitConnectTask()...");
        addTask(new ConnectTask());
    }
    private void submitRegisterTask() {
        Log.d(LOGTAG, "submitRegisterTask()...");
        submitConnectTask();
        addTask(new RegisterTask());
    }
    private void submitLoginTask() {
        Log.d(LOGTAG, "submitLoginTask()...");
        submitRegisterTask();
        addTask(new LoginTask());
    }
    private void addTask(Runnable runnable) {
        Log.d(LOGTAG, "addTask(runnable)...");
        taskTracker.increase();
        synchronized (taskList) {
        	// 如果任务列表为空，同时没有东西在运行，就开始运行
            if (taskList.isEmpty() && !running) {
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            } else {
                taskList.add(runnable);
            }
        }
        Log.d(LOGTAG, "addTask(runnable)... done");
    }
    
    /**
     * A runnable task to connect the server. 
     */
    private class ConnectTask implements Runnable {

        final XmppManager xmppManager;

        private ConnectTask() {
            this.xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "ConnectTask.run()...");

            if (!xmppManager.isConnected()) {
                // Create the configuration for this new connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(
                        xmppHost, xmppPort);
                connConfig.setSecurityMode(SecurityMode.disabled);
             // connConfig.setSecurityMode(SecurityMode.required);
                connConfig.setSASLAuthenticationEnabled(false);
                connConfig.setCompressionEnabled(false);
                connConfig.setReconnectionAllowed(true);

                XMPPConnection connection = new XMPPConnection(connConfig);
                xmppManager.setConnection(connection);                
                

                try {
                    // Connect to the server
                	Log.i(LOGTAG, "Start to connect...");
                    connection.connect();
                    Log.i(LOGTAG, "XMPP connected successfully");   
                    
                    // Set the status to available
                    Log.i(LOGTAG, TAG+"set presence");
                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                    
                    // packet provider
                    ProviderManager.getInstance().addIQProvider("openims",
                            "smit:iq:notification",
                            new NotificationIQProvider());
                    ProviderManager.getInstance().addIQProvider("openims",
                          "smit:iq:registerPushService",
                          new RegPushProvider());
                    initPubSub();

                } catch (XMPPException e) {
                	broadcastStatus(PushServiceUtil.PUSH_STATUS_CONNECTION_FAIL);
                    Log.e(LOGTAG, "XMPP connection failed", e);
                }                

            } else {
                Log.i(LOGTAG, "XMPP connected already");                
            }
            xmppManager.runTask();
        }
    }
    /**
     * A runnable task to register a new user onto the server. 
     */
    private class RegisterTask implements Runnable {

        final XmppManager xmppManager;

        private RegisterTask() {
            xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "RegisterTask.run()...");
            if(!xmppManager.isConnected())
            {
            	runTask();
            	return;
            }
            if (!xmppManager.isRegistered()) { 
                final String newUsername = mRegUsername;
                final String newPassword = mRegPassword;

                Registration registration = new Registration();

                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(
                        IQ.class));

                PacketListener packetListener = new PacketListener() {

                    public void processPacket(Packet packet) {
                        Log.d("RegisterTask.PacketListener","processPacket().....");
                        Log.d("RegisterTask.PacketListener", "packet=" + packet.toXML());

                        if (packet instanceof IQ) {
                            IQ response = (IQ) packet;
                            if (response.getType() == IQ.Type.ERROR) {
                                if (response.getError().getCode() ==409) {
                                    Log.e(LOGTAG,
                                            "Unknown error while registering XMPP account! "
                                                    + response.getError().getCondition());
                                    
                                    broadcastStatus(PushServiceUtil.PUSH_STATUS_HAVEREGISTER);
                                }else{
                                    broadcastStatus(PushServiceUtil.PUSH_STATUS_REGISTER_FAIL);
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                                xmppManager.setUsername(newUsername);
                                xmppManager.setPassword(newPassword);
                                Log.d(LOGTAG, "username=" + newUsername);
                                Log.d(LOGTAG, "password=" + newPassword);

                                Editor editor = sharedPrefs.edit();
                                editor.putString(PushServiceUtil.XMPP_USERNAME,
                                        newUsername);
                                editor.putString(PushServiceUtil.XMPP_PASSWORD,
                                        newPassword);
                                editor.commit();
                                
                                Log.i(LOGTAG,"Account registered successfully");
                                broadcastStatus(PushServiceUtil.PUSH_STATUS_REGISTER_SUC);                                
                            }
                        }
                        xmppManager.runTask();
                    }
                };

                connection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);                
                registration.addAttribute("username", newUsername);
                registration.addAttribute("password", newPassword);
                connection.sendPacket(registration);

            } else {
                Log.i(LOGTAG, "Account registered already"); 
                xmppManager.runTask();
            }
            
        }
    }
    /**
     * A runnable task to log into the server. 
     */
    private class LoginTask implements Runnable {

        final XmppManager xmppManager;

        private LoginTask() {
            this.xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "LoginTask.run()...");
            if(!xmppManager.isConnected())
            {
            	runTask();
            	return;
            }
            if (!xmppManager.isAuthenticated()) {
                Log.d(LOGTAG, "username=" + username);
                Log.d(LOGTAG, "password=" + password);

                try {
                    xmppManager.getConnection().login(
                            xmppManager.username,
                            xmppManager.getPassword(), getResource());
                    Log.d(LOGTAG, "Loggedn in successfully"); 
                    
                    initAfterLogin(); 
                    broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_SUC); 

                } catch (XMPPException e) {
                    Log.e(LOGTAG, "LoginTask.run()... xmpp error");
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "+ e.getMessage());
                    broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_FAIL);                    
                    
                    String INVALID_CREDENTIALS_ERROR_CODE = "401";
                    String errorMessage = e.getMessage();
                    if (errorMessage != null
                        && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                    	broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_UNREG);
                    	xmppManager.clearTask();
                        return;
                    }                    

                } catch (Exception e) {
                    Log.e(LOGTAG, "LoginTask.run()... other error");
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_FAIL);                    
                }

            } else {
                Log.i(LOGTAG, "Logged in already");
                broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_SUC);                
            }
            xmppManager.runTask();
        }
    }
    public void registerAccount(String userName,String psw){
    	
    	 mRegUsername = userName;
         mRegPassword = psw;

         removeAccount();
         submitRegisterTask();
         runTask();
         
         //Collection<String> a =  connection.getAccountManager().getAccountAttributes();
    	
    }
    public void broadcastStatus(String inf){
    	Intent intentSend = new Intent(PushServiceUtil.ACTION_STATUS);
		intentSend.putExtra(PushServiceUtil.PUSH_STATUS, inf);
		imservice.sendBroadcast(intentSend);
		
    }
    
    public void notifyNewMessage(String jid){
    	imservice.setOneUnreadMessage(jid);
    }
    public void notifyRosterUpdated(String jid){
    	imservice.notifyRosterUpdated(jid);
    }
    private void initPubSub(){
    	ProviderManager pm = ProviderManager.getInstance();
        pm.addIQProvider(
            "query", "http://jabber.org/protocol/disco#items",
             new org.jivesoftware.smackx.provider.DiscoverItemsProvider()
        );
        
        pm.addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new org.jivesoftware.smackx.provider.DiscoverInfoProvider());
        
        pm.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.PubSubProvider());

        pm.addExtensionProvider("subscription", PubSubNamespace.BASIC.getXmlns() , new SubscriptionProvider());
        
        pm.addExtensionProvider(
                "create",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());
        
        pm.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.ItemsProvider());
        
        pm.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());
        
        pm.addExtensionProvider("item", "",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());
        
        pm.addExtensionProvider(
                        "subscriptions",
                        "http://jabber.org/protocol/pubsub",
                        new org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider());

        pm.addExtensionProvider(
                        "subscriptions",
                        "http://jabber.org/protocol/pubsub#owner",
                        new org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider());

        pm.addExtensionProvider(
                        "affiliations",
                        "http://jabber.org/protocol/pubsub",
                        new org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider());
        
        pm.addExtensionProvider(
                        "affiliation",
                        "http://jabber.org/protocol/pubsub",
                        new org.jivesoftware.smackx.pubsub.provider.AffiliationProvider());
        
        pm.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());
        
        pm.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.PubSubProvider());
        
        pm.addExtensionProvider("configure",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());
        
        pm.addExtensionProvider("default",
                "http://jabber.org/protocol/pubsub#owner",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());


        pm.addExtensionProvider("event",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.EventProvider());
        
        pm.addExtensionProvider(
                        "configuration",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider());
        
        pm.addExtensionProvider(
                        "delete",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());
        
        pm.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.FormNodeProvider());
        
        pm.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ItemsProvider());
        
        pm.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub#event",
                new org.jivesoftware.smackx.pubsub.provider.ItemProvider());

        pm.addExtensionProvider("headers",
                "http://jabber.org/protocol/shim",
                new org.jivesoftware.smackx.provider.HeaderProvider());

        pm.addExtensionProvider("header",
                "http://jabber.org/protocol/shim",
                new org.jivesoftware.smackx.provider.HeadersProvider());
        
        
        pm.addExtensionProvider(
                        "retract",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.RetractEventProvider());
        
        pm.addExtensionProvider(
                        "purge",
                        "http://jabber.org/protocol/pubsub#event",
                        new org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider());
        
        pm.addExtensionProvider(
                "x",
                "jabber:x:data",
                new org.jivesoftware.smackx.provider.DataFormProvider());

       
    }
    private List<String> getTopicNode(){
    	PubSubManager manager = new PubSubManager(connection, "pubsub.smitnn");
    	List<String> nodes = new LinkedList<String>();
    	try {
			DiscoverItems dItems = manager.discoverNodes(null);
			Iterator<Item> it = dItems.getItems();
			Item item;
			while(it.hasNext()){
				item = it.next();
				Log.d(LOGTAG,TAG+item.toXML());
				nodes.add(item.getNode());
			}
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodes;
    }
    private boolean listenTopic(List<String> topics){
    	
    	boolean b = true;
    	topicMap.clear();
    	StringBuilder user = new StringBuilder(connection.getUser().trim());
		user.delete(user.lastIndexOf("/"), user.length());
    	for(int i=0;i<topics.size();i++){
    		PubSubManager manager = new PubSubManager(connection,"pubsub.smitnn");    		
    		LeafNode leafNode = null;    		
    		try
    		{
    			String topic = topics.get(i);
    			Node node = manager.getNode(topic);
    			if(node == null){
    				b = false;
    				continue;
    			}    				
    			node.subscribe(user.toString());
    			node.addItemEventListener(
    					new SubListener(XmppManager.this,topic)); 
    			leafNode = (LeafNode)node;
    			topicMap.put(topic, leafNode);
    			leafNode = null;
    			Log.d(LOGTAG,TAG+"subscription topic:" + topic);
    		} catch(XMPPException e1){
    			e1.printStackTrace();
    			b = false;    			
    		} catch(Exception e){
    			e.printStackTrace();
    			b = false;
    		}
    	}
    	return b;
    }
    private LeafNode getLeafNode(String topic){
    	
    	LeafNode leafNode = null;
		PubSubManager manager = new PubSubManager(connection,"pubsub.smitnn");
		Log.i(LOGTAG,TAG+connection.getHost());
		try
		{
			Node node = manager.getNode(topic);
			leafNode = (LeafNode)node;			
		} catch(XMPPException e1){
			e1.printStackTrace();
			XMPPError error = e1.getXMPPError();
			Type type = error.getType();
			try 
			{
				ConfigureForm f = new ConfigureForm(FormType.submit);    		
				
				f.setDeliverPayloads(true);
				f.setAccessModel(AccessModel.open);
				f.setPublishModel(PublishModel.open);
				f.setSubscribe(true);
				f.setPersistentItems(true);      
				
				Node eventNode = manager.createNode(topic,f); 
				leafNode = (LeafNode)eventNode;				
				Log.d(LOGTAG,TAG+"create new node "+topic);
				
			} catch (XMPPException e2)
			{    			
				e2.printStackTrace();
				return null;
			}
		}
		leafNode.addItemEventListener(
				new SubListener(XmppManager.this)); 
		
		// you will need this first time only
		try {
			StringBuilder user = new StringBuilder(connection.getUser().trim());
			user.delete(user.lastIndexOf("/"), user.length());
			Subscription sst = leafNode.subscribe(user.toString());
			Log.d(LOGTAG,TAG+connection.getUser()+"subscription");
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			 System.out.println("XMPPClient errore durante la subscribe al nodo"+ e1.toString());
		}
		return leafNode;
    }
    public void sendTopic(String topic,String message){
    	
    	sendFile();
    	if(message.equals("hello"))
    		return;
    	if(!this.isAuthenticated()){
    		broadcastStatus(PushServiceUtil.PUSH_STATUS_LOGIN_FAIL);
    		return;
    	}
        		
    	LeafNode leafNode = (LeafNode)topicMap.get(topic);
    	
    	if(leafNode==null){
    		leafNode = getLeafNode(topic);
    		if(leafNode == null){
    			Log.e(LOGTAG,TAG+"can't get topic" + topic);
    			return;
    		}else{
    			topicMap.put(topic, leafNode);
    		}
    	}
		
		SimplePayload payload = new SimplePayload("event", null,
	            "<all>" +
	            message +
	            "</all>");

		PayloadItem payloadItem = new PayloadItem(null, payload);
		
		try {
			leafNode.send(payloadItem);
			Log.d(LOGTAG,TAG+"Send info" + message);
		} catch (XMPPException e) {			
			e.printStackTrace();
			broadcastStatus(PushServiceUtil.PUSH_STATUS_SENDFAIL);
			// 发送失败
		}
    }
    private void initAfterLogin(){
    	
    	// get server name
    	String serverName = getConnection().getServiceName();
    	mAdminJid = username + "@"+serverName;
    	Editor editor = sharedPrefs.edit();
    	editor.putString(PushServiceUtil.XMPP_HOSTNAME,"@"+serverName);
    	editor.commit();
    	
    	getRoster();
    	 
    	// connection listener
        if (getConnectionListener() != null) {
            getConnection().addConnectionListener(
                    getConnectionListener());
        }
        // packet filter
        // packet listener
        PacketFilter packetFilter = new PacketTypeFilter(
                NotificationIQ.class);                    
        PacketListener packetListener = getNotificationPacketListener();
        connection.addPacketListener(packetListener, packetFilter);
        
        PacketFilter packetFilter2 = new PacketTypeFilter(
                RegPushIQ.class);                    
        PacketListener packetListener2 = getRegPushPacketListener();
        connection.addPacketListener(packetListener2, packetFilter2);
        
        PacketFilter chatFilter = new MessageTypeFilter(Message.Type.chat);
        ChatPacketListener chatListener = new ChatPacketListener(this);
        connection.addPacketListener(chatListener, chatFilter);
        
        PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
        PresenceListener presenceListener = new PresenceListener(this);
        connection.addPacketListener(presenceListener,presenceFilter);
       
        
        // TODO-ANDREW test need to delete
        NotFilter notFilter = new NotFilter(chatFilter);
        connection.addPacketListener(new PacketListener(){
        	public void processPacket(Packet packet) {
        		Log.i(LOGTAG,TAG+"xmnl:"+packet.getXmlns()
        				+";From:"+packet.getFrom());
        		Log.i(LOGTAG,TAG+packet.toXML());
        	}
        },notFilter);    
        
        List<String> topics = getTopicNode();
        listenTopic(topics);
        new ServiceDiscoveryManager(connection);//for file transfer
        
    	try{
    		fileTransferManager = new FileTransferManager(connection);
    		fileTransferManager.addFileTransferListener(new FileReceiver(this));
    	}catch(Exception e){
    		Log.e(LOGTAG,"get file manager listener error");
    		e.printStackTrace();    		
    	}    	
    	
    	// send device information to server
    	UserQueryIQ iq = new UserQueryIQ();
    	iq.setDeviceId(sharedPrefs.getString(PushServiceUtil.DEVICE_ID, ""));
    	iq.setUserAccount(mAdminJid);
    	iq.setResource(getResource());
    	iq.setDeviceName("SMIT1800");
    	iq.setOpCodeSave();
    	this.sendPacket(iq);
        
    	UserQueryIQ iqQuery = new UserQueryIQ();
    	iq.setDeviceId(sharedPrefs.getString(PushServiceUtil.DEVICE_ID, ""));
    	iq.setUserAccount(mAdminJid);
    	iq.setResource(getResource());
    	iq.setDeviceName("SMIT1800");
    	iqQuery.setOpCodeQueryOfflinePush();
    	this.sendPacket(iqQuery);
    	
    	// share connection
    	MyApplication app = (MyApplication)this.imservice.getApplication();
    	app.setConnection(getConnection(),username+"@"+serverName);
    	app.setServeName("@"+serverName);
    }
   
    public void updateRoster(String jid){
    	Roster roster = connection.getRoster();
    	RosterEntry entry = roster.getEntry(jid);
    	RosterDataBase rosterDataBase = new RosterDataBase(this.imservice,
    			mAdminJid);
    	String presenceInf = roster.getPresence(jid).getType().name();
    	
    	Collection<RosterGroup> clGroup = entry.getGroups();
    	Iterator<RosterGroup> iterator = clGroup.iterator();
    	while(iterator.hasNext()){
    		RosterGroup rg = iterator.next();
    		rosterDataBase.insert(jid, entry.getName(), rg.getName(), presenceInf);
    	}   	
    	
    	rosterDataBase.close();    	
    }
    public void deleteRoster(String jid){
    	RosterDataBase rosterDataBase = new RosterDataBase(this.imservice,mAdminJid);
    	rosterDataBase.deleteRoster(jid);
    	rosterDataBase.close();   
    }
    public void getRoster(){
    	Roster roster = connection.getRoster();
    	
    	Collection<RosterGroup> crg = roster.getGroups();
    	Iterator<RosterGroup> iterator = crg.iterator();
    	RosterDataBase rosterDataBase = new RosterDataBase(this.imservice,mAdminJid);
    	rosterDataBase.removeAll();  // delete all
    	while(iterator.hasNext()){
    		RosterGroup rg = (RosterGroup)iterator.next();
    		
    		Collection<RosterEntry> re = rg.getEntries();
    		Iterator<RosterEntry> iteratorEntry = re.iterator();
    		while(iteratorEntry.hasNext()){
    			RosterEntry entry = (RosterEntry)iteratorEntry.next();   
    			String presenceInf = roster.getPresence(entry.getUser()).getType().name();
    			rosterDataBase.insert(entry.getUser(), entry.getName(), rg.getName(),presenceInf);
    			
    		}
    	}
    	String defaultGroupName = getContext().getResources().getString(R.string.im_default_group_name);
    	if(rosterDataBase.isGroupNameExist(defaultGroupName) == false){
    		rosterDataBase.insert(mAdminJid, mAdminJid, defaultGroupName, null);
    	}
    	
    	rosterDataBase.close();
    	
    	roster.addRosterListener(new MyRosterListener(this)); 
    	
    	 
    	VCardDataBase vc = new VCardDataBase(this.getContext(),
       			 this.getUserNameWithHostName());
    	vc.removeAll();       	 	
       	vc.close();
    }
    public void getVCard(String jid) throws Exception{
    	
    	VCard vcard = new VCard();  
    	try { 
    		
    		vcard.load(this.connection,jid);    	
       	 	
		} catch (XMPPException e) {
			Log.e(LOGTAG,TAG+"get vcard exception");
			vcard = null;
			e.printStackTrace();
		} catch(Exception e){			
			e.printStackTrace();
		}finally{			
		}
		VCardDataBase vc = new VCardDataBase(this.getContext(),
      			 this.getUserNameWithHostName());
		if(vcard == null){
			vc.insert(jid);
		}else{
			vc.insert(jid, vcard);
		}
 	 	vc.close();	
 	 	if( vcard == null){
 	 		throw new Exception("error");
 	 	}
    }
    public void configure(ProviderManager pm) {
    	 
        //  Private Data Storage
        pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
 
 
        //  Time
        try {
            pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }
 
        //  Roster Exchange
        pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
 
        //  Message Events
        pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
 
        //  Chat State
        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
 
        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
 
        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
 
        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
 
        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
 
        //  XHTML
        pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
 
        //  Group Chat Invitations
        pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());
 
        //  Service Discovery # Items    
        pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
 
        //  Service Discovery # Info
        pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
 
        //  Data Forms
        pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());
 
        //  MUC User
        pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());
 
        //  MUC Admin    
        pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
 
 
        //  MUC Owner    
        pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
 
        //  Delayed Delivery
        pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());
 
        //  Version
        try {
            pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }
 
        //  VCard
        pm.addIQProvider("vCard","vcard-temp", new VCardProvider());
 
        //  Offline Message Requests
        pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
 
        //  Offline Message Indicator
        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
 
        //  Last Activity
        pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());
 
        //  User Search
        pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
 
        //  SharedGroupsInfo
        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
 
        //  JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());
 
        //   FileTransfer
        pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());
 
        pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
 
        pm.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());
 
        pm.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());
 
        pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());
 
        //  Privacy
        pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
 
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }
     
    public void sendFile(){
    	File file = new File("/sdcard/t.txt");
    	long fileLen = file.length();
    	
        OutgoingFileTransfer transfer = fileTransferManager.createOutgoingFileTransfer("test2@smitnn/spark");
        long timeOut = 60000;
        long sleepMin = 3000;
        long spTime = 0;
        int rs = 0;  
       
            try {
				transfer.sendFile(file, "pls re file!");
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            rs = transfer.getStatus().compareTo(FileTransfer.Status.complete);
            while(rs!=0){
                rs = transfer.getStatus().compareTo(FileTransfer.Status.complete);
                 spTime = spTime + sleepMin;
                 if(spTime>timeOut){return ;}
                try {
					Thread.sleep(sleepMin);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
    }
    
    public void setNewPushContent(){
    	imservice.setNewPushContent();
    }
    
}

/*DiscoverItems dn = leafNode.discoverItems();		
Iterator<Item> it =dn.getItems();
while(it.hasNext()){
	Item item = it.next();
	String xml = item.toXML();
	Log.i(LOGTAG,TAG+xml);
}*/

//stopReconnectionThread();

/*    	 

	VCard vCard = new VCard();
	 vCard.setFirstName("kir");
	 vCard.setLastName("max");
	 vCard.setEmailHome("foo@fee.bar");
	 vCard.setJabberId("jabber@id.org");
	 vCard.setOrganization("Jetbrains, s.r.o");
	 vCard.setNickName("KIR");
	 
	
	
	 vCard.setField("TITLE", "Mr");
	 vCard.setAddressFieldHome("STREET", "Some street");
	 vCard.setAddressFieldWork("CTRY", "US");
	 vCard.setPhoneWork("FAX", "3443233");
	 
	 try {
		vCard.save(connection);
	} catch (XMPPException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
*/    

