package com.example.ejemplopl3;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class MqttMonitoringActivity extends AppCompatActivity {
    private static final String TAG = "MqttMonitoringActivity";

    // --- CONFIGURACIÓN DE MQTT ---
    private static final String MQTT_BROKER_URL = "tcp://10.0.2.2:1883";
    private static final String MQTT_TOPIC = "sensors/ST_1678/traffic_counter";
    // -----------------------------

    private MqttHandler mqttHandler;
    private TextView messageTextView;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mqtt_monitoring);

        // ... (código de insets y toolbar sin cambios)
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

        messageTextView = findViewById(R.id.mqtt_message_textview);
        statusTextView = findViewById(R.id.mqtt_status_textview);

        mqttHandler = new MqttHandler();

        // Define qué hacer cuando se recibe un mensaje
        Consumer<Mqtt5Publish> onMessageReceived = publish -> {
            String payload = StandardCharsets.UTF_8.decode(publish.getPayload().get()).toString();
            Log.d(TAG, "Mensaje recibido en el topic '" + publish.getTopic() + "': " + payload);
            runOnUiThread(() -> {
                statusTextView.setText("Estado: Conectado");
                messageTextView.setText(payload);
            });
        };

        // Conecta y suscribe
        mqttHandler.connect(MQTT_BROKER_URL, onMessageReceived);

        // La suscripción se puede hacer inmediatamente después. La librería lo gestiona internamente.
        // Pequeña espera para asegurar que la conexión se inicia antes de suscribir.
        new android.os.Handler().postDelayed(() -> {
            mqttHandler.subscribe(MQTT_TOPIC, 1);
        }, 1000); // 1 segundo
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
