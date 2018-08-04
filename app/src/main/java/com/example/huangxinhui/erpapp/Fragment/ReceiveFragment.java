package com.example.huangxinhui.erpapp.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.huangxinhui.erpapp.Adapter.ReceiverAdapter;
import com.example.huangxinhui.erpapp.Information.WarrantyActivity;
import com.example.huangxinhui.erpapp.JavaBean.Err;
import com.example.huangxinhui.erpapp.JavaBean.LoginResult;
import com.example.huangxinhui.erpapp.JavaBean.Machine;
import com.example.huangxinhui.erpapp.JavaBean.Receive;
import com.example.huangxinhui.erpapp.JavaBean.Wear;
import com.example.huangxinhui.erpapp.R;
import com.example.huangxinhui.erpapp.Util.IpConfig;
import com.example.huangxinhui.erpapp.Util.JsonReader;
import com.example.huangxinhui.erpapp.Util.JsonUtil;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReceiveFragment extends Fragment {
    Unbinder unbinder;

    List<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX> data;

    List<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX> zbs;

    List<Receive.DataBean.CfyBean.ListInfoBean> cfy;

    @BindView(R.id.list_query)
    RecyclerView listReceive;

    private Map<String, String> wear;

    private Map<String, String> status;

    private Map<String, String> machine;

    String WarrantyBook = "";

    String FurnaceCode = "";

    EditText code, information;

    ProgressDialog dialog;

    Button btn;

    PopupWindow pop;

    ReceiverAdapter adapter;

    private Map<String, String> data_map;

    RecyclerView listReceiver;

    private Map<String, String> bb;

    private Map<String, String> bc;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    dialog.show();
                    break;
                case 0:
                    // 处理异常，网络请求失败
                    Toast.makeText(getActivity(), "网络异常，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 1:
                    // 处理服务器返回的数据
                    String data = msg.getData().getString("data");
                    if (JsonUtil.isJson(data)) {// 判断是否为json
                        LoginResult result = JSON.parseObject(data, LoginResult.class);
                        if (result != null && !result.getResult().equals("F")) {
                            Toast.makeText(getActivity(), "接收入库成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Err errResult = JSON.parseObject(data, Err.class);
                            Toast.makeText(getActivity(), errResult.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "接收入库失败", Toast.LENGTH_SHORT).show();
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
            }
        }
    };

    public static ReceiveFragment getInstance(
            ArrayList<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX> data,
            ArrayList<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX> zbs,
            ArrayList<Receive.DataBean.CfyBean.ListInfoBean> cfy
    ) {
        ReceiveFragment f = new ReceiveFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        bundle.putSerializable("zbs", zbs);
        bundle.putSerializable("cfy", cfy);
        f.setArguments(bundle);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = (List<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX>) getArguments().getSerializable("data");
        zbs = (List<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX>) getArguments().getSerializable("zbs");
        cfy = (List<Receive.DataBean.CfyBean.ListInfoBean>) getArguments().getSerializable("cfy");
        status = JSON.parseObject(JsonReader.getJson("status.json", getActivity()), Map.class);
        wear = getWear(JSON.parseArray(JsonReader.getJson("wear.json", getActivity()), Wear.class));
        machine = getMachine(JSON.parseArray(JsonReader.getJson("machine.json", getActivity()), Machine.class));
        bb = JSON.parseObject(JsonReader.getJson("bb.json", getActivity()), Map.class);
        bc = JSON.parseObject(JsonReader.getJson("bc.json", getActivity()), Map.class);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getKey().equals("移出库别"))
                data.get(i).setValue(wear.get(data.get(i).getValue()));
            if (data.get(i).getKey().equals("连铸机号")) {
                if (!data.get(i).getValue().equals("")) {
                    data.get(i).setValue(machine.get(data.get(i).getValue()));
                }
            }
            if(data.get(i).getKey().equals("班次")) data.get(i).setValue(bc.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("班组")) data.get(i).setValue(bb.get(data.get(i).getValue()));
        }
    }

    /**
     * 将Wear集合转化为Map，便于取值
     *
     * @param wears
     * @return
     */
    private Map<String, String> getWear(List<Wear> wears) {
        Map<String, String> result = new HashMap<>();
        for (Wear bean : wears) {
            result.put(bean.getValue(), bean.getKey());
        }
        return result;
    }

    /**
     * 将Machine集合转化为Map，便于取值
     *
     * @param machine
     * @return
     */
    private Map<String, String> getMachine(List<Machine> machine) {
        Map<String, String> result = new HashMap<>();
        for (Machine bean : machine) {
            result.put(bean.getValue(), bean.getKey());
        }
        return result;
    }

    /**
     * 将List<Info>列表转化为Map，便于取值
     *
     * @return
     */
    private Map<String, String> exchange(List<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX> data) {
        Map<String, String> result = new HashMap<>();
        for (Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX info : data) {
            result.put(info.getKey(), info.getValue());
        }
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_query, container, false);
        unbinder = ButterKnife.bind(this, view);
        listReceive.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (data != null && !data.isEmpty()) {
            data_map = exchange(data);
            FurnaceCode = data_map.get("出库炉号");
            WarrantyBook = data_map.get("质保书编号");
            adapter = new ReceiverAdapter(data, getActivity());
            adapter.setOnButtonClickListener(new ReceiverAdapter.OnButtonClickListener() {
                @Override
                public void onCLick(View view, int position) {
                    pop.showAtLocation(listReceive, Gravity.CENTER, 0, 0);
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = 0.5f; //0.0-1.0
                    getActivity().getWindow().setAttributes(lp);
                }
            });
            adapter.setOnWatchClickListener(new ReceiverAdapter.OnButtonClickListener() {

                @Override
                public void onCLick(View view, int position) {
//              查看质保书
                    Intent intent = new Intent(getActivity(), WarrantyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("zbs", (Serializable) zbs);
                    bundle.putSerializable("cfy", (Serializable) cfy);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            listReceive.setAdapter(adapter);
        }
        initPopupWindow();
        ScreenAdapterTools.getInstance().loadView(view);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("查询中");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initPopupWindow() {
        final View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_receiver, null);
        code = popView.findViewById(R.id.code);
        information = popView.findViewById(R.id.information);
        popView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提交接收入库
                new Thread(new receiveLibThread(
                        WarrantyBook,
                        FurnaceCode,
                        code.getText().toString().trim(),
                        information.getText().toString().trim()
                )).start();
                ;
            }
        });
        pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.update();
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f; //0.0-1.0
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    class receiveLibThread implements Runnable {
        private String chgLocRptNo;
        private String heatNo;
        private String areaNo;
        private String rowNo;

        public receiveLibThread(String chgLocRptNo, String heatNo, String areaNo, String rowNo) {
            this.chgLocRptNo = chgLocRptNo;
            this.heatNo = heatNo;
            this.areaNo = areaNo;
            this.rowNo = rowNo;
        }

        @Override
        public void run() {

            String result = "";
            String nameSpace = "http://service.sh.icsc.com";
            // 调用的方法名称
            String methodName = "run";
            // EndPoint
            //String endPoint = "http://10.10.4.210:9081/erp/sh/Scanning.ws";//测试
            String endPoint = "http://" + IpConfig.IP + "/erp/sh/Scanning.ws";//正式
            // SOAP Action
            String soapAction = "http://service.sh.icsc.com/run";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            String data = String.format("%-10s", "GPIS07")
                    + String.format("%-12s", chgLocRptNo) + String.format("%-10s", heatNo)
                    + String.format("%-10s", areaNo) + String.format("%-10s", rowNo)
                    + "*";
            // 设置需调用WebService接口需要传入的参数
            Log.i("params", data);
            rpc.addProperty("data", data);

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = false;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            mHandler.sendEmptyMessage(-1);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
                Object object = (Object) envelope.getResponse();
                // 获取返回的结果
                result = object.toString();
                Log.i("receivelib", result);

                // 如果有数据返回，通知handler 1
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("data", result);
                msg.setData(bundle);
                msg.sendToTarget();
            } catch (Exception e) {
                // 如果捕获异常，通知handler 0
                mHandler.sendEmptyMessage(0);
                e.printStackTrace();
            }
        }
    }

}
