package com.id_photo.geniuben;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.IOException;

public class Main extends AppCompatActivity {

    private final int MAKE = 0,CUT = 1,ZOOM = 2,COMPRESS =3;      //模式：抠图，剪切，缩放，压缩
    private final int CUT_GUDING = 1;
    private int TYPE = -1;                                        //当前模式

    private Bitmap photo = null,bitmap = null,photo_copy;                                //传入图片
    String filename = null;                                     //传入图片路径

    // MyImageView imageView = null;                               //负责图片处理的ImageView

    private CutImage cutImage = null;
    private Compress compress;
    RectImageView imageView;
    OpenCvImage toGrabcut;

    FloatMenuManager floatMenuManager = null;

    boolean iskou = false,drawRect = false;
    EditText pen_size = null;
    ImageView pen_img = null;
    String color;

    int saveWith,saveHeight;

    private boolean clickMake = false;

    private DrawerLayout mDrawerLayout;

    public static final int UPDATE_RIV = 1;
    public static final int CHECK_FACR = 2;
    public static final int DISS_PEN = 3;
    ProgressDialog progressDial;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_RIV:
                    // 在这里可以进行UI操作

                    imageView.init();
                    imageView.setImageBitmap(bitmap);
                    if(toGrabcut.checkface){
                        photo_copy = photo = Bitmap.createBitmap(photo,toGrabcut.ssx,toGrabcut.ssy,toGrabcut.sex-toGrabcut.ssx,toGrabcut.sey-toGrabcut.ssy,null,false);
                    }
                    imageView.getPenEdit(pen_size);
                    imageView.set(bitmap,photo);
                    imageView.setColor(color);
                    iskou = true;
                    break;
                case CHECK_FACR:
                    pen_size.setVisibility(View.VISIBLE);
                    pen_img.setVisibility(View.VISIBLE);
                    if(!toGrabcut.checkface){
                        AlertDialog.Builder dialog = new AlertDialog.Builder (Main.this);
                        dialog.setTitle("未发现人像");
                        dialog.setMessage("可能由于算法原因未发现人像，是否使用手动选取抠图");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("取消", new DialogInterface.
                                OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.setNegativeButton("确认", new DialogInterface.
                                OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageView.init();
                                imageView.setRect(true);
                                drawRect = true;
                            }
                        });
                        dialog.show();
                        imageView.setVisibility(View.VISIBLE);
                        if(cutImage != null)
                            cutImage.close();
                    }
                    break;
                case DISS_PEN:
                    pen_size.setVisibility(View.GONE);
                    pen_img.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();

        pen_size = (EditText)findViewById(R.id.pen_big);
        pen_img = (ImageView)findViewById(R.id.pen_txt);

        toGrabcut = new OpenCvImage(this);
        imageView = (RectImageView)findViewById(R.id.grabcut_image);
        photo = getBitmap();
        imageView.setImageBitmap(photo);


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final int[] selectedFruitIndex = {0};
                switch(item.getItemId()) {
                    case R.id.make:
                        imageView.setVisibility(View.VISIBLE);
                        if(cutImage!=null)
                            cutImage.close();

                        if(TYPE != MAKE) {
                            Toast.makeText(MyContext.getContext(), "制作", Toast.LENGTH_SHORT).show();
                            final String[] arrayFruit = new String[]{"红色", "蓝色", "白色"};

                            Dialog alertDialog = new AlertDialog.Builder(Main.this).
                                    setTitle("请选择背景色:").
                                    setIcon(R.drawable.ic_launcher)
                                    .setSingleChoiceItems(arrayFruit, 0, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            selectedFruitIndex[0] = which;
                                        }
                                    }).
                                            setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(Main.this, arrayFruit[selectedFruitIndex[0]], Toast.LENGTH_SHORT).show();
                                                    color = arrayFruit[selectedFruitIndex[0]];
                                                    //人脸检测
                                                    progressDial = new ProgressDialog(Main.this);
                                                    progressDial.setTitle("识别中...");
                                                    progressDial.setMessage("请稍后...");
                                                    progressDial.setCancelable(false);
                                                    progressDial.setCanceledOnTouchOutside(false);
                                                    progressDial.show();
                                                    new Thread(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            // TODO: Implement this method
                                                            toGrabcut.detectFace(photo);
                                                            photo_copy = photo;
                                                            Message message = new Message();
                                                            message.what = CHECK_FACR;
                                                            handler.sendMessage(message);
                                                            TYPE = MAKE;
                                                            progressDial.dismiss();
                                                        }
                                                    }).start();
                                                }
                                            }).
                                            setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                                                }
                                            }).
                                            create();
                            alertDialog.show();

                            if (floatMenuManager != null)
                                floatMenuManager.remove();
                            floatMenuManager = new FloatMenuManager(Main.this, Main.this);
                            floatMenuManager.init((int)((150.0/1080.0)*getContextWidth()), 10, 1, R.drawable.menu);
                            floatMenuManager.show();
                            floatMenuManager.addMenu(2, R.drawable.fix).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (drawRect) {
                                        toGrabcut.sx = imageView.rectsx;
                                        toGrabcut.sy = imageView.rectsy;
                                        toGrabcut.wi = imageView.rectex - imageView.rectsx;
                                        toGrabcut.he = imageView.rectey - imageView.rectsx;
                                        toGrabcut.drawRect = true;
                                    }

                                    final ProgressDialog progressDialo = new ProgressDialog
                                            (Main.this);

                                    progressDialo.setTitle("处理中...");
                                    progressDialo.setMessage("请稍后...");
                                    progressDialo.setCancelable(false);
                                    progressDialo.setCanceledOnTouchOutside(false);
                                    progressDialo.show();

                                    new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO: Implement this method
                                            imageView.setRect(false);
                                            bitmap = toGrabcut.MyGrabcut(photo, color);
                                            Toast.makeText(Main.this, "success", Toast.LENGTH_SHORT).show();
                                            Message message = new Message();
                                            message.what = UPDATE_RIV;
                                            handler.sendMessage(message); // 将Message对象发送出去
                                            progressDialo.dismiss();
                                        }
                                    }).start();

                                    if (!clickMake) {
                                        floatMenuManager.addMenu(3, R.drawable.paint).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                imageView.penColor(false);
                                            }
                                        });
                                        floatMenuManager.addMenu(4, R.drawable.rubber).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                imageView.penColor(true);
                                            }
                                        });
                                        floatMenuManager.addMenu(5, R.drawable.save).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                int num = Save.getNum("抠图", Main.this);
                                                if (!iskou)
                                                    try {
                                                        Save.save("抠图" + num + ".jpg", bitmap, 100);
                                                        Toast.makeText(Main.this, "保存至:我的证件照/抠图" + num + ".jpg", Toast.LENGTH_SHORT).show();
                                                        Save.SaveNum("抠图", ++num, Main.this);
                                                    } catch (IOException e) {
                                                    }
                                                else {
                                                    Bitmap b = imageView.getBitmap();

                                                    b = mergeBitmap(photo_copy, b);

                                                    b = Zoom.zoom(b, saveWith, saveHeight);

                                                    if (toGrabcut.checkface) {
                                                        b = Zoom.zoom(b, 358, 441);
                                                    }
                                                    try {
                                                        if (iskou)
                                                            Save.save("抠图" + num + ".jpg", b, 100);
                                                        Toast.makeText(Main.this, "保存至:我的证件照/抠图" + num + ".jpg", Toast.LENGTH_SHORT).show();
                                                        Save.SaveNum("抠图", ++num, Main.this);
                                                    } catch (IOException e) {
                                                    }
                                                }
                                            }
                                        });
                                        clickMake = true;
                                    }
                                }
                            });
                        }
                        break;
                    case R.id.cut:
                        if(TYPE != CUT){
                            Toast.makeText(Main.this, "剪切", Toast.LENGTH_SHORT).show();
                            TYPE = CUT;
                            imageView.setVisibility(View.GONE);
                            cutImage = new CutImage(Main.this,photo,(CropImageView) findViewById(R.id.cropImageView));
                            cutImage.show();
                            cutImage.free();

                            if(floatMenuManager!=null)
                                floatMenuManager.remove();
                            floatMenuManager = new FloatMenuManager(Main.this,Main.this);
                            floatMenuManager.init((int)((150.0/1080.0)*getContextWidth()),10,1,R.drawable.menu);
                            floatMenuManager.show();
                            floatMenuManager.addMenu(2,R.drawable.guding).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cutImage.guding();
                                }
                            });
                            floatMenuManager.addMenu(3,R.drawable.free).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cutImage.free();
                                }
                            });
                            floatMenuManager.addMenu(4,R.drawable.bili).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cutImage.bili();
                                }
                            });
                            floatMenuManager.addMenu(5,R.drawable.save).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    photo = cutImage.saveBitmap();
                                }
                            });
                        }
                        break;

                    case R.id.zoom:
                        if(TYPE != ZOOM){
                            TYPE = ZOOM;
                            Toast.makeText(MyContext.getContext(), "缩放", Toast.LENGTH_SHORT).show();
                            new Zoom(Main.this,photo);
                        }
                        break;
                    default:
                        if(TYPE != MAKE){
                            Message message = new Message();
                            message.what = DISS_PEN;
                            handler.sendMessage(message);
                        }
                        /*
                    case R.id.compress:
                        Toast.makeText(MyContext.getContext(), "压缩", Toast.LENGTH_SHORT).show();
                        TYPE = COMPRESS;
                        compress = new Compress(Main.this,filename);
                        try {
                            compress.compress();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                        */
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

/*
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    private Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        canvas.save(Canvas. ALL_SAVE_FLAG);
        //瀛樺偍鏂板悎鎴愮殑鍥剧墖
        canvas.restore();
        return bitmap;
    }

    private Bitmap getBitmap(){
        Bitmap bitmap = null;
        Intent in = getIntent();
        Bundle bundle=in.getExtras();
        filename = bundle.getString("filename");
        photo_copy = bitmap = BitmapFactory.decodeFile(filename);
        saveWith = bitmap.getWidth();
        saveHeight = bitmap.getHeight();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int bi = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            bi = (int) Math.sqrt(bitmap.getAllocationByteCount()/1024/500);
        }
        else
            bi = (int) Math.sqrt(bitmap.getByteCount()/1024/500);
        if(maxMemory > 2048)
            photo_copy = bitmap = decodeSampledBitmapFromResource(filename,bitmap.getWidth()/bi,bitmap.getHeight()/bi);
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(String fileName,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private int getContextWidth(){
        int width;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        return width;
    }
}
