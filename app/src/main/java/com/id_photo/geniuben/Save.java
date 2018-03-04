package com.id_photo.geniuben;
import java.io.*;
import android.graphics.*;
import android.widget.*;
import android.content.*;

public class Save
{
	static public void save(String name,Bitmap bitmap,int num) throws FileNotFoundException, IOException{
		File file = new File("/storage/emulated/0/我的证件照/"+name);
		FileOutputStream fw = new FileOutputStream(file);
		BufferedOutputStream bw = new BufferedOutputStream(fw);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bw);
		bw.flush();
		bw.close();
	}
	
	static public void SaveNum(String type,int num,Context context){
		SharedPreferences.Editor editor = context.getSharedPreferences("data",
															   context.MODE_PRIVATE).edit();
		editor.putInt(type, num);
		editor.apply();
	}
	
	static public int getNum(String type,Context context){
		SharedPreferences pref = context.getSharedPreferences("data", context.MODE_PRIVATE);
		return pref.getInt(type, 0);
	}
}
