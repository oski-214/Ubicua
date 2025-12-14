package com.example.ejemplopl3;

import android.util.Log;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos; // Importar MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import java.util.UUID;
import java.util.function.Consumer;

public class MqttHandler {
    private static final String TAG = "MqttHandler";
    private Mqtt5AsyncClient client;

    public void connect(String brokerUrl, Consumer<Mqtt5Publish> onMessageReceived) {
        String host = brokerUrl.replace("tcp://", "").split(":")[0];

        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("AndroidApp-" + UUID.randomUUID().toString())
                .serverHost(host)
                .serverPort(1883)
                .buildAsync();

        client.connect()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Fallo en la conexión MQTT", throwable);
                    } else {
                        Log.d(TAG, "¡Conexión MQTT exitosa!");
                        client.publishes(MqttGlobalPublishFilter.ALL, onMessageReceived);
                    }
                });
    }

    public void subscribe(String topic, int qos) {
        if (client == null) {
            Log.w(TAG, "El cliente no está inicializado.");
            return;
        }
        client.subscribeWith()
                .topicFilter(topic)
                // CORRECCIÓN APLICADA AQUÍ:
                .qos(MqttQos.fromCode(qos))
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Fallo en la suscripción al topic '" + topic + "'", throwable);
                    } else {
                        Log.d(TAG, "¡Suscripción al topic '" + topic + "' exitosa!");
                    }
                });
    }

    public void disconnect() {
        if (client != null && client.getState().isConnected()) {
            client.disconnect();
            Log.d(TAG, "Cliente MQTT desconectado.");
        }
    }
}
