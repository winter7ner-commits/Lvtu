import { getUserVerification } from '@/api/user'

const parseStoredJson = (key) => {
  try {
    return JSON.parse(localStorage.getItem(key) || 'null')
  } catch {
    return null
  }
}

const unwrapResponse = (response) => response?.data?.data ?? response?.data ?? response ?? null

const getUserId = (user) => {
  return user?.userId || user?.id || Number(localStorage.getItem('userId')) || null
}

const formatAccountType = (value) => {
  const type = Number(value)
  if (type === 2) return '律师用户'
  if (type === 3) return '管理员'
  return '个人用户'
}

const maskPhone = (value) => {
  const text = String(value || '')
  if (text.length < 7) return text
  return `${text.slice(0, 3)}****${text.slice(-4)}`
}

const maskIdCard = (value) => {
  const text = String(value || '')
  if (text.length < 8) return text
  return `${text.slice(0, 4)}**********${text.slice(-4)}`
}

const isVerified = (user = {}) => {
  return user.verificationStatus === 1 ||
    user.verification_status === 1 ||
    user.isVerified === true ||
    user.verified === true ||
    user.realNameVerified === true
}

const normalizeUserInfo = (user = {}) => {
  const verified = isVerified(user)
  const idCard = user.idCardNumber || user.id_card_number || user.idCard || user.idNumber || user.identityCard || user.cardNo || ''

  return {
    realName: user.realName || user.realname || user.real_name || '',
    phone: maskPhone(user.phone || user.mobile || user.phoneNumber || ''),
    idCard: maskIdCard(idCard),
    accountType: user.accountType || formatAccountType(user.userType),
    verified,
    verifiedText: verified ? '已认证' : '未认证'
  }
}

const assignUserInfo = (formData, user) => {
  Object.assign(formData, normalizeUserInfo(user))
}

export const applyCurrentUserInfo = async (formData) => {
  const currentUser = parseStoredJson('currentUser') || {}
  const userInfo = parseStoredJson('userInfo') || {}
  const mergedLocalUser = { ...userInfo, ...currentUser }

  assignUserInfo(formData, mergedLocalUser)

  const userId = getUserId(mergedLocalUser)
  if (!userId) return

  try {
    const verification = unwrapResponse(await getUserVerification(userId))
    if (verification && typeof verification === 'object') {
      assignUserInfo(formData, { ...mergedLocalUser, ...verification })
    }
  } catch {
    // Keep local account info when verification details are unavailable.
  }
}
