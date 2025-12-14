package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import logic.Log;
import logic.Logic;

@WebServlet("/GetLicensePlates")
public class GetLicensePlates extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Log.log.info("GetLicensePlates servlet iniciado");

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String table = request.getParameter("table"); // Coche, Bicicleta, Camion
            Log.log.info("Parámetro table en GetLicensePlates: " + table);

            if (table == null || table.isEmpty()) {
                Log.log.warn("Parámetro table vacío o nulo en GetLicensePlates");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"missing_table_param\"}");
                return;
            }

            List<String> plates;
            try {
                plates = Logic.getLicensePlates(table);
            } catch (IllegalArgumentException ex) {
                Log.log.warn("Tabla inválida en GetLicensePlates: " + table, ex);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"invalid_table\"}");
                return;
            }

            Log.logdb.info("GetLicensePlates: matrículas obtenidas = " + plates.size());

            String json = new Gson().toJson(plates);
            out.print(json);
            Log.log.info("GetLicensePlates finalizado correctamente");

        } catch (Exception e) {
            Log.log.error("Exception in GetLicensePlates: ", e);
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
