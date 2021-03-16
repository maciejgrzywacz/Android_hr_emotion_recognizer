package com.example.hremotionrecognizerapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hremotionrecognizer.HREmotionRecognizer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity {

    private final static int ALL_PERMISSIONS = 1;
    private final static int REQUEST_ENABLE_BT = 2;

    private HREmotionRecognizer mHREmotionLib;
    private TextView hrTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHREmotionLib = new HREmotionRecognizer(this);
        mHREmotionLib.setUserAge(23);

        hrTextView = findViewById(R.id.hr_value);

        Button findSensorButton = (Button) findViewById(R.id.button);
        findSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog with sensors
                mHREmotionLib.connectToSensorWithDialog(getSupportFragmentManager());
//                mHREmotionLib.addOnEmotionChangeListener(new HREmotionRecognizer.EmotionChangeListener() {
//                    @Override
//                    public void onEmotionChange(int emotion, int hr) {
//                        hrTextView.setText(String.valueOf(hr));
//                    }
//                });
//                mHREmotionLib.startRecording();
            }
        });


        Button displayHRButton = (Button) findViewById(R.id.displayHRButton);
        displayHRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHREmotionLib.addOnHRChangeListener(new HREmotionRecognizer.HRChangeListener() {
                    @Override
                    public void onHRChange(int hr, HREmotionRecognizer.Emotion emotion) {
                        hrTextView.setText("HR: " + String.valueOf(hr) + ", Emotion: " + emotion.getEmotionStr());
                    }
                });
                mHREmotionLib.startRecording();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        checkPermissions();
        // Ask user to enable bluetooth if not enabled
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void checkPermissions() {
        /* Checks if required permissions are set and requests them if not*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.BLUETOOTH);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissions.size() > 0)
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), ALL_PERMISSIONS);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /* If user did not grant required permissions disable app functionality*/
        if (requestCode == ALL_PERMISSIONS && grantResults.length != 3) {

        }
    }
}
