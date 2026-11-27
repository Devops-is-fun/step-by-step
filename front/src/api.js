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

export async function setStepDone(taskId, stepId, done) {
  const response = await axios.put(
    `/api/tasks/${taskId}/steps/${stepId}/done`,
    { done }
  )
  return response.data
}

export async function deleteStep(taskId, stepId) {
  const response = await axios.delete(`/api/tasks/${taskId}/steps/${stepId}`)
  return response.data
}

export async function deleteTask(taskId) {
  await axios.delete(`/api/tasks/${taskId}`)
}

export async function planTask(taskId, schedule) {
  const response = await axios.put(
    `/api/tasks/${taskId}/planned-for`,
    schedule
  )
  return response.data
}