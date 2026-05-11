import request from './request'

export const getMyApplication = (userId) =>
    request.get('/application/my', { params: { userId } })

export const submitApplication = (data) =>
    request.post('/application/submit', data)

export const getApplicationList = () =>
    request.get('/application/list')

// 统一参数名为 applicationId（后端使用 @RequestParam("applicationId")）
export const approveApplication = (applicationId) =>
    request.post('/application/approve', null, { params: { applicationId } })

export const rejectApplication = (applicationId, rejectReason) =>
    request.post('/application/reject', null, { params: { applicationId, rejectReason } })


export const uploadLicenseImage = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/application/uploadImage', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}