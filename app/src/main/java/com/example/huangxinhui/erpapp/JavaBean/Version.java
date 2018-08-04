package com.example.huangxinhui.erpapp.JavaBean;

public class Version {

    /**
     * formId : GPIS13
     * inputCode : Y
     * result : S
     * msg : 成功
     * endFlag : *
     * versionCode : 2
     * versionName : 1.0.1
     * content : 111
     * url : gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk
     */

    private int versionCode;
    private String versionName;
    private String url;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
