package com.smit.EasyLauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

public class myImageview extends ImageView {

	public static final int ajd = 0;
	public static final int aje = 1;
	private Matrix ajf;
	private int ajg;
	public myImageview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	    Matrix localMatrix = new Matrix();
	    this.ajf = localMatrix;
	    this.ajg = 0;
	}
	public void ch(int paramInt)
	{
		this.ajg = paramInt;
		invalidate();
	}
    @Override
    public void draw(Canvas arg0) {
    	// TODO Auto-generated method stub
    	int i = arg0.getSaveCount();
    	arg0.save();
    	float f1 = getPaddingLeft();
    	float f2 = getPaddingTop();
    	arg0.translate(f1, f2);
    	if (this.ajf != null)
    	{
    		Matrix localMatrix = this.ajf;
    		arg0.concat(localMatrix);
    	}
    	getDrawable().draw(arg0);
    	arg0.restoreToCount(i);
    }
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
    	// TODO Auto-generated method stub
    	//return super.setFrame(l, t, r, b);
    	float f1=0;
    	int i = 1056964608;
    	boolean bool = super.setFrame(l, t, r, b);
    	int j = getDrawable().getIntrinsicWidth();
    	int k = getDrawable().getIntrinsicHeight();
    	int width = getWidth();
    	int i1 = getPaddingLeft();
    	width -= i1;
    	i1 = getPaddingRight();
    	width -= i1;
    	i1 = getHeight();
    	int i2 = getPaddingTop();
    	i1 -= i2;
    	int i3 = getPaddingBottom();
    	i1 -= i3;
    	if (this.ajg == 270)
    	{
    		Matrix localMatrix1 = this.ajf;
    		float f2 = this.ajg;
    		localMatrix1.setRotate(f2, f1, f1);
    		int i4 = j / 2;
    		int i5 = k / 2;
    		Matrix localMatrix2 = this.ajf;
    		float f3 = -i4;
    		float f4 = -i5;
    		localMatrix2.preTranslate(f3, f4);
    		Matrix localMatrix3 = this.ajf;
    		float f5 = i5;
    		float f6 = i4;
    		localMatrix3.postTranslate(f5, f6);
    		Matrix localMatrix4 = this.ajf;
    		float f7 = (int)((width - k) * i + i);
    		float f8 = (int)((i1 - j) * i + i);
    		localMatrix4.postTranslate(k, j);
    	}
    	while (true)
    	{
    		//return bool;
    		if(this.ajg != 0)
    			continue;
    		Matrix localMatrix5 = this.ajf;
    		float f9 = (int)((l - j) * i + i);
    		float f10 = (int)((i1 - k) * i + i);
    		localMatrix5.setTranslate(j, k);
    	}
    }
}
