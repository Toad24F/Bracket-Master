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
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();
        JsonObject jsonResponse = new JsonObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12");

            // Verificar si el usuario existe
            String userCheckQuery = "SELECT password FROM usuarios WHERE username = ?";
            PreparedStatement userCheckStmt = conn.prepareStatement(userCheckQuery);
            userCheckStmt.setString(1, username);
            ResultSet rs = userCheckStmt.executeQuery();
            if (!rs.next()) {
                // Usuario no encontrado
                jsonResponse.addProperty("error", "user_not_found");
            } else {
                // Usuario encontrado, verificar la contraseña
                String storedPasswordHash = rs.getString("password");
                String enteredPasswordHash = hashPassword(password);

                if (!storedPasswordHash.equals(enteredPasswordHash)) {
                    // Contraseña incorrecta
                    jsonResponse.addProperty("error", "password_mismatch");
                } else {
                    // Credenciales válidas, guardar el usuario en la sesión
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    jsonResponse.addProperty("success", true);
                }
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
