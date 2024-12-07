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
import java.sql.DriverManager;

@WebServlet("/CheckEquipoServlet")
public class CheckEquipoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        int torneo = Integer.parseInt(request.getParameter("id_torneo"));
        String username = request.getParameter("username");
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12")) {
            // Consulta para verificar si el usuario está en un equipo en el torneo
            String sql = "SELECT COUNT(*) FROM equipos e "
                       + "JOIN equipo_participantes ep ON e.id = ep.equipo_id "
                       + "WHERE e.id_torneo = ? AND ep.participante = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, torneo);
            stmt.setString(2, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                jsonResponse.addProperty("enEquipo", rs.getInt(1) > 0);
            } else {
                jsonResponse.addProperty("enEquipo", false);
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
