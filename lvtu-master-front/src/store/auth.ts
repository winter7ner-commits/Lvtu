import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('adminToken') || '')
  const user = ref(null)
  const isLoggedIn = ref(false)

  if (token.value) {
    const savedUser = localStorage.getItem('adminUser')
    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser)
        isLoggedIn.value = true
      } catch {
        token.value = ''
        localStorage.removeItem('adminToken')
        localStorage.removeItem('adminUser')
      }
    }
  }

  const setAuth = (newToken, newUser) => {
    token.value = newToken
    user.value = newUser
    isLoggedIn.value = true
    localStorage.setItem('adminToken', newToken)
    localStorage.setItem('adminUser', JSON.stringify(newUser))
  }

  const logout = () => {
    token.value = ''
    user.value = null
    isLoggedIn.value = false
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminUser')
  }

  return { token, user, isLoggedIn, setAuth, logout }
})