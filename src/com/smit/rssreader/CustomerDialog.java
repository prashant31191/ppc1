package com.smit.rssreader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.smit.EasyLauncher.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CustomerDialog extends AlertDialog {
	private Context context = null;
	private RSSOpenHelper rssOpenHelper = null;
	private CustomerDialogListener listener = null;
	private int id = 0;
	private String[] category = null;
	private String selectedCategory = null;
	private SpinnerBaseAdapter ba = new SpinnerBaseAdapter();
	private CustomerDialogListener sLitener = new SpinnerListener();
	private InteractiveServer inter;
	ProgressDialog pDialog = null;
	private DialogReciver diaReciver ;

	private EditText field_category;
	private EditText field_description;
	private Button btn_add;
	private Button btn_cancle;
	private EditText field_url;
	private Spinner spinner_choose;
	private Button rssAdd;
	private Button rssCancle;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			FeedCategory fc = (FeedCategory) msg.obj;
			RSSFeed feed = fc.getRssFeed();
			String url = fc.getRssUrl();
			if (feed == null) {
				new PopupDialog("温馨提示", "订阅失败,请检查网络和RSS地址的合法性！", context).show();
				pDialog.dismiss();
			} else {
				String channelTitle = feed.getTitle();
				int count = feed.getItemCount();
				RSSItem item = null;
				for (int i = 0; i < count; i++) {
					item = feed.getItem(i);
					String itemTitle = item.getTitle();
					String itemDes = item.getDescription();
					String itemPub = item.getPubDate();
					String itemLink = item.getLink();
					rssOpenHelper.insertRssInfo(selectedCategory, url,
							channelTitle, itemTitle, itemDes, itemPub,
							itemLink, 0,0);
				}
				field_url.setText(" ");
				dismiss();
				pDialog.dismiss();
				listener.onOkClick();
			}
			super.handleMessage(msg);
		}

	};

	public CustomerDialog(Context context, int id,
			CustomerDialogListener listener, InteractiveServer inter) {
		super(context);
		this.context = context;
		this.id = id;
		this.listener = listener;
		this.inter = inter;
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.smit.rssreader.action.IQ_YES_BROADCAST");
		filter.addAction("com.smit.rssreader.action.IQ_NO_BROADCAST");
		diaReciver = new DialogReciver();
		context.registerReceiver(diaReciver, filter);
		
		rssOpenHelper = new RSSOpenHelper(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (id == 1) {
			LayoutInflater categoryInflater = LayoutInflater.from(context);
			View categoryLayout = categoryInflater.inflate(
					R.layout.rss_add_category_dialog, null);
			setView(categoryLayout);
			super.onCreate(savedInstanceState); // 逻辑顺序注定只能放在这里

			field_category = (EditText) categoryLayout
					.findViewById(R.id.txtcategory);
			field_description = (EditText) categoryLayout
					.findViewById(R.id.txtdescription);
			btn_add = (Button) categoryLayout.findViewById(R.id.add);
			btn_cancle = (Button) categoryLayout.findViewById(R.id.canle);

			btn_cancle.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});

			btn_add.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String cate = field_category.getText().toString().trim();
					String description = field_description.getText().toString()
							.trim();
					if (cate.equals("")) {
						new PopupDialog("错误提示：", "RSS类别不能为空！", context).show();
					} else if (isSameOfCategory(cate)) {
						new PopupDialog("温馨提示：", "该类别已存在！", context).show();
					} else {
						rssOpenHelper.insertCategory(cate, description);
						Cursor c1 = rssOpenHelper.query();
						if (c1.moveToFirst()) {
							int d = c1.getCount();
							Log.i("----", "------" + d);
						}
					}
					listener.onOkClick();
					sLitener.onOkClick();
					field_category.setText(" ");
					field_description.setText(" ");

					dismiss();
				}
			});

		}
		if (id == 2) {
			LayoutInflater rssInflater = LayoutInflater.from(context);
			View rssLayout = rssInflater.inflate(R.layout.rss_add_rss_dialog, null);
			setView(rssLayout);
			super.onCreate(savedInstanceState); // 逻辑顺序注定只能放在这里

			field_url = (EditText) rssLayout.findViewById(R.id.rssurl);
			spinner_choose = (Spinner) rssLayout
					.findViewById(R.id.spinnerchoose);
			rssAdd = (Button) rssLayout.findViewById(R.id.rssadd);
			rssCancle = (Button) rssLayout.findViewById(R.id.rsscanle);

			// 初始化spinner
			getDataSource();
			spinner_choose.setAdapter(ba);
			spinner_choose
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							LinearLayout ll = (LinearLayout) view;
							TextView text = (TextView) ll.getChildAt(0);
							selectedCategory = text.getText().toString();
							// selectedId = position + 1;
							Log.i("所选的字段", "------" + selectedCategory);

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}
					});

			// 取消按钮设置监听器
			rssCancle.setOnClickListener(new Button.OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					field_url.setText(" ");
					dismiss();
				}
			});

			// 添加按钮设置监听器
			rssAdd.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final String url = field_url.getText().toString().trim();
					List<URL> urlList = new ArrayList<URL>();
					// 验证RSS Feed地址的合法性
					if (url.equals("")) {
						new PopupDialog("错误提示:", "RSS地址不能为空", context).show();
					} else if (isSameOfRssUrl(selectedCategory, url)) {
						new PopupDialog("温馨提示:", "所选RSS类别中已经存在正在添加的RSS地址！",
								context).show();
					} else if((urlList = initUrlList(url))!=null){
						pDialog = new ProgressDialog(context);
						pDialog.setTitle("正在处理");
						pDialog.setMessage("请稍后......");
						pDialog.show();
						
						inter.subscribe(urlList);
					}
				}
			});
		}

	}

	// 在某个RSS类中添加RSS地址时，判断有无重复
	private boolean isSameOfRssUrl(String category, String url) {
		Cursor c = rssOpenHelper.queryWithCU(category, url);
		if (c.moveToFirst()) {
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	// 添加RSS类别字段时判断是否添加过该字段
	private boolean isSameOfCategory(String str) {
		Cursor c = rssOpenHelper.queryCategory();
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				String str1 = c.getString(0);
				if (str1.equals(str)) {
					c.close();
					return true;
				}
				c.moveToNext();
			}
		}
		c.close();
		return false;
	}

	// 判断RSS地址是否是一个格式合法的URI
	private List<URL> initUrlList(String feedUrl) {
		List<URL> list = new ArrayList<URL>();
		try {
			list.add(new URL(feedUrl));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			new PopupDialog("错误提示：", "rss地址格式不正确！", context).show();
			return null;
		}
		return list;
	}

	// get数据源
	private void getDataSource() {
		int i = 0;
		Cursor cursor = rssOpenHelper.query();
		if (cursor.moveToFirst()) {
			category = new String[cursor.getCount()];
			int categoryIndex = cursor
					.getColumnIndex(RSSOpenHelper.RSS_CATEGORY);
			while (!cursor.isAfterLast()) {
				category[i] = cursor.getString(categoryIndex);
				cursor.moveToNext();
				i++;
			}
		}
		cursor.close();
	}

	private class SpinnerBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (category != null) {
				return category.length;
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView tv = new TextView(context);
			tv.setText(category[position]);
			Log.i("类别字段", "----" + category[position]);
			tv.setTextSize(22);
			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER_VERTICAL);
			ll.addView(tv);
			return ll;
		}
	}

	private class SpinnerListener implements CustomerDialogListener {

		@Override
		public void onCancelClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onOkClick() {
			// TODO Auto-generated method stub
			getDataSource();
			ba.notifyDataSetChanged();
		}

	}
	
	private class DialogReciver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			String message = intent.getExtras().getString("FEEDURL");
			String subFlag = intent.getExtras().getString("SUBUNSUB");
			if(action.equals("com.smit.rssreader.action.IQ_YES_BROADCAST")){
				if(subFlag.equals("sub")){
					new PopupDialog("温馨提示", message+"订阅成功！", context).show();
					pDialog.dismiss();
				}
				
			}else if(action.equals("com.smit.rssreader.action.IQ_NO_BROADCAST")){
				if(subFlag.equals("sub")){
					HttpGetThread hgt = new HttpGetThread(message, handler,
							selectedCategory);
					hgt.start();
					//pDialog.dismiss();	
				}
			}
		}
		
	}
}
