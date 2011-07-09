package com.openims.view.chat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;

import com.openims.utility.LogUtil;
import com.openims.widgets.DragDropListener;

public class FriendGroupListView extends ExpandableListView 
	implements OnScrollListener{

	private static final String TAG = LogUtil
	.makeLogTag(FriendGroupListView.class);
	private static final String PRE = "FriendGroupListView--";

	private boolean mDragMode;

	private int mStartPosition;
	private int mEndPosition;
	private int mDragPointOffsetY;		//Used to adjust drag view location
	private int mDragPointOffsetX;	
	
	static Integer ntime = 0;
	
	private ImageView mDragView;
	
	private View firstGroupView;
	private View secondGroupView;
	private ImageView indicatorGroup = null;
	private int indicatorGroupId = -1;
	private int indicatorGroupHeight = 0;

	private boolean mEditable = false;
	private DragDropListener mDragListener;
	
	public FriendGroupListView(Context context) {
		this(context, null);
		
	}

	public FriendGroupListView(Context context, AttributeSet attrs) {
		this(context, attrs,
				com.android.internal.R.attr.expandableListViewStyle);
	}

	public FriendGroupListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		indicatorGroup = new ImageView(context);
		//this.setOnScrollListener(this);
	}
	
	
	public void setDragListener(DragDropListener l) {
		mDragListener = l;
	}
		
	public void setEditable(boolean isEdit){
		mEditable = isEdit;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		int npos = pointToPosition(1,0);
		if(npos != INVALID_POSITION){
			long pos = this.getExpandableListPosition(npos);
			int childPos = getPackedPositionChild(pos);
			int groupPos = getPackedPositionGroup(pos);
			if(childPos == INVALID_POSITION){
				//group
				firstGroupView = getChildAt(npos-getFirstVisiblePosition());
				Log.e(TAG,PRE + "START JIE TU");
				if(childPos != indicatorGroupId){
					indicatorGroupId = groupPos;	
					indicatorGroup.setImageBitmap(firstGroupView.getDrawingCache());
				}
			}
		}
		if(firstGroupView != null){
			int height = firstGroupView.getHeight();
			int nEndPos = pointToPosition(1,height);
			if(nEndPos != INVALID_POSITION){
				long pos = this.getExpandableListPosition(nEndPos);
				int childPos = getPackedPositionChild(pos);
				int groupPos = getPackedPositionGroup(pos);
				if(groupPos != indicatorGroupId){
					//group
					View viewNext = getChildAt(nEndPos-getFirstVisiblePosition());
					indicatorGroupHeight = viewNext.getTop();
					Log.e(TAG,PRE + "START UP MOVE");
				}
			}
		}
		ViewParent viewParent = view.getParent();
				
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if(mEditable == false){
			return super.onTouchEvent(ev);
		}
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();	
		Log.i(TAG, PRE + "X=" +x + " rawX=" +ev.getRawX());
		Log.i(TAG, PRE + "Y=" +y + " rawY=" +ev.getRawY());
		
		if (action == MotionEvent.ACTION_DOWN && x < this.getWidth()/4) {
			mDragMode = true;
		}
		
		if (!mDragMode) 
			return super.onTouchEvent(ev);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mStartPosition = pointToPosition(x,y);
				
				if (mStartPosition != INVALID_POSITION) {
					
					long pos = this.getExpandableListPosition(mStartPosition);
					int childPos = getPackedPositionChild(pos);
					int groupPos = getPackedPositionGroup(pos);
					// except group
					int mItemPosition = mStartPosition - getFirstVisiblePosition();
					View itemView = getChildAt(mItemPosition);
					
					if(childPos == -1){
						mDragMode = false;
						return super.onTouchEvent(ev);
					}
					
					mDragPointOffsetY = y - getChildAt(mItemPosition).getTop();
					mDragPointOffsetY -= ((int)ev.getRawY()) - y;
					
					//mDragPointOffsetX = x - getChildAt(mItemPosition).getLeft();
					//mDragPointOffsetX -= ((int)ev.getRawX()) - x;
					mDragPointOffsetX = ((int)ev.getRawX()) - x;
					startDrag(mItemPosition,y);
					//drag(mStartPosition,0,y);// replace 0 with x if desired
				}	
				break;
			case MotionEvent.ACTION_MOVE:				
				
				int npos = pointToPosition(x,y);				
				int nFirst = getFirstVisiblePosition();
				if (npos != INVALID_POSITION) {
					drag(npos,0,y,getChildAt(npos - nFirst));// replace 0 with x if desired
					if(ntime > 30){
						ntime = 0;
						Log.i(TAG,PRE + "npos=" +npos + " nFirst=" + nFirst);
						if(npos - nFirst <= 2){
							this.setSelection(nFirst-1);
						}else if(getLastVisiblePosition() - npos <= 2){						
							this.setSelection(nFirst+1);
						}	
					}					
					ntime++;
				}				
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			default:
				mDragMode = false;
				mEndPosition = pointToPosition(x,y);
				stopDrag(mEndPosition - getFirstVisiblePosition());
				
				break;
		}
		return true;		
	}	
	
	// move the drag view
	private void drag(int nposition, int x, int y,View viewItem) {
		if (mDragView != null) {
			
			WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
			layoutParams.x = mDragPointOffsetX;
			layoutParams.y = y - mDragPointOffsetY;
			WindowManager mWindowManager = (WindowManager) getContext()
					.getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.updateViewLayout(mDragView, layoutParams);

			if (mDragListener != null)
				mDragListener.onDrag(nposition, viewItem);// change null to "this" when ready to use
		}
	}

	// enable the drag view for dragging
	private void startDrag(int itemIndex, int y) {
		//stopDrag(itemIndex);

		View item = getChildAt(itemIndex);
		if (item == null) 
			return;
		
		item.setDrawingCacheEnabled(true);
		if (mDragListener != null)
			mDragListener.onStartDrag(mStartPosition,item);
		
        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
        
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = mDragPointOffsetX;
        mWindowParams.y = y - mDragPointOffsetY;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;
        
        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);      

        WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
	}

	// destroy drag view
	private void stopDrag(int itemIndex) {
		if (mDragView != null) {
			if (mDragListener != null)
				mDragListener.onStopDrag(mEndPosition,getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
	}


	
	

}
