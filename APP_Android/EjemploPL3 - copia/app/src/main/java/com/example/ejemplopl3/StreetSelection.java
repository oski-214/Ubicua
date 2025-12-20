package com.example.ejemplopl3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ListView;
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


public class StreetSelection extends AppCompatActivity {
    private Spinner spinner;
    private Button button;
    private RecyclerView recyclerView;
    private StreetAdapter streetAdapter;
    private List<Street> streetDataList;
    
    private List<String> streetIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita el modo Edge-to-Edge
        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_street_selection);



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
            getSupportActionBar().setTitle("Detalles de Calle");
        }


        // Inicializar vistas
        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.button);
        recyclerView = findViewById(R.id.streets_recycler_view);

        // Configurar RecyclerView
        streetDataList = new ArrayList<>();
        streetAdapter = new StreetAdapter(streetDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(streetAdapter);

        // 1. Configurar el adaptador del Spinner (inicialmente estará vacío)
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, streetIds);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // 2. Llamar al nuevo método para cargar los IDs desde la API
        fetchStreetIdsForSpinner();


        // Lógica del botón
        button.setOnClickListener(v -> {
            // CORREGIDO: El item seleccionado será un String.
            String selectedId = (String) spinner.getSelectedItem();
            if (selectedId != null && !selectedId.isEmpty()) {
                // CORREGIDO: El parámetro del metodo ya es String, no necesita conversión.
                fetchStreetDetailsById(selectedId);
            } else {
                Toast.makeText(this, "Por favor, selecciona un ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Obtiene la lista de IDs de calles desde la API y los carga en el Spinner.
     */
    private void fetchStreetIdsForSpinner() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<String>> call = apiService.getAllStreetIds();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    streetIds.clear();
                    streetIds.addAll(response.body());

                    // Notifica al adaptador del Spinner que los datos han cambiado
                    ((ArrayAdapter<String>) spinner.getAdapter()).notifyDataSetChanged();
                    Toast.makeText(StreetSelection.this, "IDs de calles cargados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StreetSelection.this, "Error al cargar los IDs de las calles", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                Toast.makeText(StreetSelection.this, "Fallo de red al cargar IDs: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_FAILURE_SPINNER", "Fallo al cargar IDs de calles", t);
            }
        });
    }

    private void fetchStreetDetailsById(String street_id) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        
        Call<List<Street>> call = apiService.getStreetById("Street", street_id);

        call.enqueue(new Callback<List<Street>>() {
            @Override
            public void onResponse(@NonNull Call<List<Street>> call, @NonNull Response<List<Street>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    // Limpiamos la lista de datos del RecyclerView.
                    streetDataList.clear();
                    // Añadimos TODOS los resultados de la API a la lista.
                    streetDataList.addAll(response.body());

                    // Notificamos al StreetAdapter que los datos han cambiado.
                    // El RecyclerView se redibujará automáticamente.
                    streetAdapter.notifyDataSetChanged();

                    if (streetDataList.isEmpty()) {
                        Toast.makeText(StreetSelection.this, "No se encontró la calle con ID: " + street_id, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(StreetSelection.this, "Datos cargados", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_ERROR", "Respuesta no exitosa. Código: " + response.code());
                    Toast.makeText(StreetSelection.this, "Error al obtener datos: " + response.message(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Street>> call, @NonNull Throwable t) {
                Log.e("API_FAILURE", "Fallo en la conexión: ", t);
                Toast.makeText(StreetSelection.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Cierra la actividad actual y regresa a la anterior
        finish();
        return true;
    }
    /*
    private void mostrarLista(List<Street> lista) {
        StringBuilder builder = new StringBuilder();
        listaCalles = lista;

        for (Street item : lista) {
            builder.append(item.getId())
                    .append(" - ")
                    .append(item.getNombre())
                    .append("\n");
        }

        Log.e("ubicua",builder.toString());

        ArrayList<String> nombres = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {

            nombres.add(lista.get(i).getNombre());
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ubicua", "Boton pulsado: "+ spinner.getSelectedItem());
                Log.i("ubicua", "Boton pulsado: "+ listaCalles.get((int) spinner.getSelectedItemId()).getNombre());
                spinner.getSelectedItem();
                spinner.getSelectedItemId();

                Intent intent = new Intent(StreetSelection.this, StreetMonitoring.class);
                intent.putExtra("street_id", listaCalles.get((int) spinner.getSelectedItemId()).getId());
                intent.putExtra("street_name", listaCalles.get((int) spinner.getSelectedItemId()).getNombre());
                startActivity(intent);
                finish();
            }
        });
    }

    public void pulsarBoton()
    {
        Log.i("ubicua", "Boton pulasdo");
    }

     */
}
