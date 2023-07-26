package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import edu.hitsz.R;
import edu.hitsz.basic.ClientThread;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ClientThread clientThread;
    private Handler handler;
    private CharSequence username;
    private CharSequence password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //此handler用于接收消息
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                // 来自服务器端的消息
                if (msg.what == 0x111) {
                    // 关于注册操作
                    if (msg.arg1 == 200) {
                        if (msg.obj.equals(1)) {
                            Toast.makeText(LoginActivity.this,"注册成功!",Toast.LENGTH_SHORT).show();
                            //进入模式选择界面
                            Intent intent = new Intent(LoginActivity.this, ModeActivity.class);
                            intent.putExtra("username", username.toString());
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"账号已存在，请登录",Toast.LENGTH_SHORT).show();
                        }
                    }
                    // 关于登录操作
                    else if (msg.arg1 == 201) {
                        if (msg.obj.equals(1)) {
                            Toast.makeText(LoginActivity.this,"登录成功!",Toast.LENGTH_SHORT).show();
                            //进入模式选择界面
                            Intent intent = new Intent(LoginActivity.this, ModeActivity.class);
                            intent.putExtra("username", username.toString());
                            startActivity(intent);
                        }
                        else if (msg.arg2 == 0) {
                            Toast.makeText(LoginActivity.this,"账号不存在，请先注册",Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == 1) {
                            Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();

        Button signUp_btn = findViewById(R.id.signUp);
        Button signIn_btn = findViewById(R.id.signIn);
        EditText etUsername = findViewById(R.id.inputUserNameText);
        EditText etPassword = findViewById(R.id.inputPasswordText);

        signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //登录操作
                try {
                    Message msg = new Message();
                    msg.what = 0x201;
                    msg.obj = username+" "+password;
                    clientThread.toserverHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注册操作
                try {
                    Message msg = new Message();
                    msg.what = 0x200;
                    msg.obj = username+" "+password;
                    clientThread.toserverHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
