function openCreateTournamentModal() {
    $('#createTournamentModal').modal('show');
}

function createTournament() {
    const tournamentName = document.getElementById('tournamentName').value;
    const tournamentDescripcion = document.getElementById('tournamentDescripcion').value;
    const tournamentReglas = document.getElementById('tournamentReglas').value;
    const tournamentLugar = document.getElementById('tournamentLugar').value;
    const tournamentFecha = document.getElementById('tournamentFecha').value;
    const game = document.getElementById('game').value;
    const numParticipantes = parseInt(document.getElementById('numParticipantes').value);
    // Limpiar mensaje de error
    document.getElementById('tournamentError').textContent = '';
    // Validación de campos
    if (!tournamentName || !game || !numParticipantes || !tournamentDescripcion || !tournamentReglas || !tournamentLugar || !tournamentFecha) {
        alert("Por favor, completa todos los campos.");
        return;
    }
    // Enviar los datos del torneo al servlet
    fetch('CrearTorneoServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ tournamentName, tournamentDescripcion ,game, numParticipantes, tournamentReglas, tournamentLugar, tournamentFecha })
    })
    .then(response => response.json())
    .then(data => {
        if (data.exists) {
            // Mostrar mensaje de error si el torneo ya existe
            document.getElementById('tournamentError').textContent = "El torneo ya existe.";
        } else if (data.success) {
            $('#createTournamentModal').modal('hide'); // Cerrar el modal
            alert("Torneo creado exitosamente");
            location.reload();
        } else {
            alert("Hubo un error al crear el torneo.");
        }
    })
    .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    cargarTorneos();
});

function cargarTorneos() {
    fetch('MisTorneosServlet')
        .then(response => response.json())
        .then(torneos => {
            const torneosContainer = document.getElementById('torneosContainer');
            torneosContainer.innerHTML = ''; // Limpiar el contenido existente
            torneos.forEach(torneo => {
                const row = document.createElement('tr');
                const nombreCell = document.createElement('td');
                nombreCell.textContent = torneo.nombre;
                const juegoCell = document.createElement('td');
                juegoCell.textContent = torneo.juego;
                // Crear la celda de acciones y el botón de eliminar
                const accionesCell = document.createElement('td');
                const deleteButton = document.createElement('button');
                deleteButton.classList.add('btn', 'delete-btn');
                // Agregar la estructura del bote de basura con tapa
                deleteButton.innerHTML = `
                    <div class="trash-can">
                        <div class="trash-lid"></div>
                        <div class="trash-body"></div>
                    </div>
                `;
                deleteButton.onclick = () => eliminarTorneo(torneo.nombre, torneo.id);
                accionesCell.appendChild(deleteButton);
                row.appendChild(nombreCell);
                row.appendChild(juegoCell);
                row.appendChild(accionesCell);
                torneosContainer.appendChild(row);
            });
        })
        .catch(error => console.error('Error al cargar los torneos:', error));
}
function checkLoginStatus() {
    const username = localStorage.getItem('username');
    if (username) {
        document.getElementById('userButtonContainer').style.display = 'block';
        document.getElementById('userButtonName').textContent = username;
    }
}
// Ejecutar checkLoginStatus cuando se carga la página
document.addEventListener('DOMContentLoaded', checkLoginStatus);
function logoutUserr() {
    fetch('LogoutServlet', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Eliminar el nombre de usuario de localStorage
            localStorage.removeItem('username');
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
document.addEventListener('DOMContentLoaded', function() {
    const userButton = document.querySelector('.btn-circle');
    userButton.addEventListener('click', toggleUserMenu);
    document.addEventListener('click', closeUserMenuOnClickOutside);
});

function eliminarTorneo(nombreTorneo, idtorneo) {
    if (!confirm("¿Estás seguro de que deseas eliminar este torneo? Esta acción no se puede deshacer.")) {
        return;
    }

    fetch('EliminarTorneoServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ nombreTorneo, idtorneo })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert("Torneo eliminado exitosamente.");
            location.reload(); // Recargar la página para actualizar la lista de torneos
        } else {
            alert(data.message || "Hubo un error al eliminar el torneo.");
        }
    })
    .catch(error => console.error('Error:', error));
}
