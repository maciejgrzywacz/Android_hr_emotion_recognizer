package com.example.hremotionrecognizer;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import java.util.concurrent.ExecutionException;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;

public class HREmotionRecognizer {

    enum EmotionRecognitionMethod {
        METHOD_1,
        METHOD_2
    }

    public enum Emotion {
        EMOTION_BORED("Bored"),
        EMOTION_NEUTRAL("Neutral"),
        EMOTION_STRESSED("Stressed");

        private String emotionStr;

        Emotion(String emotionStr) {
            this.emotionStr = emotionStr;
        }

        public String getEmotionStr() {
            return emotionStr;
        }
    }

    public interface HRChangeListener {
        void onHRChange(int hr, Emotion emotion);
    }

    private EmotionRecognitionMethod mEmotionRecognitionMethod = EmotionRecognitionMethod.METHOD_1;

    private int mUserAge = 0;
    private HRChangeListener mEmotionChangeListener;
    private String mSensorAddress;
    private final Context mContext;

    public HREmotionRecognizer(Context context) {
        mContext = context;
    }

    public void connectToSensorWithDialog(FragmentManager fragmentManager) {
        SensorChooserDialogFragment dialog = new SensorChooserDialogFragment(new SensorChooserDialogFragment.OnSensorChosenCallback() {
            @Override
            public void onSensorChosen(String sensorAddress) {
                mSensorAddress = sensorAddress;
            }
        });
        dialog.show(fragmentManager, "sensors dialog");
    }

    public void addOnHRChangeListener(HRChangeListener listener) {
        mEmotionChangeListener = listener;
    }

    public boolean isReadyToRecord() {
        if (mSensorAddress == null) {
            Toast.makeText(mContext, "Error: No sensor connected.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (mUserAge == 0) {
            Toast.makeText(mContext, "Error: User age not specified.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (mEmotionChangeListener == null) {
            return false;
        }

        return true;
    }

    public void setUserAge(int age) {
        mUserAge = age;
    }

    private Emotion HRtoEmotion(int hr) {
        switch (mEmotionRecognitionMethod) {
            case METHOD_1:
                double x = 0.1;

                if (mUserAge < 10) {
                    x *= hr - 120;
                }
                else if (mUserAge < 20) {
                    x *= hr - 100;
                }
                else if (mUserAge < 50) {
                    x *= hr - 85;
                }
                else if (mUserAge < 100) {
                    x *= hr - 80;
                }

                if (x < -10) return Emotion.EMOTION_BORED;
                if (x < 2) return Emotion.EMOTION_NEUTRAL;
                return Emotion.EMOTION_STRESSED;

            case METHOD_2:
        };

        return null;
    }

    public void startRecording() {
        if (isReadyToRecord()) {

            PolarBleApi api;

            api = PolarBleApiDefaultImpl.defaultImplementation(mContext,
                    PolarBleApi.FEATURE_BATTERY_INFO |
                            PolarBleApi.FEATURE_DEVICE_INFO |
                            PolarBleApi.FEATURE_HR);

            api.setApiCallback(new PolarBleApiCallback() {
                @Override
                public void deviceConnected(PolarDeviceInfo s) {
                    Toast.makeText(mContext, "Sensor connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {
                }

                @Override
                public void deviceDisconnected(PolarDeviceInfo s) {
                    Toast.makeText(mContext, "Sensor disconnected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void hrNotificationReceived(String s, PolarHrData polarHrData) {
                    mEmotionChangeListener.onHRChange(polarHrData.hr, HRtoEmotion(polarHrData.hr));
                }
            });

            try {
                api.connectToDevice(mSensorAddress);
            }
            catch (PolarInvalidArgument a){
                a.printStackTrace();
            }
        }
    }
}
