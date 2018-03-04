package com.id_photo.geniuben;
import android.net.*;
import android.provider.*;
import android.database.*;
import android.annotation.*;
import android.content.*;
import android.os.*;

public class ChoosePhoto
{

	Context main;

	public ChoosePhoto(Context main){
		this.main = main;
	}

	public String getRealPathFromURI(Uri contentUri) {  
        String res = null;  
        String[] proj = { MediaStore.Images.Media.DATA };  
        Cursor cursor = main.getContentResolver().query(contentUri, proj, null, null, null);  
        if(null!=cursor&&cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
            res = cursor.getString(column_index);  
            cursor.close();  
        }  
        return res;  
    }  

    /**  
     * 涓撲负Android4.4璁捐鐨勪粠Uri鑾峰彇鏂囦欢缁濆璺緞锛屼互鍓嶇殑鏂规硶宸蹭笉濂戒娇  
     */  
    @SuppressLint("NewApi")  
    public String getPath(final Context context, final Uri uri) {  

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;  

        // DocumentProvider  
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {  
            // ExternalStorageProvider  
            if (isExternalStorageDocument(uri)) {  
                final String docId = DocumentsContract.getDocumentId(uri);  
                final String[] split = docId.split(":");  
                final String type = split[0];  

                if ("primary".equalsIgnoreCase(type)) {  
                    return Environment.getExternalStorageDirectory() + "/" + split[1];  
                }  
            }  
            // DownloadsProvider  
            else if (isDownloadsDocument(uri)) {  

                final String id = DocumentsContract.getDocumentId(uri);  
                final Uri contentUri = ContentUris.withAppendedId(  
					Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  

                return getDataColumn(context, contentUri, null, null);  
            }  
            // MediaProvider  
            else if (isMediaDocument(uri)) {  
                final String docId = DocumentsContract.getDocumentId(uri);  
                final String[] split = docId.split(":");  
                final String type = split[0];  

                Uri contentUri = null;  
                if ("image".equals(type)) {  
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
                } else if ("video".equals(type)) {  
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
                } else if ("audio".equals(type)) {  
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
                }  

                final String selection = "_id=?";  
                final String[] selectionArgs = new String[]{split[1]};  

                return getDataColumn(context, contentUri, selection, selectionArgs);  
            }  
        }  
        // MediaStore (and general)  
        else if ("content".equalsIgnoreCase(uri.getScheme())) {  
            return getDataColumn(context, uri, null, null);  
        }  
        // File  
        else if ("file".equalsIgnoreCase(uri.getScheme())) {  
            return uri.getPath();  
        }  
        return null;  
    }  

    /** 
     * Get the value of the data column for this Uri. This is useful for 
     * MediaStore Uris, and other file-based ContentProviders. 
     * 
     * @param context       The context. 
     * @param uri           The Uri to query. 
     * @param selection     (Optional) Filter used in the query. 
     * @param selectionArgs (Optional) Selection arguments used in the query. 
     * @return The value of the _data column, which is typically a file path. 
     */  
    public String getDataColumn(Context context, Uri uri, String selection,  
                                String[] selectionArgs) {  

        Cursor cursor = null;  
        final String column = "_data";  
        final String[] projection = {column};  

        try {  
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
														null);  
            if (cursor != null && cursor.moveToFirst()) {  
                final int column_index = cursor.getColumnIndexOrThrow(column);  
                return cursor.getString(column_index);  
            }  
        } finally {  
            if (cursor != null)  
                cursor.close();  
        }  
        return null;  
    }  

    /** 
     * @param uri The Uri to check. 
     * @return Whether the Uri authority is ExternalStorageProvider. 
     */  
    public boolean isExternalStorageDocument(Uri uri) {  
        return "com.android.externalstorage.documents".equals(uri.getAuthority());  
    }  

    /** 
     * @param uri The Uri to check. 
     * @return Whether the Uri authority is DownloadsProvider. 
     */  
    public boolean isDownloadsDocument(Uri uri) {  
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
    }  

    /** 
     * @param uri The Uri to check. 
     * @return Whether the Uri authority is MediaProvider. 
     */  
    public boolean isMediaDocument(Uri uri) {  
        return "com.android.providers.media.documents".equals(uri.getAuthority());  
    }  
}
