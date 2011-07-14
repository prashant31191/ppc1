/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openims.service.notificationPacket;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.smit.EasyLauncher.R;
import com.openims.model.pushService.PushContent;
import com.openims.model.pushService.PushContentDB;
import com.openims.model.pushService.PushInfoManager;
import com.openims.service.XmppManager;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.pushContent.PushActivity;

/**
 * 用于接收PUSH过来的消息
 * @author ANDREW CHAN (chenyzpower@gmail.com)
 *
 */
public class NotificationPacketListener implements PacketListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationPacketListener.class);

    private final XmppManager xmppManager;
    private final Context serviceContext;

    public NotificationPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
        serviceContext = xmppManager.getContext();
    }

    @Override
    public void processPacket(Packet packet) {
        Log.d(LOGTAG, "NotificationPacketListener.processPacket()...");
        Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());

        if (packet instanceof NotificationIQ) {
            NotificationIQ notification = (NotificationIQ) packet;

            if (notification.getChildElementXML().contains(
                    "smit:iq:notification")) {
                String ntId = notification.getId();
                String ntPushID = notification.getPushID();
                String ntTitle = notification.getTitle();
                String ntMessage = notification.getMessage();
                String ntUri = notification.getUri();
                String ticker = notification.getTicker();
                long t = Long.valueOf(notification.getTime());  
                String time = new Date(t).toLocaleString();

                if(ntPushID == null){
                	Log.e(LOGTAG, "PushID == null");
                	return;
                }
                if(ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_WARNING)){
                	// 对用户做震动提醒
                	warning(ntTitle,ntUri);
                }else if(ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_PENDINGINTENT)){
                	// 发送到title bar， 可以打开网页
                	if(ntUri.toLowerCase().startsWith("http://") == false){
                		ntUri = "http://" + ntUri;
                	}
                	pendingIntent(ntUri,ntTitle,ntMessage,ticker);
                	saveContent(ntPushID,ntTitle,ntUri,time);
                }else if(ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_TEXT)
                		|| ntPushID.equals(PushServiceUtil.DEFAULTID_STORY)){
                	// 保存到数据库
                	saveContent(ntPushID,ntTitle,ntMessage,time);
                	
                }else if(ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_URL)){                	
                	openUrl(ntUri);
                }else if(ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_VIDEO)
                		|| ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_PICTURE)
                		|| ntPushID.equalsIgnoreCase(PushServiceUtil.DEFAULTID_AUDIO)){
                	// 保存到数据库
                	saveContent(ntPushID,ntMessage,ntUri,time);
                }else{
                	sendPushInf(ntPushID,ntTitle,ntMessage,ntUri,ticker);
                }
            }
        }

    }
    private void sendPushInf(String pushID,String title,
    		String message,String uriString,String ticker){
    	PushInfoManager pushInfo = 
    		new PushInfoManager(xmppManager.getContext());    	
    	StringBuilder packageNameBuilder = new StringBuilder();
    	StringBuilder classNameBuilder = new StringBuilder();

    	boolean bool=pushInfo.getPushInfo(pushID, 
    			packageNameBuilder, classNameBuilder);
    	pushInfo.close();
    	if(bool==false){
    		return;
    	}
    	String packageName = packageNameBuilder.substring(0);
    	String className = classNameBuilder.substring(0);
		Intent intentSend = new Intent(PushServiceUtil.ACTION_RECEIVE);
		intentSend.setClassName(packageName, className);
		intentSend.putExtra(PushServiceUtil.NTFY_TITLE, title);
		intentSend.putExtra(PushServiceUtil.NTFY_TICKER, ticker);
		intentSend.putExtra(PushServiceUtil.NTFY_MESSAGE, message);
		intentSend.putExtra(PushServiceUtil.NTFY_URI, uriString);
		serviceContext.sendBroadcast(intentSend);
    	       	
    }
    /*
     *  shake to notify user
     */
    private void warning(String title,String uri){
    	Vibrator Vibrator=(Vibrator)serviceContext.getSystemService(
    			Service.VIBRATOR_SERVICE);
    	int nLong = 5000;
    	try {
    		nLong = Integer.parseInt(uri);
    	} catch(NumberFormatException nfe) {
    	   Log.e(LOGTAG,"Could not parse string to int" + nfe);
    	} 
    	Vibrator.vibrate(nLong);    	
    	Toast.makeText(serviceContext, title, Toast.LENGTH_SHORT).show();     	
    }
    /*
     * send pending intent to notify
     */
    private void pendingIntent(String uriString,String title,String text,String ticker){
    	    	  
    	NotificationManager mNotificationManager = 
    		(NotificationManager) serviceContext.
    		getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	int icon = R.drawable.icon;
    	CharSequence tickerText = ticker;
    	long when = System.currentTimeMillis();

    	Notification notification = new Notification(icon, tickerText, when);
    	
    	Context context = serviceContext.getApplicationContext();  
    	CharSequence contentTitle = title;  
    	CharSequence contentText = text;  
    	
    	Uri uri = Uri.parse(uriString);
    	Intent intent = new Intent(Intent.ACTION_VIEW,uri);
    	PendingIntent contentIntent = 
    		PendingIntent.getActivity(serviceContext, 
    		0, intent, 0);  
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);  
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    	mNotificationManager.notify(1, notification);
    }
    /**
     * 保存到数据库
     */
    private void saveContent(String type,String title,String message,String time){
    	PushContent push = new PushContent();
    	String read = serviceContext.getResources().getString(R.string.pushcontent_uread);
    	push.setStatus(read);
    	push.setType(type);
    	push.setContent(message);
    	push.setFlag(title);
    	push.setTime(time);
    	push.setSize("10K");
    	PushContentDB pushDB = new PushContentDB(serviceContext);
    	pushDB.insertItem(push);
    	pushDB.close();
    	// 测试一下，给标题发点东西
    	Intent intent = new Intent(PushServiceUtil.ACTION_UI_PUSHCONTENT);    	
    	serviceContext.sendBroadcast(intent);
    	notifyNewMessage(title);
    }
    /**
     * 提示有新的消息
     */
    private void notifyNewMessage(String titile){
    	NotificationManager mNotificationManager = 
    		(NotificationManager) serviceContext.
    		getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	int icon = R.drawable.icon;
    	CharSequence tickerText = "有更新啦！";
    	long when = System.currentTimeMillis();

    	Notification notification = new Notification(icon, tickerText, when);
    	notification.flags = Notification.FLAG_AUTO_CANCEL;
    	
    	Context context = serviceContext.getApplicationContext();  
    	CharSequence contentTitle = "有新的信息推送过来啦";  
    	CharSequence contentText = titile;  
    	
    	
    	Intent intent = new Intent(serviceContext,PushActivity.class);
    	PendingIntent contentIntent = 
    		PendingIntent.getActivity(serviceContext, 
    		0, intent, 0);  
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);  
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    	mNotificationManager.notify(1, notification);
    	
    }
    private void openUrl(String ntUri){    	
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(ntUri));
    	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	serviceContext.startActivity(i);
    }
    	
}
