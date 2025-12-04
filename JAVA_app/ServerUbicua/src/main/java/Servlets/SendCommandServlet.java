package servlets;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import logic.Log;
import logic.Projectinitializer;
import mqtt.MQTTBroker;
import mqtt.MQTTPublisher;

@WebServlet("/sendCommand")
public class SendCommandServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Log.log.info("SendCommandServlet iniciado");
        System.out.println(">>> Entrando en SendCommandServlet <<<");
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            String action = request.getParameter("action");
            
            Log.log.info("Parámetro action: " + action);
            
            if (action == null || action.isEmpty()) {
                Log.log.warn("Parámetro action vacío o nulo");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing 'action' parameter. Use: reset, on, or off\"}");
                return;
            }
            
            JsonObject command = new JsonObject();
            command.addProperty("sensor_id", "TC_1678");
            command.addProperty("action", action);
            
            String jsonString = command.toString();
            String topic = "sensors/ST_1678/cmd";
            
            Log.log.info("Comando JSON creado: " + jsonString);
            Log.logmqtt.info("Publicando comando en topic: " + topic);
            
            MQTTBroker broker = Projectinitializer.getBroker();
            
            if (broker == null) {
                Log.log.error("MQTT Broker no inicializado");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"MQTT Broker not initialized\"}");
                return;
            }
            
            MQTTPublisher.publish(broker, topic, jsonString);
            
            Log.logmqtt.info("Comando publicado exitosamente: " + jsonString);
            System.out.println("Comando publicado en " + topic + ": " + jsonString);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\",\"command\":\"" + action + "\"}");
            Log.log.info("SendCommandServlet finalizado correctamente");
            
        } catch (Exception e) {
            Log.log.error("SendCommand Exception: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
