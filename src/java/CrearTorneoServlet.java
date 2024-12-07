
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/CrearTorneoServlet")
public class CrearTorneoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        // Obtener el usuario actual de la sesión como creador
        HttpSession session = request.getSession();
        String creador = (String) session.getAttribute("username");

        // Leer los datos del torneo desde la solicitud
        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String tournamentDescripcion = json.get("tournamentDescripcion").getAsString(); // Reemplaza espacios por guiones
        String tournamentReglas = json.get("tournamentReglas").getAsString();
        String tournamentLugar = json.get("tournamentLugar").getAsString();
        String tournamentFecha = json.get("tournamentFecha").getAsString();
        String nombre = json.get("tournamentName").getAsString();
        String juego = json.get("game").getAsString();
        int numParticipantes = json.get("numParticipantes").getAsInt(); // Obtenemos el número de participantes

        // Nombre de la tabla que queremos crear
        String sanitizedTableName = "torneo_" + nombre.replaceAll("[^a-zA-Z0-9_]", "");

        JsonObject jsonResponse = new JsonObject();
        try {
            // Verificar si la tabla del torneo ya existe
            if (existeTablaTorneo(sanitizedTableName)) {
                jsonResponse.addProperty("exists", true);
            } else {
                // Si no existe, procedemos a crear el torneo
                boolean success = crearTorneo(nombre, juego, creador, numParticipantes, sanitizedTableName, tournamentDescripcion, tournamentReglas, tournamentLugar, tournamentFecha);
                jsonResponse.addProperty("success", success);
                jsonResponse.addProperty("exists", false);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CrearTorneoServlet.class.getName()).log(Level.SEVERE, null, ex);
            jsonResponse.addProperty("error", "Server error occurred");
        }

        // Responder con el JSON al cliente
        response.getWriter().print(jsonResponse.toString());
    }

    public boolean crearTorneo(String nombre, String juego, String creador, int numParticipantes, String tableName, String tournamentDescripcion, String tournamentReglas, String tournamentLugar, String tournamentFecha) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
            // Insertar en la tabla de torneos creados
            String sqlInsert = "INSERT INTO torneoscreados (nombre, juego, descripcion, reglas, fecha, lugar, creador, numparticipantes, tabla) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
            stmtInsert.setString(1, nombre);
            stmtInsert.setString(2, juego);
            stmtInsert.setString(3, tournamentDescripcion);
            stmtInsert.setString(4, tournamentReglas);
            stmtInsert.setString(5, tournamentFecha);
            stmtInsert.setString(6, tournamentLugar);
            stmtInsert.setString(7, creador);
            stmtInsert.setInt(8, numParticipantes);
            stmtInsert.setString(9, tableName);
            int rowsInserted = stmtInsert.executeUpdate();
            if (rowsInserted > 0) {
                // Crear la tabla específica para el torneo
                crearTablaTorneo(conn, tableName, numParticipantes);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existeTablaTorneo(String tableName) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
            String checkTableExistsQuery = "SHOW TABLES LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(checkTableExistsQuery);
            stmt.setString(1, tableName);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true si la tabla ya existe
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void crearTablaTorneo(Connection conn, String tableName, int numParticipantes) throws Exception {
        // Define la estructura de la tabla según el número de participantes
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE " + tableName + " (");
        createTableSQL.append("id INT PRIMARY KEY AUTO_INCREMENT, ");
        createTableSQL.append("participante VARCHAR(50)"); // Cambiamos a VARCHAR para almacenar el nombre del usuario
        createTableSQL.append(")");

        // Ejecutar la creación de la tabla
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL.toString());
        }
    }
}
