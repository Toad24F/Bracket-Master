/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import com.google.gson.JsonArray;
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
@WebServlet("/SaveMatchServlet")
public class SaveMatchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
            int idTorneo = json.get("id_torneo").getAsInt();
            int ronda = json.get("ronda").getAsInt();
            int matchIndex = json.get("match_index").getAsInt();
            String resultado = json.get("resultado").toString(); // Resultado como string JSON

            // Agregar logs para depurar
            System.out.println("Datos recibidos para guardar:");
            System.out.println("ID Torneo: " + idTorneo);
            System.out.println("Ronda: " + ronda);
            System.out.println("Match Index: " + matchIndex);
            System.out.println("Resultado: " + resultado);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
                // Verificar si ya existe el match
                String selectQuery = "SELECT COUNT(*) FROM seeds_results WHERE id_torneo = ? AND ronda = ? AND match_index = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                selectStmt.setInt(1, idTorneo);
                selectStmt.setInt(2, ronda);
                selectStmt.setInt(3, matchIndex);

                ResultSet rs = selectStmt.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;

                // Insertar o actualizar el resultado
                String query;
                if (exists) {
                    query = "UPDATE seeds_results SET resultado = ? WHERE id_torneo = ? AND ronda = ? AND match_index = ?";
                } else {
                    query = "INSERT INTO seeds_results (resultado, id_torneo, ronda, match_index) VALUES (?, ?, ?, ?)";
                }

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, resultado);
                stmt.setInt(2, idTorneo);
                stmt.setInt(3, ronda);
                stmt.setInt(4, matchIndex);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("error", "No se pudo actualizar la base de datos.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", "Error al guardar el match.");
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
