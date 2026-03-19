import { useState, useCallback } from 'react';
import api from '../api/axios';

export function useTasks() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchTasks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get('/tasks');
      setTasks(res.data);
    } catch (e) {
      setError('Failed to load tasks.');
    } finally {
      setLoading(false);
    }
  }, []);

  const addTask = async (taskData) => {
    const tempId = Date.now();
    const optimistic = {
      id: tempId,
      ...taskData,
      completed: false,
      createdAt: new Date().toISOString(),
      overdue: false
    };
    setTasks((prev) => [optimistic, ...prev]);
    try {
      const res = await api.post('/tasks', taskData);
      setTasks((prev) => prev.map((t) => (t.id === tempId ? res.data : t)));
    } catch (e) {
      setTasks((prev) => prev.filter((t) => t.id !== tempId));
      setError('Failed to add task.');
    }
  };

  const updateTask = async (id, taskData) => {
    const previous = tasks.find((t) => t.id === id);
    setTasks((prev) => prev.map((t) => (t.id === id ? { ...t, ...taskData } : t)));
    try {
      const res = await api.put(`/tasks/${id}`, taskData);
      setTasks((prev) => prev.map((t) => (t.id === id ? res.data : t)));
    } catch (e) {
      setTasks((prev) => prev.map((t) => (t.id === id ? previous : t)));
      setError('Failed to update task.');
    }
  };

  const deleteTask = async (id) => {
    const previous = tasks.find((t) => t.id === id);
    setTasks((prev) => prev.filter((t) => t.id !== id));
    try {
      await api.delete(`/tasks/${id}`);
    } catch (e) {
      setTasks((prev) => [previous, ...prev]);
      setError('Failed to delete task.');
    }
  };

  return { tasks, loading, error, fetchTasks, addTask, updateTask, deleteTask, setError };
}