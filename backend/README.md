# Task List App — Backend

A Spring Boot REST API for a task management application. Built as part of a take-home coding exercise, JWT authentication, and role-based access control.

---

## Prerequisites

Before running, make sure you have:

- Java 20+
- Maven 3.9+

That's it — no database installation needed. H2 runs automatically inside the app.

---

## How to Run

1. Open the `backend` folder in IntelliJ IDEA
2. Wait for Maven to finish downloading dependencies
3. Open `Application.java` and run
4. Wait for this message in the console:
```
   Tomcat started on port 8080
   Started Application in X seconds
```
5. API is ready at `http://localhost:8080`

---

## Default Users

Created automatically on startup — no manual setup needed.

| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | admin123  | ADMIN      |
| viewer   | viewer123 | READ_ONLY  |

---

## How Authentication Works

I chose JWT over session-based auth for a few reasons — it's stateless, works naturally with a React SPA, and doesn't require server-side session storage which makes it easier to scale.

### Login Flow
1. `POST /api/auth/login` with username and password
2. Server checks credentials against H2 database (passwords are BCrypt hashed)
3. Returns two tokens:
    - **Access Token** — expires in 15 minutes, used for all API calls
    - **Refresh Token** — expires in 7 days, only used to get a new access token

### Using the Access Token
Add this header to every API request:
```
Authorization: Bearer <accessToken>
```

### When Access Token Expires
```
POST /api/auth/refresh
Body: { "refreshToken": "..." }
```
Returns a brand new access token and refresh token.
Old refresh token is immediately revoked — only the new one works.

### Logout
```
POST /api/auth/logout
Header: Authorization: Bearer <accessToken>
Body: { "refreshToken": "..." }
```
Two things happen:
1. Access token is added to a blacklist in the database — immediately rejected on all future requests
2. Refresh token is revoked in the database — cannot be used to generate new tokens

This means logout is truly enforced on the server side, not just on the frontend.

---

## Role-Based Access

Roles are enforced at the API level — not just hidden in the UI.

| Role      | What they can do                    |
|-----------|-------------------------------------|
| ADMIN     | View, create, update, delete tasks  |
| READ_ONLY | View tasks only                     |

If a READ_ONLY user tries to create or delete a task, they get `403 Forbidden` — the request never reaches the service layer.

---

## API Endpoints

### Auth
| Method | Endpoint            | Auth Required | Description           |
|--------|---------------------|---------------|-----------------------|
| POST   | /api/auth/login     | No            | Login                 |
| POST   | /api/auth/refresh   | No            | Get new access token  |
| POST   | /api/auth/logout    | Yes           | Logout                |

### Tasks
| Method | Endpoint         | Role             | Description     |
|--------|------------------|------------------|-----------------|
| GET    | /api/tasks       | ADMIN, READ_ONLY | Get all tasks   |
| POST   | /api/tasks       | ADMIN            | Create task     |
| PUT    | /api/tasks/{id}  | ADMIN            | Update task     |
| DELETE | /api/tasks/{id}  | ADMIN            | Delete task     |

---

## Viewing the Database

While the app is running, open this in your browser:

**http://localhost:8080/h2-console**

| Field    | Value                    |
|----------|--------------------------|
| JDBC URL | jdbc:h2:mem:tasklistdb   |
| Username | sa                       |
| Password | (leave empty)            |

Useful queries:
```sql
SELECT * FROM USERS;
SELECT * FROM TASKS;
SELECT * FROM REFRESH_TOKENS;
SELECT * FROM BLACKLISTED_TOKENS;
```



## Error Handling

All errors return a consistent JSON structure:
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "title": "Title is required"
  },
  "timestamp": "2026-03-19T10:00:00"
}
```

| Scenario               | Status |
|------------------------|--------|
| Invalid credentials    | 401    |
| Expired/invalid token  | 401    |
| Wrong role             | 403    |
| Task not found         | 404    |
| Empty title            | 400    |

---

## Bonus Features

- **Due date + overdue** — tasks past their due date are flagged automatically
- **Role-based permissions** — enforced at API level
- **Token blacklist** — true server-side logout
- **Refresh token rotation** — each use generates a new pair

---

## Known Limitations

- **Data resets on restart** — H2 is in-memory. Switching to PostgreSQL only needs a config change, no code changes.
- **No user registration** — users are seeded for demo. A `/api/auth/register` endpoint would be next.
- **No unit tests** — focused on working vertical slice within time constraint. JUnit + Mockito would be next.
- **localStorage for tokens** — refresh token in httpOnly cookie would be more secure in production.

## What I Would Do Next

1. Switch to PostgreSQL with Flyway migrations
2. Add user registration with email verification
3. Add unit and integration tests
4. Store refresh token in httpOnly cookie
5. Add pagination for large task lists
6. Add task categories and priority levels

---

## Key Design Decisions

**JWT + Refresh tokens** — Short-lived access tokens (15 min) reduce risk if a token is stolen. Refresh tokens allow seamless re-authentication without asking the user to log in again.

**Token blacklist on logout** — Most JWT implementations skip true server-side logout. Adding a blacklist table means logout is immediately enforced — not just cosmetic.

**Refresh token rotation** — Every refresh revokes the old token and issues a new one. If a refresh token is stolen and used, the legitimate user's next refresh will fail, alerting them to a potential breach.

**H2 over in-memory Map** — H2 gives real SQL relationships, JPA support, and a browser console for debugging — with zero setup overhead.

**GlobalExceptionHandler** — Consistent error responses across all endpoints. The frontend always gets the same error shape regardless of what went wrong.