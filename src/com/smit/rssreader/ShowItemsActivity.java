package com.smit.rssreader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smit.EasyLauncher.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowItemsActivity extends Activity {

	private final String READED_BROADCAST = "com.smit.rssreader.action.READED_BROADCAST";
	private RSSOpenHelper myOpenHelper = new RSSOpenHelper(this);
	ListBaseAdapter lba ;
	private String chanTitle = null;
	private String strCategory = null;
	private String strRssUrl = null ;
	private String[] strItemTitle = null;
	private String[] strItemDes = null;
    private String[] strItemLink = null ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_show_items);

		Intent startingIntent = getIntent();
		if (startingIntent != null) {
			Bundle bundle = startingIntent.getExtras();
			if (bundle != null) {
				chanTitle = bundle.getString("CHANNEL");
				strCategory = bundle.getString("CATEGORY");
				strRssUrl = bundle.getString("RSSURL");
				setTitle(chanTitle);
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
				LinearLayout ll = (LinearLayout) view;
				TextView text = (TextView) ll.getChildAt(0);
				String itemTitle = text.getText().toString();
                
				myOpenHelper.updateISREAED(strCategory, strRssUrl,strItemLink[position]);
				
				//发送广播通知RSSReaderActivity数据有更新
				Intent i = new Intent(READED_BROADCAST);
				sendBroadcast(i);
				
				Intent intent = new Intent(ShowItemsActivity.this,
						ShowDetailItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CHANNEL", chanTitle);
				bundle.putString("CATEGORY", strCategory);
				bundle.putString("ITEMTITLE", itemTitle);
				bundle.putString("RSSURL",strRssUrl);
				intent.putExtras(bundle);
				startActivity(intent);
			}

		});

	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		lba.notifyDataSetChanged();
		super.onResume();
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

	public String replaceBlank(String s) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(s);
		return m.replaceAll("");

	}

	//检查item是否阅读过
	private boolean itemIsReaded(String cate,String link,int f){
		Cursor c = myOpenHelper.queryWithCL(cate, link, f);
		if(c.moveToFirst()){
			c.close();
			return true ;
		}else{
			c.close();
			return false ;
		}
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
			boolean isReaded = itemIsReaded(strCategory, strItemLink[position], 1); 
			TextView tv1 = new TextView(ShowItemsActivity.this);
			tv1.setText(strItemTitle[position]);
			tv1.setTextSize(22);
			//tv1.setTextColor(Color.BLACK);
			tv1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv1.setGravity(Gravity.CENTER_VERTICAL);

			TextView tv2 = new TextView(ShowItemsActivity.this);
			if (strItemDes[position].length() > 80) {
				str = replaceBlank(strItemDes[position].substring(0, 80))
						+ "......";
			} else {
				str = replaceBlank(strItemDes[position]);
			}
			tv2.setText(str);
			tv2.setTextSize(16);
			//tv2.setTextColor(Color.BLACK);
		    
			tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv2.setGravity(Gravity.CENTER_VERTICAL);
			if(isReaded){
				tv1.setTextColor(Color.GRAY);
				tv2.setTextColor(Color.GRAY);
			}else{
				tv1.setTextColor(Color.WHITE);
				tv2.setTextColor(Color.WHITE);
			}
			ll.addView(tv1);
			ll.addView(tv2);
			return ll;
		}

	}

}
