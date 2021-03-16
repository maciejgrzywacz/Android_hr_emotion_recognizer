package com.example.hremotionrecognizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.model.PolarHrBroadcastData;

public class SensorChooserDialogFragment
        extends DialogFragment
        implements AvailableSensorAdapter.OnSensorClickedCallback {

    @Override
    public void onSensorClicked(String sensorAddress) {
        mOnSensorChosenCallback.onSensorChosen(sensorAddress);
        SensorChooserDialogFragment.this.getDialog().dismiss();
    }

    public interface OnSensorChosenCallback {
        void onSensorChosen(String sensorAddress);
    }

    private OnSensorChosenCallback mOnSensorChosenCallback;

    private Context mContext;
    private AvailableSensorAdapter mSensorsAdapter;
    private List<String> mSensorNames;
    private PolarBleApi api;
    private Disposable mSensorBroadcastDisposable;

    public SensorChooserDialogFragment(OnSensorChosenCallback callback) {
        mOnSensorChosenCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // create dialog and return it
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sensor_chooser_dialog_layout, null);

        // Add recycler view with sensors
        mSensorNames = new ArrayList<>();

        RecyclerView mSensorsRecyclerView = dialogView.findViewById(R.id.availableSensorsRecyclerView);
        mSensorsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSensorsAdapter = new AvailableSensorAdapter(mSensorNames, this);
        mSensorsRecyclerView.setAdapter(mSensorsAdapter);

        api = PolarBleApiDefaultImpl.defaultImplementation(mContext, 0);

        if (mSensorBroadcastDisposable != null) {
            mSensorBroadcastDisposable.dispose();
            mSensorBroadcastDisposable = null;
        }

        mSensorBroadcastDisposable = api.startListenForPolarHrBroadcasts(null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<PolarHrBroadcastData>() {
                            @Override
                            public void accept(PolarHrBroadcastData polarHrBroadcastData) throws Exception {
                                String sensorName = polarHrBroadcastData.polarDeviceInfo.address;
                                if (!mSensorNames.contains(sensorName)) {
                                    mSensorNames.add(sensorName);
                                    mSensorsAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                );

        builder.setView(dialogView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSensorBroadcastDisposable != null) mSensorBroadcastDisposable.dispose();
                        api.shutDown();
                        api = null;
                        SensorChooserDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
