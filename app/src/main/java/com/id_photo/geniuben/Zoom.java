package com.id_photo.geniuben;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by GeniuBen on 2018/2/16.
 */

public class Zoom {

    public Zoom(final Context context,final Bitmap bitmap){
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View myLoginView = layoutInflater.inflate(R.layout.alertdialog, null);

    final EditText width = (EditText) myLoginView.findViewById(R.id.width);
    final EditText height = (EditText) myLoginView.findViewById(R.id.height);
    Dialog alertDialog = new AlertDialog.Builder(context).
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
                    int w = 0, h = 0;
                    if (!sw.equals("") && !sh.equals("")) {
                        w = Integer.parseInt(sw);
                        h = Integer.parseInt(sh);
                    }
                    if (w == 0 || h == 0 || w > 1024 || h > 1024)
                        Toast.makeText(context, "请输入正确尺寸(长宽为:0-1024)", Toast.LENGTH_SHORT).show();
                    else {
                        try {
                            int num = Save.getNum("缩放", context);
                            Save.save("缩放" + num + ".jpg", zoom(bitmap,w,h), 100);
                            Toast.makeText(context, "保存至:简单证件照/缩放" + num + ".jpg", Toast.LENGTH_SHORT).show();
                            Save.SaveNum("缩放", ++num, context);
                        } catch (IOException e) {
                        }
                    }
                }
            }).
            create();
					alertDialog.show();
}

public static Bitmap zoom(Bitmap bitmap,int w,int h){
    float scaleWidth = ((float) w) / bitmap.getWidth();
    float scaleHeight = ((float) h) / bitmap.getHeight();
    // 取得想要缩放的matrix参数
    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);
    // 得到新的图片
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
}
}
