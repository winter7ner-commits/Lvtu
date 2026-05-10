import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
<<<<<<< HEAD
import path from 'path'
=======
import path from 'path'   // ⭐新增
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
<<<<<<< HEAD
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: true
=======
      '@': path.resolve(__dirname, 'src')  // ⭐关键
    }
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
  }
})