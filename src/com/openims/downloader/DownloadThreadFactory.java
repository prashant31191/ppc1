package com.openims.downloader;

import com.openims.downloader.DownloadThread.DownloadThreadListener;

//TODO 工厂类，可以做线程池的东东
public class DownloadThreadFactory{
	public DownloadThread getThread(DownloadThreadListener listener){
		return new DownloadThread(listener);
	}
}