package edu.hitsz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.audio.AudioManager;
import edu.hitsz.basic.ClientThread;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ClientThread clientThread;
    private Handler handler;
    private String username;
    private String mode;

    public static int screenWidth;
    public static int screenHeight;

    private int gameType=0;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化Activity的基本组件
        super.onCreate(savedInstanceState);
        //初始化时加载布局文件
        setContentView(R.layout.activity_main);
        //获取intent中的信息
        username = getIntent().getStringExtra("username");
        mode = getIntent().getStringExtra("mode");
        //初始化接收并处理消息的handler
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                if (msg.what == 0x111) {
                    if (msg.obj.equals(1)) {
                        //得到服务器许可，进入下一个UI
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra("gameType",gameType);
                        intent.putExtra("username", username);
                        intent.putExtra("mode", mode);
                        startActivity(intent);
                    }
                    else if(msg.obj.equals(0)) {
                        //匹配失败，通知用户
                        Toast.makeText(MainActivity.this,"匹配失败！\n请稍等或重新选择难度",Toast.LENGTH_SHORT).show();
                        System.out.println("匹配失败！ \n请稍等或重新选择难度");
                    }
                }
            }
        };
        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();

        Button medium_btn = findViewById(R.id.medium_btn);
        Button easy_btn = findViewById(R.id.easy_btn);
        Button hard_btn = findViewById(R.id.hard_btn);
        Switch audioSwitch = findViewById(R.id.switch2);

        getScreenHW();


        medium_btn.setOnClickListener(view -> {
            gameType=1;
            //给服务器发送消息
            try {
                Message msg = new Message();
                msg.what = 0x221;
                encode(msg);
                clientThread.toserverHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        easy_btn.setOnClickListener(view -> {
            gameType =2;
            //给服务器发送消息
            try {
                Message msg = new Message();
                msg.what = 0x222;
                encode(msg);
                clientThread.toserverHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        hard_btn.setOnClickListener(view -> {
            gameType =3;
            //给服务器发送消息
            try {
                Message msg = new Message();
                msg.what = 0x223;
                encode(msg);
                clientThread.toserverHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        audioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(audioSwitch.isChecked()) {
                    AudioManager.setInMusic(true);
                } else {
                    AudioManager.setInMusic(false);
                }
            }
        });
    }
    public void getScreenHW(){
        //定义DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //窗口的宽度
        screenWidth= dm.widthPixels;
        //窗口高度
        screenHeight = dm.heightPixels;

        Log.i(TAG, "screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void encode(Message msg) {
        msg.obj = username;
        if (mode.equals("single")) {
            msg.arg1 = 1;
        }
        else if (mode.equals("double")) {
            msg.arg1 = 2;
        }
    }
}