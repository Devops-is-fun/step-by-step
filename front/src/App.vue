<script setup>
import { ref, computed, onMounted } from 'vue'
import * as api from './api.js'
import logo from './assets/logo.svg'

// Données
const tasks = ref([])
const error = ref('')
const loading = ref(false)

// Formulaire de création
const newTitle = ref('')
const newMinutes = ref(60)
const newDate = ref('')
const newTime = ref('')
const creatingTask = ref(false)

// Formulaires des étapes
const stepForms = ref({})
const scheduleForms = ref({})
const busyTaskIds = ref(new Set())

// Statistiques
const todoTasks = computed(() =>
  tasks.value.filter(task => task.status === 'TODO').length
)

const globalProgress = computed(() => {
  if (tasks.value.length === 0) return 0

  return Math.round(
    completedTasks.value / tasks.value.length * 100
  )
})
const completedTasks = computed(() =>
  tasks.value.filter(task => task.status === 'DONE').length
)

const inProgressTasks = computed(() =>
  tasks.value.filter(task => task.status === 'IN_PROGRESS').length
)

const statusLabels = {
  TODO: 'À faire',
  IN_PROGRESS: 'En cours',
  DONE: 'Terminée'
}

// Progression
function completedSteps(task) {
  return task.steps.filter(step => step.done).length
}

function progress(task) {
  if (task.steps.length === 0) return 0

  return Math.round(
    completedSteps(task) / task.steps.length * 100
  )
}

// Gestion des erreurs
function showError(err) {
  error.value =
    err.response?.data?.error ||
    'Impossible de communiquer avec le serveur.'
}

function formatTime(time) {
  return time ? time.slice(0, 5) : ''
}

function isTaskBusy(taskId) {
  return busyTaskIds.value.has(taskId)
}

function setTaskBusy(taskId, busy) {
  const taskIds = new Set(busyTaskIds.value)
  if (busy) {
    taskIds.add(taskId)
  } else {
    taskIds.delete(taskId)
  }
  busyTaskIds.value = taskIds
}

function replaceTask(updatedTask) {
  const index = tasks.value.findIndex(task => task.id === updatedTask.id)
  if (index === -1) return

  tasks.value[index] = updatedTask
  scheduleForms.value[updatedTask.id] = {
    plannedFor: updatedTask.plannedFor || '',
    plannedTime: formatTime(updatedTask.plannedTime),
  }
}

function getScheduleForm(task) {
  if (!scheduleForms.value[task.id]) {
    scheduleForms.value[task.id] = {
      plannedFor: task.plannedFor || '',
      plannedTime: formatTime(task.plannedTime),
    }
  }

  return scheduleForms.value[task.id]
}

// Récupérer les tâches
async function loadTasks() {
  loading.value = true
  error.value = ''

  try {
    tasks.value = await api.getTasks()
  } catch (err) {
    showError(err)
  } finally {
    loading.value = false
  }
}

// Créer une tâche
async function createTask() {
  error.value = ''

  if (!newTitle.value.trim()) return
  if (newTime.value && !newDate.value) {
    error.value = 'Choisissez une date pour définir une heure de début.'
    return
  }

  const task = {
    title: newTitle.value.trim(),
    estimatedMinutes: Number(newMinutes.value)
  }

  if (newDate.value) {
    task.plannedFor = newDate.value
  }
  if (newTime.value) {
    task.plannedTime = newTime.value
  }

  creatingTask.value = true
  try {
    const createdTask = await api.createTask(task)
    tasks.value = [...tasks.value, createdTask]

    newTitle.value = ''
    newMinutes.value = 60
    newDate.value = ''
    newTime.value = ''
  } catch (err) {
    showError(err)
  } finally {
    creatingTask.value = false
  }
}

// Formulaire propre à chaque tâche
function getStepForm(taskId) {
  if (!stepForms.value[taskId]) {
    stepForms.value[taskId] = {
      label: '',
      estimatedMinutes: 15
    }
  }

  return stepForms.value[taskId]
}

// Ajouter une étape
async function addStep(task) {
  error.value = ''
  if (isTaskBusy(task.id)) return

  const form = getStepForm(task.id)

  if (!form.label.trim()) return

  setTaskBusy(task.id, true)
  try {
    const updatedTask = await api.addStep(task.id, {
      label: form.label.trim(),
      estimatedMinutes: Number(form.estimatedMinutes)
    })

    replaceTask(updatedTask)
    form.label = ''
    form.estimatedMinutes = 15
  } catch (err) {
    showError(err)
  } finally {
    setTaskBusy(task.id, false)
  }
}

async function toggleStep(task, step, done) {
  error.value = ''
  if (isTaskBusy(task.id)) return

  setTaskBusy(task.id, true)
  try {
    replaceTask(await api.setStepDone(task.id, step.id, done))
  } catch (err) {
    showError(err)
  } finally {
    setTaskBusy(task.id, false)
  }
}

async function removeStep(task, step) {
  if (!window.confirm(`Supprimer l’étape « ${step.label} » ?`)) return
  error.value = ''
  if (isTaskBusy(task.id)) return

  setTaskBusy(task.id, true)
  try {
    replaceTask(await api.deleteStep(task.id, step.id))
  } catch (err) {
    showError(err)
  } finally {
    setTaskBusy(task.id, false)
  }
}

async function removeTask(task) {
  if (!window.confirm(`Supprimer la tâche « ${task.title} » et ses étapes ?`)) return
  error.value = ''
  if (isTaskBusy(task.id)) return

  setTaskBusy(task.id, true)
  try {
    await api.deleteTask(task.id)
    tasks.value = tasks.value.filter(current => current.id !== task.id)
    delete scheduleForms.value[task.id]
    delete stepForms.value[task.id]
  } catch (err) {
    showError(err)
  } finally {
    setTaskBusy(task.id, false)
  }
}

async function saveSchedule(task) {
  error.value = ''
  if (isTaskBusy(task.id)) return

  const form = getScheduleForm(task)
  if (form.plannedTime && !form.plannedFor) {
    error.value = 'Choisissez une date pour définir une heure de début.'
    return
  }

  setTaskBusy(task.id, true)
  try {
    replaceTask(await api.planTask(task.id, {
      plannedFor: form.plannedFor || null,
      plannedTime: form.plannedTime || null,
    }))
  } catch (err) {
    showError(err)
  } finally {
    setTaskBusy(task.id, false)
  }
}

// Chargement au démarrage
onMounted(loadTasks)
</script>


<template>
  <main class="container">
    <header>
      <img :src="logo" alt="Logo Step by Step" class="app-logo" />
      <h1>Step by Step</h1>
      <p>Un petit pas à la fois.</p>
    </header>

    <!-- Messages d'erreur -->
    <p v-if="error" class="error-message" role="alert">
      {{ error }}
    </p>

    <p v-if="loading">Chargement...</p>

    <!-- Création d'une tâche -->
    <section class="create-task">
      <h2>Créer une tâche</h2>
      <p class="subtitle">Un petit pas vers votre objectif.</p>

      <form @submit.prevent="createTask">
        <div class="form-group">
          <label for="title">Titre de la tâche</label>
          <input
            id="title"
            v-model="newTitle"
            placeholder="Ex : Réviser les cours"
            required
          />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="minutes">Durée estimée (min)</label>
            <input
              id="minutes"
              v-model.number="newMinutes"
              type="number"
              min="1"
              required
            />
          </div>

          <div class="form-group">
            <label for="planned-for">
              Date prévue (facultatif)
            </label>
            <input
              id="planned-for"
              v-model="newDate"
              type="date"
            />
          </div>
          <div class="form-group">
            <label for="planned-time">
              Heure de début (facultative)
            </label>
            <input
              id="planned-time"
              v-model="newTime"
              type="time"
              :required="!!newTime"
            />
          </div>
        </div>

        <button type="submit" :disabled="creatingTask">
          {{ creatingTask ? 'Création…' : '+ Créer la tâche' }}
        </button>
      </form>
    </section>

    <!-- Statistiques -->
    <h2>Mes tâches</h2>
    <p class="subtitle">Chaque petite étape compte.</p>

    <section class="statistics">
      <div class="stat">
        <span>Total des tâches</span>
        <strong>{{ tasks.length }}</strong>
      </div>

      <div class="stat">
        <span>À faire</span>
        <strong>{{ todoTasks }}</strong>
      </div>

      <div class="stat">
        <span>En cours</span>
        <strong>{{ inProgressTasks }}</strong>
      </div>

      <div class="stat">
        <span>Terminées</span>
        <strong>{{ completedTasks }}</strong>
      </div>
    </section>

    <!-- Progression globale -->
    <section class="global-progress">
      <h3>Progression globale</h3>

      <div class="progress-label">
        <strong>{{ globalProgress }} %</strong>
        <span>
          {{ completedTasks }}/{{ tasks.length }} tâches
        </span>
      </div>

      <div
        class="progress-bar"
        role="progressbar"
        aria-label="Progression globale"
        :aria-valuenow="globalProgress"
        aria-valuemin="0"
        aria-valuemax="100"
      >
        <div
          class="progress-fill"
          :style="{ width: globalProgress + '%' }"
        ></div>
      </div>
    </section>

    <!-- Liste des tâches -->
    <p v-if="!loading && tasks.length === 0">
      Aucune tâche pour le moment.
    </p>

    <article
      v-for="task in tasks"
      :key="task.id"
      class="task-card"
    >
      <div class="task-header">
        <h3>{{ task.title }}</h3>

        <div class="task-actions">
          <span class="status" :class="task.status">
            {{ statusLabels[task.status] }}
          </span>
          <button
            class="delete-button"
            type="button"
            :aria-label="'Supprimer la tâche ' + task.title"
            :disabled="isTaskBusy(task.id)"
            @click="removeTask(task)"
          >
            Supprimer
          </button>
        </div>
      </div>

      <p class="task-info">
        {{ task.estimatedMinutes }} min · {{ task.plannedFor || 'Non planifiée' }}
        <span v-if="task.plannedTime">à {{ formatTime(task.plannedTime) }}</span>
      </p>

      <!-- Progression de la tâche -->
      <div class="progress-label">
        <span>Progression</span>
        <strong>{{ progress(task) }} %</strong>
      </div>

      <div
        class="progress-bar"
        role="progressbar"
        :aria-label="'Progression : ' + task.title"
        :aria-valuenow="progress(task)"
        aria-valuemin="0"
        aria-valuemax="100"
      >
        <div
          class="progress-fill"
          :style="{ width: progress(task) + '%' }"
        ></div>
      </div>

      <p class="progress-info">
        {{ completedSteps(task) }}/{{ task.steps.length }}
        étapes terminées
      </p>

      <!-- Liste des étapes -->
      <ul class="steps">
        <li
          v-for="step in task.steps"
          :key="step.id"
        >
          <label class="step-label" :class="{ completed: step.done }">
            <input
              type="checkbox"
              :checked="step.done"
              :aria-label="'Terminer ' + step.label"
              :disabled="isTaskBusy(task.id)"
              @change="toggleStep(task, step, $event.target.checked)"
            />
            <span>{{ step.label }}</span>
          </label>

          <div class="step-actions">
            <span class="duration">
              {{ step.estimatedMinutes }} min
            </span>
            <button
              class="delete-button"
              type="button"
              :aria-label="'Supprimer l’étape ' + step.label"
              :disabled="isTaskBusy(task.id)"
              @click="removeStep(task, step)"
            >
              Supprimer
            </button>
          </div>
        </li>
      </ul>

      <form class="schedule-form" @submit.prevent="saveSchedule(task)">
        <h4>Planifier la tâche</h4>

        <div class="form-row">
          <div class="form-group">
            <label :for="'schedule-date-' + task.id">Date prévue</label>
            <input
              :id="'schedule-date-' + task.id"
              v-model="getScheduleForm(task).plannedFor"
              type="date"
              :disabled="isTaskBusy(task.id)"
            />
          </div>
          <div class="form-group">
            <label :for="'schedule-time-' + task.id">Heure de début</label>
            <input
              :id="'schedule-time-' + task.id"
              v-model="getScheduleForm(task).plannedTime"
              type="time"
              :disabled="isTaskBusy(task.id)"
            />
          </div>
        </div>

        <button type="submit" :disabled="isTaskBusy(task.id)">
          {{ isTaskBusy(task.id) ? 'Enregistrement…' : 'Enregistrer la planification' }}
        </button>
      </form>

      <!-- Ajouter une étape -->
      <form
        class="step-form"
        @submit.prevent="addStep(task)"
      >
        <h4>Ajouter une étape</h4>

        <label :for="'step-label-' + task.id">
          Nom de l'étape
        </label>

        <input
          :id="'step-label-' + task.id"
          v-model="getStepForm(task.id).label"
          placeholder="Nom de l'étape"
          required
        />

        <label :for="'step-minutes-' + task.id">
          Durée estimée (minutes)
        </label>

        <input
          :id="'step-minutes-' + task.id"
          v-model.number="getStepForm(task.id).estimatedMinutes"
          type="number"
          min="1"
          max="30"
          required
        />

        <button type="submit" :disabled="isTaskBusy(task.id)">
          {{ isTaskBusy(task.id) ? 'Enregistrement…' : '+ Ajouter une étape' }}
        </button>
      </form>
    </article>

    <footer>
      Step by Step · À votre rythme
    </footer>
  </main>
</template>


<style scoped>
.container {
  max-width: 760px;
  margin: 0 auto;
  padding: 32px 20px;
  font-family: Arial, sans-serif;
  color: #263d35;
}

header {
  margin-bottom: 32px;
}

h1 {
  font-size: 32px;
  color: #48765c;
  margin: 0;
}

header p,
.subtitle {
  color: #68786c;
}

.statistics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin: 24px 0;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  background: white;
  border: 1px solid #dfe7dd;
  border-radius: 12px;
}

.stat span {
  color: #68786c;
  font-size: 14px;
}

.stat strong {
  font-size: 28px;
  color: #48765c;
}

.task-card {
  background: white;
  border: 1px solid #dfe7dd;
  border-radius: 14px;
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px #00000005;
}

.task-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}

.task-header h3 {
  margin: 0;
}

.task-actions,
.step-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.task-info {
  font-size: 14px;
  color: #68786c;
}

.status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.TODO {
  background: #edf0ed;
  color: #657365;
}

.IN_PROGRESS {
  background: #e5edfc;
  color: #45669a;
}

.DONE {
  background: #e0f2e4;
  color: #32785b;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #68786c;
  margin-top: 20px;
  margin-bottom: 8px;
}

.progress-bar {
  height: 8px;
  background: #e7ede6;
  border-radius: 20px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #6b9e79;
  border-radius: 20px;
}

.steps {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.steps li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #edf0ed;
  font-size: 14px;
}

.step-label {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.step-label input {
  flex: 0 0 auto;
  margin: 0;
}

.completed {
  text-decoration: line-through;
  color: #849287;
}

.duration {
  color: #68786c;
  white-space: nowrap;
}

.delete-button {
  padding: 6px 9px;
  border: 1px solid #d88a84;
  border-radius: 6px;
  background: white;
  color: #a33b32;
  cursor: pointer;
}

.delete-button:disabled,
button:disabled {
  cursor: wait;
  opacity: 0.65;
}

footer {
  text-align: center;
  color: #68786c;
  font-size: 13px;
  margin-top: 32px;
}

@media (max-width: 480px) {
  .stat {
    padding: 12px;
  }

  .task-card {
    padding: 16px;
  }
}
.step-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #dfe7dd;
}

.schedule-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #dfe7dd;
}

.schedule-form h4 {
  margin: 0;
}

.schedule-form input {
  width: 100%;
  padding: 10px;
  border: 1px solid #dfe7dd;
  border-radius: 8px;
}

.schedule-form button {
  background: #48765c;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.step-form h4 {
  margin: 0;
}

.step-form input {
  padding: 10px;
  border: 1px solid #dfe7dd;
  border-radius: 8px;
}

.step-form button {
  background: #48765c;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.create-task {
  background: white;
  padding: 24px;
  border: 1px solid #dfe7dd;
  border-radius: 14px;
  margin-bottom: 32px;
}

.create-task h2 {
  margin: 0 0 8px;
}

.create-task form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-top: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.create-task .form-row {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.create-task label {
  font-size: 14px;
  font-weight: 500;
  color: #48765c;
}

.create-task input {
  width: 100%;
  padding: 12px;
  border: 1px solid #dfe7dd;
  border-radius: 8px;
  font-size: 15px;
  background: #f9fbf8;
}

.create-task input:focus {
  outline: 2px solid #6b9e79;
}

.create-task button {
  width: 100%;
  background: #48765c;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
}

.create-task button:hover {
  background: #365b46;
}

@media (max-width: 600px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .create-task .form-row {
    grid-template-columns: 1fr;
  }
}
.error-message {
  background: #fff0f0;
  color: #b42318;
  border: 1px solid #f5c2c2;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 20px;
}
.statistics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin: 24px 0;
}

.global-progress {
  background: white;
  padding: 24px;
  border: 1px solid #dfe7dd;
  border-radius: 14px;
  margin-bottom: 24px;
}

.global-progress h3 {
  margin-top: 0;
}

.progress-info {
  font-size: 13px;
  color: #68786c;
}

@media (max-width: 600px) {
  .statistics {
    grid-template-columns: repeat(2, 1fr);
  }
}

.app-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  margin-bottom: 12px;
}

header {
  text-align: center;
}
</style>