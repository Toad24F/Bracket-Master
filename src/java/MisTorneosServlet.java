import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Torneo;
import modelo.TorneoDAO;
@WebServlet("/MisTorneosServlet")
public class MisTorneosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Obtener el usuario actual de la sesión
            HttpSession session = request.getSession();
            String creador = (String) session.getAttribute("username");
            // Consultar torneos creados por el usuario
            TorneoDAO torneoDAO = new TorneoDAO();
            List<Torneo> torneos = torneoDAO.obtenerTorneosPorCreador(creador);
            
            // Configurar la respuesta JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            // Convertir la lista de torneos a JSON y enviarla al cliente
            Gson gson = new Gson();
            String torneosJson = gson.toJson(torneos);
            PrintWriter out = response.getWriter();
            out.print(torneosJson);
            out.flush();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MisTorneosServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}