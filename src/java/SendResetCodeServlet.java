import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*; // Para manejar la base de datos

public class SendResetCodeServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/usuarios_torneo"; // Cambia el nombre de la base de datos
    private static final String DB_USER = "Adrian";
    private static final String DB_PASSWORD = "Gatumadre12";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        response.setContentType("text/plain");
        if (!isUserExists(email)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("El correo proporcionado no está registrado.");
            return;
        }
        // Generar un código aleatorio (6 dígitos)
        String code = generateRandomCode();
        // Guardar el código en sesión para validarlo después
        HttpSession session = request.getSession();
        session.setAttribute("resetCode", code);
        session.setAttribute("email", email);

        // Configuración del correo
        String host = "smtp.gmail.com";
        String port = "587";
        String senderEmail = "admntournament@gmail.com"; // Cambia esto
        String senderPassword = "xlkk nrpa pxlm rfao"; // Contraseña de aplicación
        String subject = "Código de Recuperación de Contraseña";
        String body = "Tu código de recuperación es: " + code;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Crear el mensaje
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(body);
            // Enviar el mensaje
            Transport.send(message);

            response.getWriter().write("Código de verificación enviado con éxito.");
        } catch (MessagingException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al enviar el correo.");
        }
    }

    // Función para verificar si el usuario existe en la base de datos
    private boolean isUserExists(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE email = ?")) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Si hay al menos un registro con el correo
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String generateRandomCode() {
        Random rand = new Random();
        int code = rand.nextInt(999999);
        return String.format("%06d", code); // Asegura 6 dígitos con ceros iniciales
    }
}
