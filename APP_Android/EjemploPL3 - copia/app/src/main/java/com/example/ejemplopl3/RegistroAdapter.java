package com.example.ejemplopl3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private final List<Registro> registroList;
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public RegistroAdapter(List<Registro> registroList) {
        this.registroList = registroList;
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.registro_row_item, parent, false);
        return new RegistroViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Registro currentRegistro = registroList.get(position);

        holder.plate.setText(currentRegistro.getPlate());
        holder.streetId.setText("en " + currentRegistro.getStreet_id());

        if (currentRegistro.getTimestamp() != null) {
            holder.timestamp.setText(displayFormat.format(currentRegistro.getTimestamp()));
        } else {
            holder.timestamp.setText("Fecha no disponible");
        }

        holder.carCount.setText("Coches: " + currentRegistro.getCar_count());
        holder.truckCount.setText("Camiones: " + currentRegistro.getTruck_count());
        holder.bicycleCount.setText("Bicis: " + currentRegistro.getBicycle_count());
        holder.gasCount.setText("GAS: " + currentRegistro.getGas_count());
        holder.ecoCount.setText("ECO: " + currentRegistro.getEco_count());
        holder.technology.setText("Tec: " + currentRegistro.getTechnology());
    }

    @Override
    public int getItemCount() {
        return registroList.size();
    }

    public static class RegistroViewHolder extends RecyclerView.ViewHolder {
        public TextView plate, timestamp, streetId, carCount, truckCount, bicycleCount, gasCount, ecoCount, technology;

        public RegistroViewHolder(View view) {
            super(view);
            plate = view.findViewById(R.id.text_registro_plate);
            timestamp = view.findViewById(R.id.text_registro_timestamp);
            streetId = view.findViewById(R.id.text_registro_street_id);
            carCount = view.findViewById(R.id.text_registro_car_count);
            truckCount = view.findViewById(R.id.text_registro_truck_count);
            bicycleCount = view.findViewById(R.id.text_registro_bicycle_count);
            gasCount = view.findViewById(R.id.text_registro_gas_count);
            ecoCount = view.findViewById(R.id.text_registro_eco_count);
            technology = view.findViewById(R.id.text_registro_technology);
        }
    }
}
