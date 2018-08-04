package com.example.huangxinhui.erpapp.JavaBean;

public class Err {

    /**
     * formId : GPIS07
     * result : F
     * msg : 入库失败，炉号[18B606164]最终重需介於理论重上下300Kg范围内，请洽炼钢发货人员进行确认!
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
