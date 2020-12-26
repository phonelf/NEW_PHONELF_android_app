package me.andythebreaker.phonelf_commu_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //網路部分宣告
    private static final int PORT = 9999;
    private List<Socket> mList = new ArrayList<Socket>();
    private volatile ServerSocket server = null;
    private ExecutorService mExecutorService = null; //線程
    private String hostip; //本機IP
    private volatile boolean flag = true;//线程标志位
    private Handler handler = new Handler();//andythebreaker 原本 no static   OKOKOK
    String s;

    //宣告GUI物件
    private TextView mText1;
    private TextView mText2;
    private TextView abtext;
    private Button mBut1 = null;
    //private Button abbton = null;
    private Handler myHandler = null;
    //private EditText userInputRunFile_obj = null;
    //private Switch SNsw = null;

    //第二區
    private TextView pt2tx1;
    private TextView pt2tx2;
    private TextView pt2tx3;
    private Button pt2bt = null;
    private Handler pt2hd = null;
    private EditText p2e1 = null;
    private EditText p2e2 = null;
    private EditText p2e3 = null;
    private EditText p2e4 = null;
    private EditText p2e5 = null;
    private Button btl = null;
    private Button btu = null;
    private Button btd = null;
    private Button btr = null;
    private Button rst1500 = null;


    //除錯區域宣告
    private static final int use_new_add_on_mode_Recommended_set_to_Yes_to_avoid_errors = 1;
    private static final int DEBUG_LOG_TEXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        //switch
        //SNsw = (Switch) findViewById(R.id.switch1);
        //button
        //abbton = (Button) findViewById(R.id.but1_re);
        mBut1 = (Button) findViewById(R.id.but2_re);
        //text_view
        mText1 = (TextView) findViewById(R.id.tx1_re);
        abtext = (TextView) findViewById(R.id.tx2_re);
        mText2 = (TextView) findViewById(R.id.tx3_re);
        //edit_text
        //userInputRunFile_obj = (EditText) findViewById(R.id.edt_re);

        //第二區
        pt2bt = (Button) findViewById(R.id.p2b);
        pt2tx1 = (TextView) findViewById(R.id.p2t1);
        pt2tx2 = (TextView) findViewById(R.id.p2t2);
        pt2tx3 = (TextView) findViewById(R.id.p2t3);
        //~
        pt2tx1.setText(hostip);
        pt2tx1.setEnabled(false);
        pt2tx2.setText("abtext");
        pt2tx2.setEnabled(true);
        pt2bt.setOnClickListener(new pt2btClickListener());
        //~
        p2e1 = (EditText) findViewById(R.id.mg1);
        p2e2 = (EditText) findViewById(R.id.mg2);
        p2e3 = (EditText) findViewById(R.id.mg3);
        p2e4 = (EditText) findViewById(R.id.mg4);
        p2e5 = (EditText) findViewById(R.id.mg5);
        //~
        btl = (Button) findViewById(R.id.mgleft);
        btr = (Button) findViewById(R.id.mgright);
        btu = (Button) findViewById(R.id.mgup);
        btd = (Button) findViewById(R.id.mgdown);
        btl.setOnClickListener(new btlClickListener());
        btr.setOnClickListener(new btrClickListener());
        btu.setOnClickListener(new btuClickListener());
        btd.setOnClickListener(new btdClickListener());
        rst1500 = (Button) findViewById(R.id.btrst);
        rst1500.setOnClickListener(new rst1500ClickListener());


        //GUI動作宣告
        mText1.setText(hostip);
        mText1.setEnabled(false);
        abtext.setText("abtext");
        abtext.setEnabled(true);
        //abbton.setOnClickListener(new abbtonClickListener());
        mBut1.setOnClickListener(new Button1ClickListener());

        hostip = getLocalIpAddress();  //獲取本機IP

        //取得非UI線程傳來的msg，以改變介面
        myHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                //把網路傳來的文字show到GUI上
                if (msg.what == 0x1234) {
                    //為了不要超過行數，直接用取代的
                    //mText2.append("\n" + msg.obj.toString());
                    mText2.setText("\n" + msg.obj.toString());
                    /*
                    TODO:
                    換成卷軸式的顯示文字
                     */
                }
            }
        };
        pt2hd = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                //把網路傳來的文字show到GUI上
                if (msg.what == 0x1234) {
                    //為了不要超過行數，直接用取代的
                    //mText2.append("\n" + msg.obj.toString());
                    pt2tx3.setText("\n" + msg.obj.toString());
                    /*
                    TODO:
                    換成卷軸式的顯示文字
                     */
                }
            }
        };
    }

    private final class btlClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            p2e4.setText(Integer.toString(Integer.valueOf(p2e4.getText().toString()) - 4));

        }
    }

    private final class btrClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            p2e4.setText(Integer.toString(Integer.valueOf(p2e4.getText().toString()) + 4));

        }
    }

    private final class btuClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            p2e3.setText(Integer.toString(Integer.valueOf(p2e3.getText().toString()) - 4));

        }
    }

    private final class btdClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            p2e3.setText(Integer.toString(Integer.valueOf(p2e3.getText().toString()) + 4));
        }
    }

    private final class rst1500ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//p2e2.setText("1500");
            char default_value_1500[]={'1','5','0','0'};
            p2e3.setText(default_value_1500,0,4);
            p2e4.setText(default_value_1500,0,4);
            p2e5.setText(default_value_1500,0,4);
        }
    }



    //對button1的監聽事件
    private final class Button1ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast toast22 = Toast.makeText(MainActivity.this, mBut1.getText().toString(), Toast.LENGTH_LONG);
            toast22.show();
            /** @brief 網路主功能

             * 需求:
             * TBD

             * @return void */
            // TODO Auto-generated method stub
            //如果是「啟動」，證明服務器是關閉狀態，可以開啟服務器
            if (mBut1.getText().toString().equals("啟動")) {
                ServerThread serverThread = new ServerThread(PORT);
                flag = true;

                serverThread.start();
                mBut1.setText("關閉");
                mText1.setText(getLocalIpAddress());
                //show IP address
                Toast toast = Toast.makeText(MainActivity.this, "IP address: " + getLocalIpAddress(), Toast.LENGTH_LONG);
                toast.show();
            } else {
                try {
                    flag = false;
                    server.close();
                    for (int p = 0; p < mList.size(); p++) {
                        Socket s = mList.get(p);
                        s.close();
                    }
                    mExecutorService.shutdownNow();
                    mBut1.setText("啟動");
                    mText1.setText("Not Connect");
                    Log.v("Socket-status", "服務器已關閉");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private final class pt2btClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (pt2bt.getText().toString().equals("啟動")) {
                ServerThread2 serverThread = new ServerThread2(PORT);
                flag = true;

                serverThread.start();
                pt2bt.setText("關閉");
                pt2tx1.setText(getLocalIpAddress());
                //show IP address
                Toast toast = Toast.makeText(MainActivity.this, "IP address: " + getLocalIpAddress(), Toast.LENGTH_LONG);
                toast.show();
            } else {
                try {
                    flag = false;
                    server.close();
                    for (int p = 0; p < mList.size(); p++) {
                        Socket s = mList.get(p);
                        s.close();
                    }
                    mExecutorService.shutdownNow();
                    pt2bt.setText("啟動");
                    pt2tx1.setText("Not Connect");
                    Log.v("Socket-status", "服務器已關閉");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    //Server端的主線程
    class ServerThread extends Thread {
        public ServerThread(int port_in) {
            port_in = port_r;
        }

        public int port_r = 9999;

        public void run() {
            try {
                server = new ServerSocket(PORT);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mExecutorService = Executors.newCachedThreadPool();  //創建一個線程
            Socket client = null;
            while (flag) {
                try {
                    Log.v("test", String.valueOf(flag));
                    client = server.accept();
                    handler.post(new Runnable() {
                        public void run() {
                            mText1.setText("Connected.");
                        }
                    });
                    try {
                        handler.post(new Runnable() {
                            public void run() {
                                mText2.setText(s);  //post message on the textView
                            }
                        });
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                mText2.setText(s);
                            }
                        });
                    }
                    //把客户端放入客户端集合中
                    mList.add(client);
                    mExecutorService.execute(new Service(client)); //啟動一個新的線程來處理連接
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        public void run() {
                            mText2.setText("disConncet");
                        }
                    });
                }
            }
        }
    }

    class ServerThread2 extends Thread {
        public ServerThread2(int port_in) {
            port_in = port_r;
        }

        public int port_r = 9999;

        public void run() {
            try {
                server = new ServerSocket(PORT);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mExecutorService = Executors.newCachedThreadPool();  //創建一個線程
            Socket client = null;
            while (flag) {
                try {
                    Log.v("test", String.valueOf(flag));
                    client = server.accept();
                    handler.post(new Runnable() {
                        public void run() {
                            pt2tx1.setText("Connected.");
                        }
                    });
                    try {
                        handler.post(new Runnable() {
                            public void run() {
                                pt2tx3.setText(s);  //post message on the textView
                            }
                        });
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                pt2tx3.setText(s);
                            }
                        });
                    }
                    //把客户端放入客户端集合中
                    mList.add(client);
                    mExecutorService.execute(new Service2(client)); //啟動一個新的線程來處理連接
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        public void run() {
                            pt2tx3.setText("disConncet");
                        }
                    });
                }
            }
        }
    }

    //獲取本地IP
    @SuppressLint("LongLogTag")
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    //處理與client對話的線程
    class Service implements Runnable {
        private volatile boolean kk = true;
        private Socket socket;
        private BufferedReader in = null;
        private String msg = "";

        public Service(Socket socket) {
            this.socket = socket;   //reada6
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                msg = "OK";   //reada8
                this.sendmsg(msg);  //reada9
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (kk) {
                try {
                    if ((msg = in.readLine()) != null) {
                        //當客戶端發送的訊息為：exit時，關閉連接
                        if (msg.equals("exit")) {
                            mList.remove(socket);
                            break;
                            //接收客户端發過來的訊息msg，然後發送給客戶端。
                        } else {
                            Message msgLocal = new Message();
                            msgLocal.what = 0x1234;
                            msgLocal.obj = msg + " （From Client）";

                            //if (SNsw.isChecked()) {
                            //pack_write_file_sd(msg + "\n");
                            //} else {
                            //pack_write_file_sd(msg);
                            //}

                            System.out.println(msgLocal.obj.toString());
                            System.out.println(msg);
                            myHandler.sendMessage(msgLocal);
                            msg = socket.getInetAddress() + ":" + msg + "（From Server）";
                            this.sendmsg(msg);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("close");
                    kk = false;
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        //向客戶端發送訊息
        public void sendmsg(String msg) {
            System.out.println(msg);
            PrintWriter pout = null;
            try {
                pout = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                pout.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Service2 implements Runnable {
        private volatile boolean kk = true;
        private Socket socket;
        private BufferedReader in = null;
        private String msg = "";

        public Service2(Socket socket) {
            this.socket = socket;   //reada6
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                msg = "OK";   //reada8
                this.sendmsg(msg);  //reada9
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (kk) {
                try {
                    if ((msg = in.readLine()) != null) {
                        //當客戶端發送的訊息為：exit時，關閉連接
                        if (msg.equals("exit")) {
                            mList.remove(socket);
                            break;
                            //接收客户端發過來的訊息msg，然後發送給客戶端。
                        } else {
                            Message msgLocal = new Message();
                            msgLocal.what = 0x1234;
                            msgLocal.obj = msg + " （From Client）";

                            //if (SNsw.isChecked()) {
                            //pack_write_file_sd(msg + "\n");
                            //} else {
                            //pack_write_file_sd(msg);
                            //}

                            System.out.println(msgLocal.obj.toString());
                            System.out.println(msg);
                            myHandler.sendMessage(msgLocal);
                            msg = "$"+p2e1.getText()+"000000"+"0"+p2e2.getText()+"0"+p2e3.getText()+"0"+p2e4.getText()+"0"+p2e5.getText()+ "~";
                            this.sendmsg(msg);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("close");
                    kk = false;
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        //向客戶端發送訊息
        public void sendmsg(String msg) {
            System.out.println(msg);
            PrintWriter pout = null;
            try {
                pout = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                pout.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
