
package com.openims.service.notificationPacket;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.openims.model.pushService.PushInfoManager;
import com.openims.utility.LogUtil;

public class RegPushProvider implements IQProvider{

	private static final String LOGTAG = LogUtil.makeLogTag(RegPushProvider.class);
	private static final String TAG = LogUtil.makeTag(RegPushProvider.class);
	
	public RegPushProvider(){
		
	}
	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {

		RegPushIQ regPushPacket = new RegPushIQ();
		
		for (boolean done = false; !done;) {
            int eventType = parser.next();
            if (eventType == 2) {
            	Log.i(LOGTAG,TAG+parser.getName());
                if ("userName".equals(parser.getName())) {
                	regPushPacket.setUserName(parser.nextText());
                }
                if ("pushServiceName".equals(parser.getName())) {
                	regPushPacket.setPushServiceName(parser.nextText());
                }
                if ("regPush".equals(parser.getName())) {
                	String string = parser.nextText();
                	if(string.equals("true")){
                		regPushPacket.setRegOrUnreg(true);
                	}else{
                		regPushPacket.setRegOrUnreg(false);
                	}                	
                }
                if ("pushID".equals(parser.getName())) {
                	regPushPacket.setPushRegID(parser.nextText());
                }                
            } else if (eventType == 3
                    && "openims".equals(parser.getName())) {
                done = true;
            }
        }
		
		return regPushPacket;
	}
	
}