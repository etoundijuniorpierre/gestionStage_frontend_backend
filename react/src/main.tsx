import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

console.log('🔍 main_debug.tsx: Début du chargement');

const rootElement = document.getElementById('root');

if (!rootElement) {
  console.error('🚨 main_debug.tsx: Élément #root non trouvé!');
} else {
  console.log('✅ main_debug.tsx: Élément #root trouvé');
}

createRoot(rootElement!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)

console.log('🔍 main_debug.tsx: App rendu avec succès');
