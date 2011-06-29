package com.openims.service.notificationPacket;


import org.jivesoftware.smack.packet.IQ;

public class RegPushIQ extends IQ{
	
	boolean bRegPush = true;	// false for unregister push
	String userName = null;
	String pushServiceName = null;
	String pushRegID = null;
	
	public RegPushIQ( ){		
	}
	
	public void setUserName(String name){
		userName = name;
	}
	public String getUserName(){
		return userName;
	}
	public void setPushServiceName(String name){
		pushServiceName = name;
	}
	public String getPushServiceName(){
		return pushServiceName;
	}
	public void setPushRegID(String id){
		pushRegID = id;
	}
	public void setRegOrUnreg(boolean bReg){
		bRegPush = bReg;
	}
	public boolean getRegOrUnreg(){
		return bRegPush;
	}
	
	public String getPushRegID(){
		return pushRegID;
	}
	/*
	 * 
	 * @chenyzpower@gmail.com
	 */
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		
        buf.append("<openims xmlns=\"smit:iq:registerPushService\">");
        buf.append("<userName>").append(userName).append("</userName>");
        buf.append("<pushServiceName>").append(pushServiceName).append("</pushServiceName>");               
        buf.append("<regPush>").append(bRegPush).append("</regPush>");

        buf.append("</openims>");
        return buf.toString();
	}
	
}