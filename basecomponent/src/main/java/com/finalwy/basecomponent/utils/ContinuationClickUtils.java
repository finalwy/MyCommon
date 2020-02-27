package com.finalwy.basecomponent.utils;


/**
 * 防止连续点击
 *
 * @author wy
 * @Date 2020-02-18
 */
public class ContinuationClickUtils {
    private static String TAG = ContinuationClickUtils.class.getSimpleName();
    private static long lastClickTime;
    private static final int MIN_DELAY_TIME = 1000;  // 两次点击间隔不能少于1000ms
    public static final int MIN_DELAY_TIME_1 = 1000;  // 两次点击间隔不能少于1000ms
    public static final int MIN_DELAY_TIME_500 = 500;  // 两次点击间隔不能少于1000ms

    public static boolean isFastClick() {
        return isFastClick(MIN_DELAY_TIME);
    }

    public static boolean isFastClick(int delay) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        LogUtil.i(TAG, "currentClickTime:" + currentClickTime + " lastClickTime:" + lastClickTime);
        LogUtil.i(TAG, "currentClickTime - lastClickTime:" + (currentClickTime - lastClickTime));
        if ((currentClickTime - lastClickTime) >= delay) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
