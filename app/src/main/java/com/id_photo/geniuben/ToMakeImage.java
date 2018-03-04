package com.id_photo.geniuben;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.widget.SearchView.*;
import android.view.*;
import java.io.*;

public class ToMakeImage extends Activity
{

	String fileName;
	Bitmap bitmap;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tomakeimage);

		Intent in = this.getIntent();
		Bundle bundle=in.getExtras();
        fileName = bundle.getString("filename");
		bitmap = BitmapFactory.decodeFile(fileName);

		ImageView img = (ImageView)findViewById(R.id.showimage);
		Button make = (Button)findViewById(R.id.make);
		Button cut = (Button)findViewById(R.id.cut);
		Button zoom = (Button)findViewById(R.id.zoom);
		Button compress = (Button)findViewById(R.id.compress);

		img.setImageBitmap(bitmap);
		make.setOnClickListener(new View.OnClickListener(){


				@Override
				public void onClick(View p1)
				{
					final String[] arrayFruit = new String[] { "红色", "蓝色", "白色"};
					final int[] selectedFruitIndex = new int[1];
					Dialog alertDialog = new AlertDialog.Builder(ToMakeImage.this).
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
								Toast.makeText(ToMakeImage.this, arrayFruit[selectedFruitIndex[0]], Toast.LENGTH_SHORT).show();
								Intent in = new Intent(ToMakeImage.this,MainActivity.class);
								in.putExtra("COLOR",arrayFruit[selectedFruitIndex[0]]);
								in.putExtra("filename",fileName);
								startActivity(in);
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
				}
			});

		cut.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					Intent i = new Intent(ToMakeImage.this,CutImage.class);
					i.putExtra("filename",fileName);
					startActivity(i);
				}
			});

		zoom.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					LayoutInflater layoutInflater = LayoutInflater.from(ToMakeImage.this);
					View myLoginView = layoutInflater.inflate(R.layout.alertdialog, null);

					final EditText width = (EditText)myLoginView.findViewById(R.id.width);
					final EditText height = (EditText)myLoginView.findViewById(R.id.height);
					Dialog alertDialog = new AlertDialog.Builder(ToMakeImage.this).
						setTitle("请选择处理后的像素:").
						setIcon(R.drawable.ic_launcher).
						setView(myLoginView).
						setPositiveButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).
						setNegativeButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String sw = width.getText().toString();
								String sh = height.getText().toString();
								int w = 0,h = 0;
								if(!sw.equals("")&&!sh.equals("")){
								    w = Integer.parseInt(sw);
								    h = Integer.parseInt(sh);
								}
								if(w == 0 || h == 0 || w>1024 || h>1024)
									Toast.makeText(ToMakeImage.this,"请输入正确尺寸(长宽为:0-1024)",Toast.LENGTH_SHORT).show();
								else{

									float scaleWidth = ((float) w) / bitmap.getWidth();
									float scaleHeight = ((float) h) / bitmap.getHeight();
									// 取得想要缩放的matrix参数
									Matrix matrix = new Matrix();
									matrix.postScale(scaleWidth, scaleHeight);
									// 得到新的图片
									bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
									try
									{
										int num = Save.getNum("缩放",ToMakeImage.this);
										Save.save("缩放"+num+".jpg", bitmap, 100);
										Toast.makeText(ToMakeImage.this,"保存至:简单证件照/缩放"+num+".jpg",Toast.LENGTH_SHORT).show();
										Save.SaveNum("缩放",++num,ToMakeImage.this);
									}
									catch (IOException e)
									{}
								}
							}
						}).
						create();
					alertDialog.show();
				}
			});

		compress.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					LayoutInflater layoutInflater = LayoutInflater.from(ToMakeImage.this);
					View myLoginView = layoutInflater.inflate(R.layout.compress, null);

					final EditText bai = (EditText)myLoginView.findViewById(R.id.baifenbi);
					final EditText gu = (EditText)myLoginView.findViewById(R.id.gudingzhi);
					Dialog alertDialog = new AlertDialog.Builder(ToMakeImage.this).
						setTitle("压缩至:").
						setIcon(R.drawable.ic_launcher).
						setView(myLoginView).
						setPositiveButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).
						setNeutralButton("百分比压缩", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String sb = bai.getText().toString();
								int b = 0;
								if(!sb.equals("")){
								    b = Integer.parseInt(sb);
								}
								
								if(b <= 0 || b>100)
									Toast.makeText(ToMakeImage.this,"请输入正确数值",Toast.LENGTH_SHORT).show();
								else{
									try
									{
										BitmapFactory.Options options = new BitmapFactory.Options();
										options.inJustDecodeBounds = true;
										BitmapFactory.decodeFile(fileName, options);
										String type = options.outMimeType;
										Toast.makeText(ToMakeImage.this,type,Toast.LENGTH_LONG).show();

										int num = Save.getNum("压缩",ToMakeImage.this);
										File file = new File("/storage/emulated/0/简单证件照/"+"压缩"+num+".jpg");
										FileOutputStream fw = new FileOutputStream(file);
										BufferedOutputStream bw = new BufferedOutputStream(fw);
										if(type.equals("image/png")){
											bitmap.compress(Bitmap.CompressFormat.PNG, b, bw);
										}
										else if(type.equals("image/jpeg"))
											bitmap.compress(Bitmap.CompressFormat.JPEG, b, bw);//30 是压缩率，表示压缩70%; 如果不压缩
										bw.flush();
										bw.close();
										Toast.makeText(ToMakeImage.this,"保存至:简单证件照/压缩"+num+".jpg",Toast.LENGTH_SHORT).show();
										Save.SaveNum("压缩",++num,ToMakeImage.this);
									}
									catch (IOException e)
									{}
								}
							}
						}).setNegativeButton("固定值压缩", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String sg = gu.getText().toString();
								int g = 0;
								if(!sg.equals("")){
								    g = Integer.parseInt(sg);
								}
								if(g <= 0 || g>1024*5)
									Toast.makeText(ToMakeImage.this,"请输入正确数值",Toast.LENGTH_SHORT).show();
								else{

									int ya= (int)(100*((double)g/(getBitmapSize(bitmap)/16/1024)));
									try
									{
										BitmapFactory.Options options = new BitmapFactory.Options();
										options.inJustDecodeBounds = true;
										BitmapFactory.decodeFile(fileName, options);
										String type = options.outMimeType;

										int num = Save.getNum("压缩",ToMakeImage.this);
										File file = new File("/storage/emulated/0/简单证件照/"+"压缩"+num+".jpg");
										FileOutputStream fw = new FileOutputStream(file);
										BufferedOutputStream bw = new BufferedOutputStream(fw);
										if(type.equals("image/png")){
											bitmap.compress(Bitmap.CompressFormat.PNG, ya, bw);
										}
										else if(type.equals("image/jpeg"))
											bitmap.compress(Bitmap.CompressFormat.JPEG, ya, bw);//30 是压缩率，表示压缩70%; 如果不压缩
										bw.flush();
										bw.close();
										Toast.makeText(ToMakeImage.this,"保存至:简单证件照/压缩"+num+".jpg",Toast.LENGTH_SHORT).show();
										Save.SaveNum("压缩",++num,ToMakeImage.this);
									}
									catch (IOException e)
									{}
								}
							}
						}).
						create();
					alertDialog.show();
				}
			});
	}

	public int getBitmapSize(Bitmap bitmap){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
			return bitmap.getAllocationByteCount();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
			return bitmap.getByteCount();
		}
		return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
	}
}
