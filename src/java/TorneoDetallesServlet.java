import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/TorneoDetallesServlet")
public class TorneoDetallesServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        
        int torneo = Integer.parseInt(request.getParameter("torneo")); // Obtener el ID del torneo
        JsonObject jsonResponse = new JsonObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12");
            String query = "SELECT nombre, descripcion, reglas, lugar, fecha, creador, numparticipantes, usuariosREG, estado FROM torneoscreados WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, torneo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                jsonResponse.addProperty("nombre", rs.getString("nombre"));
                jsonResponse.addProperty("descripcion", rs.getString("descripcion"));
                jsonResponse.addProperty("reglas", rs.getString("reglas"));
                jsonResponse.addProperty("lugar", rs.getString("lugar"));
                jsonResponse.addProperty("fecha", rs.getDate("fecha").toString());
                jsonResponse.addProperty("creador", rs.getString("creador"));
                jsonResponse.addProperty("numparticipantes", rs.getString("numparticipantes"));
                jsonResponse.addProperty("usuariosREG", rs.getString("usuariosREG"));
                jsonResponse.addProperty("estado", rs.getString("estado"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Establecer el tipo de respuesta como JSON
    response.setContentType("application/json; charset=UTF-8");

    // Obtener el cuerpo de la solicitud JSON
    JsonObject json = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    String torneo = json.get("torneo").getAsString();

    // Variables para los campos de torneo
    String descripcion = json.has("descripcion") ? json.get("descripcion").getAsString() : null;
    String reglas = json.has("reglas") ? json.get("reglas").getAsString() : null;
    String lugar = json.has("lugar") ? json.get("lugar").getAsString() : null;
    Date fecha = json.has("fecha") ? Date.valueOf(json.get("fecha").getAsString()) : null;

    boolean success = false;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/torneos", "Adrian", "Gatumadre12");

        // Construir la consulta de actualización dinámica
        StringBuilder updateQuery = new StringBuilder("UPDATE torneoscreados SET ");
        boolean firstField = true;

        // Agregar las columnas que no son nulas
        if (descripcion != null) {
            if (!firstField) updateQuery.append(", ");
            updateQuery.append("descripcion = ?");
            firstField = false;
        }
        if (reglas != null) {
            if (!firstField) updateQuery.append(", ");
            updateQuery.append("reglas = ?");
            firstField = false;
        }
        if (lugar != null) {
            if (!firstField) updateQuery.append(", ");
            updateQuery.append("lugar = ?");
            firstField = false;
        }
        if (fecha != null) {
            if (!firstField) updateQuery.append(", ");
            updateQuery.append("fecha = ?");
        }

        // Agregar la cláusula WHERE
        updateQuery.append(" WHERE nombre = ?");

        // Preparar la consulta
        PreparedStatement stmt = conn.prepareStatement(updateQuery.toString());

        int parameterIndex = 1;
        // Asignar los valores de los campos modificados
        if (descripcion != null) stmt.setString(parameterIndex++, descripcion);
        if (reglas != null) stmt.setString(parameterIndex++, reglas);
        if (lugar != null) stmt.setString(parameterIndex++, lugar);
        if (fecha != null) stmt.setDate(parameterIndex++, fecha);

        // Establecer el nombre del torneo
        stmt.setString(parameterIndex, torneo);

        // Ejecutar la actualización
        int rowsUpdated = stmt.executeUpdate();
        success = rowsUpdated > 0;
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Preparar la respuesta
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("success", success);
    PrintWriter out = response.getWriter();
    out.print(jsonResponse.toString());
    out.flush();
}

}
