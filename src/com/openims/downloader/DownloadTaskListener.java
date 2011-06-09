package com.openims.downloader;

public interface DownloadTaskListener{
	public void finish(int nFinishSize,int nTotalSize);
	public void finish();
}