package com.smit.rssreader;

import com.smit.EasyLauncher.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CategoryEditDialog extends AlertDialog{
    
	private Context context;
	private String category ;
	private RSSOpenHelper roh ;
	private String description = null ;
	private CustomerDialogListener listener ;
	
	private EditText field_cate;
	private EditText field_des;
	
	protected CategoryEditDialog(Context context,String cate,CustomerDialogListener lis) {
		super(context);
		this.context = context ;
		this.category = cate ;
		this.listener = lis ;
		roh = new RSSOpenHelper(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LayoutInflater categoryInflater = LayoutInflater.from(context);
		View categoryLayout = categoryInflater.inflate(
				R.layout.rss_catedory_edit_dialog, null);
		setView(categoryLayout);
		super.onCreate(savedInstanceState); // 逻辑顺序注定只能放在这里
		
		field_cate = (EditText)findViewById(R.id.editcategory);
		field_des = (EditText)findViewById(R.id.editdescription);
		initiaData();
		
		Button btn_modify = (Button)findViewById(R.id.editcanle);
		btn_modify.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		Button btn_update = (Button)findViewById(R.id.modify);
		btn_update.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str1 = field_cate.getText().toString().trim();
				String str2 = field_des.getText().toString().trim();
				boolean b1 = str1.equals(category);
				boolean b2 = str2.equals(description);
				if(!b1&& !b2){
					roh.updateRssCate(str1, str2, category);
					roh.updateRssInfo(str1, category);
				}
				if(!b1 && b2){
					roh.updateRssCate(str1, str2, category);
					roh.updateRssInfo(str1, category);
				}
				if(b1&&!b2){
					roh.updateRssDes(str2, category);
				}
//				Intent i1 = new Intent(RssReaderConstant.EDIT_BROADCAST);
//				context.sendBroadcast(i1);
				
				listener.onOkClick();
				field_cate.setText(" ");
				field_des.setText(" ");
				dismiss();
			}
			
		});
		
	}
	
	//设置编辑对话框的初始数据
	private void initiaData(){
		Cursor c = roh.queryDes(category);
		if(c.moveToFirst()){
			int desIndex = c.getColumnIndex(RSSOpenHelper.RSS_DESCRIPTION);
			description =c.getString(desIndex);
			field_cate.setText(category);
			field_des.setText(description);
		}
		c.close();
	}
	
}
