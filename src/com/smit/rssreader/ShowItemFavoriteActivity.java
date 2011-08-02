package com.smit.rssreader;

import com.smit.EasyLauncher.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowItemFavoriteActivity extends Activity {

	private HistoryOpenHelper historyOpenhelper = new HistoryOpenHelper(this);
	private final int DELETE = Menu.FIRST;
	private FavoBaseAdapter adapter;
	private ListView listview;
	private FavoListener favoListener =new FavoListener();
	private String httpAddress[];
	private String titles[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_show_item_favorite);
		setTitle("收藏夹");

		listview = (ListView) findViewById(R.id.favoritelist);
		initiaAdapter(); //为Adapter设置初始数据

		adapter = new FavoBaseAdapter();
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LinearLayout ll = (LinearLayout) view;
				TextView text = (TextView) ll.getChildAt(1);
				String link = text.getText().toString();

				WebView web = (WebView) findViewById(R.id.webshow);
				web.getSettings().setJavaScriptEnabled(true);
				web.loadUrl(link);
			}
		});

		listview
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						// TODO Auto-generated method stub
						menu.add(0, DELETE, Menu.NONE, "删除");
					}
				});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case DELETE:
			AdapterView.AdapterContextMenuInfo menuInfo;
			menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			int position = menuInfo.position;
			LinearLayout ll = (LinearLayout) listview.getChildAt(position);
			TextView text = (TextView) ll.getChildAt(1);
			String link = text.getText().toString();
			historyOpenhelper.deleteFavorite(link);
			favoListener.onOkClick();
			break;
		}
		return super.onContextItemSelected(item);
	}

    //为Adapter设置数据源
	private void initiaAdapter(){
		int i = 0;
		Cursor cursor = historyOpenhelper.queryFavorite(1);
		if (cursor.moveToFirst()) {
			titles = new String[cursor.getCount()];
			httpAddress = new String[cursor.getCount()];
			int titleIndex = cursor.getColumnIndex(HistoryOpenHelper.TITLE);
			int httpIndex = cursor.getColumnIndex(HistoryOpenHelper.HTTPADDRESS);
			while (!cursor.isAfterLast()) {
				titles[i] = cursor.getString(titleIndex);
				httpAddress[i] = cursor.getString(httpIndex);
				cursor.moveToNext();
				i++;
			}
			cursor.close();
		}
	}
	
	 private class FavoListener implements CustomerDialogListener{
	
	 @Override
	 public void onCancelClick() {
	 // TODO Auto-generated method stub
				
	 }
	
	 @Override
	 public void onOkClick() {
	 // TODO Auto-generated method stub
		 initiaAdapter();
		 adapter.notifyDataSetChanged();
	 }
			
	 }

	 private class FavoBaseAdapter extends BaseAdapter {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				if (titles != null) {
					return titles.length;
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
				LinearLayout ll = new LinearLayout(ShowItemFavoriteActivity.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				TextView tv1 = new TextView(ShowItemFavoriteActivity.this);
				tv1.setText(titles[position]);
				tv1.setTextSize(18);
				tv1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				tv1.setGravity(Gravity.CENTER_VERTICAL);

				TextView tv2 = new TextView(ShowItemFavoriteActivity.this);
				tv2.setText(httpAddress[position]);
				tv2.setTextSize(18);
				tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				tv2.setGravity(Gravity.CENTER_VERTICAL);
				ll.addView(tv1);
				ll.addView(tv2);
				return ll;
			}

		}

}
