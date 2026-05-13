import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // State
  const userInfo = ref(null)
  const isLoggedIn = ref(false)

  // Getters
  const userName = computed(() => userInfo.value?.name || '我的')
  const userAvatar = computed(() => userInfo.value?.avatar || 'https://via.placeholder.com/32')

  // Actions
  const login = (user) => {
    userInfo.value = user
    isLoggedIn.value = true
    // 保存到 localStorage
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  const logout = () => {
    userInfo.value = null
    isLoggedIn.value = false
    // 清除 localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userInfo')
  }

  const restoreFromStorage = () => {
    const storedLoginStatus = localStorage.getItem('isLoggedIn')
    const storedUserInfo = localStorage.getItem('userInfo')
    
    if (storedLoginStatus === 'true' && storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
        isLoggedIn.value = true
      } catch (error) {
        console.error('Failed to restore auth state from storage:', error)
        logout()
      }
    }
  }

  return {
    userInfo,
    isLoggedIn,
    userName,
    userAvatar,
    login,
    logout,
    restoreFromStorage
  }
})
