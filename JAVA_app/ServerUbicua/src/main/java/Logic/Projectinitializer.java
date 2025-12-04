package logic;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import mqtt.MQTTBroker;
import mqtt.MQTTPublisher;
import mqtt.MQTTSuscriber;

@WebListener
public class Projectinitializer implements ServletContextListener {

    // Mantén como atributo de clase la única instancia de suscriptor y broker
    private static MQTTSuscriber suscriber;
    private static MQTTBroker broker;
    
    public static MQTTBroker getBroker() {
        return broker;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Log.log.info("========================================");
        Log.log.info("=== UBICOMP SERVER INICIANDO ===");
        Log.log.info("========================================");
        
        System.out.println("-->Suscribe Topics<--");
        String topic = "sensors/ST_1678/traffic_counter";
        
        Log.logmqtt.info("Inicializando MQTT Broker...");
        broker = new MQTTBroker();
        Log.logmqtt.info("MQTT Broker inicializado");

        // SOLO crea un suscriptor si aún no existe
        if (suscriber == null) {
            Log.logmqtt.info("Creando suscriptor para topic: " + topic);
            suscriber = new MQTTSuscriber();
            suscriber.suscribeTopic(broker, topic);
            Log.logmqtt.info("Suscripción completada");
            // (Opcional) Realiza el publish solo una vez
            //MQTTPublisher.publish(broker, topic, "Hello from Tomcat :)");
        }
        
        Log.log.info("=== SERVIDOR INICIADO CORRECTAMENTE ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Log.log.info("=== APAGANDO SERVIDOR ===");
        // Desconexión limpia del cliente MQTT (muy recomendable para evitar leaks)
        if (suscriber != null) {
            Log.logmqtt.info("Desconectando suscriptor MQTT...");
            suscriber.disconnect();
            Log.logmqtt.info("Suscriptor desconectado");
        }
        Log.log.info("=== SERVIDOR APAGADO ===");
    }
}
