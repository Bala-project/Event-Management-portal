/* ================= ADMIN LOGIN PAGE ================= */
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("adminLoginForm");
  if (loginForm) loginForm.addEventListener("submit", handleAdminLogin);

  // Dashboard-only setup
  if (document.getElementById("panel-addEvent")) {
    guardDashboard();
    setupSidebar();
    document.getElementById("addEventForm").addEventListener("submit", handleAddEvent);
    document.getElementById("logoutBtn").addEventListener("click", handleLogout);
    document.getElementById("loadRegsBtn").addEventListener("click", loadRegistrationsForSelectedEvent);
    loadManageEvents();
  }
});

async function handleAdminLogin(e) {
  e.preventDefault();
  const msg = document.getElementById("loginMsg");
  const id = document.getElementById("adminId").value.trim();
  const password = document.getElementById("adminPassword").value.trim();

  msg.className = "form-msg";
  msg.textContent = "Checking credentials...";

  try {
    const res = await fetch(`${API_BASE}/admin/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: id, password })
    });
    const data = await res.json();
    if (res.ok && data.success) {
      sessionStorage.setItem("niet_admin_token", data.token || "authenticated");
      sessionStorage.setItem("niet_admin_name", id);
      window.location.href = "admin-dashboard.html";
    } else {
      msg.className = "form-msg error";
      msg.textContent = data.message || "Invalid Admin ID or Password.";
    }
  } catch (err) {
    msg.className = "form-msg error";
    msg.textContent = "Could not reach server. Please ensure the backend is running.";
  }
}

/* ================= DASHBOARD GUARD ================= */
function guardDashboard() {
  const token = sessionStorage.getItem("niet_admin_token");
  if (!token) {
    window.location.href = "admin-login.html";
    return;
  }
  const welcome = document.getElementById("adminWelcome");
  if (welcome) welcome.textContent = "Welcome, " + (sessionStorage.getItem("niet_admin_name") || "Admin");
}

function handleLogout() {
  sessionStorage.removeItem("niet_admin_token");
  sessionStorage.removeItem("niet_admin_name");
  window.location.href = "admin-login.html";
}

function authHeaders() {
  return { "Content-Type": "application/json", "Authorization": "Bearer " + sessionStorage.getItem("niet_admin_token") };
}

/* ================= SIDEBAR NAV ================= */
function setupSidebar() {
  document.querySelectorAll(".admin-sidebar button").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".admin-sidebar button").forEach(b => b.classList.remove("active"));
      document.querySelectorAll(".admin-panel").forEach(p => p.classList.remove("active"));
      btn.classList.add("active");
      document.getElementById("panel-" + btn.dataset.panel).classList.add("active");
      if (btn.dataset.panel === "manageEvents") loadManageEvents();
      if (btn.dataset.panel === "viewRegs") populateEventSelect();
    });
  });
}

/* ================= ADD EVENT ================= */
async function handleAddEvent(e) {
  e.preventDefault();
  const msg = document.getElementById("addEventMsg");
  const payload = {
    name: document.getElementById("evName").value.trim(),
    description: document.getElementById("evDesc").value.trim(),
    eventDate: document.getElementById("evDate").value,
    venue: document.getElementById("evVenue").value.trim(),
    imageUrl: document.getElementById("evImage").value.trim()
  };
  msg.className = "form-msg";
  msg.textContent = "Adding event...";

  try {
    const res = await fetch(`${API_BASE}/events`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(payload)
    });
    const data = await res.json();
    if (res.ok && data.success) {
      msg.className = "form-msg success";
      msg.textContent = "✅ Event added successfully!";
      document.getElementById("addEventForm").reset();
      loadManageEvents();
    } else {
      msg.className = "form-msg error";
      msg.textContent = data.message || "Failed to add event.";
    }
  } catch (err) {
    msg.className = "form-msg error";
    msg.textContent = "Could not reach server. Please ensure the backend is running.";
  }
}

/* ================= MANAGE (LIST / DELETE) EVENTS ================= */
async function loadManageEvents() {
  const tbody = document.getElementById("manageTableBody");
  tbody.innerHTML = `<tr><td colspan="4">Loading...</td></tr>`;
  try {
    const res = await fetch(`${API_BASE}/events`);
    const events = await res.json();
    window.__adminEvents = events;
    if (!events.length) {
      tbody.innerHTML = `<tr><td colspan="4">No events added yet.</td></tr>`;
      return;
    }
    tbody.innerHTML = events.map(ev => `
      <tr>
        <td>${ev.name}</td>
        <td>${formatDate(ev.eventDate)}</td>
        <td>${ev.venue || "-"}</td>
        <td><button class="btn btn-danger btn-sm" onclick="deleteEvent(${ev.id})">Delete</button></td>
      </tr>
    `).join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="4">Could not reach server.</td></tr>`;
  }
}

async function deleteEvent(id) {
  if (!confirm("Delete this event? This will also remove its registration records.")) return;
  try {
    const res = await fetch(`${API_BASE}/events?id=${id}`, {
      method: "DELETE",
      headers: authHeaders()
    });
    const data = await res.json();
    if (res.ok && data.success) {
      loadManageEvents();
    } else {
      alert(data.message || "Failed to delete event.");
    }
  } catch (err) {
    alert("Could not reach server.");
  }
}

/* ================= VIEW REGISTRATIONS ================= */
async function populateEventSelect() {
  const select = document.getElementById("regEventSelect");
  select.innerHTML = `<option value="">Select an event</option>`;
  try {
    const res = await fetch(`${API_BASE}/events`);
    const events = await res.json();
    events.forEach(ev => {
      const opt = document.createElement("option");
      opt.value = ev.id;
      opt.textContent = ev.name;
      select.appendChild(opt);
    });
  } catch (err) { /* ignore, dropdown stays empty */ }
}

async function loadRegistrationsForSelectedEvent() {
  const eventId = document.getElementById("regEventSelect").value;
  const table = document.getElementById("regsTable");
  const tbody = document.getElementById("regsTableBody");
  const emptyMsg = document.getElementById("regsEmptyMsg");

  if (!eventId) {
    alert("Please select an event first.");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/admin/registrations?eventId=${eventId}`, {
      headers: authHeaders()
    });
    const regs = await res.json();

    if (!regs.length) {
      table.style.display = "none";
      emptyMsg.style.display = "block";
      return;
    }
    emptyMsg.style.display = "none";
    table.style.display = "table";
    tbody.innerHTML = regs.map(r => `
      <tr>
        <td>${r.name}</td>
        <td>${r.email}</td>
        <td>${r.phone}</td>
        <td>${r.department}</td>
        <td>${r.year}</td>
      </tr>
    `).join("");
  } catch (err) {
    alert("Could not reach server.");
  }
}

