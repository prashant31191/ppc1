package com.openims.service.notificationPacket;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

public class UserQueryIQListener implements PacketListener {

	@Override	
	public void processPacket(Packet packet) {
		System.out.print("packet.toXML()=" + packet.toXML());
		
		if (packet instanceof UserQueryIQ) {
			System.out.println("user query iq return");	
			
			UserQueryIQ iq = (UserQueryIQ)packet;
			
		}
	}

}
