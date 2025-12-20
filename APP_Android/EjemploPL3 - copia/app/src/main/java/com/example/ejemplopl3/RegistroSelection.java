package com.example.ejemplopl3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
    private CardView cardResumen;
    private TextView txtResumenTotal, txtResumenTipo, txtResumenTech;
    private Button btnExportPdf;
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

        cardResumen = findViewById(R.id.card_resumen);
        txtResumenTotal = findViewById(R.id.txt_resumen_total);
        txtResumenTipo = findViewById(R.id.txt_resumen_tipo);
        txtResumenTech = findViewById(R.id.txt_resumen_tech);

        btnExportPdf = findViewById(R.id.btn_export_pdf);
        btnExportPdf.setOnClickListener(v -> generarPDF());

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
                    actualizarTarjetaResumen(response.body());
                    Toast.makeText(RegistroSelection.this, "Datos cargados: " + registroDataList.size() + " registros.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Respuesta no exitosa: " + response.code());
                    Toast.makeText(RegistroSelection.this, "Error al obtener datos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Registro>> call, @NonNull Throwable t) {

                // Imprime el error completo en el Logcat para poder diagnosticarlo.
                Log.e("API_FAILURE", "Fallo de conexión en RegistroSelection. Causa: " + t.getMessage(), t);

                // Muestra un Toast más detallado al usuario.
                Toast.makeText(RegistroSelection.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void actualizarTarjetaResumen(List<Registro> lista) {
        if (lista == null || lista.isEmpty()) {
            cardResumen.setVisibility(View.GONE);
            return;
        }

        int totalCoches = 0;
        int totalCamiones = 0;
        int totalBicis = 0;
        int totalEco = 0;
        int totalGas = 0;

        for (Registro reg : lista) {
            totalCoches += reg.getCar_count();
            totalCamiones += reg.getTruck_count();
            totalBicis += reg.getBicycle_count();

            String tech = reg.getTechnology();
            if (tech != null) {
                if (tech.equalsIgnoreCase("ECO")) totalEco++;
                else if (tech.equalsIgnoreCase("GAS")) totalGas++;
            }
        }

        
        int sumaTotal = totalCoches + totalCamiones + totalBicis;

        String masNumeroso = "Coches";
        if (totalCamiones > totalCoches && totalCamiones > totalBicis) masNumeroso = "Camiones";
        else if (totalBicis > totalCoches && totalBicis > totalCamiones) masNumeroso = "Bicicletas";

        String techPredom = (totalEco >= totalGas) ? "ECO" : "GAS";

       
       
        String textoTotal = "Total de vehículos hoy: " + sumaTotal;
        String textoTipo = "Tipo predominante: " + masNumeroso;
        String textoTech = "Tecnología predominante: " + techPredom;

        runOnUiThread(() -> {
            cardResumen.setVisibility(View.VISIBLE);
            btnExportPdf.setVisibility(View.VISIBLE);
            txtResumenTotal.setText(textoTotal);
            txtResumenTipo.setText(textoTipo);
            txtResumenTech.setText(textoTech);

            if (techPredom.trim().equalsIgnoreCase("ECO")) {
                cardResumen.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5E9")); // Verde
            } else {
                cardResumen.setCardBackgroundColor(android.graphics.Color.parseColor("#E3F2FD")); // Azul
            }
        });
    }
    private void generarPDF() {
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();

        // Calculamos el alto de la página según el número de matrículas (30dp por matrícula + cabecera)
        int altoPagina = 250 + (registroDataList.size() * 20);
        android.graphics.pdf.PdfDocument.PageInfo pageInfo =
                new android.graphics.pdf.PdfDocument.PageInfo.Builder(300, altoPagina, 1).create();

        android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);
        android.graphics.Canvas canvas = page.getCanvas();
        android.graphics.Paint paint = new android.graphics.Paint();

        // --- Título y Resumen ---
        paint.setTextSize(14f);
        paint.setFakeBoldText(true);
        canvas.drawText("INFORME DE TRÁFICO", 10, 30, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(10f);
        canvas.drawText("Fecha: " + dateInput.getText().toString(), 10, 55, paint);
        canvas.drawText(txtResumenTotal.getText().toString(), 10, 75, paint);
        canvas.drawText(txtResumenTipo.getText().toString(), 10, 95, paint);
        canvas.drawText(txtResumenTech.getText().toString(), 10, 115, paint);

        // --- Listado de Matrículas ---
        paint.setFakeBoldText(true);
        canvas.drawText("DETALLE DE MATRÍCULAS:", 10, 150, paint);

        paint.setFakeBoldText(false);
        int yPos = 175; // Posición inicial de la primera matrícula

        for (Registro reg : registroDataList) {
            String matricula = (reg.getPlate() != null) ? reg.getPlate() : "S/N";
            String tipo = reg.getType_vehicle();
            canvas.drawText("- " + matricula + " (" + tipo + ")", 15, yPos, paint);
            yPos += 20; // Bajamos 20dp para la siguiente línea
        }

        document.finishPage(page);

        // --- Guardar Archivo ---
        String fileName = "Informe_" + dateInput.getText().toString() + ".pdf";
        java.io.File file = new java.io.File(getExternalFilesDir(null), fileName);

        try {
            document.writeTo(new java.io.FileOutputStream(file));
            Toast.makeText(this, "Informe PDF generado con éxito", Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            Log.e("PDF_ERROR", e.getMessage());
            Toast.makeText(this, "Error al crear el PDF", Toast.LENGTH_SHORT).show();
        }

        document.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Cierra la actividad actual y regresa a la anterior
        finish();
        return true;
    }

}
