package com.smit.rssreader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.XMPPConnection;
import com.openims.model.MyApplication;
import com.smit.EasyLauncher.R;
import com.smit.rssreader.extension.notification.ItemExtension;
import com.smit.rssreader.extension.notification.SuperfeedrEventExtension;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class RSSReaderActivity extends Activity {
	private final int ADD_CATEGORY_DIALOG = 1;
	private final int ADD_RSS_DIALOG = 2;
	private final int MODIFY_CATEGORY_DIALOG = 3;
	private final int EDIT = Menu.FIRST;
	private final int DELETE = Menu.FIRST + 1;

	private InteractiveServer interactive;
	private ExpandableListView expandList = null;
	private LinearLayout layoutAddCategory = null;
	private LinearLayout layoutAddRss = null;
	private LinearLayout layoutUpdate = null;
	private LinearLayout layoutFavotite = null;
	private RSSOpenHelper rssOpenHelper = new RSSOpenHelper(this);
	private BaseRssInfoAdapter rssInfoAdapter = new BaseRssInfoAdapter(this);
	private MyDialogListener myDiaListener = new MyDialogListener();
	private List<String> categoryGroup;
	private List<List<String>> rssChild;
	private List<List<String>> rssUrl;
	private RSSReceiver rssReceiver = null;
	private String selectedCate = null;
	private int threadCount = 0;
	ProgressDialog proDialog = null;
	//LoginReceiver loginReceiver;

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			FeedCategory fc = (FeedCategory) msg.obj;
			RSSFeed rssFeed = fc.getRssFeed();
			String strUrl = fc.getRssUrl();
			String strCate = fc.getRssCategory();
			if (rssFeed == null) {
				new PopupDialog("错误提示：", strUrl + "处理失败！",
						RSSReaderActivity.this).show();
			} else {
				rssOpenHelper.deleteRssUrl(strCate, strUrl);
				String channelTitle = rssFeed.getTitle();
				int count = rssFeed.getItemCount();
				RSSItem item = null;
				for (int i = 0; i < count; i++) {
					item = rssFeed.getItem(i);
					String itemTitle = item.getTitle();
					String itemDes = item.getDescription();
					String itemPub = item.getPubDate();
					String itemLink = item.getLink();
					rssOpenHelper.insertRssInfo(strCate, strUrl, channelTitle,
							itemTitle, itemDes, itemPub, itemLink, 0,0 );
				}
			}
			threadCount--;
			if (threadCount == 0) {
				proDialog.dismiss();
				myDiaListener.onOkClick();
			}

			super.handleMessage(msg);
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_main);

		
		
//        List<URL> urlList = new ArrayList<URL>() ;
//        try {
//			urlList.add(new URL("http://www.dtheatre.com/backend.php?xml=yes"));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		interactive.subscribe(urlList, new OnResponseHandler() {
//			
//			@Override
//			public void onSuccess(Object response) {
//			// TODO Auto-generated method stub
//				rssOpenHelper.insertRssInfo("tiyu", "http://www.dtheatre.com/backend.php?xml=yes", "","", "", "", "", 1);
//				//pDialog.dismiss();							
//			 }
//										
//			 @Override
//			 public void onError(String infos) {
//			 // TODO Auto-generated method stub
//				 Log.i("-----","-----订阅失败");
//				 //new PopupDialog("订阅失败：", infos, RSSReaderActivity.this).show();
//				// pDialog.dismiss();
//			 }
//			 });
		
		findView();
		getBasicInfo();
		expandList.setAdapter(rssInfoAdapter);

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.smit.rssreader.action.READED_BROADCAST");
		filter.addAction("com.smit.rssreader.action.SUBSCRIPTION");
		rssReceiver = new RSSReceiver(myDiaListener);
		registerReceiver(rssReceiver, filter);

//		//注册广播，接收登录状态
//		IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(PushServiceUtil.ACTION_STATUS);
//        RSSReaderActivity.this.registerReceiver(loginReceiver, intentFilter);
//        
		layoutAddCategory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(ADD_CATEGORY_DIALOG);
			}
		});

		layoutAddRss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(ADD_RSS_DIALOG);
			}
		});

		expandList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RSSReaderActivity.this,
						ShowItemsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CHANNEL", rssChild.get(groupPosition).get(
						childPosition));
				bundle.putString("CATEGORY", categoryGroup.get(groupPosition));
				bundle.putString("RSSURL", rssUrl.get(groupPosition).get(
						childPosition));
				intent.putExtras(bundle);

				startActivity(intent);
				return false;
			}

		});

		expandList
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						// TODO Auto-generated method stub
						ExpandableListContextMenuInfo mInfo = (ExpandableListContextMenuInfo) menuInfo;
						int type = ExpandableListView
								.getPackedPositionType(mInfo.packedPosition);
						if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
							menu.add(0, EDIT, Menu.NONE, "编辑");
							menu.add(0, DELETE, Menu.NONE, "删除");
						} else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
							menu.add(0, DELETE, Menu.NONE, "删除");
						}
					}
				});

		layoutUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				proDialog = new ProgressDialog(RSSReaderActivity.this);
				proDialog.setTitle("正在更新");
				proDialog.setMessage("请稍后......");
				proDialog.show();

				String strCates[] = null;
				int i = 0;
				Cursor cursor = rssOpenHelper.query();
				if (cursor.moveToFirst()) {
					strCates = new String[cursor.getCount()];
					int index = cursor
							.getColumnIndex(RSSOpenHelper.RSS_CATEGORY);
					while (!cursor.isAfterLast()) {
						strCates[i] = cursor.getString(index);
						cursor.moveToNext();
						i++;
					}
				}
				HttpGetThread httpGetThread[][] = new HttpGetThread[strCates.length][];
				for (int k = 0; k < strCates.length; k++) {
					Cursor cur = rssOpenHelper.queryNotOnServer(strCates[k],0);
					String strUpdateCate = strCates[k];
					Log.i("数据库中的feed地址的数目", "------" + cur.getCount());
					if (cur.moveToFirst()) {
						int urlIndex = cur
								.getColumnIndex(RSSOpenHelper.RSS_URL);
						httpGetThread[k] = new HttpGetThread[cur.getCount()];
						int j = 0;
						while (!cur.isAfterLast()) {
							String strUpdateUrl = cur.getString(urlIndex);
							Log.i("输出feed地址", "------" + strUpdateUrl);
							httpGetThread[k][j] = new HttpGetThread(
									strUpdateUrl, myHandler, strUpdateCate);
							httpGetThread[k][j].start();
							cur.moveToNext();
							j++;
							threadCount++;
							Log.i("------", "-----" + threadCount);
						}
					}
				}
			}
		});

		layoutFavotite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RSSReaderActivity.this,
						ShowItemFavoriteActivity.class);
				startActivity(i);
			}
		});
		
		//需要测试初始化会不会影响页面的加载
		MyApplication myApp = (MyApplication)RSSReaderActivity.this.getApplication();
        XMPPConnection connection = myApp.getConnection();
        String jid = myApp.getAdminJid();
        String server = myApp.getServeName();
        interactive = new InteractiveServer(connection, jid, server, RSSReaderActivity.this);
        interactive.addOnNotificationHandler(new OnNotificationHandler() {

			@Override
			public void onNotification(SuperfeedrEventExtension event) {
				// TODO Auto-generated method stub
				int count = event.getItems().getItemsCount();
				if (count != 0) {
					String strCategory[] = null;
					//String channelTitle = event.getStatus().getChannleTitle();
					String feedUrl = event.getItems().getNode();
//					Cursor c = rssOpenHelper.queryWithUrl(feedUrl);
//					if (c.moveToFirst()) {
//						strCategory = new String[c.getCount()];
//						int i = 0;
//						while (!c.isAfterLast()) {
//							strCategory[i] = c.getString(0);
//							c.moveToNext();
//							i++;
//						}
//						c.close();
//					}
//					for (int j = 0; j < strCategory.length; j++) {
						for (Iterator<ItemExtension> iterator = event
								.getItems().getItems(); iterator.hasNext();) {
							ItemExtension item = iterator.next();
							String itemTitle = item.getEntry().getTitle();
							String itemDes = item.getEntry().getSummary();
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss");
							String itemPub = sdf.format(item.getEntry()
									.getPublished());
							String itemLink = item.getEntry().getLink();
							Log.i("超链接----","-----"+itemLink);
							//rssOpenHelper.insertRssInfo(strCategory[j],
							//		feedUrl, channelTitle, itemTitle, itemDes,
							//		itemPub, itemLink, 0);
						}
//					}

				}
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		ExpandableListContextMenuInfo menuInfo = (ExpandableListContextMenuInfo) item
				.getMenuInfo();
		int type = ExpandableListView
				.getPackedPositionType(menuInfo.packedPosition);
		int groupPos = ExpandableListView
				.getPackedPositionGroup(menuInfo.packedPosition);
		selectedCate = categoryGroup.get(groupPos);
		switch (item.getItemId()) {
		case DELETE:
			if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				rssOpenHelper.deleteCategory(selectedCate);
				myDiaListener.onOkClick();
			} else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				int childPos = ExpandableListView
						.getPackedPositionChild(menuInfo.packedPosition);
				final String childTitle = rssChild.get(groupPos).get(childPos);
				
//				Cursor cur = rssOpenHelper.queryWithCateChannel(selectedCate, childTitle);
//				List<URL> feedUrls = new ArrayList<URL>();
//				if(cur.moveToFirst()){
//					int urlIndex = cur.getColumnIndex(RSSOpenHelper.RSS_DESCRIPTION);
//					String feedUrl = cur.getString(urlIndex);
//					try {
//						feedUrls.add(new URL(feedUrl));
//					} catch (MalformedURLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				interactive.unsubscribe(feedUrls, new OnResponseHandler() {
//					
//					@Override
//					public void onSuccess(Object response) {
//						// TODO Auto-generated method stub
//						rssOpenHelper.deleteChannel(selectedCate, childTitle);
//						new PopupDialog("温馨提示：","已经取消该频道的订阅！",RSSReaderActivity.this).show();
//					}
//					
//					@Override
//					public void onError(String infos) {
//						// TODO Auto-generated method stub
//						new PopupDialog("温馨提示：","服务器端无响应，取消订阅不成功！",RSSReaderActivity.this).show();
//					}
//				});
				myDiaListener.onOkClick();
			}
			break;
		case EDIT:
			if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				groupPos = ExpandableListView
						.getPackedPositionGroup(menuInfo.packedPosition);
				showDialog(MODIFY_CATEGORY_DIALOG);
			}
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		super.onCreateDialog(id);
		switch (id) {
		case ADD_CATEGORY_DIALOG:
			CustomerDialog dialog1 = null;
			dialog1 = new CustomerDialog(RSSReaderActivity.this,
					ADD_CATEGORY_DIALOG, myDiaListener, null);
			dialog1.setTitle("添加类");
			dialog1.setIcon(R.drawable.rss_icon);
			return dialog1;
		case ADD_RSS_DIALOG:
			CustomerDialog dialog2 = null;
			dialog2 = new CustomerDialog(RSSReaderActivity.this,
					ADD_RSS_DIALOG, myDiaListener, interactive);
			dialog2.setTitle("添加RSS");
			dialog2.setIcon(R.drawable.rss_icon);
			return dialog2;
		case MODIFY_CATEGORY_DIALOG:
			CategoryEditDialog dia = new CategoryEditDialog(
					RSSReaderActivity.this, selectedCate, myDiaListener);
			dia.setTitle("编辑RSS类");
			dia.setIcon(R.drawable.rss_icon);
			return dia;
		}
		return null;

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		removeDialog(id);
		super.onPrepareDialog(id, dialog);
	}

	private void findView() {
		expandList = (ExpandableListView) findViewById(R.id.expandlist);
		layoutAddCategory = (LinearLayout) findViewById(R.id.layout_category);
		layoutAddRss = (LinearLayout) findViewById(R.id.layout_rss);
		layoutUpdate = (LinearLayout) findViewById(R.id.updatecontent);
		layoutFavotite = (LinearLayout) findViewById(R.id.layout_favo);
	}

	public void getBasicInfo() {
		categoryGroup = new ArrayList<String>();
		rssChild = new ArrayList<List<String>>();
		rssUrl = new ArrayList<List<String>>();
		Cursor c1 = rssOpenHelper.queryCategory();
		String[] categorys = null;
		if (c1.moveToFirst()) {
			int categoryIndex = c1.getColumnIndex(RSSOpenHelper.RSS_CATEGORY);
			categorys = new String[c1.getCount()];
			int i = 0;
			while (!c1.isAfterLast()) {
				categorys[i] = c1.getString(categoryIndex);
				c1.moveToNext();
				i++;
			}
			c1.close();
			for (int j = 0; j < categorys.length; j++) {
				String[] titles = null;
				String[] urls = null;
				Cursor c2 = rssOpenHelper.queryWithCategory(categorys[j]);
				if (c2.moveToFirst()) {
					int titleIndex = c2
							.getColumnIndex(RSSOpenHelper.CHANNEL_TITLE);
					int urlIndex = c2.getColumnIndex(RSSOpenHelper.RSS_URL);
					titles = new String[c2.getCount()];
					urls = new String[c2.getCount()];
					int k = 0;
					while (!c2.isAfterLast()) {
						titles[k] = c2.getString(titleIndex);
						urls[k] = c2.getString(urlIndex);
						c2.moveToNext();
						k++;
					}
					c2.close();
				}
				addInfo(categorys[j], titles, urls);
			}
		}
	}

	public void addInfo(String p, String[] c, String[] u) {
		categoryGroup.add(p);

		List<String> item = new ArrayList<String>();
		List<String> urlList = new ArrayList<String>();

		if (c == null) {
			c = new String[] { " " };
		}
		if (u == null) {
			u = new String[] { " " };
		}
		for (int i = 0; i < c.length; i++) {
			if (!c[i].equals(" ")) {
				item.add(c[i]);
				if (!u[i].equals(" "))
					urlList.add(u[i]);
			}
		}
		rssChild.add(item);
		rssUrl.add(urlList);
	}

	// 查询某一RSS类的item的条目数
	private int getCountOfItems(String category, String url) {
		Cursor c = rssOpenHelper.queryWithCU(category, url);
		if (c.moveToFirst()) {
			c.close();
			return c.getCount();
		} else {
			c.close();
			return 0;
		}

	}

	// 查询某一RSS类中未读的item条目数
	private int getCountNoRead(String category, String url, int flag) {
		Cursor c = rssOpenHelper.queryWithCUF(category, url, flag);
		if (c.moveToFirst()) {
			c.close();
			return c.getCount();
		} else {
			c.close();
			return 0;
		}
	}

	private class BaseRssInfoAdapter extends BaseExpandableListAdapter {

		Context context;
		boolean flag = false;

		public BaseRssInfoAdapter(Context context) {
			this.context = context;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return rssChild.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			flag = false;
			String string = rssChild.get(groupPosition).get(childPosition);
			String str1 = rssUrl.get(groupPosition).get(childPosition);
			String str2 = categoryGroup.get(groupPosition);
			return getGenericView(string, flag, str1, str2);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub

			return rssChild.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return categoryGroup.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return categoryGroup.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			flag = true;
			String string = categoryGroup.get(groupPosition);
			return getGenericView(string, flag, null, null);
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

		// View stub to create Group/Children 's View
		public View getGenericView(String s, boolean f, String url, String cate) {
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView tv = new TextView(context);

			tv.setTextColor(Color.WHITE);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setPadding(36, 0, 0, 0);
			if (f == true) {
				tv.setTextSize(32);
				tv.setText(s);
			} else {
				tv.setTextSize(24);
				int count1 = getCountOfItems(cate, url);
				int count2 = getCountNoRead(cate, url, 0);
				String str = "(" + count2 + "/" + count1 + ")";
				tv.setText(s + str);
			}

			ll.addView(tv);
			return ll;
		}

	}

	private class MyDialogListener implements CustomerDialogListener {

		@Override
		public void onCancelClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onOkClick() {
			// TODO Auto-generated method stub
			getBasicInfo();
			Log.i("刷新ListView", "-------");
			rssInfoAdapter.notifyDataSetChanged();
		}
	}

//	public class LoginReceiver extends BroadcastReceiver{	    
//	    
//    	@Override
//    	public void onReceive(Context context,Intent intent){
//    		String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
//    		Log.i("STATUSE:","-------"+status);
//    		if(PushServiceUtil.PUSH_STATUS_LOGIN_SUC.equals(status)){
//    			//获取MyApplication对象,初始化interactive对象
//    			MyApplication myApp = (MyApplication)RSSReaderActivity.this.getApplication();
//    	        XMPPConnection connection = myApp.getConnection();
//    	        String jid = myApp.getAdminJid();
//    	        String server = myApp.getServeName();
//    	        interactive = new InteractiveServer(connection, jid, server, RSSReaderActivity.this);
//    	        interactive.addOnNotificationHandler(new OnNotificationHandler() {
//
//    				@Override
//    				public void onNotification(SuperfeedrEventExtension event) {
//    					// TODO Auto-generated method stub
//    					int count = event.getItems().getItemsCount();
//    					if (count != 0) {
//    						String strCategory[] = null;
//    						//String channelTitle = event.getStatus().getChannleTitle();
//    						String feedUrl = event.getItems().getNode();
////    						Cursor c = rssOpenHelper.queryWithUrl(feedUrl);
////    						if (c.moveToFirst()) {
////    							strCategory = new String[c.getCount()];
////    							int i = 0;
////    							while (!c.isAfterLast()) {
////    								strCategory[i] = c.getString(0);
////    								c.moveToNext();
////    								i++;
////    							}
////    							c.close();
////    						}
////    						for (int j = 0; j < strCategory.length; j++) {
//    							for (Iterator<ItemExtension> iterator = event
//    									.getItems().getItems(); iterator.hasNext();) {
//    								ItemExtension item = iterator.next();
//    								String itemTitle = item.getEntry().getTitle();
//    								String itemDes = item.getEntry().getSummary();
//    								SimpleDateFormat sdf = new SimpleDateFormat(
//    										"yyyy-MM-dd'T'HH:mm:ss");
//    								String itemPub = sdf.format(item.getEntry()
//    										.getPublished());
//    								String itemLink = item.getEntry().getLink();
//    								Log.i("超链接----","-----"+itemLink);
//    								//rssOpenHelper.insertRssInfo(strCategory[j],
//    								//		feedUrl, channelTitle, itemTitle, itemDes,
//    								//		itemPub, itemLink, 0);
//    							}
////    						}
//
//    					}
//    				}
//    			});
//    		}
//    	}
//    }
}
