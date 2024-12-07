import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SaveBracketServ")
public class SaveBracketServ extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/torneos";
    private static final String USER = "Adrian";
    private static final String PASSWORD = "Gatumadre12";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String jsonData = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

        System.out.println("JSON Received: " + jsonData); // Log para depuración

        Gson gson = new Gson();
        BracketResults bracketResults;

        try {
            // Deserializar JSON en un objeto BracketResults
            bracketResults = gson.fromJson(jsonData, BracketResults.class);
            System.out.println("Deserialized Bracket Results: " + bracketResults);
        } catch (JsonSyntaxException e) {
            System.err.println("Error deserializing JSON: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid JSON format");
            return;
        }

        // Guardar resultados en la base de datos
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            connection.setAutoCommit(false);

            // Guardar resultados
            saveResults(connection, bracketResults);

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error saving bracket results.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Bracket results saved successfully.");
    }

 private void saveResults(Connection connection, BracketResults bracketResults) throws SQLException {
     // Borrar los registros actuales del torneo antes de insertar nuevos datos
    String deleteQuery = """
        DELETE FROM results
        WHERE tournament_id = ?
    """;

    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
        deleteStmt.setInt(1, bracketResults.getTournamentId());
        deleteStmt.executeUpdate();
    }
     
    String query = """
        INSERT INTO results (tournament_id, round, partida, team_1_score, team_2_score)
        VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE team_1_score = VALUES(team_1_score), team_2_score = VALUES(team_2_score)
    """;

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        int roundNumber = 1;
        for (List<List<List<Integer>>> roundSet : bracketResults.getResults()) {
            for (List<List<Integer>> round : roundSet) {
                int matchNumber = 1;
                for (List<Integer> match : round) {
                    stmt.setInt(1, bracketResults.getTournamentId());
                    stmt.setInt(2, roundNumber);
                    stmt.setInt(3, matchNumber);
                    stmt.setInt(4, match.get(0) == null ? 0 : match.get(0)); // Manejar null
                    stmt.setInt(5, match.get(1) == null ? 0 : match.get(1)); // Manejar null
                    stmt.addBatch();
                    matchNumber++;
                }
                roundNumber++;
            }
        }
        stmt.executeBatch();
    }
}

    // Clase interna para mapear los datos del JSON
public static class BracketResults {
    private int tournamentId;
    private List<List<List<List<Integer>>>> results; // Ajustar para manejar 4 niveles de listas

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public List<List<List<List<Integer>>>> getResults() {
        return results;
    }

    public void setResults(List<List<List<List<Integer>>>> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "BracketResults{" +
                "tournamentId=" + tournamentId +
                ", results=" + results +
                '}';
    }
}
}
