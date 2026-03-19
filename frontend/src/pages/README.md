# Task List App — Frontend

React frontend for the Task List application. Built with Vite + React, connects to a Spring Boot backend.

---

## Prerequisites

- Node.js 18+
- npm 9+
- Backend running at `http://localhost:8080`

---

## How to Run

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173` in your browser.

> Make sure the backend is running first, otherwise API calls will fail.

---

## Login Credentials

| Username | Password  | Role       | What they can do         |
|----------|-----------|------------|--------------------------|
| admin    | admin123  | ADMIN      | Full access              |
| viewer   | viewer123 | READ_ONLY  | View tasks only          |

---

## Features

### Authentication
- Login page with username/password
- JWT token stored in localStorage
- Automatic token refresh when access token expires (15 min)
- If refresh token also expires (7 days), user is redirected to login
- Logout clears both tokens from localStorage and invalidates them on the server

### Tasks
- View all tasks sorted by newest first
- Add a task with title (required), description (optional), and due date
- Click the checkbox on any task to toggle it complete/incomplete
- Edit task title, description, due date, and completed status
- Delete a task
- Filter by All / Active / Completed / Overdue

### Role-based UI
- ADMIN sees the add form, edit and delete buttons
- READ_ONLY user sees tasks only — no create/edit/delete
- This is enforced on the backend too, not just hidden in the UI

### Due Date & Overdue
- Tasks with a past due date that aren't completed show a red highlight
- Overdue filter shows only these tasks
- Due date shown on each task card

### Optimistic UI
- When you add, update, or delete a task, the UI updates instantly
- If the API call fails, the change is rolled back and an error is shown
- Makes the app feel fast even on slow connections

### Accessibility
- All buttons have aria-label attributes
- Form inputs have proper labels and aria-required
- Error messages use aria-live so screen readers announce them
- Filter buttons use aria-pressed to indicate active state
- Task list uses aria-live for dynamic updates

---



## How Auth Works in the UI

When the user logs in, we get back an access token and a refresh token. The access token goes into every API request via an Axios interceptor. When a request fails with 401, the interceptor automatically tries to refresh the token before retrying the original request — the user never sees an error for this. If the refresh also fails, they get redirected to login.

Protected routes check the auth context before rendering. If there's no token, the user is sent to `/login`. This means you can't access `/tasks` by typing it in the URL bar if you're not logged in.

---

## Environment

No environment variables needed. The API base URL is hardcoded to `http://localhost:8080/api` in `src/api/axios.js`. If the backend runs on a different port, update this file.

---

## Known Limitations

- Tokens stored in localStorage — httpOnly cookies would be more secure against XSS
- No unit tests — React Testing Library would be the next addition
- No infinite scroll/pagination — works fine for small lists, would need pagination for large datasets

## What I Would Do Next

1. Move refresh token to httpOnly cookie
2. Add React Testing Library tests for key flows
3. Add pagination or infinite scroll
4. Add loading skeletons instead of plain "Loading..." text
5. Add toast notifications instead of inline error banners
6. Add task priority levels and sorting options