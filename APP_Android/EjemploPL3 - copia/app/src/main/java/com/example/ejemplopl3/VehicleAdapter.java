package com.example.ejemplopl3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;

    public VehicleAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_row_item, parent, false);
        return new VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle currentVehicle = vehicleList.get(position);

        holder.licensePlate.setText(currentVehicle.getMatricula());
        holder.type.setText(currentVehicle.getTipo());
        holder.streetId.setText(currentVehicle.getStreet_id());
        holder.pressure.setText(String.format(Locale.getDefault(), "%.2f", currentVehicle.getMedia_presiones()));
        holder.distance.setText(String.format(Locale.getDefault(), "%.2f", currentVehicle.getMedia_distancias()));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        public TextView licensePlate, type, pressure, distance, streetId;

        public VehicleViewHolder(View view) {
            super(view);
            licensePlate = view.findViewById(R.id.text_vehicle_license_plate);
            type = view.findViewById(R.id.text_vehicle_type);
            pressure = view.findViewById(R.id.text_vehicle_pressure);
            distance = view.findViewById(R.id.text_vehicle_distance);
            streetId = view.findViewById(R.id.text_vehicle_street_id);
        }
    }
}
