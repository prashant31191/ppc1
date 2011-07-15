package com.smit.EasyLauncher;


import com.smit.MyView.MyViewctrl;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class LoginActivity extends Activity {
	private Rect logrect;
	private FrameLayout loginLayout;
	private EasyLauncher mLauncher;
	private Animation myAnimation_Rotate;  //旋转
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
			if(!IsOnclickRecy(x,y))
				finish();

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
    
}
