package com.openims.downloader;

import com.openims.downloader.DownloadThread.DownloadThreadListener;

//TODO �����࣬�������̳߳صĶ���
public class DownloadThreadFactory{
	public DownloadThread getThread(DownloadThreadListener listener){
		return new DownloadThread(listener);
	}
}