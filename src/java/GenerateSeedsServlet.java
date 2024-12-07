/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author adria
 */
@WebServlet("/GenerateSeedsServlet")
public class GenerateSeedsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        int idTorneo = Integer.parseInt(request.getParameter("id_torneo"));
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
            // Verificar si ya existen seeds para el torneo
            String verificarSeedsSQL = "SELECT COUNT(*) FROM seeds WHERE id_torneo = ?";
            PreparedStatement verificarStmt = conn.prepareStatement(verificarSeedsSQL);
            verificarStmt.setInt(1, idTorneo);
            ResultSet rs = verificarStmt.executeQuery();

            // borrar registro
            String queryseeds = "DELETE FROM seeds WHERE id_torneo = ?";
            PreparedStatement stmtseeds = conn.prepareStatement(queryseeds);
            stmtseeds.setInt(1, idTorneo);
            stmtseeds.executeUpdate();
            
            // borrar los resultados
            String queryresults = "DELETE FROM results WHERE tournament_id = ?";
            PreparedStatement stmtsres = conn.prepareStatement(queryresults);
            stmtsres.setInt(1, idTorneo);
            stmtsres.executeUpdate();
            
            // Obtener el nombre de la tabla del torneo
            String queryTabla = "SELECT tabla FROM torneoscreados WHERE id = ?";
            PreparedStatement stmtTabla = conn.prepareStatement(queryTabla);
            stmtTabla.setInt(1, idTorneo);
            ResultSet rsTabla = stmtTabla.executeQuery();

            String tabla = null;
            if (rsTabla.next()) {
                tabla = rsTabla.getString("tabla");
            }

            // Validar el nombre de la tabla
            if (tabla == null || tabla.isEmpty()) {
                jsonResponse.addProperty("error", "La tabla del torneo no existe.");
            } else {
                // Obtener los equipos registrados en el torneo
                String queryEquipos = "SELECT participante FROM " + tabla;
                try (PreparedStatement stmtEquipos = conn.prepareStatement(queryEquipos); ResultSet rsEquipos = stmtEquipos.executeQuery()) {

                    List<String> equipos = new ArrayList<>();
                    while (rsEquipos.next()) {
                        equipos.add(rsEquipos.getString("participante"));
                    }

                    // Generar seeds aleatorias
                    Collections.shuffle(equipos);

                    // Guardar las seeds
                    String guardarSeedsSQL = "INSERT INTO seeds (id_torneo, equipo, posicion) VALUES (?, ?, ?)";
                    PreparedStatement stmtGuardar = conn.prepareStatement(guardarSeedsSQL);
                    int posicion = 1;

                    for (String equipo : equipos) {
                        stmtGuardar.setInt(1, idTorneo);
                        stmtGuardar.setString(2, equipo);
                        stmtGuardar.setInt(3, posicion++);
                        stmtGuardar.addBatch();
                    }
                    stmtGuardar.executeBatch();

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Seeds generadas correctamente.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", "Error en el servidor.");
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
