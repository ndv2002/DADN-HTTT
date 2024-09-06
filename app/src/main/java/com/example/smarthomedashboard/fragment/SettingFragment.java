package com.example.smarthomedashboard.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthomedashboard.MainActivity;
import com.example.smarthomedashboard.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.example.smarthomedashboard.mqtt.MQTTHelper;
import com.google.firebase.firestore.DocumentReference;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {
    float tempLimit = 0;
    MQTTHelper mqttHelper;
    TextView limit;
    Slider slider;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        slider = view.findViewById(R.id.slider);
        Button apply = view.findViewById(R.id.applyBtn);
        limit = view.findViewById(R.id.tempLimit);
        startMQTT_new();
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tempLimit = value;
//                Log.d("aaa", "onValueChange: " + tempLimit);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit.setText(Integer.toString((int) tempLimit) + "°C");
                //((MainActivity) getActivity()).updateLimit((int) tempLimit);
                sendDataMQTT("dlhcmut/feeds/fire-alarm", Integer.toString((int) tempLimit));
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        curlRequest("fire-alarm");

    }

    @Override
    public void onResume() {
        super.onResume();
        TextView limit = getView().findViewById(R.id.tempLimit);
        limit.setText(Integer.toString((int) tempLimit) + "°C");
    }
    public void curlRequest(String feeds) {
        //String url = "https://io.adafruit.com/api/v2/baokhanhle123/feeds/" + feeds + "/data/last?x-aio-key=" + MainActivity.AIO_key;

        //String url ="https://io.adafruit.com/api/v2/ndv/feeds/home/data/last?x-aio-key=aio_qJNi94xTXXFCpurL5dX6vOcAjp0S";
        String url ="https://io.adafruit.com/api/v2/dlhcmut/feeds/fire-alarm/data/last?x-aio-key=aio_DSdv08B4n07hxZDuifw02Rsil40e";
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
                                setLimit(value);
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
                                /*Float temp = (Float.parseFloat(jsonVal.getString("temp")));
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
                                setHomeInfo(temp, humid,"0");*/


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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setLimit(String temp_limit){
        Log.d("send", "set " + temp_limit);
        limit.setText(temp_limit + "°C");
        slider.setValue(Integer.parseInt(temp_limit));
    }
    public void sendDataMQTT(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){

        }
    }

    public void startMQTT_new() {
        mqttHelper = new MQTTHelper(getContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {



            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("send ",topic + "*" + message.toString());

                if (topic.contains("fire-alarm")) {
                    //limit.setText(Integer.toString((int) tempLimit) + "°C");
                    limit.setText(message.toString() + "°C");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
