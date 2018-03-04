package com.id_photo.geniuben;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.util.*;
import android.view.*;

/**
 * Created by Administrator on 2018/2/23.
 */

public class FloatMenu extends View{

    public static int width;
    int color;
    Paint paint;
    int id;
    Bitmap bitmap;
    Rect ad;
    public static FloatMenu f;

    public FloatMenu(Context context,int color,Bitmap bitmap){
        super(context);
        paint = new Paint();
        paint.setColor(color);
        this.bitmap = bitmap;
        ad = new Rect(50,50,width-50,width-50);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画大圆
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //paint.setColor(color);
        canvas.drawCircle(width/2,width/2,width/2,paint);
        canvas.drawBitmap(bitmap,null,ad,paint);
        //canvas.drawLine(10,width/2,width-10,width/2,paint);
        //canvas.drawLine(width/2,10,width/2,width-10,paint);
        //invalidate();
    }



    public FloatMenu init(int id,Bitmap bitmap){
        this.id = id;
        this.bitmap = bitmap;
        return FloatMenu.this;
    }

    public FloatMenu setWidth(int width) {
        this.width = width;
        ad = new Rect(10,10,width-10,width-10);
        return FloatMenu.this;
    }

    public FloatMenu setColor(int color) {
        this.color = color;
        paint.setColor(Color.RED);
        return FloatMenu.this;
    }

    public void show(boolean open){

    }

}