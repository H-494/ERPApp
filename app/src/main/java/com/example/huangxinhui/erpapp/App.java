package com.example.huangxinhui.erpapp;

import android.app.Application;
import android.content.res.Configuration;

import com.example.huangxinhui.erpapp.JavaBean.GroupBean;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

public class App extends Application {
    private GroupBean group;// 登录时选择的班别

    public GroupBean getGroup() {
        return group;
    }

    public void setGroup(GroupBean group) {
        this.group = group;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenAdapterTools.init(this);
        ZXingLibrary.initDisplayOpinion(this);
    }

    //旋转适配,如果应用屏幕固定了某个方向不旋转的话(比如qq和微信),下面可不写.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ScreenAdapterTools.getInstance().reset(this);
    }
}
