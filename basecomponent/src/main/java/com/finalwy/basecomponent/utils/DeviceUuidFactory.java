package com.finalwy.basecomponent.utils;

import android.content.Context;

/**
 * 获取设备唯一标识UUID
 *
 * @author wy
 * @Date 2020-02-18
 */
public class DeviceUuidFactory {

    /**
     * 获取设备唯一标识UUID
     *
     * @param mContext
     * @return
     */
    public static String getIdentity(Context mContext) {
        String identity;
        try {
            identity = (String) PreferenceUtils.getInstance().get("identity", "");
            if (identity.equals("")) {
                identity = java.util.UUID.randomUUID().toString();
                PreferenceUtils.getInstance().put("identity", identity);
            }
        } catch (Exception e) {
            identity = "";
        }
        return identity;
    }
}


