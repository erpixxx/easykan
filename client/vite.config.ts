import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: ['unless-sell-pop-raleigh.trycloudflare.com'],
    port: 5173,
    open: true,
  },
})
