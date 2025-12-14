package com.example.ejemplopl3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonToStreets;
    private Button buttonToVehicles;
    private Button buttonToRegistros;
    private Button buttonToMqtt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Encontrar los botones en el layout
        buttonToStreets = findViewById(R.id.button_to_streets);
        buttonToVehicles = findViewById(R.id.button_to_vehicles);
        buttonToRegistros = findViewById(R.id.button_to_registros);
        buttonToMqtt = findViewById(R.id.button_to_mqtt);

        // Configurar el listener para el botón de Calles
        buttonToStreets.setOnClickListener(v -> {
            // Crear un Intent para abrir StreetSelection
            Intent intent = new Intent(MainActivity.this, StreetSelection.class);
            startActivity(intent);
        });

        // Configurar el listener para el botón de Vehículos
        buttonToVehicles.setOnClickListener(v -> {
            // Crear un Intent para abrir VehicleSelection
            Intent intent = new Intent(MainActivity.this, VehicleTypeSelection.class);
            startActivity(intent);
        });

        // Configurar el listener para el botón de Registros
        buttonToRegistros.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroSelection.class);
            startActivity(intent);
        });

        buttonToMqtt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MqttMonitoringActivity.class);
            startActivity(intent);
        });
    }
}
