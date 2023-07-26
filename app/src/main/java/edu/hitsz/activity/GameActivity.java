package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.basic.ClientThread;
import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private static int gameType=0;
    private ClientThread clientThread;
    private BaseGame baseGameView;
    private Handler handler;
    private String username;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BaseGame basGameView = null;
        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
            username = getIntent().getStringExtra("username");
            mode = getIntent().getStringExtra("mode");

        }

//        BaseGame finalBasGameView = basGameView;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                //来自GameView的消息--游戏结束
                if (msg.what == 0x230) {
                    Message msg1 = new Message();
                    msg1.what = 0x230;
                    msg1.obj = username+" "+mode;
                    msg1.arg2 = gameType;
                    msg1.arg1 = msg.arg1;
                    clientThread.toserverHandler.sendMessage(msg1);
                    Toast.makeText(GameActivity.this,"GameOver",Toast.LENGTH_SHORT).show();
                }
                //来自GameView的消息--更新自己信息
                if (msg.what == 0x231) {
                    Message msg1 = new Message();
                    msg1.what = 0x231;
                    msg1.arg1 = msg.arg1;
                    msg1.arg2 = msg.arg2;
                    msg1.obj = username;
                    clientThread.toserverHandler.sendMessage(msg1);
                }
                //来自服务器端消息--更新对手信息
                if (msg.what == 0x131) {
                    baseGameView.updateRival((String) msg.obj, msg.arg1, msg.arg2);
                }
                //来自服务器端消息
                if (msg.what == 0x111) {
                    //允许进入下一个UI
                    if (msg.obj.equals(1)) {
                        Intent intent = new Intent(GameActivity.this, RankActivity.class);
                        intent.putExtra("gameType", gameType);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                }
            }
        };
        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();

        if(gameType == 1){
            baseGameView = new MediumGame(this,handler,mode);

        }else if(gameType == 3){
            baseGameView = new HardGame(this,handler,mode);
        }else{
            baseGameView = new EasyGame(this,handler,mode);
        }


        setContentView(baseGameView);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static String getGameType() {
        if(gameType == 1) {
            return "MEDIUM";
        } else if(gameType == 3){
            return "HARD";
        } else {
            return "EASY";
        }
    }


}