package com.example.ejemplopl3;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroSelection extends AppCompatActivity {

    private EditText dateInput;
    private Button searchButton;
    private RecyclerView recyclerView;
    private RegistroAdapter registroAdapter;
    private List<Registro> registroDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Búsqueda de Registros");
        }

        dateInput = findViewById(R.id.date_input);
        searchButton = findViewById(R.id.registro_button);
        recyclerView = findViewById(R.id.registros_recycler_view);

        registroDataList = new ArrayList<>();
        registroAdapter = new RegistroAdapter(registroDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(registroAdapter);

        searchButton.setOnClickListener(v -> {
            String date = dateInput.getText().toString();
            if (!date.isEmpty()) {
                fetchRegistrosByDate(date);
            } else {
                Toast.makeText(this, "Por favor, introduce una fecha", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRegistrosByDate(String date) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Registro>> call = apiService.getRecordsByDate("Registro", date);

        call.enqueue(new Callback<List<Registro>>() {
            @Override
            public void onResponse(@NonNull Call<List<Registro>> call, @NonNull Response<List<Registro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registroDataList.clear();
                    registroDataList.addAll(response.body());
                    registroAdapter.notifyDataSetChanged();
                    Toast.makeText(RegistroSelection.this, "Datos cargados: " + registroDataList.size() + " registros.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Respuesta no exitosa: " + response.code());
                    Toast.makeText(RegistroSelection.this, "Error al obtener datos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Registro>> call, @NonNull Throwable t) {

                // --- MODIFICACIÓN IMPORTANTE ---
                // Imprime el error completo en el Logcat para poder diagnosticarlo.
                Log.e("API_FAILURE", "Fallo de conexión en RegistroSelection. Causa: " + t.getMessage(), t);

                // Muestra un Toast más detallado al usuario.
                Toast.makeText(RegistroSelection.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
