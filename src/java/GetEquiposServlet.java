import com.google.gson.JsonArray;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@WebServlet("/GetEquiposServlet")
public class GetEquiposServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        int id_torneo = Integer.parseInt(request.getParameter("id_torneo"));
        String username = request.getParameter("username");
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12")) {
            // Verificar si el usuario ya está en un equipo
            String usuarioEnEquipoSQL = "SELECT COUNT(*) FROM equipos e "
                + "JOIN equipo_participantes ep ON e.id = ep.equipo_id "
                + "WHERE e.id_torneo = ? AND ep.participante = ?";
            PreparedStatement usuarioStmt = conn.prepareStatement(usuarioEnEquipoSQL);
            usuarioStmt.setInt(1, id_torneo);
            usuarioStmt.setString(2, username);
            ResultSet rsUsuario = usuarioStmt.executeQuery();
            boolean usuarioEnEquipo = rsUsuario.next() && rsUsuario.getInt(1) > 0;

            // Obtener los equipos del torneo
            String equiposSQL = "SELECT e.id, e.equipo, e.Administrador FROM equipos e WHERE e.id_torneo = ?";
            PreparedStatement equiposStmt = conn.prepareStatement(equiposSQL);
            equiposStmt.setInt(1, id_torneo);
            ResultSet rsEquipos = equiposStmt.executeQuery();

            JsonArray equiposArray = new JsonArray();
            while (rsEquipos.next()) {
                JsonObject equipo = new JsonObject();
                equipo.addProperty("id", rsEquipos.getInt("id"));
                equipo.addProperty("nombre", rsEquipos.getString("equipo"));
                equipo.addProperty("administrador", rsEquipos.getString("Administrador"));

                // Obtener participantes del equipo
                String participantesSQL = "SELECT participante FROM equipo_participantes WHERE equipo_id = ?";
                PreparedStatement participantesStmt = conn.prepareStatement(participantesSQL);
                participantesStmt.setInt(1, rsEquipos.getInt("id"));
                ResultSet rsParticipantes = participantesStmt.executeQuery();

                JsonArray participantesArray = new JsonArray();
                while (rsParticipantes.next()) {
                    participantesArray.add(rsParticipantes.getString("participante"));
                }
                equipo.add("participantes", participantesArray);

                equiposArray.add(equipo);
            }

            jsonResponse.addProperty("success", true);
            jsonResponse.add("equipos", equiposArray);
            jsonResponse.addProperty("usuarioEnEquipo", usuarioEnEquipo);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", "Error al cargar los equipos.");
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
    
}
