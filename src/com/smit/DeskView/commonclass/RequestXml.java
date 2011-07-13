package com.smit.DeskView.commonclass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//提交一个个测试项交互线程   把取的状态传给主线程
public  class RequestXml extends Thread {

	public 		URL 			mUrl;
	private 	Handler 		mMainHandle;
	private     int 			msg;
	public 		String 	        getString =null;
	private     Timer 			mTimer;
	private     String          postString=null;
	private     final int       TimeOut=20*1000;
	private     String          Tag="RequestXml";


	
	public void sendData() {  
		String tag ="HTTPSend:sendData";  
		int statsID = 101;  
		StringBuffer sb = new StringBuffer();  
	
		try {
			

			Log.e(Tag, "2222222222222222222222222222");
			
			HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);//忽略缓存
			connection.setReadTimeout(TimeOut);
			connection.setConnectTimeout(TimeOut);
			int responseCode = connection.getResponseCode();
			
			
			Log.e(tag, "Response code :" + connection.getResponseCode());
			if (HttpURLConnection.HTTP_OK == responseCode)
			{
				  //当正确响应时处理数据
		           String readLine;
		           BufferedReader responseReader;
		           //处理响应流，必须与服务器响应流输出的编码一致
		           responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		           while ((readLine = responseReader.readLine()) != null) {
		               sb.append(readLine).append("\n");
		            }
		            responseReader.close();
		            
			}else {
				
			}		
			connection.disconnect();
			
		    getString=null;
	        getString=sb.toString();           
			if (mTimer!=null) {      //取到数据马上返回
				mTimer.cancel();
				mTimer=null;
			}
			
			Message m =new Message();
			m.what=msg;
			m.obj=getString;				
			mMainHandle.sendMessage(m);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Tag, e.toString());
		}
	}
	
	public RequestXml(URL url, Handler Handle, int message, String str)
	{		
		mMainHandle=Handle;
		mUrl=url;
		msg=message;
		postString=str;
		stopThread();
		mTimer= new Timer();
		TimerTask timeTask=new TimerTask() {		
				@Override
				public void run() {
					Message m =new Message();
					m.what=msg; 
					m.obj=getString;
/*					m.arg1=getUpItemStatus();
					m.obj=postString;*/
					mMainHandle.sendMessage(m);
				}
			};
		mTimer.schedule(timeTask,TimeOut);//通知主线程数据已取完 		
	
	}
	
	/*public int getUpItemStatus(){
		String retStr;
		int nRet=ParseSeverData.ERROR_CODE_UP_ITEM_INFO;

		retStr = getDate();
		if (retStr != null && retStr.length() > 0) {
			nRet = ParseSeverData.ParseUpLogRet(retStr);
			if (nRet == ParseSeverData.ERROR_CODE_NO_DATA
					|| nRet == ParseSeverData.ERROR_CODE_UP_ITEM_INFO) {
				
				Log.e(Tag, "===UP_ITEM_INFO item_info error===");
			} else {

				Log.e(Tag, "===UP_ITEM_INFO sucess===");
			}
		} else {
			Log.e(Tag, "===UP_ITEM_INFO No Data===");
		}
		
		return nRet;
	}*/
	
	
	@Override
	public void run() {
		sendData();
		super.run();
	}
	
	public void stopThread(){
		if (mTimer!=null) {
			mTimer.cancel();
			mTimer=null;
		}
	}   	
	//得到数据
	public  String getDate()
	{
		return getString;
	}
}