import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.DriverManager;

@WebServlet("/JoinTeamServlet")
public class JoinTeamServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        int equipoId = json.get("equipoId").getAsInt();
        String username = json.get("username").getAsString();
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12")) {
            // Verificar si el usuario ya está registrado en otro equipo del mismo torneo
            String verificarSQL = "SELECT COUNT(*) FROM equipos e "
                                + "JOIN equipo_participantes ep ON e.id = ep.equipo_id "
                                + "WHERE e.id_torneo = (SELECT id_torneo FROM equipos WHERE id = ?) AND ep.participante = ?";
            PreparedStatement verificarStmt = conn.prepareStatement(verificarSQL);
            verificarStmt.setInt(1, equipoId);
            verificarStmt.setString(2, username);
            ResultSet rsVerificar = verificarStmt.executeQuery();

            if (rsVerificar.next() && rsVerificar.getInt(1) > 0) {
                // Usuario ya está registrado en un equipo del torneo
                jsonResponse.addProperty("error", "Ya estás registrado en un equipo de este torneo.");
            } else {
                // Registrar al usuario en el equipo
                String registrarSQL = "INSERT INTO equipo_participantes (equipo_id, participante) VALUES (?, ?)";
                PreparedStatement registrarStmt = conn.prepareStatement(registrarSQL);
                registrarStmt.setInt(1, equipoId);
                registrarStmt.setString(2, username);
                int rowsInserted = registrarStmt.executeUpdate();

                if (rowsInserted > 0) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("error", "No se pudo unir al equipo.");
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
