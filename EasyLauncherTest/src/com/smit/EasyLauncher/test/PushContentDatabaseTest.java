package com.smit.EasyLauncher.test;

import java.util.Iterator;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.smit.EasyLauncher.R;
import com.openims.model.pushService.PushContent;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public class PushContentDatabaseTest extends AndroidTestCase {
	
	PushContentDB db = null;
	String uread = null;
	String read = null;
	
	private static final String LOGTAG = LogUtil.makeLogTag(PushContentDatabaseTest.class);
	private static final String TAG = LogUtil.makeTag(PushContentDatabaseTest.class);
	
	protected void setUp() throws Exception {
		super.setUp();
		db = new PushContentDB(getContext());
		uread = getContext().getResources().getString(R.string.pushcontent_uread);
		read = getContext().getResources().getString(R.string.pushcontent_read);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testUpdateData(){
		db.updateStatus(1, read);
	}
	public void testRecreate(){
		db.reCreateTable();
	}
	public void testData(){
		
		for(int i=0;i<1000;i++){
			PushContent pc = new PushContent();
			pc.setSize("100K"+String.valueOf(i));
			pc.setContent("数据库测试 " + i);
			pc.setLocalPath("sdcard/img");
			pc.setTime("2011-4-1 11:22:55");
			pc.setType(PushServiceUtil.DEFAULTID_PICTURE);			
			pc.setStatus(uread);
			pc.setFlag("111");
			
			db.insertItem(pc);
		}
	}
	
	public void testInsertUseData(){
		testRecreate();
		final String baseUrl = "http://192.168.0.158:8080";
		// insert text
		saveContent(PushServiceUtil.DEFAULTID_TEXT,
				"这是文本","我今天要坐车去广州，时间是9:50，车程M124");
		saveContent(PushServiceUtil.DEFAULTID_TEXT,
				"这是long文本","我今天要坐车去广州，时间是9:50，车程M124.目的是和老友小q，见面，"
				+ "跟他分享我最近这段时间的生活情况");
		saveContent(PushServiceUtil.DEFAULTID_TEXT,
				"这是longlong文本,这是longlong文本,这是longlong文本,这是longlong文本,这是longlong文本",
				"我今天要坐车去广州，时间是9:50，车程M124.目的是和老友小q，见面，"
				+ "跟他分享我最近这段时间的生活情况.long long long long long long 非常的常，溢出啦，"
				+ "溢出啦,溢出啦,溢出啦,溢出啦,溢出啦,溢出啦");
		
		// insert music
		saveContent(PushServiceUtil.DEFAULTID_AUDIO,
				"What We Talkin' About",baseUrl + "/pring/download/What We Talkin' About.mp3");
		saveContent(PushServiceUtil.DEFAULTID_AUDIO,
				"Real As It Gets",baseUrl + "/pring/download/Real As It Gets (feat. Young Jeez.mp3");
		saveContent(PushServiceUtil.DEFAULTID_AUDIO,
				"nice song",baseUrl + "/pring/download/a.wav");
		saveContent(PushServiceUtil.DEFAULTID_AUDIO,
				"nice song",baseUrl + "/pring/download/young.mp3");
		
		// insert picture
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"珍藏的记忆",baseUrl + "/pring/download/img1.png");
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"珍藏的记忆，珍藏的记忆",baseUrl + "/pring/download/img2.png");
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"珍藏的记忆3",baseUrl + "/pring/download/img4.png");
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"昨天",baseUrl + "/pring/download/img3.png");
		// video
		saveContent(PushServiceUtil.DEFAULTID_VIDEO,
				"戏曲联唱",baseUrl + "/pring/download/戏曲联唱.mp4");
		saveContent(PushServiceUtil.DEFAULTID_VIDEO,
				"魔法高尔夫4PV高清晰HD画质",baseUrl + "/pring/download/glof.flv");
		saveContent(PushServiceUtil.DEFAULTID_VIDEO,
				"sing",baseUrl + "/pring/download/sing.mp4");
		
		// web address
		saveContent(PushServiceUtil.DEFAULTID_URL,
				"国微","http://www.smit.com.cn");
		saveContent(PushServiceUtil.DEFAULTID_URL,
				"赶集","http://www.ganji.com");
		
		// story
		saveContent(PushServiceUtil.DEFAULTID_STORY,
				"无畏的希望","对这个问题我丝毫不感到陌生。早在几年前我刚到芝加哥，在一个低收入的社区工作时就遇到过类似问题。这不仅是对政界的嘲讽，还是对公众生活的挖苦。助长这种愤世嫉俗心态的是那些屡见不鲜的廉价承诺和空头支票--至少在我想代表的南部地区是这样。我常笑着点头回答，我能理解这种怀疑的态度，但不能否认，曾经并且一直以来都存在另外一种政治传承，它贯穿了从建国之处到民权运动的辉煌时期；它基于一个单纯的信念――我们彼此之间攸息相关；它令我们彼此之间团结压倒分裂。如果有足够多的人民信仰这个传统并付之行动，即使我们不能解决所有问题，也终归会有所作为。");
		saveContent(PushServiceUtil.DEFAULTID_STORY,
				"无畏的希望2","从我开始参加政界竞选，至今已近十年。那时，我才35岁，从哈佛法学院刚毕业四年就结了婚，迫不及待地开始了新的生活。正好伊利诺伊州立法委员会有个空位，一些朋友就建议我去竞选。他们觉得我作为一名民权事务律师，又是社区负责人，社交广泛，候选人的位子唾手可得。和妻子商量后，我参加了竞选。像所有第一次参加竞选的人一样，我不放弃任何一次谈话的机会。我去过街区俱乐部的集会，参加教会活动，还去过美容院和理发店。如果见到街头拐角有两位交谈的人，我便会走过马路，递给他们竞选宣传册。无论我到哪里，我常常会遇到类似的两个提问。");
	
	}
	
	private void saveContent(String type,String title,String message){
    	PushContent push = new PushContent();
    	String read = getContext().getResources().getString(R.string.pushcontent_uread);
    	push.setStatus(read);
    	push.setType(type);
    	push.setContent(message);
    	push.setFlag(title);
    	push.setSize("10K");
    	PushContentDB pushDB = new PushContentDB(getContext());
    	pushDB.insertItem(push);
	}
	
	public void testReadData(){
		List<PushContent> list = null;
		
		int num = 10;		
		list = db.queryItems(0,num);
		printListPushContent(list);
		list = null;
	}
	
	public void testDeleteData() {
		this.assertEquals(db.deleteItem("5"), true);
	}
	
	private void printListPushContent(List<PushContent> list){
		Iterator<PushContent> it = list.iterator();
		while(it.hasNext()){
			PushContent pc = it.next();
			Log.d(LOGTAG,TAG+pc.toString());
			System.out.println(pc.toString());
		}
	}

}
