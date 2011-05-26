package com.openims.utility;

import java.io.File;

public class FileOperation {
	
	public static String getFileName(String filePathWithName){
		String name;
		int n = filePathWithName.lastIndexOf("/");
		name = filePathWithName.substring(n+1);
		return name;
	}
	
	public static boolean makedir(String path){
		File file = new File(path);
		return file.mkdirs();		
	}

}
