package com.smit.rssreader.iqrequest;

import org.jivesoftware.smack.packet.IQ;

public class SubUnsubIQ extends IQ{
    private String xmlChild;
	
	public SubUnsubIQ(String xmlChild){
		this.xmlChild = xmlChild;
	}

	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		return xmlChild;
	}
	

}
