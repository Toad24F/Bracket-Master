
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

@WebServlet("/EliminarTorneoServlet")
public class EliminarTorneoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        // Leer el nombre del torneo desde la solicitud
        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String nombreTorneo = json.get("nombreTorneo").getAsString();
        int idtorneo = json.get("idtorneo").getAsInt();

        JsonObject jsonResponse = new JsonObject();
        try {
            // Obtener el nombre de la tabla correspondiente al torneo
            String nombreTabla = obtenerNombreTabla(nombreTorneo);
            if (nombreTabla != null) {
                // Eliminar la tabla del torneo y el registro en torneoscreados
                boolean success = eliminarTorneo(nombreTorneo, nombreTabla, idtorneo);
                jsonResponse.addProperty("success", success);
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "El torneo no existe.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
        }

        // Enviar la respuesta JSON
        response.getWriter().print(jsonResponse.toString());
    }

    private String obtenerNombreTabla(String nombreTorneo) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
            String query = "SELECT id, tabla FROM torneoscreados WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nombreTorneo);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("tabla"); // Retornar el nombre de la tabla
                }
            }
        }
        return null;
    }

    public boolean eliminarTorneo(String nombreTorneo, String nombreTabla, int idtorneo) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
            // Eliminar la tabla específica del torneo
            String dropTableSQL = "DROP TABLE IF EXISTS " + nombreTabla;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(dropTableSQL);
            }
            // Eliminar el registro del las seeds del bracket generados
            String deleteseedstorneo = "DELETE FROM seeds WHERE id_torneo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteseedstorneo)) {
                stmt.setInt(1, idtorneo);
                stmt.executeUpdate();
            }
            // Eliminar el registro de resultados del torneo
            String deleteresultados = "DELETE FROM results WHERE id_torneo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteseedstorneo)) {
                stmt.setInt(1, idtorneo);
                stmt.executeUpdate();
            }
            // Eliminar el registro del torneo en la tabla torneoscreados
            String deleteFromTorneosCreadosSQL = "DELETE FROM torneoscreados WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteFromTorneosCreadosSQL)) {
                stmt.setString(1, nombreTorneo);
                stmt.executeUpdate();
            }
            try (Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12")) {

                // Eliminar el participantes de los equipos
                String deleteparticipantes = "DELETE FROM equipo_participantes WHERE id_torneo = ?";
                try (PreparedStatement stmt = conn1.prepareStatement(deleteparticipantes)) {
                    stmt.setInt(1, idtorneo);
                    stmt.executeUpdate();
                }
                // Eliminar el equipos del torneo
                String deleteequipo = "DELETE FROM equipos WHERE id_torneo = ?";
                try (PreparedStatement stmt = conn1.prepareStatement(deleteequipo)) {
                    stmt.setInt(1, idtorneo);
                    stmt.executeUpdate();
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
