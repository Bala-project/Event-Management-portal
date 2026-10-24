# NIET Event Management Portal 🎓

A full-stack college event management portal that allows students to browse and register for campus events, and allows administrators to manage events and view registrations.

## 🚀 Live Demo
*   **Frontend:** [https://your-vercel-link.vercel.app](https://your-vercel-link.vercel.app) *(Replace with your actual Vercel URL)*
*   **Backend API:** [https://event-management-portal-xyy9.onrender.com/eventportal/api/events](https://event-management-portal-xyy9.onrender.com/eventportal/api/events)

## 🛠️ Tech Stack
*   **Frontend:** HTML5, CSS3, Vanilla JavaScript
*   **Backend:** Java 11, Jakarta Servlets, Apache Tomcat 10
*   **Database:** MySQL (Hosted on Aiven Cloud)
*   **Deployment:** Frontend on Vercel, Backend on Render (via Docker), Database on Aiven

## ✨ Features
*   **Student Portal:** 
    *   Browse upcoming events on the home page.
    *   Register for events via a dynamic modal form.
    *   Look up previously registered events using an email address.
*   **Admin Dashboard:**
    *   Secure login for administrators.
    *   Add new events (automatically creates a dedicated MySQL registration table for each new event).
    *   Delete events (automatically drops the corresponding registration table).
    *   View a list of all students registered for a specific event.
*   **Dynamic Database Architecture:** Unlike traditional designs that use one massive table for all registrations, this system creates a **dedicated table** for every event (e.g., `reg_annual_day_2026`). This makes data retrieval incredibly fast and organized.

## 📁 Project Structure
```text
niet-event-portal/
├── frontend/                 # Static frontend website
│   ├── index.html            # Home page
│   ├── events.html           # Events list & registration lookup
│   ├── admin-login.html      # Admin login page
│   ├── admin-dashboard.html  # Admin controls
│   ├── css/style.css
│   └── js/                   # Frontend logic (main.js, events.js, admin.js)
│
├── backend/                  # Java Servlet Backend
│   ├── Dockerfile            # Docker configuration for Render deployment
│   ├── pom.xml               # Maven dependencies
│   └── src/main/java/com/niet/eventportal/
│       ├── config/           # DBConnection.java
│       ├── model/            # Data models (Event, Registration, Admin)
│       ├── dao/              # Database Access Objects
│       ├── servlet/          # API Endpoints
│       └── util/             # CORS Filter & Response Utilities
│
└── database/
    └── schema.sql            # MySQL Schema & Sample Data

## 💻 Local Setup Instructions

### 1. Database Setup (MySQL)
1. Open MySQL Workbench or your preferred SQL client.
2. Create a database named `niet_event_portal` (or use the `CREATE DATABASE` command in the script).
3. Run the provided script located in the `database/schema.sql` file to create the necessary tables and insert sample data.

### 2. Backend Setup (Java & Tomcat)
1. Open the `backend` folder in your IDE (IntelliJ IDEA, Eclipse, or VS Code) as a Maven project.
2. Open `src/main/java/com/niet/eventportal/config/DBConnection.java` and update the default database credentials to match your local MySQL setup:
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/niet_event_portal?useSSL=false";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "your_local_password";
