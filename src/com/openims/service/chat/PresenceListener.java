package com.openims.service.chat;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import com.openims.model.chat.RosterDataBase;
import com.openims.service.XmppManager;

public class PresenceListener implements PacketListener {

	private final XmppManager xmppManager;
	
    public PresenceListener(XmppManager xmpp){
    	xmppManager = xmpp;
    }
	@Override
	public void processPacket(Packet packet) {

		Presence presence = (Presence)packet;
		String from = presence.getFrom();
		from = from.substring(0, from.lastIndexOf("/"));
		RosterDataBase roster = new RosterDataBase(xmppManager.getContext(),
				xmppManager.getUserNameWithHostName());
		if (presence.getType() == Presence.Type.available){
			roster.updatePresence(from, Presence.Type.available.name());
		} 
		else if (presence.getType() == Presence.Type.unavailable) {
			roster.updatePresence(from, Presence.Type.unavailable.name());
		}
		roster.close();
		xmppManager.notifyRosterUpdated(from);
	}

}
