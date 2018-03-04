package com.id_photo.geniuben;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;

/**
 * Created by GeniuBen on 2018/2/16.
 */

public class Compress {

    Context context = null;
    Bitmap bitmap = null;
    Compressor compressedImageBitmap = null;
    String filename = null;
    static int num = 0;

    public Compress(Context context,String filename){
        this.context = context;
        this.bitmap = BitmapFactory.decodeFile(filename);
    }

    /*
    public void compress() throws IOException {
        num = Save.getNum("压缩",context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View myLoginView = layoutInflater.inflate(R.layout.compress, null);

        final EditText bai = (EditText)myLoginView.findViewById(R.id.baifenbi);
        final EditText gu = (EditText)myLoginView.findViewById(R.id.gudingzhi);
        Dialog alertDialog = new AlertDialog.Builder(context).
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
                            Toast.makeText(context,"请输入正确数值",Toast.LENGTH_SHORT).show();
                        else{
                            try
                            {

                                File compressedImage = new Compressor(context)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(75)
                                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(new File(filename));
                                Toast.makeText(MyContext.getContext(),"保存至:简单证件照/压缩"+num+".jpg",Toast.LENGTH_SHORT).show();
                                Save.SaveNum("压缩",++num,context);
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
                    Toast.makeText(context,"请输入正确数值",Toast.LENGTH_SHORT).show();
                else{
                    try {
                        Save.save("压缩"+num+".jpg",bitmap,100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    long n = 0;
                    try {
                        FileInputStream fis = new FileInputStream(new File("/storage/emulated/0/简单证件照/" + "压缩" + num + ".jpg"));
                        n = fis.available();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    double b = (double)g/((double)n/1024)*100.0;
                    Toast.makeText(MyContext.getContext(),""+b,Toast.LENGTH_LONG).show();
                    try {
                        File compressedImage = new Compressor(context)
                                .setQuality((int)b)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath("/storage/emulated/0/简单证件照")
                                .compressToFile(new File(filename));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MyContext.getContext(),"保存至:简单证件照/压缩"+num+".jpg",Toast.LENGTH_SHORT).show();
                    Save.SaveNum("压缩",++num,context);
                }
            }
        }).
                create();
        alertDialog.show();
    }
    */
}
