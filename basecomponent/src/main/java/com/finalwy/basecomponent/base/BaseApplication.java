package com.finalwy.basecomponent.base;

import android.app.Application;
import android.content.Context;

/**
 * baseapplication
 *
 * @author wy
 * @Date 2020-02-18
 */
public class BaseApplication extends Application {
    private static Context yContext;

    @Override
    public void onCreate() {
        super.onCreate();
        yContext = getApplicationContext();


    }

    public static Context getContext() {
        return yContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
