import { defineStore } from 'pinia'
import { me } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('authToken') || '',
    user: JSON.parse(localStorage.getItem('currentUser') || 'null'),
  }),
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user,
  },
  actions: {
    setAuth(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem('authToken', token)
      localStorage.setItem('currentUser', JSON.stringify(user))
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userInfo', JSON.stringify({
        name: user.username || '',
        avatar: user.avatarUrl || ''
      }))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('authToken')
      localStorage.removeItem('currentUser')
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
    },
    async initAuth() {
      if (!this.token) {
        return
      }
      try {
        const response = await me()
        if (response?.code === 200) {
          this.user = response.data
          localStorage.setItem('currentUser', JSON.stringify(response.data))
          localStorage.setItem('userInfo', JSON.stringify({
            name: response.data.username || '',
            avatar: response.data.avatarUrl || ''
          }))
        } else {
          this.logout()
        }
      } catch (error) {
        this.logout()
      }
    }
  },
})
