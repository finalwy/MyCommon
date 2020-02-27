package com.finalwy.basecomponent.utils;

import android.util.Log;

import com.finalwy.basecomponent.BuildConfig;


/**
 * @author wy
 * @Date 2020-02-18
 */
public final class LogUtil {
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private LogUtil() {

    }

    public static void e(final String tag, final String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(final String tag, final String msg, final Exception e) {
        if (DEBUG) {
            Log.e(tag, msg, e);
        }
    }

    public static void v(final String tag, final String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void i(final String tag, final String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void d(final String tag, final String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void w(final String tag, final String msg, final Throwable tr) {
        if (DEBUG) {
            Log.w(tag, msg, tr);
        }
    }
}
