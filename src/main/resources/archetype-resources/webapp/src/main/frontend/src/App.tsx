import { FormEvent, useState } from 'react'
import { registerCustomer } from './api'

type SubmissionState =
  | { status: 'idle' }
  | { status: 'submitting' }
  | { status: 'success'; id: string }
  | { status: 'error'; message: string }

export function App() {
  const [email, setEmail] = useState('')
  const [submission, setSubmission] = useState<SubmissionState>({
    status: 'idle',
  })

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmission({ status: 'submitting' })

    try {
      const response = await registerCustomer({ email })
      setSubmission({ status: 'success', id: response.id })
      setEmail('')
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Registration failed'
      setSubmission({ status: 'error', message })
    }
  }

  return (
    <main className='app-shell'>
      <section className='panel'>
        <p className='eyebrow'>Hexagonal Spring Boot</p>
        <h1>Customer registration</h1>
        <p className='summary'>
          This React frontend calls the REST inbound adapter, which invokes the
          same application use case as the Kafka inbound adapter.
        </p>

        <form onSubmit={onSubmit} className='registration-form'>
          <label htmlFor='email'>Email address</label>
          <div className='field-row'>
            <input
              id='email'
              type='email'
              value={email}
              onChange={event => setEmail(event.target.value)}
              placeholder='customer@example.com'
              required
            />
            <button type='submit' disabled={submission.status === 'submitting'}>
              {submission.status === 'submitting' ? 'Registering' : 'Register'}
            </button>
          </div>
        </form>

        {submission.status === 'success' && (
          <p className='status success'>Registered customer {submission.id}</p>
        )}
        {submission.status === 'error' && (
          <p className='status error'>{submission.message}</p>
        )}
      </section>
    </main>
  )
}
