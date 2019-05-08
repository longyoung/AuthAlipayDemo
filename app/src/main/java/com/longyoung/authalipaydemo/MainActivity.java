package com.longyoung.authalipaydemo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;

import java.util.Map;
/**
 * Created by longyoung on 2019/4/28.
 */
public class MainActivity extends AppCompatActivity {

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "xxx";
    /** 支付宝账户登录授权业务：入参pid值，在账号管理页面*/
    public static final String PID = "xxx";
    /** 支付宝账户登录授权业务：入参target_id值 */ //这个target_id参数是商户自定义的，不需要获取。这个自己随便写，最好能唯一标识。
    public static final String TARGET_ID = "test123";
    public static final String RSA2_PRIVATE = "xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAliLogin();
            }
        });
    }

    private void testAliLogin(){
        Map<String, String> authInfoMap = OrderInfoUtil.buildAuthInfoMap(PID, APPID, TARGET_ID);
        final String authInfo = OrderInfoUtil.buildOrderParam(authInfoMap) + "&" + OrderInfoUtil.getSign(authInfoMap, RSA2_PRIVATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(MainActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    //返回数据类型，authResult=resultStatus={9000};memo={处理成功};result={success=true&result_code=200&app_id=xxx&auth_code=xxx&scope=kuaijie&alipay_open_id=xxx&user_id=xxx&target_id=test123}
                    Log.e("taggg", authResult + "");

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(context,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
                        Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(authResult.getResultCode(), "6001")){
                        Toast.makeText(MainActivity.this, "中断授权操作", Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

}
