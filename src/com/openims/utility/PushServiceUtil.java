package com.openims.utility;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class PushServiceUtil{
	//public static final String PACKAGE_NAME = "com.openims";
	public static final String SERVER_NAME = "@smit";
    // the action for service
    //public final static String ACTION_IMSERVICE = "com.openims.service.IMService";
    public static final String ACTION_SERVICE_STATUS = "com.openims.service.IMService.STATUS";
    public static final String ACTION_SERVICE_REGISTER = "com.openims.service.IMService.REGISTER";
    public static final String ACTION_SERVICE_MESSAGE = "com.openims.service.IMService.MESSAGE";
    public static final String ACTION_SERVICE_PUBSUB = "com.openims.service.IMService.PUBSUB";
    public static final String ACTION_SERVICE_CONNECT = "com.openims.service.IMService.CONNECT";
    
    // the action for broadcast
    public static final String ACTION_REGISTRATION = "com.openims.pushService.REGISTRATION";
    public static final String ACTION_RECEIVE = "com.openims.pushService.RECEIVE";
    public static final String ACTION_STATUS = "com.openims.CONNECT_STATUS";
    public static final String ACTION_UI_PUSHCONTENT = "com.openims.ui.pushContent";
    
    public static final int PUSH_TIMEOUT_TIME = 5000; // as long as possible
       
    // push register intent parameter
    public static final String PUSH_DEVELOPER = "developer";
    public static final String PUSH_PACKAGENAME = "callBackPackageName";
    public static final String PUSH_CLASSNAME = "callBackClassName";
    public static final String PUSH_NAME_KEY = "pushNameKey";
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
    public static final String DEFAULTID_AUDIO = "AUDIO";
    public static final String DEFAULTID_STORY = "STORY";
    
    
    // for preferences file
    public static final String SHARED_PREFERENCE_NAME = "client_preferences";
    public static final String XMPP_HOST = "XMPP_HOST";
    public static final String XMPP_PORT = "XMPP_PORT";
    public static final String XMPP_USERNAME = "XMPP_USERNAME";
    public static final String XMPP_PASSWORD = "XMPP_PASSWORD";
    public static final String XMPP_RESOURCE = "XMPP_RESOURCE";
    public static final String XMPP_HOSTNAME = "XMPP_HOSTNAME";
    // PREFERENCE KEYS
    public static final String API_KEY = "API_KEY";
    public static final String VERSION = "VERSION";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";
    
    public static final String PUBSUB_ADDRES = "pubsub";
    
    public static final float HORIZONTAL_MARGIN = 0.35f;
    public static final float VERTICAL_MARGIN = 0.2f;
    
    // service communicate with activity
    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;
    
    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;
    
    /**
     * request unread message number
     */
    public static final int MSG_UNREAD_NUMBBER = 3;
    
    public static final int MSG_NEW_MESSAGE = 4;
    
    public static final int MSG_ROSTER_UPDATED = 5;
    
    public static final int MSG_ROSTER_DELETE = 6;
    
    public static final int MSG_REQUEST_VCARD = 7; 
    
    
    public static ColorFilter GREY_COLOR_FILTER = new ColorMatrixColorFilter( new ColorMatrix(new float[]{0.5f,0.5f,0.5f,0,0, 
            0.5f,0.5f,0.5f,0,0, 
            0.5f,0.5f,0.5f,0,0, 
            0,0,0,1,0,0, 
            0,0,0,0,1,0 
            })); 
}
