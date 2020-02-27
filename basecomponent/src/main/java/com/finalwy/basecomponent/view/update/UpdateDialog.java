package com.finalwy.basecomponent.view.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.finalwy.basecomponent.R;
import com.finalwy.basecomponent.utils.AppUtils;
import com.finalwy.basecomponent.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 版本更新
 *
 * @author wy
 * @Date 2020-02-18
 */
public class UpdateDialog extends Dialog implements View.OnClickListener {
    private static final String SCHEME = "package";
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private final String SDCard_Dir = Environment.getExternalStorageDirectory().getAbsolutePath();
    //apk文件保存路径
    private final String DownloadDir = SDCard_Dir + File.separator + "download";
    private String TAG = UpdateDialog.class.getSimpleName();
    private Activity mContext;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mManager;
    //更新说明
    private String updateDescription;
    /**
     * 是否强迫安装
     */
    private boolean isForced;
    private String mDownloadUrl;
    //apk文件
    private File mFileInstall;
    private List<Boolean> mPermissionList = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    private Handler completeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            LogUtil.i(TAG, "msg.what:" + msg.what);
            if (msg.what < 100) {
                mBuilder.setProgress(100, msg.what, false);
                mBuilder.setContentTitle(mContext.getResources().getString(R.string.base_update_downloading) + msg.what + "%");
                mManager.notify(1, mBuilder.build());
            } else {
                mBuilder.setProgress(100, msg.what, false);
                mBuilder.setContentTitle(mContext.getResources().getString(R.string.base_update_downloading) + msg.what + "%");
                mManager.cancel(1);
                installApk();
            }
        }
    };

    public UpdateDialog(Activity activity, String updateContent, String url, boolean isForced) {
        super(activity);
        setContentView(R.layout.base_dialog_update);
        setCanceledOnTouchOutside(false);
        mContext = activity;
        this.updateDescription = updateContent;
        this.mDownloadUrl = url;
        this.isForced = isForced;
        initView();
        initData();

    }

    private void initView() {
        TextView tv_dialog_content = findViewById(R.id.base_tv_dialog_content);
        TextView tv_dialog_cancel = findViewById(R.id.base_tv_dialog_cancel);
        TextView tv_dialog_confirm = findViewById(R.id.base_tv_dialog_confirm);
        tv_dialog_cancel.setOnClickListener(this);
        tv_dialog_confirm.setOnClickListener(this);
        tv_dialog_content.setText(updateDescription);
    }

    private void initData() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.base_tv_dialog_cancel) {
            //如果是强制更新，不能隐藏弹窗
            if (!isForced) {
                dismiss();
            }
        } else if (i == R.id.base_tv_dialog_confirm) {//请求权限
            requestPermission();

        }
    }

    private void requestPermission() {
//        RxPermissions permissions = new RxPermissions((FragmentActivity) mContext);
//        boolean isGranted = permissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (!isGranted) {
//            permissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .subscribe(new Consumer<Permission>() {
//                        @Override
//                        public void accept(Permission permission) throws Exception {
//                            mPermissionList.add(permission.granted);
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//
//                        }
//                    }, new Action() {
//                        @Override
//                        public void run() throws Exception {
//                            dismiss();
//                            //默认所有的权限已经申请成功
//                            boolean flag = false;
//                            for (Boolean b : mPermissionList) {
//                                if (!b) {//当前有禁止的权限
//                                    flag = true;
//                                    break;
//                                }
//                            }
//                            if (flag) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                                builder.setMessage(mContext.getResources().getString(R.string.base_permission_tip));
//                                builder.setNegativeButton("暂不", new OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setPositiveButton("去设置", new OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent();
//                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        Uri uri = Uri.fromParts(SCHEME, mContext.getPackageName(), null);
//                                        intent.setData(uri);
//                                        mContext.startActivity(intent);
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.show();
//                            } else {
//                                downLoadNewApk();
//                            }
//                        }
//                    });
//        } else {
//            downLoadNewApk();
//            dismiss();
//        }
    }


    /**
     * download apk
     */
    private void downLoadNewApk() {
        LogUtil.i(TAG, "downLoadNewApk()");
        boolean notificationEnabled = isNotificationEnabled(mContext);
        LogUtil.e(TAG, "notificationEnabled " + notificationEnabled);
        if (notificationEnabled) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.base_dialog_upgrade_check_down_process), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.base_dialog_upgrade_check_down_process_no_permission), Toast.LENGTH_SHORT).show();
        }
        mManager = (NotificationManager) mContext.getSystemService((mContext.NOTIFICATION_SERVICE));
        mBuilder = new NotificationCompat.Builder(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.enableLights(false);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{0});
            mChannel.setSound(null, null);
            mManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID);
        }
        int icon = mContext.getResources().getIdentifier("icon", "mipmap", mContext.getPackageName());
        mBuilder.setSmallIcon(icon);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setVibrate(new long[]{0});
        mManager.notify(1, mBuilder.build());

        String apkFile = DownloadDir + File.separator + AppUtils.getAppName(mContext) + System.currentTimeMillis() + ".apk";
        mFileInstall = new File(apkFile);
        downLoadSchedule(completeHandler, mFileInstall);
//        downloadApk(completeHandler, mFileInstall);
    }

    private boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * schedule download
     *
     * @param handler
     * @param file
     */

    public void downLoadSchedule(final Handler handler, final File file) {
        final int perLength = 4096;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(mDownloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (conn.getResponseCode() == 302 || conn.getResponseCode() == 301) {
                        String location = conn.getHeaderField("Location");
                        url = new URL(location);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(3000);
                    }
                    if (conn.getResponseCode() == 200)    //请求成功
                    {
                        InputStream in = conn.getInputStream();
                        int fileLength = conn.getContentLength();
                        LogUtil.e(TAG, "apk_size:" + fileLength);
                        byte[] buffer = new byte[perLength];
                        int len = -1;
                        FileOutputStream out = new FileOutputStream(file);
                        int temp = 0;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            int schedule = (int) ((file.length() * 100) / fileLength);
                            if (temp != schedule
                                    && (schedule % 10 == 0 || schedule % 4 == 0 || schedule % 7 == 0)) {
                                temp = schedule;
                                handler.sendEmptyMessage(schedule);
                            }
                        }
                        out.flush();
                        out.close();
                        in.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 下载工作-下载最新的apk
     */
    private void downloadApk(final Handler handler, final File file) {
        new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                FileOutputStream out = null;
                try {
                    URL url = new URL(mDownloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int fileLength = conn.getContentLength();
                    LogUtil.e(TAG, "apk_size:" + fileLength);
                    is = conn.getInputStream();
                    File fileDir = new File(DownloadDir);
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    out = new FileOutputStream(file);
                    byte buf[] = new byte[4096];
                    int len = 0;
                    int temp = 0;
                    while (-1 != (len = is.read(buf))) {
                        out.write(buf, 0, len);

                        int schedule = (int) ((file.length() * 100) / fileLength);
                        if (temp != schedule
                                && (schedule % 10 == 0 || schedule % 4 == 0 || schedule % 7 == 0)) {
                            temp = schedule;
                            handler.sendEmptyMessage(schedule);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (null != is) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != out) {
                        try {
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * install apk
     */
    private void installApk() {
        Uri apkUri = null;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            apkUri = FileProvider.getUriForFile(mContext, getAuthoritiesName(), mFileInstall);//在AndroidManifest中的android:authorities值
        } else {
            apkUri = Uri.fromFile(mFileInstall);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);

    }

    /**
     * 获取authorities值
     */
    private String getAuthoritiesName() {
        PackageInfo packageInfo = null;
        String authoritiesName = "";
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getComponentName().getPackageName(), PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = packageInfo.providers;
            for (ProviderInfo providerInfo : providers) {
                String name = providerInfo.name;
                //"android.support.v4.content.FileProvider"该name值是在清单文件配置的，获取这个名字的provider的authority值
                if ("androidx.core.content.FileProvider".equals(name)) {
                    authoritiesName = providerInfo.authority;
                    break;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return authoritiesName;
    }
}
