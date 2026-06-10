import { defineStore } from 'pinia'

export const useAuthStore = defineStore('adminAuth', {
  state: () => ({
    token: localStorage.getItem('adminToken') || localStorage.getItem('authToken') || '',
    user: JSON.parse(localStorage.getItem('adminUser') || localStorage.getItem('currentUser') || 'null')
  }),
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user
  },
  actions: {
    setAuth(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem('adminToken', token)
      localStorage.setItem('adminUser', JSON.stringify(user))
      localStorage.setItem('authToken', token)
      localStorage.setItem('currentUser', JSON.stringify(user))
      localStorage.setItem('isLoggedIn', 'true')
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminUser')
      localStorage.removeItem('authToken')
      localStorage.removeItem('currentUser')
      localStorage.removeItem('isLoggedIn')
    }
  }
})
