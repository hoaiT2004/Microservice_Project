import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// All requests go through API Gateway at port 8090
export default defineConfig({
    plugins: [react()],
    server: {
        port: 3000,
        proxy: {
            '/api/v1/auth': { target: 'http://localhost:8090', changeOrigin: true },
            '/api/v1/inventory': { target: 'http://localhost:8090', changeOrigin: true },
            '/api/v1/booking': { target: 'http://localhost:8090', changeOrigin: true },
            '/api/v1/orders': { target: 'http://localhost:8090', changeOrigin: true },
        },
    },
})