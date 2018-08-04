package com.example.huangxinhui.erpapp.JavaBean;

public class LoginResult {

    /**
     * formId : GPIS00
     * result : F
     * msg : 班组()不得为空值!
     * endFlag : *
     */

    private String formId;
    private String result;
    private String msg;
    private String endFlag;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(String endFlag) {
        this.endFlag = endFlag;
    }
}
