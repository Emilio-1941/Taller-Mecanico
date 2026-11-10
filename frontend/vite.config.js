import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  server: { port: 5173, strictPort: true },
  preview: {
    host: '0.0.0.0',
    allowedHosts: ['easygoing-integrity-production-8671.up.railway.app']
  }
})
