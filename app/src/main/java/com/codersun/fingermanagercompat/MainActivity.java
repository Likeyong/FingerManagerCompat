package com.codersun.fingermanagercompat;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.codersun.fingerprintcompat.AonFingerChangeCallback;
import com.codersun.fingerprintcompat.FingerManager;
import com.codersun.fingerprintcompat.SimpleFingerCheckCallback;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById(R.id.textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (FingerManager.checkSupport(MainActivity.this)) {
                        case DEVICE_UNSUPPORTED:
                            showToast("您的设备不支持指纹");
                            break;
                        case SUPPORT_WITHOUT_DATA:
                            showToast("请在系统录入指纹后再验证");
                            break;
                        case SUPPORT:
                            FingerManager.build().setApplication(getApplication())
                                    .setTitle("指纹验证")
                                    .setDes("请按下指纹")
                                    .setNegativeText("取消")
                                    .setFingerCheckCallback(new SimpleFingerCheckCallback() {
                                        @Override
                                        public void onSucceed() {
                                            showToast("验证成功");
                                        }

                                        @Override
                                        public void onError(String error) {
                                            showToast("验证失败");
                                        }

                                        @Override
                                        public void onCancel() {
                                            showToast("您取消了识别");
                                        }
                                    })
                                    .setFingerChangeCallback(new AonFingerChangeCallback() {
                                        @Override
                                        protected void onFingerDataChange() {
                                            showToast("指纹数据发生了变化");
                                        }
                                    })
                                    .create()
                                    .startListener(MainActivity.this);
                            break;
                    }
                }
            });


            findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FingerManager.updateFingerData(MainActivity.this);
                }
            });

        }

    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
