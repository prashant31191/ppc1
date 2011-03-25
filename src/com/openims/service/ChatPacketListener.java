package com.openims.service;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.openims.utility.LogUtil;

import android.content.Intent;
import android.util.Log;

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
            
            Intent intent1 = new Intent();
            intent1.setClassName("com.openims","com.openims.onlineHelper.ChatActivity");
            intent1.putExtra(Constants.MESSAGE_FROM, fromName);
            intent1.putExtra(Constants.MESSAGE_CONTENT, message.getBody());
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            xmppManager.getContext().startActivity(intent1);
        }
	}
}