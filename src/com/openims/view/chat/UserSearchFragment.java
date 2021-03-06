package com.openims.view.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openims.model.MyApplication;
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.utility.Utility;
import com.smit.EasyLauncher.R;

public class UserSearchFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
				.makeLogTag(UserSearchFragment.class);
	private static final String PRE = "UserSearchFragment--";	
		
	private static final String EMAIL = "Email";
	private static final String USERNAME = "Username";
	private static final String NickNAME = "Name";
	private static final String HAD_ADD = "had_add";
	private static final String GROUPS = "groups";
	private Activity mActivity;
	
	private ListView listView;
	private EditText mEditInput;
	private ResultAdapter mSchedule;
	
	private CheckBox checkBoxNickName;
	private CheckBox checkBoxUsername;
	private CheckBox checkBoxEmail;
	
	private ArrayList<HashMap<String, String>> mSearchResult = null;
	private ProgressBar mProgress;
	
	private XMPPConnection xmppConnection;
	private String mAdminJid = "test2@smit";
	private String mServerName = "@smit";
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		Log.d(TAG, PRE + "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, PRE + "onCreate");
		MyApplication app = (MyApplication)mActivity.getApplication();
		xmppConnection = app.getConnection();
		mAdminJid = app.getAdminJid();
		mServerName = app.getServeName();
		setRetainInstance(true); // save result		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, PRE + "onCreateView");
		return createView(inflater,container,savedInstanceState);
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
		xmppConnection = null;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mSearchResult != null){
			//TODO
			//outState.putParcelableArrayList("ddd", mSearchResult);
		}
	}
	
	/**
     * A simple adapter which maintains an ArrayList of photo resource Ids. 
     * Each photo is displayed as an image. This adapter supports clearing the
     * list of photos and adding a new photo.
     *
     */
    public class ResultAdapter extends BaseAdapter {
        
        private List<? extends Map<String, String>> mData;

        private int mResource;
        private int mDropDownResource;
        private LayoutInflater mInflater;
        
        private final WeakHashMap<View, View[]> mHolders = new WeakHashMap<View, View[]>();

        private OnClickListener mListener;  

        public ResultAdapter(Context context, List<? extends Map<String, String>> data,
                int resource,OnClickListener listener) {
            mData = data;
            mResource = mDropDownResource = resource;           
            mListener = listener;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }        
        @Override
		public boolean areAllItemsEnabled() {
			return super.areAllItemsEnabled();
		}
		@Override
		public boolean isEnabled(int position) {
			return super.isEnabled(position);
		}
		public int getCount() {
            return mData.size();
        }
        public Object getItem(int position) {
        	return mData.get(position);
        }
        public long getItemId(int position) {
        	return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	return createViewFromResource(position, convertView, parent, mResource);
        }
        
        private View createViewFromResource(int position, View convertView,
                ViewGroup parent, int resource) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(resource, parent, false);
            
                final View[] holder = new View[4];
               
                holder[0] = v.findViewById(R.id.name);
                holder[1] = v.findViewById(R.id.username);
                holder[2] = v.findViewById(R.id.email);                
                holder[3] = v.findViewById(R.id.btn_add_friend);

                mHolders.put(v, holder);
            } else {
                v = convertView;
            }

            bindView(position, v);
            if(position%2 == 0){
            	v.setBackgroundColor(0xFFDDDEDF);
            }else{
            	v.setBackgroundColor(0xFFB9BECA);
            }
            return v;
        }
        
        public void setDropDownViewResource(int resource) {
            this.mDropDownResource = resource;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return createViewFromResource(position, convertView, parent, mDropDownResource);
        }
        public void setViewImage(ImageView v, int value) {
            v.setImageResource(value);
        }
        public void setViewImage(ImageView v, String value) {
            try {
                v.setImageResource(Integer.parseInt(value));
            } catch (NumberFormatException nfe) {
                v.setImageURI(Uri.parse(value));
            }
        }
       
        public void setViewText(TextView v, String text) {
            v.setText(text);
        }

        private void bindView(int position, View view) {
            final Map<String,String> dataSet = mData.get(position);
            if (dataSet == null) {
                return;
            }
            
            final View[] holder = mHolders.get(view);           
           
            TextView nickName = (TextView)holder[0];
            nickName.setText((String)dataSet.get(NickNAME));
            TextView username = (TextView)holder[1];
            username.setText((String)dataSet.get(USERNAME));
            TextView email = (TextView)holder[2];
            email.setText((String)dataSet.get(EMAIL));
            
            Button btn = (Button)holder[3];
            btn.setTag(dataSet.get(USERNAME)); 
            btn.setOnClickListener(mListener);
            String isExist = (String)dataSet.get(HAD_ADD);
            
            if(Boolean.parseBoolean(isExist)){
            	btn.setEnabled(false);
            	btn.setText(R.string.btn_has_friend);
            }else{
            	btn.setEnabled(true);
            	btn.setText(R.string.btn_add_friend);
            }
           
        }// the end of bind view function
        
        public void setUserHadAdd(String usrJid){
        	
        	Iterator<? extends Map<String,String>> it = mData.iterator();
        	while(it.hasNext()){
        		Map<String, String> item = it.next();
        		String username = item.get(USERNAME)+mServerName;
        		if(usrJid.equalsIgnoreCase(username)){
        			item.put(HAD_ADD, "true");
        			break;
        		}
        	}
        }

    }
    
    class UserSearchTask extends AsyncTask<Void, Void, Exception> {

    	private ReportedData reportData;
    	private Boolean bNickName;
    	private Boolean bUserName;
    	private Boolean bEmail;
    	private String input;
    	
    	public UserSearchTask(Boolean bNickName,Boolean bUserName,
    			Boolean bEmail,String input){
    		this.bNickName = bNickName;
    		this.bUserName = bUserName;
    		this.bEmail = bEmail;
    		this.input = input;
    		
    	}
		@Override
		protected Exception doInBackground(Void... params) {
		 
	    	try {
	    		
	    		UserSearchManager search = new UserSearchManager(xmppConnection);
	    		
	    		List<String> list = (List<String>)search.getSearchServices();
				String searchService = list.get(0);
				Form from = search.getSearchForm(searchService);
				
				Form answerForm = from.createAnswerForm();
	            answerForm.setAnswer(NickNAME, bNickName);
	            answerForm.setAnswer(USERNAME, bUserName);
	            answerForm.setAnswer(EMAIL, bEmail);
	            answerForm.setAnswer("search", input);
				
	            reportData =search.getSearchResults(answerForm, searchService);
	            
	    	} catch (XMPPException e) {
				e.printStackTrace();
				return e;
			} catch (Exception e){
				e.printStackTrace();
				return e;
			}			
			return null;
		}		

		@Override
		protected void onPostExecute(Exception result) {			
			super.onPostExecute(result);
			
			mProgress.setVisibility(View.GONE);
			
			if(result != null){
				Utility.showToast(mActivity, R.string.search_fail,
						Toast.LENGTH_LONG);
				return;
			}
			Iterator<Row> itRow = reportData.getRows(); 
			
			mSearchResult = new ArrayList<HashMap<String, String>>();
			
			RosterDataBase rosterDb = new RosterDataBase(mActivity,mAdminJid);
			
			while(itRow.hasNext()){
				HashMap<String, String> map = new HashMap<String, String>();
				
				Row row = itRow.next();				
				Iterator<String> jids = row.getValues(NickNAME);
				if(jids.hasNext()){
					map.put(NickNAME, jids.next() );
				}
				
				Iterator userNames = row.getValues(USERNAME);
				if(userNames.hasNext()){
					String username = (String)userNames.next();
					map.put(USERNAME, username);
					Boolean bExist = rosterDb.isUserExist(username+mServerName);
					map.put(HAD_ADD, Boolean.toString(bExist));
				}
				
				Iterator emails = row.getValues(EMAIL);
				if(emails.hasNext()){
					map.put(EMAIL, (String)emails.next() );
				}
				
				mSearchResult.add(map);
			}
			
			mSchedule = new ResultAdapter(mActivity, mSearchResult, 
					R.layout.im_user_search_list_item,UserSearchFragment.this);

			listView.setAdapter(mSchedule);	
			
			// TODO can't hide input method
			InputMethodManager inputMgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.hideSoftInputFromInputMethod(mEditInput.getWindowToken(),
					0);
		}    	
    }
    class UserTask extends AsyncTask<Object, Void, Exception> {

    	private String userJid;
    	private String nickName;
    	private String groupName;
    	
    	private Boolean bAddUser = false;
    	private Boolean bAddGroup = false;
    	private Boolean bDeleteUser = false;
    	private Boolean bDeleteGroup = false;
    	private Boolean bMove2Group = false;
    	
    	public void addUser(String userJid, String nickName, String groupName){
    		bAddUser = true;
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
    	public void deleteGroup(String groupName){
    		bDeleteGroup = true;
    		this.groupName = groupName;
    	}
		@Override
		protected Exception doInBackground(Object... params) {
			
			Roster roster = xmppConnection.getRoster();
		   	
		   	RosterGroup myGroup = null;
		   	try {   		
		   		if(bAddUser){
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
		   			return null;
		   		}else if(bDeleteUser){
					RosterEntry rosterEntry = roster.getEntry(userJid);
					roster.removeEntry(rosterEntry);
					return null;						   		
				}else if(bDeleteGroup){
					
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
			Utility.showToast(mActivity, R.string.add_user_suc,
					Toast.LENGTH_SHORT);
			if(mSchedule != null){
				mSchedule.setUserHadAdd(userJid);
				mSchedule.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
		
    	
    }
    private View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.im_user_search_fragment, container, false);
		
		mEditInput = (EditText)v.findViewById(R.id.et_search_input);
		checkBoxNickName = (CheckBox)v.findViewById(R.id.checkBox_name);
		checkBoxUsername = (CheckBox)v.findViewById(R.id.checkBox_username);
		checkBoxEmail = (CheckBox)v.findViewById(R.id.checkBox_email);
		listView = (ListView)v.findViewById(R.id.search_result_listView);
		mProgress = (ProgressBar)v.findViewById(R.id.searchProgressBar);
		
		mProgress.setVisibility(View.GONE);
		
		TextView emptyView = new TextView(mActivity);
		emptyView.setText(R.string.no_data);
		emptyView.setGravity(Gravity.CENTER);
		
		listView.setEmptyView(v.findViewById(R.id.search_result_listView_empty));
		listView.addHeaderView(inflater.inflate(
				R.layout.im_user_search_list_header, null),null,false);
		
		if(mSearchResult != null){
			mSchedule = new ResultAdapter(mActivity, mSearchResult, 
					R.layout.im_user_search_list_item,UserSearchFragment.this);
			listView.setAdapter(mSchedule);	
			
		}else{
			listView.setAdapter(null);
		}
		
		addListener(v);
		return v;
		
	}
	private void addListener(View v){
		
		Button btnSearch = (Button)v.findViewById(R.id.search);
		btnSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String input = mEditInput.getText().toString();				
				if(input.isEmpty()){
					Utility.showToast(mActivity, R.string.search_input_null,
							Toast.LENGTH_LONG);
					return;
				}				
				if(xmppConnection == null ||
						xmppConnection.isAuthenticated() == false){
					Utility.showToast(mActivity, R.string.im_connect_fail,
							Toast.LENGTH_SHORT);
					return;
				}
				UserSearchTask userSearch = new UserSearchTask(checkBoxNickName.isChecked(),
						checkBoxUsername.isChecked(),checkBoxEmail.isChecked(),input);
				userSearch.execute();
				mProgress.setVisibility(View.VISIBLE);
			}			
		});
	}
	

	@Override
	public void onClick(View v) {
		showDialog((String)v.getTag());		
	}
	
	void showDialog(String user) { 
        String[] groups = null;
        RosterDataBase rosterDb = new RosterDataBase(getActivity(),mAdminJid);
        groups = rosterDb.getGroups();
        if(groups == null){
        	Utility.showToast(mActivity, R.string.im_no_group,
					Toast.LENGTH_LONG);
        	return;
        }
        
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        
        GroupDialogFragment newFragment = GroupDialogFragment.newInstance(this,
        		user, groups);
        newFragment.show(getFragmentManager(), "dialog");     
       
    }
	public static class GroupDialogFragment extends DialogFragment {
		 
		 private String userName = null;
		 private int position = 0;
		 private String[] groups = null;
		 
		public static GroupDialogFragment newInstance(Fragment targetFragment,
				String username,String[] groups){
			 GroupDialogFragment newFragment = new GroupDialogFragment();
			 Bundle args = new Bundle();
		     args.putString(USERNAME, username);
		     args.putStringArray(GROUPS, groups);
		     newFragment.setArguments(args); 
		     newFragment.setTargetFragment(targetFragment, 0);
		     return newFragment;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			userName = this.getArguments().getString(USERNAME);	
			groups = this.getArguments().getStringArray(GROUPS);
            
            return new AlertDialog.Builder(getActivity())
            //.setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle(R.string.im_select_group)
            .setSingleChoiceItems(groups, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {                  
                	position = whichButton;
                }
            })
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                	UserSearchFragment fragment = (UserSearchFragment)getTargetFragment();
                	fragment.excuAddUser2Group(userName,userName,groups[position]);
                }
            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                   
                }
            })
           .create();
		}
		 
	 }
	
	public void excuAddUser2Group(String jid,String nickName,String groupName){
		if(xmppConnection == null ||
				xmppConnection.isAuthenticated() == false){
			Utility.showToast(mActivity, R.string.im_connect_fail,
					Toast.LENGTH_SHORT);
			return;
		}
		UserTask userTask = new UserTask();
	 	userTask.addUser(jid+mServerName,nickName,groupName);
	 	userTask.execute(new Object());
	 	mActivity.setResult(1);
	}
}


