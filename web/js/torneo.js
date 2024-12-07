//Registrar usuario}
const torneoData = JSON.parse(sessionStorage.getItem('torneoData'));

function registerUser() {
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Limpiar mensajes de error previos
    document.getElementById('username').classList.remove('is-invalid');
    document.getElementById('email').classList.remove('is-invalid');
    document.getElementById('usernameError').textContent = "";
    document.getElementById('emailError').textContent = "";

    // Validación en el cliente para campos vacíos
    let hasError = false;

    if (!username) {
        document.getElementById('username').classList.add('is-invalid');
        document.getElementById('usernameError').textContent = "El nombre de usuario no puede estar vac\u00EDo.";
        hasError = true;
    }
    // Validación del formato del correo electrónico
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    if (!email) {
        document.getElementById('email').classList.add('is-invalid');
        document.getElementById('emailError').textContent = "El correo no puede estar vac\u00EDo.";
        hasError = true;
    } else if (!emailRegex.test(email)) {
        document.getElementById('email').classList.add('is-invalid');
        document.getElementById('emailError').textContent = "Por favor, ingresa un correo electrónico válido.";
        hasError = true;
    }
    if (!password) {
        document.getElementById('password').classList.add('is-invalid');
        document.getElementById('passwordError').textContent = "La contrase\u00F1a no puede estar vac\u00EDa."; // Usamos una alerta para la contraseña
        hasError = true;
    }
    if (!password) {
        document.getElementById('password').classList.add('is-invalid');
        document.getElementById('passwordError').textContent = "La contrase\u00F1a no puede estar vac\u00EDa."; // Usamos una alerta para la contraseña
        hasError = true;
    }

    if (hasError)
        return; // No envía el formulario si hay errores

    const formData = {username, email, password};

    fetch('RegisterServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(formData)
    })
            .then(response => response.text())
            .then(text => {
                const data = JSON.parse(text);

                if (data.success) {
                    // Cerrar el modal
                    $('#registerModal').modal('hide');
                    location.reload();
                    // Guardar el nombre de usuario en localStorage
                    localStorage.setItem('username', username);

                    // Ocultar botones y mostrar botón circular
                    document.getElementById('loginButtons').style.display = 'none';
                    document.getElementById('userButtonContainer').style.display = 'block';
                    document.getElementById('userButtonName').textContent = username;
                } else {
                    // Mostrar mensajes específicos de duplicados
                    if (data.error === "username") {
                        document.getElementById('username').classList.add('is-invalid');
                        document.getElementById('usernameError').textContent = "El nombre de usuario ya est\u00E1 registrado.";
                    }
                    if (data.error === "email") {
                        document.getElementById('email').classList.add('is-invalid');
                        document.getElementById('emailError').textContent = "El correo ya est\u00E1 registrado.";
                    }
                }
            })
            .catch(error => console.error('Error:', error));
}
//Iniciar sesion
function loginUser() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    // Limpiar mensajes de error previos
    document.getElementById('loginUsername').classList.remove('is-invalid');
    document.getElementById('loginPassword').classList.remove('is-invalid');
    document.getElementById('loginUsernameError').textContent = "";
    document.getElementById('loginPasswordError').textContent = "";
    document.getElementById('loginResult').textContent = "";

    let hasError = false;

    // Validaciones en el cliente para campos vacíos
    if (!username) {
        document.getElementById('loginUsername').classList.add('is-invalid');
        document.getElementById('loginUsernameError').textContent = "El nombre de usuario no puede estar vac\u00EDo.";
        hasError = true;
    }

    if (!password) {
        document.getElementById('loginPassword').classList.add('is-invalid');
        document.getElementById('loginPasswordError').textContent = "La contrase\u00F1a no puede estar vac\u00EDa.";
        hasError = true;
    }

    // Si hay errores en los campos, no enviar el formulario
    if (hasError)
        return;

    const formData = {username, password};

    fetch('LoginServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(formData)
    })
            .then(response => response.text())
            .then(text => {
                const data = JSON.parse(text);

                if (data.success) {
                    // Cerrar el modal
                    $('#loginModal').modal('hide');

                    // Guardar el nombre de usuario en localStorage
                    localStorage.setItem('username', username);

                    // Ocultar botones y mostrar botón circular
                    document.getElementById('loginButtons').style.display = 'none';
                    document.getElementById('userButtonContainer').style.display = 'block';
                    document.getElementById('userButtonName').textContent = username;
                    // Recargar la página para reflejar los cambios
                    location.reload();
                } else {
                    // Manejo de errores específicos de inicio de sesión
                    if (data.error === "invalid") {
                        document.getElementById('loginUsername').classList.add('is-invalid');
                        document.getElementById('loginPassword').classList.add('is-invalid');
                        document.getElementById('loginResult').textContent = "Usuario o contrase\u00F1a incorrectos.";
                    } else if (data.error === "user_not_found") {
                        document.getElementById('loginUsername').classList.add('is-invalid');
                        document.getElementById('loginUsernameError').textContent = "El usuario no fue encontrado.";
                    } else if (data.error === "password_mismatch") {
                        document.getElementById('loginPassword').classList.add('is-invalid');
                        document.getElementById('loginPasswordError').textContent = "La contrase\u00F1a no coincide.";

                    }
                }
            })
            .catch(error => console.error('Error:', error));
}
//checar si estas logueado
function checkLoginStatus() {
    const username = localStorage.getItem('username');
    const participarButton = document.getElementById('participarButton'); // Obtén el botón por su ID
    const id_torneo = torneoData.idTorneo;// Obtener el nombre del torneo desde la URL
    if (username) {
        // Usuario está logueado
        document.getElementById('loginButtons').style.display = 'none';
        document.getElementById('userButtonContainer').style.display = 'block';
        document.getElementById('userButtonName').textContent = username;

        // Verificar si el usuario está en un equipo
        fetch(`CheckEquipoServlet?id_torneo=${encodeURIComponent(id_torneo)}&username=${encodeURIComponent(username)}`)
                .then(response => response.json())
                .then(data => {
                    if (data.enEquipo) {
                        // Si el usuario ya está en un equipo, deshabilitar el botón
                        if (participarButton) {
                            participarButton.disabled = true;
                            participarButton.title = "Ya estás registrado en un equipo.";
                        }
                    } else {
                        // Si el usuario no está en un equipo, habilitar el botón
                        if (participarButton) {
                            participarButton.disabled = false;
                            participarButton.title = ""; // Limpiar el mensaje
                        }
                    }
                })
                .catch(error => console.error('Error al verificar equipo:', error));
    } else {
        // Usuario no está logueado
        document.getElementById('loginButtons').style.display = 'block';
        document.getElementById('userButtonContainer').style.display = 'none';

        // Deshabilitar el botón "Participar"
        if (participarButton) {
            participarButton.disabled = true;
            participarButton.title = "Inicia sesión para participar en el torneo."; // Mensaje de ayuda
        }
    }
}
// Ejecutar checkLoginStatus cuando se carga la página
document.addEventListener('DOMContentLoaded', checkLoginStatus);
function logoutUser() {
    fetch('LogoutServlet', {
        method: 'POST'
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Eliminar el nombre de usuario de localStorage
                    localStorage.removeItem('username');
                    // Restaurar la interfaz
                    document.getElementById('loginButtons').style.display = 'block';
                    document.getElementById('userButtonContainer').style.display = 'none';
                    // Opcional: Redirigir a la página principal
                    window.location.href = 'index.jsp';
                }
            })
            .catch(error => console.error('Error al cerrar sesión:', error));
}
// Función para alternar la visibilidad del menú desplegable
function toggleUserMenu(event) {
    event.stopPropagation(); // Detener la propagación para que el menú no se cierre inmediatamente
    const userMenu = document.getElementById('userSettingsMenu');
    // Alternar la visibilidad del menú
    if (userMenu.style.display === 'block') {
        userMenu.style.display = 'none';
    } else {
        userMenu.style.display = 'block';
    }
}
// Ocultar el menú cuando se hace clic fuera de él
function closeUserMenuOnClickOutside(event) {
    const userMenu = document.getElementById('userSettingsMenu');
    const userButton = document.querySelector('.btn-circle');

    // Ocultar el menú si el clic fue fuera del botón y del menú
    if (userMenu.style.display === 'block' && !userMenu.contains(event.target) && !userButton.contains(event.target)) {
        userMenu.style.display = 'none';
    }
}
// Añadir listeners al documento y al botón circular
document.addEventListener('DOMContentLoaded', function () {
    const userButton = document.querySelector('.btn-circle');
    userButton.addEventListener('click', toggleUserMenu);
    document.addEventListener('click', closeUserMenuOnClickOutside);
});
//cambiar la barra lateral
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('expanded'); // Alterna la clase 'expanded' para expandir/plegar la barra
}

document.addEventListener('DOMContentLoaded', () => {
    cargarDetallesTorneo();
});
function cargarDetallesTorneo() {
    cargarEquipos();
    document.getElementById('equiposContainer').style.display = 'none';
    document.querySelector('.main-content').style.display = 'flex';
    document.getElementById('bracketcontenedor').style.display = 'none';
    document.getElementById('participarButton').style.display = 'block';
    const torneo = torneoData.idTorneo; // Suponiendo que el ID viene en la URL
    fetch(`TorneoDetallesServlet?torneo=${torneo}`)
            .then(response => response.json())
            .then(data => {
                const participantes = `${data.usuariosREG}/${data.numparticipantes}`;
                //document.getElementById('torneoNombre').textContent = data.nombre;
                document.getElementById('descripcion').textContent = data.descripcion;
                document.getElementById('reglas').textContent = data.reglas;
                document.getElementById('estado').textContent = data.estado;
                document.getElementById('lugar').textContent = data.lugar;
                document.getElementById('fecha').textContent = data.fecha;
                document.getElementById('cupos').textContent = participantes;

                const usuarioSesion = localStorage.getItem('username');
                if (usuarioSesion === data.creador) {
                    habilitarEdicion();
                }
                if (data.estado === 'Iniciado') {
                    Iniciado();
                }
            })
            .catch(error => console.error('Error:', error));
}

function Iniciado() {
    document.getElementById('BotonDinamico').style.display = 'none';
    document.getElementById('BotonDinamico2').style.display = 'none';
}

function habilitarEdicion() {
    // Añadir icono de lápiz para cada sección editable
    ['descripcion', 'reglas', 'lugar', 'fecha'].forEach(campo => {
        const elemento = document.getElementById(campo);
        const iconoEditar = document.createElement('i');
        iconoEditar.classList.add('fas', 'fa-pencil-alt');
        iconoEditar.style.cursor = 'pointer';
        iconoEditar.onclick = () => activarModoEdicion(campo);
        elemento.appendChild(iconoEditar);
    });
}

function activarModoEdicion(campo) {
    const elemento = document.getElementById(campo);
    const contenidoActual = elemento.textContent;
    const input = document.createElement('textarea');
    input.value = contenidoActual;
    elemento.innerHTML = '';
    elemento.appendChild(input);
    const iconoGuardar = document.createElement('i');
    iconoGuardar.classList.add('fas', 'fa-check');
    iconoGuardar.style.cursor = 'pointer';

    iconoGuardar.onclick = () => guardarCambio(campo, input.value);
    elemento.appendChild(iconoGuardar);
}

function guardarCambio(campo, valor) {
    const torneo = getParameterByName('torneo');
    const payload = {
        torneo,
        [campo]: valor
    };

    fetch('TorneoDetallesServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById(campo).textContent = valor;
                    location.reload();
                } else {
                    alert('Hubo un error al guardar el cambio.');
                }
            })
            .catch(error => console.error('Error:', error));
}

// Utilidad para obtener parámetros de la URL
function getParameterByName(name) {
    const url = new URL(window.location.href);
    return url.searchParams.get(name);
}

function registrarEquipo() {
    const nombreEquipo = document.getElementById('nombreEquipo').value;
    const prefix = document.getElementById('prefix').value.toUpperCase();
    const username = localStorage.getItem('username');
    const torneo = getParameterByName('torneo');
    const id_torneo = torneoData.idTorneo;
    if (!nombreEquipo || prefix.length !== 3) {
        alert("Por favor, completa los campos correctamente.");
        return;
    }

    const formData = {
        torneo_nombre: torneo,
        nombre_equipo: nombreEquipo,
        id_torneo: id_torneo,
        username
    };
    fetch('RegisterTeamServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("¡Equipo registrado exitosamente!");
                    $('#participarModal').modal('hide');
                    location.reload();
                } else {
                    alert(data.error || "Ocurrió un error.");
                }
            })
            .catch(error => console.error('Error:', error));
}

function Equipos() {
    // Ocultar otros contenidos
    document.querySelector('.main-content').style.display = 'none';
    document.getElementById('equiposContainer').style.display = 'block';
    document.getElementById('bracketcontenedor').style.display = 'none';
    document.getElementById('participarButton').style.display = 'block';
}

function cargarEquipos() {
    const id_torneo = torneoData.idTorneo;// Obtenemos el nombre del torneo desde la URL
    const username = localStorage.getItem('username');
    fetch(`GetEquiposServlet?id_torneo=${encodeURIComponent(id_torneo)}&username=${encodeURIComponent(username)}`)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    renderEquipos(data.equipos, data.usuarioEnEquipo);

                } else {
                    alert("Error al cargar los equipos.");
                }
            })
            .catch(error => console.error('Error:', error));
}

function renderEquipos(equipos, usuarioEnEquipo) {
    const equiposList = document.querySelector('.equipos');
    const estado = document.getElementById('estado').textContent;
    equiposList.innerHTML = ''; // Limpiar contenido previo
    equipos.forEach(equipo => {
        const tarjeta = document.createElement('div');
        tarjeta.classList.add('teams-card');

        tarjeta.innerHTML = `
            <h4 class="card-tittle2">${equipo.nombre}</h4>
            <p class="card-text"><strong>Administrador:</strong> ${equipo.administrador}</p>
            <p class="card-text"><strong>Miembros:</strong> ${equipo.participantes.join(', ')}</p>
            <div id="BotonDinamico">
                ${
                estado === "Iniciado" ? '' : // Si el estado es "Iniciado", no mostrar los botones
                !usuarioEnEquipo
                ? `<button class="btn btn-primary btn-sm" id="BotonDinamico" onclick="unirseEquipo('${equipo.id}')">Unirse</button>`
                : ''
                }
                ${
                estado === "Iniciado" ? '' : // Si el estado es "Iniciado", no mostrar los botones
                equipo.participantes.includes(localStorage.getItem('username'))
                ? `<button class="btn btn-danger btn-sm" id="BotonDinamico" onclick="salirEquipo('${equipo.id}','${equipo.nombre}')">
                        ${equipo.administrador === localStorage.getItem('username') ? 'Eliminar Equipo' : 'Salir del Equipo'}
                      </button>`
                : ''
                }
            </div>
        `;

        equiposList.appendChild(tarjeta);
    });
}

function unirseEquipo(equipoId) {
    const username = localStorage.getItem('username');

    // Verificar si el usuario está logueado
    if (!username) {
        alert("Debes iniciar sesión para unirte a un equipo.");
        return; // Salir de la función si no está logueado
    }
    fetch(`JoinTeamServlet`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({equipoId, username})
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Te has unido al equipo exitosamente.");
                    location.reload();
                } else {
                    alert(data.error || "No se pudo unir al equipo.");
                }
            })
            .catch(error => console.error('Error:', error));
}

function salirEquipo(equipoId, nombre_equipo) {
    const username = localStorage.getItem('username');
    const id_torneo = torneoData.idTorneo;
    fetch('LeaveTeamServ', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({equipoId, username, id_torneo, nombre_equipo})
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert(data.error || "No se pudo salir del equipo.");
                }
            })
            .catch(error => console.error('Error:', error));
}
function Bracket() {
    document.querySelector('.main-content').style.display = 'none';
    document.getElementById('equiposContainer').style.display = 'none';
    document.getElementById('bracketcontenedor').style.display = 'flex';
    document.getElementById('participarButton').style.display = 'none';
    cargarBracket();
}
function generarSeeds() {
    const idTorneo = torneoData.idTorneo;
    fetch(`GenerateSeedsServlet?id_torneo=${encodeURIComponent(idTorneo)}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({id_torneo: idTorneo})
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    $('#ConfirmarModal').modal('hide');
                    alert(data.message);
                    Bracket();
                } else {
                    alert(data.error || "No se pudieron generar las seeds.");
                }
            })
            .catch(error => console.error('Error:', error));


}
let bracketData = null; // Inicialización

function cargarBracket() {
    const idTorneo = torneoData.idTorneo; // Obtiene el ID del torneo desde la URL
    const creadorTorneo = torneoData.creador; // Obtiene el creador desde el parámetro de la URL
    const username = localStorage.getItem('username');
    // Variable para saber si el usuario puede modificar el bracket
    const esAdministrador = username === creadorTorneo;

    $.ajax({
        url: `GetBracketDataServlet?id_torneo=${encodeURIComponent(idTorneo)}`,
        method: 'GET',
        dataType: 'json',
        success: function (bracketData) {

            // Inicializa el bracket con los datos obtenidos
            $('#bracket-container').bracket({
                init: {
                    teams: bracketData.teams, // Equipos iniciales
                    results: bracketData.results // Resultados iniciales
                },
                save: esAdministrador
                        ? function (data) {
                            // Construir la estructura para enviar al servidor
                            const payload = {
                                tournamentId: idTorneo,
                                results: data.results.map(round =>
                                    round.map(match => match.map(score => (score === null ? 0 : score)))
                                )
                            };
                            $.ajax({
                                url: 'SaveBracketServ',
                                method: 'POST',
                                contentType: 'application/json',
                                data: JSON.stringify(payload),
                                success: function (response) {
                                    console.log('Bracket results saved successfully:', response);
                                },
                                error: function (error) {
                                    console.error('Error saving bracket results:', error);
                                }
                            });
                        }
                : null // Si no es administrador, no permite guardar
            });
        },
        error: function (error) {
            console.error('Error loading bracket data:', error);
        }
    });
}

// Función para obtener parámetros desde la URL
function getParameterByName(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// Función para obtener parámetros desde la URL
function getParameterByName(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}




function Empezar() {
    const idTorneo = torneoData.idTorneo;
    fetch(`EmpezarServlet?id_torneo=${encodeURIComponent(idTorneo)}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({id_torneo: idTorneo})
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    $('#EmpezarModal').modal('hide');
                    alert(data.message);
                    location.reload();
                } else {
                    alert(data.error || "No se pudieron generar las seeds.");
                }
            })
            .catch(error => console.error('Error:', error));


}
// Función para mostrar el formulario de recuperación de contraseña
function showForgotPasswordForm() {
    document.getElementById("forgotPasswordForm").style.display = "block";
    document.getElementById("loginModal").style.display = "none";  // Ocultar el formulario de login
}

// Función para enviar el código de verificación al correo
function sendResetCode() {
    var email = document.getElementById("emailp").value;

    let hasError = false;
    // Validación del formato del correo electrónico
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    if (!email) {
        document.getElementById('emailp').classList.add('is-invalid');
        document.getElementById('emailErrorp').textContent = "El correo no puede estar vac\u00EDo.";
        hasError = true;
    } else if (!emailRegex.test(email)) {
        document.getElementById('emailp').classList.add('is-invalid');
        document.getElementById('emailErrorp').textContent = "Por favor, ingresa un correo electrónico válido.";
        hasError = true;
    }
    if (hasError)
        return; // No envía el formulario si hay errores


    $.ajax({
        url: `SendResetCodeServlet?email=${encodeURIComponent(email)}`, // Servlet para enviar el código al correo
        method: 'POST',
        data: {email: email},
        success: function (response) {
            $('#forgotPasswordModal').modal('hide');
            $('#reserPasswordModal').modal('show');
            alert("Código de verificación enviado al correo.");
            // Mostrar una pantalla para ingresar el código recibido
            // Puedes redirigir a una nueva página o mostrar un nuevo campo aquí para que ingresen el código.
            // Mostrar el formulario de cambio de contraseña
            document.getElementById("emailp").value = "";
        },
        error: function (xhr) {
            if (xhr.status === 400) {
                alert("El correo proporcionado no está registrado.");
            } else {
                alert("Hubo un error al enviar el código. Por favor intenta nuevamente.");
            }
        }
    });
}
function changePassword() {
    var resetCode = document.getElementById("resetCode").value;
    var newPassword = document.getElementById("newPassword").value;

    if (!resetCode || !newPassword) {
        alert("Por favor completa todos los campos.");
        return;
    }

    $.ajax({
        url: 'ChangePasswordServlet', // Servlet para cambiar la contraseña
        method: 'POST',
        data: {
            resetCode: resetCode,
            newPassword: newPassword
        },
        success: function (response) {
            alert(response); // Mensaje de éxito
            location.reload(); // Opcional: recargar la página
            document.getElementById("newPassword").value = "";
            document.getElementById("resetCode").value = "";
        },
        error: function (xhr) {
            if (xhr.status === 400) {
                alert("El código de recuperación no es válido.");
            } else {
                alert("Hubo un error al cambiar la contraseña. Intenta nuevamente.");
            }
        }
    });
}
