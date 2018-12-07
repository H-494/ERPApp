package com.example.huangxinhui.erpapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.huangxinhui.erpapp.JavaBean.GroupBean;
import com.example.huangxinhui.erpapp.JavaBean.Version;
import com.example.huangxinhui.erpapp.Permission.PermissionsActivity;
import com.example.huangxinhui.erpapp.Permission.PermissionsChecker;
import com.example.huangxinhui.erpapp.Util.DownLoadRunnable;
import com.example.huangxinhui.erpapp.Util.IpConfig;
import com.example.huangxinhui.erpapp.Util.JsonUtil;
import com.example.huangxinhui.erpapp.Util.MyProvide;
import com.example.huangxinhui.erpapp.Util.MyUtils;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.remember)
    CheckBox remember;
    @BindView(R.id.team)
    TextView team;

    SharedPreferences sp;

    ProgressDialog dialog;

    String code = "";

    // 班组信息
    List<GroupBean> list_group = new ArrayList<>();

    private int select_group;

    App app;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 0x999; // 请求码

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    /**
     *
     */
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPermissionsChecker = new PermissionsChecker(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        app = (App) getApplication();
        list_group.add(new GroupBean("A", "甲"));
        list_group.add(new GroupBean("B", "乙"));
        list_group.add(new GroupBean("C", "丙"));
        dialog = new ProgressDialog(this);
        dialog.setMessage("登录中");
        sp = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        if (sp.getBoolean("remember", false)) {
            user.setText(sp.getString("username", null));
            remember.setChecked(true);
        }
        team.setText(list_group.get(select_group).getName());
        new Thread(new UpdateThread()).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
        Log.i("onActivityResult", "requestCode=" + requestCode + "resultCode" + resultCode);
        if (requestCode == 26) {
            checkO();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    dialog.show();
                    break;
                case -2:
                    // 处理异常，网络请求失败
                    Toast.makeText(LoginActivity.this, "网络异常，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 0:
                    if (dialog.isShowing())
                        dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    // 记住密码
                    if (remember.isChecked()) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", user.getText().toString());
                        editor.putBoolean("remember", true);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", "");
                        editor.putBoolean("remember", false);
                        editor.apply();
                    }
                    code = user.getText().toString().trim();
                    Bundle bundle = new Bundle();
                    bundle.putString("code",code);
                    app.setGroup(list_group.get(select_group));
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtras(bundle);
                    pwd.setText("");
                    startActivity(intent);

                    if (dialog.isShowing())
                        dialog.dismiss();
                    break;
                case 3:
                    String data = msg.getData().getString("data");
                    if (JsonUtil.isJson(data)){
                        final Version version = JSON.parseObject(data, Version.class);
                        if (version.getVersionCode() > getVersion(LoginActivity.this)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("检测到有新版本更新，是否下载最新版本？")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String url = version.getUrl();
                                            Log.i("data",url);

//                                        String url = "gdown.baidu.com/data/wisegame/fd84b7f6746f0b18/baiduyinyue_4802.apk";
                                            new Thread(new DownLoadRunnable(LoginActivity.this,  url, "产销ERP", 0, handler)).start();
                                        }
                                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                        }
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.team, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.team:
                OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        select_group = options1;
                        team.setText(list_group.get(options1).getName());
                    }
                }).setOutSideCancelable(false)
                        .setContentTextSize(25)
                        .build();
                pvOptions.setPicker(list_group);
                pvOptions.setSelectOptions(select_group);
                pvOptions.setTitleText("请选择班组");
                pvOptions.show();
                break;
            case R.id.login:
                new Thread(new LoginThread(user.getText().toString().trim(), pwd.getText().toString())).start();
//                app.setGroup(list_group.get(select_group));
//                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
                break;
        }
    }


    /**
     * 登录请求
     */
    class LoginThread implements Runnable {
        private String userName;
        private String password;

        public LoginThread(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        public void run() {
            String result = "";
            String nameSpace = "http://service.sh.icsc.com";
            // 调用的方法名称
            String methodName = "run";
            // EndPoint
            //String endPoint = "http://10.10.4.210:9081/erp/sh/Scanning.ws";//测试
            String endPoint = "http://" + "10.10.3.216:80" + "/erp/sh/Scanning.ws";//正式
            // SOAP Action
            String soapAction = "http://service.sh.icsc.com/run";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            String data = String.format("%-10s", "SHHL31")
                    + String.format("%-10s", userName) + String.format("%-10s", password)
                    + String.format("%-10s", list_group.get(select_group).getId())
                    + String.format("%-1s", "A") + "*";

            Log.i("params", data);
            // 设置需调用WebService接口需要传入的参数
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
                String result1 = result.substring(10, 11).trim();
                if ("F".equals(result1)) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    mHandler.sendEmptyMessage(1);
                }


                // 如果有数据返回，通知handler 1
//                Message msg = mHandler.obtainMessage();
//                msg.what = 1;
//                Bundle bundle = new Bundle();
//                bundle.putString("data", result);
//                msg.setData(bundle);
//                msg.sendToTarget();
            } catch (Exception e) {
                // 如果捕获异常，通知handler 0
                mHandler.sendEmptyMessage(-2);
                e.printStackTrace();
            }
        }
    }

    class UpdateThread implements Runnable {

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

            String data = String.format("%-10s", "GPIS12") + "*";
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
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
                Object object = (Object) envelope.getResponse();
                // 获取返回的结果
                result = object.toString();
                Log.i("Receiver", result);

                Message msg = mHandler.obtainMessage();
                msg.what = 3;
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


    //handler更新ui
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(LoginActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    checkO();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(LoginActivity.this, "更新出错", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    break;
            }
            return false;
        }
    });


    // 取得版本号
    public static int getVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 检查安卓8.0
     */
    private void checkO() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {//有权限
                install();
            } else { // 没有权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10010);
            }
        } else {
            install();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10010:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    install();
                } else {
                    startInstallPermissionSettingActivity();
                }
                break;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, 26);
    }

    public void install() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + MyUtils.PACKAGE_NAME + "/" + MyUtils.APP_NAME);
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri data = MyProvide.getUriForFile(this, "com.example.huangxinhui.erpapp.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
        Log.i("install", "finish");
    }

}
