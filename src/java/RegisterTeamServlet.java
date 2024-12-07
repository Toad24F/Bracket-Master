import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/RegisterTeamServlet")
public class RegisterTeamServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String equipo = json.get("nombre_equipo").getAsString();
        String administrador = json.get("username").getAsString();
        int id_torneo = json.get("id_torneo").getAsInt();
        JsonObject jsonResponse = new JsonObject();

        try (Connection connTorneos = DatabaseConnectionFactory.getTorneosConnection();
             Connection connUsuariosTorneo = DatabaseConnectionFactory.getUsuariosTorneoConnection()) {

            // Validar si el usuario ya está registrado en un equipo para este torneo
            String validacionSQL = "SELECT e.id FROM equipos e "
                    + "JOIN equipo_participantes ep ON e.id = ep.equipo_id "
                    + "WHERE ep.participante = ? AND e.id_torneo = ?";
            PreparedStatement validacionStmt = connUsuariosTorneo.prepareStatement(validacionSQL);
            validacionStmt.setString(1, administrador);
            validacionStmt.setInt(2, id_torneo);
            ResultSet rs = validacionStmt.executeQuery();

            if (rs.next()) {
                jsonResponse.addProperty("error", "Ya estás registrado en un equipo para este torneo.");
            } else {
                // Obtener el nombre de la tabla específica del torneo
                String obtenerTablaSQL = "SELECT tabla, usuariosReg, numparticipantes FROM torneoscreados WHERE id = ?";
                PreparedStatement stmtObtenerTabla = connTorneos.prepareStatement(obtenerTablaSQL);
                stmtObtenerTabla.setInt(1, id_torneo);
                ResultSet rsTabla = stmtObtenerTabla.executeQuery();

                if (rsTabla.next()) {
                    String nombreTabla = rsTabla.getString("tabla");
                    int usuariosRegistrados = rsTabla.getInt("usuariosReg");
                    int numMaxParticipantes = rsTabla.getInt("numparticipantes");

                    // Verificar si aún hay espacio en el torneo
                    if (usuariosRegistrados >= numMaxParticipantes) {
                        jsonResponse.addProperty("error", "El torneo ya está lleno.");
                    } else {
                        // Insertar el equipo en la tabla del torneo
                        String insertarEquipoSQL = "INSERT INTO " + nombreTabla + " (participante) VALUES (?)";
                        PreparedStatement stmtInsertarEquipo = connTorneos.prepareStatement(insertarEquipoSQL);
                        stmtInsertarEquipo.setString(1, equipo);
                        stmtInsertarEquipo.executeUpdate();

                        // Incrementar el valor de usuariosReg en torneoscreados
                        String actualizarUsuariosSQL = "UPDATE torneoscreados SET usuariosReg = usuariosReg + 1 WHERE id = ?";
                        PreparedStatement stmtActualizarUsuarios = connTorneos.prepareStatement(actualizarUsuariosSQL);
                        stmtActualizarUsuarios.setInt(1, id_torneo);
                        stmtActualizarUsuarios.executeUpdate();

                        // Registrar el equipo y administrador en la tabla equipos
                        String registrarEquipoSQL = "INSERT INTO equipos (id_torneo, equipo, Administrador) VALUES (?, ?, ?)";
                        PreparedStatement stmtRegistrarEquipo = connUsuariosTorneo.prepareStatement(registrarEquipoSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                        stmtRegistrarEquipo.setInt(1, id_torneo);
                        stmtRegistrarEquipo.setString(2, equipo);
                        stmtRegistrarEquipo.setString(3, administrador);
                        stmtRegistrarEquipo.executeUpdate();

                        // Obtener el ID del equipo recién creado
                        ResultSet rsEquipo = stmtRegistrarEquipo.getGeneratedKeys();
                        if (rsEquipo.next()) {
                            int equipoId = rsEquipo.getInt(1);

                            // Registrar el administrador como participante en la tabla equipo_participantes
                            String registrarParticipanteSQL = "INSERT INTO equipo_participantes (id_torneo, equipo_id, participante) VALUES (?, ?, ?)";
                            PreparedStatement stmtRegistrarParticipante = connUsuariosTorneo.prepareStatement(registrarParticipanteSQL);
                            stmtRegistrarParticipante.setInt(1, id_torneo);
                            stmtRegistrarParticipante.setInt(2, equipoId);
                            stmtRegistrarParticipante.setString(3, administrador);
                            stmtRegistrarParticipante.executeUpdate();

                            jsonResponse.addProperty("success", true);
                        } else {
                            jsonResponse.addProperty("error", "No se pudo registrar el equipo.");
                        }
                    }
                } else {
                    jsonResponse.addProperty("error", "El torneo no existe.");
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
public class DatabaseConnectionFactory {
    public static Connection getTorneosConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12");
    }

    public static Connection getUsuariosTorneoConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12");
    }
}
}
