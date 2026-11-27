import axios from 'axios'

export async function getTasks() {
  const response = await axios.get('/api/tasks')
  return response.data
}

export async function createTask(task) {
  const response = await axios.post('/api/tasks', task)
  return response.data
}

export async function addStep(taskId, step) {
  const response = await axios.post(
    `/api/tasks/${taskId}/steps`,
    step
  )
  return response.data
}