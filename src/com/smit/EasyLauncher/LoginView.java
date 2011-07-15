package com.smit.EasyLauncher;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class LoginView extends CustomPopupWindow {
	private final View root;
	//private final ImageView mArrowUp;
	//private final ImageView mArrowDown;
	private final Animation mTrackAnim;
	private final LayoutInflater inflater;
	private final Context context;
	
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;
	
	private int animStyle;
	private boolean animateTrack;
	
	public LoginView(View anchor) {
		super(anchor);
		// TODO Auto-generated constructor stub
		
		context		= anchor.getContext();
		inflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		root		= (ViewGroup) inflater.inflate(R.layout.loginpage, null);
		
		setContentView(root);
		
		mTrackAnim 	= AnimationUtils.loadAnimation(anchor.getContext(), R.anim.rail);
		
		mTrackAnim.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
	              // Pushes past the target area, then snaps back into place.
	                // Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = (t * 1.55f) - 1.1f;
				
	            return 1.2f - inner * inner;
	        }
		});
		
		animStyle		= ANIM_GROW_FROM_CENTER;
		animateTrack	= true;
		
		
	}
	/**
	 * Animate track
	 * 
	 * @param animateTrack flag to animate track
	 */
	public void animateTrack(boolean animateTrack) {
		this.animateTrack = animateTrack;
	}
	
	/**
	 * Set animation style
	 * 
	 * @param animStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int animStyle) {
		this.animStyle = animStyle;
	}

	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
//	public void addActionItem(ActionItem action) {
//		actionList.add(action); 
//	}
	
	/**
	 * Show popup window
	 */
	public void show () {
		preShow();

		int[] location 		= new int[2];
		
		anchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
		                	+ anchor.getHeight());
		//LayoutParams.
		root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	//	root.setl
		
		int rootWidth 		= root.getMeasuredWidth();
		int rootHeight 		= root.getMeasuredHeight();

		int screenWidth 	= windowManager.getDefaultDisplay().getWidth();
		//int screenHeight 	= windowManager.getDefaultDisplay().getHeight();

		int xPos 			= (screenWidth - rootWidth) / 2;
		int yPos	 		= anchorRect.top - rootHeight;

		boolean onTop		= true;
		
		// display on bottom
		if (rootHeight > anchor.getTop()) {
			yPos 	= anchorRect.bottom;
			onTop	= false;
		}

	//	showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX());
		
		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		
	//	createActionList();
		//window.setWindowLayoutType(layoutType)
		window.showAtLocation(this.anchor, Gravity.CENTER, 0, 0);
		
		//if (animateTrack) mTrack.startAnimation(mTrackAnim);
	}
	
	/**
	 * Set animation style
	 * 
	 * @param screenWidth Screen width
	 * @param requestedX distance from left screen
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX/2;

		switch (animStyle) {
		case ANIM_GROW_FROM_LEFT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;
					
		case ANIM_GROW_FROM_RIGHT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;
					
		case ANIM_GROW_FROM_CENTER:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;
					
		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopDownMenu_Right : R.style.Animations_PopDownMenu_Right);
			}
					
			break;
		}
	}
}
