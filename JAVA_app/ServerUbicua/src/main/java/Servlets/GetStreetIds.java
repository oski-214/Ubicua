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

@WebServlet("/GetStreetIds")
public class GetStreetIds extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Log.log.info("GetStreetIds servlet iniciado");

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<String> streets = Logic.getStreetIds();

            Log.logdb.info("GetStreetIds: ids obtenidos = " + streets.size());

            String json = new Gson().toJson(streets);
            out.print(json);
            Log.log.info("GetStreetIds finalizado correctamente");

        } catch (Exception e) {
            Log.log.error("Exception in GetStreetIds: ", e);
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
