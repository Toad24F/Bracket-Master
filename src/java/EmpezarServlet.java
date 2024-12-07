/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;


/**
 *
 * @author adria
 */
@WebServlet("/EmpezarServlet")
public class EmpezarServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        int idTorneo = Integer.parseInt(request.getParameter("id_torneo"));
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
            // Verificar si ya existen seeds para el torneo
            String verificarSeedsSQL = "UPDATE torneoscreados SET estado = 'Iniciado' WHERE id = ?";
            PreparedStatement verificarStmt = conn.prepareStatement(verificarSeedsSQL);
            verificarStmt.setInt(1, idTorneo);
            verificarStmt.executeUpdate();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Listo el torneo a sido empezado");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", "Error en el servidor.");
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
