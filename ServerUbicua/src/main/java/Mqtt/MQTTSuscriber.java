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
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("[MQTT RECEIVED] Topic: " + topic + " Msg: " + message);
        JsonObject json = JsonParser.parseString(message.toString()).getAsJsonObject();
        JsonObject location = json.getAsJsonObject("location");
        JsonObject data = json.getAsJsonObject("data");

        // Raíz del JSON
        String sensor_id = json.get("sensor_id").getAsString();
        String sensor_type = json.get("sensor_type").getAsString();
        String street_id = json.get("street_id").getAsString();
        String timestamp = json.get("timestamp").getAsString();

        // Información de "location"
        Double street_length = location.get("street_length").getAsDouble();
        Double latitude = location.get("latitude").getAsDouble();
        Double longitude = location.get("longitude").getAsDouble();
        String district = location.get("district").getAsString();
        String neighborhood = location.get("neighborhood").getAsString();
        Integer postal_code = location.get("postal_code").getAsInt();
        String street_name = location.get("street_name").getAsString();
        String surface_type = location.get("surface_type").getAsString();
        Integer speed_limit = location.get("speed_limit").getAsInt();

        // Información de "data"
        Integer vehicle_count = data.get("vehicle_count").getAsInt();
        Integer pedestrian_count = data.get("pedestrian_count").getAsInt();
        Integer bicycle_count = data.get("bicycle_count").getAsInt();
        Integer car_count = data.get("car_count").getAsInt();
        Integer truck_count = data.get("truck_count").getAsInt();
        Integer eco_count = data.get("eco_count").getAsInt();
        Integer gas_count = data.get("gas_count").getAsInt();
        String plate = data.get("plate").getAsString();
        Double mean_pressure = data.get("mean_pressure").getAsDouble();
        Double distance = data.get("distance").getAsDouble();
        String direction = data.get("direction").getAsString();
        String counter_type = data.get("counter_type").getAsString();
        String technology = data.get("technology").getAsString();
        String type_vehicle = data.get("type_vehicle").getAsString();
        
        Logic.insertStreetData(street_id, street_length, latitude, longitude, district, neighborhood, postal_code, street_name, surface_type, speed_limit);
        
        
        
        Log.logmqtt.info("{}: {}", topic, message.toString());
        Topics newTopic = new Topics();
        newTopic.setValue(message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    
    
}
