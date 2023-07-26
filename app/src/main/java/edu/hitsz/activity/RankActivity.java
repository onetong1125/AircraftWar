package edu.hitsz.activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import edu.hitsz.R;
import edu.hitsz.basic.ClientThread;
import edu.hitsz.database.Score;
import edu.hitsz.database.ScoreDAOImpl;

public class RankActivity extends AppCompatActivity {
    private static final String TAG = "RankActivity";
    private int gameType;
    private String username;
    private ClientThread clientThread;
    private Handler handler;
    private SimpleAdapter adapter;
    private ArrayList<Score> rankList = new ArrayList<>();
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    ScoreDAOImpl sdi = new ScoreDAOImpl();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        gameType = getIntent().getIntExtra("gameType", 1);
        username = getIntent().getStringExtra("username");
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                //获取到rankList
                if (msg.what == 0x140) {
                    Log.i(TAG, "got the rankList");
                    rankList.add((Score)msg.obj);
                    //准备ListView要显示的数据
                    data.clear();
                    setData();
                    adapter.notifyDataSetChanged();
                    System.out.println(data.toString());
                }
            }
        };
        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();
        //构建适配器
        adapter = new SimpleAdapter(RankActivity.this, data, R.layout.list_item,
                new String[]{"rank", "name", "score", "time"},
                new int[]{R.id.rank,R.id.name,R.id.score,R.id.time});

        TextView title = findViewById(R.id.textView);
        title.setText("排行榜："+GameActivity.getGameType());
        Button showRank_btn = findViewById(R.id.button3);
        Button updateRank_btn = findViewById(R.id.button4);
        showRank_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送消息从服务器返回获取排行榜
                Message msg = new Message();
                msg.what = 0x240;
                msg.arg1 = gameType;
                rankList.clear();
                clientThread.toserverHandler.sendMessage(msg);
            }
        });
        updateRank_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从服务器返回最新排行榜
                Message msg = new Message();
                msg.what = 0x240;
                msg.arg1 = gameType;
                rankList.clear();
                clientThread.toserverHandler.sendMessage(msg);
            }
        });
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RankActivity.this);
                builder.setMessage("确定删除？");
                builder.setTitle("提示");

                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!data.get((int) l).get("name").equals(username)) {
                            Toast.makeText(RankActivity.this,"只能删除自己的记录！",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //发送消息让服务器删除数据并返回新的排行榜
                            Message msg = new Message();
                            msg.what = 0x241;
                            msg.arg1 = Integer.parseInt(data.get((int) l).get("id"));
                            msg.arg2 = gameType;
                            rankList.clear();
                            clientThread.toserverHandler.sendMessage(msg);

                            data.clear();
                            setData();

                            //更新排行榜
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return false;
            }
        });
    }

    private void setData(){
        for(int rank=1; rank<=rankList.size(); rank++) {
            Score score = rankList.get(rank-1);
            HashMap<String, String> user = new HashMap<>();
            user.put("id", String.valueOf(score.getId()));
            user.put("rank", String.valueOf(rank));
            user.put("name", score.getName());
            user.put("score", String.valueOf(score.getScore()));
            user.put("time", score.getTime());
            data.add(user);
        }
    }
}
