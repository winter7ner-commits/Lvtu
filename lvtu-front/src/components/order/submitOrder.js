import { ElMessage } from 'element-plus'
import { createOrder } from '@/api/order'

const DEFAULT_AMOUNTS = {
  101: 129,
  102: 99,
  103: 399,
  104: 299,
  105: 199,
  106: 899
}

const getCurrentUserId = () => {
  const localUserId = localStorage.getItem('userId')
  if (localUserId) {
    return Number(localUserId)
  }

  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId ? Number(currentUser.userId) : null
}

const normalizeAmount = (budget, serviceTypeId) => {
  const value = Number(budget)
  if (Number.isFinite(value) && value >= 0) {
    return value
  }
  return DEFAULT_AMOUNTS[serviceTypeId] || 0
}

export const submitOrderForm = async ({ formRef, formData, serviceTypeId }) => {
  if (!formRef.value) return null

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return null

  const userId = getCurrentUserId()
  if (!userId) {
    ElMessage.warning('请先登录后再提交订单')
    return null
  }

  const snapshot = JSON.parse(JSON.stringify(formData))
  const response = await createOrder({
    userId,
    serviceTypeId,
    totalAmount: normalizeAmount(snapshot.budget, serviceTypeId),
    status: '待接单',
    formData: JSON.stringify(snapshot)
  })

  if (response?.code === 200) {
    ElMessage.success(`订单提交成功，订单号：${response.data.orderId}`)
    return response.data
  }

  ElMessage.error(response?.message || '订单提交失败')
  return null
}
