package edu.hitsz.basic;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import edu.hitsz.database.Score;

public class ClientObjectThread implements Runnable{
    private static final String HOST = "10.0.2.2"; //本机IP
    private static final int PORT = 11451;
    private static final int TIMEOUT = 5000;
    private Socket socket = null;
    private Handler toclientHandler;    //向UI线程发送消息的Handler
    public Handler toserverHandler;     //接收UI线程消息的Handler
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private ObjectInputStream ois;
    private ObjectOutputStream objectOutput = null;

    public ClientObjectThread(Handler myHandler) {this.toclientHandler = myHandler;}

    public void run() {
        Connect();
    }

    private void Connect() {
        try {
            //在此之前，服务器应该已经启动
            Socket socket = new Socket();
            //发出连接请求
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT);
            //在TIMEOUT之前，阻塞等待连接建立

            //初始化序列化输入输出流

            objectOutput = new ObjectOutputStream(socket.getOutputStream());

            //创建子线程接收从服务器发来的消息, 以及向UI发送消息
            new Thread() {
                @Override
                public void run() {
                    try {
                        ArrayList<Score> rankList;
                        ois = new ObjectInputStream(socket.getInputStream());
                        while ((rankList = (ArrayList<Score>) ois.readObject()) != null) {
                            System.out.println("got the rankLIst");
                            Message servermsg = new Message();
                            servermsg.what = 0x140;
                            servermsg.obj = rankList;
                            //向UI发送消息
                            toclientHandler.sendMessage(servermsg);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            //在子线程中初始化一个Looper对象，即为当前线程创建消息队列
            Looper.prepare();
            //根据各UI向ClientThread发送的消息做不同处理
            toserverHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //获取排行榜
                    if (msg.what == 0x240) {
                        if (msg.arg1 == 1) {
                            writer.println("GetRankList MEDIUM");
                        }
                        else if (msg.arg1 == 2) {
                            writer.println("GetRankList EASY");
                        }
                        else if (msg.arg1 == 3) {
                            writer.println("GetRankList HARD");
                        }
                        System.out.println("to get rank list");
                    }
                }
            };
            //启动Looper，运行刚才初始化的Looper对象，循环取消息队列的消息
            Looper.loop();


            //关闭连接
            socket.shutdownOutput();;
            //等待对方关闭

            while (reader.readLine() != null) {
                //处理对方发来的收尾消息
            }
            socket.close(); //关闭socket，至此，通信全部结束


        } catch (SocketTimeoutException el){
            System.out.println("网络连接超时！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encode(String fromServer, Message msg) {
        msg.what = 0x111;
        switch (fromServer) {
            case "Sign in successfully!":
                msg.obj = 1;
                msg.arg1 = 201;
                break;
            case "Account doesn't exist!":
                msg.obj = 0;
                msg.arg1 = 201;
                msg.arg2 = 0;
                break;
            case "Wrong password!":
                msg.obj = 0;
                msg.arg1 = 201;
                msg.arg2 = 1;
                break;
            case "Sign up successfully!":
                msg.obj = 1;
                msg.arg1 = 200;
                break;
            case "Account already existed!":
                msg.obj = 0;
                msg.arg1 = 200;
                msg.arg2 = 1;
                break;
            case "Game Admission":
                msg.obj = 1;
                msg.arg1 = 220;
                break;
            case "Match failed!":
                msg.obj = 0;
                msg.arg1 = 220;
                break;
            case "Rank Admission":
                msg.obj = 1;
                msg.arg1 = 230;
        }
        if (fromServer.contains("Admission")) {
            msg.obj = 1;
        }
    }
}
