import request from './request'



export const getApplicationList = () =>
    request.get('/application/list')


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