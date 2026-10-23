let allEvents = [];

document.addEventListener("DOMContentLoaded", () => {
  loadUpcomingEvents();
  setupTabs();
  setupModal();

  document.getElementById("lookupBtn").addEventListener("click", checkRegisteredEvents);
  document.getElementById("lookupEmail").addEventListener("keypress", e => {
    if (e.key === "Enter") checkRegisteredEvents();
  });
});


/* ===== Tabs ===== */
function setupTabs() {
  document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
      document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
      btn.classList.add("active");
      document.getElementById("panel-" + btn.dataset.tab).classList.add("active");
    });
  });
}

/* ===== Upcoming Events ===== */
async function loadUpcomingEvents() {
  const grid = document.getElementById("upcomingGrid");
  try {
    const res = await fetch(`${API_BASE}/events`);
    if (!res.ok) throw new Error();
    allEvents = await res.json();
  } catch (err) {
    // Fallback demo data so the UI is browsable even before backend is running
    allEvents = [
      { id: 1, name: "Annual Day 2026", eventDate: "2026-09-15", description: "A celebration of student achievements with cultural performances and prize distribution." },
      { id: 2, name: "Tech Fest", eventDate: "2026-09-28", description: "Coding contests, hackathons, robotics and project expos across all departments." },
      { id: 3, name: "Pongal Celebration", eventDate: "2026-10-05", description: "Traditional harvest festival celebrated with games, food stalls and cultural events." },
      { id: 4, name: "Annual Sports Meet", eventDate: "2026-10-20", description: "Inter-department athletics, cricket, football and indoor sports competitions." }
    ];
  }

  if (!allEvents.length) {
    grid.innerHTML = `<p class="empty-state" style="grid-column:1/-1;">No upcoming events are scheduled right now.</p>`;
    return;
  }

  grid.innerHTML = allEvents.map(eventCardHTML).join("");
  grid.querySelectorAll(".event-card").forEach(card => {
    card.addEventListener("click", () => openRegModal(card.dataset.id));
  });
}

/* ===== Registration Modal ===== */
function setupModal() {
  document.getElementById("closeModal").addEventListener("click", closeRegModal);
  document.getElementById("regModal").addEventListener("click", e => {
    if (e.target.id === "regModal") closeRegModal();
  });
  document.getElementById("regForm").addEventListener("submit", submitRegistration);
}

function openRegModal(eventId) {
  const ev = allEvents.find(e => String(e.id) === String(eventId));
  document.getElementById("modalEventName").textContent = "Register for " + (ev ? ev.name : "Event");
  document.getElementById("regEventId").value = eventId;
  document.getElementById("regMsg").textContent = "";
  document.getElementById("regForm").reset();
  document.getElementById("regEventId").value = eventId;
  document.getElementById("regModal").classList.add("active");
}

function closeRegModal() {
  document.getElementById("regModal").classList.remove("active");
}

async function submitRegistration(e) {
  e.preventDefault();
  const msg = document.getElementById("regMsg");
  const payload = {
    eventId: document.getElementById("regEventId").value,
    name: document.getElementById("regName").value.trim(),
    email: document.getElementById("regEmail").value.trim(),
    phone: document.getElementById("regPhone").value.trim(),
    department: document.getElementById("regDept").value,
    year: document.getElementById("regYear").value
  };

  msg.className = "form-msg";
  msg.textContent = "Submitting...";

  try {
    const res = await fetch(`${API_BASE}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    const data = await res.json();
    if (res.ok && data.success) {
      msg.className = "form-msg success";
      msg.textContent = "✅ Registered successfully!";
      setTimeout(closeRegModal, 1400);
    } else {
      msg.className = "form-msg error";
      msg.textContent = data.message || "Registration failed. Please try again.";
    }
  } catch (err) {
    msg.className = "form-msg error";
    msg.textContent = "Could not reach server. Please ensure the backend is running.";
  }
}

/* ===== Registered Events Lookup ===== */
async function checkRegisteredEvents() {
  const email = document.getElementById("lookupEmail").value.trim();
  const results = document.getElementById("registeredResults");

  if (!email) {
    results.innerHTML = `<p class="empty-state">Please enter your email address.</p>`;
    return;
  }
  results.innerHTML = `<p class="empty-state">Searching...</p>`;

  try {
    const res = await fetch(`${API_BASE}/registered?email=${encodeURIComponent(email)}`);
    if (!res.ok) throw new Error();
    const regs = await res.json();

    if (!regs.length) {
      results.innerHTML = `<p class="empty-state">No events are registered for this email.</p>`;
      return;
    }

    results.innerHTML = `<div class="registered-list">${regs.map(r => `
      <div class="registered-item">
        <div>
          <h4>${r.eventName}</h4>
          <span>${r.department} • ${r.year}</span>
        </div>
        <span>Registered on ${formatDate(r.registeredAt)}</span>
      </div>
    `).join("")}</div>`;
  } catch (err) {
    results.innerHTML = `<p class="empty-state">Could not reach server. Please ensure the backend is running.</p>`;
  }
}
