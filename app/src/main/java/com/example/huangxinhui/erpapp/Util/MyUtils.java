package com.example.huangxinhui.erpapp.Util;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;


public class MyUtils {

    public static final String APP_NAME = "erpapp.apk";//名字

    public static final String PACKAGE_NAME = "com.example.huangxinhui.erpapp";

    /**
     * 获得存储文件
     *
     * @param
     * @param
     * @return
     */
    public static File getCacheFile(String name, Context context) {
        return new File(Environment.getExternalStorageDirectory() + "/" + MyUtils.PACKAGE_NAME + "/" + MyUtils.APP_NAME);
    }

    /**
     * 获取手机大小，px
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getPhoneMetrics(Context context) {// 获取手机分辨率
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
