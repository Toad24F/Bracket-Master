package modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.Torneo;
public class TorneoDAO {
    // Método para obtener los torneos creados por un usuario específico
    public List<Torneo> obtenerTorneosPorCreador(String creador) throws ClassNotFoundException {
        List<Torneo> torneos = new ArrayList<>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12");) {
            String sql = "SELECT * FROM torneoscreados WHERE creador = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, creador);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Torneo torneo = new Torneo();
                torneo.setId(rs.getInt("id"));
                torneo.setNombre(rs.getString("nombre"));
                torneo.setJuego(rs.getString("juego"));
                torneo.setCreador(rs.getString("creador"));
                torneos.add(torneo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return torneos;
    }
}
