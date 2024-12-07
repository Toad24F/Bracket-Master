<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="icon" type="image/png" href="imagenes/logo.png" >
        <title>Torneo: <%=request.getParameter("torneo")%></title>
        <link rel="stylesheet" href="css/torneo.css">
        <link rel="stylesheet" href="css/jquery.bracket.min.css">
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <script src="js/torneo.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    </head>
    <body>
        <% String creadorSesion = (String) session.getAttribute("username"); %>
        <% String creadorTorneo = request.getParameter("creador");%>
        <!-- Barra de navegación -->
        <nav class="navbar">
            <a class="navbar-brand" href="index.jsp"><img src="imagenes/logo.png" alt="10%"/></a>
            <div id="Name_Torneo">Torneo: <%=request.getParameter("torneo")%></div>
            <div class="ml-auto" id="loginButtons">
                <button class="btn btn-outline-light" data-toggle="modal" data-target="#loginModal">Iniciar Sesión</button>
                <button class="btn btn-outline-light ml-2" data-toggle="modal" data-target="#registerModal" id="registerButton">Registrarse</button>
            </div>
            <!-- Botón Circular de Usuario (Oculto por defecto) -->
            <div id="userButtonContainer" style="display: none;">
                <button class="btn btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span id="userButtonName"></span>
                </button>
                <!-- Popup Menú -->
                <div class="dropdown-menu dropdown-menu-right" id="userSettingsMenu">
                    <a class="dropdown-item" href="misTorneos.jsp">Mis Torneos</a>
                    <a class="dropdown-item" href="#">Configuración de Cuenta (En Servicio)</a>
                    <a class="dropdown-item" href="#">Configuración (En Servicio)</a>
                    <div class="dropdown-divider"></div>
                    <button class="dropdown-item" onclick="logoutUser()">Cerrar Sesión</button>
                </div>
            </div>
        </nav>
        <div class="sidebar" id="sidebar">
            <!-- Botón para expandir/plegar la barra -->
            <div class="sidebar-toggle" onclick="toggleSidebar()">
                <img src="imagenes/BurguerIcon.png" alt="Menu" class="menu-icon">
            </div>
            <!-- Barra de herramientas -->
            <div class="sidebar-content">
                <div class="torneo-item" onclick="cargarDetallesTorneo()">
                    <img src="imagenes/Hogar.png" alt="Home" class="tool-icon">
                    <span class="tool-name">Casa</span>
                </div>
                <div class="torneo-item" onclick="Bracket()">
                    <img src="imagenes/bracket.png" alt="Bracket" class="tool-icon">
                    <span class="tool-name">Bracket</span>
                </div>
                <div class="torneo-item" onclick="Equipos()">
                    <img src="imagenes/Teams.png" alt="Equipos" class="tool-icon">
                    <span class="tool-name">Equipos</span>
                </div>
            </div>
        </div>
        <div class="Inicio">
            <div class="main-content">
                <div class="editable-card"><h3>Lugar: </h3> <div id="lugar"></div></div>
                <div class="editable-card"><h3>Fecha: </h3><div id="fecha"></div></div>
                <div class="editable-card"><h3>Estado: </h3> <div id="estado"></div></div>
                <div class="editable-card"><h3>Cupos: </h3> <div id="cupos"></div></div>
                <div class="editable-card"><h3>Reglas: </h3> <div id="reglas"></div></div>
                <div class="editable-card"><h3>Descripcion: </h3> <div id="descripcion"></div></div>
            </div>
            <!-- boton participar -->
            <div class="participar-container text-center mt-4" id="BotonDinamico">
                <button id="participarButton" class="btn-participar" data-toggle="modal" data-target="#participarModal">
                    <i class="fas fa-users"></i> Incripción
                </button>
            </div>
            <div class="participar-container text-center mt-4" id="BotonDinamico2">
                <% if (creadorSesion != null && creadorSesion.equals(creadorTorneo)) { %>
                <button id="participarButton" class="btn-participar" data-toggle="modal" data-target="#EmpezarModal">
                    <i class="fas fa-users"></i> Empezar
                </button>
                <% }%>
            </div>
        </div>
        <div id="equiposContainer" style="display: none">
            <h2 class="text-center mt-4">Equipos del Torneo</h2>
            <div class="equipos">
                <!-- Aquí se insertarán dinámicamente las tarjetas de equipos -->
            </div>
        </div>
        <div id="bracketcontenedor" class="bracket-material">
            <div id="seed">

                <% if (creadorSesion != null && creadorSesion.equals(creadorTorneo)) { %>
                <button id="participarButton" class="btn-participar" data-toggle="modal" data-target="#ConfirmarModal">
                    <i class="fas fa-users"></i> Generar Emparejamiento
                </button>
                <% }%>
            </div>
            <div id="bracket-scroll" style="overflow-x: auto; white-space: nowrap;">
                <div id="bracket-container" style="width: 500px; height: 600px;">
                    <!-- El ancho se ajusta dependiendo del tamaño del bracket -->
                </div>
            </div>
        </div>
        <!-- Modal para Confirmacion empezar torneo -->
        <div class="modal fade" id="EmpezarModal" tabindex="-1" role="dialog" aria-labelledby="EmpezarModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="ConfirmarModalLabel">¿Estas Seguro?</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="form-group">
                        <label for="nombreEquipo">Ya no se podran unir mas equipos</label>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" onclick="Empezar()">Empezar</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal para Confirmacion generacion de seeds -->
        <div class="modal fade" id="ConfirmarModal" tabindex="-1" role="dialog" aria-labelledby="ConfirmarModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="ConfirmarModalLabel">¿Estas Seguro?</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="form-group">
                        <label for="nombreEquipo">Se creara un nuevo emparejamiento</label>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" onclick="generarSeeds()">Generar</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Participar -->
        <div class="modal fade" id="participarModal" tabindex="-1" role="dialog" aria-labelledby="participarModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="participarModalLabel">Registrar Equipo</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="participarForm">
                            <div class="form-group">
                                <label for="nombreEquipo">Nombre del Equipo</label>
                                <input type="text" class="form-control" id="nombreEquipo" required>
                            </div>
                            <div class="form-group">
                                <label for="prefix">Prefijo del Equipo (3 Letras)</label>
                                <input type="text" class="form-control" id="prefix" maxlength="3" required>
                            </div>
                        </form>
                        <div id="participarResult" class="mt-3"></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" onclick="registrarEquipo()">Registrar Equipo</button>
                    </div>
                </div>
            </div>
        </div>


        <!-- Modal de Iniciar Sesión -->
        <div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="loginModalLabel">Iniciar Sesión</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="loginForm">
                            <div class="form-group">
                                <label for="loginUsername">Usuario</label>
                                <input type="text" class="form-control" id="loginUsername" name="username" required>
                                <div class="invalid-feedback" id="loginUsernameError"></div>
                            </div>
                            <div class="form-group">
                                <label for="loginPassword">Contraseña</label>
                                <input type="password" class="form-control" id="loginPassword" name="password" required>
                                <div class="invalid-feedback" id="loginPasswordError"></div>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="loginUser()">Iniciar Sesión</button>
                        </form>
                        <div id="loginResult"></div>
                        <div class="mt-3">
                            <a href="#" data-toggle="modal" data-target="#forgotPasswordModal" data-dismiss="modal">¿Olvidaste tu contraseña?</a>
                        </div>
                    </div>
                </div>
            </div>
        </div><!-- Modal de Recuperaci�n de Contraseña -->
        <div class="modal fade" id="forgotPasswordModal" tabindex="-1" role="dialog" aria-labelledby="forgotPasswordModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="forgotPasswordModalLabel">Recuperar Contraseña</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="forgotPasswordForm">
                            <div class="form-group">
                                <label for="email">Correo Electronico</label>
                                <input type="email" class="form-control" id="emailp" name="email" required>
                                <div class="invalid-feedback" id="emailErrorp"></div>
                            </div>
                            <button type="button" class="btn btn-primary" data-toggle="modal"  onclick="sendResetCode()">Enviar Código</button>
                        </form>
                        <div id="resetResult"></div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Formulario para ingresar el c�digo de recuperaci�n y la nueva contrase�a -->
        <div class="modal fade" id="reserPasswordModal" tabindex="-1" role="dialog" aria-labelledby="resetPasswordModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="forgotPasswordModalLabel">Cambiar Contraseña</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="resetPassword">
                            <div class="form-group">
                                <label for="resetCode">Codigo de Recuperación</label>
                                <input type="text" class="form-control" id="resetCode" required>
                            </div>
                            <div class="form-group">
                                <label for="newPassword">Nueva Contraseña</label>
                                <input type="password" class="form-control" id="newPassword" required>
                            </div>
                            <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="changePassword()">Cambiar Contraseña</button>
                        </form>
                        <div id="resetResult"></div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal de Registro -->
        <div class="modal fade" id="registerModal" tabindex="-1" role="dialog" aria-labelledby="registerModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="registerModalLabel">Registrarse</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="registerForm">
                            <div class="form-group">
                                <label for="username">Usuario</label>
                                <input type="text" class="form-control" id="username" name="username" required>
                                <!-- Contenedor para mensaje de error del username -->
                                <div class="invalid-feedback" id="usernameError"></div>
                            </div>
                            <div class="form-group">
                                <label for="email">Correo Electrónico</label>
                                <input type="email" class="form-control" id="email" name="email" required>
                                <!-- Contenedor para mensaje de error del email -->
                                <div class="invalid-feedback" id="emailError"></div>
                            </div>
                            <div class="form-group">
                                <label for="password">Contraseña</label>
                                <input type="password" class="form-control" id="password" name="password" required>
                                <!-- Contenedor para mensaje de error del contraseña -->
                                <div class="invalid-feedback" id="passwordError"></div>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="registerUser()">Registrarse</button>
                        </form>
                        <div id="registerResult"></div>
                    </div>
                </div>
            </div>
        </div>
        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.bundle.min.js"></script>
        <script src="js/jquery.bracket.min.js"></script>
    </body>
</html>