package com.example.weatherfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView city;
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (TextView) findViewById(R.id.cityView);
        resultTextView = (TextView) findViewById(R.id.informationView);

    }

    public void weather(View view) throws UnsupportedEncodingException {
try{
        DownloadInfo task = new DownloadInfo();
        String encodedCityName = URLEncoder.encode(city.getText().toString(), "UTF-8");
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=09227b3783e367c80adf6943768e78e7");

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);
    }catch (Exception e) {
    e.printStackTrace();
    Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
}
    }
        public class DownloadInfo extends AsyncTask<String,Void,String>{

            @Override
            protected String doInBackground(String... urls) {

                String result = "";
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();


                    while (data != -1){
                        char current = (char) data;
                        result += current;

                        data = reader.read();
                    }
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return  null;
                }

            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String weatherInfo = jsonObject.getString("weather");

                    Log.i("Weather :", weatherInfo);
                    JSONArray array = new JSONArray(weatherInfo);
                    String message = "";

                    for(int i =0;i<array.length();i++){
                        JSONObject jsonPart = array.getJSONObject(i);

                        String main =jsonPart.getString("main");
                        String description = jsonPart.getString("description");

                        if(!main.equals("") && !description.equals("")){
                            message += main + ":" + description + "\r\n";
                        }
                    }

                    String message1 = "";
                    JSONObject result = jsonObject.getJSONObject("main");
                    String temp = result.getString("temp");
                    String humidity = result.getString("humidity");

                    float i = (int) (Float.parseFloat(temp) - 273.15);
                    temp = String.valueOf(i);

                    if(!temp.equals("") && !humidity.equals("")){
                        message1 += "Temperature :" + temp + "°C" + "\r\n" + "Humidity :" + humidity + "\r\n";
                    }

                    String messFinal = message + message1;

                    if(!messFinal.equals("")){
                        resultTextView.setText(messFinal);
                    }else{
                        Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }


}
