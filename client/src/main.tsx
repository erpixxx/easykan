import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import EasyKan from './EasyKan.tsx'
import './scss/main.scss'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <EasyKan />
  </StrictMode>,
)
