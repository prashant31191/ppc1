package com.openims.service.notificationPacket;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

/**
 * ����������ѯ�Լ��û������»�����Ϣ���������е�resource 
 * @author Andrew
 *
 */
public class UserQueryIQ extends IQ {

	private String userAccount;		// ��ѯ���û��˺�
	private String deviceName;		// �����ύ�豸������
	private String deviceId;		// �����ύ�豸��Ψһid
	private String opCode;			// ������save or query or queryOfflinePush

	private String resource;
	
	// �����ǽ��յ�������
	private List<String> resources = new ArrayList<String>(); // ���ص�
	private List<String> deviceNames = new ArrayList<String>();
	private List<String> deviceIds = new ArrayList<String>();
	
	private String status;			// ���ص�״̬
	
	public static final String OPCODE_SAVE = "save";
	public static final String OPCODE_QUERY = "query";
	public static final String OPCODE_QUERY_OFFLINE_PUSH = "queryOfflinePush";

	
	public static String getElementName(){
    	return "openims";
    }
    public static String getNamespace(){
    	return "smit:iq:queryUserAccountResource";
    }
    
	@Override
	public String getChildElementXML() {		
		StringBuilder buf = new StringBuilder();
	       buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
	       buf.append(getXML());
	       buf.append("</").append(getElementName()).append(">");
     return buf.toString();
	}
	
	private String getXML(){
		StringBuilder buf = new StringBuilder();
		if(userAccount != null)
		buf.append("<userAccount>").append(userAccount).append("</userAccount>");
		
		if(deviceName != null)
		buf.append("<deviceName>").append(deviceName).append("</deviceName>");
		
		if(deviceId != null)
		buf.append("<deviceId>").append(deviceId).append("</deviceId>");
		
		if(resource != null)
			buf.append("<resource>").append(resource).append("</resource>");
		
		if(opCode != null)
		buf.append("<opCode>").append(opCode).append("</opCode>");
		return buf.toString();
	}
	
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOpCode() {
		return opCode;
	}
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public void setOpCodeSave(){
		this.opCode = OPCODE_SAVE;
	}

	public void setOpCodeQuery(){
		this.opCode = OPCODE_QUERY;
	}
	public void setOpCodeQueryOfflinePush(){
		this.opCode = OPCODE_QUERY_OFFLINE_PUSH;
	}
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	public List<String> getDeviceNames() {
		return deviceNames;
	}
	public void setDeviceNames(List<String> deviceNames) {
		this.deviceNames = deviceNames;
	}
	public List<String> getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}

	
}
