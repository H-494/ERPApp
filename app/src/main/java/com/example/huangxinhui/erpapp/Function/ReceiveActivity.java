package com.example.huangxinhui.erpapp.Function;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.huangxinhui.erpapp.Information.ReceiveInformationActivity;
import com.example.huangxinhui.erpapp.JavaBean.Machine;
import com.example.huangxinhui.erpapp.JavaBean.Receive;
import com.example.huangxinhui.erpapp.R;
import com.example.huangxinhui.erpapp.Util.IpConfig;
import com.example.huangxinhui.erpapp.Util.JsonReader;
import com.example.huangxinhui.erpapp.Util.JsonUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveActivity extends AppCompatActivity {
    @BindView(R.id.producedDate)
    TextView producedDate;
    @BindView(R.id.furnaceCode)
    EditText furnaceCode;
    @BindView(R.id.brevityCode)
    EditText brevityCode;
    @BindView(R.id.deviceNumber)
    TextView deviceNumber;
    @BindView(R.id.qualityBooks)
    EditText qualityBooks;

    String date = "";

    ProgressDialog dialog;

    AlertDialog date_dialog;

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
                    Toast.makeText(ReceiveActivity.this, "网络异常，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 1:
                    // 处理服务器返回的数据
                    String data = msg.getData().getString("data");
                    if (JsonUtil.isJson(data)) {// 判断是否为json
                        Receive result = JSON.parseObject(data, Receive.class);
                        if (result != null && !result.getResult().equals("F")) {
                            Intent intent = new Intent(ReceiveActivity.this, ReceiveInformationActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", (Serializable) result.getData().get(0).getInfos());
                            bundle.putSerializable("cfy", (Serializable) result.getData().get(0).getCfy().getList_info());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ReceiveActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ReceiveActivity.this, data, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_receive);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        dialog = new ProgressDialog(this);
        dialog.setMessage("查询中");
        machine = JSON.parseArray(JsonReader.getJson("machine.json", ReceiveActivity.this), Machine.class);
    }

    @OnClick({R.id.back, R.id.producedDate, R.id.query, R.id.img_qr, R.id.deviceNumber})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.producedDate:
                if (date_dialog == null) {
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
                            }
                    );
                    date_dialog = builder.create();
                }
                date_dialog.show();
                break;
            case R.id.query:
                new Thread(new ReceiverThread(furnaceCode.getText().toString().trim(), brevityCode.getText().toString().trim(), device_id, qualityBooks.getText().toString().trim())).start();
                break;
            case R.id.img_qr:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                    //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA,}, 1);
                } else {
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, 0x666);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x666) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    qualityBooks.setText(result);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class ReceiverThread implements Runnable {

        private String heatNo;
        private String heatNoj;
        private String ccNo;
        private String chgLocRptNo;

        public ReceiverThread(String heatNo, String heatNoj, String ccNo, String chgLocRptNo) {
            this.heatNo = heatNo;
            this.heatNoj = heatNoj;
            this.ccNo = ccNo;
            this.chgLocRptNo = chgLocRptNo;
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

            String data = String.format("%-10s", "GPIS02")
                    + String.format("%-10s", heatNo) + String.format("%-10s", heatNoj)
                    + String.format("%-10s", date) + String.format("%-10s", ccNo)
                    + String.format("%-12s", chgLocRptNo) + "*";
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
                Log.i("Receiver", result);

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
