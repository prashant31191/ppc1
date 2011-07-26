package com.smit.DeskView.tvlive;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.smit.DeskView.commonclass.AlwaysMarqueeTextView;
import com.smit.DeskView.commonclass.TvLiveChannelParse.ItemTvInfo;
import com.smit.EasyLauncher.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TvProgramListActivity extends Activity implements OnClickListener {
	Button mMonday, mTuesday, mWendesday, mThursday, mFriday, mSaturday,
			mSunday;
	ListView mProgramListView;
	TextView mNoProgramListView;
	ProgramListAdapter adapter;
	String path;
	String tvname;
	LinkedList<ItemProgramList> allprogram = new LinkedList<ItemProgramList>();
	int CurWeek=0;
	boolean isExistprogram=false;
	
	private final static String Tag = "TvProgramListActivity";

	// String string=getIntent().getExtras().getString("media");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tv_programlist);
		setupView();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		path = getIntent().getExtras().getString("tvprogramfilepath");
		tvname = getIntent().getExtras().getString("tvname");
		
		allprogram.clear();
		
		isExistprogram=getProgramList();
		if (isExistprogram) {
			mMonday.setText(getResources().getString(R.string.monday)+"\n"+allprogram.get(0).item_data);
			mTuesday.setText(getResources().getString(R.string.tuesday)+"\n"+allprogram.get(1).item_data);
			mWendesday.setText(getResources().getString(R.string.wednesday)+"\n"+allprogram.get(2).item_data);
			mThursday.setText(getResources().getString(R.string.thursday)+"\n"+allprogram.get(3).item_data);
			mFriday.setText(getResources().getString(R.string.friday)+"\n"+allprogram.get(4).item_data);
			mSaturday.setText(getResources().getString(R.string.saturday)+"\n"+allprogram.get(5).item_data);
			mSunday.setText(getResources().getString(R.string.sunday)+"\n"+allprogram.get(6).item_data);
		}	
		
		CurWeek=getCurWeek();
		View weekView=null;
		switch (CurWeek) {
		case 0:{
			weekView=mMonday;
			break;
			}
		case 1:{
			weekView=mTuesday;
			break;
		}
		case 2:{
			weekView=mWendesday;
			break;
		}
		case 3:{
			weekView=mThursday;
			break;
		}
		case 4:{
			weekView=mFriday;
			break;
		}
		case 5:{
			weekView=mSaturday;
			break;
		}
		case 6:{
			weekView=mSunday;
			break;
		}
		default:
			break;
		}
		onClick(weekView);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0 == mMonday) {
			mMonday.setSelected(true);

			mTuesday.setSelected(false);
			mWendesday.setSelected(false);
			mThursday.setSelected(false);
			mFriday.setSelected(false);
			mSaturday.setSelected(false);
			mSunday.setSelected(false);
			
			CurWeek=0;
			showProgramList();
		} else if (arg0 == mTuesday) {
			mTuesday.setSelected(true);

			mMonday.setSelected(false);
			mWendesday.setSelected(false);
			mThursday.setSelected(false);
			mFriday.setSelected(false);
			mSaturday.setSelected(false);
			mSunday.setSelected(false);
			
			CurWeek=1;
			showProgramList();

		} else if (arg0 == mWendesday) {
			mWendesday.setSelected(true);

			mMonday.setSelected(false);
			mTuesday.setSelected(false);
			mThursday.setSelected(false);
			mFriday.setSelected(false);
			mSaturday.setSelected(false);
			mSunday.setSelected(false);
			
			CurWeek=2;
			showProgramList();
		} else if (arg0 == mThursday) {
			mThursday.setSelected(true);

			mMonday.setSelected(false);
			mTuesday.setSelected(false);
			mWendesday.setSelected(false);
			mFriday.setSelected(false);
			mSaturday.setSelected(false);
			mSunday.setSelected(false);
			
			CurWeek=3;
			showProgramList();
		} else if (arg0 == mFriday) {
			mFriday.setSelected(true);

			mMonday.setSelected(false);
			mTuesday.setSelected(false);
			mWendesday.setSelected(false);
			mThursday.setSelected(false);
			mSaturday.setSelected(false);
			mSunday.setSelected(false);
			
			CurWeek=4;
			showProgramList();
		} else if (arg0 == mSaturday) {
			mSaturday.setSelected(true);

			mMonday.setSelected(false);
			mTuesday.setSelected(false);
			mWendesday.setSelected(false);
			mThursday.setSelected(false);
			mFriday.setSelected(false);
			mSunday.setSelected(false);
			
			CurWeek=5;
			showProgramList();
		} else if (arg0 == mSunday) {
			mSunday.setSelected(true);

			mMonday.setSelected(false);
			mTuesday.setSelected(false);
			mWendesday.setSelected(false);
			mThursday.setSelected(false);
			mFriday.setSelected(false);
			mSaturday.setSelected(false);
			
			CurWeek=6;
			showProgramList();
		}

	}
	
	void showProgramList(){

		if(isExistprogram&&CurWeek<=allprogram.size()){
			mProgramListView.setVisibility(View.VISIBLE);
			mNoProgramListView.setVisibility(View.GONE);
		    adapter=new ProgramListAdapter(this);
			mProgramListView.setAdapter(adapter);
			
			
		}else {
			mNoProgramListView.setVisibility(View.VISIBLE);
			mProgramListView.setVisibility(View.GONE);
			mNoProgramListView.setText(R.string.noprogram);
		}
	}

	private void setupView() {
		mMonday = (Button) findViewById(R.id.monday);
		mMonday.setOnClickListener(this);
		mTuesday = (Button) findViewById(R.id.tuesday);
		mTuesday.setOnClickListener(this);
		mWendesday = (Button) findViewById(R.id.wednesday);
		mWendesday.setOnClickListener(this);
		mThursday = (Button) findViewById(R.id.thursday);
		mThursday.setOnClickListener(this);
		mFriday = (Button) findViewById(R.id.friday);
		mFriday.setOnClickListener(this);
		mSaturday = (Button) findViewById(R.id.saturday);
		mSaturday.setOnClickListener(this);
		mSunday = (Button) findViewById(R.id.sunday);
		mSunday.setOnClickListener(this);
		
		mProgramListView=(ListView)findViewById(R.id.programlist);
		mNoProgramListView=(TextView)findViewById(R.id.noprogramlist);
	}
	
	public int getCurWeek(){
		Calendar calendar = Calendar.getInstance();
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		if (week>=2) {
			week-=2;
		}else if (week==1) {
			week=6;
		}
		return week;
	}

	public String GetCurData() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String string = null;
		string = String.format("%d/%02d/%02d", (int) (year % 100), month,
				day);
		return string;
	}
	
	// 取节目信息
	public boolean getProgramList() {
		boolean nRet=false;
		do {
			try {
				
				InputStream is = null;
				byte[] data = null;
				String str = null;
				File file = new File(path);
				if (!file.exists()) {
					break;
				}
				int length = (int) file.length() + 10;
				data = new byte[length];
				is = new BufferedInputStream(new FileInputStream(file));
				while (is.read(data) != -1)
					;
				is.close();

				str = new String(data);

				ByteArrayInputStream stream = new ByteArrayInputStream(
						str.getBytes());
				InputSource mInputSource = new InputSource(stream);
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dbuilder = dbf.newDocumentBuilder();
				Document doc = dbuilder.parse(mInputSource);


				NodeList n = doc.getElementsByTagName("entry");
				int itemcount = n.getLength();
				if (itemcount <= 0) {
					break;
				}
				for (int i = 0; i < itemcount; i++) {// 所有entry
					ItemProgramList curList = new ItemProgramList();

					Node item = n.item(i);
					if (!item.hasChildNodes()) {
						continue;
					}

					NamedNodeMap Attributes = item.getAttributes();
					Node timeNode = Attributes.getNamedItem("date");
					String timeValue = timeNode.getNodeValue();
					curList.item_data = timeValue;
					Node weekNode = Attributes.getNamedItem("day");
					String weekValue = weekNode.getNodeValue();
					curList.item_week = weekValue;
					

					NodeList nodeList = item.getChildNodes();
					int len = nodeList.getLength();
					if (len <= 0) {
						continue;
					}
					for (int j = 0; j < len; j++) { // item
						Node tempNodeFront = nodeList.item(j);
						
						int frontTime = 0;
						String programString = null;

						NodeList List = tempNodeFront.getChildNodes();
						for (int k = 0; k < List.getLength(); k++) {// item
																	// child
							Node tmpnode;
							Node tempNode = List.item(k);
							String tempStr = tempNode.getNodeName();
							if (tempStr.equals("time")) {
								tmpnode = tempNode.getChildNodes().item(0);
								if (tmpnode != null) {
									curList.item_time.add(tmpnode.getNodeValue());
									//String[] lunars = tmpnode.getNodeValue().split(":");
									//frontTime = Integer.parseInt(lunars[0])* 60 + Integer.parseInt(lunars[1]);
								} else {

								}

							} else if (tempStr.equals("program")) {
								curList.item_program.add(tempNode.getChildNodes().item(0).getNodeValue());
								//programString = tempNode.getChildNodes().item(0).getNodeValue();
							}
						}

					}					
					allprogram.add(curList);
				}
				
				nRet=true;
			} catch (Exception e) {
				Log.e(Tag, "======" + e.toString() + "======");
			}
		} while (false);

		return nRet;
	}

	public class ProgramListAdapter extends BaseAdapter {
		Context mContext;
		
		public ProgramListAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext=context;
		}

		public int getCount() {
			return allprogram.get(CurWeek).item_time.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			TextView program_time, program_tv;

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.tv_programlist_item, null);		
			}

			ItemProgramList curItem = allprogram.get(CurWeek);
			program_time = (TextView) convertView.findViewById(R.id.tv_program_time);
			program_time.setText(curItem.item_time.get(position));

			program_tv = (TextView) convertView.findViewById(R.id.tv_program_program);
			program_tv.setText(curItem.item_program.get(position));

			return convertView;
		}
	}

	// 一天节目信息
	public class ItemProgramList {
		public LinkedList<String> item_time = null;
		public LinkedList<String> item_program = null;
		public String item_data = null;
		public String item_week = null;

		public ItemProgramList() {
			item_time = new LinkedList<String>();
			item_program = new LinkedList<String>();
		}

	}
}
