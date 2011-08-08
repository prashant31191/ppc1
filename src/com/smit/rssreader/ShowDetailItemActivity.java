package com.smit.rssreader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smit.EasyLauncher.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowDetailItemActivity extends Activity {
	private RSSOpenHelper myRssOpenHelper = new RSSOpenHelper(this);
	private HistoryOpenHelper history = new HistoryOpenHelper(this);
	private String channelTitle = null;
	private String category = null;
	private String itemTitle = null;
	private String itemLink = null;
	private String rssUrl = null ;
	private Cursor currentPoint = null ;
	private WebView webView = null;
    private TextView txtLink ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_show_detail_item);

		Intent startingIntent = getIntent();
		if (startingIntent != null) {
			Bundle bundle = startingIntent.getExtras();
			if (bundle != null) {
				channelTitle = bundle.getString("CHANNEL");
				category = bundle.getString("CATEGORY");
				itemTitle = bundle.getString("ITEMTITLE");
				rssUrl = bundle.getString("RSSURL");
				itemLink = bundle.getString("ITEMLINK");
				//setTitle(channelTitle);
			}
		}
        
		webView = (WebView) findViewById(R.id.itemdetail);
		txtLink =(TextView)findViewById(R.id.textview3);
		
		initializeDatasource(); // ��ʼ��ListBaseAdapter����Դ
		currentPoint = findCurrentPosition(category ,rssUrl);  //�ҵ���ǰItem��λ��
		
		webView.getSettings().setJavaScriptEnabled(true);
		txtLink.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webView.loadUrl(itemLink);
			}
		});
		
		LinearLayout layoutLast = (LinearLayout)findViewById(R.id.layout_last);
		LinearLayout layoutAddFavorite = (LinearLayout)findViewById(R.id.layout_addfavorite);
		LinearLayout layoutNext = (LinearLayout)findViewById(R.id.layout_next);
		
		layoutLast.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currentPoint != null){
					if(currentPoint.isFirst()){
						Toast.makeText(ShowDetailItemActivity.this, "�Ѿ��ǵ�һ���ˣ�", Toast.LENGTH_SHORT).show();
					}else{
						currentPoint.moveToPrevious();
						resetShow(currentPoint);
						myRssOpenHelper.updateISREAED(category, rssUrl,itemLink);
						
						//���͹㲥֪ͨRSSReaderActivity�����и���
						Intent i = new Intent(RssReaderConstant.READED_BROADCAST);
						sendBroadcast(i);
					}
				}
			}
		});
		
		layoutAddFavorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isInHistory(category,itemLink)){
					history.updateFavorite(category,itemLink, RssReaderConstant.INFAVORITE);
					Toast.makeText(ShowDetailItemActivity.this, "�Ѿ���ӵ��ղؼУ�", Toast.LENGTH_SHORT).show();
				}else{
					TextView tv =(TextView)findViewById(R.id.textview1);
					String title = tv.getText().toString();
					history.insertItem(category, title, itemLink, RssReaderConstant.INFAVORITE);
					Toast.makeText(ShowDetailItemActivity.this, "�Ѿ���ӵ��ղؼУ�", Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent(RssReaderConstant.ADDFAVORITE);
				sendBroadcast(intent);
			}
		});
		
		layoutNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currentPoint != null){
					if(currentPoint.isLast()){
						Toast.makeText(ShowDetailItemActivity.this, "�Ѿ������һ���ˣ�", Toast.LENGTH_SHORT).show();
					}else{
						currentPoint.moveToNext();
						resetShow(currentPoint);
						myRssOpenHelper.updateISREAED(category, rssUrl,itemLink);
						
						//���͹㲥֪ͨRSSReaderActivity�����и���
						Intent i = new Intent(RssReaderConstant.READED_BROADCAST);
						sendBroadcast(i);
					}
				}
			}
		});
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initializeDatasource() {
		Cursor c = myRssOpenHelper.queryItem(category,itemLink);
		if (c.moveToFirst()) {

			int desIndex = c.getColumnIndex(RSSOpenHelper.ITEM_DES);
			int pubIndex = c.getColumnIndex(RSSOpenHelper.ITEM_PUBDATE);
			String des = c.getString(desIndex);
			String pub = c.getString(pubIndex);
			
			TextView tv1 =(TextView)findViewById(R.id.textview1);
			tv1.setTextColor(Color.BLACK);
			tv1.setText(replaceBlank(itemTitle));
			
			TextView tv2 =(TextView)findViewById(R.id.textview2);
			tv2.setTextColor(Color.BLACK);
			tv2.setText(Html.fromHtml(replaceBlank(des)));
			
			txtLink.setText(replaceBlank(itemLink));
			
			TextView tv4 =(TextView)findViewById(R.id.textview4);
			tv4.setTextColor(Color.BLACK);
			tv4.setText("�������ڣ�"+replaceBlank(pub));
		}
		c.close();
	}

	public String replaceBlank(String s)    
	{    
	     Pattern p = Pattern.compile("\\s*|\t|\r|\n");    
	     Matcher m = p.matcher(s);    
	     return m.replaceAll("");    
	       
	} 
	
	private Cursor findCurrentPosition(String cate ,String url){
		Cursor cur = myRssOpenHelper.queryWithCU(cate, url);
		if(cur.moveToFirst()){
			int linkIndex = cur.getColumnIndex(RSSOpenHelper.ITEM_LINK);
			while(!cur.isAfterLast()){
				String link = cur.getString(linkIndex);
				if(link.equals(itemLink)){
					return cur ;
				}
				cur.moveToNext();
			}
			
		}
		cur.close();
		return null ;
	}
	
	private void resetShow(Cursor c){
		int linkIndex = c.getColumnIndex(RSSOpenHelper.ITEM_LINK);
		itemLink = c.getString(linkIndex);

		int desIndex = c.getColumnIndex(RSSOpenHelper.ITEM_DES);
		int pubIndex = c.getColumnIndex(RSSOpenHelper.ITEM_PUBDATE);
		int titIndex = c.getColumnIndex(RSSOpenHelper.ITEM_TITLE);
		String des = c.getString(desIndex);
		String pub = c.getString(pubIndex);
		String tit = c.getString(titIndex);
		
		TextView tv1 =(TextView)findViewById(R.id.textview1);
		tv1.setTextColor(Color.BLACK);
		tv1.setText(Html.fromHtml(replaceBlank(tit)));
		
		TextView tv2 =(TextView)findViewById(R.id.textview2);
		tv2.setTextColor(Color.BLACK);
		tv2.setText(Html.fromHtml(replaceBlank(des)));
		
		txtLink.setText(replaceBlank(itemLink));
		
		TextView tv4 =(TextView)findViewById(R.id.textview4);
		tv4.setTextColor(Color.BLACK);
		tv4.setText("�������ڣ�"+replaceBlank(pub));
	}
	
	private boolean isInHistory(String cate, String link){
		Cursor c = history.queryWithCH(cate,link);
		if(c.moveToFirst()){
			c.close();
			return true ;
		}
		c.close();
		return false ;
	}
}
