package com.example.ejemplopl3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class StreetAdapter extends RecyclerView.Adapter<StreetAdapter.StreetViewHolder> {

    private List<Street> streetList;

    public StreetAdapter(List<Street> streetList) {
        this.streetList = streetList;
    }

    @NonNull
    @Override
    public StreetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.street_row_item, parent, false);
        return new StreetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StreetViewHolder holder, int position) {
        Street currentStreet = streetList.get(position);

        // Asigna los valores a cada TextView
        holder.streetId.setText(currentStreet.getStreet_id());
        holder.streetName.setText(currentStreet.getStreet_name());
        holder.streetLength.setText(String.format(Locale.getDefault(), "%.2f m", currentStreet.getStreet_length()));
        holder.latitude.setText(String.format(Locale.getDefault(), "%.4f", currentStreet.getLatitude()));
        holder.longitude.setText(String.format(Locale.getDefault(), "%.4f", currentStreet.getLongitude()));
        holder.district.setText(currentStreet.getDistrict());
        holder.neighborhood.setText(currentStreet.getNeighborhood());
        holder.postalCode.setText(currentStreet.getPostal_code());
        holder.surfaceType.setText(currentStreet.getSurface_type());
        holder.speedLimit.setText(String.format(Locale.getDefault(), "%d km/h", currentStreet.getSpeed_limit()));
    }

    @Override
    public int getItemCount() {
        return streetList.size();
    }

    /**
     * ViewHolder: Encuentra y guarda una referencia a cada TextView del layout.
     */
    public static class StreetViewHolder extends RecyclerView.ViewHolder {
        // Declara un TextView para cada campo que quieras mostrar
        public TextView streetId, streetName, streetLength, latitude, longitude, district, neighborhood, postalCode, surfaceType, speedLimit;

        public StreetViewHolder(View view) {
            super(view);
            // Encuentra cada TextView por su ID
            streetId = view.findViewById(R.id.text_street_id);
            streetName = view.findViewById(R.id.text_street_name);
            streetLength = view.findViewById(R.id.text_street_length);
            latitude = view.findViewById(R.id.text_latitude);
            longitude = view.findViewById(R.id.text_longitude);
            district = view.findViewById(R.id.text_district);
            neighborhood = view.findViewById(R.id.text_neighborhood);
            postalCode = view.findViewById(R.id.text_postal_code);
            surfaceType = view.findViewById(R.id.text_surface_type);
            speedLimit = view.findViewById(R.id.text_speed_limit);
        }
    }
}
