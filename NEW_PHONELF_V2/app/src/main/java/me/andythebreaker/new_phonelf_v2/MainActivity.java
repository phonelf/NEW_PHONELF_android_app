package me.andythebreaker.new_phonelf_v2;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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
import java.io.FileOutputStream;
import android.os.Environment;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.OutputStream;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.net.Uri;
import java.io.InputStream;
import java.text.DateFormat;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    //網路部分參考資料
    //https://fullmetal6927.pixnet.net/blog/post/204684237-%5Bandroid%5D-%5Bstudio%5D-socket-server%E6%8E%A5%E6%94%B6%E6%B8%AC%E8%A9%A6%E7%A8%8B%E5%BC%8F

    //網路部分宣告
    private static final int PORT = 9999;
    private List<Socket> mList = new ArrayList<Socket>();
    private volatile ServerSocket server = null;
    private ExecutorService mExecutorService = null; //線程
    private String hostip; //本機IP
    private volatile boolean flag = true;//线程标志位
    private Handler handler = new Handler();//andythebreaker 原本 no static   OKOKOK
    String s;
    private Handler myHandler = null;

    //宣告GUI物件
    private TextView ip_stat_obj;
    private Button start_server_bton_obj = null;
    private EditText command_out = null;
    private Button cmd = null;
    private TextView muti_text;
    private Switch motor_auto = null;
    private SeekBar throttle_bar=null;
    private SeekBar pitch_bar=null;
    private SeekBar row_bar=null;
    private SeekBar yaw_bar=null;
    private Button motor_up_obj = null;
    private EditText throttle_num=null;
    private EditText pitch_num=null;
    private EditText row_num=null;
    private EditText yaw_num=null;
    private Button motor_start_obj = null;
    private Button motor_stop_obj = null;
    private TextView motor_stat_obj = null;
    private Switch siri_auto = null;
    private Button siri_trig = null;
    private TextView siri_solu;
    private Button siri_send = null;
    private Button siri_solu_clear = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//?
        setContentView(R.layout.activity_main);

        //GUI動作宣告
        muti_text = (TextView) findViewById(R.id.tcp_msg);
        ip_stat_obj= (TextView) findViewById(R.id.ip_stat);
        start_server_bton_obj = (Button) findViewById(R.id.start_server_bton);
        command_out = (EditText) findViewById(R.id.command_our);
        cmd = (Button) findViewById(R.id.CMD);
        motor_auto = (Switch)findViewById(R.id.motor_auto_up);
        throttle_bar=(SeekBar) findViewById(R.id.set_throttle_bar);
        pitch_bar=(SeekBar) findViewById(R.id.set_pitch_bar);
        row_bar=(SeekBar) findViewById(R.id.set_row_bar);
        yaw_bar=(SeekBar) findViewById(R.id.set_yaw_bar);
        motor_up_obj =  (Button) findViewById(R.id.motor_up);
        throttle_num=(EditText) findViewById(R.id.set_throttle_num);
        pitch_num=(EditText) findViewById(R.id.set_pitch_num);
        row_num=(EditText) findViewById(R.id.set_row_num);
        yaw_num=(EditText) findViewById(R.id.set_yaw_num);
        motor_start_obj = (Button) findViewById(R.id.motor_start);
        motor_stop_obj = (Button) findViewById(R.id.motor_stop);
        motor_stat_obj = (TextView) findViewById(R.id.motor_stat);
        siri_auto = (Switch)findViewById(R.id.speech_auto_up);
        siri_trig = (Button) findViewById(R.id.speech_main);
        siri_solu=(TextView) findViewById(R.id.Identify);
        siri_send =  (Button) findViewById(R.id.speech_send);
        siri_solu_clear = (Button) findViewById(R.id.identify_clear);

        hostip = getLocalIpAddress();  //獲取本機IP

        //取得非UI線程傳來的msg，以改變介面
        myHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                //把網路傳來的文字show到GUI上
                if (msg.what == 0x1234) {
                    muti_text.append("\n" + msg.obj.toString());
                    /*
                    TODO:
                    換成卷軸式的顯示文字
                     */
                }
            }
        };
    }
    //對button1的監聽事件
    private final class start_server_bton_objClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /** @brief 網路主功能

             * 需求:
             * TBD

             * @return void */
            // TODO Auto-generated method stub
            //如果是「啟動」，證明服務器是關閉狀態，可以開啟服務器
            if (start_server_bton_obj.getText().toString().equals("START")) {
                ServerThread serverThread = new ServerThread();
                flag = true;
                serverThread.start();
                start_server_bton_obj.setText("STOP");
                ip_stat_obj.setText(getLocalIpAddress());
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
                    start_server_bton_obj.setText("START");
                    ip_stat_obj.setText("Not Connect");
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
                            ip_stat_obj.setText("Connected.");
                        }
                    });
                    try {
                        handler.post(new Runnable() {
                            public void run() {
                                muti_text.append("\n" +s);  //post message on the textView
                            }
                        });
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                muti_text.append("\n" +s);
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
                            ip_stat_obj.setText("disConncet");
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

                            /*if (SNsw.isChecked()) {
                                pack_write_file_sd(msg + "\n");
                            } else {
                                pack_write_file_sd(msg);
                            }*/

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
}