import request from './request'

export const getMyApplication = (userId) =>
    request.get('/application/my', { params: { userId } })

export const submitApplication = (data) =>
    request.post('/application/submit', data)



export const uploadLicenseImage = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/application/uploadImage', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}