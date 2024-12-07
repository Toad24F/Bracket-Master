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

    if (hasError)
        return; // No envía el formulario si hay errores

    const formData = {username, email, password};
    console.log(formData);
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
function checkLoginStatus() {
    const username = localStorage.getItem('username');
    if (username) {
        document.getElementById('loginButtons').style.display = 'none';
        document.getElementById('userButtonContainer').style.display = 'block';
        document.getElementById('userButtonName').textContent = username;
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
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('expanded'); // Alterna la clase 'expanded' para expandir/plegar la barra
}

function cargarTorneos() {
    fetch('TorneosServlet') // Asegúrate de que el servlet devuelva torneos de ambos juegos
            .then(response => response.json())
            .then(data => {
                // Torneos para Rocket League
                const rocketLeagueTournaments = data.filter(t => t.juego === "Rocket League");
                mostrarTorneos(rocketLeagueTournaments, "rocketLeagueTournaments", "rocketLeagueSection");

                // Torneos para Super Smash Bros Ultimate
                const smashBrosTournaments = data.filter(t => t.juego === "Super Smash Bros Ultimate");
                mostrarTorneos(smashBrosTournaments, "smashBrosTournaments", "smashBrosSection");
            })
            .catch(error => console.error('Error al cargar los torneos:', error));
}

function mostrarTorneos(torneos, containerId, sectionId) {
    const container = document.getElementById(containerId);
    const section = document.getElementById(sectionId);
    // Limpiar contenido previo
    container.innerHTML = '';

    if (torneos.length === 0) {
        section.style.display = 'none'; // Ocultar la sección si no hay torneos
    } else {
        section.style.display = 'block'; // Mostrar la sección si hay torneos
        torneos.forEach(torneo => {
            const card = document.createElement('div');
            card.classList.add('tournament-card', 'card'); // Agrega clases de estilo

            // Redirigir al hacer clic
            card.onclick = () => {
                // Almacenar los datos en sessionStorage
                sessionStorage.setItem('torneoData', JSON.stringify({
                    idTorneo: torneo.id,
                    creador: torneo.creador
                }));

                // Crear el formulario para enviar nombre y juego
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = 'torneo.jsp';

                // Agregar los campos ocultos al formulario (nombre y juego)
                form.innerHTML = `
        <input type="hidden" name="torneo" value="${torneo.nombre}">
        <input type="hidden" name="juego" value="${torneo.juego}">
                <input type="hidden" name="creador" value="${torneo.creador}">
    `;

                // Agregar el formulario al body y enviarlo
                document.body.appendChild(form);
                form.submit();
            };



            // Información del torneo
            card.innerHTML = `
                <h3>${torneo.nombre}</h3>
                <div class="tournament-info">Creador: ${torneo.creador}</div>
                <div class="tournament-info">Participantes: ${torneo.usuariosReg}/${torneo.maxParticipantes}</div>
            `;
            container.appendChild(card);
        });
    }
}
document.addEventListener('DOMContentLoaded', function () {
    // Verifica si la URL actual corresponde a la raíz del servidor donde carga index.jsp
    if (window.location.pathname === "/Torneo/" || window.location.pathname === "/Torneo/index.jsp") {
        cargarTorneos();
    }
});
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
            // Mostrar el segundo modal (Modal de reinicio de contraseña)
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
