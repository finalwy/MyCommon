package com.finalwy.basecomponent.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;
import com.finalwy.basecomponent.BuildConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import androidx.core.content.FileProvider;

/**
 * @author wy
 * @Date 2020-02-18
 */
public class SaveImage extends AsyncTask<String, Void, String> {

    private Context mContext;
    private String imgurl = "";
    private File file;

    public SaveImage(Context mContext, String imgurl) {
        this.mContext = mContext;
        this.imgurl = imgurl;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        file = new File(galleryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {

            File imgfile = new File(file.getAbsolutePath(), new Date().getTime() + ".png");
            InputStream inputStream = null;
            URL url = new URL(imgurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            if (conn.getResponseCode() == 200) {
                inputStream = conn.getInputStream();
            }
            byte[] buffer = new byte[4096];
            int len = 0;
            FileOutputStream outStream = new FileOutputStream(imgfile.toString());
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inputStream.close();
            outStream.close();
            result = imgfile.getAbsolutePath();
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        //通知相册更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);
        Toast.makeText(mContext, "已保存", Toast.LENGTH_LONG).show();
        scanFileAsync(mContext, result);
        scanDirAsync(mContext, file.getAbsolutePath());
    }

    //扫描指定文件
    public void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }

    //扫描指定目录
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    public void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0通过FileProvider授权访问
            uri = FileProvider.getUriForFile(ctx, BuildConfig.PROVIDER_CONFIG, new File(dir));
        } else {
            uri = Uri.fromFile(new File(dir));
        }
        scanIntent.setData(uri);
        ctx.sendBroadcast(scanIntent);
    }
}

