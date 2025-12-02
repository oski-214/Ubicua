package mqtt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import db.Topics;
import logic.Log;
import logic.Logic;

public class MQTTSuscriber implements MqttCallback {
    private MqttClient sampleClient; // <--- atributo global de la clase

    public void suscribeTopic(MQTTBroker broker, String topic) {
        Log.logmqtt.debug("Suscribe to topics");
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient( // <-- instancia asignada al atributo
                MQTTBroker.getBroker(), 
                MQTTBroker.getClientId() + System.currentTimeMillis(), 
                persistence
            );
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(MQTTBroker.getUsername());
            connOpts.setPassword(MQTTBroker.getPassword().toCharArray());
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker: " + MQTTBroker.getBroker());
            sampleClient.connect(connOpts);
            System.out.println("Connected to broker!");

            sampleClient.setCallback(this);

            System.out.println("About to subscribe to topic: " + topic);
            sampleClient.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);

            Log.logmqtt.info("Subscribed to {}", topic);

        } catch (MqttException me) {
            System.out.println("Error suscribing topic: " + me);
            me.printStackTrace();
            Log.logmqtt.error("Error suscribing topic: {}", me);
        } catch (Exception e) {
            System.out.println("Error suscribing topic: " + e);
            Log.logmqtt.error("Error suscribing topic: {}", e);
        }
    }

    public void disconnect() {
        try {
            if (sampleClient != null && sampleClient.isConnected()) {
                sampleClient.disconnect();
                System.out.println("MQTT client disconnected cleanly.");
            }
        } catch (Exception e) {
            System.out.println("MQTT disconnect error: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.logmqtt.warn("Connection lost: {}", cause.getMessage());
        // Aquí podrías implementar lógica de reconexión
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("[MQTT RECEIVED] Topic: " + topic + " Msg: " + message);
        Log.logmqtt.info("{}: {}", topic, message.toString());

        try {
            JsonObject json = JsonParser.parseString(message.toString()).getAsJsonObject();
            JsonObject location = json.getAsJsonObject("location");
            JsonObject data = json.getAsJsonObject("data");

            // 1. Datos del JSON (raíz)
            // String sensor_id = json.get("sensor_id").getAsString(); // No se usa directamente en DB
            // String sensor_type = json.get("sensor_type").getAsString(); // No se usa directamente en DB
            String street_id = json.get("street_id").getAsString();
            String timestamp = json.get("timestamp").getAsString(); // Se usará en Registro

            // 2. Información de "location" (para Street)
            Double street_length = location.get("street_length").getAsDouble();
            Double latitude = location.get("latitude").getAsDouble();
            Double longitude = location.get("longitude").getAsDouble();
            String district = location.get("district").getAsString();
            String neighborhood = location.get("neighborhood").getAsString();
            Integer postal_code = location.get("postal_code").getAsInt();
            String street_name = location.get("street_name").getAsString();
            String surface_type = location.get("surface_type").getAsString();
            Integer speed_limit = location.get("speed_limit").getAsInt();

            // 3. Información de "data" (para Vehículos y Registro)
            // Integer vehicle_count = data.get("vehicle_count").getAsInt(); // Se puede calcular
            // Integer pedestrian_count = data.get("pedestrian_count").getAsInt(); // No se usa en DB
            Integer bicycle_count = data.get("bicycle_count").getAsInt();
            Integer car_count = data.get("car_count").getAsInt();
            Integer truck_count = data.get("truck_count").getAsInt();
            Integer eco_count = data.get("eco_count").getAsInt();
            Integer gas_count = data.get("gas_count").getAsInt();
            String plate = data.get("plate").getAsString();
            Double mean_pressure = data.get("mean_pressure").getAsDouble();
            // Double distance = data.get("distance").getAsDouble(); // Solo se usa en Vehículo
            // String direction = data.get("direction").getAsString(); // No se usa en DB
            // String counter_type = data.get("counter_type").getAsString(); // No se usa en DB
            String technology = data.get("technology").getAsString(); // 'ECO' o 'GAS' (tipo_emision)
            String type_vehicle = data.get("type_vehicle").getAsString(); // 'Coche', 'Bicicleta', 'Camion' (tipo_vehiculo)
            
            // ----------------------------------------------------
            // 4. Lógica de Inserción en MariaDB
            // ----------------------------------------------------
            
            // 4.1. Inserción/Actualización de Street: Se inserta si no existe.
            // La función debería manejar el ON DUPLICATE KEY UPDATE o un SELECT antes de INSERT
            Logic.insertOrUpdateStreet(
                street_id, street_length, latitude, longitude, district, neighborhood, 
                postal_code, street_name, surface_type, speed_limit
            );
            
            // 4.2. Inserción del Vehículo (Coche, Bicicleta o Camion): Se inserta si no existe
            // Asumiendo que el JSON tiene también el dato 'distance'
            Double media_distancias = data.get("distance").getAsDouble();
            Logic.insertOrUpdateVehicle(
                plate, mean_pressure, technology, media_distancias, street_id, type_vehicle
            );

            // 4.3. Inserción de Registro: La inserción del Registro es única para cada evento.
            // La función Logic.insertRegistro debe retornar el ID generado.
            int registroId = Logic.insertRegistro(
                type_vehicle, plate, street_id, timestamp, car_count, truck_count, 
                bicycle_count, gas_count, eco_count, technology
            );

            // 4.4. Inserción en la tabla de relación Many-to-Many
            if (registroId > 0) {
                Logic.insertManyToOne(type_vehicle, plate, registroId);
            } else {
                Log.logmqtt.error("Error al obtener el ID de Registro, no se insertó la relación Many-to-Many.");
            }
            
            // Además, guardamos el mensaje completo en la tabla Topics si es necesario
            Topics newTopic = new Topics();
            newTopic.setValue(message.toString());
            // Lógica para guardar newTopic si aplica (Ej: Logic.saveTopic(newTopic);)


        } catch (Exception e) {
            System.err.println("Error processing MQTT message: " + e.getMessage());
            e.printStackTrace();
            Log.logmqtt.error("Error processing MQTT message: {}", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    
    
}
