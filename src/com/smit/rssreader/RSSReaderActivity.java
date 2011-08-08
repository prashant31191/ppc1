package com.smit.rssreader;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.XMPPConnection;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.openims.model.MyApplication;
import com.openims.utility.PushServiceUtil;
import com.smit.EasyLauncher.LoginActivity;
import com.smit.EasyLauncher.R;
import com.smit.rssreader.extension.notification.ItemExtension;
import com.smit.rssreader.extension.notification.SuperfeedrEventExtension;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
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
	private ExpandableListView expandList;
	private LinearLayout layoutAddCategory;
	private LinearLayout layoutAddRss;
	private LinearLayout layoutUpdate;
	private LinearLayout layoutFavotite;
	private LinearLayout layoutCommend;
	private Button btn_login;
	private RSSOpenHelper rssOpenHelper = new RSSOpenHelper(this);
	private BaseRssInfoAdapter rssInfoAdapter = new BaseRssInfoAdapter(this);
	private MyDialogListener myDiaListener = new MyDialogListener();
	private List<String> categoryGroup;
	private List<List<String>> rssChild;
	private List<List<String>> rssUrl;
	private RSSReceiver rssReceiver;
	private String selectedCate = null;
	private int threadCount = 0;
	private ProgressDialog proDialog = null;
	private LoginReceiver loginReceiver;
	private MessageNotification messNotification;

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			FeedCategory fc = (FeedCategory) msg.obj;
			SyndFeed rssFeed = fc.getRssFeed();
			String strUrl = fc.getRssUrl();
			String strCate = fc.getRssCategory();
			if (rssFeed == null) {
				new PopupDialog("错误提示：", strUrl + "处理失败！",
						RSSReaderActivity.this).show();
			} else {
				// rssOpenHelper.deleteRssUrl(strCate, strUrl);
				String channelTitle = rssFeed.getTitle().trim();
				int count = rssFeed.getEntries().size();
				SyndEntry entry = null;
				for (int i = 0; i < count; i++) {
					entry = (SyndEntry) rssFeed.getEntries().get(i);
					String itemTitle = entry.getTitle().trim();
					String itemDes = entry.getDescription().getValue().trim();

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String itemPub = sdf.format(entry.getPublishedDate());

					String itemLink = entry.getLink().trim();
					// 检查是否是新发布的item
					if (isNewPublished(strCate, strUrl, itemLink)) {
						rssOpenHelper.insertRssInfo(strCate, strUrl,
								channelTitle, itemTitle, itemDes, itemPub,
								itemLink, RssReaderConstant.NOTREAD, RssReaderConstant.FEEDNOTONSERVER);
					}
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
		// setTheme(R.style.Transparent);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.rss_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.rss_title_rssreader);

		messNotification = new MessageNotification();
		rssReceiver = new RSSReceiver(myDiaListener, RSSReaderActivity.this,
				rssOpenHelper);
		loginReceiver = new LoginReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(RssReaderConstant.READED_BROADCAST);
		filter.addAction(RssReaderConstant.IQRESPONSEYES);
		registerReceiver(rssReceiver, filter);

		// 注册广播，接收登录和退出状态
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PushServiceUtil.ACTION_STATUS);
		RSSReaderActivity.this.registerReceiver(loginReceiver, intentFilter);

		findView();
		//writeToDatabase();
		
		getBasicInfo();
		expandList.setAdapter(rssInfoAdapter);

		layoutAddCategory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutAddCategory
						.setBackgroundResource(R.drawable.rss_menu_highlight_background);
				layoutAddRss
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutUpdate
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutFavotite
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutCommend
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				showDialog(ADD_CATEGORY_DIALOG);
			}
		});

		layoutAddRss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutAddRss
						.setBackgroundResource(R.drawable.rss_menu_highlight_background);
				layoutAddCategory
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutUpdate
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutFavotite
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutCommend
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
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
				layoutUpdate
						.setBackgroundResource(R.drawable.rss_menu_highlight_background);
				layoutAddRss
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutAddCategory
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutFavotite
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutCommend
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				// 先判断数据库中有没有feed地址
				if (haveFeedUrl()) {
					proDialog = new ProgressDialog(RSSReaderActivity.this);
					proDialog.setTitle("正在更新");
					proDialog.setMessage("请稍后......");
					proDialog.show();

					Cursor cur = rssOpenHelper
							.queryNotOnServer(RssReaderConstant.FEEDNOTONSERVER);
					if (cur.moveToFirst()) {
						int urlIndex = cur
								.getColumnIndex(RSSOpenHelper.RSS_URL);
						int cateIndex = cur
								.getColumnIndex(RSSOpenHelper.RSS_CATEGORY);
						HttpGetThread httpGetThread[] = new HttpGetThread[cur
								.getCount()];
						int j = 0;
						while (!cur.isAfterLast()) {
							String strUpdateCate = cur.getString(cateIndex);
							String strUpdateUrl = cur.getString(urlIndex);
							Log.i("输出feed所属类别", "------" + strUpdateCate);
							Log.i("输出feed地址", "------" + strUpdateUrl);
							httpGetThread[j] = new HttpGetThread(strUpdateUrl,
									myHandler, strUpdateCate);
							httpGetThread[j].start();
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
				layoutFavotite
						.setBackgroundResource(R.drawable.rss_menu_highlight_background);
				layoutAddRss
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutUpdate
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutAddCategory
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutCommend
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				Intent i = new Intent(RSSReaderActivity.this,
						ShowItemFavoriteActivity.class);
				startActivity(i);
			}
		});

		layoutCommend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				layoutCommend
						.setBackgroundResource(R.drawable.rss_menu_highlight_background);
				layoutAddRss
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutUpdate
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutAddCategory
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				layoutFavotite
						.setBackgroundResource(R.drawable.rss_menu_normal_background);
				Intent i = new Intent(RSSReaderActivity.this,
						ShowCommendActivity.class);
				startActivity(i);
			}
		});

		/*
		 * Button button = (Button) findViewById(R.id.btntest);
		 * button.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub // 测试取消订阅 List<URL> feedUrls = new ArrayList<URL>(); try
		 * { feedUrls.add(new URL("http://www.jiucool.com/feed/atom/")); } catch
		 * (MalformedURLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } interactive.unsubscribe(feedUrls);// 取消订阅 }
		 * });
		 */

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!xmppIsConnected()) {
			new PopupDialog("温馨提示：", "您还没有登录，登录后将享受更多精彩！",
					RSSReaderActivity.this).show();
			btn_login.setVisibility(View.VISIBLE);
			btn_login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// 调用登录对话框
					Intent intent = new Intent();
					intent
							.setClass(RSSReaderActivity.this,
									LoginActivity.class);
					startActivity(intent);
				}
			});
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(rssReceiver); // 取消注册Broadcast Receiver
		unregisterReceiver(loginReceiver);
		super.onDestroy();
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

				Cursor cur = rssOpenHelper.queryWithCateChannel(selectedCate,
						childTitle);
				int onServer = 0;
				String feedUrl = "";
				if (cur.moveToFirst()) {
					int urlIndex = cur.getColumnIndex(RSSOpenHelper.RSS_URL);
					int onServerIndex = cur
							.getColumnIndex(RSSOpenHelper.ISONSERVER);
					feedUrl = cur.getString(urlIndex).trim();
					onServer = cur.getInt(onServerIndex);
					Log.i("取消订阅的feed地址：", "-------" + feedUrl);
					Log.i("取消订阅的feed是否是本地服务器上的feed:", "-----标志为：" + onServer);
				}
				if (onServer == RssReaderConstant.FEEDONSERVER) {
					List<URL> feedUrls = new ArrayList<URL>();
					try {
						feedUrls.add(new URL(feedUrl));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					interactive.unsubscribe(feedUrls); // 取消订阅
				}else{
					rssOpenHelper.deleteChannel(feedUrl);
					new PopupDialog("温馨提示：", "这个频道的订阅已经取消！", RSSReaderActivity.this).show();
					myDiaListener.onOkClick();  //通知RSSReaderActivity数据有更新
				}
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
		layoutCommend = (LinearLayout) findViewById(R.id.layout_recommend);
		btn_login = (Button) findViewById(R.id.rss_login);
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

	// 更新时检查是否是新发布的item
	private boolean isNewPublished(String cate, String feedUrl, String link) {
		Cursor c = rssOpenHelper.queryWithCFL(cate, feedUrl, link);
		if (c.moveToFirst()) {
			c.close();
			return false;
		} else {
			c.close();
			return true;
		}
	}

	// 查询数据库中有无feed地址
	private boolean haveFeedUrl() {
		Cursor c = rssOpenHelper.queryFeed();
		if (c.moveToFirst()) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
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

	// 判断是否建立XMPP通信的TCP连接
	private boolean xmppIsConnected() {
		MyApplication myApp = (MyApplication) RSSReaderActivity.this
				.getApplication();
		if (myApp.getConnection() != null) {
			if (myApp.getConnection().isConnected()) {
				return true;
			}
		}
		return false;
	}

	// 插入到数据库之前,判断属于哪个类别的feed
	private String getCategoryOfRssUrl(String feedUrl) {
		Cursor c = rssOpenHelper.queryWithUrl(feedUrl);
		if (c.moveToFirst()) {
			int cateIndex = c.getColumnIndex(RSSOpenHelper.RSS_CATEGORY);
			return c.getString(cateIndex);
		} else {
			c.close();
			return null;
		}
	}

	// 收到服务端push过来的Message后通知User
	private void notifyUser() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.rss_icon;
		CharSequence tickerText = "内容更新啦！";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		int notification_id = 1;
		PendingIntent pt = PendingIntent.getActivity(RSSReaderActivity.this, 0,
				new Intent(RSSReaderActivity.this, RSSReaderActivity.class), 0);
		// 点击通知后的动作，这里是转回RSSReaderActivity
		notification.setLatestEventInfo(RSSReaderActivity.this, "更新",
				"RSS频道内容更新啦！", pt);
		mNotificationManager.notify(notification_id, notification);
	}
	
	/*//写入几条初始数据
	private void writeToDatabase(){
		rssOpenHelper.insertCategory("体育", "");
		rssOpenHelper.insertCategory("博客", "");

		rssOpenHelper.insertRssInfo(
				"体育",
				"http://rss.sina.com.cn/news/allnews/sports.xml",
				"焦点新闻-新浪体育",
				"庄则栋：何谓美德 乒乓球队长久不衰秘诀",
				"",
				"2011-08-08",
				"http://go.rss.sina.com.cn/redirect.php?url=http://blog.sina.com.cn/s/blog_4cf7b4ec0102dry8.html",
				0, 0);
		rssOpenHelper.insertRssInfo(
				"体育",
				"http://rss.sina.com.cn/news/allnews/sports.xml",
				"焦点新闻-新浪体育",
				"老将莫科已基本确定离队 防守不兴奋让邓帅不感冒",
				"记者袁俊8月7日广州报道&nbsp; 在王治郅还在养伤的时候，谁会是易建联最好的内线搭档。恐怕大多数人会想到的是莫科。但是出人意料的是，在斯杯海宁站的头两场中短暂出场之后，莫科更多时候是坐在替补席上看着队友们表现。而在广州站的比赛中，莫科虽然在和新西兰的比赛中出场....",
				"2011-08-08",
				"http://go.rss.sina.com.cn/redirect.php?url=http://sports.sina.com.cn/cba/2011-08-08/15255694418.shtml",
				0, 0);
		
		rssOpenHelper.insertRssInfo(
				"博客",
				"http://feed.williamlong.info",
				"月光博客",
				"移动互联网的入口之争",
				"入口是指你最常寻找信息、解决问题的方式，搜索引擎是互联网最大的入口，网址导航提供与搜索引擎不同价值的入口。QQ是一个中国互联网的怪胎巨鳄，拥有最完整、最真实的社交网络，但是因为它没有广泛输出价值所以现在还不算入口，浏览器作为用户访问互联网的重要工具也成为入口，而操作系统则是整个链条中最大的入口。",
				"2011-08-08",
				"http://www.williamlong.info/archives/2766.html",
				0, 0);
	}*/
	
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

			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setPadding(30, 8, 0, 8);
			if (f == true) {
				tv.setTextSize(28);
				tv.setText(s);
			} else {
				tv.setTextSize(20);
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

	private class MessageNotification implements OnNotificationHandler {

		@Override
		public void onNotification(SuperfeedrEventExtension event) {
			// TODO Auto-generated method stub
			int count = event.getItems().getItemsCount();
			if (count != 0) {
				String channelTitle = event.getChannelTitle().trim();
				Log.i("频道标题", "-----" + channelTitle);
				String feedUrl = event.getItems().getNode().trim();
				Log.i("频道的feed地址", "-----" + feedUrl);

				// 插入到数据库之前查看属于哪个类别的feed
				String cate = getCategoryOfRssUrl(feedUrl);
				for (Iterator<ItemExtension> iterator = event.getItems()
						.getItems(); iterator.hasNext();) {
					ItemExtension item = iterator.next();
					String itemTitle = item.getTitle();
					Log.i("item标题", "-----" + itemTitle);
					String itemDes = item.getSummary();
					Log.i("item摘要", "-----" + itemDes);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
					String itemPub = sdf.format(item.getPublished());
					Log.i("item发布日期", "-----" + itemPub);
					String itemLink = item.getLink();
					Log.i("超链接----", "-----" + itemLink);
					rssOpenHelper.insertRssInfo(cate, feedUrl, channelTitle,
							itemTitle, itemDes, itemPub, itemLink, RssReaderConstant.NOTREAD, RssReaderConstant.FEEDONSERVER);
				}
				myDiaListener.onOkClick(); // 数据更新后通知RSSReaderActivity
				notifyUser();     // 通知用户有新的消息push过来
				Intent i = new Intent(RssReaderConstant.NEWCONTENT); // 数据更新后通知ShowItemsActivity
				sendBroadcast(i);
			}
		}

	}

	private class LoginReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
			Log.i("STATUSE:", "-------" + status);
			if (PushServiceUtil.PUSH_STATUS_LOGIN_SUC.equals(status)) {
				btn_login.setVisibility(View.INVISIBLE);
				MyApplication myApp = (MyApplication) RSSReaderActivity.this
						.getApplication();
				XMPPConnection connection = myApp.getConnection();
				String jid = myApp.getAdminJid();
				String server = myApp.getServeName();
				interactive = new InteractiveServer(connection, jid, server,
						RSSReaderActivity.this);
				interactive.addOnNotificationHandler(messNotification);
			}
		}
	}
}
