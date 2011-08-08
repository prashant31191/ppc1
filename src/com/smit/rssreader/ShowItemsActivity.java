package com.smit.rssreader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smit.EasyLauncher.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowItemsActivity extends Activity {

	private RSSOpenHelper myOpenHelper = new RSSOpenHelper(this);
	private HistoryOpenHelper hisOpenHelper;
	ListBaseAdapter lba;
	private String chanTitle = null;
	private String strCategory = null;
	private String strRssUrl = null;
	private String[] strItemTitle = null;
	private String[] strItemDes = null;
	private String[] strItemLink = null;
	private boolean isAllMarked = false;
	private TextView txt_markedAll;
	private MarkedItemListener markListener;
	private MarkedBroadcastReceiver markReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.rss_show_items);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.rss_title_showitems);

		hisOpenHelper = new HistoryOpenHelper(this);
		markListener = new MarkedItemListener();
		txt_markedAll = (TextView) findViewById(R.id.rss_mark);

		// 注册广播接收器，收听标星的消息
		markReceiver = new MarkedBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RssReaderConstant.ADDFAVORITE);
		intentFilter.addAction(RssReaderConstant.NEWCONTENT);
		ShowItemsActivity.this.registerReceiver(markReceiver, intentFilter);

		Intent startingIntent = getIntent();
		if (startingIntent != null) {
			Bundle bundle = startingIntent.getExtras();
			if (bundle != null) {
				chanTitle = bundle.getString("CHANNEL");
				strCategory = bundle.getString("CATEGORY");
				strRssUrl = bundle.getString("RSSURL");
			}
		}

		ListView itemList = (ListView) findViewById(R.id.itemlist);
		getInitiaData(); // 初始化ListBaseAdapter数据源
		lba = new ListBaseAdapter();
		itemList.setAdapter(lba);
		itemList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 要先判断是否为标星 的条目
				String itemTitle = "";
				LinearLayout ll = (LinearLayout) view;
				if (ll.getChildAt(0) instanceof TextView) {
					TextView text = (TextView) ll.getChildAt(0);
					itemTitle = text.getText().toString();
				} else {
					LinearLayout l2 = (LinearLayout) ll.getChildAt(0);
					TextView text = (TextView) l2.getChildAt(1);
					itemTitle = text.getText().toString();
				}

				myOpenHelper.updateISREAED(strCategory, strRssUrl,
						strItemLink[position]);

				// 发送广播通知RSSReaderActivity数据有更新
				Intent i = new Intent(RssReaderConstant.READED_BROADCAST);
				sendBroadcast(i);

				Intent intent = new Intent(ShowItemsActivity.this,
						ShowDetailItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CHANNEL", chanTitle);
				bundle.putString("CATEGORY", strCategory);
				bundle.putString("ITEMTITLE", itemTitle);
				bundle.putString("RSSURL", strRssUrl);
				bundle.putString("ITEMLINK",strItemLink[position]);
				intent.putExtras(bundle);
				startActivity(intent);

				markListener.onOkClick(); // 通知showItemsActivity有数据更新
			}

		});

		txt_markedAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				myOpenHelper.updateISREAED(strCategory, strRssUrl, null);
				// 发送广播通知RSSReaderActivity数据有更新
				Intent i = new Intent(RssReaderConstant.READED_BROADCAST);
				sendBroadcast(i);
				markListener.onOkClick();
				txt_markedAll.setTextColor(Color.GRAY);
				isAllMarked = true;
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		isMarkedReaded(strCategory, strRssUrl, 1); // 检查该频道的item是否全部标记为已读
		if (isAllMarked == true) {
			txt_markedAll.setTextColor(Color.WHITE);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(markReceiver); // 取消注册Broadcast Receiver
		super.onDestroy();
	}

	private void getInitiaData() {
		int i = 0;
		Cursor cursor = myOpenHelper.queryWithUrlAndCategory(strCategory,
				strRssUrl);
		if (cursor.moveToFirst()) {
			strItemTitle = new String[cursor.getCount()];
			strItemDes = new String[cursor.getCount()];
			strItemLink = new String[cursor.getCount()];
			int titleIndex = cursor.getColumnIndex(RSSOpenHelper.ITEM_TITLE);
			int desIndex = cursor.getColumnIndex(RSSOpenHelper.ITEM_DES);
			int linkIndex = cursor.getColumnIndex(RSSOpenHelper.ITEM_LINK);
			while (!cursor.isAfterLast()) {
				strItemTitle[i] = cursor.getString(titleIndex);
				strItemDes[i] = cursor.getString(desIndex);
				strItemLink[i] = cursor.getString(linkIndex);
				cursor.moveToNext();
				i++;
			}
			cursor.close();
		}

	}

	//去除字符串中的空格、回车、换行符、制表符
	public String replaceBlank(String s) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(s);
		return m.replaceAll("");

	}

	// 检查该频道的item是否都被标记为已读
	private void isMarkedReaded(String cate, String feedUrl, int flag) {
		Cursor c1 = myOpenHelper.queryWithCU(cate, feedUrl);
		Cursor c2 = myOpenHelper.queryWithCUF(cate, feedUrl, flag);
		if (c1 != null && c2 != null) {
			isAllMarked = c1.getCount() != c2.getCount() ? false : true;
		}
		c1.close();
		c2.close();
	}

	// 检查item是否阅读过
	private boolean itemIsReaded(String cate, String link, int f) {
		Cursor c = myOpenHelper.queryWithCL(cate, link, f);
		if (c.moveToFirst()) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	// 检查该item是否被添加到收藏夹
	private boolean itemIsFavorite(String cate, String link, int flag) {
		Cursor c = hisOpenHelper.queryWithCHF(cate, link, flag);
		if (c.moveToFirst()) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	// 过滤字符串中所有tag标签
	private String stripTags(final String pHTMLString) { 
	      return pHTMLString.replaceAll("\\<.*?>",""); 
	}
	
	private class ListBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (strItemDes != null) {
				return strItemDes.length;
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
			String str = null;
			LinearLayout ll = new LinearLayout(ShowItemsActivity.this);
			ll.setOrientation(LinearLayout.VERTICAL);
			boolean isReaded = itemIsReaded(strCategory, strItemLink[position],
					1);
			boolean isFavorite = itemIsFavorite(strCategory,
					strItemLink[position], 1);
			TextView tv1 = new TextView(ShowItemsActivity.this);
			tv1.setText(Html.fromHtml(strItemTitle[position]));
			tv1.setTextSize(22);
			tv1.setPadding(0, 5, 0, 0);
			// tv1.setTextColor(Color.BLACK);
			tv1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv1.setGravity(Gravity.CENTER_VERTICAL);

			TextView tv2 = new TextView(ShowItemsActivity.this);
			String strOld = stripTags(strItemDes[position]);
			if (strOld.length() > 80) {
				str = replaceBlank(strOld.substring(0, 80)) + "......";
			} else {
				str = replaceBlank(strOld);
			}
			tv2.setText(Html.fromHtml(str));
			tv2.setTextSize(16);
			tv1.setPadding(0, 0, 0, 5);
			// tv2.setTextColor(Color.BLACK);

			tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv2.setGravity(Gravity.CENTER_VERTICAL);
			if (isReaded) {
				tv1.setTextColor(Color.GRAY);
				tv2.setTextColor(Color.GRAY);
			} else {
				tv1.setTextColor(Color.BLACK);
				tv2.setTextColor(Color.BLACK);
			}
			if (isFavorite) {
				LinearLayout lf = new LinearLayout(ShowItemsActivity.this);
				lf.setOrientation(LinearLayout.HORIZONTAL);
				ImageView image = new ImageView(ShowItemsActivity.this);
				image.setImageResource(R.drawable.rss_icon);
				lf.addView(image);
				lf.addView(tv1);
				ll.addView(lf);
				ll.addView(tv2);
			} else {
				ll.addView(tv1);
				ll.addView(tv2);
			}
			return ll;
		}

	}

	private class MarkedItemListener implements CustomerDialogListener {

		@Override
		public void onCancelClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onOkClick() {
			// TODO Auto-generated method stub
			getInitiaData();
			lba.notifyDataSetChanged();
		}

	}

	private class MarkedBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(RssReaderConstant.NEWCONTENT)) {
				markListener.onOkClick();
			} else if (action.equals(RssReaderConstant.ADDFAVORITE)) {
				
				/*
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
				int icon = R.drawable.rss_icon;
				CharSequence tickerText = "内容更新啦！";
				long when = System.currentTimeMillis();
				Notification notification = new Notification(icon, tickerText, when);
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.flags|=Notification.FLAG_AUTO_CANCEL;
				int notification_id = 1;
				PendingIntent pt=PendingIntent.getActivity(ShowItemsActivity.this, 0, new Intent(ShowItemsActivity.this,ShowItemsActivity.class), 0);
		    	//点击通知后的动作，这里是转回ShowItemsActivity
		    	notification.setLatestEventInfo(ShowItemsActivity.this,"更新","RSS频道内容更新啦！",pt);
		    	mNotificationManager.notify(notification_id, notification);
		    	*/
				markListener.onOkClick();
			}
		}

	}
}
