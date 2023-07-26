package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



import edu.hitsz.R;
import edu.hitsz.basic.ClientThread;

public class ModeActivity extends AppCompatActivity {
    private static final String TAG = "ModeActivity";

    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        username = getIntent().getStringExtra("username");


        Button single_btn = findViewById(R.id.button);
        Button double_btn = findViewById(R.id.button2);

        single_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"SINGLE");
                //进入单人模式
                Intent intent = new Intent(ModeActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("mode", "single");
                startActivity(intent);
            }
        });

        double_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"DOUBLE");
                //进入双人模式
                Intent intent = new Intent(ModeActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("mode", "double");
                startActivity(intent);
            }
        });
    }
}
