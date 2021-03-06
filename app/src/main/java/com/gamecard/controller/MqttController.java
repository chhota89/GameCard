package com.gamecard.controller;

import android.util.Log;

import com.gamecard.callback.CallBackMqtt;
import com.gamecard.model.GameResponseModel;
import com.gamecard.utility.JsonConvertor;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.realm.Realm;

/**
 * Created by bridgeit on 1/8/16.
 */

public class MqttController {
    MqttAndroidClient client;
    private final String TAG = "MqttController";
    IMqttToken subToken = null;

    public MqttController(MqttAndroidClient client) {
        this.client = client;
    }

    public void subcibeTopic(final Realm realm, String clientId, final CallBackMqtt callback) {

        //subcribe to the topic
        int qos = 1;

        try {
            subToken = client.subscribe(clientId, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        subToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        Log.e(TAG, "connectionLost: ", throwable);
                        callback.onError(throwable);
                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                        String message = new String(mqttMessage.getPayload());
                        Log.i(TAG, "messageArrived: ....." + message);
                        //callback.onMessageRecive(gson.fromJson(message, GameResponseModel.class));
                        GameResponseModel model = null;

                        try {
                            realm.beginTransaction();
                            model = realm.createObjectFromJson(GameResponseModel.class, message);
                            callback.onMessageRecive(model);
                        } catch (Exception exception) {
                            Log.e(TAG, "messageArrived: ", exception);
                        } finally {
                            realm.commitTransaction();
                        }
                        //model = realm.createObjectFromJson(GameResponseModel.class, message);
                        //model = JsonConvertor.getGameResponseFromJson(message);

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    }
                });
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken,
                                  Throwable exception) {
                Log.e(TAG, "onFailure: ", exception);
                callback.onError(exception);
            }
        });

    }

    public interface CallBackConnectMqtt {
        void onConnectionSuccess();

        void onFailure(Throwable throwable);
    }

    public void connectToMqtt(final CallBackConnectMqtt callback) {
        IMqttToken token = null;
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("lxakiriz");
        mqttConnectOptions.setPassword("58IinVRYyVcJ".toCharArray());
        try {
            token = client.connect(mqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        token.setActionCallback(new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                callback.onConnectionSuccess();
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }
}
