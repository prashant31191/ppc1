/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openims.service;

import com.openims.utility.LogUtil;

import android.util.Log;

/** 
 * A thread class for recennecting the server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ReconnectionThread extends Thread {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ReconnectionThread.class);
    private static final String TAG = "ReconnectionThread";

    private final XmppManager xmppManager;

    private int waiting;

    ReconnectionThread(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
        this.waiting = 0;
    }
    ReconnectionThread(XmppManager xmppManager,int wait) {
        this.xmppManager = xmppManager;
        this.waiting = wait;
    }
    
    public void run() {
        try {
        	if(!ConnectivityReceiver.isNetAvailable(xmppManager.getContext())){
        		xmppManager.broadcastStatus(Constants.PUSH_STATUS_NONETWORK); 
        	}
            while (!xmppManager.isAuthenticated() && 
            	   !this.isInterrupted() &&
            	   ConnectivityReceiver.isNetAvailable(xmppManager.getContext())) {
                
            	Log.d(LOGTAG,TAG+"开始连接");
                xmppManager.connect();
                waiting++;
                Log.e(LOGTAG, TAG + waiting() + " seconds 后重新连接");
                Thread.sleep((long) waiting() * 1000L);
                
            }
        } catch (final InterruptedException e) {
            xmppManager.getHandler().post(new Runnable() {
                public void run() {
                    xmppManager.getConnectionListener().reconnectionFailed(e);
                }
            });
        }
    }
    /*
     * 设置重新连接的时间段
     */
    private int waiting() {    	
    	   
    	//return 5;
        if (waiting > 20) {
            return 300;
        }
        if (waiting > 30) {
            return 600;
        }
        return waiting <= 10 ? waiting : 30; 
        
    }
}
