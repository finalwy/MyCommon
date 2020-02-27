package com.finalwy.basecomponent.view.commonwebview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.finalwy.basecomponent.base.RxBus;
import com.finalwy.basecomponent.base.RxEvent;
import com.finalwy.basecomponent.constant.RxEventConstant;
import com.finalwy.basecomponent.utils.CommonUtils;
import com.finalwy.basecomponent.utils.LogUtil;
import com.finalwy.basecomponent.utils.PreferenceUtils;
import com.finalwy.basecomponent.utils.ToastUtil;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


/**
 * @author wy
 * @Date 2020-02-18
 */
public class JSBridge {
    private static final String TAG = "JSEngine";
    private static final String SHOT_FILE = "/img-webshot.jpg";
    protected WebView mWebView;
    protected Context mContext;
    private AppCompatActivity mActivity;
    private String function = "";

    public JSBridge(Context context, WebView webView) {
        this.mContext = context;
        this.mWebView = webView;
        this.mActivity = (AppCompatActivity) CommonUtils.scanForActivity(context);

    }

    public static Bitmap capture(WebView webView) {
        int height = (int) (webView.getContentHeight() * webView.getScale());
        int width = webView.getWidth();
        int pH = webView.getHeight();
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        int top = height;
        while (top > 0) {
            if (top < pH) {
                top = 0;
            } else {
                top -= pH;
            }
            canvas.save();
            canvas.clipRect(0, top, width, top + pH);
            webView.scrollTo(0, top);
            webView.draw(canvas);
            canvas.restore();
        }
        return bm;
    }


    @JavascriptInterface
    /**
     * {"title":"红色宣传员 谁不盼祖国繁荣富强 - 严凤英","targetUrl":"http://m.ecochina.net/xqyplby/xiqu/audio/detail/20190318/1002700000040631553757486702914501_1.html","picUrl":"http://m.ecochina.net/static/v2/images/audio-detail-icon.png","describe":"红色宣传员 谁不盼祖国繁荣富强 - 严凤英"}
     */
    public void showShareToolbar(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject jsObj = new JSONObject(jsonStr);
                String title = jsObj.getString("title");
                String targetUrl = jsObj.getString("targetUrl");
                String picUrl = jsObj.getString("picUrl");
                String describe = jsObj.getString("describe");
//                ShareInfoEntity mShareInfoEntity = new ShareInfoEntity();
//                mShareInfoEntity.setDocId("");
//                mShareInfoEntity.setPicUrl(picUrl);
//                mShareInfoEntity.setTitle(title);
//                mShareInfoEntity.setDescribe(describe);
//                mShareInfoEntity.setTargetUrl(targetUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 调用JS方法
     *
     * @param function
     * @param objects
     */

    public void postDataToJs(final String function, final String... objects) {
        if (!TextUtils.isEmpty(function)) {
            new Handler(Looper.getMainLooper()).post(() -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(function + "(");
                        if (objects != null && objects.length > 0) {
                            int length = objects.length;
                            int i = 0;
                            for (String object : objects) {
                                if (!TextUtils.isEmpty(object)) {
                                    stringBuilder.append("'");
                                    stringBuilder.append(object);
                                    stringBuilder.append("'");
                                    if (i < length - 1) {
                                        stringBuilder.append(",");
                                    }
                                }
                                i++;
                            }
                        }
                        stringBuilder.append(")");
                        String jsCodes = "javascript:" + stringBuilder.toString();
                        LogUtil.e("jxx", jsCodes);
                        mWebView.loadUrl(jsCodes);
                    }
            );
        } else {
            Log.d(TAG, "js function's name is empty");
        }
    }

    /**
     * 调用JS方法
     *
     * @param function
     * @param objects
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    public String postDataToJsHaveResult(final String function, final String... objects) {
        final String[] str = {""};
        if (!TextUtils.isEmpty(function)) {
            new Handler(Looper.getMainLooper()).post(() -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(function + "(");
                        if (objects != null && objects.length > 0) {
                            int length = objects.length;
                            int i = 0;
                            for (String object : objects) {
                                if (!TextUtils.isEmpty(object)) {
                                    stringBuilder.append("'");
                                    stringBuilder.append(object);
                                    stringBuilder.append("'");
                                    if (i < length - 1) {
                                        stringBuilder.append(",");
                                    }
                                }
                                i++;
                            }
                        }
                        stringBuilder.append(")");
                        String jsCodes = "javascript:" + stringBuilder.toString();
                        mWebView.evaluateJavascript(jsCodes, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                str[0] = s;
                                LogUtil.e("jxx", s);
                            }
                        });
                    }
            );
        } else {
            Log.d(TAG, "js function's name is empty");
            return "";
        }
        return str[0];
    }

    /**
     * 隐藏显示原生搜索框
     * type //1，隐藏；2，不隐藏
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void toggleSearchBox(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject jsObj = new JSONObject(jsonStr);
                int toggle = jsObj.getInt("toggle");
                //0 // 显示 1 //隐藏
                if (1 == toggle) {
                    RxBus.getDefault().post(new RxEvent(RxEventConstant.UPDATE_SEARCH, 1));
                } else {
                    RxBus.getDefault().post(new RxEvent(RxEventConstant.UPDATE_SEARCH, 0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 获取city
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void getLocation(String jsonStr) {
        if (jsonStr != null) {
            String city = PreferenceUtils.getInstance().get("city", "").toString();
            if (!TextUtils.isEmpty(city))
                postDataToJs("getLocationCallback", "{" + "\\\"location\\\"" + ":\\\"" + city + "\\\"}");
        }
    }


    /**
     * 反馈
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void feedBackSuccess(String jsonStr) {
        if (jsonStr != null) {
            new ToastUtil(mContext).showToast("反馈成功");
            mActivity.finish();
        }
    }

    /**
     * 点赞
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void clickLike(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void goBack(String jsonStr) {
        mActivity.runOnUiThread(() -> {
            mActivity.finish();
        });
    }

    /**
     * 获取用户token并传给js
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void getUserToken(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索词
     *
     * @param world
     */
    @JavascriptInterface
    public void searchWord(String world) {
        if (world != null) {
            try {
                JSONObject jsObj = new JSONObject(world);
                String word = jsObj.getString("word");
                if (!TextUtils.isEmpty(word)) {
                    List<String> searchHistoryList = PreferenceUtils.getInstance().getDataList("history", String.class);
                    if (!searchHistoryList.contains(word)) {
                        searchHistoryList.add(0, word);
                        //去重
                        LinkedHashSet<String> set = new LinkedHashSet<>(searchHistoryList.size());
                        set.addAll(searchHistoryList);
                        searchHistoryList.clear();
                        searchHistoryList.addAll(set);
                        //数组最多存储10个
                        if (searchHistoryList.size() > 10) {
                            searchHistoryList.remove(searchHistoryList.size() - 1);
                        }
                        PreferenceUtils.getInstance().putDataList("history", searchHistoryList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
