package com.openims.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

import android.os.Handler;
import android.util.Log;

import com.openims.utility.LogUtil;
import com.openims.view.pushContent.PushContentListFragment;

/**
 * This is not a real download task.
 * It just sleeps for some random time when it's launched. 
 * The idea is not to require a connection and not to eat it.
 * 
 */
public class DownloadTask implements Runnable {

	private static final String TAG = LogUtil
	.makeLogTag(PushContentListFragment.class);
	private static final String LOG = "Download:";
	
	private DownloadTaskListener listener;
	private Handler mainThreadHandler;
	private int nFinishSize = 0;
	private int nTotalSize = 0; 	// 无法获取总大小
	private String fileUrl;
	private String fileName;
	
	public DownloadTask(String fileUrl,String fileName,
			Handler mainThreadHandler,DownloadTaskListener listener) {
		this.fileUrl = fileUrl;
		this.fileName = fileName;
		this.mainThreadHandler = mainThreadHandler;
		this.listener = listener;
	}


	@Override
	public void run() {
		try {
			downloadFromUrl(fileUrl, fileName);
		} catch (Throwable t) {
			Log.e(TAG, "Error in DownloadTask", t);
		}
	}
	

	public void downloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
       
		FileOutputStream fos = null;
        try {
        	URL url = new URL(imageURL);
            File file = new File(fileName);
            fos = new FileOutputStream(file);
            
                long startTime = System.currentTimeMillis();
                Log.d(TAG, LOG + "url:" + url);
                Log.d(TAG, LOG + "file name:" + fileName);
                
                URLConnection ucon = url.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                
                byte[] data = new byte[1024]; 
                nFinishSize = 0;
                while( bis.read(data, 0, 1024) != -1){
                	fos.write(data, 0, 1024);
                	nFinishSize += 1024;
                	mainThreadHandler.post(new Runnable(){  
                		@Override
						public void run() {
							listener.finish(nFinishSize, nTotalSize);							
						}                    	
                    });
                }              
                
                Log.d(TAG, "download ready in"
                                + ((System.currentTimeMillis() - startTime) / 1000)
                                + " sec");
                mainThreadHandler.post(new Runnable(){  
            		@Override
					public void run() {
            			listener.finish();						
					}
                	
                });
        } catch (IOException e) {
                Log.d(TAG, LOG + "Error: " + e);
        } finally{
        	
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				Log.d(TAG, LOG + "Error: " + e);
				e.printStackTrace();
			}
        }
	}
	
	public void setListener(DownloadTaskListener listener) {
		this.listener = listener;
	}

	public DownloadTaskListener getListener() {
		return listener;
	}
	
	
	public void setMainThreadHandler(Handler mainThreadHandler) {
		this.mainThreadHandler = mainThreadHandler;
	}

	
}
