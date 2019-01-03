package com.wechat.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_open)
    Button btnOpen;
    @BindView(R.id.btn_wechat)
    Button btnWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_open, R.id.btn_wechat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;
            case R.id.btn_wechat:
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                startActivity(intent);
                break;
        }
    }
}
