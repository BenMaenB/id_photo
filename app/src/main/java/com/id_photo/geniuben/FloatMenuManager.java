package com.id_photo.geniuben;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import android.os.*;
import android.app.*;
import android.graphics.*;
import android.widget.TableRow.*;
import android.text.*;
import android.util.*;

/**
 * Created by Administrator on 2018/2/23.
 */

public class FloatMenuManager {

    Context context = null;

    FloatMenu floatMenu;
    FloatMenuManager floatMenuManager;
    WindowManager windowManager;
    private WindowManager.LayoutParams floatMenuParams = null;

    int width, height;
    int color;
    Activity activity;
    List<FloatMenu> floatMenuList = new ArrayList<FloatMenu>();
    List<WindowManager.LayoutParams> floatMenuParamsList = new ArrayList<WindowManager.LayoutParams>();

    boolean open = true;
    int up = 1;

    public FloatMenuManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        height = dm.heightPixels;
    }

    public FloatMenuManager init(int width, int color, int id, Bitmap bitmap) {
        this.width = width;
        floatMenu.width = width;
        floatMenu = new FloatMenu(context, Color.RED, bitmap);
        floatMenu.setColor(Color.RED);
        floatMenu.init(id, bitmap);
        return this;
    }

    public FloatMenuManager init(int width, int color, int id, int bitmap) {
        this.width = width;
        floatMenu.width = width;
        floatMenu = new FloatMenu(context, Color.RED, BitmapFactory.decodeResource(context.getResources(),bitmap));
        floatMenu.setColor(Color.RED);
        floatMenu.init(id, BitmapFactory.decodeResource(context.getResources(),bitmap));
        return this;
    }

    public FloatMenuManager setColor(int color) {
        this.color = color;
        floatMenu.setColor(color);
        return this;
    }

    //注册新Menu按钮时，在addMenu后注册点击事件
    public FloatMenu addMenu(int id, Bitmap bitmap) {
        FloatMenu fm = new FloatMenu(context, Color.RED, bitmap);
        fm.setColor(color);
        fm.init(id, bitmap);
        floatMenuList.add(fm);
        floatMenuParamsList.add(new WindowManager.LayoutParams());
        floatMenuParamsList.get(floatMenuList.size() - 1).width = width;
        floatMenuParamsList.get(floatMenuList.size() - 1).height = width;
        floatMenuParamsList.get(floatMenuList.size() - 1).gravity = Gravity.TOP | Gravity.LEFT;

        floatMenuParamsList.get(floatMenuList.size() - 1).type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;;// WindowManager.LayoutParams.TYPE_TOAST;
        IBinder windowToken = activity.getWindow().getDecorView().getWindowToken();
        floatMenuParamsList.get(floatMenuList.size() - 1).token = windowToken;

        floatMenuParamsList.get(floatMenuList.size() - 1).flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        floatMenuParamsList.get(floatMenuList.size() - 1).format = PixelFormat.RGBA_8888;
        floatMenuParamsList.get(floatMenuList.size() - 1).x = floatMenuParams.x;
        floatMenuParamsList.get(floatMenuList.size() - 1).y = floatMenuParams.y + up * floatMenuList.size() * (width);

        windowManager.addView(floatMenuList.get(floatMenuList.size() - 1), floatMenuParamsList.get(floatMenuList.size() - 1));
        return floatMenuList.get(floatMenuList.size() - 1);
    }

    public FloatMenu addMenu(int id, int bitmap) {
        FloatMenu fm = new FloatMenu(context, Color.RED, BitmapFactory.decodeResource(context.getResources(),bitmap));
        fm.setColor(color);
        fm.init(id, BitmapFactory.decodeResource(context.getResources(),bitmap));
        floatMenuList.add(fm);
        floatMenuParamsList.add(new WindowManager.LayoutParams());
        floatMenuParamsList.get(floatMenuList.size() - 1).width = width;
        floatMenuParamsList.get(floatMenuList.size() - 1).height = width;
        floatMenuParamsList.get(floatMenuList.size() - 1).gravity = Gravity.TOP | Gravity.LEFT;

        floatMenuParamsList.get(floatMenuList.size() - 1).type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;;// WindowManager.LayoutParams.TYPE_TOAST;
        IBinder windowToken = activity.getWindow().getDecorView().getWindowToken();
        floatMenuParamsList.get(floatMenuList.size() - 1).token = windowToken;

        floatMenuParamsList.get(floatMenuList.size() - 1).flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        floatMenuParamsList.get(floatMenuList.size() - 1).format = PixelFormat.RGBA_8888;
        floatMenuParamsList.get(floatMenuList.size() - 1).x = floatMenuParams.x;
        floatMenuParamsList.get(floatMenuList.size() - 1).y = floatMenuParams.y + up * floatMenuList.size() * (width);

        windowManager.addView(floatMenuList.get(floatMenuList.size() - 1), floatMenuParamsList.get(floatMenuList.size() - 1));
        return floatMenuList.get(floatMenuList.size() - 1);
    }

    public void show() {
        //floatMenu = new FloatMenu(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (floatMenuParams == null) {
            floatMenuParams = new WindowManager.LayoutParams();
            floatMenuParams.width = width;
            floatMenuParams.height = width;
            floatMenuParams.gravity = Gravity.TOP | Gravity.LEFT;

            floatMenuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;;// WindowManager.LayoutParams.TYPE_TOAST;
            IBinder windowToken = activity.getWindow().getDecorView().getWindowToken();
            floatMenuParams.token = windowToken;

            floatMenuParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            floatMenuParams.format = PixelFormat.RGBA_8888;
        }

        windowManager.addView(floatMenu, floatMenuParams);

        floatMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open = !open;
                showMenu(open);
            }
        });

        floatMenu.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            float tempX;
            float tempY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        tempX = event.getRawX();
                        tempY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - startX;
                        float dy = event.getRawY() - startY;
                        float endx = event.getRawX();
                        float endy = event.getRawY();
                        //计算偏移量，刷新视图
                        floatMenuParams.x += dx;
                        if (open == true)
                            if (((up == 1 && (floatMenuParams.y <= 0 && dy < 0)) || (up == 1 && (floatMenuParams.y >= height - up * (floatMenuList.size() + 1.5) * (width) && dy > 0)))
                                    || ((up == -1 && (floatMenuParams.y < -1 * up * (floatMenuList.size()) * (width)) && dy < 0) || (up == -1 && (floatMenuParams.y > height - 1.5 * width) && dy > 0))) {
                                dy = 0;
                            }
                        floatMenuParams.y += dy;
                        windowManager.updateViewLayout(floatMenu, floatMenuParams);
                        for (int i = 0; i < floatMenuParamsList.size(); i++) {
                            floatMenuParamsList.get(i).x += dx;
                            floatMenuParamsList.get(i).y += dy;
                            if (open)
                                windowManager.updateViewLayout(floatMenuList.get(i), floatMenuParamsList.get(i));
                        }
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;

                }
                return false;
            }

        });
    }

    public int getScreenWidth() {
        return windowManager.getDefaultDisplay().getWidth();
    }

    private void showMenu(boolean open) {
        if (open) {
            if (floatMenuParams.y > height - up * (floatMenuList.size() + 1.5) * (width))
                up = -1;
            else if (floatMenuParams.y + up * (floatMenuList.size() + 1.5) * (width) < 0)
                up = 1;
            for (int i = 0; i < floatMenuList.size(); i++) {
                floatMenuParamsList.get(i).y = floatMenuParams.y + up * (i + 1) * (width);

                windowManager.addView(floatMenuList.get(i), floatMenuParamsList.get(i));
            }
        } else {
            for (FloatMenu f : floatMenuList)
                windowManager.removeView(f);
        }
    }

    public void remove(){
        for (FloatMenu f : floatMenuList)
            windowManager.removeView(f);
        floatMenuList = null;
        floatMenuParamsList = null;
        windowManager.removeView(floatMenu);
    }
}