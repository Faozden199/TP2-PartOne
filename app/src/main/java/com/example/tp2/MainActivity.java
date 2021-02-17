package com.example.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void send(View view){
        thread.start();
    }

    private Thread thread = new Thread(){
        public void run(){
            URL url = null;
            String credential="";
            try {
                url = new URL(" https://httpbin.org/basic-auth/bob/sympa");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                EditText inputName = (EditText) findViewById(R.id.inputName);
                EditText inputPass = (EditText) findViewById(R.id.inputPass);
                StringBuilder builder = new StringBuilder();
                builder.append(inputName.getText().toString()).append(":").append(inputPass.getText().toString());
                String basicAuth = "Basic " + Base64.encodeToString(builder.toString().getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty ("Authorization", basicAuth);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    JSONObject s = readStream(in);
                    credential = "Authentificated : " + s.getString("authenticated") + " ; " + "User : " + s.getString("user");
                    Log.i("JFL", credential);
                } catch (JSONException e){
                 e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String finalCredential = credential;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView name = (TextView)findViewById(R.id.credentialsLabel);
                    name.setText(finalCredential);
                }
            });
        }
    };

    private JSONObject readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return new JSONObject(bo.toString());
        } catch (IOException | JSONException e) {
            return new JSONObject();
        }
    }
}