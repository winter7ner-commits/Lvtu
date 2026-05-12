import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

request.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

export default request
