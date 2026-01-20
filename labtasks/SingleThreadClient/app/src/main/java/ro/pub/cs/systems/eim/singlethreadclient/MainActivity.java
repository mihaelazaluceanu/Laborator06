package ro.pub.cs.systems.eim.singlethreadclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import ro.pub.cs.systems.eim.singlethreadclient.general.*;

public class MainActivity extends AppCompatActivity {
    private Handler mainHandler;
    Button button;
    TextView text;
    private class TestThread extends Thread {
        @Override
        public void run() {
            String serverResponse = null;
            Socket socket = null;
            try {
                socket = new Socket("localhost", 2017);
                BufferedReader bufferedReader = Utilities.getReader(socket);
                serverResponse = bufferedReader.readLine();
                Log.d(Constants.TAG, "The server returned: " + serverResponse);
            } catch (IOException ioException) {
                Log.d(Constants.TAG, Objects.requireNonNull(ioException.getMessage()));
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    Log.d(Constants.TAG, Objects.requireNonNull(e.getMessage()));
                }
            }

            // Send result back to UI thread
            Message message = mainHandler.obtainMessage();
            message.obj = serverResponse;
            mainHandler.sendMessage(message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        text = findViewById(R.id.text);

        // init Handler for main thread (UI)
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // this method runs on main thread
                String timeInfo = (String) msg.obj;
                if (timeInfo != null) {
                    text.setText(timeInfo);
                } else {
                    text.setText("No data are received.");
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestThread().start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacksAndMessages(null);
    }
}
