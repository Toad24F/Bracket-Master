import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class ChangePasswordServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/usuarios_torneo"; // Cambia el nombre de la base de datos
    private static final String DB_USER = "Adrian";
    private static final String DB_PASSWORD = "Gatumadre12";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("resetCode");
        String resetCode = request.getParameter("resetCode");
        String newPassword = request.getParameter("newPassword");
        response.setContentType("text/plain");

        if (sessionCode == null || !sessionCode.equals(resetCode)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("El código de recuperación no es válido.");
            return;
        }

        // Encriptar la nueva contraseña
        String encryptedPassword = encryptPassword(newPassword);
        // Actualizar la contraseña en la base de datos
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE usuarios SET password = ? WHERE email = ?")) {
            
            String email = (String) session.getAttribute("email"); // Guarda el email en sesión al enviar el código
            System.out.println(email);
            statement.setString(1, encryptedPassword);
            statement.setString(2, email);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                response.getWriter().write("Contraseña cambiada con éxito.");
                session.removeAttribute("resetCode"); // Opcional: limpiar el código de recuperación
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Hubo un error al actualizar la contraseña.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error en la base de datos.");
        }
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }
}
