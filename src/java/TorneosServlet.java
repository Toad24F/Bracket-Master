
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

// Clase para representar un torneo
class Torneo {
    private int id;
    private String nombre;
    private String juego;
    private String creador;
    private int usuariosReg;
    private int maxParticipantes;

    public Torneo(int id, String nombre, String juego, String creador, int usuariosReg, int maxParticipantes) {
        this.id = id;
        this.nombre = nombre;
        this.juego = juego;
        this.creador = creador;
        this.usuariosReg = usuariosReg;
        this.maxParticipantes = maxParticipantes;
    }
}

@WebServlet("/TorneosServlet")
public class TorneosServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Conectar a la base de datos y obtener los torneos
        List<Torneo> torneos = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12");
            String query = "SELECT id, nombre, juego, creador, usuariosReg, numparticipantes FROM torneoscreados";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id =rs.getInt("id");
                String nombre = rs.getString("nombre");
                String juego = rs.getString("juego");
                String creador = rs.getString("creador");
                int usuariosReg = rs.getInt("usuariosReg");
                int numParticipantes = rs.getInt("numparticipantes"); // Cambiado de maxParticipantes a numparticipantes

                Torneo torneo = new Torneo(id, nombre, juego, creador, usuariosReg, numParticipantes);
                torneos.add(torneo);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convertir la lista de torneos a JSON y enviarla al cliente
        Gson gson = new Gson();
        String json = gson.toJson(torneos);
        out.print(json);
        out.flush();
    }
}
