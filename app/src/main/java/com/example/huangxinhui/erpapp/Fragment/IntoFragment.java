package com.example.huangxinhui.erpapp.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.huangxinhui.erpapp.Adapter.IntoAdapter;
import com.example.huangxinhui.erpapp.JavaBean.Err;
import com.example.huangxinhui.erpapp.JavaBean.LoginResult;
import com.example.huangxinhui.erpapp.JavaBean.Query;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IntoFragment extends Fragment {
    List<Query.DataBean.Info> data;

    String WarrantyBook;

    @BindView(R.id.list_query)
    RecyclerView listInto;

    Unbinder unbinder;

    PopupWindow pop;

    IntoAdapter adapter;

    EditText code, information;

    Button btn;

    ProgressDialog dialog;

    private Map<String, String> status;

    private Map<String, String> wear;

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
                            Toast.makeText(getActivity(), "入库成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Err errResult = JSON.parseObject(data, Err.class);
                            Toast.makeText(getActivity(), errResult.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "入库失败", Toast.LENGTH_SHORT).show();
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
            }
        }
    };

    public static IntoFragment getInstance(ArrayList<Query.DataBean.Info> data, String WarrantyBook) {
        IntoFragment inf = new IntoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        bundle.putString("title", WarrantyBook);
        inf.setArguments(bundle);
        return inf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = JSON.parseObject(JsonReader.getJson("status.json", getActivity()), Map.class);
        wear = getWear(JSON.parseArray(JsonReader.getJson("wear.json", getActivity()), Wear.class));
        data = (List<Query.DataBean.Info>) getArguments().getSerializable("data");
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getKey().equals("钢坯状态")) data.get(i).setValue(status.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("移出库别")) data.get(i).setValue(wear.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("移入库别")) data.get(i).setValue(wear.get(data.get(i).getValue()));
        }
        WarrantyBook = getArguments().getString("title");
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
            result.put(bean.getValue(),bean.getKey());
        }
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_query, container, false);
        unbinder = ButterKnife.bind(this, view);
        initPopupWindow();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("查询中");
        listInto.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new IntoAdapter(data, getActivity());
        adapter.setOnButtonClickListener(new IntoAdapter.OnButtonClickListener() {
            @Override
            public void onCLick(View view, int position) {
                pop.showAtLocation(listInto, Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.5f; //0.0-1.0
                getActivity().getWindow().setAttributes(lp);
            }
        });
        listInto.setAdapter(adapter);
        ScreenAdapterTools.getInstance().loadView(view);
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
        btn = popView.findViewById(R.id.button);
        btn.setText("点击入库");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提交钢柸入库
                new Thread(new intoLibThread(
                        WarrantyBook,
                        code.getText().toString().trim(),
                        information.getText().toString().trim())
                ).start();
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

    class intoLibThread implements Runnable {
        private String chgLocRptNo;
        private String areaNo;
        private String rowNo;

        public intoLibThread(String chgLocRptNo, String areaNo, String rowNo) {
            this.chgLocRptNo = chgLocRptNo;
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

            String data = String.format("%-10s", "GPIS09")
                    + String.format("%-12s", chgLocRptNo) + String.format("%-10s", areaNo)
                    + String.format("%-10s", rowNo) + "*";
            // 设置需调用WebService接口需要传入的参数
            Log.i("params", data);
            rpc.addProperty("date", data);

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
                Log.i("intolib", result);

                // 如果有数据返回，通知handler 1
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("data", result);
                msg.setData(bundle);
                msg.sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

