package com.id_photo.geniuben;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.view.View.*;
import android.view.*;
import java.io.*;
import android.view.inputmethod.*;
import android.view.ViewGroup.*;
import android.app.*;
import android.os.*;

public class MainActivity extends AppCompatActivity 
{

	String fileName;
	Bitmap bitmap,srcBitmap;
	OpenCvImage toGrabcut;
	RectImageView riv;

	EditText pen_big;
	Button make,paint,alpha,save;

	String color;
	int myColor = 0;

	boolean drawRect = false;
	boolean iskou = false;

	ProgressDialog progressDial;

	public static final int UPDATE_RIV = 1;
	public static final int CHECK_FACR = 2;

    private Handler handler = new Handler() {

	public void handleMessage(Message msg) {
		switch (msg.what) {
			case UPDATE_RIV:
				// 在这里可以进行UI操作

				riv.init();
				riv = (RectImageView)findViewById(R.id.grabcut_image);
				riv.setImageBitmap(srcBitmap);
				if(toGrabcut.checkface){
					bitmap = Bitmap.createBitmap(bitmap,toGrabcut.ssx,toGrabcut.ssy,toGrabcut.sex-toGrabcut.ssx,toGrabcut.sey-toGrabcut.ssy,null,false);
				}
				riv.getPenEdit(pen_big);
				riv.set(srcBitmap,bitmap);
				riv.setColor(color);
				iskou = true;
				break;
			case CHECK_FACR:
				progressDial.dismiss();
				if(!toGrabcut.checkface){
					AlertDialog.Builder dialog = new AlertDialog.Builder (MainActivity.
							this);
					dialog.setTitle("未发现人像");
					dialog.setMessage("可能由于算法原因未发现人像，是否使用手动选取抠图");
					dialog.setCancelable(false);
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
							riv.init();
							riv.setRect(true);
							drawRect = true;
						}
					});
					dialog.show();
				}
				break;
			default:
				break;
		}
	}
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grabcuttheimage);

		Intent in = this.getIntent();
		Bundle bundle=in.getExtras();
		color = bundle.getString("COLOR");
        fileName = bundle.getString("filename");
		bitmap = BitmapFactory.decodeFile(fileName);

		riv = (RectImageView)findViewById(R.id.grabcut_image);
		make = (Button)findViewById(R.id.grabcut_do);
		paint = (Button)findViewById(R.id.paint);
		alpha = (Button)findViewById(R.id.alpha);
		save = (Button)findViewById(R.id.grabcut_save);
		pen_big = (EditText)findViewById(R.id.pen_big);

		riv.setImageBitmap(bitmap);

		toGrabcut = new OpenCvImage(this);

		make.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					if(drawRect){
						toGrabcut.sx = riv.rectsx;
						toGrabcut.sy = riv.rectsy;
						toGrabcut.wi = riv.rectex - riv.rectsx;
						toGrabcut.he = riv.rectey - riv.rectsx;
						toGrabcut.drawRect = true;
					}

					final ProgressDialog progressDialo = new ProgressDialog
					(MainActivity.this);

					progressDialo.setTitle("处理中...");
					progressDialo.setMessage("请稍后...");
					progressDialo.setCancelable(false);
					progressDialo.setCanceledOnTouchOutside(false);
					progressDialo.show();

					new Thread(new Runnable(){

							@Override
							public void run()
							{
								// TODO: Implement this method
								riv.setRect(false);
								srcBitmap = toGrabcut.MyGrabcut(bitmap,color);
								Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();
								Message message = new Message();
								message.what = UPDATE_RIV;
								handler.sendMessage(message); // 将Message对象发送出去
								progressDialo.dismiss();
							}
						}).start();
				}


			});

		paint.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					riv.penColor(false);
				}


			});

		alpha.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					riv.penColor(true);
				}


			});

		save.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					int num = Save.getNum("抠图",MainActivity.this);
					if (!iskou)
						try
						{
							Save.save("抠图" + num + ".jpg", bitmap, 100);
							Toast.makeText(MainActivity.this,"保存至:简单证件照/抠图"+num+".jpg",Toast.LENGTH_SHORT).show();
							Save.SaveNum("抠图",++num,MainActivity.this);
						}
						catch (IOException e)
						{}
					else{
						Bitmap b = riv.getBitmap();

						b = mergeBitmap(srcBitmap,b);

						float scaleWidth = ((float) 358) / b.getWidth();
						float scaleHeight = ((float) 441) / b.getHeight();
						// 取得想要缩放的matrix参数
						Matrix matrix = new Matrix();
						matrix.postScale(scaleWidth, scaleHeight);
						// 得到新的图片
						b = Bitmap.createBitmap(b, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
						try
						{
							if(iskou)
								Save.save("抠图"+num+".jpg", b, 100);
							Toast.makeText(MainActivity.this,"保存至:简单证件照/抠图"+num+".jpg",Toast.LENGTH_SHORT).show();
							Save.SaveNum("抠图",++num,MainActivity.this);
						}
						catch (IOException e)
						{}
					}
					
				}
			});

		progressDial = new ProgressDialog(MainActivity.this);
		progressDial.setTitle("识别中...");
		progressDial.setMessage("请稍后...");
		progressDial.setCancelable(false);
		progressDial.setCanceledOnTouchOutside(false);
		progressDial.show();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					toGrabcut.detectFace(bitmap);

					Message message = new Message();
					message.what = CHECK_FACR;
					handler.sendMessage(message);

				}


			}).start();
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
}





