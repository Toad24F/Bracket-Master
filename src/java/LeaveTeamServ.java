
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
import com.google.gson.JsonParser;
import java.sql.DriverManager;

@WebServlet("/LeaveTeamServ")
public class LeaveTeamServ extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String username = json.get("username").getAsString();
        int equipoId = json.get("equipoId").getAsInt();
        int id_torneo = json.get("id_torneo").getAsInt();
        String nombre_equipo = json.get("nombre_equipo").getAsString();
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/usuarios_torneo", "Adrian", "Gatumadre12")) {
            // Verificar si el usuario es el administrador del equipo
            String verificarAdminSQL = "SELECT Administrador FROM equipos WHERE id = ?";
            PreparedStatement verificarStmt = conn.prepareStatement(verificarAdminSQL);
            verificarStmt.setInt(1, equipoId);
            ResultSet rs = verificarStmt.executeQuery();

            if (rs.next()) {
                String administrador = rs.getString("Administrador");
                if (administrador.equals(username)) {
                    // Usuario es el administrador, eliminar el equipo y sus miembros
                    String borrarParticipantesSQL = "DELETE FROM equipo_participantes WHERE equipo_id = ?";
                    PreparedStatement borrarParticipantesStmt = conn.prepareStatement(borrarParticipantesSQL);
                    borrarParticipantesStmt.setInt(1, equipoId);
                    borrarParticipantesStmt.executeUpdate();

                    String borrarEquipoSQL = "DELETE FROM equipos WHERE id = ?";
                    PreparedStatement borrarEquipoStmt = conn.prepareStatement(borrarEquipoSQL);
                    borrarEquipoStmt.setInt(1, equipoId);
                    borrarEquipoStmt.executeUpdate();
                    try (Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12")) {
                        String nombreTabla = "";
                        String tabla = "SELECT tabla FROM torneoscreados WHERE id = ?";
                        try (PreparedStatement torneostatement = conn2.prepareStatement(tabla)) {
                            torneostatement.setInt(1, id_torneo);

                            // Ejecuta la consulta y procesa los resultados
                            try (ResultSet resultSet = torneostatement.executeQuery()) {
                                if (resultSet.next()) {
                                    // Obtén el valor de la columna "tabla"
                                    nombreTabla = resultSet.getString("tabla");
                                    System.out.println("Tabla encontrada: " + nombreTabla);
                                } else {
                                    System.out.println("No se encontró ningún registro con el id: " + id_torneo);
                                }
                            }
                        }
                        String eliminarquipotabla = "DELETE FROM " + nombreTabla + " WHERE participante  = ?";
                        String eliminarquiposeeds = "DELETE FROM seeds WHERE equipo  = ?";
                        String actualizarUsuariosSQL = "UPDATE torneoscreados SET usuariosReg = usuariosReg - 1 WHERE id = ?";
                        try (PreparedStatement torneostatement = conn2.prepareStatement(eliminarquipotabla)) {
                            torneostatement.setString(1, nombre_equipo);
                            torneostatement.executeUpdate();
                        }
                        try (PreparedStatement torneostatement = conn2.prepareStatement(eliminarquiposeeds)) {
                            torneostatement.setString(1, nombre_equipo);
                            torneostatement.executeUpdate();
                        }
                        try (PreparedStatement torneostatement = conn2.prepareStatement(actualizarUsuariosSQL)) {
                            torneostatement.setInt(1, id_torneo);
                            torneostatement.executeUpdate();
                        }
                        
                        
                    }

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "El equipo ha sido eliminado.");
                } else {
                    // Usuario no es el administrador, solo salir del equipo
                    String salirEquipoSQL = "DELETE FROM equipo_participantes WHERE equipo_id = ? AND participante = ?";
                    PreparedStatement salirStmt = conn.prepareStatement(salirEquipoSQL);
                    salirStmt.setInt(1, equipoId);
                    salirStmt.setString(2, username);
                    int rowsAffected = salirStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("message", "Te has salido del equipo.");
                    } else {
                        jsonResponse.addProperty("error", "No se pudo salir del equipo.");
                    }
                }
            } else {
                jsonResponse.addProperty("error", "El equipo no existe.");
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
