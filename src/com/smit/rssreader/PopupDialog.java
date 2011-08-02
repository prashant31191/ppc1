package com.smit.rssreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PopupDialog {

	private String diaTitle;
	private String diaMessage;
	private Context context;
	private AlertDialog.Builder builder; // = new AlertDialog.Builder(arg0);

	public PopupDialog(String title, String message, Context context) {
		this.diaTitle = title;
		this.diaMessage = message;
		this.context = context;
		builder = new AlertDialog.Builder(context);
	}

	public void show() {
		builder.setTitle(diaTitle);
		builder.setMessage(diaMessage);
		builder.setPositiveButton("È·¶¨", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				// stub
			}
		});
		builder.create();
		builder.show();
	}
	
}
