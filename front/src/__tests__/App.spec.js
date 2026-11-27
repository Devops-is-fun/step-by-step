
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import {
  render,
  screen,
  fireEvent,
  waitFor,
  cleanup,
} from '@testing-library/vue'
import '@testing-library/jest-dom/vitest'

import App from '../App.vue'
import * as api from '../api.js'

vi.mock('../api.js', () => ({
  getTasks: vi.fn(),
  createTask: vi.fn(),
  addStep: vi.fn(),
  setStepDone: vi.fn(),
  deleteStep: vi.fn(),
  deleteTask: vi.fn(),
  planTask: vi.fn(),
}))

const mockTask = {
  id: 1,
  title: 'Préparer le projet DevOps',
  estimatedMinutes: 60,
  plannedFor: null,
  plannedTime: null,
  status: 'TODO',
  steps: [
    {
      id: 1,
      position: 1,
      label: 'Lire les consignes',
      estimatedMinutes: 15,
      done: false,
    },
  ],
}

beforeEach(() => {
  vi.resetAllMocks()
  api.getTasks.mockResolvedValue([])
})

afterEach(() => {
  cleanup()
})

describe('Step by Step', () => {
  // Test 1 : Affichage des tâches et des étapes
  it('affiche les tâches et leurs étapes', async () => {
    api.getTasks.mockResolvedValue([mockTask])

    render(App)

    expect(
      await screen.findByText('Préparer le projet DevOps'),
    ).toBeInTheDocument()

    expect(
      screen.getByText('Lire les consignes', { exact: false }),
    ).toBeInTheDocument()
  })

  // Test 2 : Création d'une tâche
  it('envoie les bonnes données lors de la création', async () => {
    api.createTask.mockResolvedValue(mockTask)

    render(App)

    await fireEvent.update(
      screen.getByLabelText('Titre de la tâche'),
      'Réviser les cours',
    )

    await fireEvent.click(
      screen.getByRole('button', { name: /créer la tâche/i }),
    )

    await waitFor(() => {
      expect(api.createTask).toHaveBeenCalledWith({
        title: 'Réviser les cours',
        estimatedMinutes: 60,
      })
    })
  })

  it('envoie la date et l’heure de début lors de la création', async () => {
    api.createTask.mockResolvedValue(mockTask)

    render(App)

    await fireEvent.update(screen.getByLabelText('Titre de la tâche'), 'Réviser les cours')
    await fireEvent.update(screen.getByLabelText('Date prévue (facultatif)'), '2026-11-02')
    await fireEvent.update(screen.getByLabelText('Heure de début (facultative)'), '09:30')
    await fireEvent.click(screen.getByRole('button', { name: /créer la tâche/i }))

    await waitFor(() => {
      expect(api.createTask).toHaveBeenCalledWith({
        title: 'Réviser les cours',
        estimatedMinutes: 60,
        plannedFor: '2026-11-02',
        plannedTime: '09:30',
      })
    })
  })

  // Test 3 : Gestion des erreurs de l'API
  it('affiche le message d’erreur retourné par le backend', async () => {
    const message =
      'Une tâche non terminée est déjà planifiée pour cette journée.'

    api.createTask.mockRejectedValue({
      response: {
        status: 400,
        data: {
          error: message,
        },
      },
    })

    render(App)

    await fireEvent.update(
      screen.getByLabelText('Titre de la tâche'),
      'Nouvelle tâche',
    )

    await fireEvent.click(
      screen.getByRole('button', { name: /créer la tâche/i }),
    )

    expect(await screen.findByRole('alert')).toHaveTextContent(message)
  })

  // Test 4 : Ajout d'une étape
  it('ajoute une étape à une tâche existante', async () => {
    api.getTasks.mockResolvedValue([mockTask])
    api.addStep.mockResolvedValue(mockTask)

    render(App)

    await screen.findByText('Préparer le projet DevOps')

    await fireEvent.update(
      screen.getByLabelText("Nom de l'étape"),
      'Écrire les tests',
    )

    await fireEvent.click(
      screen.getByRole('button', { name: /ajouter une étape/i }),
    )

    await waitFor(() => {
      expect(api.addStep).toHaveBeenCalledWith(1, {
        label: 'Écrire les tests',
        estimatedMinutes: 15,
      })
    })
  })

  it('met à jour une étape et son statut avec la réponse du serveur', async () => {
    const updatedTask = {
      ...mockTask,
      status: 'DONE',
      steps: [{ ...mockTask.steps[0], done: true }],
    }
    api.getTasks.mockResolvedValue([mockTask])
    api.setStepDone.mockResolvedValue(updatedTask)

    render(App)

    await fireEvent.click(await screen.findByRole('checkbox', { name: 'Terminer Lire les consignes' }))

    await waitFor(() => {
      expect(api.setStepDone).toHaveBeenCalledWith(1, 1, true)
    })
    expect(await screen.findByText('Terminée')).toBeInTheDocument()
    expect(screen.getByRole('checkbox', { name: 'Terminer Lire les consignes' })).toBeChecked()
  })

  it('confirme puis supprime une étape et applique la réponse serveur', async () => {
    const updatedTask = { ...mockTask, status: 'TODO', steps: [] }
    api.getTasks.mockResolvedValue([mockTask])
    api.deleteStep.mockResolvedValue(updatedTask)
    vi.spyOn(window, 'confirm').mockReturnValue(true)

    render(App)

    await fireEvent.click(await screen.findByRole('button', { name: 'Supprimer l’étape Lire les consignes' }))

    await waitFor(() => {
      expect(api.deleteStep).toHaveBeenCalledWith(1, 1)
    })
    expect(window.confirm).toHaveBeenCalled()
    expect(screen.queryByText('Lire les consignes', { exact: false })).not.toBeInTheDocument()
  })

  it('confirme la suppression d’une tâche', async () => {
    api.getTasks.mockResolvedValue([mockTask])
    api.deleteTask.mockResolvedValue(undefined)
    vi.spyOn(window, 'confirm').mockReturnValue(true)

    render(App)

    await fireEvent.click(await screen.findByRole('button', { name: 'Supprimer la tâche Préparer le projet DevOps' }))

    await waitFor(() => {
      expect(api.deleteTask).toHaveBeenCalledWith(1)
    })
    expect(screen.queryByText('Préparer le projet DevOps')).not.toBeInTheDocument()
  })

  it('enregistre la date et l’heure d’une planification', async () => {
    const updatedTask = {
      ...mockTask,
      plannedFor: '2026-11-02',
      plannedTime: '09:30:00',
    }
    api.getTasks.mockResolvedValue([mockTask])
    api.planTask.mockResolvedValue(updatedTask)

    render(App)

    await screen.findByText('Préparer le projet DevOps')
    await fireEvent.update(screen.getByLabelText('Date prévue', { exact: true }), '2026-11-02')
    await fireEvent.update(screen.getByLabelText('Heure de début', { exact: true }), '09:30')
    await fireEvent.click(screen.getByRole('button', { name: 'Enregistrer la planification' }))

    await waitFor(() => {
      expect(api.planTask).toHaveBeenCalledWith(1, {
        plannedFor: '2026-11-02',
        plannedTime: '09:30',
      })
    })
    expect(await screen.findByText('à 09:30')).toBeInTheDocument()
  })

  it('conserve la planification saisie lorsque le serveur la refuse', async () => {
    api.getTasks.mockResolvedValue([mockTask])
    api.planTask.mockRejectedValue({
      response: { data: { error: 'Cette date est déjà prise.' } },
    })

    render(App)

    await screen.findByText('Préparer le projet DevOps')
    const dateInput = screen.getByLabelText('Date prévue', { exact: true })
    const timeInput = screen.getByLabelText('Heure de début', { exact: true })
    await fireEvent.update(dateInput, '2026-11-02')
    await fireEvent.update(timeInput, '09:30')
    await fireEvent.click(screen.getByRole('button', { name: 'Enregistrer la planification' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('Cette date est déjà prise.')
    expect(dateInput).toHaveValue('2026-11-02')
    expect(timeInput).toHaveValue('09:30')
    expect(screen.getByText(/Non planifiée/)).toBeInTheDocument()
  })
})
