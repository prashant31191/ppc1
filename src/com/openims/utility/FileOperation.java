package com.openims.utility;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.util.Log;

public class FileOperation {
	
	private static final String TAG = LogUtil
    			.makeLogTag(FileOperation.class);
	private static final String PRE = "FileOperation--";

	public static String getFileName(String filePathWithName){
		String name;
		int n = filePathWithName.lastIndexOf("/");
		name = filePathWithName.substring(n+1);
		try {
			name = URLDecoder.decode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	public static boolean makedir(String path){
		File file = new File(path);
		boolean b = file.mkdirs();
		if(b == false){
			Log.e(TAG, PRE + "mkdirs fail:" + path);
		}
		return b;
	}

}
