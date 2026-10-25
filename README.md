
# NIET Event Management Portal 🎓

A full-stack college event management portal that allows students to browse and register for campus events, while administrators can manage events and view student registrations.

## 🚀 Live Demo

- **Frontend:** [https://event-management-portal-frontend.vercel.app](https://event-management-portal-frontend.vercel.app)
- **Backend API:** [https://event-management-portal-xyy9.onrender.com/eventportal/api/events](https://event-management-portal-xyy9.onrender.com/eventportal/api/events)

> **Note:** Replace the frontend URL with your actual Vercel deployment URL.

## 🛠️ Tech Stack

| Technology | Description |
|---|---|
| HTML5 | Website structure |
| CSS3 | Styling and responsive design |
| Vanilla JavaScript | Frontend functionality and API integration |
| Java 11 | Backend development |
| Jakarta Servlets | REST API and request handling |
| Apache Tomcat 10 | Servlet container |
| MySQL | Relational database |
| Aiven Cloud | Cloud-hosted database |
| Apache Maven | Dependency management and build automation |
| Docker | Backend containerization |
| Render | Backend deployment |
| Vercel | Frontend deployment |
| Git & GitHub | Version control and source code hosting |

## ✨ Features

### 👨‍🎓 Student Portal

- Browse upcoming campus events on the home page.
- View available event details.
- Register for events using a dynamic modal form.
- Look up previously registered events using an email address.
- Access event information through the backend API.

### 🔐 Admin Dashboard

- Secure administrator login.
- Add new campus events.
- Automatically create a dedicated MySQL registration table for each new event.
- Delete existing events.
- Automatically drop the corresponding registration table when an event is deleted.
- View all students registered for a specific event.

### 🗄️ Dynamic Database Architecture

Unlike traditional designs that store every event registration in a single table, this project creates a dedicated MySQL table for each event.

For example:

- `reg_annual_day_2026`
- `reg_technical_symposium_2026`
- `reg_cultural_fest_2026`

**Advantages:**
- Organizes registrations by event.
- Simplifies event-specific registration queries.
- Makes it easier to retrieve registrations for individual events.

> **Important:** Dedicated tables do not automatically guarantee better performance. For larger systems, a normalized registration table linked to an events table is generally easier to maintain and scale.

## 📁 Project Structure

```text
niet-event-portal/
│
├── frontend/                         # Static frontend website
│   ├── index.html                    # Home page
│   ├── events.html                   # Events list and registration lookup
│   ├── admin-login.html              # Admin login page
│   ├── admin-dashboard.html          # Admin dashboard
│   │
│   ├── css/
│   │   └── style.css                 # Website styling
│   │
│   └── js/
│       ├── main.js                   # Main frontend logic and API configuration
│       ├── events.js                 # Event listing and registration logic
│       └── admin.js                  # Admin dashboard functionality
│
├── backend/                          # Java Servlet backend
│   ├── Dockerfile                    # Docker configuration for deployment
│   ├── pom.xml                       # Maven dependencies and build configuration
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── niet/
│                       └── eventportal/
│                           ├── config/
│                           │   └── DBConnection.java
│                           ├── model/       # Event, Registration, Admin
│                           ├── dao/         # Database access objects
│                           ├── servlet/     # API endpoints
│                           └── util/        # CORS and response utilities
│
└── database/
    └── schema.sql                    # MySQL schema and sample data
```

## 💻 Local Setup Instructions

### Prerequisites

Make sure you have the following installed:

- Java Development Kit (JDK) 11
- Apache Maven
- Apache Tomcat 10
- MySQL Server
- MySQL Workbench or another SQL client
- Git
- Visual Studio Code or an IDE such as IntelliJ IDEA or Eclipse


### 1. Clone the Repository

Clone the project from GitHub:

```bash
git clone https://github.com/Bala-project/Event-Management-portal.git
cd Event-Management-portal
```

This will download the project to your local machine and navigate to the project directory.

### 2. Database Setup

1. Start your local MySQL Server.
2. Open MySQL Workbench or your preferred SQL client.
3. Create a database named `niet_event_portal`.
4. Execute the SQL script located at `database/schema.sql`.

Example:

```sql
CREATE DATABASE niet_event_portal;
USE niet_event_portal;
```

> **Note:** If the schema script already contains these statements, do not execute them twice. When using Aiven Cloud, select the appropriate existing database and remove the `CREATE DATABASE` and `USE` statements if necessary.

### 3. Configure the Backend

Open the `backend` folder in IntelliJ IDEA or Eclipse as a Maven project.

Update the database connection configuration in:

`backend/src/main/java/com/niet/eventportal/config/DBConnection.java`

Example local configuration:

```java
private static final String DB_URL =
    "jdbc:mysql://localhost:3306/niet_event_portal?useSSL=false";

private static final String DB_USER = "root";

private static final String DB_PASSWORD = "your_password";
```

Replace the username and password with your local MySQL credentials.

**Security recommendation:** For production deployments, read database credentials from environment variables instead of hardcoding them in the source code.

### 4. Build the Backend

Navigate to the backend directory:

```bash
cd backend
```

Run the Maven build command:

```bash
mvn clean package
```

After a successful build, the generated WAR file will be available in the `target/` directory.

Deploy the WAR file to Apache Tomcat 10 and start the server.

The application context path may be `/eventportal`, depending on the deployed WAR filename.

### 5. Configure the Frontend

Open the `frontend/js/main.js` file and update the API base URL to point to your local Tomcat server.

Example:

```javascript
const API_BASE = "http://localhost:8080/eventportal/api";
```

Ensure that the URL matches your actual backend deployment and API routes.

### 6. Run the Frontend

Open the `frontend` folder in Visual Studio Code.

You can use the **Live Server** extension or another static web server to run the website.

Then open the local frontend URL in your browser.

Verify that:
- Events are displayed correctly.
- Student registration works.
- Registration lookup returns the expected results.
- Administrator login and dashboard operations work.

## ☁️ Production Deployment

The project uses the following deployment architecture:

| Component | Hosting Platform |
|---|---|
| Frontend | Vercel |
| Backend API | Render |
| Database | Aiven Cloud MySQL |

### 1. Database Deployment — Aiven

1. Create a MySQL service on Aiven Cloud.
2. Obtain the database hostname, port, username, and password.
3. Connect using the provided database credentials and SSL settings.
4. Execute the SQL statements from `database/schema.sql` against the appropriate database, such as `defaultdb`.
5. Verify that the tables and sample data are available.

Do not commit production database credentials to GitHub.

### 2. Backend Deployment — Render

The backend is containerized using a multi-stage Dockerfile.

1. Push the project to GitHub.
2. Open Render and create a new Web Service.
3. Connect your GitHub repository.
4. Select **Docker** as the deployment environment.
5. Set the root directory to:

   `backend`

6. Configure the following environment variables in Render:

| Variable | Value |
|---|---|
| `DB_URL` | `jdbc:mysql://<aiven-host>:<port>/defaultdb?ssl-mode=REQUIRED&verifyServerCertificate=false` |
| `DB_USER` | Your Aiven database username |
| `DB_PASSWORD` | Your Aiven database password |

Replace the placeholders with the actual values from Aiven.

Ensure the JDBC driver, Docker configuration, database connection code, and Tomcat setup support the selected Java and servlet versions.

**Important:** The database connection configuration must read these environment variables for the Render settings to take effect.

After deployment, verify that the backend starts successfully and that the events API responds correctly.

### 3. Frontend Deployment — Vercel

1. Open Vercel and import your GitHub repository.
2. Set the root directory to:

   `frontend`

3. Set the framework preset to **Other**.
4. Leave the build command empty if no build step is required.
5. Update `frontend/js/main.js` to use the deployed Render backend URL.

Example:

```javascript
const API_BASE =
    "https://event-management-portal-xyy9.onrender.com/eventportal/api";
```

6. Deploy the project.
7. Open the generated Vercel URL and verify that the frontend can communicate with the backend.

Make sure the backend CORS configuration allows requests from your deployed frontend domain.

## 🔗 API Endpoint

The deployed events API is available at:

[https://event-management-portal-xyy9.onrender.com/eventportal/api/events](https://event-management-portal-xyy9.onrender.com/eventportal/api/events)

This endpoint is intended to provide event data through the backend API.

Other API routes depend on the servlet mappings implemented in the project.

## 🔑 Admin Login

The development credentials provided for this project are:

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin123` |

> **Security warning:** These are example/default credentials. Change the password before deploying the application publicly. Never use default administrator credentials in production.

For a production deployment, use securely stored credentials, hashed passwords, proper session management, and server-side authorization checks.

## 🔒 Security Considerations

- Keep database credentials and secrets out of the GitHub repository.
- Use environment variables for production configuration.
- Change default administrator credentials before public deployment.
- Hash administrator passwords using a secure password-hashing algorithm.
- Validate and sanitize user input on the server.
- Use prepared statements to prevent SQL injection.
- Protect administrator endpoints with server-side authentication and authorization.
- Configure CORS to allow only trusted frontend origins.
- Use HTTPS for deployed services.
- Validate event identifiers before dynamically creating or dropping database tables.

## 🚧 Future Enhancements

- Student authentication and user profiles.
- Event search, filtering, and category selection.
- Event capacity limits and waitlists.
- Email confirmations after successful registration.
- QR-code-based event check-in.
- Event attendance tracking.
- Admin analytics and registration reports.
- Pagination and improved search for large registration lists.
- Role-based access control.
- Automated testing and continuous deployment.

## 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

1. Fork the repository.
2. Create a new feature branch.
3. Commit your changes.
4. Push your branch to GitHub.
5. Open a pull request describing your changes.

## 📄 License

This project is available for educational and learning purposes.

Add a `LICENSE` file to the repository if you want to distribute the project under a specific open-source license.

---

**Developed as a college event management project for NIET.** 🎓
