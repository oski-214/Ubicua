package com.example.ejemplopl3;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class MqttMonitoringActivity extends AppCompatActivity {
    private static final String TAG = "MqttMonitoringActivity";

    private static final String MQTT_BROKER_URL = "tcp://10.0.2.2:1883";
    private static final String MQTT_TOPIC = "sensors/ST_1678/traffic_counter";

    private MqttHandler mqttHandler;
    private TextView messageTextView, statusTextView;
    private ImageView imgVehicleType, imgFuelType;

    // Variables de la tarjeta nueva
    private TextView textPlate, textTech, textPressure, textDistance, textStreet, textTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mqtt_monitoring);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Monitorización MQTT");
        }

        // Vincular vistas
        messageTextView = findViewById(R.id.mqtt_message_textview);
        statusTextView = findViewById(R.id.mqtt_status_textview);
        imgVehicleType = findViewById(R.id.img_vehicle_type);
        imgFuelType = findViewById(R.id.img_fuel_type);

        // Vincular tarjeta
        textPlate = findViewById(R.id.text_vehicle_license_plate);
        textTech = findViewById(R.id.text_vehicle_type);
        textPressure = findViewById(R.id.text_vehicle_pressure);
        textDistance = findViewById(R.id.text_vehicle_distance);
        textStreet = findViewById(R.id.text_vehicle_street_id);
        textTime = findViewById(R.id.text_vehicle_time);

        mqttHandler = new MqttHandler();

        Consumer<Mqtt5Publish> onMessageReceived = publish -> {
            String payload = StandardCharsets.UTF_8.decode(publish.getPayload().get()).toString();

            runOnUiThread(() -> {
                statusTextView.setText("Estado: Conectado");
                messageTextView.setText(payload);

                try {
                    JSONObject fullJson = new JSONObject(payload);
                    JSONObject dataObject = fullJson.getJSONObject("data");

                    // Extraer datos principales
                    String plate = dataObject.optString("plate", "N/A");
                    String tech = dataObject.optString("technology", "N/A");
                    double pressure = dataObject.optDouble("mean_pressure", 0.0);
                    double distance = dataObject.optDouble("distance", 0.0);
                    String street = fullJson.optString("street_id", "Desconocida");
                    String timestamp = fullJson.optString("timestamp", "--:--:--");
                    String tipoVehiculo = dataObject.optString("type_vehicle", "").toLowerCase();

                    // Rellenar tarjeta
                    textPlate.setText(plate);
                    textTech.setText(tech.toUpperCase());
                    textPressure.setText(String.valueOf(pressure));
                    textDistance.setText(String.valueOf(distance));
                    textStreet.setText(street);
                    textTime.setText("Recibido: " + timestamp);

                    // Lógica de imágenes
                    if (tipoVehiculo.contains("coche")) imgVehicleType.setImageResource(R.drawable.coche);
                    else if (tipoVehiculo.contains("bici") || tipoVehiculo.contains("bicycle")) imgVehicleType.setImageResource(R.drawable.bici);
                    else if (tipoVehiculo.contains("camion") || tipoVehiculo.contains("truck")) imgVehicleType.setImageResource(R.drawable.camion);
                    else imgVehicleType.setImageResource(0);

                    if (tech.toLowerCase().contains("eco")) imgFuelType.setImageResource(R.drawable.eco);
                    else if (tech.toLowerCase().contains("gas")) imgFuelType.setImageResource(R.drawable.gas);
                    else imgFuelType.setImageResource(0);

                } catch (Exception e) {
                    Log.e(TAG, "Error JSON: " + e.getMessage());
                }
            });
        };

        mqttHandler.connect(MQTT_BROKER_URL, onMessageReceived);
        new android.os.Handler().postDelayed(() -> mqttHandler.subscribe(MQTT_TOPIC, 1), 1000);
    }

    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}