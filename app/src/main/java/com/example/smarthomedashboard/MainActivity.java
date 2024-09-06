package com.example.smarthomedashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthomedashboard.adapter.MainViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.smarthomedashboard.mqtt.MQTTHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    //public static String AIO_key = "aio_UinG05gAytsbGdi4mvZw2mgQJsGl";
    public static String AIO_key = "aio_OcUq99IvIe55uNA8OM0SCbUaI5vP";

    // Declare
    private BottomNavigationView main_bottom_navigation;
    private ViewPager main_view_pager;
    TextView tempView, humidView, gasView;
    ProgressBar tempProgress, humidProgress, gasProgress;
    MQTTHelper mqttHelper;

    int tempLimit = 0;
    JSONArray livingRoomLightStatus, bedRoomLightStatus, diningRoomLightStatus;
    JSONArray livingRoomAirStatus, bedRoomAirStatus, diningRoomAirStatus;
    public DBHandler dbHandler;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private static MainActivity instance;
    public FirebaseFirestore db;
    public CollectionReference usersRef;
    @Override
    protected void onCreate  (Bundle savedInstanceState) throws SQLException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;
        // Get view
        main_bottom_navigation = findViewById(R.id.main_bottom_navigation);
        main_view_pager = findViewById(R.id.main_view_pager);
        dbHandler = new DBHandler(MainActivity.this);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        db=FirebaseFirestore.getInstance();
        usersRef = db.collection("test");
        myRef.setValue("Hello, World!");

        //FirebaseApp.initializeApp(this);
        //ConnectMySql connectMySql = new ConnectMySql();
        //connectMySql.execute();
        /*.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        final View touchView = findViewById(R.id.main_view_pager);
        touchView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });*/
        // Set up
        setUpBottomNavigation();
        setUpViewPager();

        //sendMQTT();
    }
    public static MainActivity getInstance() {
        return instance;
    }
    public void createHero(Float temp, Float humid) {
        /*String name = editTextName.getText().toString().trim();
        String realname = editTextRealname.getText().toString().trim();

        int rating = (int) ratingBar.getRating();

        String team = spinnerTeam.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(realname)) {
            editTextRealname.setError("Please enter real name");
            editTextRealname.requestFocus();
            return;
        }*/

        HashMap<String, String> params = new HashMap<>();
        params.put("temp", Float.toString(temp));
        params.put("humid", Float.toString(humid));
        //.put("rating", String.valueOf(rating));
        //params.put("teamaffiliation", team);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_HERO, params, CODE_POST_REQUEST);
        request.execute();
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            /*super.onPostExecute(s);

            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshing the herolist after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    //refreshHeroList(object.getJSONArray("heroes"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpBottomNavigation() {
        main_bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        main_view_pager.setCurrentItem(0);
                        break;
                    case R.id.action_chart:
                        main_view_pager.setCurrentItem(1);
                        break;
                    case R.id.action_setting:
                        main_view_pager.setCurrentItem(2);
                        break;
                    /*case R.id.action_camera:
                        main_view_pager.setCurrentItem(3);
                        break;
                    case R.id.action_voice:
                        main_view_pager.setCurrentItem(4);
                        break;*/
                }
                return true;
            }
        });
    }

    private void setUpViewPager() {
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        main_view_pager.setAdapter(mainViewPagerAdapter);
        main_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        main_bottom_navigation.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        main_bottom_navigation.getMenu().findItem(R.id.action_chart).setChecked(true);
                        break;
                    case 2:
                        main_bottom_navigation.getMenu().findItem(R.id.action_setting).setChecked(true);
                        break;
                    /*case 3:
                        main_bottom_navigation.getMenu().findItem(R.id.action_camera).setChecked(true);
                        break;
                    case 4:
                        main_bottom_navigation.getMenu().findItem(R.id.action_voice).setChecked(true);
                        break;*/
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
    }


    /*private void sendMQTT() {
        mqttHelper = new MQTTHelper(getApplicationContext(), "123");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("Mqtt", "Connect successfully");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONObject jsonObject = new JSONObject(message.toString());
                if (topic.contains("homeinfo")) {
                    int temp = jsonObject.getInt("temp");
                    int humid = jsonObject.getInt("humidity");
                    int gas = jsonObject.getInt("gas");

                    handleExceedLimit(temp);

                    int[] tempViewList = {R.id.tempText_1, R.id.tempText_2, R.id.tempText_3};
                    int[] humidViewList = {R.id.humidText_1, R.id.humidText_2, R.id.humidText_3};
                    //int[] gasViewList = {R.id.gasText_1, R.id.gasText_2, R.id.gasText_3};

                    int[] tempProgressList = {R.id.tempProgressBar_1, R.id.tempProgressBar_2, R.id.tempProgressBar_3};
                    int[] humidProgressList = {R.id.humidProgressBar_1, R.id.humidProgressBar_2, R.id.humidProgressBar_3};
                    //int[] gasProgressList = {R.id.gasProgressBar_1, R.id.gasProgressBar_2, R.id.gasProgressBar_3};

                    for (int i = 0; i < 3; i++) {
                        tempView = findViewById(tempViewList[i]);
                        tempView.setText(Integer.toString(temp).concat("°C"));

                        humidView = findViewById(humidViewList[i]);
                        humidView.setText(Integer.toString(humid).concat("%"));

                        //gasView = findViewById(gasViewList[i]);
                        gasView.setText(Integer.toString(gas).concat("%"));

                        tempProgress = findViewById(tempProgressList[i]);
                        ObjectAnimator.ofInt(tempProgress, "progress", temp).setDuration(300).start();

                        humidProgress = findViewById(humidProgressList[i]);
                        ObjectAnimator.ofInt(humidProgress, "progress", humid).setDuration(300).start();

                        //gasProgress = findViewById(gasProgressList[i]);
                        ObjectAnimator.ofInt(gasProgress, "progress", gas).setDuration(300).start();
                    }

                } else if (topic.contains("livingroom")) {
                    int[] livingRoomLightSwitchList = {
                            R.id.living_room_switch_light_1,
                            R.id.living_room_switch_light_2,
                            R.id.living_room_switch_light_3,
                            R.id.living_room_switch_light_4
                    };
                    //int[] livingRoomAirSwitchList = {R.id.living_room_switch_air_conditioner_1, R.id.living_room_switch_air_conditioner_2};

                    JSONArray lightResponse = jsonObject.getJSONArray("light");
                    JSONArray airResponse = jsonObject.getJSONArray("air");

                    for (int i = 0; i < lightResponse.length(); i++) {
                        SwitchCompat switchCompat = findViewById(livingRoomLightSwitchList[i]);
                        switchCompat.setChecked(lightResponse.getString(i).equals("1"));
                    }

                    for (int i = 0; i < airResponse.length(); i++) {
                        SwitchCompat switchCompat = findViewById(livingRoomAirSwitchList[i]);
                        switchCompat.setChecked(airResponse.getString(i).equals("1"));
                    }
                } else if (topic.contains("bedroom")) {
                    int[] bedRoomLightSwitchList = {
                            R.id.bed_room_switch_light_1,
                            R.id.bed_room_switch_light_2,
                            R.id.bed_room_switch_light_3,
                            R.id.bed_room_switch_light_4
                    };
                    int[] bedRoomAirSwitchList = {R.id.bed_room_switch_air_conditioner_1, R.id.bed_room_switch_air_conditioner_2};

                    JSONArray lightResponse = jsonObject.getJSONArray("light");
                    JSONArray airResponse = jsonObject.getJSONArray("air");

                    for (int i = 0; i < lightResponse.length(); i++) {
                        SwitchCompat switchCompat = findViewById(bedRoomLightSwitchList[i]);
                        switchCompat.setChecked(lightResponse.getString(i).equals("1"));
                    }

                    for (int i = 0; i < airResponse.length(); i++) {
                        SwitchCompat switchCompat = findViewById(bedRoomAirSwitchList[i]);
                        switchCompat.setChecked(airResponse.getString(i).equals("1"));
                    }
                } else if (topic.contains("diningroom")) {
                    int[] diningRoomLightSwitchList = {
                            R.id.dining_room_switch_light_1,
                            R.id.dining_room_switch_light_2,
                            R.id.dining_room_switch_light_3,
                            R.id.dining_room_switch_light_4
                    };
                    int[] diningRoomAirSwitchList = {R.id.dining_room_switch_air_conditioner_1, R.id.dining_room_switch_air_conditioner_2};

                    JSONArray lightResponse = jsonObject.getJSONArray("light");
                    JSONArray airResponse = jsonObject.getJSONArray("air");

                    for (int i = 0; i < lightResponse.length(); i++) {
                        SwitchCompat switchCompat = findViewById(diningRoomLightSwitchList[i]);
                        switchCompat.setChecked(lightResponse.getString(i).equals("1"));
                    }

                    for (int i = 0; i < airResponse.length(); i++) {
                        SwitchCompat switchCompat = findViewById(diningRoomAirSwitchList[i]);
                        switchCompat.setChecked(airResponse.getString(i).equals("1"));
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }*/

    public void updateLimit(int limit) {
        tempLimit = limit;
    }

    public void updateLivingRoomStatus(JSONArray light, JSONArray air) {
        livingRoomLightStatus = light;
        livingRoomAirStatus = air;
    }

    public void updateBedRoomStatus(JSONArray light, JSONArray air) {
        bedRoomLightStatus = light;
        bedRoomAirStatus = air;
    }

    public void updateDiningRoomStatus(JSONArray light, JSONArray air) {
        diningRoomLightStatus = light;
        diningRoomAirStatus = air;
    }

    public void handleExceedLimit(int temp) throws JSONException {
        if (tempLimit != 0 && temp > tempLimit) {
            JSONObject livingRoomData = new JSONObject();
            JSONObject bedRoomData = new JSONObject();
            if (temp < 39) {
                livingRoomAirStatus.put(0, 1);
                bedRoomAirStatus.put(0, 1);
            } else if (temp >= 39) {
                livingRoomAirStatus.put(0, 1);
                livingRoomAirStatus.put(1, 1);

                bedRoomAirStatus.put(0, 1);
                bedRoomAirStatus.put(1, 1);
//
//                diningRoomAirStatus.put(0, 1);
//                diningRoomAirStatus.put(1, 1);
            }
            livingRoomData.put("light", livingRoomLightStatus);
            livingRoomData.put("air", livingRoomAirStatus);
            sendDataMQTT(livingRoomData, "livingroom");

            bedRoomData.put("light", bedRoomLightStatus);
            bedRoomData.put("air", bedRoomAirStatus);
            sendDataMQTT(bedRoomData, "bedroom");
//
//            diningRoomData.put("light", diningRoomLightStatus);
//            diningRoomData.put("air", diningRoomAirStatus);
//            sendDataMQTT(diningRoomData, "diningroom");
        }
    }

    public void sendDataMQTT(JSONObject data, String topic) {
        String clientID = Integer.toString((int)Math.random() * 1000);
        MqttMessage message = new MqttMessage();
        message.setId(Integer.parseInt(clientID));
        message.setQos(0);
        message.setRetained(true);
        byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);
        message.setPayload(bytes);
        Log.d("publish", "Publish:" + message);
        try {
            mqttHelper.mqttAndroidClient.publish("baokhanhle123/feeds/" + topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToLivingRoom(boolean isOn)  {
        JSONObject livingRoomData = new JSONObject();
        try{
            for (int i = 0; i < 4; i++) {
                if (isOn){
                    livingRoomLightStatus.put(i, 1);
                }else{
                    livingRoomLightStatus.put(i, 0);
                }
            }
            livingRoomData.put("light", livingRoomLightStatus);
            livingRoomData.put("air", livingRoomAirStatus);
            sendDataMQTT(livingRoomData, "livingroom");
        }catch(Exception e){

        }
    }

    public void sendDataToBedRoom(boolean isOn)  {
        JSONObject bedRoomData = new JSONObject();
        try{
            for (int i = 0; i < 4; i++) {
                if (isOn){
                    bedRoomLightStatus.put(i, 1);
                }else{
                    bedRoomLightStatus.put(i, 0);
                }
            }
            bedRoomData.put("light", bedRoomLightStatus);
            bedRoomData.put("air", bedRoomAirStatus);
            sendDataMQTT(bedRoomData, "bedroom");
        }catch(Exception e){

        }
    }

}
