package com.example.ejemplopl3;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView; // Importar TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleSelection extends AppCompatActivity {

    private Spinner vehicleSpinner;
    private Button searchButton;
    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleDataList;
    private List<String> licensePlates = new ArrayList<>();
    private String vehicleType; // Variable para guardar el tipo de vehículo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita el modo Edge-to-Edge. 
        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_vehicle_selection);



        // Encuentra el layout principal por su nuevo ID
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Obtiene los espacios ocupados por las barras del sistema (arriba y abajo)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplica esos espacios como padding al layout principal
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Devuelve los insets para que el sistema continúe procesándolos
            return insets;
        });


        // 1. Recibir el tipo de vehículo de la actividad anterior
        vehicleType = getIntent().getStringExtra("VEHICLE_TYPE");
        if (vehicleType == null) {
            // Fallback por si algo va mal
            Toast.makeText(this, "Error: Tipo de vehículo no especificado", Toast.LENGTH_LONG).show();
            finish(); // Cierra la actividad si no hay tipo
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Vehículos: " + vehicleType);
        }


        // 2. Actualizar el título de la pantalla
        TextView title = findViewById(R.id.vehicle_selection_title);
        title.setText("Selecciona un " + vehicleType);


        vehicleSpinner = findViewById(R.id.vehicle_spinner);
        searchButton = findViewById(R.id.vehicle_button);
        recyclerView = findViewById(R.id.vehicles_recycler_view);

        // Configurar RecyclerView
        vehicleDataList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(vehicleDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(vehicleAdapter);


        // 1. Configurar el adaptador del Spinner (inicialmente vacío)
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, licensePlates);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(spinnerAdapter);

        // 2. Llamar al nuevo método para cargar las matrículas desde la API
        fetchLicensePlatesForSpinner(vehicleType);


        searchButton.setOnClickListener(v -> {
            String selectedPlate = (String) vehicleSpinner.getSelectedItem();
            if (selectedPlate != null && !selectedPlate.isEmpty()) {
                fetchVehicleDetails(selectedPlate);
            } else {
                Toast.makeText(this, "Por favor, selecciona una matrícula", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Este método llama a la API para obtener las matrículas.
     * @param type El tipo de vehículo ("Coche", "Camion", etc.) que se usará como nombre de la tabla.
     */
    private void fetchLicensePlatesForSpinner(String type) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<String>> call = apiService.getLicensePlatesByType(type);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    licensePlates.clear();
                    licensePlates.addAll(response.body());

                    // Notifica al adaptador del Spinner que los datos han cambiado
                    ((ArrayAdapter<String>) vehicleSpinner.getAdapter()).notifyDataSetChanged();
                    Toast.makeText(VehicleSelection.this, "Matrículas cargadas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VehicleSelection.this, "Error al cargar las matrículas", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                Toast.makeText(VehicleSelection.this, "Fallo de red al cargar matrículas: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_FAILURE_SPINNER", "Fallo al cargar matrículas para " + type, t);
            }
        });
    }

    private void fetchVehicleDetails(String licensePlate) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        // La llamada a la API usa la variable 'vehicleType' para la tabla correcta
        Call<List<Vehicle>> call = apiService.getVehicleByLicensePlate(vehicleType, licensePlate);

        call.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(@NonNull Call<List<Vehicle>> call, @NonNull Response<List<Vehicle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleDataList.clear();
                    vehicleDataList.addAll(response.body());
                    vehicleAdapter.notifyDataSetChanged();

                    if (vehicleDataList.isEmpty()) {
                        Toast.makeText(VehicleSelection.this, "No se encontró el vehículo: " + licensePlate, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VehicleSelection.this, "Datos cargados", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_ERROR", "Respuesta no exitosa. Código: " + response.code());
                    Toast.makeText(VehicleSelection.this, "Error al obtener datos: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Vehicle>> call, @NonNull Throwable t) {
                Log.e("API_FAILURE", "Fallo en la conexión: ", t);
                Toast.makeText(VehicleSelection.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Cierra la actividad actual y regresa a la anterior
        finish();
        return true;
    }

}
