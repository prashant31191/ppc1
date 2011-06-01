package com.openims.service.notificationPacket;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

import com.openims.model.pushService.PushInfoManager;
import com.openims.service.IMService;
import com.openims.service.XmppManager;
import com.openims.utility.Constants;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;



public class RegPushPacketListener implements PacketListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationPacketListener.class);
    private static final String TAG = LogUtil.makeTag(RegPushPacketListener.class);

    private final XmppManager xmppManager;

    public RegPushPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {
        Log.d(LOGTAG, TAG+"processPacket()...");
        Log.d(LOGTAG, TAG+"packet.toXML()=" + packet.toXML());

        if (packet instanceof RegPushIQ) {
        	RegPushIQ regPushIQ = (RegPushIQ) packet;
        	String pushName = regPushIQ.getPushServiceName();
        	String pushID = regPushIQ.getPushRegID();
        	String type = PushServiceUtil.PUSH_TYPE_REG;
        	
        	PushInfoManager pushInfo = new PushInfoManager(xmppManager.getContext());
        	
        	StringBuilder packageNameBuilder = new StringBuilder();
        	StringBuilder classNameBuilder = new StringBuilder();
        	
        	if(regPushIQ.getRegOrUnreg() == false){
        		type = PushServiceUtil.PUSH_TYPE_UNREG;
        		pushInfo.getPushInfo(pushID, packageNameBuilder, classNameBuilder);
        		pushInfo.deletePushInfoInDb(pushName,xmppManager.getUsername());
        		// TODO-ANDREW it's not good enough. Unsafty
        	}else{
        		if( pushInfo.updatePushID(pushName,xmppManager.getUsername(), pushID)== false)
            		return; // have timeout
        		pushInfo.getPushInfo(pushID, packageNameBuilder, classNameBuilder);
        	}         	
        	IMService.sendRegisterBroadcast(packageNameBuilder.toString(), 
        			classNameBuilder.toString(), 
        			pushID, PushServiceUtil.PUSH_STATUS_SUC,type,xmppManager.getContext());
        }
    }
}