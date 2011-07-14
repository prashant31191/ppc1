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
			pc.setContent("���ݿ���� " + i);
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
				"�����ı�","�ҽ���Ҫ����ȥ���ݣ�ʱ����9:50������M124");
		saveContent(PushServiceUtil.DEFAULTID_TEXT,
				"����long�ı�","�ҽ���Ҫ����ȥ���ݣ�ʱ����9:50������M124.Ŀ���Ǻ�����Сq�����棬"
				+ "����������������ʱ����������");
		saveContent(PushServiceUtil.DEFAULTID_TEXT,
				"����longlong�ı�,����longlong�ı�,����longlong�ı�,����longlong�ı�,����longlong�ı�",
				"�ҽ���Ҫ����ȥ���ݣ�ʱ����9:50������M124.Ŀ���Ǻ�����Сq�����棬"
				+ "����������������ʱ����������.long long long long long long �ǳ��ĳ����������"
				+ "�����,�����,�����,�����,�����,�����");
		
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
				"��صļ���",baseUrl + "/pring/download/img1.png");
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"��صļ��䣬��صļ���",baseUrl + "/pring/download/img2.png");
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"��صļ���3",baseUrl + "/pring/download/img4.png");
		saveContent(PushServiceUtil.DEFAULTID_PICTURE,
				"����",baseUrl + "/pring/download/img3.png");
		// video
		saveContent(PushServiceUtil.DEFAULTID_VIDEO,
				"Ϸ������",baseUrl + "/pring/download/Ϸ������.mp4");
		saveContent(PushServiceUtil.DEFAULTID_VIDEO,
				"ħ���߶���4PV������HD����",baseUrl + "/pring/download/glof.flv");
		saveContent(PushServiceUtil.DEFAULTID_VIDEO,
				"sing",baseUrl + "/pring/download/sing.mp4");
		
		// web address
		saveContent(PushServiceUtil.DEFAULTID_URL,
				"��΢","http://www.smit.com.cn");
		saveContent(PushServiceUtil.DEFAULTID_URL,
				"�ϼ�","http://www.ganji.com");
		
		// story
		saveContent(PushServiceUtil.DEFAULTID_STORY,
				"��η��ϣ��","�����������˿�����е�İ�������ڼ���ǰ�Ҹյ�֥�Ӹ磬��һ�����������������ʱ���������������⡣�ⲻ���Ƕ�����ĳ������ǶԹ���������ڿࡣ�������ַ���������̬������Щ�ż����ʵ����۳�ŵ�Ϳ�ͷ֧Ʊ--���������������ϲ��������������ҳ�Ц�ŵ�ͷ�ش�����������ֻ��ɵ�̬�ȣ������ܷ��ϣ���������һֱ��������������һ�����δ��У����ᴩ�˴ӽ���֮������Ȩ�˶��ĻԻ�ʱ�ڣ�������һ������������D�D���Ǳ˴�֮����Ϣ��أ��������Ǳ˴�֮���Ž�ѹ�����ѡ�������㹻����������������ͳ����֮�ж�����ʹ���ǲ��ܽ���������⣬Ҳ�չ��������Ϊ��");
		saveContent(PushServiceUtil.DEFAULTID_STORY,
				"��η��ϣ��2","���ҿ�ʼ�μ����羺ѡ�������ѽ�ʮ�ꡣ��ʱ���Ҳ�35�꣬�ӹ���ѧԺ�ձ�ҵ����ͽ��˻飬�Ȳ������ؿ�ʼ���µ������������ŵ��������ίԱ���и���λ��һЩ���Ѿͽ�����ȥ��ѡ�����Ǿ�������Ϊһ����Ȩ������ʦ���������������ˣ��罻�㷺����ѡ�˵�λ�����ֿɵá��������������Ҳμ��˾�ѡ�������е�һ�βμӾ�ѡ����һ�����Ҳ������κ�һ��̸���Ļ��ᡣ��ȥ���������ֲ��ļ��ᣬ�μӽ̻�����ȥ������Ժ�����ꡣ���������ͷ�ս�����λ��̸���ˣ��ұ���߹���·���ݸ����Ǿ�ѡ�����ᡣ�����ҵ�����ҳ������������Ƶ��������ʡ�");
	
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
