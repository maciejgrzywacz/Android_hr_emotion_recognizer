package com.example.hremotionrecognizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class AvailableSensorAdapter
        extends RecyclerView.Adapter<AvailableSensorAdapter.SensorViewHolder> {

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        View sensorView;
        SensorViewHolder(View v) {
            super(v);
            sensorView = v;
        }
    }

    public interface OnSensorClickedCallback {
        void onSensorClicked(String sensorAddress);
    }

    private OnSensorClickedCallback mOnSensorClickedCallback;
    private RecyclerView mRecyclerView;
    private List<String> mSensorNames;

    public AvailableSensorAdapter(List<String> sensorNames, OnSensorClickedCallback callback) {
        mSensorNames = sensorNames;
        mOnSensorClickedCallback = callback;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_view, parent, false);
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = mRecyclerView.getChildAdapterPosition(v);
                        String deviceAddress = mSensorNames.get(index);
                        mOnSensorClickedCallback.onSensorClicked(deviceAddress);
                    }
                }
        );

        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        ((TextView) holder.sensorView.findViewById(R.id.sensorNameTextView)).setText(mSensorNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mSensorNames.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }
}
