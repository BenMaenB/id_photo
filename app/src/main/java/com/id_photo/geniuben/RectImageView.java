package com.id_photo.geniuben;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Paint.Style;  
import android.graphics.RectF;  
import android.util.AttributeSet;
import android.view.*;
import android.view.View.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.widget.*;  

public class RectImageView extends android.support.v7.widget.AppCompatImageView implements OnTouchListener
{

	private Paint mPaint,mpaint,mypaint;
    private Bitmap fgBitmpa,srcBitmap,map,bp;
    private Path mPath;
    private Canvas mCanvas = null;
	boolean t = false,isdrawRect = false;
	private final Paint paint;  
	private final Context context;
	private int sx = 0,sy = 0,ex = 0,ey = 0;//width,height;
	int rivW,rivH;
	int left,top,right,end;
	int penWidth = 25;
	EditText penbig;
	String color;

	boolean isIntent = false;

	boolean drawRect = false;
	public int rectsx=-1,rectsy=-1,rectex=-1,rectey=-1,recsx=-1,recsy=-1,recex=-1,recey=-1;

	public AttributeSet attr;
	@Override
	public boolean onTouch(View view, MotionEvent event) 
	{  
		/*
		 float eventX = event.getX();
		 float eventY = event.getY();
		 float[] eventXY = new float[] {eventX, eventY};

		 Matrix invertMatrix = new Matrix();
		 ((ImageView)view).getImageMatrix().invert(invertMatrix);

		 invertMatrix.mapPoints(eventXY);
		 int x = Integer.valueOf((int)eventXY[0]);
		 int y = Integer.valueOf((int)eventXY[1]);

		 Drawable imgDrawable = ((ImageView)view).getDrawable();
		 Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

		 //Limit x, y range within bitmap
		 if(x < 0)
		 {
		 x = 0;
		 }
		 else if(x > bitmap.getWidth()-1)
		 {
		 x = bitmap.getWidth()-1;
		 }

		 if(y < 0)
		 {
		 y = 0;
		 }
		 else if(y > bitmap.getHeight()-1)
		 {
		 y = bitmap.getHeight()-1;
		 }

		 int touchedRGB = bitmap.getPixel(x, y);


		 switch(event.getAction()){
		 case MotionEvent.ACTION_DOWN:
		 sx = x;
		 sy = y;
		 break;
		 case MotionEvent.ACTION_MOVE:
		 ex = x;
		 ey = y;
		 break;
		 }
		 */
		float eventX = event.getX();
		float eventY = event.getY();
		float[] eventXY = new float[] {eventX, eventY};

		Matrix invertMatrix = new Matrix();
		((ImageView)view).getImageMatrix().invert(invertMatrix);

		invertMatrix.mapPoints(eventXY);
		int x = Integer.valueOf((int)eventXY[0]);
		int y = Integer.valueOf((int)eventXY[1]);

		Drawable imgDrawable = ((ImageView)view).getDrawable();
		Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

		//Limit x, y range within bitmap
		if(x < 0)
		{
			x = 0;
		}
		else if(x > bitmap.getWidth()-1)
		{
			x = bitmap.getWidth()-1;
		}

		if(y < 0)
		{
			y = 0;
		}
		else if(y > bitmap.getHeight()-1)
		{
			y = bitmap.getHeight()-1;
		}

		int touchedRGB = bitmap.getPixel(x, y);


		sx = x;
		sy = y;
		switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
				if(drawRect){
					rectsx = sx;
					rectsy = sy;
					recsx = (int) eventX;
					recsy = (int) eventY;
				}
				else{
					mPath.reset();
					mPath.moveTo(sx,sy);//event.getX(), event.getY());
				}
				isdrawRect = true;
                break;

            case MotionEvent.ACTION_MOVE:
				if(drawRect){
					rectex = sx;
					rectey = sy;
					recex = (int) eventX;
					recey = (int) eventY;
				}
				else
					mPath.lineTo(sx,sy);//event.getX(), event.getY());
				//isdrawRect = true;

                break;
			case MotionEvent.ACTION_UP:
				isdrawRect = false;
				break;
        }

		//mCanvas.drawBitmap(srcBitmap,0,0,null);
		//mCanvas.drawPath(mPath, mypaint);

		if(!drawRect){
			String s = penbig.getText().toString();
			if(!s.equals("")){
				int i = 25;
			if(Integer.parseInt(s)>100)
				i = 100;
			if(i>100){
				i = 100;
				penbig.setText(Integer.toString(i));
				}
				else
			penWidth = setPenBig(i);
			}
			else{
				penbig.setText(Integer.toString(1));
				penWidth = setPenBig(1);
			}

			fgBitmpa = Bitmap.createBitmap(map.getWidth(), map.getHeight(), Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(fgBitmpa);
			mCanvas.drawBitmap(map,0,0,null);
			mCanvas.drawBitmap(srcBitmap,0,0,null);
			mCanvas.drawPath(mPath, mypaint);
			mCanvas.save(Canvas.ALL_SAVE_FLAG);
			mCanvas.restore();
			srcBitmap = fgBitmpa;
			invalidate();
		}
		return true;
	}

	public void init() {

		isIntent = true;

		setOnTouchListener(this);

		mpaint = new Paint();
		mpaint.setAntiAlias(true);
		mpaint.setStyle(Paint.Style.STROKE);
		mpaint.setStrokeCap(Paint.Cap.ROUND);
		mpaint.setStrokeJoin(Paint.Join.ROUND);
		mpaint.setStrokeWidth(25);

        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(25);
        mPaint.setAntiAlias(true);
        //让笔触和连接处更加圆滑
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置交叉模式
    }

	public RectImageView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
		attr = attrs;
		// TODO Auto-generated constructor stub  
		this.context = context;  
		this.paint = new Paint();  
		this.paint.setTextSize(1000);
		this.paint.setColor(10);
		this.paint.setAntiAlias(true); //消除锯齿   
		this.paint.setStyle(Style.STROKE);  //绘制空心圆或 空心矩形   
		setDrawingCacheEnabled(true);  
	}   
	@Override  
	protected void onDraw(Canvas canvas) {  
		// TODO Auto-generated method stub  
		super.onDraw(canvas);

		paint.setColor(Color.BLUE);
		//width = canvas.getWidth();
		//height = canvas.getHeight();
		if(drawRect)
			canvas.drawRect(recsx,recsy,recex,recey,paint);

		if(!drawRect){
			if(t)
			{
				//canvas.drawBitmap(,0,0,null);
				Rect rec1 = new Rect(0,0,map.getWidth()-1,map.getHeight()-1);
				Rect rec2 = new Rect(left,top,right,end);
				canvas.drawBitmap(map,rec1,rec2,paint);
				rec1 = new Rect(0,0,fgBitmpa.getWidth()-1,fgBitmpa.getHeight()-1);
				canvas.drawBitmap(fgBitmpa,rec1,rec2,paint);
				canvas.drawRect(left,top,right,end,paint);
				invalidate();
			}

			if(isdrawRect){
				bp = map;
				int a,s,w,h;
				a = (sx-50>0)?sx-50:0;
				s = (sy-50>0)?sy-50:0;
				w = (sx+50<bp.getWidth()-1)?sx+50:bp.getWidth()-1;
				h = (sy+50<bp.getHeight()-1)?sy+50:bp.getHeight()-1;
				//bp = Bitmap.createBitmap(bp,a,s,w,h,null,false);
				//np = Bitmap.createBitmap(srcBitmap,a,s,w,h,null,false);
				//bp = TowBitmap(bp,np);

				Rect rect1 = new Rect(10,10,210,210);
				Rect rect2 = new Rect(a,s,w,h);
				canvas.drawRect(0,0,220,220,paint);
				canvas.drawBitmap(bp,rect2,rect1,paint);
				canvas.drawBitmap(srcBitmap,rect2,rect1,paint);
				canvas.drawCircle(110,110,penWidth,paint);
			}
		}
		invalidate();


	}  

	public void penColor(boolean isAlpha){
		if(!isAlpha){
			mypaint = mpaint;
		}
		else{
			mypaint = mPaint;

		}
	}

	public void setColor(String color){
		switch(color){
			case "红色":
				mpaint.setColor(Color.RED);
				break;
			case "白色":
				mpaint.setColor(Color.WHITE);
				break;
			case "蓝色":
				mpaint.setColor(Color.rgb(67,142,219));
				break;
		}
	}

	private void setWH(){
		Matrix matrix = getImageMatrix();
		RectF rectF = new RectF();
		Drawable drawable = getDrawable();
		if (drawable != null) {
			rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			matrix.mapRect(rectF);
		}

		top = (int) rectF.top;
		right = (int) rectF.right;
		left = (int) rectF.left;
		end = (int) rectF.bottom;
	}

	public void set(Bitmap bit,Bitmap map){
		t = true;
		srcBitmap = bit;
		this.map = map;

		fgBitmpa = Bitmap.createBitmap(map.getWidth(), map.getHeight(), Bitmap.Config.ARGB_8888);
		//创建一个canvas并关联到fgBitmap
		mCanvas = new Canvas(fgBitmpa);
		//在canvas上绘制灰色背景，并且会关联到fgBitmap上
		mCanvas.drawBitmap(srcBitmap,0,0,null);
		//mCanvas.drawColor(Color.BLUE);
		mCanvas.drawBitmap(map,0,0,null);
		mCanvas.drawBitmap(srcBitmap,0,0,null);

		mypaint = mpaint;

        mPath = new Path();

		setWH();

	}

	public void setRect(boolean drawRect){
		this.drawRect = drawRect;
	}

	public void getPenEdit(EditText pb){
		penbig = pb;
	}


	public Bitmap getBitmap(){
		mPath.reset();
		mPath.moveTo(0,0);
		mPath.lineTo(0,0);
		mypaint = mpaint;
		mpaint.setStrokeWidth(1);
		fgBitmpa = Bitmap.createBitmap(map.getWidth(), map.getHeight(), Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(fgBitmpa);
		mCanvas.drawBitmap(map,0,0,null);
		mCanvas.drawBitmap(srcBitmap,0,0,null);
		mCanvas.drawPath(mPath, mypaint);
		mCanvas.save(Canvas.ALL_SAVE_FLAG);
		mCanvas.restore();
		srcBitmap = fgBitmpa;
		return fgBitmpa;
	}

	private int setPenBig(int b){
		mPaint.setStrokeWidth(b);
		mpaint.setStrokeWidth(b);
		return b;
	}

	/* 根据手机的分辨率从 dp 的单位 转成为 px(像素) */    
	public static int dip2px(Context context, float dpValue) {    
		final float scale = context.getResources().getDisplayMetrics().density;    
		return (int) (dpValue * scale + 0.5f);    
	} 

	public float getSx(){
		return sx;
	}
	public float getSy(){
		return sy;
	}
	public float getEx(){
		return ex;
	}
	public float getEy(){
		return ey;
	}



}
