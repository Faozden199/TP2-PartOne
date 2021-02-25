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

    //AUTHENTICATE BUTTON TRIGGER
    public void send(View view) {
        EditText inputName = (EditText) findViewById(R.id.inputName);
        EditText inputPass = (EditText) findViewById(R.id.inputPass);

        //Launch new thread on our authentication service
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                String credential = "";
                try {
                    url = new URL(" https://httpbin.org/basic-auth/bob/sympa");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    StringBuilder builder = new StringBuilder();
                    builder.append(inputName.getText().toString()).append(":").append(inputPass.getText().toString());

                    //Add the Authentification parameter from our two form input
                    String basicAuth = "Basic " + Base64.encodeToString(builder.toString().getBytes(), Base64.NO_WRAP);
                    urlConnection.setRequestProperty("Authorization", basicAuth);
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        JSONObject s = readStream(in);
                        credential = "Authenticated : " + s.getString("authenticated") + " ; " + "User : " + s.getString("user");
                        Log.i("JFL", credential);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String finalCredential = credential;

                // Modify our main view with a runOnUiThread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView name = (TextView) findViewById(R.id.credentialsLabel);
                        if (finalCredential.length() == 0) {
                            Log.i("cred", "Unable to connect");
                            name.setText(new String("Unable to connect"));
                        } else {
                            name.setText(finalCredential);
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private JSONObject readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return new JSONObject(bo.toString());
        } catch (IOException | JSONException e) {
            return new JSONObject();
        }
    }
}