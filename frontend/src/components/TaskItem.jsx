import React, { useState } from 'react';
import { format } from 'date-fns';

export default function TaskItem({ task, onUpdate, onDelete, readOnly }) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(task.title);
  const [description, setDescription] = useState(task.description || '');
  const [completed, setCompleted] = useState(task.completed);
  const [dueDate, setDueDate] = useState(
    task.dueDate ? task.dueDate.slice(0, 16) : ''
  );

  const saveEdit = () => {
    if (!title.trim()) return;
    onUpdate(task.id, {
      title: title.trim(),
      description: description.trim(),
      completed: completed,
      dueDate: dueDate || null,
    });
    setEditing(false);
  };

  const cancelEdit = () => {
    setTitle(task.title);
    setDescription(task.description || '');
    setCompleted(task.completed);
    setDueDate(task.dueDate ? task.dueDate.slice(0, 16) : '');
    setEditing(false);
  };

  const toggleComplete = () => {
    onUpdate(task.id, {
      title: task.title,
      description: task.description,
      completed: !task.completed,
      dueDate: task.dueDate,
    });
  };

  return (
    <li
      className={`task-item ${task.completed ? 'completed' : ''} ${task.overdue ? 'overdue' : ''}`}
      aria-label={`Task: ${task.title}${task.overdue ? ', overdue' : ''}`}
    >
      {editing ? (
        <div className="task-edit">
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            aria-label="Edit title"
            placeholder="Task title *"
          />
          <input
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Description (optional)"
            aria-label="Edit description"
          />
          <input
            type="datetime-local"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            aria-label="Edit due date"
          />
          <div className="task-edit-completed">
            <input
              type="checkbox"
              id={`completed-${task.id}`}
              checked={completed}
              onChange={(e) => setCompleted(e.target.checked)}
              aria-label="Mark as completed"
            />
            <label htmlFor={`completed-${task.id}`}>Mark as completed</label>
          </div>
          <div className="task-edit-actions">
            <button className="btn btn-primary" onClick={saveEdit}>Save</button>
            <button className="btn btn-ghost" onClick={cancelEdit}>Cancel</button>
          </div>
        </div>
      ) : (
        <div className="task-content">
          <input
            type="checkbox"
            checked={task.completed}
            onChange={toggleComplete}
            disabled={readOnly}
            aria-label={`Mark "${task.title}" as ${task.completed ? 'incomplete' : 'complete'}`}
          />
          <div className="task-text">
            <span className="task-title">{task.title}</span>
            {task.description && (
              <span className="task-desc">{task.description}</span>
            )}
            <div className="task-meta">
              <span className="task-date">
                Added {format(new Date(task.createdAt), 'MMM d, yyyy')}
              </span>
              {task.dueDate && (
                <span className={`task-due ${task.overdue ? 'overdue-label' : ''}`}>
                  {task.overdue ? '⚠ Overdue · ' : 'Due '}
                  {format(new Date(task.dueDate), 'MMM d, yyyy HH:mm')}
                </span>
              )}
            </div>
          </div>
          {!readOnly && (
            <div className="task-actions">
              <button
                className="btn btn-ghost btn-sm"
                onClick={() => setEditing(true)}
                aria-label="Edit task"
              >
                ✏️
              </button>
              <button
                className="btn btn-danger btn-sm"
                onClick={() => onDelete(task.id)}
                aria-label="Delete task"
              >
                🗑
              </button>
            </div>
          )}
        </div>
      )}
    </li>
  );
}