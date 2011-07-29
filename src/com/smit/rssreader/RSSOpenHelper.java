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
	public static final String ISONSERVER = "onserver"; // "1"表示在本地服务器上

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

	public Cursor queryWithUrl(String url) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, new String[] { RSS_CATEGORY }, RSS_URL
				+ "='" + url + "'", null, RSS_CATEGORY, null, null);
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

	public Cursor queryNotOnServer(String category, int flag) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, new String[] { RSS_CATEGORY,
				CHANNEL_TITLE, RSS_URL }, RSS_CATEGORY + "='" + category + "'"
				+ " AND " + ISONSERVER + "='" + flag + "'", null, RSS_URL,
				null, null);
	}

	public Cursor queryWithUrlAndCategory(String category, String url) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + category + "'"
				+ " AND " + RSS_URL + "='" + url + "'", null, null, null, null);
	}

	public Cursor queryItem(String category, String channel, String itemTitle) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_RSSINFO, null, RSS_CATEGORY + "='" + category + "'"
				+ " AND " + CHANNEL_TITLE + "='" + channel + "'" + " AND "
				+ ITEM_TITLE + "='" + itemTitle + "'", null, null, null, null);
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
		values.put(ISREAD, 1);
		db.update(TAB_RSSINFO, values, RSS_CATEGORY + "='" + category + "'"
				+ " AND " + RSS_URL + "='" + url + "'" + " AND " + ITEM_LINK
				+ "='" + link + "'", null);
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

	public void deleteChannel(String cate, String channel) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TAB_RSSINFO, RSS_CATEGORY + "='" + cate + "'" + " AND "
				+ CHANNEL_TITLE + "='" + channel + "'", null);
		db.close();
	}
}
