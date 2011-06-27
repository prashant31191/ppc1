package com.openims.service.chat;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

import com.openims.model.chat.MessageRecord;
import com.openims.model.chat.RosterDataBase;
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
           
            Log.i(LOGTAG, TAG+packet.toXML()); 
            
            // write to database                 
            try {
            	String fromJid = message.getFrom().substring(0, 
            			message.getFrom().lastIndexOf("/") );
            	
            	MessageRecord mr = new MessageRecord(xmppManager.getContext(),
            			MessageRecord.getMessageRecordTableName(
            			xmppManager.getUserNameWithHostName(), fromJid));
            	
                int id = mr.insert(fromJid, xmppManager.getUserNameWithHostName(), 
                		message.getBody(), 
                		String.valueOf(System.currentTimeMillis()));
                mr.close();
                RosterDataBase roster = new RosterDataBase(xmppManager.getContext(),
                		xmppManager.getUserNameWithHostName());
                roster.updateUnReadMsg(fromJid, 1, id);
                roster.close();
                xmppManager.notifyNewMessage(fromJid);
                
            } catch(DataAccessException e) {
            	e.printStackTrace();
            }            
        }
	}
}