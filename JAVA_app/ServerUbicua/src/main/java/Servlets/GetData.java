package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import logic.Log;
import logic.Logic;
import logic.Measurement;

/**
 * Servlet implementation class GetData
 */
@WebServlet("/GetData")
public class GetData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Log.log.info("GetData servlet iniciado");
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String table = request.getParameter("table"); // Street, Coche, Bicicleta, Camion, Registro

            Log.log.info("Parámetro table: " + table);

            if (table == null || table.isEmpty()) {
                Log.log.warn("Parámetro table vacío o nulo");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"missing_table_param\"}");
                return;
            }

            // Siempre devolvemos una lista de Measurement
            java.util.List<Measurement> result;

            switch (table) {
                case "Street": {
                    String streetId = request.getParameter("street_id");
                    Log.logdb.info("Consultando Street con street_id: " + streetId);
                    result = Logic.getStreetMeasurements(streetId);
                    break;
                }
                case "Coche":
                case "Bicicleta":
                case "Camion": {
                    String matricula = request.getParameter("matricula");
                    if (matricula == null || matricula.isEmpty()) {
                        Log.log.warn("Parámetro matricula vacío para tabla: " + table);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"error\":\"missing_matricula\"}");
                        return;
                    }
                    Log.logdb.info("Consultando " + table + " con matricula: " + matricula);
                    result = Logic.getVehicleMeasurements(table, matricula);
                    break;
                }
                case "Registro": {
                    String dateStr = request.getParameter("date"); // YYYY-MM-DD
                    if (dateStr == null || dateStr.isEmpty()) {
                        Log.log.warn("Parámetro date vacío para Registro");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"error\":\"missing_date\"}");
                        return;
                    }
                    Log.logdb.info("Consultando Registro con fecha: " + dateStr);
                    result = Logic.getRegistroMeasurementsByDay(dateStr);
                    break;
                }
                default:
                    Log.log.warn("Tabla inválida: " + table);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"invalid_table\"}");
                    return;
            }

            Log.logdb.info("Consulta completada, registros obtenidos: " + result.size());
            String json = new Gson().toJson(result);
            out.print(json);
            Log.log.info("GetData finalizado correctamente");

        } catch (Exception e) {
            Log.log.error("Exception in GetData: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"internal_error\"}");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
