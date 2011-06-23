package com.smit.MyView;

import com.smit.EasyLauncher.R;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;

public class MyViewctrl extends ImageButton {

	private Bitmap img, r_img;
	private Canvas canvas;
	private Paint paint;
	Matrix matrix;
	Bitmap viewbitmap = null;
	int mViewWidth = 0, mViewHeight = 0;
	float mAngle;
	public int curid;

	private void Init() {
		// BitmapFactory：从源创建一个Bitmap对象，这些源包括：文件，流或者数组
		paint = new Paint();
		matrix = new Matrix();
		// 让矩阵实现翻转，参数为FLOAT型
		matrix.postRotate(mAngle);
		int width = img.getWidth();
		int height = img.getHeight();
		// 源Bitmap通过一个Matrix变化后，返回一个不可变的Bitmap
		r_img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
	}

	public MyViewctrl(Context context) {
		super(context);
		// Init();
	}

	public MyViewctrl(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.myview);
		int icon = a.getResourceId(R.styleable.myview_icon, 0);

		img = BitmapFactory.decodeResource(context.getResources(), icon);
		mAngle = a.getFloat(R.styleable.myview_angle, 0);
		Init();
	}

	public MyViewctrl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// Init();
	}
	

	// 在自定义VIEW时，必须实现此方法
	@Override
	public void onDraw(Canvas canvas) {
		// 在重写父类的方法时，必须先调用父类的方法
		Region region = new Region(0, 0, 1, 1);
		// 利用Canvas在View上绘制一个Bitmap，并设置它的样式和颜色
		int mwidth = getWidth();
		int mheight = getHeight();

		canvas.drawBitmap(r_img, 0, 0, paint);

		Rect rc = canvas.getClipBounds();
		paint.setColor(Color.RED);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		canvas.drawText("ABC", 0, 0, paint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	//设置图片
	public void setIcon(Bitmap bitmap){
		img=bitmap;
	}
	//设置角度
	public void setangle(float angle){
		mAngle=angle;
	}
	
	public void createctrl(){
		Init();
	}
	
	public void SetCurId(int id) {
		curid = id;
	}
	public int getCurId() {
		return curid;
	}
}
