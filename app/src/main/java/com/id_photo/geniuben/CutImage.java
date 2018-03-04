package com.id_photo.geniuben;
import android.app.*;
import android.os.*;
import android.widget.*;
import com.theartofdev.edmodo.cropper.*;
import android.graphics.*;
import android.view.*;
import android.content.*;
import java.util.*;
import java.io.*;

public class CutImage
{
	private Bitmap bitmap;

	private CropImageView cutImage;

	private Context context = null;

	public CutImage(Context context,Bitmap bitmap,CropImageView cutImage) {
		// TODO: Implement this method
		this.context = context;

		this.bitmap = bitmap;

		this.cutImage = cutImage;
	}

		public void guding(){
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View myLoginView = layoutInflater.inflate(R.layout.alertdialog, null);

				final EditText width = (EditText) myLoginView.findViewById(R.id.width);
				final EditText height = (EditText) myLoginView.findViewById(R.id.height);
				Dialog alertDialog = new AlertDialog.Builder(context).
						setTitle("请输入裁剪框尺寸:").
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
								cutImage.clearAspectRatio();
								cutImage.clearImage();
								String sw = width.getText().toString();
								String sh = height.getText().toString();
								int w = 0, h = 0;
								if (!sw.equals("") && !sh.equals("")) {
									w = Integer.parseInt(sw);
									h = Integer.parseInt(sh);
								}

								if (w == 0 || h == 0 || w >= bitmap.getWidth() || h >= bitmap.getHeight()) {
									Toast.makeText(context, "请输入正确尺寸(长:0-" + bitmap.getWidth() + ",宽:0-" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();
									cutImage.setImageBitmap(bitmap);
								} else {
									cutImage.setImageBitmap(bitmap);
									cutImage.setMinCropResultSize(w, h);
									cutImage.setMaxCropResultSize(w, h);
									Rect rect = new Rect(0, 0, w, h);
									cutImage.setCropRect(rect);
								}
							}
						}).
						create();
				alertDialog.show();
	}

		public void bili(){
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View myLoginView = layoutInflater.inflate(R.layout.alertdialog, null);

				final EditText width = (EditText) myLoginView.findViewById(R.id.width);
				final EditText height = (EditText) myLoginView.findViewById(R.id.height);
				Dialog alertDialog = new AlertDialog.Builder(context).
						setTitle("请输入裁剪框比值:").
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
								cutImage.clearAspectRatio();
								cutImage.clearImage();
								String sw = width.getText().toString();
								String sh = height.getText().toString();
								int w = 0, h = 0;
								if (!sw.equals("") && !sh.equals("")) {
									w = Integer.parseInt(sw);
									h = Integer.parseInt(sh);
								}

								if (w <= 0 || h <= 0 || w >= bitmap.getWidth() || h >= bitmap.getHeight()) {
									Toast.makeText(context, "请输入正确尺寸(长:0-" + bitmap.getWidth() + ",宽:0-" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();
									cutImage.setImageBitmap(bitmap);
								} else {

									cutImage.clearImage();
									cutImage.setImageBitmap(bitmap);
									cutImage.setMinCropResultSize(1, 1);
									cutImage.setMaxCropResultSize(bitmap.getWidth(), bitmap.getHeight());
									cutImage.setFixedAspectRatio(true);//设置允许按比例截图，如果不设置就是默认的任意大小截图
									cutImage.setAspectRatio(w, h);
									Rect rect = new Rect(0, 0, 100 * w, 100 * h);
									//cutImage.setCropRect(rect);
									//cutImage.clearImage();
								}
							}
						}).
						create();
				alertDialog.show();
	}

		public void free() {
				cutImage.clearAspectRatio();
				cutImage.clearImage();
				cutImage.setImageBitmap(bitmap);
				//cutImage.setCropRect(rect);
				//cutImage.clearImage();
	}

		public Bitmap saveBitmap(){
					bitmap = cutImage.getCroppedImage();
					cutImage.setImageBitmap(bitmap);
					try
					{
						int num = Save.getNum("裁剪",context);
						Save.save("裁剪"+num+".jpg", bitmap, 100);
						Toast.makeText(MyContext.getContext(),"保存至:简单证件照/裁剪"+num+".jpg",Toast.LENGTH_SHORT).show();
						Save.SaveNum("裁剪",++num,context);
					}
					catch (IOException e)
					{}
					return bitmap;
				}

				public void show(){
					cutImage.setVisibility(View.VISIBLE);
				}

				public void close(){
					cutImage.setVisibility(View.GONE);
				}

}
