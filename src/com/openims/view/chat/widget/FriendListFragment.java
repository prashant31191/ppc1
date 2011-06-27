package com.openims.view.chat.widget;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.openims.R;
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.MultiChatActivity;
import com.openims.view.chat.OnAvater;

public class FriendListFragment extends Fragment 
		implements OnChildClickListener {

	private static final String TAG = LogUtil
					.makeLogTag(FriendListFragment.class);
	private static final String PRE = "FriendListFragment--";

	private Activity mActivity;
	private RosterExpandableListAdapter mAdapter;
	private ExpandableListView mFriendListView;
	
	private ColorFilter mGreyColorFilter = new ColorMatrixColorFilter( new ColorMatrix(new float[]{0.5f,0.5f,0.5f,0,0, 
            0.5f,0.5f,0.5f,0,0, 
            0.5f,0.5f,0.5f,0,0, 
            0,0,0,1,0,0, 
            0,0,0,0,1,0 
            })); 
	
	private OnAvater onAvater;
    
    private String mUsername;
    private Bitmap mDefaultHead;
    
    private int columnIndexJId = 0;
    private int columnIndexGroup = 0;
    private int columnIndexId = 0;
    private int indexUserName = 0;
    private int indexPresence = 0;	
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		Log.d(TAG, PRE + "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, PRE + "onCreate");	
		SharedPreferences sharedPrefs = mActivity.getSharedPreferences(
				PushServiceUtil.SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		mUsername = sharedPrefs.getString(PushServiceUtil.XMPP_USERNAME, null)+"@smit";
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.im_friend_list_fragment, 
				container, false);
		mFriendListView = (ExpandableListView)v.findViewById(R.id.listview_friend);		
		
        mAdapter = new RosterExpandableListAdapter(mActivity); 
        
        RosterDataBase roster = new RosterDataBase(mActivity,mUsername);
        Cursor cursor = roster.queryAll();
        columnIndexJId = cursor.getColumnIndex(RosterDataBase.JID);
		columnIndexGroup = cursor.getColumnIndex(RosterDataBase.GROUP_NAME);
		columnIndexId = cursor.getColumnIndex(RosterDataBase.ID);
		indexUserName = cursor.getColumnIndex(RosterDataBase.USER_NAME);
		indexPresence = cursor.getColumnIndex(RosterDataBase.PRESENCE);		
	
		cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
        	
        	RosterEntry entry = new RosterEntry();
        	entry.groupName = cursor.getString(columnIndexGroup);
        	entry.Jid = cursor.getString(columnIndexJId);
        	entry.userName = cursor.getString(indexUserName);
        	entry.id = cursor.getInt(columnIndexId);
        	String presence = cursor.getString(indexPresence);
        	if(presence.equals(Presence.Type.available.name())){
        		entry.isOnline = true;
        	}else{
        		entry.isOnline = false;
        	}
        	mAdapter.addItem(entry);
        	cursor.moveToNext();
        }
        
        // Set up our adapter
        mFriendListView.setAdapter(mAdapter);		
        mFriendListView.setGroupIndicator(null);	
        mFriendListView.setOnChildClickListener(this);
		
		return v;
	}
	
	public void reRoadData(){

		mAdapter.initData(); 
        
        RosterDataBase roster = new RosterDataBase(mActivity,mUsername);
        Cursor cursor = roster.queryAll();
        	
	
		cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
        	
        	RosterEntry entry = new RosterEntry();
        	entry.groupName = cursor.getString(columnIndexGroup);
        	entry.Jid = cursor.getString(columnIndexJId);
        	entry.userName = cursor.getString(indexUserName);
        	entry.id = cursor.getInt(columnIndexId);
        	String presence = cursor.getString(indexPresence);
        	if(presence.equals(Presence.Type.available.name())){
        		entry.isOnline = true;
        	}else{
        		entry.isOnline = false;
        	}
        	mAdapter.addItem(entry);
        	cursor.moveToNext();
        }
        
        mAdapter.notifyDataSetChanged();        
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
		
		
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, PRE + "onStart");
		
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, PRE + "onResume");
	}	

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, PRE + "onPause");
	}

	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, PRE + "onStop");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, PRE + "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach");
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		RosterEntry entry = mAdapter.getChild(groupPosition, childPosition);
		
		RosterDataBase roster = new RosterDataBase(mActivity, mUsername);
		roster.updateColumn(entry.Jid, RosterDataBase.NEW_MSG_TIME, 
				String.valueOf(System.currentTimeMillis()));
		roster.close();
		
		Intent intent = new Intent();
		intent.putExtra(MultiChatActivity.ACCOUNT_JID, entry.Jid);
		intent.setClassName("com.openims", "com.openims.view.chat.MultiChatActivity");
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);	
		return false;
	}
	
	public class RosterGroup {
		public RosterGroup(String groupName){
			this.groupName = groupName;
		}
		public String groupName;
		@Override
		public boolean equals(Object o) {
			if(o instanceof RosterGroup){
				RosterGroup r = (RosterGroup)o;
				return groupName.equals(r.groupName);
			}
			return false;
		}
		
		
	}
	public class RosterEntry {
		public Integer id;
		public String groupName;
		public String Jid;
		public String userName;
		public boolean isOnline;
	}
	
	 public class RosterExpandableListAdapter extends BaseExpandableListAdapter
	 						implements OnAvater.OnAvaterListener{
	        	        
	        private ArrayList<RosterGroup> groups = new ArrayList<RosterGroup>();
	        private ArrayList<ArrayList<RosterEntry>> children = new ArrayList<ArrayList<RosterEntry>>();
	        
	        private LayoutInflater mInflater;
	        
	        
	        public RosterExpandableListAdapter(Context context, ArrayList<RosterGroup> groups,
	        		ArrayList<ArrayList<RosterEntry>> children){
	        	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	this.groups = groups;
	            this.children = children;
	        }
	        public RosterExpandableListAdapter(Context context){
	        	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	
	        }
	        public void initData(){
	        	groups = new ArrayList<RosterGroup>();
		        children = new ArrayList<ArrayList<RosterEntry>>();
	        }
	        public void addItem(RosterEntry entry) {
	        	RosterGroup g = new RosterGroup(entry.groupName);
	            if (!groups.contains(g)) {
	                groups.add(g);
	            }
	            int index = groups.indexOf(g);
	            if (children.size() < index + 1) {
	                children.add(new ArrayList<RosterEntry>());
	            }
	            children.get(index).add(entry);
	        }
	        
	       

	        public Object getGroup(int groupPosition) {
	            return groups.get(groupPosition);
	        }

	        public int getGroupCount() {
	            return groups.size();
	        }

	        public long getGroupId(int groupPosition) {
	            return groupPosition;
	        }

	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
	                ViewGroup parent) {
	            View view;
	            if(convertView == null){
	            	view = mInflater.inflate(R.layout.im_friend_item_group, null);
	            } else {
	            	view = convertView;
	            }
	            TextView groupName = (TextView)view.findViewById(R.id.groupName);
	            groupName.setText(groups.get(groupPosition).groupName);
	            ImageView btn = (ImageView)view.findViewById(R.id.icon_expand);
	            if(isExpanded){
	            	btn.setSelected(true);
	            }else{
	            	btn.setSelected(false);
	            }
	            return view;
	        }

	        public RosterEntry getChild(int groupPosition, int childPosition) {
	            return children.get(groupPosition).get(childPosition);
	        }

	        public long getChildId(int groupPosition, int childPosition) {
	            return children.get(groupPosition).get(childPosition).id;
	        }

	        public int getChildrenCount(int groupPosition) {
	            return children.get(groupPosition).size();
	        }	       
	        
	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
	                View convertView, ViewGroup parent) {
	        	View view;
	        	if(convertView != null){
	        		view = convertView;
	        	}else{
	        		view = mInflater.inflate(R.layout.im_friend_item_entry, null);
	        	}
	        	RosterEntry entry = getChild(groupPosition, childPosition);
	            TextView textView = (TextView)view.findViewById(R.id.username);
	            textView.setText(entry.Jid);
	            ImageView head = (ImageView)view.findViewById(R.id.imageView);
	            Drawable avater = onAvater.getAvater(entry.Jid, this);
	            
	            head.setImageDrawable(avater);
	            
	            if(entry.isOnline){	            	
	            	head.setColorFilter(null);
	            }else{	            	
	            	head.setColorFilter(mGreyColorFilter);
	            }
	            //head.setImageBitmap(mDefaultHead);
	            return view;
	        }
	        public boolean isChildSelectable(int groupPosition, int childPosition) {
	            return true;
	        }

	        public boolean hasStableIds() {
	            return true;
	        }
	        
	        @Override
	    	public void avater(String avaterJid, Drawable avater) {
	    		Log.e(TAG, PRE + avaterJid);
	    		this.notifyDataSetChanged();
	    	}

	    }
	
	
	public void setOnAvater(OnAvater onAvater){
		this.onAvater = onAvater;
	}

	
	
}
