package com.example.ejemplopl3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

public class VehicleTypeSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita el modo Edge-to-Edge.
        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_vehicle_type_selection);



        // Encuentra el layout principal por su nuevo ID
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Obtiene los espacios ocupados por las barras del sistema (arriba y abajo)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplica esos espacios como padding al layout principal
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Devuelve los insets para que el sistema continúe procesándolos
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tipos de Vehículo");
        }


        Button buttonToCars = findViewById(R.id.button_to_cars);
        Button buttonToTrucks = findViewById(R.id.button_to_trucks);
        Button buttonToBicycles = findViewById(R.id.button_to_bicycles);

        // Listener para Coches
        buttonToCars.setOnClickListener(v -> {
            // Lanza la pantalla de selección de matrículas, pasándole el tipo "Coche"
            Intent intent = new Intent(this, VehicleSelection.class);
            intent.putExtra("VEHICLE_TYPE", "Coche");
            startActivity(intent);
        });

        // Listener para Camiones
        buttonToTrucks.setOnClickListener(v -> {
            Intent intent = new Intent(this, VehicleSelection.class);
            intent.putExtra("VEHICLE_TYPE", "Camion");
            startActivity(intent);
        });

        // Listener para Bicicletas
        buttonToBicycles.setOnClickListener(v -> {
            Intent intent = new Intent(this, VehicleSelection.class);
            intent.putExtra("VEHICLE_TYPE", "Bicicleta");
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
