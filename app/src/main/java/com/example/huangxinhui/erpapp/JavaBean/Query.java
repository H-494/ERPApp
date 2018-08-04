package com.example.huangxinhui.erpapp.JavaBean;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable {

    /**
     * formId : GPIS01
     * inputCode : Y
     * result : S
     * msg : 成功
     * endFlag : *
     * data : [{"name":"B20","list_info":[{"key":"炉号","value":"18B102434"},{"key":"生成日期","value":"20180226"},{"key":"班次","value":"2"},{"key":"班别","value":"C"},{"key":"连铸机号","value":"B1"},{"key":"状态","value":"21"},{"key":"用途","value":"G"},{"key":"去向","value":"B20"},{"key":"坯型","value":""},{"key":"长度","value":"12000"},{"key":"库位","value":"B20"},{"key":"跨","value":"R"},{"key":"储序","value":""},{"key":"钢种","value":""},{"key":"GKNO","value":"J002B308"},{"key":"订单编号","value":"N0018010693"},{"key":"轧制序号","value":"B21802024"},{"key":"支数","value":"10"},{"key":"重量","value":"23790"}]},{"name":"B20","list_info":[{"key":"炉号","value":"18B102434"},{"key":"生成日期","value":"20180226"},{"key":"班次","value":"2"},{"key":"班别","value":"C"},{"key":"连铸机号","value":"B1"},{"key":"状态","value":"21"},{"key":"用途","value":"G"},{"key":"去向","value":"B20"},{"key":"坯型","value":""},{"key":"长度","value":"12000"},{"key":"库位","value":"B20"},{"key":"跨","value":"R"},{"key":"储序","value":""},{"key":"钢种","value":""},{"key":"GKNO","value":"J002B308"},{"key":"订单编号","value":"N0018010693"},{"key":"轧制序号","value":"B21802024"},{"key":"支数","value":"10"},{"key":"重量","value":"23790"}]},{"name":"化学成分","list_info":[{"key":"MN","value":"1.46"},{"key":"B","value":"0.0002"},{"key":"MO","value":"0.00"},{"key":"C","value":"0.23"},{"key":"ALS","value":"0.0000"},{"key":"AL","value":"0.003"},{"key":"N","value":"0.0000"},{"key":"CEQ","value":"0.49"},{"key":"CR","value":"0.04"},{"key":"P","value":"0.033"},{"key":"CU","value":"0.02"},{"key":"S","value":"0.014"},{"key":"NB","value":"0.002"},{"key":"TI","value":"0.0034"},{"key":"V","value":"0.031"},{"key":"SI","value":"0.46"},{"key":"NI","value":"0.01"},{"key":"CA","value":"0.0000"}]}]
     */

    private String formId;
    private String inputCode;
    private String result;
    private String msg;
    private String endFlag;
    private ArrayList<DataBean> data;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getInputCode() {
        return inputCode;
    }

    public void setInputCode(String inputCode) {
        this.inputCode = inputCode;
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

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * name : B20
         * list_info : [{"key":"炉号","value":"18B102434"},{"key":"生成日期","value":"20180226"},{"key":"班次","value":"2"},{"key":"班别","value":"C"},{"key":"连铸机号","value":"B1"},{"key":"状态","value":"21"},{"key":"用途","value":"G"},{"key":"去向","value":"B20"},{"key":"坯型","value":""},{"key":"长度","value":"12000"},{"key":"库位","value":"B20"},{"key":"跨","value":"R"},{"key":"储序","value":""},{"key":"钢种","value":""},{"key":"GKNO","value":"J002B308"},{"key":"订单编号","value":"N0018010693"},{"key":"轧制序号","value":"B21802024"},{"key":"支数","value":"10"},{"key":"重量","value":"23790"}]
         */

        private String name;
        private ArrayList<Info> list_info;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Info> getList_info() {
            return list_info;
        }

        public void setList_info(ArrayList<Info> list_info) {
            this.list_info = list_info;
        }

        public static class Info implements Serializable {
            /**
             * key : 炉号
             * value : 18B102434
             */

            private String key;
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
