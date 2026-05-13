import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('authToken')
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

request.interceptors.response.use(response => response, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('authToken')
    localStorage.removeItem('currentUser')
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userInfo')
  }
  return Promise.reject(error)
})

export default request