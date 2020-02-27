package com.finalwy.basecomponent.view.commonwebview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;

import androidx.fragment.app.FragmentActivity;

import com.finalwy.basecomponent.utils.CommonUtils;
import com.finalwy.basecomponent.utils.LogUtil;
import com.finalwy.basecomponent.utils.RegexUtils;
import com.finalwy.basecomponent.utils.SaveImage;
import com.finalwy.basecomponent.utils.ToastUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * @author wy
 * @Date 2020-02-18
 */
public class CommonWebView extends WebView {
    /**
     * 视频全屏参数
     */
    protected static FrameLayout.LayoutParams COVER_SCREEN_PARAMS = null;
    private String TAG = CommonWebView.class.getSimpleName();
    private WebViewProgressBar progressBar;//进度条的矩形（进度线）
    private Handler handler;
    private com.tencent.smtt.sdk.WebView mWebView;
    private JSBridge mJsBridge;
    private Context mContext;
    private Activity activity;
    private View customView;
    private FrameLayout fullscreenContainer;
    private IX5WebChromeClient.CustomViewCallback customViewCallback;
    private boolean flag = false;
    private boolean isShowComment = false;
    private onScrollChangeCallback callback;

    //设置回调借口，获取webview滑动的上下，左右距离差
    public interface onScrollChangeCallback {
        void onScroll(int dx, int dy);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (callback != null) {
            callback.onScroll(l - oldl, t - oldt);
        }
    }

    public onScrollChangeCallback getOnScrollChangeCallback() {
        return callback;
    }

    public void setScrollChangeCallback(onScrollChangeCallback callback) {
        this.callback = callback;
    }

    /**
     * 刷新界面（此处为加载完成后进度消失）
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        activity = CommonUtils.scanForActivity(mContext);
        //实例化进度条
        progressBar = new WebViewProgressBar(context);
        //设置进度条的size
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //刚开始时候进度条不可见
        progressBar.setVisibility(GONE);
        //把进度条添加到webView里面
//        addView(progressBar);
        //初始化handle
        handler = new Handler();
        mWebView = this;

    }

    public void setSetting(String agent) {
        initSettings(agent);
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    public boolean isShowComment() {
        return isShowComment;
    }

    /*
     * 是否滚动到评论区
     * @author wy
     * @Date 2020-01-02 9:03
     * @param showComment :
     * @return null
     */
    public void setShowComment(boolean showComment) {
        isShowComment = showComment;
    }

    private void initSettings(String agent) {
        WebSettings webSettings = mWebView.getSettings();

        mJsBridge = new JSBridge(mContext, mWebView);
        mWebView.addJavascriptInterface(mJsBridge, "webViewApp");
        String ua = mWebView.getSettings().getUserAgentString();//原来获取的UA
        webSettings.setUserAgentString(ua + agent);
        //自动播放
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        // 设置 禁止file 协议
        webSettings.setAllowFileAccess(false);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 支持保存数据
        webSettings.setSaveFormData(false);
        // 设置默认缩放方式尺寸是far
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(false);
        //设置 缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        // 设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //http https混合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(com.tencent.smtt.sdk.WebSettings.LOAD_NORMAL);
        }
        // 清除缓存
        mWebView.clearCache(true);
        // 清除历史记录
        mWebView.clearHistory();
//        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setWebViewClient(new MyWebClient());
        setWebChromeClient(new MyWebChromeClient());
        setOnLongClickListener(new MyOnLongClickListener());
    }

    public void downloadImage(final Context context, final String url) {
//        RxPermissions rxPermissions = new RxPermissions((FragmentActivity) activity);
//        rxPermissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
//                .subscribe(granted -> {
//                    if (granted) {
//                        AlertDialog alertDialog = new AlertDialog.Builder(context)
//                                .setTitle("下载")
//                                .setMessage("保存图片到手机？")
//                                .setNegativeButton("是", (dialog, which) ->
//                                        new SaveImage(context, url).execute()).setPositiveButton("否", null).show();
//                        alertDialog.show();
//                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
//                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
//                    }
//                });

    }

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        FrameLayout decor = (FrameLayout) CommonUtils.scanForActivity(mContext).getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(mContext);

        if (checkDeviceHasNavigationBar(mContext)) {
            if (!flag) {//竖屏 if (!flag) {//竖屏
                COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                COVER_SCREEN_PARAMS.setMargins(0, 0, 0, getNavigationBarHeight());
            } else {//横屏
                COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                COVER_SCREEN_PARAMS.setMargins(getNavigationBarHeight() / 2, 0, getNavigationBarHeight(), 0);
            }
        } else {
            COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    private int getNavigationBarHeight() {
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        CommonUtils.scanForActivity(mContext).getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 从url中提取电话号码
     *
     * @param url
     * @return
     */
    private String getPhoneNumber(String url) {
        try {
            //对url字符进行转义
            String transferUrl = URLDecoder.decode(url, "utf-8");
            //tel://(+8610)　87869999
            String mobile = transferUrl.substring(transferUrl.lastIndexOf("/") + 1);
            return RegexUtils.extractNumberFromStr(mobile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * destroy
     */
    public void dodestroy() {
        if (mWebView != null) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    public JSBridge getJsEngine() {
        return mJsBridge;
    }

    /**
     * 调用js方法，暂停视频播放
     */
    public void pauseVideo() {
        if (!TextUtils.isEmpty(mJsBridge.getFunction())) {
            LogUtil.e("Jxx", "调用js方法" + mJsBridge.getFunction());
            mJsBridge.postDataToJs(mJsBridge.getFunction(), "");
        }
    }

    public boolean isFull() {
        return flag;
    }

    public void backPress() {
        hideCustomView();
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) CommonUtils.scanForActivity(mContext).getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
//            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    /**
     * 长按下载图片
     */
    private class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
//            HitTestResult result = ((WebView) v).getHitTestResult();
            com.tencent.smtt.sdk.WebView.HitTestResult result = mWebView.getHitTestResult();
            if (result != null) {
                int type = result.getType();
                if (type == HitTestResult.IMAGE_TYPE
                        || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    downloadImage(mContext, result.getExtra());
                }
            }
            return false;
        }
    }

    /**
     * 自定义WebChromeClient
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onHideCustomView() {
            LogUtil.i(TAG, "onHideCustomView");
            flag = false;
            hideCustomView();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//不播放时竖屏

        }

        /**
         * 进度改变的回掉
         *
         * @param view        WebView
         * @param newProgress 新进度
         */
        @Override
        public void onProgressChanged(com.tencent.smtt.sdk.WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(100);
                handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }
            //设置初始进度10，这样会显得效果真一点，总不能从1开始吧
            if (newProgress < 10) {
                newProgress = 10;
            }
            //不断更新进度
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            flag = true;
            showCustomView(view, customViewCallback);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//播放时横屏幕，
        }

        /*** 视频播放相关的方法 **/

        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            return frameLayout;
        }


    }

    private class MyWebClient extends WebViewClient {
        /**
         * 页面加载过程中，加载资源回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onLoadResource(com.tencent.smtt.sdk.WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /**
         * 加载过程中 拦截加载的地址url
         *
         * @param view
         * @param url  被拦截的url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
            if (url == null) return false;
            if (url.contains("tel:")) {
//                if (BasePermission.build().hasPermission(mContext, Manifest.permission.CALL_PHONE)) {
//                    String mobile = getPhoneNumber(url);
//                    if (!TextUtils.isEmpty(mobile)) {//判断是否为空
//                        Uri uri = Uri.parse("tel:" + mobile);
//                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
//                        activity.startActivity(intent);
//                    }
//                } else {
//                    new ToastUtil(mContext).showToast("需要权限");
//                }
                return true;
            } else if (url.contains("mailto:")) {
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse(url));
                data.putExtra(Intent.EXTRA_SUBJECT, "业务联系");
//				        data.putExtra(Intent.EXTRA_TEXT, "这是内容");
                activity.startActivity(data);
            }
            try {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    return true;
                }
            } catch (Exception e) {//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }
            // TODO Auto-generated method stub
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            if (Build.VERSION.SDK_INT < 26) {
                view.loadUrl(url);
                return true;
            }
            return false;
        }

        /**
         * 页面开始加载调用的方法
         *
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(com.tencent.smtt.sdk.WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /**
         * 页面加载完成回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
            super.onPageFinished(view, url);
            // 关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onReceivedError(com.tencent.smtt.sdk.WebView webView, int errorCode, String description, String failingUrl) {
            super.onReceivedError(webView, errorCode, description, failingUrl);
//            StatusUtils.create(webView).fail(view -> {
//                StatusUtils.create(webView).showLoading();
//                new Handler().postDelayed(() -> {
//                    StatusUtils.create(webView).hint();
//                    webView.reload();
//                }, 1000);
//            });
        }


        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
//            ProgressWebView.this.requestFocus();
//            ProgressWebView.this.requestFocusFromTouch();
        }
    }
}

