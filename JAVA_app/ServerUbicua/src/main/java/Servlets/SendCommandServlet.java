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
        
        System.out.println(">>> Entrando en SendCommandServlet <<<");
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            String action = request.getParameter("action");
            
            if (action == null || action.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing 'action' parameter. Use: reset, on, or off\"}");
                return;
            }
            
            JsonObject command = new JsonObject();
            command.addProperty("sensor_id", "TC_1678");
            command.addProperty("action", action);
            
            String jsonString = command.toString();
            String topic = "sensors/ST_1678/cmd";
            
            MQTTBroker broker = Projectinitializer.getBroker();
            
            if (broker == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"MQTT Broker not initialized\"}");
                return;
            }
            
            MQTTPublisher.publish(broker, topic, jsonString);
            
            System.out.println("Comando publicado en " + topic + ": " + jsonString);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\",\"command\":\"" + action + "\"}");
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            Log.log.error("SendCommand Exception: ", e);
        }
    }
}
