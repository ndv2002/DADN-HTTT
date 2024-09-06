package com.example.smarthomedashboard.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthomedashboard.MainActivity;
import com.example.smarthomedashboard.R;
import com.example.smarthomedashboard.mqtt.MQTTHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import android.os.Bundle;
import android.os.Handler;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//implements View.OnClickListener,CompoundButton.OnCheckedChangeListener
public class LivingRoomFragment extends Fragment {

    // Declare
    CardView living_room_light, living_room_air_conditioner;
    ConstraintLayout roomSetting, lightSetting, airConditionerSetting;
    ImageButton btn_light_back, btn_air_conditioner_back;
    TextView tempView, humidView, gasView;
    ProgressBar tempProgress, humidProgress, gasProgress;
    JSONArray light;
    JSONArray airConditioner;
    MQTTHelper mqttHelper;
    String clientID = Integer.toString((int)Math.random() * 1000);
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    private static final String TAG = "LivingRoomFragment";
    SwitchCompat living_room_switch_light_1,living_room_switch_air_conditioner_1;
    static Boolean isLightTouched = false,isFanTouched = false;
    public LivingRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_living_room, container, false);

        // Match view
        living_room_light = view.findViewById(R.id.living_room_light);
        living_room_air_conditioner = view.findViewById(R.id.living_room_air_conditioner);

        roomSetting = view.findViewById(R.id.living_room_setting_container);
        lightSetting = view.findViewById(R.id.living_room_light_container);
        airConditionerSetting = view.findViewById(R.id.living_room_air_conditioner_container);

        btn_light_back = view.findViewById(R.id.living_room_btn_light_back);
        btn_air_conditioner_back = view.findViewById(R.id.living_room_btn_air_conditioner_back);


        //Call
        setUpLivingRoomLightButton(view);
        setUpLivingRoomAirConditionerButton(view);
        setUpBtnLightBack(view);
        setUpBtnAirConditionerBack(view);

        /*SwitchCompat switchCompat_1 = (SwitchCompat) view.findViewById(R.id.living_room_switch_light_1);
        switchCompat_1.setOnClickListener(this);
        SwitchCompat switchCompat_2 = (SwitchCompat) view.findViewById(R.id.living_room_switch_light_2);
        switchCompat_2.setOnClickListener(this);
        SwitchCompat switchCompat_3 = (SwitchCompat) view.findViewById(R.id.living_room_switch_light_3);
        switchCompat_3.setOnClickListener(this);
        SwitchCompat switchCompat_4 = (SwitchCompat) view.findViewById(R.id.living_room_switch_light_4);
        switchCompat_4.setOnClickListener(this);
        SwitchCompat switchCompat_5 = (SwitchCompat) view.findViewById(R.id.living_room_switch_air_conditioner_1);
        switchCompat_5.setOnClickListener(this);
        SwitchCompat switchCompat_6 = (SwitchCompat) view.findViewById(R.id.living_room_switch_air_conditioner_2);
        switchCompat_6.setOnClickListener(this);


        mqttHelper = new MQTTHelper(view.getContext(), clientID);*/

        living_room_switch_light_1 = (SwitchCompat) view.findViewById(R.id.living_room_switch_light_1);
        living_room_switch_air_conditioner_1 = (SwitchCompat) view.findViewById(R.id.living_room_switch_air_conditioner_1);
        startMQTT_new();
        /*living_room_switch_light_1.setTag("switch1");
        living_room_switch_air_conditioner_1.setTag("switch2");

        living_room_switch_light_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do something when the switch is toggled
                if (buttonView.getTag().equals("switch1")) {
                    // Code to execute when switch1 is clicked
                    if (isChecked == true) {
                        sendDataMQTT("dlhcmut/feeds/bbc-led", "1");

                    } else {
                        sendDataMQTT("dlhcmut/feeds/bbc-led", "0");

                    }
                }

            }
        });


        living_room_switch_air_conditioner_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do something when the switch is toggled
                if (buttonView.getTag().equals("switch2")) {
                    // Code to execute when switch1 is clicked
                    if (isChecked == true) {
                        sendDataMQTT("dlhcmut/feeds/bbc-fan", "2");

                    } else {
                        sendDataMQTT("dlhcmut/feeds/bbc-fan", "3");

                    }
                }

            }
        });*/


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        living_room_switch_light_1.setTag("switch1");
        living_room_switch_air_conditioner_1.setTag("switch2");

        living_room_switch_light_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do something when the switch is toggled
                if (buttonView.getTag().equals("switch1")) {
                    // Code to execute when switch1 is clicked
                    if (isChecked == true) {
                        sendDataMQTT("dlhcmut/feeds/bbc-led", "1");

                    } else {
                        sendDataMQTT("dlhcmut/feeds/bbc-led", "0");

                    }
                }

            }
        });


        living_room_switch_air_conditioner_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do something when the switch is toggled
                if (buttonView.getTag().equals("switch2")) {
                    // Code to execute when switch1 is clicked
                    if (isChecked == true) {
                        sendDataMQTT("dlhcmut/feeds/bbc-fan", "2");

                    } else {
                        sendDataMQTT("dlhcmut/feeds/bbc-fan", "3");

                    }
                }

            }
        });
        getLight();
        getFan();
        curlRequest("homeinfo");
        curlRequest("livingroom");
    }

    @Override
    public void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                curlRequest("homeinfo");
            }
        }, delay);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setUpLivingRoomLightButton(View context) {
        living_room_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().INVISIBLE);
                lightSetting.setVisibility(getView().VISIBLE);
            }
        });
    }

    private void setUpBtnLightBack(View context) {
        btn_light_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().VISIBLE);
                lightSetting.setVisibility(getView().INVISIBLE);
            }
        });
    }

    private void setUpLivingRoomAirConditionerButton(View context) {
        living_room_air_conditioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().INVISIBLE);
                airConditionerSetting.setVisibility(getView().VISIBLE);
            }
        });
    }

    private void setUpBtnAirConditionerBack(View context) {
        btn_air_conditioner_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().VISIBLE);
                airConditionerSetting.setVisibility(getView().INVISIBLE);
            }
        });
    }

    public void curlRequest(String feeds) {
        //String url = "https://io.adafruit.com/api/v2/baokhanhle123/feeds/" + feeds + "/data/last?x-aio-key=" + MainActivity.AIO_key;

        //String url ="https://io.adafruit.com/api/v2/ndv/feeds/home/data/last?x-aio-key=aio_qJNi94xTXXFCpurL5dX6vOcAjp0S";
        String url ="https://io.adafruit.com/api/v2/dlhcmut/feeds/value/data/last?x-aio-key=aio_DSdv08B4n07hxZDuifw02Rsil40e";
        //String url = "https://io.adafruit.com/api/v2/nghiaMysT/feeds/sensor-light/data/retain?fbclid=IwAR11BWL_Wz9BV5elUImPVJiwu9n3KH5wm9T0Ih4WdfayxBHlGuk-TZWgjYo";
        //String url = "https://io.adafruit.com/api/v2/nghiaMysT/feeds/sensor-light/data/retain/";
        Context context = getActivity();
        if (context != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        //@RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String value = response.getString("value");
                                JSONObject jsonVal = new JSONObject(value);
                            /*if (feeds.equals("homeinfo")) {
                                String temp = jsonVal.getString("temp");
                                String humid = jsonVal.getString("humidity");
                                String gas = jsonVal.getString("gas");

                                Log.d("Message", "homeinfo " + temp );

                                setHomeInfo(temp, humid, gas);
                            } else if (feeds.equals("livingroom")) {
                                Log.d("Message", "living room X" );
                                JSONArray light = jsonVal.getJSONArray("light");
                                JSONArray air = jsonVal.getJSONArray("air");

                                Log.d("Message", "light ");

                                handleData(light, air);
                            }*/
                                //String temp = response.getString("value");
                                ////String temp =String.valueOf(BigDecimal.valueOf(jsonVal.getDouble("temp")).floatValue());
                                //String humid =String.valueOf(BigDecimal.valueOf(jsonVal.getDouble("humidity")).floatValue());
                                Float temp = (Float.parseFloat(jsonVal.getString("temp")));
                                Float humid =  (Float.parseFloat(jsonVal.getString("humidity")));
                                MainActivity.getInstance().dbHandler.addNewRecord(temp,humid);
                                //MainActivity.getInstance().dbHandler.backup();
                                Map<String, Object> record = new HashMap<>();
                                record.put("temp", temp);
                                record.put("humid", humid);
                                MainActivity.getInstance().usersRef.add(record)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                                setHomeInfo(temp, humid,"0");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }

    }
    public void getLight() {
        //String url = "https://io.adafruit.com/api/v2/baokhanhle123/feeds/" + feeds + "/data/last?x-aio-key=" + MainActivity.AIO_key;

        //String url ="https://io.adafruit.com/api/v2/ndv/feeds/home/data/last?x-aio-key=aio_qJNi94xTXXFCpurL5dX6vOcAjp0S";
        String url ="https://io.adafruit.com/api/v2/dlhcmut/feeds/bbc-led/data/last?x-aio-key=aio_DSdv08B4n07hxZDuifw02Rsil40e";
        //String url = "https://io.adafruit.com/api/v2/nghiaMysT/feeds/sensor-light/data/retain?fbclid=IwAR11BWL_Wz9BV5elUImPVJiwu9n3KH5wm9T0Ih4WdfayxBHlGuk-TZWgjYo";
        //String url = "https://io.adafruit.com/api/v2/nghiaMysT/feeds/sensor-light/data/retain/";
        Context context = getActivity();
        if (context != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        //@RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String value = response.getString("value");

                                setLightButton(value);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }

    }
    public void getFan() {
        //String url = "https://io.adafruit.com/api/v2/baokhanhle123/feeds/" + feeds + "/data/last?x-aio-key=" + MainActivity.AIO_key;

        //String url ="https://io.adafruit.com/api/v2/ndv/feeds/home/data/last?x-aio-key=aio_qJNi94xTXXFCpurL5dX6vOcAjp0S";
        String url ="https://io.adafruit.com/api/v2/dlhcmut/feeds/bbc-fan/data/last?x-aio-key=aio_DSdv08B4n07hxZDuifw02Rsil40e";
        //String url = "https://io.adafruit.com/api/v2/nghiaMysT/feeds/sensor-light/data/retain?fbclid=IwAR11BWL_Wz9BV5elUImPVJiwu9n3KH5wm9T0Ih4WdfayxBHlGuk-TZWgjYo";
        //String url = "https://io.adafruit.com/api/v2/nghiaMysT/feeds/sensor-light/data/retain/";
        Context context = getActivity();
        if (context != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        //@RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String value = response.getString("value");

                                setFanButton(value);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }

    }
    public void setLightButton(String light){
        if (light.equals("1")) {
            if(living_room_switch_light_1.isChecked()==false) {
                living_room_switch_light_1.setChecked(true);

            }
        } else {
            if(living_room_switch_light_1.isChecked()==true) {
                living_room_switch_light_1.setChecked(false);

            }


        }
    }
    public void setFanButton(String fan){
        if (fan.equals("2")) {

            if(living_room_switch_air_conditioner_1.isChecked()==false) {
                living_room_switch_air_conditioner_1.setChecked(true);

            }
        } else {
            if(living_room_switch_air_conditioner_1.isChecked()==true) {
                living_room_switch_air_conditioner_1.setChecked(false);

            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setHomeInfo(Float temp, Float humid, String gas) {
        tempView = getView().findViewById(R.id.tempText_1);
        humidView = getView().findViewById(R.id.humidText_1);
        //gasView = getView().findViewById(R.id.gasText_1);

        tempProgress = getView().findViewById(R.id.tempProgressBar_1);
        humidProgress = getView().findViewById(R.id.humidProgressBar_1);
        //gasProgress = getView().findViewById(R.id.gasProgressBar_1);

        tempView.setText(temp + "°C");
        humidView.setText(humid + "%");
        //gasView.setText(gas + "%");
        tempProgress.setProgress(Math.round(temp), true);
        humidProgress.setProgress(Math.round(humid), true);
        //gasProgress.setProgress(Integer.parseInt(gas), true);
    }

    /*public void handleData(JSONArray lightData, JSONArray airConditionerData) throws JSONException {
        int[] livingRoomLightSwitchList = {
                R.id.living_room_switch_light_1,
                R.id.living_room_switch_light_2,
                R.id.living_room_switch_light_3,
                R.id.living_room_switch_light_4
        };
        int[] livingRoomAirSwitchList = {
                R.id.living_room_air_conditioner_1,
                R.id.living_room_air_conditioner_2,
        };
        int[] livingRoomAirSwitchList = {R.id.living_room_switch_air_conditioner_1, R.id.living_room_switch_air_conditioner_2};

        for (int i = 0; i < lightData.length(); i++) {
            SwitchCompat switchCompat = getView().findViewById(livingRoomLightSwitchList[i]);
            switchCompat.setChecked(lightData.getString(i).equals("1"));
        }

        for (int i = 0; i < airConditionerData.length(); i++) {
            SwitchCompat switchCompat = getView().findViewById(livingRoomAirSwitchList[i]);
            switchCompat.setChecked(airConditionerData.getString(i).equals("1"));
        }

        light = new JSONArray(lightData.toString());
        airConditioner = new JSONArray(airConditionerData.toString());

        ((MainActivity) getActivity()).updateLivingRoomStatus(light, airConditioner);
    }

    public void handlePublishData(View v, String kind, int ID) throws JSONException {
        if (kind.equals("light")) {
            int[] livingRoomLightSwitchList = {
                    R.id.living_room_switch_light_1,
                    R.id.living_room_switch_light_2,
                    R.id.living_room_switch_light_3,
                    R.id.living_room_switch_light_4
            };

            JSONObject data = new JSONObject();

            try{
                light.put(ID, light.getInt(ID) == 1 ? 0 : 1);
                data.put("light", light);
                data.put("air", airConditioner);
                sendDataMQTT(data, "livingroom");

            }catch(Exception e){

            }

            sendDataMQTT(data, "livingroom");
        } else if (kind.equals("air")) {
            airConditioner.put(ID, airConditioner.getInt(ID) == 1 ? 0 : 1);
            JSONObject data = new JSONObject();
            data.put("light", light);
            data.put("air", airConditioner);
            sendDataMQTT(data, "livingroom");
        }
    }


    @Override
    public void onClick(View v) {
        Log.d("Bug", "Bug");
        switch (v.getId()) {
            case R.id.living_room_switch_light_1:
                try {
                    Log.d("Bug2", "Bug2");
                    handlePublishData(v, "light", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.living_room_switch_light_2:
                try {
                    handlePublishData(v, "light", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.living_room_switch_light_3:
                try {
                    handlePublishData(v, "light", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.living_room_switch_light_4:
                try {
                    handlePublishData(v, "light", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.living_room_switch_air_conditioner_1:
                try {
                    handlePublishData(v, "air", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.living_room_switch_air_conditioner_2:
                try {
                    handlePublishData(v, "air", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void sendDataMQTT(JSONObject data, String topic) {
        MqttMessage message = new MqttMessage();
        message.setId(Integer.parseInt(clientID));
        message.setQos(0);
        message.setRetained(true);
        byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);
        message.setPayload(bytes);
        Log.d("publish", "Publish:" + message);
        try {
            mqttHelper.mqttAndroidClient.publish("xMysT/feeds/" + topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }*/
    public void sendDataMQTT(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            if(mqttHelper.mqttAndroidClient.isConnected()){
            mqttHelper.mqttAndroidClient.publish(topic, msg);}
        }catch (MqttException e){

        }
    }

    public void startMQTT_new() {
        mqttHelper = new MQTTHelper(getContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                //sendDataMQTT("dlhcmut/feeds/bbc-fan", "get_state");

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("send ",topic + "*" + message.toString());

                if (topic.contains("sensor")) {

                } else if (topic.contains("bbc-fan")) {

                    if (message.toString().equals("2")) {

                        if(living_room_switch_air_conditioner_1.isChecked()==false) {
                            living_room_switch_air_conditioner_1.setChecked(true);

                        }
                    } else {
                        if(living_room_switch_air_conditioner_1.isChecked()==true) {
                            living_room_switch_air_conditioner_1.setChecked(false);

                        }
                    }
                }
                else if (topic.contains("bbc-led")) {

                    if (message.toString().equals("1")) {
                        if(living_room_switch_light_1.isChecked()==false) {
                            living_room_switch_light_1.setChecked(true);

                        }
                    } else {
                        if(living_room_switch_light_1.isChecked()==true) {
                            living_room_switch_light_1.setChecked(false);

                        }


                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}