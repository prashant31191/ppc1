package com.openims.service.chat;

import java.util.Date;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.util.Log;

import com.openims.model.chat.MessageRecord;
import com.openims.service.XmppManager;
import com.openims.utility.DataAccessException;
import com.openims.utility.LogUtil;

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
            String fromName = StringUtils.parseBareAddress(message.getFrom());
            Log.i(LOGTAG, TAG+"Got text [" + message.getBody() + "] from [" + fromName + "]");
            Log.i(LOGTAG, TAG+packet.toXML());
            
            /*Intent intent1 = new Intent();
            intent1.setClassName(PushServiceUtil.PACKAGE_NAME,
            					 PushServiceUtil.CHAT_ACTIVITY);
            intent1.putExtra(PushServiceUtil.MESSAGE_FROM, fromName);
            intent1.putExtra(PushServiceUtil.MESSAGE_CONTENT, message.getBody());
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            xmppManager.getContext().startActivity(intent1);*/
            
            // write to database
            Date date = new Date(System.currentTimeMillis());            
            try {
            	MessageRecord mr = new MessageRecord(xmppManager.getContext(),
            			MessageRecord.getMessageRecordTableName("555", "77"));
                mr.insert(message.getFrom(), message.getTo(), 
                		message.getBody(), date.toLocaleString());
                xmppManager.notifyNewMessage(77);
                mr.close();
            } catch(DataAccessException e) {
            	e.printStackTrace();
            }
            
            
            
        }
	}
}