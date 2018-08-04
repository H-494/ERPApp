package com.example.huangxinhui.erpapp.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.huangxinhui.erpapp.Adapter.OutAdapter;
import com.example.huangxinhui.erpapp.App;
import com.example.huangxinhui.erpapp.JavaBean.Banci;
import com.example.huangxinhui.erpapp.JavaBean.BusinessType;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OutFragment extends Fragment {

    Unbinder unbinder;
    List<Query.DataBean.Info> data;

    Map<String, String> data_map;

    ProgressDialog dialog;

    @BindView(R.id.list_query)
    RecyclerView listOut;

    private Map<String, String> status;

    private Map<String, String> wear;

    private PopupWindow pop;

    private List<BusinessType> types = new ArrayList<>();
    private int select_lb;

    private AlertDialog dialog_date;

    private String selected_date;

    private List<Wear> list_wear;
    private int selected_wear;

    private List<Banci> list_banci = new ArrayList<>();

    private int selected_banci;

    private App app;

    private String code;

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
                            Toast.makeText(getActivity(), "出库成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Err errResult = JSON.parseObject(data, Err.class);
                            Toast.makeText(getActivity(), errResult.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "出库失败", Toast.LENGTH_SHORT).show();
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
            }
        }
    };


    public static OutFragment getInstance(ArrayList<Query.DataBean.Info> data,String code) {
        OutFragment qf = new OutFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        bundle.putString("code",code);
        qf.setArguments(bundle);
        return qf;
    }

    /**
     * 初始化pop
     */
    private void initPop() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_out, null);
        final TextView lb = view.findViewById(R.id.lb);
        final TextView outWarehouseNo = view.findViewById(R.id.outWarehouseNo);
        final TextView chgLocDate = view.findViewById(R.id.chgLocDate);
        final EditText carNo = view.findViewById(R.id.carNo);
        final TextView operateShift = view.findViewById(R.id.operateShift);
        TextView operateCrew = view.findViewById(R.id.operateCrew);
        final EditText driver = view.findViewById(R.id.driver);
        final TextView inWarehouseNo = view.findViewById(R.id.inWarehouseNo);
        final EditText qty = view.findViewById(R.id.qty);
        final EditText bz = view.findViewById(R.id.bz);

        qty.setText(data_map.get("块数"));

        lb.setText(types.get(select_lb).getName());
        lb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        select_lb = options1;
                        lb.setText(types.get(select_lb).getName());
                        showPop();
                    }
                }).setOutSideCancelable(false)
                        .setContentTextSize(25)
                        .build();
                pvOptions.setPicker(types);
                pvOptions.setSelectOptions(select_lb);
                pvOptions.setTitleText("请选择班组");
                pvOptions.show();
            }
        });
        outWarehouseNo.setText(wear.get(data_map.get("当前库")));

        selected_date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        chgLocDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        chgLocDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pop.isShowing())
                    pop.dismiss();
                if (dialog_date == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_date, null);
                    final DatePicker picker = itemView.findViewById(R.id.dataPicker);
                    builder.setTitle("请选择查询日期")
                            .setView(itemView)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showPop();
                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selected_date = new SimpleDateFormat("yyyyMMdd").format(new Date(picker.getYear() - 1900, picker.getMonth(), picker.getDayOfMonth()));
                            chgLocDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(picker.getYear() - 1900, picker.getMonth(), picker.getDayOfMonth())));
                            showPop();
                        }
                    });
                    dialog_date = builder.create();
                }
                dialog_date.show();
            }
        });
        inWarehouseNo.setText(list_wear.get(selected_wear).getKey());
        inWarehouseNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        selected_wear = options1;
                        inWarehouseNo.setText(list_wear.get(selected_wear).getKey());
                        showPop();
                    }
                }).setOutSideCancelable(false)
                        .setContentTextSize(25)
                        .build();
                pvOptions.setPicker(list_wear);
                pvOptions.setSelectOptions(selected_wear);
                pvOptions.setTitleText("请选择接收库别");
                pvOptions.show();
            }
        });

        operateShift.setText(list_banci.get(selected_banci).getDesc());
        operateShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        selected_banci = options1;
                        operateShift.setText(list_banci.get(selected_banci).getDesc());
                        showPop();
                    }
                }).setOutSideCancelable(false)
                        .setContentTextSize(25)
                        .build();
                pvOptions.setPicker(list_banci);
                pvOptions.setSelectOptions(selected_banci);
                pvOptions.setTitleText("请选择班次");
                pvOptions.show();
            }
        });

        operateCrew.setText(app.getGroup().getName());

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!qty.getText().toString().equals("")) {
                    new Thread(new OutConfirmThread(
                            types.get(select_lb).getValue(),
                            data_map.get("当前库"),
                            selected_date,
                            carNo.getText().toString(),
                            list_banci.get(selected_banci).getCode(),
                            app.getGroup().getId(),
                            driver.getText().toString(),
                            list_wear.get(selected_wear).getValue(),
                            bz.getText().toString(),
                            data_map.get("炉号"),
                            data_map.get("长度"),
                            data_map.get("重量"),
                            data_map.get("钢种"),
                            data_map.get("钢坯状态"),
                            //data_map.get("块数"),// 后面改
                            qty.getText().toString(),
                            data_map.get("当前库"),
                            data_map.get("当前跨"),
                            data_map.get("当前储序"),
                            code
                    )).start();
                } else {
                    Toast.makeText(app, "块数不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = JSON.parseObject(JsonReader.getJson("status.json", getActivity()), Map.class);
        wear = getWear(JSON.parseArray(JsonReader.getJson("wear.json", getActivity()), Wear.class));
        data = (List<Query.DataBean.Info>) getArguments().getSerializable("data");
        code = getArguments().getString("code");
        list_wear = JSON.parseArray(JsonReader.getJson("wear.json", getActivity()), Wear.class);
        data_map = exchange(data);
        types.add(new BusinessType("热送", "A"));
        types.add(new BusinessType("冷发", "B"));

        list_banci.add(new Banci("1", "夜"));
        list_banci.add(new Banci("2", "白"));
        list_banci.add(new Banci("3", "中"));

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getKey().equals("钢坯状态"))
                data.get(i).setValue(status.get(data.get(i).getValue()));
            if (data.get(i).getKey().equals("当前库"))
                data.get(i).setValue(wear.get(data.get(i).getValue()));
        }

    }

    /**
     * 将List转化为Map
     */
    private Map<String, String> exchange(List<Query.DataBean.Info> data) {
        Map<String, String> result = new HashMap<>();
        for (Query.DataBean.Info bean : data) {
            result.put(bean.getKey(), bean.getValue());
        }
        return result;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_query, container, false);
        ScreenAdapterTools.getInstance().loadView(view);
        unbinder = ButterKnife.bind(this, view);
        app = (App) getActivity().getApplication();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("出库中");
        initPop();
        listOut.setLayoutManager(new LinearLayoutManager(getActivity()));
        OutAdapter adapter = new OutAdapter(data, getActivity());
        adapter.setOnButtonClickListener(new OutAdapter.OnButtonClickListener() {
            @Override
            public void onCLick(View view, int position) {
                showPop();
            }
        });
        listOut.setAdapter(adapter);
        return view;
    }

    private void showPop() {
        if (!pop.isShowing()) {
            pop.showAtLocation(listOut, Gravity.CENTER, 0, 0);
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.5f; //0.0-1.0
            getActivity().getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class OutConfirmThread implements Runnable {
        private String lb;
        private String outWarehouseNo;
        private String chgLocDate;
        private String carNo;
        private String operateShift;
        private String operateCrew;
        private String driver;
        private String inWarehouseNo;
        private String bz;
        private String heatNo;
        private String length;
        private String weight;
        private String spec;
        private String status;
        private String qty;
        private String warehouseNo;
        private String areaNo;
        private String rowNo;
        private String code;

        public OutConfirmThread(String lb,
                                String outWarehouseNo,
                                String chgLocDate,
                                String carNo,
                                String operateShift,
                                String operateCrew,
                                String driver,
                                String inWarehouseNo,
                                String bz,
                                String heatNo,
                                String length,
                                String weight,
                                String spec,
                                String status,
                                String qty,
                                String warehouseNo,
                                String areaNo,
                                String rowNo,
                                String code
        ) {
            this.lb = lb;
            this.outWarehouseNo = outWarehouseNo;
            this.chgLocDate = chgLocDate;
            this.carNo = carNo;
            this.operateShift = operateShift;
            this.operateCrew = operateCrew;
            this.driver = driver;
            this.inWarehouseNo = inWarehouseNo;
            this.bz = bz;
            this.heatNo = heatNo;
            this.length = length;
            this.weight = weight;
            this.spec = spec;
            this.status = status;
            this.qty = qty;
            this.warehouseNo = warehouseNo;
            this.areaNo = areaNo;
            this.rowNo = rowNo;
            this.code = code;
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

            String data = String.format("%-10s", "GPIS08")
                    + String.format("%-10s", lb)
                    + String.format("%-10s", outWarehouseNo)
                    + String.format("%-8s", chgLocDate)
                    + String.format("%-15s", carNo)
                    + String.format("%-5s", operateShift)
                    + String.format("%-5s", operateCrew)
                    + String.format("%-10s", driver)
                    + String.format("%-10s", inWarehouseNo)
                    + String.format("%-50s", bz)
                    + String.format("%-10s", heatNo)
                    + String.format("%-10s", length)
                    + String.format("%-10s", weight)
                    + String.format("%-20s", spec)
                    + String.format("%-5s", status)
                    + String.format("%-3s", qty)
                    + String.format("%-10s", warehouseNo)
                    + String.format("%-10s", areaNo)
                    + String.format("%-10s", rowNo)
                    + String.format("%-10s", code)
                    + "*";
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
                Log.i("login", result);

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
