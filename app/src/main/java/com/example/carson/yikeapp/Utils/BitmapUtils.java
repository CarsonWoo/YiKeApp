package com.example.carson.yikeapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_PICK_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_TAKE_PHOTO;

/**
 * Created by Administrator on 2017/5/20.
 */

//BitmapUtils工具类
public class BitmapUtils {

    private static String TAG = "BitmapUtils";

    static File mTmpFile;


    /*** 该方法用于将图片进行圆形处理* */
    public static Bitmap circleBitmap(Bitmap source) {
        if (source == null){
            return null;
        }
        //默认只对宽进行处理
        int width = source.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();    //设置抗锯齿
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return bitmap;
    }

    /**
     * 该方法用于图片压缩处理,注意width、height参数的类型必须是float   *
     */

    public static Bitmap zoom(Bitmap source, float width, float height) {
        if (source == null){
            return null;
        }
        Matrix matrix = new Matrix();    //图片进行压缩处理
        matrix.postScale(width / source.getWidth(), height / source.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        return bitmap;
    }


    //保存图片
    public static String saveImage(Context context, Bitmap bitmap, String fileName) {
        Log.d("SetImage","running");

        File file;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            //路径1：storage/sdcard/Android/data/包名/files
            Log.i("SAVE_IMAGE","SAVING_SDCARD");
            file = context.getExternalFilesDir("");

        }else{//手机内部存储
            //路径：data/data/包名/files
            Log.i("SAVE_IMAGE","SAVING_PHONE");
            file = context.getFilesDir();

        }
        FileOutputStream fos = null;
        try {
            final File files = new File(file,fileName+".png");
            fos = new FileOutputStream(files);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
            Log.i("SAVE_IMAGE_SUCCESS",fileName+".png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("FILE",Uri.fromFile(file)+"/"+fileName+".png");
        return Uri.fromFile(file)+"/"+fileName+".png";
    }

    /**
     * 按照一定的宽高比例裁剪图片
     * @param bitmap 要裁剪的图片
     * @param num1 长边的比例
     * @param num2 短边的比例
     * @param isRecycled 是否回收原图片
     * @return 裁剪后的图片
     */
    public static Bitmap imageCrop(Bitmap bitmap, int num1, int num2, boolean isRecycled){
        if (bitmap == null){
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h){
            if (h > w * num2 / num1){
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else{
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else{
            if (w > h * num2 / num1){
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else{
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;}
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)&& !bitmap.isRecycled()){
            bitmap.recycle();//回收原图片
            bitmap = null;
        }
        return bmp;
    }


    /**查询路径*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        //高于4.4.2的版本
        if (sdkVersion >= 19) {
            Log.e("TAG", "uri auth: " + uri.getAuthority());
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
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
            } else if (isMedia(uri)) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = ((Activity)context).managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                return actualimagecursor.getString(actual_image_column_index);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**uri路径查询字段*/
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static boolean isMedia(Uri uri) {
        return "media".equals(uri.getAuthority());
    }
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Dialog中的item
     * */
    private static String[] items = new String[] { "选择本地图片", "拍照" };
    private static int hasWriteCalendarPermission;
    static final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    /**设置选择图片来源Dialog*/
    public static void showDialogToChoosePic(final Context context , @Nullable final Fragment fragment, @Nullable final String tempName) {
        new AlertDialog.Builder(context)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                hasWriteCalendarPermission = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if(hasWriteCalendarPermission != PackageManager.PERMISSION_GRANTED){
                                    if(!((Activity)context).shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                        showMessageOKCancel(context,"你需要先允许使用存储空间权限",
                                                new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ((Activity)context).requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                REQUEST_CODE_ASK_PERMISSIONS);
                                                    }
                                                });
                                        return;
                                    }
                                    ((Activity)context).requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                    return;
                                }else {
                                    Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    if (fragment != null) {
                                        fragment.startActivityForResult(picIntent, CODE_PICK_PHOTO);
                                    }else {
                                        ((Activity)context).startActivityForResult(picIntent, CODE_PICK_PHOTO);
                                    }
                                }
                                break;
                            case 1:
                                hasWriteCalendarPermission = ((Activity)context).checkCallingOrSelfPermission(Manifest.permission.CAMERA);
                                if(hasWriteCalendarPermission != PackageManager.PERMISSION_GRANTED){
                                    if(!((Activity)context).shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                        showMessageOKCancel(context,"你需要先允许使用相机权限",
                                                new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ((Activity)context).requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                                REQUEST_CODE_ASK_PERMISSIONS);
                                                    }
                                                });
                                        return;
                                    }
                                    ((Activity)context).requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                    return;
                                }else {
                                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takeIntent.resolveActivity(context.getPackageManager()) != null) {

                                                if (fragment != null) {
                                                    fragment.startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
                                                }else {
                                                    ((Activity)context).startActivityForResult(takeIntent,CODE_TAKE_PHOTO);
                                                }
                                    } else {
                                        Toast.makeText(context, "没有可用相机", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap getImageBitmapFromResult(Context context, int requestCode, int resultCode, Intent data, ImageView view, String saveName){

        if (requestCode == CODE_PICK_PHOTO && resultCode == RESULT_OK && data != null) {//系统相册
            Uri imageData = data.getData();
            String path = getPath(context,imageData);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Log.d(TAG,"view.getWidth"+view.getWidth());
            Log.d(TAG,"view.getHeight"+view.getHeight());
//            Bitmap bitmap1 = BitmapUtils.zoom(bitmap, view.getWidth(), view.getHeight());
//            Bitmap bitmap1 = getImageThumbnail(path,view.getWidth(),view.getHeight());
//            Bitmap bitmap2 = BitmapUtils.circleBitmap(bitmap1);
            //加载显示
//            view.setImageBitmap(bitmap2);
//            BitmapUtils.saveImage(context,bitmap,saveName);
            return bitmap;
        } else if (requestCode == CODE_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {//系统相机
            Log.d(TAG,"getPicFromCamera:running");
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            Bitmap bitmap1 = BitmapUtils.zoom(bitmap, view.getWidth(), view.getHeight());
//            Bitmap bitmap1 = getImageThumbnail(data.getData().getPath(),view.getWidth(),view.getHeight());
//            Bitmap bitmap2 = BitmapUtils.circleBitmap(bitmap1);

            //加载显示
//            view.setImageBitmap(bitmap);
//            BitmapUtils.saveImage(context,bitmap,saveName);
            return bitmap;
        }
        return null;
    }


    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**读取本地图片，不存在时返回FALSE*/
    public static boolean readImage(ImageView view, Context context, String name) {
        File filesDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){/**判断sd卡是否挂载*/
            /**路径1：storage/sdcard/Android/data/包名/files*/
            filesDir = context.getExternalFilesDir("");

        }else{/**手机内部存储   路径：data/data/包名/files*/
            filesDir = context.getFilesDir();

        }
        File file = new File(filesDir,name + ".png");
        if(file.exists()){
            /**存储--->内存*/
            Log.d(TAG,"file.path:"+file.getAbsolutePath());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            bitmap = zoom(bitmap,view.getWidth(),view.getHeight());
            Log.d(TAG,"view.getWidth:"+view.getWidth());
            Log.d(TAG,"view.getHeight:"+view.getHeight());
            bitmap = getImageThumbnail(file.getAbsolutePath(),view.getWidth(),view.getHeight());
            bitmap = circleBitmap(bitmap);
            view.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }
}