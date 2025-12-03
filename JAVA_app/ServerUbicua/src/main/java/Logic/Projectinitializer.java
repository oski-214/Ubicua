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
        System.out.println("-->Suscribe Topics<--");
        String topic = "sensors/#";
        broker = new MQTTBroker();

        // SOLO crea un suscriptor si aún no existe
        if (suscriber == null) {
            suscriber = new MQTTSuscriber();
            suscriber.suscribeTopic(broker, topic);
            // (Opcional) Realiza el publish solo una vez
            MQTTPublisher.publish(broker, topic, "Hello from Tomcat :)");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Desconexión limpia del cliente MQTT (muy recomendable para evitar leaks)
        if (suscriber != null) {
            suscriber.disconnect();
        }
    }
}
