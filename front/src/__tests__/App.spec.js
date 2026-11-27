
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
}))

const mockTask = {
  id: 1,
  title: 'Préparer le projet DevOps',
  estimatedMinutes: 60,
  plannedFor: null,
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
})
