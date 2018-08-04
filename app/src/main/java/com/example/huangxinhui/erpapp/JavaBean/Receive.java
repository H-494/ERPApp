package com.example.huangxinhui.erpapp.JavaBean;

import java.io.Serializable;
import java.util.List;

public class Receive implements Serializable{

    /**
     * formId : GPIS02
     * inputCode : Y
     * result : S
     * msg : 成功
     * data : [{"cfy":{"name":"化学成分","list_info":[{"key":"Mn","value":"0.75~0.75"},{"key":"C","value":"0.56~0.58"},{"key":"Al","value":"0.01~0.01"},{"key":"Cr","value":"0.78~0.78"},{"key":"P","value":"0.010~0.010"},{"key":"Cu","value":"0.01~0.10"},{"key":"S","value":"0.011~0.011"},{"key":"Si","value":"0.25~0.28"},{"key":"Ni","value":"0.01~0.11"}]},"infos":[{"tabs":{"name":"data","list_info":[{"key":"质保书编号","value":"A180700009"},{"key":"出库日期","value":"20180709"},{"key":"移出库别","value":"300"},{"key":"出库炉号","value":"P18000265"},{"key":"块数","value":"3"},{"key":"理重(t)","value":"5.343"},{"key":"实际重量(t)","value":"5.343"},{"key":"连铸机号","value":""},{"key":"毛重(t)","value":"0.000"},{"key":"皮重(t)","value":"5.343"},{"key":"过磅时间","value":"20180709111618"},{"key":"运输方式","value":"车运"}]},"zbs":{"name":"chg","list_info":[{"key":"质保书编号","value":"A180700009"},{"key":"企业牌号","value":"ZD35"},{"key":"国标牌号","value":"ZD35"},{"key":"规格","value":"160*160*9"},{"key":"发货分厂","value":"炼钢三厂"},{"key":"接收分厂","value":"棒5B"},{"key":"车号","value":"2320"},{"key":"驾驶员","value":"施卫兵"},{"key":"生成批号","value":"P18000265"},{"key":"垮号","value":""},{"key":"定尺长度","value":"9"},{"key":"支数","value":"3"},{"key":"理论重量(t)","value":"5.343"},{"key":"生产日期","value":""},{"key":"班组","value":""},{"key":"表面质量","value":"合格"},{"key":"表面检验员","value":""},{"key":"审核人","value":"管理员"},{"key":"结果判定","value":"合格"}]}}]}]
     * endFlag : *
     */

    private String formId;
    private String inputCode;
    private String result;
    private String msg;
    private String endFlag;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * cfy : {"name":"化学成分","list_info":[{"key":"Mn","value":"0.75~0.75"},{"key":"C","value":"0.56~0.58"},{"key":"Al","value":"0.01~0.01"},{"key":"Cr","value":"0.78~0.78"},{"key":"P","value":"0.010~0.010"},{"key":"Cu","value":"0.01~0.10"},{"key":"S","value":"0.011~0.011"},{"key":"Si","value":"0.25~0.28"},{"key":"Ni","value":"0.01~0.11"}]}
         * infos : [{"tabs":{"name":"data","list_info":[{"key":"质保书编号","value":"A180700009"},{"key":"出库日期","value":"20180709"},{"key":"移出库别","value":"300"},{"key":"出库炉号","value":"P18000265"},{"key":"块数","value":"3"},{"key":"理重(t)","value":"5.343"},{"key":"实际重量(t)","value":"5.343"},{"key":"连铸机号","value":""},{"key":"毛重(t)","value":"0.000"},{"key":"皮重(t)","value":"5.343"},{"key":"过磅时间","value":"20180709111618"},{"key":"运输方式","value":"车运"}]},"zbs":{"name":"chg","list_info":[{"key":"质保书编号","value":"A180700009"},{"key":"企业牌号","value":"ZD35"},{"key":"国标牌号","value":"ZD35"},{"key":"规格","value":"160*160*9"},{"key":"发货分厂","value":"炼钢三厂"},{"key":"接收分厂","value":"棒5B"},{"key":"车号","value":"2320"},{"key":"驾驶员","value":"施卫兵"},{"key":"生成批号","value":"P18000265"},{"key":"垮号","value":""},{"key":"定尺长度","value":"9"},{"key":"支数","value":"3"},{"key":"理论重量(t)","value":"5.343"},{"key":"生产日期","value":""},{"key":"班组","value":""},{"key":"表面质量","value":"合格"},{"key":"表面检验员","value":""},{"key":"审核人","value":"管理员"},{"key":"结果判定","value":"合格"}]}}]
         */

        private CfyBean cfy;
        private List<InfosBean> infos;

        public CfyBean getCfy() {
            return cfy;
        }

        public void setCfy(CfyBean cfy) {
            this.cfy = cfy;
        }

        public List<InfosBean> getInfos() {
            return infos;
        }

        public void setInfos(List<InfosBean> infos) {
            this.infos = infos;
        }

        public static class CfyBean implements Serializable{
            /**
             * name : 化学成分
             * list_info : [{"key":"Mn","value":"0.75~0.75"},{"key":"C","value":"0.56~0.58"},{"key":"Al","value":"0.01~0.01"},{"key":"Cr","value":"0.78~0.78"},{"key":"P","value":"0.010~0.010"},{"key":"Cu","value":"0.01~0.10"},{"key":"S","value":"0.011~0.011"},{"key":"Si","value":"0.25~0.28"},{"key":"Ni","value":"0.01~0.11"}]
             */

            private String name;
            private List<ListInfoBean> list_info;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ListInfoBean> getList_info() {
                return list_info;
            }

            public void setList_info(List<ListInfoBean> list_info) {
                this.list_info = list_info;
            }

            public static class ListInfoBean implements Serializable{
                /**
                 * key : Mn
                 * value : 0.75~0.75
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

        public static class InfosBean implements Serializable{
            /**
             * tabs : {"name":"data","list_info":[{"key":"质保书编号","value":"A180700009"},{"key":"出库日期","value":"20180709"},{"key":"移出库别","value":"300"},{"key":"出库炉号","value":"P18000265"},{"key":"块数","value":"3"},{"key":"理重(t)","value":"5.343"},{"key":"实际重量(t)","value":"5.343"},{"key":"连铸机号","value":""},{"key":"毛重(t)","value":"0.000"},{"key":"皮重(t)","value":"5.343"},{"key":"过磅时间","value":"20180709111618"},{"key":"运输方式","value":"车运"}]}
             * zbs : {"name":"chg","list_info":[{"key":"质保书编号","value":"A180700009"},{"key":"企业牌号","value":"ZD35"},{"key":"国标牌号","value":"ZD35"},{"key":"规格","value":"160*160*9"},{"key":"发货分厂","value":"炼钢三厂"},{"key":"接收分厂","value":"棒5B"},{"key":"车号","value":"2320"},{"key":"驾驶员","value":"施卫兵"},{"key":"生成批号","value":"P18000265"},{"key":"垮号","value":""},{"key":"定尺长度","value":"9"},{"key":"支数","value":"3"},{"key":"理论重量(t)","value":"5.343"},{"key":"生产日期","value":""},{"key":"班组","value":""},{"key":"表面质量","value":"合格"},{"key":"表面检验员","value":""},{"key":"审核人","value":"管理员"},{"key":"结果判定","value":"合格"}]}
             */

            private TabsBean tabs;
            private ZbsBean zbs;

            public TabsBean getTabs() {
                return tabs;
            }

            public void setTabs(TabsBean tabs) {
                this.tabs = tabs;
            }

            public ZbsBean getZbs() {
                return zbs;
            }

            public void setZbs(ZbsBean zbs) {
                this.zbs = zbs;
            }

            public static class TabsBean implements Serializable{
                /**
                 * name : data
                 * list_info : [{"key":"质保书编号","value":"A180700009"},{"key":"出库日期","value":"20180709"},{"key":"移出库别","value":"300"},{"key":"出库炉号","value":"P18000265"},{"key":"块数","value":"3"},{"key":"理重(t)","value":"5.343"},{"key":"实际重量(t)","value":"5.343"},{"key":"连铸机号","value":""},{"key":"毛重(t)","value":"0.000"},{"key":"皮重(t)","value":"5.343"},{"key":"过磅时间","value":"20180709111618"},{"key":"运输方式","value":"车运"}]
                 */

                private String name;
                private List<ListInfoBeanX> list_info;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<ListInfoBeanX> getList_info() {
                    return list_info;
                }

                public void setList_info(List<ListInfoBeanX> list_info) {
                    this.list_info = list_info;
                }

                public static class ListInfoBeanX implements Serializable{
                    /**
                     * key : 质保书编号
                     * value : A180700009
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

            public static class ZbsBean implements Serializable{
                /**
                 * name : chg
                 * list_info : [{"key":"质保书编号","value":"A180700009"},{"key":"企业牌号","value":"ZD35"},{"key":"国标牌号","value":"ZD35"},{"key":"规格","value":"160*160*9"},{"key":"发货分厂","value":"炼钢三厂"},{"key":"接收分厂","value":"棒5B"},{"key":"车号","value":"2320"},{"key":"驾驶员","value":"施卫兵"},{"key":"生成批号","value":"P18000265"},{"key":"垮号","value":""},{"key":"定尺长度","value":"9"},{"key":"支数","value":"3"},{"key":"理论重量(t)","value":"5.343"},{"key":"生产日期","value":""},{"key":"班组","value":""},{"key":"表面质量","value":"合格"},{"key":"表面检验员","value":""},{"key":"审核人","value":"管理员"},{"key":"结果判定","value":"合格"}]
                 */

                private String name;
                private List<ListInfoBeanXX> list_info;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<ListInfoBeanXX> getList_info() {
                    return list_info;
                }

                public void setList_info(List<ListInfoBeanXX> list_info) {
                    this.list_info = list_info;
                }

                public static class ListInfoBeanXX implements Serializable{
                    /**
                     * key : 质保书编号
                     * value : A180700009
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
    }
}
