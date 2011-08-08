package com.smit.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSOpenHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "rss_reader";
	public static final String TAB_CATEGORY = "rss_category";
	public static final String CID = "cid";
	public static final String RSS_CATEGORY = "category";
	public static final String RSS_DESCRIPTION = "description";

	public static final String TAB_RSSINFO = "rss_info";
	public static final String RID = "rid";
	public static final String RSS_URL = "rssurl";
	public static final String CHANNEL_TITLE = "channeltitle";
	public static final String ITEM_TITLE = "itemtitle";
	public static final String ITEM_DES = "itemdescription";
	public static final String ITEM_PUBDATE = "itempubdate";
	public static final String ITEM_LINK = "itemlink";
	public static final String ISREAD = "isread";
	public static final String ISONSERVER = "onserver"; // "1"��ʾ�ڱ��ط�������

	public RSSOpenHelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists " + TAB_CATEGORY + " (" + CID
				+ " integer primary key autoincrement," + RSS_CATEGORY
				+ " varchar," + RSS_DESCRIPTION + " varchar)");

		db
				.execSQL("create table if not exists " + TAB_RSSINFO + " ("
						+ RID + " integer primary key autoincrement,"
						+ RSS_CATEGORY + " varchar," + RSS_URL + " varchar,"
						+ CHANNEL_TITLE + " varchar," + ITEM_TITLE
						+ " varchar," + ITEM_DES + " varchar," + ITEM_PUBDATE
						+ " varchar," + ITEM_LINK + " varchar," + ISREAD
						+ " integer," + ISONSERVER + " integer)");
		db.execSQL("insert into " + TAB_CATEGORY
				+ " (category, description) values ('����', '')");
		db.execSQL("insert into " + TAB_CATEGORY
				+ " (category, description) values ('����', '')");
		db
				.execSQL("insert into rss_info(category,rssurl,channeltitle,itemtitle,itemdescription,itempubdate,itemlink,isread,onserver) values('����',"
						+ "'http://rss.sina.com.cn/news/allnews/sports.xml',"
						+ "'��������-��������',"
						+ "'ׯ�򶰣���ν���� ƹ����ӳ��ò�˥�ؾ�',"
						+ "'',"
						+ "'2011-08-08',"
						+ "'http://go.rss.sina.com.cn/redirect.php?url=http://blog.sina.com.cn/s/blog_4cf7b4ec0102dry8.html',"
						+ "'0', '0')");
		db
		.execSQL("insert into rss_info(category,rssurl,channeltitle,itemtitle,itemdescription,itempubdate,itemlink,isread,onserver) values('����',"
				+ "'http://rss.sina.com.cn/news/allnews/sports.xml',"
				+ "'��������-��������',"
				+ "'�Ͻ�Ī���ѻ���ȷ����� ���ز��˷��õ�˧����ð',"
				+ "'����Ԭ��8��7�չ��ݱ���&nbsp; ������ۤ�������˵�ʱ��˭�����׽�����õ����ߴ�����´�����˻��뵽����Ī�ơ����ǳ������ϵ��ǣ���˹������վ��ͷ�����ж��ݳ���֮��Ī�Ƹ���ʱ���������油ϯ�Ͽ��Ŷ����Ǳ��֡����ڹ���վ�ı����У�Ī����Ȼ�ں��������ı����г���....',"
				+ "'2011-08-08',"
				+ "'http://go.rss.sina.com.cn/redirect.php?url=http://sports.sina.com.cn/cba/2011-08-08/15255694418.shtml',"
				+ "'0', '0')");

		db
		.execSQL("insert into rss_info(category,rssurl,channeltitle,itemtitle,itemdescription,itempubdate,itemlink,isread,onserver) values('����',"
				+ "'http://feed.williamlong.info',"
				+ "'�¹ⲩ��',"
				+ "'�ƶ������������֮��',"
				+ "'�����ָ���Ѱ����Ϣ���������ķ�ʽ�����������ǻ�����������ڣ���ַ�����ṩ���������治ͬ��ֵ����ڡ�QQ��һ���й��������Ĺ�̥������ӵ��������������ʵ���罻���磬������Ϊ��û�й㷺�����ֵ�������ڻ�������ڣ��������Ϊ�û����ʻ���������Ҫ����Ҳ��Ϊ��ڣ�������ϵͳ��������������������ڡ�',"
				+ "'2011-08-08',"
				+ "'http://www.williamlong.info/archives/2766.html',"
				+ "'0', '0')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public Cursor query() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_CATEGORY, null, null, null, null, null, null);

	}

	public Cursor queryDes(String category) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_CATEGORY, new String[] { RSS_DESCRIPTION },
				RSS_CATEGORY + "='" + category + "'", null, null, null, null);
	}

	public Cursor queryCategory() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_CATEGORY, new String[] { RSS_CATEGORY }, null,
				null, null, null, null);

	}

	public Cursor queryFeed() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_URL + "!='" + "'", null, null,
				null, null);

	}

	public Cursor queryWithUrl(String url) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, new String[] { RSS_CATEGORY, RSS_URL },
				RSS_URL + "='" + url + "'", null, RSS_URL, null, null);
	}

	public Cursor queryWithCateChannel(String cate, String channel) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + cate + "'"
				+ " AND " + CHANNEL_TITLE + "='" + channel + "'", null, null,
				null, null);
	}

	public Cursor queryWithCategory(String category) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, new String[] { RSS_CATEGORY,
				CHANNEL_TITLE, RSS_URL }, RSS_CATEGORY + "='" + category + "'",
				null, RSS_URL, null, null);
	}

	public Cursor queryNotOnServer(int onserver) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, new String[] { RSS_CATEGORY,
				CHANNEL_TITLE, RSS_URL }, ISONSERVER + "='" + onserver + "'",
				null, RSS_URL, null, null);
	}

	public Cursor queryWithUrlAndCategory(String category, String url) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + category + "'"
				+ " AND " + RSS_URL + "='" + url + "'", null, null, null, null);
	}

	public Cursor queryItem(String category, String link) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + category + "'"
				+ " AND " + ITEM_LINK + "='" + link + "'", null, null, null,
				null);
	}

	public Cursor queryWithCU(String cate, String url) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + cate + "'"
				+ " AND " + RSS_URL + "='" + url + "'", null, null, null, null);
	}

	public Cursor queryWithCUF(String cate, String url, int flag) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + cate + "'"
				+ " AND " + RSS_URL + "='" + url + "'" + " AND " + ISREAD
				+ "='" + flag + "'", null, null, null, null);
	}

	public Cursor queryWithCL(String cate, String link, int flag) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + cate + "'"
				+ " AND " + ITEM_LINK + "='" + link + "'" + " AND " + ISREAD
				+ "='" + flag + "'", null, null, null, null);
	}

	public Cursor queryWithCFL(String cate, String feedUrl, String link) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + cate + "'"
				+ " AND " + RSS_URL + "='" + feedUrl + "'" + " AND "
				+ ITEM_LINK + "='" + link + "'", null, null, null, null);
	}

	public void insertCategory(String category, String description) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RSS_CATEGORY, category);
		values.put(RSS_DESCRIPTION, description);
		db.insert(TAB_CATEGORY, null, values);
		db.close();

	}

	public void insertRssInfo(String category, String url, String channleTitle,
			String itemTile, String itemDes, String itemPub, String itemLink,
			int flag1, int flag2) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RSS_CATEGORY, category);
		values.put(RSS_URL, url);
		values.put(CHANNEL_TITLE, channleTitle);
		values.put(ITEM_TITLE, itemTile);
		values.put(ITEM_DES, itemDes);
		values.put(ITEM_PUBDATE, itemPub);
		values.put(ITEM_LINK, itemLink);
		values.put(ISREAD, flag1);
		values.put(ISONSERVER, flag2);
		db.insert(TAB_RSSINFO, null, values);
		db.close();

	}

	public void updateRssCate(String newCategory, String newDes,
			String oldCategory) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RSS_CATEGORY, newCategory);
		values.put(RSS_DESCRIPTION, newDes);
		db.update(TAB_CATEGORY, values,
				RSS_CATEGORY + "='" + oldCategory + "'", null);
	}

	public void updateRssDes(String newDes, String oldCategory) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RSS_DESCRIPTION, newDes);
		db.update(TAB_CATEGORY, values,
				RSS_CATEGORY + "='" + oldCategory + "'", null);
	}

	public void updateRssInfo(String newCategory, String oldCategory) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RSS_CATEGORY, newCategory);
		db.update(TAB_RSSINFO, values, RSS_CATEGORY + "='" + oldCategory + "'",
				null);
	}

	public void updateISREAED(String category, String url, String link) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ISREAD, RssReaderConstant.ISREAD);
		if (link == null) {
			db.update(TAB_RSSINFO, values, RSS_CATEGORY + "='" + category + "'"
					+ " AND " + RSS_URL + "='" + url + "'", null);
		} else {
			db.update(TAB_RSSINFO, values, RSS_CATEGORY + "='" + category + "'"
					+ " AND " + RSS_URL + "='" + url + "'" + " AND "
					+ ITEM_LINK + "='" + link + "'", null);
		}

	}

	public void deleteCategory(String category) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TAB_CATEGORY, RSS_CATEGORY + "='" + category + "'", null);
		db.delete(TAB_RSSINFO, RSS_CATEGORY + "='" + category + "'", null);
		db.close();

	}

	public void deleteRssUrl(String category, String url) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TAB_RSSINFO, RSS_CATEGORY + "='" + category + "'" + " AND "
				+ RSS_URL + "='" + url + "'", null);
		db.close();
	}

	public void deleteChannel(String feedUrl) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TAB_RSSINFO, RSS_URL + "='" + feedUrl + "'", null);
		db.close();
	}
}