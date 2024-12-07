<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Administrador de Torneos</title>
        <link rel="icon" type="image/png" href="imagenes/logo.png" >
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link href="css/index.css" rel="stylesheet" type="text/css"/>
        <script src="js/index.js" type="text/javascript"></script>
    </head>
    <body>
        <!-- Barra de navegaci�n -->
        <nav class="navbar navbar-expand-lg">
            <a class="navbar-brand" href="index.jsp"><img src="imagenes/logo.png" alt="10%"/></a>
            <div class="ml-auto" id="loginButtons">
                <button class="btn btn-outline-light" data-toggle="modal" data-target="#loginModal">Iniciar Sesi�n</button>
                <button class="btn btn-outline-light ml-2" data-toggle="modal" data-target="#registerModal" id="registerButton">Registrarse</button>
            </div>
            <!-- Bot�n Circular de Usuario (Oculto por defecto) -->
            <div id="userButtonContainer" style="display: none;">
                <button class="btn btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span id="userButtonName"></span>
                </button>
                <!-- Popup Men� -->
                <div class="dropdown-menu dropdown-menu-right" id="userSettingsMenu">
                    <a class="dropdown-item" href="misTorneos.jsp">Mis Torneos</a>
                    <a class="dropdown-item" href="#">Configuraci�n de Cuenta (En Servicio)</a>
                    <a class="dropdown-item" href="#">Configuraci�n (En Servicio)</a>
                    <div class="dropdown-divider"></div>
                    <button class="dropdown-item" onclick="logoutUser()">Cerrar Sesi�n</button>
                </div>
            </div>
        </nav>
        <div class="sidebar" id="sidebar">
            <!-- Bot�n para expandir/plegar la barra -->
            <div class="sidebar-toggle" onclick="toggleSidebar()">
                <img src="imagenes/BurguerIcon.png" alt="Menu" class="menu-icon">
            </div>
            <!-- �conos de juegos -->
            <div class="sidebar-content">
                <div class="game-item">
                    <img src="imagenes/Rocket league.png" alt="Rocket League" class="game-icon">
                    <span class="game-name" id="Name-Game">Rocket League</span>
                </div>
                <div class="game-item">
                    <img src="imagenes/Smash-logo.png" alt="Super Smash Bros" class="game-icon">
                    <span class="game-name" id="Name-Game">Super Smash Bros</span>
                </div>
            </div>
        </div>
        <div class="main-content">
            <div id="torneosContainer">
                <!-- Contenedor para Rocket League -->
                <div id="rocketLeagueSection" class="game-section">
                    <h2>Rocket League</h2>
                    <div class="tournament-list" id="rocketLeagueTournaments">
                        <!-- Torneos de Rocket League se cargar�n aqu� -->
                    </div>
                </div>
                <!-- Contenedor para Super Smash Bros Ultimate -->
                <div id="smashBrosSection" class="game-section">
                    <h2>Super Smash Bros Ultimate</h2>
                    <div class="tournament-list" id="smashBrosTournaments">
                        <!-- Torneos de Super Smash Bros Ultimate se cargar�n aqu� -->
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal de Iniciar Sesión -->
        <div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="loginModalLabel">Iniciar Sesi�n</h5>
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
                                <label for="loginPassword">Contrase�a</label>
                                <input type="password" class="form-control" id="loginPassword" name="password" required>
                                <div class="invalid-feedback" id="loginPasswordError"></div>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="loginUser()">Iniciar Sesi�n</button>
                        </form>
                        <div id="loginResult"></div>
                        <div class="mt-3">
                            <a href="#" data-toggle="modal" data-target="#forgotPasswordModal" data-dismiss="modal">�Olvidaste tu contrase�a?</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal de Recuperaci�n de Contrase�a -->
        <div class="modal fade" id="forgotPasswordModal" tabindex="-1" role="dialog" aria-labelledby="forgotPasswordModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="forgotPasswordModalLabel">Recuperar Contrase�a</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="forgotPasswordForm">
                            <div class="form-group">
                                <label for="email">Correo Electr�nico</label>
                                <input type="email" class="form-control" id="emailp" name="email" required>
                                <div class="invalid-feedback" id="emailErrorp"></div>
                            </div>
                            <button type="button" class="btn btn-primary" data-toggle="modal"  onclick="sendResetCode()">Enviar C�digo</button>
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
                        <h5 class="modal-title" id="forgotPasswordModalLabel">Cambiar Contrase�a</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form id="resetPassword">
                            <div class="form-group">
                                <label for="resetCode">C�digo de Recuperaci�n</label>
                                <input type="text" class="form-control" id="resetCode" required>
                            </div>
                            <div class="form-group">
                                <label for="newPassword">Nueva Contrase�a</label>
                                <input type="password" class="form-control" id="newPassword" required>
                            </div>
                            <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="changePassword()">Cambiar Contrase�a</button>
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
                                <label for="email">Correo Electronico</label>
                                <input type="email" class="form-control" id="email" name="email" required>
                                <!-- Contenedor para mensaje de error del email -->
                                <div class="invalid-feedback" id="emailError"></div>
                            </div>
                            <div class="form-group">
                                <label for="password">Contrase�a</label>
                                <input type="password" class="form-control" id="password" name="password" required>
                                <!-- Contenedor para mensaje de error del contrase�a -->
                                <div class="invalid-feedback" id="passwordError"></div>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="registerUser()">Registrarse</button>
                        </form>
                        <div id="registerResult"></div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Scripts de Bootstrap y jQuery -->
        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.bundle.min.js"></script>
    </body>
</html>