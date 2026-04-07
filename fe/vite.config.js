import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// API Gateway at port 8090; Booking Service direct at port 8081
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/v1/auth': { target: 'http://localhost:8090', changeOrigin: true },
      '/api/v1/inventory': { target: 'http://localhost:8090', changeOrigin: true },
      '/api/v1/booking': { target: 'http://localhost:8090', changeOrigin: true },
      // Direct to booking-service for customer registration/login (gateway shadows these routes with auth-service)
      '/bs': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/bs/, ''),
      },
    },
  },
})
