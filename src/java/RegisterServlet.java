import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
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
import javax.servlet.http.HttpSession;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8"); // Configura el Content-Type completo
        response.setCharacterEncoding("UTF-8"); // Establece la codificación

        PrintWriter out = response.getWriter();

        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String username = json.get("username").getAsString();
        String email = json.get("email").getAsString();
        String password = json.get("password").getAsString();
        JsonObject jsonResponse = new JsonObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12");

            // Comprobar si el nombre de usuario o el correo ya existen
            String checkQuery = "SELECT * FROM usuarios WHERE username = ? OR email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("username").equals(username)) {
                    jsonResponse.addProperty("error", "username");
                }
                if (rs.getString("email").equals(email)) {
                    jsonResponse.addProperty("error", "email");
                }
            } else {
                String hashedPassword = hashPassword(password);
                String sql = "INSERT INTO usuarios (username, email, password) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, hashedPassword);

                stmt.executeUpdate();
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                jsonResponse.addProperty("success", true);
            }

            out.print(jsonResponse.toString());
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", "server");
            out.print(jsonResponse.toString());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
