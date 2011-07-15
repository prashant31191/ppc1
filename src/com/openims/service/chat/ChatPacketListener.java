package com.openims.service.chat;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.openims.model.SharedData;
import com.openims.model.chat.MessageRecord;
import com.openims.model.chat.RosterDataBase;
import com.openims.service.XmppManager;
import com.openims.utility.DataAccessException;
import com.openims.utility.LogUtil;
import com.openims.view.chat.MultiChatActivity;
import com.smit.EasyLauncher.R;

public class ChatPacketListener implements PacketListener{
	
	private static final String LOGTAG = LogUtil.makeLogTag(ChatPacketListener.class);
    private static final String TAG = LogUtil.makeTag(ChatPacketListener.class);
    
    private final XmppManager xmppManager;
    public ChatPacketListener(XmppManager xmpp){
    	xmppManager = xmpp;
    }
	public void processPacket(Packet packet){
		Message message = (Message) packet;
        if (message.getBody() != null) {
           
            Log.i(LOGTAG, TAG+packet.toXML()); 
            
            // write to database                 
            try {
            	String fromJid = message.getFrom().substring(0, 
            			message.getFrom().lastIndexOf("/") );
            	
            	MessageRecord mr = new MessageRecord(xmppManager.getContext(),
            			MessageRecord.getMessageRecordTableName(
            			xmppManager.getUserNameWithHostName(), fromJid));
            	
                long id = mr.insert(fromJid, xmppManager.getUserNameWithHostName(), 
                		message.getBody(), 
                		String.valueOf(System.currentTimeMillis()));
                mr.close();
                RosterDataBase roster = new RosterDataBase(xmppManager.getContext(),
                		xmppManager.getUserNameWithHostName());
                roster.updateUnReadMsg(fromJid, 1, id);
                roster.close();
                xmppManager.notifyNewMessage(fromJid);
                
                // send notification to task bar
                if(SharedData.getInstance().isShowNewMessageNotify()){
                	NotificationManager mNotificationManager = 
                		(NotificationManager) xmppManager.getContext().
                		getSystemService(Context.NOTIFICATION_SERVICE);
                	
                	Intent intent = new Intent(xmppManager.getContext(),
                    		MultiChatActivity.class);
                	intent.putExtra(MultiChatActivity.ACCOUNT_JID, fromJid);
                	PendingIntent pendingIntent = PendingIntent.getActivity(
                			xmppManager.getContext(),0, intent, 0);  
                	
                	Notification notification = new Notification(R.drawable.icon,
                			xmppManager.getContext().getResources()
                			.getString(R.string.im_new_message), 
                			System.currentTimeMillis());
                	notification.flags = Notification.FLAG_AUTO_CANCEL;                	
                	
                	notification.setLatestEventInfo(xmppManager.getContext(),
                			fromJid, message.getBody(), pendingIntent);
                	mNotificationManager.notify(1, notification);
                }
                
            } catch(DataAccessException e) {
            	e.printStackTrace();
            }            
        }
	}
}