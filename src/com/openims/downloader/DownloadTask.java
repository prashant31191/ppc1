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
import com.openims.view.PushContentListFragment;

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
	private int nTotalSize = 0; 	// �޷���ȡ�ܴ�С
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
        try {
                URL url = new URL(imageURL);
                File file = new File(fileName);                

                long startTime = System.currentTimeMillis();
                Log.d(TAG, LOG + "begining");
                Log.d(TAG, LOG + "url:" + url);
                Log.d(TAG, LOG + "file name:" + fileName);
                /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();

                /*
                 * Define InputStreams to read from the URLConnection.
                 */
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                
                /*
                 * Read bytes to the Buffer until there is nothing more to read(-1).
                 */
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                        nFinishSize++;
                        // ֪ͨUI����
                        if(nFinishSize%1024 == 0){
                        	mainThreadHandler.post(new Runnable(){  
                        		@Override
    							public void run() {
    								listener.finish(nFinishSize, nTotalSize);							
    							}
                            	
                            });
                        }							
                }

                /* Convert the Bytes read to a String. */
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
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
