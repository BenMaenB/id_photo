package com.id_photo.geniuben;

import android.content.*;
import android.app.*;
import android.os.*;
import android.net.*;
import android.provider.*;
import android.database.*;
import java.io.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.support.v7.app.*;
import java.util.*;
import android.webkit.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.support.v4.app.*;
import android.*;
import android.support.annotation.*;
import android.annotation.*;
import android.graphics.*;
import com.theartofdev.edmodo.cropper.*;
import android.app.AlertDialog;
import me.nereo.multi_image_selector.*;


public class First extends AppCompatActivity {

    String filename = "null";
    private static final int CHOOSE_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    final static File systemFile = new File("/storage/emulated/0/我的证件照");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        View contentView=LayoutInflater.from(this).inflate(R.layout.main,null);
        setContentView(contentView);

        if(canMakeSmores()){
            String[] perms = {"android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.CAMERA"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode);
        }

        if(!systemFile.exists())
            systemFile.mkdirs();
        File fbitmnap = new File("/storage/emulated/0/我的证件照/camera.jpg");
        if(!fbitmnap.exists())
            try {
                fbitmnap.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        Button btn = (Button) findViewById(R.id.choosePhoto);
        btn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                // TODO: Implement this method
                String permission = "android.permission.READ_EXTERNAL_STORAGE";
                if (checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    onSelectImage();
                }
                else
                    Toast.makeText(First.this,"未授权",Toast.LENGTH_LONG).show();
            }
        });

        ImageButton takePhoto = (ImageButton)findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                // TODO: Implement this method
                if (checkCallingOrSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED
                        && checkCallingOrSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                    Intent intentPhote = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentPhote.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    File out = new File("/storage/emulated/0/我的证件照/camera.jpg");
                    String fileName = "camera.jpg";
                    final String CACHE_IMG = Environment.getExternalStorageDirectory()+"/我的证件照/";
                    File file = new File(CACHE_IMG, fileName);
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(MyContext.getContext(),"id_photo.geniuben", file);

                    } else
                        uri = Uri.fromFile(out);
                    // 获取拍照后未压缩的原图片，并保存在uri路径中
                    intentPhote.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    intentPhote.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intentPhote.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    startActivityForResult(intentPhote,TAKE_PHOTO);
                }
                else
                    Toast.makeText(MyContext.getContext(),"未授权",Toast.LENGTH_LONG).show();
            }


        });

    }
    /** Start pick image activity with chooser. */

    public void onSelectImage() {
        MultiImageSelector.create(First.this)
                .showCamera(false) // show camera or not. true by default
                .single() // single mode
                .start(First.this, CHOOSE_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //选取图片
            if(requestCode == CHOOSE_PHOTO){
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                if(path!=null)
                filename = path.get(0);
                Intent i = new Intent(First.this,Main.class);
                i.putExtra("filename",filename);
                startActivity(i);
            }
            //拍照
            else if (requestCode == TAKE_PHOTO) {
                Intent i = new Intent(First.this, Main.class);
                i.putExtra("filename","/storage/emulated/0/我的证件照/camera.jpg");
                startActivity(i);
            }
        }
    }

    //权限请求
    private boolean canMakeSmores(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    @Override

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean cameraAccepted = (grantResults[0]==PackageManager.PERMISSION_GRANTED);
                if(cameraAccepted){
                    //授权成功之后，调用系统相机进行拍照操作等
                }else{
                    //用户授权拒绝之后，友情提示一下就可以了
                    Toast.makeText(First.this,"拒绝后将应用无法获得选取的图片，重启应用可重新授权",Toast.LENGTH_LONG).show();
                }

                break;

        }

    }
}

