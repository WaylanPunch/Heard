package com.way.heard.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.way.heard.R;
import com.way.heard.ui.views.waveview.WaveView;

import java.util.Timer;
import java.util.TimerTask;

public class IndexActivity extends AppCompatActivity {

    private WaveView waveView;
    private static int mProgress;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        initView();
    }

    private void initView() {
        waveView = (WaveView) findViewById(R.id.wave_view);

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        mProgress = 0;
        timer = new Timer(true);
        timer.schedule(task, 500, 30); //延时1000ms后执行，1000ms执行一次
        //timer.cancel(); //退出计时器
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    waveView.setProgress(mProgress);

                    if (mProgress == 100) {
                        timer.cancel();
                        startActivity(new Intent(IndexActivity.this, LoginActivity.class));
                        finish();
                    }

                    mProgress++;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
        finish();
    }
}
