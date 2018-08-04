package com.example.huangxinhui.erpapp.Function;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.huangxinhui.erpapp.Information.QueryInformationActivity;
import com.example.huangxinhui.erpapp.JavaBean.Machine;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueryActivity extends AppCompatActivity {

    @BindView(R.id.furnaceCode)
    EditText furnaceCode;
    @BindView(R.id.deviceNumber)
    TextView deviceNumber;
    @BindView(R.id.producedDate)
    TextView producedDate;

    String date = "";

    ProgressDialog dialog;

    AlertDialog data_dialog;

    List<Machine> machine;

    String device_id = "";

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
                    Toast.makeText(QueryActivity.this, "网络异常，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 1:
                    // 处理服务器返回的数据
                    String data = msg.getData().getString("data");
                    if (JsonUtil.isJson(data)) {
                        Query query = JSON.parseObject(data, Query.class);
                        if (query != null && !query.getResult().equals("F")) {
                            Intent intent = new Intent(QueryActivity.this, QueryInformationActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("title", furnaceCode.getText().toString());
                            bundle.putSerializable("data", query.getData());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(QueryActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QueryActivity.this, data, Toast.LENGTH_SHORT).show();
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        dialog = new ProgressDialog(this);
        dialog.setMessage("查询中");
        machine = JSON.parseArray(JsonReader.getJson("machine.json", QueryActivity.this), Machine.class);
    }

    @OnClick({R.id.back, R.id.query, R.id.producedDate,R.id.deviceNumber})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.query:
                if (date == null) {
                    Toast.makeText(this, "请选择查询日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new QueryThread(furnaceCode.getText().toString(), device_id)).start();
                break;
            case R.id.producedDate:
                if (data_dialog == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View itemView = LayoutInflater.from(this).inflate(R.layout.item_date, null);
                    final DatePicker picker = itemView.findViewById(R.id.dataPicker);
                    builder.setTitle("请选择查询日期")
                            .setView(itemView)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            date = new SimpleDateFormat("yyyyMMdd").format(new Date(picker.getYear() - 1900, picker.getMonth(), picker.getDayOfMonth()));
                            producedDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(picker.getYear() - 1900, picker.getMonth(), picker.getDayOfMonth())));
                        }
                    });
                    data_dialog = builder.create();
                }
                data_dialog.show();
                break;
            case R.id.deviceNumber:
                OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        deviceNumber.setText(machine.get(options1).getKey());
                        device_id = machine.get(options1).getValue();
                    }
                }).setOutSideCancelable(false)
                        .setContentTextSize(25)
                        .build();
                pvOptions.setPicker(machine);
                pvOptions.setTitleText("请选择连铸机号");
                pvOptions.show();
                break;
        }
    }

    class QueryThread implements Runnable {

        private String heatNo;
        private String ccNo;

        public QueryThread(String heatNo, String ccNo) {
            this.heatNo = heatNo;
            this.ccNo = ccNo;
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

            String data = String.format("%-10s", "GPIS01")
                    + String.format("%-15s", heatNo) + String.format("%-8s", date)
                    + String.format("%-10s", ccNo) + "*";
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
                Log.i("Query", result);

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
