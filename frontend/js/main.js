/* ===== Global Config ===== */
// Base URL of the Java backend (Tomcat). Change this if deployed elsewhere.
const API_BASE = "https://event-management-portal-xyy9.onrender.com/eventportal/api/events";

/* ===== Footer (shared across all pages) ===== */
const FOOTER_HTML = `
<div class="container">
  <div class="footer-grid">
    <div class="footer-col">
      <h4>NIET Event Portal</h4>
      <p>Your one-stop platform to discover, register and manage all college events.</p>
      <div class="social-icons"> 
        <a href="https://www.instagram.com/niet_nehrugroupofinstitutions/" target="_blank" rel="noopener" title="Instagram"><i class="fab fa-instagram"></i></a>
        <a href="https://www.facebook.com/ngi.niet/" target="_blank" rel="noopener" title="Facebook"><i class="fab fa-facebook-f"></i></a>
        <a href="https://www.youtube.com/@NehruGroupofInstitutions/videos" target="_blank" rel="noopener" title="YouTube"><i class="fab fa-youtube"></i></a>
      </div>
    </div>
    <div class="footer-col">
      <h4>Quick Links</h4>
      <a href="index.html">Home</a>
      <a href="index.html#about">About Us</a>
      <a href="events.html">Events</a>
      <a href="admin-login.html">Admin</a>
      <a href="index.html#contact">Contact Us</a>
    </div>
    <div class="footer-col">
      <h4>Contact</h4>
      <p><i class="fas fa-phone-alt"></i> +91 98765 43210</p>
      <p><i class="fas fa-envelope"></i> niet.principal@gmail.com</p>
      <p><i class="fas fa-map-marker-alt"></i> Coimbatore, Tamil Nadu – 641105</p>
    </div>
  </div>
  <div class="footer-bottom">
    &copy; ${new Date().getFullYear()} NIET Event Management Portal. All rights reserved.
  </div>
</div>`;
document.addEventListener("DOMContentLoaded", () => {
  const footerEl = document.getElementById("footer-placeholder");
  if (footerEl) footerEl.innerHTML = FOOTER_HTML;

  // Mobile nav toggle
  const hamburger = document.getElementById("hamburger");
  const navLinks = document.getElementById("navLinks");
  if (hamburger && navLinks) {
    hamburger.addEventListener("click", () => navLinks.classList.toggle("open"));
  }

  if (document.getElementById("heroSlider")) initSlider();
  if (document.getElementById("previewEventGrid")) loadPreviewEvents();
});

/* ===== Hero Slider (auto-plays, 1s crossfade) ===== */
function initSlider() {
  const slides = document.querySelectorAll("#heroSlider .slide");
  const dotsWrap = document.getElementById("sliderDots");
  let current = 0;

  slides.forEach((_, i) => {
    const dot = document.createElement("div");
    dot.className = "dot" + (i === 0 ? " active" : "");
    dot.addEventListener("click", () => showSlide(i));
    dotsWrap.appendChild(dot);
  });
  const dots = dotsWrap.querySelectorAll(".dot");

  function showSlide(index) {
    slides[current].classList.remove("active");
    dots[current].classList.remove("active");
    current = (index + slides.length) % slides.length;
    slides[current].classList.add("active");
    dots[current].classList.add("active");
  }

  setInterval(() => showSlide(current + 1), 3500); // change slide every 3.5s, 1s CSS crossfade
}

/* ===== Load preview events on home page ===== */
async function loadPreviewEvents() {
  const grid = document.getElementById("previewEventGrid");
  try {
    const res = await fetch(`${API_BASE}/events`);
    if (!res.ok) throw new Error("Failed to fetch");
    const events = await res.json();
    if (!events.length) {
      grid.innerHTML = `<p class="empty-state" style="grid-column:1/-1;">No upcoming events right now. Check back soon!</p>`;
      return;
    }
    grid.innerHTML = events.slice(0, 4).map(ev => eventCardHTML(ev)).join("");
    grid.querySelectorAll(".event-card").forEach(card => {
      card.addEventListener("click", () => window.location.href = "events.html");
    });
  } catch (err) {
    grid.innerHTML = sampleEventCards(); // fallback demo data if backend not running
  }
}

function eventCardHTML(ev) {
  return `
  <div class="event-card" data-id="${ev.id}">
    <img src="${ev.imageUrl || 'https://images.unsplash.com/photo-1531482615713-2afd69097998?w=500'}" alt="${ev.name}">
    <div class="event-card-body">
      <span class="event-date">${formatDate(ev.eventDate)}</span>
      <h3>${ev.name}</h3>
      <p>${(ev.description || "").slice(0, 80)}${ev.description && ev.description.length > 80 ? "..." : ""}</p>
    </div>
  </div>`;
}

function formatDate(dateStr) {
  if (!dateStr) return "TBA";
  const d = new Date(dateStr);
  if (isNaN(d)) return dateStr;
  return d.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
}

