import React, { useState } from 'react';

export default function TaskForm({ onAdd }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setError('Title is required.');
      return;
    }
    onAdd({
      title: title.trim(),
      description: description.trim(),
      dueDate: dueDate || null
    });
    setTitle('');
    setDescription('');
    setDueDate('');
    setError('');
  };

  return (
    <form className="task-form" onSubmit={handleSubmit} aria-label="Add new task">
      {error && (
        <p className="field-error" role="alert">{error}</p>
      )}
      <div className="task-form-row">
        <input
          type="text"
          placeholder="Task title *"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          aria-label="Task title"
          aria-required="true"
          className="input-title"
        />
        <input
          type="text"
          placeholder="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          aria-label="Task description"
          className="input-desc"
        />
        <input
          type="datetime-local"
          value={dueDate}
          onChange={(e) => setDueDate(e.target.value)}
          aria-label="Due date"
          className="input-due"
        />
        <button
          type="submit"
          className="btn btn-primary"
          aria-label="Add task"
        >
          + Add
        </button>
      </div>
    </form>
  );
}