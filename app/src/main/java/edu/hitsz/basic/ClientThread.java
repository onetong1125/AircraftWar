package edu.hitsz.basic;

import androidx.dynamicanimation.animation.SpringAnimation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import edu.hitsz.activity.GameActivity;
import edu.hitsz.activity.LoginActivity;
import edu.hitsz.database.Score;

public class ClientThread implements Runnable{

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

    public ClientThread(Handler myHandler) {this.toclientHandler = myHandler;}

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
             //初始化输入输出流
             reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                     socket.getOutputStream(), "UTF-8")), true);
             //初始化序列化输入输出流

             objectOutput = new ObjectOutputStream(socket.getOutputStream());

             //创建子线程接收从服务器发来的消息, 以及向UI发送消息
             new Thread() {
                 @Override
                 public void run(){
                     String fromServer = null;
                     try {
                         while ((fromServer = reader.readLine()) != null) {
                             System.out.println("Server: "+fromServer);
                             Message servermsg = new Message();
                             encode(fromServer, servermsg);
                             //向UI发送消息
                             toclientHandler.sendMessage(servermsg);
                         }

                     } catch (Exception e ) {
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
                     //登录
                     if (msg.what == 0x201) {
                         try {
                             writer.println("SignIn "+msg.obj);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     //注册
                     else if (msg.what == 0x200) {
                         try {
                             writer.println("SignUp "+msg.obj);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     //普通模式
                     else if (msg.what == 0x221) {
                         //mode gametype username
                         try {
                             if (msg.arg1==1) {
                                 writer.println("single medium "+msg.obj);
                             } else if (msg.arg1==2){
                                 writer.println("double medium "+msg.obj);
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     //简单模式
                     else if (msg.what == 0x222) {
                         //mode gametype username
                         try {
                             if (msg.arg1==1) {
                                 writer.println("single easy "+msg.obj);
                             } else if (msg.arg1==2){
                                 writer.println("double easy "+msg.obj);
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     //困难模式
                     else if (msg.what == 0x223) {
                         //mode gametype username
                         try {
                             if (msg.arg1==1) {
                                 writer.println("single hard "+msg.obj);
                             } else if (msg.arg1==2){
                                 writer.println("double hard "+msg.obj);
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     //记录得分
                     else if (msg.what == 0x230) {
                         //gametype username mode score
                         if (msg.arg2 == 1) {
                             writer.println("MEDIUM "+msg.obj+" "+msg.arg1);
                         }
                         else if (msg.arg2 == 2) {
                             writer.println("EASY "+msg.obj+" "+msg.arg1);
                         }
                         else if (msg.arg2 == 3) {
                             writer.println("HARD "+msg.obj+" "+msg.arg1);
                         }

                     }
                     //更新得分和生命
                     else if (msg.what == 0x231) {
                         //UPDATE: username score life
                         writer.println("UPDATE: "+msg.obj+" "+
                                 msg.arg1+" "+
                                 msg.arg2);
                     }
                     //删除得分记录
                     else if (msg.what == 0x241) {
                         //Delete gametype id
                         if (msg.arg2 == 1) {
                             writer.println("Delete MEDIUM "+msg.arg1);
                         }
                         else if (msg.arg2 == 2) {
                             writer.println("Delete EASY "+msg.arg1);
                         }
                         else if (msg.arg2 == 3) {
                             writer.println("Delete HARD "+msg.arg1);
                         }

                     }
                     //获取排行榜
                     else if (msg.what == 0x240) {
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
        String[] words = fromServer.split(" ");
        if ("Sign in successfully!".equals(fromServer)) {
            msg.obj = 1;
            msg.arg1 = 201;
        } else if ("Account doesn't exist!".equals(fromServer)) {
            msg.obj = 0;
            msg.arg1 = 201;
            msg.arg2 = 0;
        } else if ("Wrong password!".equals(fromServer)) {
            msg.obj = 0;
            msg.arg1 = 201;
            msg.arg2 = 1;
        } else if ("Sign up successfully!".equals(fromServer)) {
            msg.obj = 1;
            msg.arg1 = 200;
        } else if ("Account already existed!".equals(fromServer)) {
            msg.obj = 0;
            msg.arg1 = 200;
            msg.arg2 = 1;
        } else if ("Game Admission".equals(fromServer)) {
            msg.obj = 1;
            msg.arg1 = 220;
        } else if ("Match failed!".equals(fromServer)) {
            msg.obj = 0;
            msg.arg1 = 220;
        } else if ("Rank Admission".equals(fromServer)) {
            msg.obj = 1;
            msg.arg1 = 230;
        } else if (fromServer.contains("Admission")) {
            msg.obj = 1;
        } else if (words.length == 6) {
            //得分排行榜
            Score score = new Score(Integer.parseInt(words[1]),
                    words[2],
                    Integer.parseInt(words[3]),
                    words[4]+" "+words[5]);
            msg.obj = score;
            msg.what = 0x140;

        } else if (words[0].contains("UPDATE:")) {
            msg.what = 0x131;
            msg.obj = words[1];
            msg.arg1 = Integer.parseInt(words[2]);
            msg.arg2 = Integer.parseInt(words[3]);
        }
    }
}
