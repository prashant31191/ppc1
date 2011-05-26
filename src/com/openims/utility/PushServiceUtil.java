package com.openims.utility;

public class PushServiceUtil{
	public static final String PACKAGE_NAME = "com.openims";
    // the action for service
    public final static String ACTION_IMSERVICE = "com.openims.service.IMService";
    public static final String ACTION_SERVICE_STATUS = "com.openims.service.IMService.STATUS";
    public static final String ACTION_SERVICE_REGISTER = "com.openims.service.IMService.REGISTER";
    public static final String ACTION_SERVICE_MESSAGE = "com.openims.service.IMService.MESSAGE";
    public static final String ACTION_SERVICE_PUBSUB = "com.openims.service.IMService.PUBSUB";
    
    // the action for broadcast
    public static final String ACTION_REGISTRATION = "com.openims.pushService.REGISTRATION";
    public static final String ACTION_RECEIVE = "com.openims.pushService.RECEIVE";
    public static final String ACTION_STATUS = "com.openims.CONNECT_STATUS";
    public static final String ACTION_UI_PUSHCONTENT = "com.openims.ui.pushContent";
    
    public static final int PUSH_TIMEOUT_TIME = 50000; // as long as possible
       
    // push register intent parameter
    public static final String PUSH_USER = "user";
    public static final String PUSH_PACKAGENAME = "callBackPackageName";
    public static final String PUSH_CLASSNAME = "callBackClassName";
    public static final String PUSH_NAME = "pushName";
    public static final String PUSH_ID = "PushServiceID";
    // push register status
    public static final String PUSH_STATUS = "PushServiceStatus";
    public static final String PUSH_STATUS_FAIL = "Fail";
    public static final String PUSH_STATUS_UNCONNECT = "unconnected";
    public static final String PUSH_STATUS_HAVEREGISTER = "haveRegister";
    public static final String PUSH_STATUS_NOTREGISTER = "haveNotRegister";
    public static final String PUSH_STATUS_SUC = "success";
    public static final String PUSH_STATUS_CONNECTION_FAIL = "CONNECTION_FAIL";
    public static final String PUSH_STATUS_LOGIN_FAIL = "LOGIN_FAIL";
    public static final String PUSH_STATUS_REGISTER_FAIL = "REGISTER_FAIL";
    public static final String PUSH_STATUS_LOGIN_SUC = "LOGIN_SUC";
    public static final String PUSH_STATUS_REGISTER_SUC = "REGISTER_SUC";
    
    // connection status
    public static final String PUSH_STATUS_NONETWORK = "NO_NETWORK";
    public static final String PUSH_STATUS_SENDFAIL = "SENDFAIL";
    
    
    public static final String PUSH_TYPE = "pushType";
    public static final String PUSH_TYPE_REG = "register";
    public static final String PUSH_TYPE_UNREG = "unregister";   
   
    
    // the parameter for chat intent
    public static final String CHAT_ACTIVITY = "com.openims.view.onlineHelper.ChatActivity";
    public static final String MESSAGE_TYPE = "message_chat";
    public static final String MESSAGE_TOWHOS = "message_towhos";
    public static final String MESSAGE_FROM = "message_from";
    public static final String MESSAGE_CONTENT = "message_content";
    
    // the parameter for notification
    public static final String NTFY_TITLE = "notifyTitle";
    public static final String NTFY_TICKER = "notifyTicker";
    public static final String NTFY_MESSAGE = "notifyMessage";
    public static final String NTFY_URI = "notifyUri";    
    
    
    // some default push id for special purpose
    public static final String DEFAULTID_WARNING = "WARNING";
    public static final String DEFAULTID_PENDINGINTENT = "PENDINGINTENT";
    public static final String DEFAULTID_TEXT = "TEXT";
    public static final String DEFAULTID_URL = "URL";
    public static final String DEFAULTID_PICTURE = "PICTURE";
    public static final String DEFAULTID_VIDEO = "VIDEO";
    public static final String DEFAULTID_STORY = "STORY";
    
    
    // for preferences file
    public static final String SHARED_PREFERENCE_NAME = "client_preferences";
    public static final String XMPP_HOST = "XMPP_HOST";
    public static final String XMPP_PORT = "XMPP_PORT";
    public static final String XMPP_USERNAME = "XMPP_USERNAME";
    public static final String XMPP_PASSWORD = "XMPP_PASSWORD";
    public static final String XMPP_RESOURCE = "XMPP_RESOURCE";
    // PREFERENCE KEYS
    public static final String API_KEY = "API_KEY";
    public static final String VERSION = "VERSION";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";
    
    public static final String PUBSUB_ADDRES = "pubsub";
}