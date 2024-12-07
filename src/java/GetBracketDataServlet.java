/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import com.google.gson.Gson;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
@WebServlet("/GetBracketDataServlet")
public class GetBracketDataServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/torneos";
    private static final String USER = "Adrian";
    private static final String PASSWORD = "Gatumadre12";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int tournamentId = Integer.parseInt(request.getParameter("id_torneo")); // Recibe el ID del torneo
        List<String[]> teams = new ArrayList<>();
        List<List<List<Integer>>> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // Obtener los equipos desde seeds
            String seedQuery = """
                SELECT equipo
                FROM seeds
                WHERE id_torneo = ?
                ORDER BY posicion
            """;
            try (PreparedStatement seedStmt = connection.prepareStatement(seedQuery)) {
                seedStmt.setInt(1, tournamentId);
                ResultSet seedRs = seedStmt.executeQuery();

                String previousTeam = null;

                while (seedRs.next()) {
                    String currentTeam = seedRs.getString("equipo");

                    if (previousTeam == null) {
                        previousTeam = currentTeam;
                    } else {
                        teams.add(new String[]{previousTeam, currentTeam});
                        previousTeam = null;
                    }
                }

                // Si hay un equipo sin pareja (número impar), añade con null.
                if (previousTeam != null) {
                    teams.add(new String[]{previousTeam, null});
                }
            }

            // Obtener los resultados de matches
            String matchQuery = """
                SELECT round, partida, team_1_score, team_2_score
                FROM results
                WHERE tournament_id = ?
                ORDER BY round, partida
            """;
            try (PreparedStatement matchStmt = connection.prepareStatement(matchQuery)) {
                matchStmt.setInt(1, tournamentId);
                ResultSet matchRs = matchStmt.executeQuery();

                Map<Integer, List<List<Integer>>> rounds = new HashMap<>();
                while (matchRs.next()) {
                    int roundNumber = matchRs.getInt("round");
                    int matchNumber = matchRs.getInt("partida");
                    Integer team1Score = (matchRs.getObject("team_1_score") != null) ? matchRs.getInt("team_1_score") : null;
                    Integer team2Score = (matchRs.getObject("team_2_score") != null) ? matchRs.getInt("team_2_score") : null;

                    // Crea la entrada de la ronda si no existe
                    rounds.computeIfAbsent(roundNumber, k -> new ArrayList<>());

                    // Agrega el resultado del partido usando ArrayList
                    List<Integer> matchScores = new ArrayList<>();
                    matchScores.add(team1Score); // Permite null
                    matchScores.add(team2Score); // Permite null
                    rounds.get(roundNumber).add(matchScores);
                }

                // Convierte el mapa en una lista ordenada de rondas
                for (int i = 1; i <= rounds.size(); i++) {
                    results.add(rounds.getOrDefault(i, new ArrayList<>()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving bracket data.");
            return;
        }

        // Crear JSON para el frontend
        Map<String, Object> bracketData = new HashMap<>();
        bracketData.put("teams", teams);
        bracketData.put("results", results);

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(bracketData));
    }
}
