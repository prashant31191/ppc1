package com.openims.service;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.util.Log;

import com.openims.utility.LogUtil;

/**
 * Receiver files form other side.
 * 
 * @author chenyzpower@gmail.com
 *
 */
public class FileReceiver implements FileTransferListener {

	private static final String LOGTAG = LogUtil.makeLogTag(FileReceiver.class);
    private static final String TAG = LogUtil.makeTag(FileReceiver.class);
    
    private final XmppManager xmppManager;
    public FileReceiver(XmppManager xmpp){
    	xmppManager = xmpp;
    }
	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		Log.i(LOGTAG, TAG+"receiver file from "+request.getRequestor());
		File file = new File("/sdcard/"+request.getFileName());  
		IncomingFileTransfer transfer = request.accept();  
		try {   
			transfer.recieveFile(file);  
		} catch (XMPPException e) {   
			e.printStackTrace();  
		}
	}

}
