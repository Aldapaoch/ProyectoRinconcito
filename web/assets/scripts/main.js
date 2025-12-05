document.addEventListener("DOMContentLoaded", () => {
  const hoy = new Date();
  const maxFecha = new Date();
  maxFecha.setFullYear(hoy.getFullYear() + 2);

  const fechaInput = document.getElementById("fecha");
  const horaInput = document.getElementById("hora");

  if (fechaInput) {
    // convertir a (YYYY-MM-DD)
    const localHoy = new Date();
    const offset = localHoy.getTimezoneOffset();
    const localISO = new Date(localHoy.getTime() - offset * 60000).toISOString().split("T")[0];
    const maxISO = new Date(maxFecha.getTime() - offset * 60000).toISOString().split("T")[0];
    fechaInput.max = maxISO;
     fechaInput.min = localISO; 
  }

  function actualizarHorasDisponibles() {
  if (!fechaInput || !horaInput) return;

  const fechaSeleccion = fechaInput.value;
  const hoy = new Date();
  const yyyy = hoy.getFullYear();
  const mm = String(hoy.getMonth() + 1).padStart(2, "0");
  const dd = String(hoy.getDate()).padStart(2, "0");
  const hoyStr = `${yyyy}-${mm}-${dd}`;

  [...horaInput.options].forEach(opt => opt.disabled = false);

  if (fechaSeleccion === hoyStr) {
    const ahora = new Date();
    const minutosActuales = ahora.getHours() * 60 + ahora.getMinutes();

    [...horaInput.options].forEach(opt => {
      if (opt.value) {
        const [h, m] = opt.value.split(":").map(Number);
        const minutosOpcion = h * 60 + m;

        if (minutosOpcion <= minutosActuales) {
          opt.disabled = true;
        }
      }
    });
  }
}

  fechaInput?.addEventListener("change", actualizarHorasDisponibles);

  actualizarHorasDisponibles();
});

/* CONTROL DE MESAS */
// reservas locales 
let reservas = loadReservasFromStorage();

function horaToMinutos(hora) {
  const [h, m] = hora.split(":").map(Number);
  return h * 60 + m;
}

function parseFechaHoraToDate(fechaISO, horaHHMM) {
  const [yyyy, mm, dd] = fechaISO.split("-").map(Number);
  const [hh, min] = horaHHMM.split(":").map(Number);
  return new Date(yyyy, mm - 1, dd, hh, min, 0, 0);
}

function liberarMesas(fecha, hora) {
  const ahora = new Date();
  reservas = reservas.filter(r => {
    if (!r.fecha || !r.hora) return false;
    if (fecha && r.fecha !== fecha) return true;
    const fechaReserva = parseFechaHoraToDate(r.fecha, r.hora);
    const expiracion = new Date(fechaReserva.getTime() + 90 * 60000); // +90 min
    return ahora < expiracion;
  });
  saveReservasToStorage();
}

function limpiarReservasExpiradas() {
  const ahora = new Date();
  reservas = reservas.filter(r => {
    if (!r.fecha || !r.hora) return false;
    const fechaReserva = parseFechaHoraToDate(r.fecha, r.hora);
    const expiracion = new Date(fechaReserva.getTime() + 90 * 60000); 
    return ahora < expiracion;
  });
  saveReservasToStorage();
}

setInterval(() => {
  limpiarReservasExpiradas();
}, 60 * 1000);

function getMesasDisponibles(personas) {
  if (personas <= 4) return { inicio: 1, fin: 10 };
  if (personas <= 8) return { inicio: 11, fin: 22 };
  return { inicio: 23, fin: 37 };
}

function formatearFecha(fechaISO) {
  const [yyyy, mm, dd] = fechaISO.split("-");
  return `${dd}/${mm}/${yyyy}`;
}

function loadReservasFromStorage() {
  try {
    const raw = localStorage.getItem("reservasLocales");
    const arr = raw ? JSON.parse(raw) : [];
  
    const now = new Date();
    const filtered = arr.filter(r => {
      if (!r || !r.fecha || !r.hora) return false;
      const fechaReserva = parseFechaHoraToDate(r.fecha, r.hora);
      const expiracion = new Date(fechaReserva.getTime() + 90 * 60000);
      return now < expiracion;
    });
    // sincroniza
    if (filtered.length !== (arr ? arr.length : 0)) {
      localStorage.setItem("reservasLocales", JSON.stringify(filtered));
    }
    return filtered;
  } catch (e) {
    console.error("Error leyendo reservasLocales:", e);
    return [];
  }
}

function saveReservasToStorage() {
  try {
    localStorage.setItem("reservasLocales", JSON.stringify(reservas));
  } catch (e) {
    console.error("Error guardando reservasLocales:", e);
  }
}

limpiarReservasExpiradas();

/* REGISTRO DE RESERVA */
function registrarReserva() {
  const form = document.getElementById("form-reserva");
  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const usuarioActivo = JSON.parse(localStorage.getItem("usuarioActivo"));
  if (!usuarioActivo || !usuarioActivo.idUsuario) {
    alert("⚠️ Debes iniciar sesión para realizar una reserva.");
    return;
  }

  const nombre = document.getElementById("nombre").value.trim();
  const apellido = document.getElementById("apellido").value.trim();
  const dni = document.getElementById("dni").value.trim();
  const telefono = document.getElementById("telefono").value.trim();
  const correo = document.getElementById("correo").value.trim();
  const personas = parseInt(document.getElementById("cantidad").value, 10);
  const fecha = document.getElementById("fecha").value;
  const hora = document.getElementById("hora").value;
  const vistaSeleccionada = document.querySelector('input[name="vista"]:checked');

  if (!nombre || !apellido || !dni || !telefono || !correo || !personas || !fecha || !hora || !vistaSeleccionada) {
    alert("⚠️ Por favor complete todos los campos.");
    return;
  }

  const vista = vistaSeleccionada.value === "vista1" ? "Vista al Mar" : "Vista a la Terraza";
  const fechaFormateada = formatearFecha(fecha);

  limpiarReservasExpiradas();

  const config = getMesasDisponibles(personas);

  // calcular intervalo
  const inicioDeseado = parseFechaHoraToDate(fecha, hora);
  const finDeseado = new Date(inicioDeseado.getTime() + 90 * 60000);

  const ocupadas = reservas
    .filter(r => r.fecha === fecha)
    .filter(r => {
     
      const inicioExistente = parseFechaHoraToDate(r.fecha, r.hora);
      const finExistente = new Date(inicioExistente.getTime() + 90 * 60000);
   
      return inicioDeseado < finExistente && inicioExistente < finDeseado;
    })
    .map(r => r.mesa);

 
   let mesaAsignada = null;
  for (let i = config.inicio; i <= config.fin; i++) {
    if (!ocupadas.includes(i)) {
      mesaAsignada = i;
      break;
    }
  }

  if (!mesaAsignada) {
    alert("⚠️ No hay mesas disponibles para este horario.");
    return;
  }

  const confirmar = confirm(
    `¿Confirma su reserva para ${personas} persona(s) el ${fechaFormateada} a las ${hora}?\n` +
    `Vista seleccionada: ${vista}\n` +
    `Mesa asignada: N° ${mesaAsignada}`
  );
  if (!confirmar) {
    alert("❌ Su reserva ha sido cancelada.");
    return;
  }

  const formData = new FormData();
  formData.append("usuario_id", usuarioActivo.idUsuario);
  formData.append("nombre", nombre);
  formData.append("apellido", apellido);
  formData.append("dni", dni);
  formData.append("telefono", telefono);
  formData.append("correo", correo);
  formData.append("personas", personas);
  formData.append("fecha", fecha);
  formData.append("hora", hora);
  formData.append("vista", vista);
  formData.append("mesa", mesaAsignada);

  console.log(" UsuarioActivo:", usuarioActivo);
  console.log(" ID enviado:", usuarioActivo.idUsuario);
  for (let [k, v] of formData.entries()) {
    console.log("➡️", k, "=", v);
  }

  fetch("/Proyecto_Rinconcito/ServletReserva", {
    method: "POST",
    body: formData
  })
    .then(resp => resp.text())
    .then(data => {
      console.log("📥 Respuesta del servidor:", data);

      if (data && data.includes("✅")) {
        
        reservas.push({
          fecha,
          hora,
          mesa: mesaAsignada,
          personas,
          creadoEn: new Date().toISOString()
        });
        saveReservasToStorage();

        alert(
          `✅ Reserva registrada correctamente.\n\n` +
          `Estimado(a) ${nombre} ${apellido}, tu reserva para ${personas} personas el ${fechaFormateada} a las ${hora} fue guardada.\n` +
          `Mesa asignada: N° ${mesaAsignada}.\n` +
          `Una copia llegará a ${correo} y al teléfono ${telefono}.\n\n` +
          `¡Muchas gracias!`
        );

        form.reset();
      } else {
        alert("⚠️ No se pudo registrar la reserva: " + data);
      }
    })
    .catch(err => {
      console.error("🚨 Error al registrar reserva:", err);
      alert("❌ Error al registrar la reserva.");
    });
}

/* MODAL LOGIN / REGISTRO */
const botonLogin = document.getElementById("abrirLogin");
const overlayLogin = document.getElementById("overlayLogin");
const formLogin = document.getElementById("formLogin");
const formRegistro = document.getElementById("formRegistroContainer");

if (botonLogin) {
  botonLogin.addEventListener("click", e => {
    e.preventDefault();
    overlayLogin.style.display = "flex";
    formLogin.classList.remove("oculto");
    formRegistro.classList.add("oculto");
  });
}

function mostrarRegistro() {
  formLogin.classList.add("oculto");
  formRegistro.classList.remove("oculto");
}

function mostrarLogin() {
  formRegistro.classList.add("oculto");
  formLogin.classList.remove("oculto");
}

function cerrarFormularios() {
  overlayLogin.style.display = "none";
  const formLoginReal = document.getElementById("formLoginReal");
  const formRegistroReal = document.getElementById("formRegistro");
  if (formLoginReal) formLoginReal.reset();
  if (formRegistroReal) formRegistroReal.reset();
}

window.addEventListener("click", e => {
  if (e.target === overlayLogin) cerrarFormularios();
});

document.querySelectorAll(".cerrar").forEach(btn =>
  btn.addEventListener("click", cerrarFormularios)
);

/* CONTROL DE SESIÓN*/
function mostrarUsuarioActivo(usuario) {
  const abrirLoginBtn = document.getElementById("abrirLogin");
  if (abrirLoginBtn) abrirLoginBtn.classList.add("oculto");
  const contenedor = document.getElementById("usuarioActivo");
  const nombreSpan = document.getElementById("nombreUsuario");
  nombreSpan.textContent = `${usuario.nombre} ${usuario.apellido}`;
  contenedor.classList.remove("oculto");
}

function cerrarSesion() {
  localStorage.removeItem("usuarioActivo");
  document.getElementById("usuarioActivo").classList.add("oculto");
  const abrirLoginBtn = document.getElementById("abrirLogin");
  if (abrirLoginBtn) abrirLoginBtn.classList.remove("oculto");
  alert(" Sesión cerrada correctamente.");
}

document.getElementById("cerrarSesion")?.addEventListener("click", cerrarSesion);

document.addEventListener("DOMContentLoaded", () => {
  const usuarioGuardado = localStorage.getItem("usuarioActivo");
  if (usuarioGuardado) mostrarUsuarioActivo(JSON.parse(usuarioGuardado));
});

/* REGISTRO E INICIO DE SESIÓN  */
function registrarUsuario() {
  const params = new URLSearchParams();
  params.append("accion", "registrar");
  params.append("nombre", document.getElementById("nombreReg").value);
  params.append("apellido", document.getElementById("apellidoReg").value);
  params.append("dni", document.getElementById("dniReg").value);
  params.append("telefono", document.getElementById("telefonoReg").value);
  params.append("correo", document.getElementById("correoReg").value);
  params.append("contrasena", document.getElementById("passwordReg").value);

  fetch("/Proyecto_Rinconcito/ServletUsuario", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString()
  })
    .then(resp => resp.text())
    .then(data => {
      alert(data);
      if (data.includes("✅")) {
        document.getElementById("formRegistro").reset();
        cerrarFormularios();
      }
    })
    .catch(err => console.error("🚨 Error al conectar con el servlet:", err));
}
document.getElementById("formRegistro").addEventListener("submit", e => {
  e.preventDefault();
  registrarUsuario();
});

function iniciarSesion() {
  const correo = document.getElementById("correoLogin").value.trim();
  const contrasena = document.getElementById("passwordLogin").value.trim();

  if (!correo || !contrasena) {
    alert("⚠️ Complete todos los campos.");
    return;
  }

  const params = new URLSearchParams();
  params.append("correo", correo);
  params.append("contrasena", contrasena);

  fetch("http://localhost:8080/Proyecto_Rinconcito/ServletLogin", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString()
  })
    .then(resp => resp.json())
    .then(data => {
      console.log(" Login:", data);
      if (data.status === "ok") {
        const usuario = {
          idUsuario: data.idUsuario,
          nombre: data.nombre,
          apellido: data.apellido
        };
        localStorage.setItem("usuarioActivo", JSON.stringify(usuario));
        alert("✅ Bienvenido, " + usuario.nombre + " " + usuario.apellido);
        cerrarFormularios();
        mostrarUsuarioActivo(usuario);
      } else {
        alert("❌ " + data.message);
      }
    })
    .catch(err => {
      console.error(" Error en login:", err);
      alert("❌ Error al conectar con el servidor.");
    });
}

document.getElementById("formLoginReal").addEventListener("submit", e => {
  e.preventDefault();
  iniciarSesion();
});
