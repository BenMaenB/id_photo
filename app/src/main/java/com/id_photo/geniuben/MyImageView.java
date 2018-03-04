package com.id_photo.geniuben;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by GeniuBen on 2018/2/13.
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {

    private int touchX = 0,touchY = 0;      //触摸点相对ImageView坐标
    private int rectIsx = 0,rectIsy = 0,rectIex = 0,rectIey = 0;      //没人脸是手动扩选框坐标(相对图片)
    private int rectsx =0,rectsy = 0,rectex = 0,rectey = 0;      //绘制人脸扩选框
    private boolean haveFace = false,t = false,isdrawRect = false;     //是否检测到人脸

    private Path mPath = null;      //保存修图触摸路径
    private Paint paint = null,alphapaint = null,colorpaint = null;      //画笔,橡皮

    private EditText getpenSize = null;    //获取画笔大小
    private int penSize = 25;

    private Bitmap fgBitmpa = null,bcBitmap = null;
    private Canvas mCanvas = null;

    int left,top,right,end;

    Context context = null;
    AttributeSet attr = null;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        attr = attrs;
        // TODO Auto-generated constructor stub
        this.context = context;
        this.colorpaint = new Paint();
        this.colorpaint.setTextSize(1000);
        this.colorpaint.setColor(Color.RED);
        this.colorpaint.setAntiAlias(true); //消除锯齿
        this.colorpaint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形
        setDrawingCacheEnabled(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!haveFace){
                    rectIsx = touchX;
                    rectIsy = touchY;
                    rectsx = (int) eventX;
                    rectsy = (int) eventY;
                }
                else{
                    mPath.reset();
                    mPath.moveTo(touchX,touchY);
                }
                haveFace = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if(!haveFace){
                    rectex = touchX;
                    rectey = touchY;
                    rectex = (int) eventX;
                    rectey = (int) eventY;
                }
                else
                    mPath.lineTo(touchX,touchY);//event.getX(), event.getY());
                //isdrawRect = true;

                break;
            case MotionEvent.ACTION_UP:
                haveFace = false;
                break;
        }

        if(!haveFace) {
            String s = getpenSize.getText().toString();
            if (!s.equals("")) {
                int i = Integer.parseInt(s);
                if (i > 100) {
                    i = 100;
                    getpenSize.setText(Integer.toString(i));
                } else
                    penSize = setPenSize(i);
            } else {
                getpenSize.setText(Integer.toString(1));
                penSize = setPenSize(1);
            }
        }
            composeBitmap();

        return true;
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(!haveFace)
            canvas.drawRect(rectsx,rectsy,rectex,rectey,paint);

        if(haveFace){
            if(t)
            {
                //canvas.drawBitmap(,0,0,null);
                Rect rec1 = new Rect(0,0,fgBitmpa.getWidth()-1,fgBitmpa.getHeight()-1);
                Rect rec2 = new Rect(left,top,right,end);
                canvas.drawBitmap(fgBitmpa,rec1,rec2,paint);
                rec1 = new Rect(0,0,fgBitmpa.getWidth()-1,fgBitmpa.getHeight()-1);
                canvas.drawBitmap(fgBitmpa,rec1,rec2,paint);
                canvas.drawRect(left,top,right,end,paint);
                //penbig.setText(99);
                invalidate();
            }

            if(isdrawRect){
                Bitmap bp = fgBitmpa;
                int a,s,w,h;
                a = (touchX-50>0)?touchX-50:0;
                s = (touchY-50>0)?touchY-50:0;
                w = (touchX+50<bp.getWidth()-1)?touchX+50:bp.getWidth()-1;
                h = (touchY+50<bp.getHeight()-1)?touchY+50:bp.getHeight()-1;
                //bp = Bitmap.createBitmap(bp,a,s,w,h,null,false);
                //np = Bitmap.createBitmap(srcBitmap,a,s,w,h,null,false);
                //bp = TowBitmap(bp,np);

                Rect rect1 = new Rect(10,10,210,210);
                Rect rect2 = new Rect(a,s,w,h);
                canvas.drawRect(0,0,220,220,paint);
                canvas.drawBitmap(bp,rect2,rect1,paint);
                canvas.drawBitmap(fgBitmpa,rect2,rect1,paint);
                canvas.drawCircle(110,110,penSize,paint);
            }
        }

        invalidate();
    }

    private void getTouch(View view,float eventX,float eventY){
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

        touchX = x;
        touchY = y;
    }

    private int setPenSize(int b){
        alphapaint.setStrokeWidth(b);
        colorpaint.setStrokeWidth(b);
        return b;
    }

    public void penColor(boolean isAlpha){
        if(!isAlpha){
            paint = colorpaint;
        }
        else{
            paint = alphapaint;

        }
    }

    public void setColor(String color){
        switch(color){
            case "红色":
                colorpaint.setColor(Color.RED);
                break;
            case "白色":
                colorpaint.setColor(Color.WHITE);
                break;
            case "蓝色":
                colorpaint.setColor(Color.rgb(67,142,219));
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
        fgBitmpa = bit;
        this.fgBitmpa = map;

        fgBitmpa = Bitmap.createBitmap(map.getWidth(), map.getHeight(), Bitmap.Config.ARGB_8888);
        //创建一个canvas并关联到fgBitmap
        mCanvas = new Canvas(fgBitmpa);
        //在canvas上绘制灰色背景，并且会关联到fgBitmap上
        mCanvas.drawBitmap(fgBitmpa,0,0,null);
        //mCanvas.drawColor(Color.BLUE);
        mCanvas.drawBitmap(map,0,0,null);
        mCanvas.drawBitmap(fgBitmpa,0,0,null);

        paint = colorpaint;

        mPath = new Path();

        setWH();

    }

    public void setRect(boolean drawRect){
        this.haveFace = !drawRect;
    }

    public void getPenEdit(EditText pb){
        getpenSize = pb;
    }


    public Bitmap getBitmap(){
        Bitmap bitmap = null;
        mPath.reset();
        mPath.moveTo(0,0);
        mPath.lineTo(0,0);
        paint = colorpaint;
        colorpaint.setStrokeWidth(1);
        bitmap = Bitmap.createBitmap(fgBitmpa.getWidth(), fgBitmpa.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
        mCanvas.drawBitmap(bitmap,0,0,null);
        mCanvas.drawBitmap(fgBitmpa,0,0,null);
        mCanvas.drawPath(mPath, colorpaint);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        fgBitmpa = bitmap;
        return bitmap;
    }

    /* 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public float getSx(){
        return rectIsx;
    }
    public float getSy(){
        return rectIsy;
    }
    public float getEx(){
        return rectIex;
    }
    public float getEy() { return rectIey; }

    private void composeBitmap(){
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(bcBitmap.getWidth(), bcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
        mCanvas.drawBitmap(bcBitmap,0,0,null);
        mCanvas.drawBitmap(fgBitmpa,0,0,null);
        mCanvas.drawPath(mPath, colorpaint);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        fgBitmpa = bitmap;
        invalidate();
    }
}
