package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import logic.Log;
import logic.Logic;
import logic.Projectinitializer;
import mqtt.MQTTBroker;
import mqtt.MQTTPublisher;

/**
 *
 * @author oscar
 */
@WebServlet("/sendCommand")
public class SendCommandServlet extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException {
        
        System.out.println(">>> Entrando en SendCommandServlet <<<");
        String led = request.getParameter("led");      // ej: "1"
        String state = request.getParameter("state");  // ej: "ON"

        String json = "{\"led\":" + led + ",\"state\":\"" + state + "\"}";
        String topic = "sensors/ST_1678/cmd";

        MQTTBroker broker = Projectinitializer.getBroker();
        MQTTPublisher.publish(broker, topic, json);

        System.out.println("Publicado en " + topic + ": " + json);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Command sent");
    }
}
