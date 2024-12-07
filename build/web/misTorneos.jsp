<!DOCTYPE html>
<html lang="es">
    <%@ page import="java.time.LocalDate" %>
    <%
        if (session.getAttribute("username") == null) {
            response.sendRedirect("index.jsp");
            return; // Termina la ejecuciï¿½n de la pï¿½gina
        }
    %>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Mis Torneos</title>
        <link rel="stylesheet" href="css/misTorneos.css">
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    </head>
    <body>
        <!-- Barra de navegaciï¿½n -->
        <nav class="navbar">
            <a class="navbar-brand" href="index.jsp"><img src="imagenes/logo.png" alt="10%"/></a>
            <!-- Botï¿½n Circular de Usuario (Oculto por defecto) -->
            <div id="userButtonContainer" style="display: block;">
                <button class="btn btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span id="userButtonName"></span>
                </button>
                <!-- Popup Menï¿½ -->
                <div class="dropdown-menu dropdown-menu-right" id="userSettingsMenu">
                    <a class="dropdown-item" href="index.jsp">Inicio</a>
                    <a class="dropdown-item" href="#">Configuración de Cuenta (En Servicio)</a>
                    <a class="dropdown-item" href="#">Configuración (En Servicio)</a>
                    <div class="dropdown-divider"></div>
                    <button class="dropdown-item" onclick="logoutUserr()">Cerrar Sesión</button>
                </div>
            </div>
        </nav>
        <div class="container mt-5">
            <button class="btn btn-primary mt-3" onclick="openCreateTournamentModal()">Crear Torneo</button>
            <button class="btn btn-primary mt-3" onclick="window.location.href = 'index.jsp'">Regresar</button>
            <h1>Mis Torneos</h1>
            <!-- Tabla de torneos creados por el usuario -->
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Nombre del Torneo</th>
                        <th>Juego</th>

                    </tr>
                </thead>
                <tbody id="torneosContainer">
                    <!-- Los torneos se insertarï¿½n aquï¿½ dinï¿½micamente -->
                </tbody>
            </table>
        </div>

        <!-- Modal de Creaciï¿½n de Torneo -->
        <div class="modal fade" id="createTournamentModal" tabindex="-1" aria-labelledby="createTournamentModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="createTournamentModalLabel">Crear Torneo</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="createTournamentForm">
                            <div class="form-group">
                                <label for="tournamentName">Nombre del Torneo</label>
                                <input type="text" class="form-control" id="tournamentName" name="tournamentName" required>
                                <div id="tournamentError" class="text-danger"></div> <!-- Mensaje de error -->
                            </div>
                            <div class="form-group">
                                <label for="tournamentDescripcion">Descripcion</label>
                                <textarea class="form-control" id="tournamentDescripcion" name="tournamentDescripcion" rows="4" required></textarea>
                                <div id="tournamentError" class="text-danger"></div> <!-- Mensaje de error -->
                            </div>
                            <div class="form-group">
                                <label for="tournamentReglas">Reglas</label>
                                <textarea class="form-control" id="tournamentReglas" name="tournamentReglas" rows="4" required></textarea>
                                <div id="tournamentError" class="text-danger"></div> <!-- Mensaje de error -->
                            </div>
                            <div class="form-group">
                                <label for="tournamentLugar">Lugar del  torneo</label>
                                <input type="text" class="form-control" id="tournamentLugar" name="tournamentLugar" required>
                                <div id="tournamentError" class="text-danger"></div> <!-- Mensaje de error -->
                            </div>
                            <div class="form-group">
                                <label for="tournamentFecha">Fecha</label>
                                <%
                                    // Obtener la fecha actual desde el servidor
                                    LocalDate fechaActual = LocalDate.now();
                                %>
                                <input type="date" class="form-control" id="tournamentFecha" name="tournamentFecha"  min="<%= fechaActual%>"  required>
                                <div id="tournamentError" class="text-danger"></div> <!-- Mensaje de error -->
                            </div>
                            <div class="form-group">
                                <label for="game">Juego</label>
                                <select class="form-control" id="game" name="game" required>
                                    <option value="Rocket League">Rocket League</option>
                                    <option value="Super Smash Bros Ultimate">Super Smash Bros Ultimate</option>
                                    <!-- Agrega mï¿½s opciones de juegos aquï¿½ -->
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="numParticipantes">Número de Participantes</label>
                                <select class="form-control" id="numParticipantes" name="numParticipantes" required>
                                    <option value="8">8</option>
                                    <option value="16">16</option>
                                </select>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="createTournament()">Guardar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.bundle.min.js"></script>
        <script src="js/misTorneos.js"></script>
    </body>
</html>
