package com.openims.view.chat.widget;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView.OnChildClickListener;

import com.openims.model.MyApplication;
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.utility.Utility;
import com.openims.view.chat.MultiChatActivity;
import com.openims.view.chat.OnAvater;
import com.openims.view.chat.ProgressFragment;
import com.openims.widgets.DragDropListener;
import com.smit.EasyLauncher.R;

public class FriendListFragment extends Fragment 
		implements OnChildClickListener, DragDropListener,
					OnScrollListener, OnClickListener{

	private static final String TAG = LogUtil.makeLogTag(FriendListFragment.class);
	private static final String PRE = "FriendListFragment--";

	private Activity mActivity;
	private RosterExpandableListAdapter mAdapter;
	private FriendGroupListView mFriendListView;
	
	private OnAvater onAvater;
	private boolean mEditable = false;
    
    private String mAdminJid;
    
    private int columnIndexJId = 0;
    private int columnIndexGroup = 0;
    private int columnIndexId = 0;
    private int indexUserName = 0;
    private int indexPresence = 0;	
    
    private XMPPConnection xmppConnection;    

	private LinearLayout indicatorGroup = null;
	private int indicatorGroupId;
	private int indicatorGroupHeight;
	
	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {		
		super.onInflate(activity, attrs, savedInstanceState);
		
		TypedArray a = activity.obtainStyledAttributes(attrs,R.styleable.FriendListFragment);
		mEditable = a.getBoolean(R.styleable.FriendListFragment_editable, false);
	}

	
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
		
		MyApplication app = (MyApplication)mActivity.getApplication();
		mAdminJid = app.getAdminJid();
		xmppConnection = app.getConnection();
		
		indicatorGroupId = -1;
		indicatorGroupHeight = 0;	
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.im_friend_list_fragment, 
				container, false);
		mFriendListView = (FriendGroupListView)v.findViewById(R.id.listview_friend);
		mFriendListView.setEditable(mEditable);
		indicatorGroup = (LinearLayout)v.findViewById(R.id.indicatorGroup);
		indicatorGroup.setVisibility(View.INVISIBLE);
		
        mAdapter = new RosterExpandableListAdapter(mActivity); 
        inflater.inflate(mEditable?R.layout.im_friend_item_group_edit:
    		R.layout.im_friend_item_group, indicatorGroup, true);
        
        
        RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
        Cursor cursor = roster.queryAll();
        columnIndexJId = cursor.getColumnIndex(RosterDataBase.JID);
		columnIndexGroup = cursor.getColumnIndex(RosterDataBase.GROUP_NAME);
		columnIndexId = cursor.getColumnIndex(RosterDataBase.ID);
		indexUserName = cursor.getColumnIndex(RosterDataBase.USER_NAME);
		indexPresence = cursor.getColumnIndex(RosterDataBase.PRESENCE);		
	
		cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
        	
        	MyRosterEntry entry = new MyRosterEntry();
        	entry.groupName = cursor.getString(columnIndexGroup);
        	entry.Jid = cursor.getString(columnIndexJId);
        	entry.userName = cursor.getString(indexUserName);
        	entry.id = cursor.getInt(columnIndexId);
        	String presence = cursor.getString(indexPresence);
        	if(Presence.Type.available.name().equals(presence)){
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
        mFriendListView.setDragListener(this);   
        mFriendListView.setOnScrollListener(this);
        roster.close();
		return v;
	}	
	public void reRoadData(){
		
		// initial data
		MyApplication app = (MyApplication)mActivity.getApplication();
		mAdminJid = app.getAdminJid();
		xmppConnection = app.getConnection();
		indicatorGroupId = -1;
		if(mAdapter != null){
			mAdapter.hideGroup(-1);
		}
		   
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
        try {
        	
			Cursor cursor = roster.queryAll();
			mAdapter.initData(); 
			
			cursor.moveToFirst();
			while(cursor.isAfterLast() == false){
				
				MyRosterEntry entry = new MyRosterEntry();
				entry.groupName = cursor.getString(columnIndexGroup);
				entry.Jid = cursor.getString(columnIndexJId);
				entry.userName = cursor.getString(indexUserName);
				entry.id = cursor.getInt(columnIndexId);
				String presence = cursor.getString(indexPresence);
				if(Presence.Type.available.name().equals(presence)){
					entry.isOnline = true;
				}else{
					entry.isOnline = false;
				}
				mAdapter.addItem(entry);
				cursor.moveToNext();
			}
			
			//mAdapter.notifyDataSetChanged();
			mAdapter.notifyDataSetInvalidated();
		} catch (Exception e) {			
			Log.e(TAG, PRE + "—œ÷ÿ¥ÌŒÛ");
			e.printStackTrace();
		}   
		roster.close();	
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
		xmppConnection = null;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach");
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if(mEditable){
			return false;
		}
		MyRosterEntry entry = mAdapter.getChild(groupPosition, childPosition);
		
		RosterDataBase roster = new RosterDataBase(mActivity, mAdminJid);
		roster.updateColumn(entry.Jid, RosterDataBase.NEW_MSG_TIME, 
				String.valueOf(System.currentTimeMillis()));
		roster.close();
		
		Intent intent = new Intent(getActivity(),MultiChatActivity.class);
		intent.putExtra(MultiChatActivity.ACCOUNT_JID, entry.Jid);
        startActivity(intent);	
		return true;
	}
	/**
	 * define my own data
	 * @author ANDREW CHAN (chenyzpower@gmail.com)
	 *
	 */
	public class MyRosterGroup {
		public MyRosterGroup(String groupName){
			this.groupName = groupName;
		}
		public String groupName;
		public int totalNum = -1;
		public int onlineNum = -1;
		@Override
		public boolean equals(Object o) {
			if(o instanceof MyRosterGroup){
				MyRosterGroup r = (MyRosterGroup)o;
				return groupName.equals(r.groupName);
			}
			return false;
		}		
	}
	public class MyRosterEntry {
		public Integer id;
		public String groupName;
		public String Jid;
		public String userName;
		public boolean isOnline;
	}
	
	 public class RosterExpandableListAdapter extends BaseExpandableListAdapter
	 						implements OnAvater.OnAvaterListener,DragDropListener{
	        	        
	        private ArrayList<MyRosterGroup> groups = new ArrayList<MyRosterGroup>();
	        private ArrayList<ArrayList<MyRosterEntry>> children = new ArrayList<ArrayList<MyRosterEntry>>();
	        
	        private LayoutInflater mInflater;
	        
	        private int mFirstGroupPos = -1;
	        
	        private int mStartDragPosition = AdapterView.INVALID_POSITION;
	        private int mStartDragGroupPosition  = AdapterView.INVALID_POSITION;
	        private int mStartDragChildPosition  = AdapterView.INVALID_POSITION;
	        private int mDragFocusGroupPosition  = AdapterView.INVALID_POSITION;
	        private int mDragFocusChildPosition  = AdapterView.INVALID_POSITION;
	        
	        public RosterExpandableListAdapter(Context context, ArrayList<MyRosterGroup> groups,
	        		ArrayList<ArrayList<MyRosterEntry>> children){
	        	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	this.groups = groups;
	            this.children = children;
	        }
	        public RosterExpandableListAdapter(Context context){
	        	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	
	        }	        
	        public void initData(){
	        	groups = new ArrayList<MyRosterGroup>();
		        children = new ArrayList<ArrayList<MyRosterEntry>>();
	        }
	        public void addItem(MyRosterEntry entry) {
	        	MyRosterGroup g = new MyRosterGroup(entry.groupName);
	        	
	            if (!groups.contains(g)) {
	                groups.add(g);
	            }
	            int index = groups.indexOf(g);
	            if (children.size() < index + 1) {
	                children.add(new ArrayList<MyRosterEntry>());
	            }
	            if(entry.userName.endsWith(mAdminJid)){
	            	return;
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
	        /**
	         * Calculate group information
	         */
	        private void initGroupChildCount(int groupPosition){
	        	MyRosterGroup group = groups.get(groupPosition);
	        	if(group.totalNum != -1){
	        		return;
	        	}
	        	group.onlineNum = 0;
        		group.totalNum = getChildrenCount(groupPosition);
        		if(group.totalNum == 0){
        			return;
        		}
        		ArrayList<MyRosterEntry> arrayEntry = children.get(groupPosition);	        		
        		Iterator<MyRosterEntry> it = arrayEntry.iterator();
        		while(it.hasNext()){
        			MyRosterEntry entry = it.next();
        			if(entry.isOnline){
        				group.onlineNum++;
        			}
        		}
	        }
	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
	                ViewGroup parent) {
	            View view;
	            if(convertView == null){
	            	view = mInflater.inflate(mEditable?R.layout.im_friend_item_group_edit:
	            		R.layout.im_friend_item_group, null);
	            } else {
	            	view = convertView;
	            }
	            initGroupChildCount(groupPosition);
	            MyRosterGroup group = groups.get(groupPosition);
	            String groupName = group.groupName;
	            TextView txGroup = (TextView)view.findViewById(R.id.groupName);
	            txGroup .setText(mActivity.getResources()
	            		.getString(R.string.im_groupname_format,groupName,
	            		group.onlineNum,group.totalNum));
	            
	            ImageView btn = null;
	            if(mEditable == false){
	            	btn = (ImageView)view.findViewById(R.id.icon_expand);
		            if(isExpanded){
		            	btn.setSelected(true);
		            }else{
		            	btn.setSelected(false);
		            }	            	
	            }else{
	            	View viewDel = view.findViewById(R.id.im_group_delete);
	            	viewDel.setTag(groupName);
	            	viewDel.setOnClickListener(FriendListFragment.this);
	            	ImageView viewEdit = (ImageView)view.findViewById(R.id.im_group_edit);
	            	viewEdit.setTag(groupName);
	            	viewEdit.setOnClickListener(FriendListFragment.this);
	            }
	            
	            if(mStartDragGroupPosition == groupPosition && 
	               mStartDragChildPosition == -1){
	            	txGroup.setText("start");
	            }else if(mDragFocusGroupPosition == groupPosition && 
	            		mDragFocusChildPosition == -1){
	            	txGroup.setText("focus");
	            }
	            view.setTag(R.integer.tag_group, groupPosition);
	            view.setTag(R.integer.tag_child, -1);
	            
	            if(mFirstGroupPos == groupPosition && isExpanded){
	            	txGroup.setVisibility(View.INVISIBLE);
	            	if(btn != null){
	            		btn.setVisibility(View.INVISIBLE);
	            	}
	            
	            }else{
	            	txGroup.setVisibility(View.VISIBLE);
	            	if(btn != null){
	            		btn.setVisibility(View.VISIBLE);
	            	}
	            }
	            Log.i(TAG, PRE + "getGroupView");
	            return view;
	        }

	        public MyRosterEntry getChild(int groupPosition, int childPosition) {
	            return children.get(groupPosition).get(childPosition);
	        }

	        public long getChildId(int groupPosition, int childPosition) {
	            return children.get(groupPosition).get(childPosition).id;
	        }

	        public int getChildrenCount(int groupPosition) {
	        	if(groupPosition >= children.size() ){
	        		return 0;
	        	}	        	
	            return children.get(groupPosition).size();
	        }
	        /**
	         * for user move to other groups
	         * @param groupPosition
	         * @return
	         */
	        private String[] getTargetGroups(int groupPosition){
	        	String[] groupStrings = new String[getGroupCount()-1];
	        	MyRosterGroup rosterGroup = groups.get(groupPosition);
	        	Iterator<MyRosterGroup> it = groups.iterator();
	        	int i = 0;
	        	while(it.hasNext()){
	        		MyRosterGroup tempGroup = it.next();
	        		if(tempGroup.equals(rosterGroup)){
	        			continue;
	        		}
	        		groupStrings[i] = tempGroup.groupName;
	        		i++;
	        	}
	        	return groupStrings;
	        }
	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
	                View convertView, ViewGroup parent) {
	        	View view;
	        	if(convertView != null){
	        		view = convertView;
	        	}else{
	        		view = mInflater.inflate(mEditable?R.layout.im_friend_item_entry_edit:
	        			R.layout.im_friend_item_entry, null);
	        	}
	        	MyRosterEntry entry = getChild(groupPosition, childPosition);
	        	
	        	if(mEditable == false){
	            	
	            }else{
	            	View viewDel = view.findViewById(R.id.im_entry_delete);
	            	viewDel.setTag(entry);
	            	viewDel.setOnClickListener(FriendListFragment.this);
	            	ImageView viewEdit = (ImageView)view.findViewById(R.id.im_entry_edit);
	            	viewEdit.setTag(entry);
	            	viewEdit.setOnClickListener(FriendListFragment.this);
	            	View viewMove = view.findViewById(R.id.im_entry_move);
	            	viewMove.setOnClickListener(FriendListFragment.this);
	            	viewMove.setTag(entry);
	            	viewMove.setTag(R.integer.tag_groupname, getTargetGroups(groupPosition));
	            }	        	
	        	
	            TextView textView = (TextView)view.findViewById(R.id.username);
	            textView.setText(entry.userName);
	            textView = (TextView)view.findViewById(R.id.sign);
	            textView.setText(entry.Jid);
	            ImageView head = (ImageView)view.findViewById(R.id.imageView);
	            Drawable avater = onAvater.getAvater(entry.Jid, this);
	            
	            head.setImageDrawable(avater);
	            
	            if(entry.isOnline){	            	
	            	head.setColorFilter(null);
	            }else{	            	
	            	head.setColorFilter(PushServiceUtil.GREY_COLOR_FILTER);
	            }
	           
	            if(mStartDragGroupPosition == groupPosition && 
	 	            mStartDragChildPosition == childPosition){
	            	textView.setText("start");
	 	        }else if(mDragFocusGroupPosition == groupPosition && 
	            		mDragFocusChildPosition == childPosition){
	 	        	textView.setText("focus");
	            }
	            view.setTag(R.integer.tag_group, groupPosition);
	            view.setTag(R.integer.tag_child, childPosition);            
	            
	            return view;
	        }
	        
	        public boolean isChildSelectable(int groupPosition, int childPosition) {
	            return mEditable?false:true;
	        }

	        public boolean hasStableIds() {
	            return true;
	        }
	        
	        @Override
	    	public void avater(String avaterJid, Drawable avater) {
	    		Log.e(TAG, PRE + avaterJid);
	    		this.notifyDataSetChanged();
	    	}
	        
			@Override
			public void onDrag(int position, View itemView) {
				if(itemView != null){
					Integer dragGroupPosition = (Integer)itemView.getTag(R.integer.tag_group);
					Integer dragChildPosition = (Integer)itemView.getTag(R.integer.tag_child);
					if(dragGroupPosition == mDragFocusGroupPosition && 
					   dragChildPosition==mDragFocusChildPosition){
						return;
					}else{
						mDragFocusGroupPosition = dragGroupPosition;
						mDragFocusChildPosition = dragChildPosition;
						notifyDataSetChanged();
					}
				}	
				
			}
			@Override
			public void onStartDrag(int position, View itemView) {
				if(itemView == null){
					return;
				}
				mDragFocusGroupPosition = -1;
				mDragFocusChildPosition = -1;
				
				mStartDragPosition = position;
				mStartDragGroupPosition = (Integer)itemView.getTag(R.integer.tag_group);
				mStartDragChildPosition = (Integer)itemView.getTag(R.integer.tag_child);
				if(mStartDragPosition != AdapterView.INVALID_POSITION){
					notifyDataSetChanged();
				}
			}
			@Override
			public void onStopDrag(int position, View itemView) {
				if(itemView != null){
					Integer endDragGroupPosition = (Integer)itemView.getTag(R.integer.tag_group);
					Integer endDragChildPosition = (Integer)itemView.getTag(R.integer.tag_child);
					
					// change data
					
					MyRosterEntry entry = children.get(mStartDragGroupPosition).get(mStartDragChildPosition);
					
					// delete
					children.get(mStartDragGroupPosition).remove(mStartDragChildPosition);
					
					MyRosterGroup group = groups.get(endDragGroupPosition);
					entry.groupName = group.groupName;
					children.get(endDragGroupPosition).add(entry);
					
					 RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
					 roster.updateColumn(entry.Jid, RosterDataBase.GROUP_NAME, group.groupName);
					 roster.close();
					 
					UserTask userTask = new UserTask();
					userTask.addUser(entry.Jid,"hello1",group.groupName);
					userTask.execute();
					
				}
				
				mStartDragGroupPosition = -1;
				mStartDragChildPosition = -1;
				mDragFocusGroupPosition = -1;
				mDragFocusChildPosition = -1;
				notifyDataSetChanged();
				return;
			}
			public void hideGroup(int groupPos){
				mFirstGroupPos = groupPos;
			}

	}
	 class UserTask extends AsyncTask<Void, Void, Exception> {

	    	private String userJid;
	    	private String nickName;
	    	private String groupName;
	    	private String oldGroupName;
	    	
	    	private Boolean bAddUser = false;
	    	private Boolean bEditUser = false;
	    	private Boolean bAddGroup = false;
	    	private Boolean bDeleteUser = false;
	    	private Boolean bDeleteGroup = false;
	    	private Boolean bMoveGroup2Group = false;
	    	private Boolean bMoveUser2Group = false;
	    	
	    	public void addUser(String userJid, String nickName, String groupName){
	    		bAddUser = true;
	    		this.userJid = userJid;
	    		this.nickName = nickName;
	    		this.groupName = groupName;
	    	}
	    	public void moveUser2Group(String userJid, String nickName, String groupName){
	    		bMoveUser2Group = true;
	    		this.userJid = userJid;
	    		this.nickName = nickName;
	    		this.groupName = groupName;
	    	}
	    	
	    	public void editUser(String userJid, String nickName, String groupName){
	    		bEditUser = true;
	    		this.userJid = userJid;
	    		this.nickName = nickName;
	    		this.groupName = groupName;
	    	}
	    	public void addGroup(String groupName){
	    		bAddGroup = true;
	    		this.groupName = groupName;
	    	}
	    	public void deleteUser(String userJid){
	    		bDeleteUser = true;
	    		this.userJid = userJid;
	    	}
	    	public void deleteGroup(String oldGroupName,String groupName){
	    		bDeleteGroup = true;
	    		this.oldGroupName = oldGroupName;
	    		this.groupName = groupName;
	    	}
	    	public void moveGroup2Group(String oldGroupName,String groupName){
	    		bMoveGroup2Group = true;
	    		this.oldGroupName = oldGroupName;
	    		this.groupName = groupName;
	    	}
	    	
			@Override
			protected void onPreExecute() {
				if(bAddUser){
		   			
		   		}else if(bAddGroup){
		   					   			
		   		}else if(bDeleteUser){
															   		
				}else if(bDeleteGroup){
					
				}else if(bMoveGroup2Group){
					
				}
				showProgressDialog(true);
				super.onPreExecute();
			}
			@Override
			protected Exception doInBackground(Void... params) {
				
				Roster roster = xmppConnection.getRoster();
			   	
			   	RosterGroup myGroup = null;
			   	try {   		
			   		if(bAddUser || bEditUser || bMoveUser2Group){
			   			myGroup = roster.getGroup(groupName);
				   		if(myGroup == null){
				   			myGroup = roster.createGroup(groupName);
				   		}
				   		if(roster.contains(userJid)){
				   			Log.d(TAG, PRE + "aready has " + userJid);
				   		}
						roster.createEntry(userJid, nickName, new String[]{groupName});	
						myGroup = null;
			   		}else if(bAddGroup){
			   			myGroup = roster.createGroup(groupName);
			   			RosterEntry adminEntry = roster.getEntry(mAdminJid);
			   			if(adminEntry == null){
			   				roster.createEntry(mAdminJid, mAdminJid, new String[]{groupName});	
			   			}else{
			   				myGroup.addEntry(adminEntry);
			   			}
			   		}else if(bDeleteUser){
						RosterEntry rosterEntry = roster.getEntry(userJid);
						roster.removeEntry(rosterEntry);											   		
					}else if(bDeleteGroup || bMoveGroup2Group){
						RosterGroup rosterGroup = roster.getGroup(oldGroupName);
						Iterator<RosterEntry> it = rosterGroup
								.getEntries().iterator();
						while(it.hasNext()){
							RosterEntry entry = it.next();
							if(entry.getUser().equalsIgnoreCase(mAdminJid)){								
								myGroup = roster.getGroup(groupName);
						   		if(myGroup == null){
						   			myGroup = roster.createGroup(groupName);
						   		}
								myGroup.addEntry(entry);
								rosterGroup.removeEntry(entry);
							}else{
								roster.createEntry(entry.getUser(), entry.getName(),
										new String[]{groupName});	
							}
						}
					}
					
			   	} catch (IllegalArgumentException e) {		   		
			   		e.printStackTrace();
			   		return e;
			   	} catch (XMPPException e) {		   		
					e.printStackTrace();
					return e;
				} catch (Exception e) {		   		
					e.printStackTrace();
					return e;
				}
				return null;
			}
			@Override
			protected void onPostExecute(Exception result) {
				int nString = R.string.add_user_suc;
				if(bAddUser){
					nString = R.string.add_user_suc;
		   		}else if(bAddGroup){
		   			nString = R.string.add_group_suc;
		   		}else if(bDeleteUser){
		   			nString = R.string.del_user_suc;		   		
				}else if(bDeleteGroup){
					nString = R.string.del_group_suc;
				}else if(bMoveGroup2Group){
					nString = R.string.edit_group_suc;
				}else if(bEditUser){
					nString = R.string.edit_user_suc;
				}else if(bMoveUser2Group){
					nString = R.string.move_user_suc;
				}
				showProgressDialog(false);
				reRoadData();
				Utility.showToast(mActivity, nString, Toast.LENGTH_SHORT);
				super.onPostExecute(result);
			}
	}
	
	
	public void setOnAvater(OnAvater onAvater){
		this.onAvater = onAvater;
	}
	public void setEditable(boolean isEdit){
		mEditable = isEdit;
		if(mFriendListView != null){
			mFriendListView.setEditable(mEditable);
		}
	}

	@Override
	public void onDrag(int position, View itemView) {
		mAdapter.onDrag(position, itemView);		
	}

	@Override
	public void onStartDrag(int position, View itemView) {
		mAdapter.onStartDrag(position, itemView);		
	}

	@Override
	public void onStopDrag(int position, View itemView) {
		mAdapter.onStopDrag(position, itemView);		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(mEditable){			
			return;
		}
		ExpandableListView listView = (ExpandableListView)view;
		int npos = view.pointToPosition(1,0);
		if(npos != AdapterView.INVALID_POSITION){
			long pos = listView.getExpandableListPosition(npos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if(childPos == AdapterView.INVALID_POSITION){
				View groupView = listView.getChildAt(npos - listView.getFirstVisiblePosition());
				indicatorGroupHeight = groupView.getHeight();
				if(indicatorGroupHeight == 0){
					return;
				}
			}
			if(groupPos != indicatorGroupId){				
				mAdapter.getGroupView(groupPos, 
						listView.isGroupExpanded(groupPos), indicatorGroup.getChildAt(0), null);				
				indicatorGroupId = groupPos;				
				mAdapter.hideGroup(groupPos);				
				mAdapter.notifyDataSetChanged();
				Log.e(TAG,PRE + "move to new group" + groupPos);
			}
		}
		int imageHeight = indicatorGroupHeight;
		int nEndPos = listView.pointToPosition(1,indicatorGroupHeight-1);
		if(nEndPos != AdapterView.INVALID_POSITION){
			long pos = listView.getExpandableListPosition(nEndPos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if(groupPos != indicatorGroupId){
				//group
				View viewNext = listView.getChildAt(nEndPos-listView.getFirstVisiblePosition());
				imageHeight = viewNext.getTop();
				Log.e(TAG,PRE + "START UP MOVE:" + imageHeight);
			}
		}
		// show group
		MarginLayoutParams layoutParams = (MarginLayoutParams)indicatorGroup.getLayoutParams();
		
		layoutParams.topMargin = imageHeight-indicatorGroupHeight;
		indicatorGroup.setLayoutParams(layoutParams);
		int firstVisible = listView.getFirstVisiblePosition();
		if(firstVisible != AdapterView.INVALID_POSITION){			
			int firstGroup = ExpandableListView.getPackedPositionGroup(
					listView.getExpandableListPosition(firstVisible));
			if(firstGroup == indicatorGroupId &&
					listView.isGroupExpanded(indicatorGroupId) == false){
				indicatorGroup.setVisibility(View.INVISIBLE);
				Log.e(TAG,PRE + "indicator group hide");
			}else{
				indicatorGroup.setVisibility(View.VISIBLE);
				Log.e(TAG,PRE + "indicator group show");
			}
		}
				
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.im_entry_delete:
			showDeleteUserDialog((MyRosterEntry)v.getTag());
			break;
		case R.id.im_entry_edit:
			showEditUserDialog((MyRosterEntry)v.getTag());
			break;
		case R.id.im_group_delete:
			showDeleteGroupDialog((String)v.getTag());
			break;
		case R.id.im_group_edit:
			showEditGroupNameDialog((String)v.getTag());
			break;
		case R.id.im_entry_move:
			showMoveEntryGroup((MyRosterEntry)v.getTag(),(String[])v.getTag(R.integer.tag_groupname));
			break;
		}		
	}
	
	private void showProgressDialog(boolean bShow){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ProgressFragment prev = (ProgressFragment)getFragmentManager().findFragmentByTag("dialog_progress");
        if(prev != null){        	
        	prev.dismiss();          
        }
        if(bShow == false){
        	return;
        }

    	if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        
        ProgressFragment fragment = ProgressFragment.newInstance(this,
				EditDialogFragment.REQCODE_EDIT_GROUP,"waiting");
		fragment.show(getFragmentManager(), "dialog_progress");
		
	}
	private void showDeleteGroupDialog(String groupName){
		clearDialogBackStack();
        
		EditDialogFragment fragment = EditDialogFragment.newInstance(this,
				EditDialogFragment.REQCODE_DEL_GROUP,groupName,null);
		fragment.show(getFragmentManager(), "dialog_friend");
	}
	private void showDeleteUserDialog(MyRosterEntry userEntry){
		clearDialogBackStack();
        
		EditDialogFragment fragment = EditDialogFragment.newInstance(this,
				EditDialogFragment.REQCODE_DEL_USER,null,userEntry);
		fragment.show(getFragmentManager(), "dialog_friend");
	}
	private void showEditUserDialog(MyRosterEntry userEntry){		
		clearDialogBackStack();
        
		EditDialogFragment fragment = EditDialogFragment.newInstance(this,
				EditDialogFragment.REQCODE_EDIT_USER,null,userEntry);
		
		fragment.show(getFragmentManager(), "dialog_friend");
	}
	private void showEditGroupNameDialog(String groupName){		
		clearDialogBackStack();
        
		EditDialogFragment fragment = EditDialogFragment.newInstance(this,
				EditDialogFragment.REQCODE_EDIT_GROUP,groupName,null);
		fragment.show(getFragmentManager(), "dialog_friend");
	}
	private void showMoveEntryGroup(MyRosterEntry userEntry,String[] groups){
		if(groups == null || groups.length == 0){
			Utility.showToast(mActivity, R.string.im_no_more_group,
					Toast.LENGTH_SHORT);
			return;
		}
		clearDialogBackStack();
		EditDialogFragment fragment = EditDialogFragment.newInstance(this,
				EditDialogFragment.REQCODE_MOVE_USER,null,userEntry);
		fragment.setTargetGroups(groups);
		fragment.show(getFragmentManager(), "dialog_friend");
	}
	private void clearDialogBackStack(){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog_friend");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
	}
	private void excuChangeGroup(String groupname,String oldGroupName){
		if(isConnectionOk() == false || groupname.isEmpty()){
			return;
		}
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
		if(roster.isGroupNameExist(groupname)){
			String tips = mActivity.getResources()
				.getString(R.string.im_groupname_exist,groupname);
			Utility.showToast(mActivity, tips, Toast.LENGTH_SHORT);
			return;
		}
		roster.updateGroupName(groupname,oldGroupName);		
		roster.close();
		
		UserTask userTask = new UserTask();
		userTask.moveGroup2Group(oldGroupName, groupname);
		userTask.execute();
	}
	private void excuAddGroup(String groupname){
		if(isConnectionOk() == false){
			return;
		}
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
		if(roster.isGroupNameExist(groupname)){
			String tips = mActivity.getResources()
				.getString(R.string.im_groupname_exist,groupname);
			Utility.showToast(mActivity, tips, Toast.LENGTH_SHORT);
			return;
		}		
		roster.insert(mAdminJid, mAdminJid, groupname, null);
		roster.close();
		
		UserTask userTask = new UserTask();
		userTask.addGroup(groupname);
		userTask.execute();
	}
	private void excuDeleteGroup(String groupName){
		if(isConnectionOk() == false){
			return;
		}
		String defaultGroupName = getResources().getString(R.string.im_default_group_name);
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
		roster.updateGroupName(defaultGroupName,groupName);
		roster.close();
		UserTask userTask = new UserTask();
		userTask.deleteGroup(groupName, defaultGroupName);
		userTask.execute();
	}
	private void excuDeleteUser(String userJid){
		if(isConnectionOk() == false){
			return;
		}
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
		roster.deleteRoster(userJid);
		roster.close();
		UserTask userTask = new UserTask();
		userTask.deleteUser(userJid);
		userTask.execute();
	}
	private void excuEditUser(String userJid,String nickName,String groupName){
		if(isConnectionOk() == false || nickName.isEmpty()){
			return;
		}
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
		roster.updateColumn(userJid, RosterDataBase.USER_NAME, nickName);
		roster.close();
		UserTask userTask = new UserTask();
		userTask.editUser(userJid,nickName,groupName);
		userTask.execute();
	}
	private boolean isConnectionOk(){
		if(xmppConnection == null ||
				xmppConnection.isAuthenticated() == false){
			Utility.showToast(mActivity, R.string.im_connect_fail,
					Toast.LENGTH_SHORT);
			return false;
		}	
		return true;
	}
	private void excuMoveGroup(String userJid,String nickName,
						String groupName,String oldGroup){
		if(isConnectionOk() == false){
			return;
		}
		RosterDataBase roster = new RosterDataBase(mActivity,mAdminJid);
		roster.updateColumn(userJid, RosterDataBase.GROUP_NAME, groupName);
		if(roster.isGroupNameExist(oldGroup) == false){
			roster.insert(mAdminJid, mAdminJid, oldGroup, null);
		}
		roster.close();
		UserTask userTask = new UserTask();
		userTask.moveUser2Group(userJid,nickName, groupName);
		userTask.execute();
	}
	public static class EditDialogFragment extends DialogFragment{
		
		private static String GROUPNAME = "param1";
		public static int REQCODE_EDIT_GROUP = 1;
		public static int REQCODE_DEL_GROUP = 2;
		public static int REQCODE_ADD_GROUP = 3;
		public static int REQCODE_EDIT_USER = 4;
		public static int REQCODE_DEL_USER = 5;
		public static int REQCODE_MOVE_USER = 6;

		String groupName;
		MyRosterEntry userEntry;
		String[] targetGroups;
		Integer selPosition = 0;
		
		FriendListFragment targetFragment;
		
		int reqCode = REQCODE_EDIT_GROUP;
		
		public static EditDialogFragment newInstance(Fragment targetFragment,int reqCode,
				String groupName,MyRosterEntry userEntry){
			EditDialogFragment fragment = new EditDialogFragment();
			Bundle args = new Bundle();
			args.putString(GROUPNAME, groupName);
			fragment.setArguments(args);
			fragment.setUserEntry(userEntry);
			fragment.setTargetFragment(targetFragment, reqCode);
			return fragment;
		}
		public void setUserEntry(MyRosterEntry entry){
			userEntry = entry;
		}
		
		public void setTargetGroups(String[] groups){
			targetGroups = groups;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			Bundle args = getArguments();
			groupName = args.getString(GROUPNAME);
			targetFragment = (FriendListFragment)getTargetFragment();
			reqCode = getTargetRequestCode();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			// cancel button
			builder.setNegativeButton(R.string.alert_dialog_cancel, new Dialog.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {					
				}
			});
			
			if(reqCode == REQCODE_EDIT_GROUP ||
					reqCode == REQCODE_ADD_GROUP){
				String tips = getActivity().getResources()
					.getString(R.string.im_change_group,groupName);
				if(reqCode == REQCODE_ADD_GROUP){
					tips = getActivity().getResources().getString(R.string.im_new_group);
				}
				final View layout = View.inflate(getActivity(), R.layout.im_edit_group_input, null);
				final EditText editText = (EditText)layout.findViewById(R.id.editText);
				builder.setTitle(tips);
				builder.setIcon(R.drawable.icon);
				builder.setView(layout);
				builder.setPositiveButton(R.string.alert_dialog_ok, new Dialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newGroupName = editText.getEditableText().toString();
						// TODO: when input is empty we should do something ...
						if(reqCode == REQCODE_EDIT_GROUP){
							targetFragment.excuChangeGroup(newGroupName,groupName);				
						}else if(reqCode == REQCODE_ADD_GROUP){
							targetFragment.excuAddGroup(newGroupName);
						}
					}			
				});
			}else if(reqCode == REQCODE_DEL_GROUP){
				String tips = getActivity().getResources()
					.getString(R.string.im_del_group_commit,groupName);
			
				builder.setTitle(R.string.im_import_note);
				builder.setIcon(R.drawable.icon);
				builder.setMessage(tips);
			
				builder.setPositiveButton(R.string.alert_dialog_ok, new Dialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						targetFragment.excuDeleteGroup(groupName);					
					}			
				});				
			}else if(reqCode == REQCODE_DEL_USER){
				String tips = getActivity().getResources()
					.getString(R.string.im_del_user_commit,userEntry.userName);
		
				builder.setTitle(R.string.im_import_note);
				builder.setIcon(R.drawable.icon);
				builder.setMessage(tips);
			
				builder.setPositiveButton(R.string.alert_dialog_ok, new Dialog.OnClickListener(){
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						targetFragment.excuDeleteUser(userEntry.Jid);					
					}			
				});		
			}else if(reqCode == REQCODE_EDIT_USER){
				String tips = getActivity().getResources()
					.getString(R.string.im_change_nickname,userEntry.userName);
				
				final View layout = View.inflate(getActivity(), R.layout.im_edit_group_input, null);
				final EditText editText = (EditText)layout.findViewById(R.id.editText);
				editText.setHint(R.string.im_input_nickname_hit);
				builder.setTitle(tips);
				builder.setIcon(R.drawable.icon);
				builder.setView(layout);
				builder.setPositiveButton(R.string.alert_dialog_ok, new Dialog.OnClickListener(){
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newNickName = editText.getEditableText().toString();
						// TODO: when input is empty we should do something ...						
						targetFragment.excuEditUser(userEntry.Jid,newNickName,
								userEntry.groupName);			
						
					}			
				});
			}else if(reqCode == REQCODE_MOVE_USER){
				builder.setTitle(R.string.im_select_group);
	            builder.setSingleChoiceItems(targetGroups, 0, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {                  
	                	selPosition = whichButton;
	                }
	            });
	            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	targetFragment.excuMoveGroup(userEntry.Jid,userEntry.userName,
	                			targetGroups[selPosition],userEntry.groupName);
	                }
	            });
			}
			
			return builder.create();
		}
	}

	
}
