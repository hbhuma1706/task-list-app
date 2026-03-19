# Task List App

A task management app I built as a take-home exercise. Spring Boot on the backend, React on the frontend, with JWT auth and role-based access.

---


## Architecture

- Backend follows controller → service → repository pattern
- Frontend uses hooks + context for state management
- Axios interceptors handle auth and token refresh

## API Overview

### Auth
- POST /api/auth/login
- POST /api/auth/refresh
- POST /api/auth/logout

### Tasks
- GET /api/tasks
- POST /api/tasks
- PUT /api/tasks/{id}
- DELETE /api/tasks/{id}


## Running it locally

You'll need Java 20+, Maven 3.9+, Node 18+, and npm 9+. No database installation needed — H2 spins up automatically with the backend.

**Backend** — Open the `backend` folder in IntelliJ, let Maven finish loading, then run `Application.java`. API runs at `http://localhost:8080`.

**Frontend**
```bash
cd frontend
npm install
npm run dev
```
Then open `http://localhost:5173`.

Start the backend first — the frontend will fail to load tasks if it's not running.

---

## Logging in

| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | admin123  | ADMIN      |
| viewer   | viewer123 | READ_ONLY  |

These get created automatically on startup, nothing to set up manually.

---

## How auth works

I went with JWT because it fits naturally with a React SPA — stateless, no server session to manage, and easy to send in request headers.

When you log in you get two tokens back — an access token that lasts 15 minutes and a refresh token that lasts 7 days. The access token goes into every API request. When it expires, the frontend quietly refreshes it in the background using the refresh token, so you never get randomly logged out mid-session.

Logout actually does something on the server — the access token gets blacklisted in the database and the refresh token gets revoked. So even if someone captured your token before you logged out, it stops working immediately. Most JWT implementations skip this and just clear the frontend, which always felt like a gap to me.

Roles are enforced at the API level. ADMIN gets full CRUD, READ_ONLY can only view. Hiding buttons in the UI isn't enough — a READ_ONLY user hitting the create endpoint directly still gets a 403.

---

## What's in the app

- Create tasks with a title, optional description, and optional due date
- Check off tasks to mark them complete, uncheck to revert
- Edit title, description, due date inline
- Delete tasks
- Filter by All, Active, Completed, or Overdue
- Tasks past their due date get highlighted in red automatically
- Changes appear instantly in the UI and roll back if the API call fails

---

## Viewing the database

While the backend is running, go to `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:mem:tasklistdb`
- Username: `sa`
- Password: (leave empty)

You can see the USERS, TASKS, REFRESH_TOKENS, and BLACKLISTED_TOKENS tables live. Data resets every time the backend restarts since it's in-memory.

---

## No environment variables needed

Everything works out of the box with the default config.

---

## Honest limitations

- Data doesn't survive a backend restart — H2 is in-memory. Switching to PostgreSQL is just a config change, no code changes needed.
- No user registration — users are seeded in code for the demo.
- No tests written — I prioritized getting a clean working slice done first. JUnit + Mockito for the backend and React Testing Library for the frontend would be the next thing I'd add.
- Access token lives in localStorage. For higher security I'd move the refresh token to an httpOnly cookie to protect against XSS.

---

## What I'd do next

Roughly in order of priority — PostgreSQL with migrations, unit and integration tests, user registration, move refresh token to httpOnly cookie, and task categories or priorities.

---

## Decisions worth mentioning

The refresh token blacklist on logout was a deliberate choice. JWT logout is usually just "delete the token from the client" which means the token is still technically valid until it expires. That felt wrong for a real app so I added server-side invalidation.

Optimistic UI updates make the app feel fast — you click delete and it's gone instantly, no waiting for the server. If the request fails it rolls back and shows an error. Small thing but makes a big difference to how the app feels.

Vite instead of Create React App — CRA is deprecated and Vite is what people actually use now. No strong reason to use the old tooling.

---

## Time spent

Roughly 7 hours across backend, frontend, and documentation.