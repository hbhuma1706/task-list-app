import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTasks } from '../hooks/useTasks';
import TaskForm from '../components/TaskForm';
import TaskItem from '../components/TaskItem';
import api from '../api/axios';

const FILTERS = ['All', 'Active', 'Completed', 'Overdue'];

export default function TasksPage() {
  const { auth, logout } = useAuth();
  const { tasks, loading, error, fetchTasks, addTask, updateTask, deleteTask, setError } = useTasks();
  const [filter, setFilter] = useState('All');
  const isReadOnly = auth?.role === 'READ_ONLY';

  useEffect(() => {
  fetchTasks();
}, []);

  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      const token = localStorage.getItem('token');
      await api.post('/auth/logout',
        { refreshToken },
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (e) {
      console.log('Logout error', e);
    } finally {
      logout();
    }
  };

  const filtered = tasks.filter((t) => {
    if (filter === 'Active') return !t.completed;
    if (filter === 'Completed') return t.completed;
    if (filter === 'Overdue') return t.overdue;
    return true;
  });

  return (
    <main className="tasks-page" role="main">
      <header className="tasks-header">
        <div>
          <h1>My Tasks</h1>
          <span className="role-badge">
            {auth?.username} · {auth?.role === 'READ_ONLY' ? 'Viewer' : 'Admin'}
          </span>
        </div>
        <button className="btn btn-ghost" onClick={handleLogout} aria-label="Logout">
          Logout
        </button>
      </header>

      {!isReadOnly && <TaskForm onAdd={addTask} />}

      {isReadOnly && (
        <div className="alert alert-info" role="status">
          👁 You have read-only access. You can view tasks but cannot make changes.
        </div>
      )}

      {error && (
        <div className="alert alert-error" role="alert" aria-live="assertive">
          {error}
          <button className="alert-close" onClick={() => setError(null)} aria-label="Dismiss">✕</button>
        </div>
      )}

      <nav className="filter-bar" aria-label="Task filters">
        {FILTERS.map((f) => (
          <button
            key={f}
            className={`filter-btn ${filter === f ? 'active' : ''}`}
            onClick={() => setFilter(f)}
            aria-pressed={filter === f}
          >
            {f}
            <span className="filter-count">
              {f === 'All' ? tasks.length
                : f === 'Active' ? tasks.filter(t => !t.completed).length
                : f === 'Completed' ? tasks.filter(t => t.completed).length
                : tasks.filter(t => t.overdue).length}
            </span>
          </button>
        ))}
      </nav>

      {loading && (
        <p className="loading" role="status" aria-live="polite">Loading tasks…</p>
      )}

      {!loading && filtered.length === 0 && (
        <p className="empty-state">
          No tasks here. {!isReadOnly && 'Add one above!'}
        </p>
      )}

      <ul className="task-list" aria-label="Task list" aria-live="polite">
        {filtered.map((task) => (
          <TaskItem
            key={task.id}
            task={task}
            onUpdate={updateTask}
            onDelete={deleteTask}
            readOnly={isReadOnly}
          />
        ))}
      </ul>
    </main>
  );
}