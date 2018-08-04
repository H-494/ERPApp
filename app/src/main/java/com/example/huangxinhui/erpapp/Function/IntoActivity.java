package com.example.huangxinhui.erpapp.Function;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.huangxinhui.erpapp.Information.IntoInformationActivity;
import com.example.huangxinhui.erpapp.JavaBean.Query;
import com.example.huangxinhui.erpapp.R;
import com.example.huangxinhui.erpapp.Util.IpConfig;
import com.example.huangxinhui.erpapp.Util.JsonUtil;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntoActivity extends AppCompatActivity {

    @BindView(R.id.intoCode)
    EditText intoCode;
    @BindView(R.id.brevityCode)
    EditText brevityCode;
    ProgressDialog dialog;
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
                    Toast.makeText(IntoActivity.this, "网络异常，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 1:
                    String data = msg.getData().getString("data");
                    if (JsonUtil.isJson(data)) {
                        Query query = JSON.parseObject(data, Query.class);
                        if (query != null && !query.getResult().equals("F")) {
                            Intent intent = new Intent(IntoActivity.this, IntoInformationActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("title", intoCode.getText().toString());
                            bundle.putSerializable("data", query.getData());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(IntoActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(IntoActivity.this, data, Toast.LENGTH_SHORT).show();
                    }
                    if (dialog.isShowing()) dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        dialog = new ProgressDialog(this);
        dialog.setMessage("查询中");
    }

    @OnClick({R.id.back, R.id.query})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.query:
                new Thread(new IntoThread(intoCode.getText().toString().trim(), brevityCode.getText().toString().trim())).start();
                break;
        }
    }

    class IntoThread implements Runnable {
        private String heatNo;
        private String heatNoj;

        public IntoThread(String intoCode, String brevityCode) {
            this.heatNo = intoCode;
            this.heatNoj = brevityCode;
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

            String data = String.format("%-10s", "GPIS06")
                    + String.format("%-10s", heatNo) + String.format("%-10s", heatNoj)
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
                Log.i("out", result);
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("data", result);
                msg.setData(bundle);
                msg.sendToTarget();
            } catch (Exception e) {
                mHandler.sendEmptyMessage(0);
                e.printStackTrace();
            }
        }
    }
}
