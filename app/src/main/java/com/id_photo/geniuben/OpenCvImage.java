package com.id_photo.geniuben;
import android.graphics.Bitmap;
import org.opencv.core.*;
import org.opencv.android.*;
import org.opencv.imgproc.*;
import java.io.*;
import android.widget.*;
import android.content.*;

public class OpenCvImage
{
	public int ssx,ssy,sex,sey,sx,sy,wi,he;
	public boolean checkface = false;
	Context context;
	
	boolean drawRect = false;
	
	private static String strLibraryName = "opencv_java3"; // 不需要添加前缀 libopencv_java3
	static {
        try {
            //Log.e("loadLibrary", strLibraryName);
            System.loadLibrary(strLibraryName);
            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // couldn't find "libopencv_java320.so"
        } catch (UnsatisfiedLinkError e) {
            //Log.e("loadLibrary", "Native code library failed to load.\n" + e);
        } catch (Exception e) {
            //Log.e("loadLibrary", "Exception: " + e);
        }
    }
	
	public OpenCvImage(Context context){
		this.context = context;
	}
	
	public boolean detectFace(Bitmap srcBitmap) {

		ObjectDetector mFaceDetector;
        mFaceDetector = new ObjectDetector(context, R.raw.haarcascade_frontalface_alt, 6, 0.2F, 0.2F, new Scalar(255, 0, 0, 255));

        try {
            // bitmapToMat
            Mat toMat = new Mat();
            Utils.bitmapToMat(srcBitmap, toMat);
            Mat copyMat = new Mat();
            toMat.copyTo(copyMat); // 复制

            // togray
            Mat gray = new Mat();
            Imgproc.cvtColor(toMat, gray, Imgproc.COLOR_RGBA2GRAY);

            MatOfRect mRect = new MatOfRect();
            Rect[] object = mFaceDetector.detectObjectImage(gray, mRect);

            //Log.e("objectLength", object.length + "");


            int maxRectArea = 0 * 0;
            Rect maxRect = null;

            int facenum = 0;
            // Draw a bounding box around each face.
            for (Rect rect : object) {
				sx = rect.x;
				sy = rect.y;
				wi = rect.width;
				he = rect.height;
                Imgproc.rectangle(
					toMat,
					new Point(rect.x, rect.y),
					new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(255, 0, 0), 3);
                ++facenum;
                // 找出最大的面积
                int tmp = rect.width * rect.height;
                if (tmp >= maxRectArea) {
                    maxRectArea = tmp;
                    maxRect = rect;
                }
            }

            //Bitmap bp = null;
			/* if (facenum != 0) {
			 // 剪切最大的头像
			 //Log.e("剪切的长宽", String.format("高:%s,宽:%s", maxRect.width, maxRect.height));
			 org.opencv.core.Rect rect = new org.opencv.core.Rect(maxRect.x, maxRect.y, maxRect.width, maxRect.height);
			 Mat rectMat = new Mat(copyMat, rect);  // 从原始图像拿
			 srcBitmap = Bitmap.createBitmap(rectMat.cols(), rectMat.rows(), Bitmap.Config.ARGB_8888);
			 Utils.matToBitmap(rectMat, srcBitmap);
			 }*/
			if(facenum == 0)
				return false;
			else{
				checkface = true;
				return true;
				}

        } catch (Exception e) {
            e.printStackTrace();
        }

		return false;
	}
	
	public Bitmap MyGrabcut(Bitmap srcBitmap,String colors){

		Scalar color = chooseColor(colors);

		//checkface = detectFace(srcBitmap);
		Mat img = new Mat();
//缂╁皬鍥剧墖灏哄
		//Bitmap bm = Bitmap.createScaledBitmap(srcBitmap,srcBitmap.getWidth()/10,srcBitmap.getHeight()/10,true);
//bitmap->mat
//杞垚CV_8UC3鏍煎紡
		Point tl = null,br = null;
		if(checkface){
			double fc = 1.0 - (double)190/(double)338;
			int fw = (int)(fc * wi);
			ssx = (sx-fw>5)?(sx-fw):5;
			ssy = (sy-he/4*5>5)?(sy-he/4*5):5;
			sex = (sx+wi+fw<srcBitmap.getWidth()-5)?(sx+wi+fw):srcBitmap.getWidth()-5;
			sey = (sy+he*2<srcBitmap.getHeight()-5)?(sy+he*2):srcBitmap.getHeight()-5;
//璁剧疆鎶犲浘鑼冨洿鐨勫乏涓婅鍜屽彸涓嬭
			srcBitmap = Bitmap.createBitmap(srcBitmap,ssx,ssy,sex-ssx,sey-ssy,null,false);
			tl=new Point(5,5);
			br=new Point(srcBitmap.getWidth()-5,srcBitmap.getHeight()-5);
		}
		else if(drawRect){
			if(sx==-1&&sy==-1&&wi==-1&&he==-1){
			tl=new Point(sx,sy);
			br=new Point(sx+wi,sy+he);
			}
			else{
				tl=new Point(5,5);
				br=new Point(srcBitmap.getWidth()-5,srcBitmap.getHeight()-5);
			}
			drawRect = false;
		}
		else{
			tl=new Point(5,5);
			br=new Point(srcBitmap.getWidth()-5,srcBitmap.getHeight()-5);
		}
		Rect rect = new Rect(tl, br);

		Utils.bitmapToMat(srcBitmap, img);
		Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
		
//鐢熸垚閬澘
		Mat firstMask = new Mat();
		Mat bgModel = new Mat();
		Mat fgModel = new Mat();
		Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
		Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,1, Imgproc.GC_INIT_WITH_RECT);
		Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

//鎶犲浘
		Mat foreground = new Mat(img.size(), CvType.CV_8UC3, color);
		img.copyTo(foreground, firstMask);

//mat->bitmap
		Bitmap b = Bitmap.createBitmap(srcBitmap.getWidth(),srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(foreground,b);
		//Bitmap c = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/Screenshots/IMG_20171029_183009.png");
		
		return b;
	}

	private Scalar chooseColor(String colors)
	{
		// TODO: Implement this method
		Scalar scalar = null;
		switch(colors){
			case "红色":scalar = new Scalar(255,0,0);
				break;
			case "白色":scalar = new Scalar(255,255,255);
			    break;
			case "蓝色":scalar = new Scalar(67,142,219);
		}
		return scalar;
	}

	
}
