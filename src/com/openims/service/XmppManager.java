package com.openims.service;

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
import org.jivesoftware.smack.ReconnectionManager;
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
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;

import com.openims.notificationPacket.NotificationIQ;
import com.openims.notificationPacket.NotificationIQProvider;
import com.openims.notificationPacket.NotificationPacketListener;
import com.openims.notificationPacket.RegPushIQ;
import com.openims.notificationPacket.RegPushPacketListener;
import com.openims.notificationPacket.RegPushProvider;
import com.openims.pubsub.SubListener;
import com.openims.utility.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

public class XmppManager{
	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);
	private static final String XMPP_RESOURCE_NAME = "SMIT";
	private static final String TAG = LogUtil.makeTag(XmppManager.class);
	
	private String xmppHost;
	private int xmppPort;	    
    private String username;
    private String password;
    
    private XMPPConnection connection;
    private Context context;
    private IMService.TaskSubmitter taskSubmitter;
    private IMService.TaskTracker taskTracker;
    private SharedPreferences sharedPrefs;
    private ConnectionListener connectionListener;
    private PacketListener notificationPacketListener;
    private PacketListener regPushPacketListener;
    
    private List<Runnable> taskList;
    private boolean running = false;
    private Future<?> futureTask;
    private Thread reconnection = null;
    int reconnectTime = 0;
    private Handler handler;
    
    //pubsub
    private Map<String,LeafNode> topicMap = null;
    
    public XmppManager(IMService imservice){
    	context = imservice;
    	taskSubmitter = imservice.getTaskSubmitter();
        taskTracker = imservice.getTaskTracker();
        sharedPrefs = imservice.getSharedPreferences();
        
        xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
        username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
        password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
        
        connectionListener = new PersistentConnectionListener(this);
        notificationPacketListener = new NotificationPacketListener(this);
        regPushPacketListener = new RegPushPacketListener(this);

        handler = new Handler();
        taskList = new ArrayList<Runnable>();
        
    	
        topicMap = new HashMap<String,LeafNode>();
    }
    public void connect() {
        Log.d(LOGTAG, "connect()...");
        submitLoginTask();
        //runTask();
    }
    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        if (getConnection() != null && 
            	getConnection().isConnected()) 
        {
            getConnection().disconnect();
        }
        stopReconnectionThread();
        terminatePersistentConnection();
    }
    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
        runTask();
    }    

    public void terminatePersistentConnection() {
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
    public void sendPacket(Packet packet){
    	if(this.isAuthenticated())
    	connection.sendPacket(packet);
    	else
    		broadcastStatus(Constants.PUSH_STATUS_LOGIN_FAIL);
    }
    public void startReconnectionThread() {
        /*synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
            }
        }*/    	
    	reconnection = new ReconnectionThread(this);
    	reconnection.start();
    	increaseReconnectTime();
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
    	return context;
    }
    public XMPPConnection getConnection() {
        return connection;
    }
    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
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
        return sharedPrefs.contains(Constants.XMPP_USERNAME)
                && sharedPrefs.contains(Constants.XMPP_PASSWORD);
    }
    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
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
                	broadcastStatus(Constants.PUSH_STATUS_CONNECTION_FAIL);
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
                final String newUsername = newRandomUUID();
                final String newPassword = newRandomUUID();

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
                                if (!response.getError().toString().contains(
                                        "409")) {
                                    Log.e(LOGTAG,
                                            "Unknown error while registering XMPP account! "
                                                    + response.getError().getCondition());
                                    broadcastStatus(Constants.PUSH_STATUS_REGISTER_FAIL);
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                                xmppManager.setUsername(newUsername);
                                xmppManager.setPassword(newPassword);
                                Log.d(LOGTAG, "username=" + newUsername);
                                Log.d(LOGTAG, "password=" + newPassword);

                                Editor editor = sharedPrefs.edit();
                                editor.putString(Constants.XMPP_USERNAME,
                                        newUsername);
                                editor.putString(Constants.XMPP_PASSWORD,
                                        newPassword);
                                editor.commit();
                                
                                Log.i(LOGTAG,"Account registered successfully");
                                broadcastStatus(Constants.PUSH_STATUS_REGISTER_SUC);                                
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
                            xmppManager.getUsername(),
                            xmppManager.getPassword(), XMPP_RESOURCE_NAME);
                    Log.d(LOGTAG, "Loggedn in successfully");
                    broadcastStatus(Constants.PUSH_STATUS_LOGIN_SUC); 
                    
                    initAfterLogin();

                } catch (XMPPException e) {
                    Log.e(LOGTAG, "LoginTask.run()... xmpp error");
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "+ e.getMessage());
                    broadcastStatus(Constants.PUSH_STATUS_LOGIN_FAIL);                    
                    
                    String INVALID_CREDENTIALS_ERROR_CODE = "401";
                    String errorMessage = e.getMessage();
                    if (errorMessage != null
                        && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                        xmppManager.reregisterAccount();
                        return;
                    }                    

                } catch (Exception e) {
                    Log.e(LOGTAG, "LoginTask.run()... other error");
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    broadcastStatus(Constants.PUSH_STATUS_LOGIN_FAIL);                    
                }

            } else {
                Log.i(LOGTAG, "Logged in already");
                broadcastStatus(Constants.PUSH_STATUS_LOGIN_SUC);                
            }
            xmppManager.runTask();
        }
    }
    
    public void broadcastStatus(String inf){
    	Intent intentSend = new Intent(Constants.ACTION_STATUS);
		intentSend.putExtra(Constants.PUSH_STATUS, inf);
		context.sendBroadcast(intentSend);
		
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
    	
    	if(!this.isAuthenticated()){
    		broadcastStatus(Constants.PUSH_STATUS_LOGIN_FAIL);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			broadcastStatus(Constants.PUSH_STATUS_SENDFAIL);
			// 发送失败
		}
    }
    private void initAfterLogin(){
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
        
        PacketFilter charFilter = new MessageTypeFilter(Message.Type.chat);
        ChatPacketListener chatListener = new ChatPacketListener(this);
        connection.addPacketListener(chatListener, charFilter);
        
        // TODO-ANDREW test need to delete
        NotFilter notFilter = new NotFilter(charFilter);
        connection.addPacketListener(new PacketListener(){
        	public void processPacket(Packet packet) {
        		Log.i(LOGTAG,TAG+"xmnl:"+packet.getXmlns()
        				+";From:"+packet.getFrom());
        		Log.i(LOGTAG,TAG+packet.toXML());
        	}
        },notFilter);                    
        List<String> topics = getTopicNode();
        listenTopic(topics);
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
/*Roster roster = connection.getRoster();
MyRosterListener rosterListener = new MyRosterListener();
roster.addRosterListener(rosterListener);

int n = roster.getGroupCount();
Collection<RosterGroup> crg = roster.getGroups();
Iterator iterator = crg.iterator();
while(iterator.hasNext()){
	RosterGroup rg = (RosterGroup)iterator.next();
	Log.i(LOGTAG,TAG+rg.getName()+"number:"+String.valueOf(rg.getEntryCount()));
}
Collection<RosterEntry> re = roster.getEntries();
iterator = re.iterator();
RosterGroup myGroup = null;
try {
	myGroup = roster.createGroup("a");
} catch (IllegalArgumentException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
//RosterEntry rosterEntry = new RosterEntry();

while(myGroup!=null && iterator.hasNext()){
	RosterEntry rg = (RosterEntry)iterator.next();
	myGroup.addEntry(rg);
	Log.i(LOGTAG,TAG+"user:"+rg.getUser()+";name:"
			+rg.getName());
}*/
