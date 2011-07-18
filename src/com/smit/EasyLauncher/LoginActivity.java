package com.smit.EasyLauncher;


import java.util.ArrayList;

import com.openims.demo.MainActivity;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.MultiChatActivity;
import com.openims.view.chat.widget.IMActivity;
import com.openims.view.setting.Setting.InnerReceiver;
import com.smit.MyView.MyViewctrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity {
	
	private Rect logrect;
	private FrameLayout loginLayout;
	private EasyLauncher mLauncher;
	private Animation myAnimation_Rotate;  //旋转
	private LoginDataBaseAdapter m_MyDataBaseAdapter;
	private TextView mTextView1;
	private TextView mTextView2;
	private Button mBotton1;
	private Button mBotton2;
	private CheckBox  checkBox1;
	private CheckBox  checkBox2;
	private ImageButton mImageBotton;
	private ListView m_ListView	= null;
	private Context context;
	private ListViewAdapter adapter;
	private PopupWindow mPopupWindow = null;
	private BroadcastReceiver receiver;
	private ProgressDialog m_Dialog;
	
	private String username;
	private String password;
	private String savepwd;
	private boolean bAutoLogin;

	
	public void setLauncher(EasyLauncher launcher)
	{
		mLauncher=launcher;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(R.style.Transparent);
		setContentView(R.layout.loginpage);
		mTextView1 = (TextView)findViewById(R.id.login_edit_account);
		mTextView2 = (TextView)findViewById(R.id.login_edit_pwd);
        mBotton1 = (Button)findViewById(R.id.login_btn_register);
        mBotton2 = (Button)findViewById(R.id.login_btn_login);
        mImageBotton = (ImageButton)findViewById(R.id.ImageButton);
		checkBox1 = (CheckBox) findViewById(R.id.login_cb_autoLogin);
		checkBox2 = (CheckBox) findViewById(R.id.login_cb_savepwd);
        m_ListView = new ListView(this);		
		receiver = new InnerReceiver();
        context = this;
        
		m_MyDataBaseAdapter = new LoginDataBaseAdapter(this);		
//		m_MyDataBaseAdapter.open();		
	
        mBotton1.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {

    		}
    	});
        
        mBotton2.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
				
	    		username = mTextView1.getText().toString();
	    		password = mTextView2.getText().toString();
	    			    		
	    		if(checkBox1.isChecked()){
	    			bAutoLogin = true;
	    		}else{
	    			bAutoLogin = false;
	    		}
	    		
	    		if(checkBox2.isChecked()){
	    			savepwd = "1";
	    		}else{
	    			savepwd = "0";
	    		}
	    		
	    		Log.d("username = ",   username);
	            Log.d("password = ",   password);
	          
    			Intent intent = new Intent();
				intent.putExtra(PushServiceUtil.XMPP_USERNAME, username);	
				intent.putExtra(PushServiceUtil.XMPP_PASSWORD, password);	
				intent.putExtra(PushServiceUtil.XMPP_AUTO_LOGIN, bAutoLogin);	
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(PushServiceUtil.ACTION_SERVICE_LOGIN);
		    	startService(intent);		    	
		    	
		    	m_Dialog = ProgressDialog.show
                (
        		   context,
                  "请等待...",
                  "正在为你登录...", 
                  true
                );

/*		    	 new Thread()
                 { 
                   public void run()
                   { 
                     try
                     { 
                       sleep(3000);
                     }
                     catch (Exception e)
                     {
                       e.printStackTrace();
                     }
                     finally
                     {
                     	//登录结束，取消m_Dialog对话框
                     	m_Dialog.dismiss();
                     }
                   }
                 }.start();
*/    		}
    	});
        
        mImageBotton.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {

    			if(mPopupWindow == null){
    				mPopupWindow = new PopupWindow(m_ListView,260,LinearLayout.LayoutParams.WRAP_CONTENT);  
    			}
    			if(!mPopupWindow.isShowing()){
    			adapter = new ListViewAdapter(context);
    			m_ListView.setAdapter(adapter);
    			m_ListView.invalidate();   
    			mPopupWindow.showAsDropDown(findViewById(R.id.login_edit_account));
    			}else{
    				mPopupWindow.dismiss();
    			}
    		}
    	});
        
        if(!EasyLauncher.isFirstInit())
        {
        	View v=findViewById(R.id.LinearLayout01);
        	myAnimation_Rotate= AnimationUtils.loadAnimation(this,R.anim.my_initrotate_action);
        	myAnimation_Rotate.setStartOffset(10);
        	//v.setAnimation(myAnimation_Rotate);
        	v.startAnimation(myAnimation_Rotate);
        	
        }
		SetupView();		
	}
	
	private void SetupView() {
		loginLayout=(FrameLayout) findViewById(R.id.loginRoot);

		logrect=new Rect();
		
		// TODO Auto-generated method stub
		//loginLayout.setc
//		View root=findViewById(R.id.login);
//		final LoginView mQuickAction 	= new LoginView(root);
////		//mQuickAction.window.getContentView().setf
//		mQuickAction.show();
	}
	public void onClick(View v){
		
//		View layout= findViewById(R.id.LinearLayout01);
//		int width=layout.getLayoutParams().width;
//		int height=layout.getLayoutParams().height;
//		int xpos=(getWindow().getWindowManager().getDefaultDisplay().getWidth()-width)/2;
//		int ypos=(getWindow().getWindowManager().getDefaultDisplay().getHeight()-height)/2;
//		logrect.set(xpos,ypos, width, height);
//		int x=0,y=0;
//			x=getIconcanditax(v);
//			y=getIconcanditay(v);
//			if(!IsOnclickRecy(x,y))
//				finish();
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		View layout= findViewById(R.id.LinearLayout01);
		int width=layout.getLayoutParams().width;
		int height=layout.getLayoutParams().height;
		int xpos=(loginLayout.getRight()-width)/2;
		int ypos=(loginLayout.getBottom()-loginLayout.getTop()-height)/2+loginLayout.getTop();
		logrect.set(xpos,ypos,xpos+width,ypos+height);
	     int x = (int) event.getX();
	        int y = (int) event.getY();
			if(!IsOnclickRecy(x,y)){
//				m_MyDataBaseAdapter.close();
				LoginActivity.this.finish();
			}
		return super.onTouchEvent(event);
	}
	
    public int getIconcanditax(View v){
    	LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)v.getLayoutParams();
    	return lp.leftMargin;
    }
    
    public int getIconcanditay(View v){
    	LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)v.getLayoutParams();
    	return lp.topMargin;
    }
    
    //是不是在回收站上
    public boolean IsOnclickRecy(int x,int y) {
		boolean ret=false;
		if(logrect.contains(x, y)){
			ret=true;
		}else{
			ret=false;
		}		
		return ret;
	}

	/* 更新一条数据 */
	public void UpData()
	{	
	//	m_MyDataBaseAdapter.updateData(miCount, "修改的数据" + miCount);
	}

	/* 向表中添加一条数据 */
	public void AddData()
	{				
		if(m_MyDataBaseAdapter.fetchData(username).getCount() == 0)
		{
		System.out.println("---insertData0   : " + username);
		m_MyDataBaseAdapter.insertData(username, password, savepwd);
		}
		
		if(m_MyDataBaseAdapter.fetchData(username).getCount() == 1)
		{
		System.out.println("---updateData0   : " + username);
		m_MyDataBaseAdapter.updateData(username, password, savepwd);
		}
	}

	/* 从表中删除指定的一条数据 */
	public void DeleteData(String num)
	{
		m_MyDataBaseAdapter.deleteData(num);
		adapter = new ListViewAdapter(context);
		m_ListView.setAdapter(adapter);
		m_ListView.invalidate();   
	}
	
	/* 按键事件处理 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			/* 退出时，不要忘记关闭 */
//			m_MyDataBaseAdapter.close();
			LoginActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
 
    public  class ListViewAdapter extends BaseAdapter {
    	
    	private Context mContext;
    	private Cursor cursor;

    	public ListViewAdapter(Context context) {
    		cursor = m_MyDataBaseAdapter.fetchAllData();
    		mContext = context;
    	}


    	public Object getItem(int position) {
    		return position;
    	}

    	public long getItemId(int position) {
    		return position;
    	}

    	class ViewItem {
    		TextView username;
    		Button delete;
    	}

    	// Make a view to hold each row.
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		ViewItem m_ViewItem;
    		
    		if(convertView == null)
    		{
    			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_username_item,
    					null);
    			m_ViewItem = new ViewItem();
    			m_ViewItem.username = (TextView) convertView.findViewById(R.id.username);
    			m_ViewItem.delete = (Button) convertView.findViewById(R.id.delete_icon);
    			convertView.setTag(m_ViewItem);
    		}
    		else
    		{
    			m_ViewItem = (ViewItem) convertView.getTag();
    		}
    		
    		cursor.moveToPosition(position); 
            final String name = cursor.getString(cursor.getColumnIndex(LoginDataBaseAdapter.KEY_NUM));   
            final String psw = cursor.getString(cursor.getColumnIndex(LoginDataBaseAdapter.KEY_PASSWORD));   
            final String rem = cursor.getString(cursor.getColumnIndex(LoginDataBaseAdapter.KEY_REMEMBER));   

            m_ViewItem.username.setText(String.valueOf(name));   
            m_ViewItem.username.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    			System.out.println("---chick  text---");
	      			mTextView1.setText(String.valueOf(name));
	    			mTextView2.setText(psw);
	    			if(rem.equals("1")){
	    				checkBox2.setChecked(true);
	    			}else{
	    				checkBox2.setChecked(false);
	    			}
	    			mPopupWindow.dismiss();	
	    		}
	    	});
            m_ViewItem.delete.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    			DeleteData(name);
	    		}
	    	});
    		return convertView;
    	}


		@Override
		public int getCount() {
			if(cursor != null){
				return cursor.getCount();
			}			
			return 0;
		}
    }
    
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushServiceUtil.ACTION_STATUS);
        registerReceiver(receiver, intentFilter);  
        m_MyDataBaseAdapter.open();
    }  
      
    @Override  
    protected void onPause() {  
        super.onPause();  
        unregisterReceiver(receiver);
        m_MyDataBaseAdapter.close();
    }
    
    public class InnerReceiver extends BroadcastReceiver{
        
    	@Override
    	public void onReceive(Context context,Intent intent){
    		Log.d("login ----","intent : "+intent);
    		if(intent.getAction().equals(PushServiceUtil.ACTION_STATUS)){
	    		String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
	    		Log.d("login ----","STATUSE:"+status);
	    		if(status.equals(PushServiceUtil.PUSH_STATUS_LOGIN_SUC)){
	     			
	    			AddData();	     				     	
					Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();		
	                
	    		}else{
					Toast.makeText(context, "登陆失败", Toast.LENGTH_SHORT).show();		
	
	    		}
	    		m_Dialog.dismiss();
//	    		m_MyDataBaseAdapter.close();
	    		LoginActivity.this.finish();
    		}
    	}
    }
}
