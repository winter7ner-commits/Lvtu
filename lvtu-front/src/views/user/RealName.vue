<template>
  <main class="page">
    <section class="panel">
      <div class="header">
        <div>
          <h1>实名认证</h1>
          <p>提交真实身份信息后，由平台认证审核员进行审核。</p>
        </div>
        <el-tag :type="statusTagType" effect="light" round>{{ statusText }}</el-tag>
      </div>

      <div v-if="verification?.verificationStatus === 1" class="readonly-box">
        <div><span>真实姓名</span><strong>{{ verification.realName }}</strong></div>
        <div><span>身份证号</span><strong>{{ maskIdCard(verification.idCardNumber) }}</strong></div>
        <div><span>手机号</span><strong>{{ currentPhone }}</strong></div>
      </div>

      <div v-else>
        <div v-if="verification?.verificationStatus === 2" class="reject-box">
          <strong>驳回原因</strong>
          <p>{{ verification.rejectReason || '认证信息未通过审核，请修改后重新提交。' }}</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="form">
          <div class="grid">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="身份证号" prop="idCardNumber">
              <el-input v-model="form.idCardNumber" maxlength="18" show-word-limit placeholder="请输入18位身份证号" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" maxlength="11" show-word-limit placeholder="需与账号绑定手机号一致" />
            </el-form-item>
          </div>

          <div class="upload-grid">
            <el-form-item label="身份证正面照片" prop="idCardFrontUrl">
              <div class="upload-card" :class="{ filled: frontPreview }" @click="triggerFile('front')">
                <img v-if="frontPreview" :src="frontPreview" alt="身份证正面" />
                <div v-else class="placeholder">
                  <span>+</span>
                  <p>上传正面照片</p>
                </div>
              </div>
            </el-form-item>

            <el-form-item label="身份证反面照片" prop="idCardBackUrl">
              <div class="upload-card" :class="{ filled: backPreview }" @click="triggerFile('back')">
                <img v-if="backPreview" :src="backPreview" alt="身份证反面" />
                <div v-else class="placeholder">
                  <span>+</span>
                  <p>上传反面照片</p>
                </div>
              </div>
            </el-form-item>
          </div>

          <input ref="frontInput" type="file" accept="image/jpeg,image/jpg,image/png" hidden @change="handleFileChange($event, 'front')" />
          <input ref="backInput" type="file" accept="image/jpeg,image/jpg,image/png" hidden @change="handleFileChange($event, 'back')" />

          <div class="actions">
            <button type="button" class="ghost-btn" @click="handleBack">返回</button>
            <button type="button" class="primary-btn" :disabled="submitting || uploading || verification?.verificationStatus === 0" @click="submit">
              {{ verification?.verificationStatus === 0 ? '审核中' : submitting ? '提交中' : '提交认证' }}
            </button>
          </div>
        </el-form>
      </div>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { getUserVerification, submitUserVerification, uploadVerificationImage } from '@/api/user'
import { goBack } from '@/utils/navigation'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const frontInput = ref(null)
const backInput = ref(null)
const uploading = ref(false)
const submitting = ref(false)
const verification = ref(null)
const frontPreview = ref('')
const backPreview = ref('')
const BASE_URL = ''

const currentPhone = computed(() => authStore.user?.phone || '')

const form = reactive({
  realName: '',
  idCardNumber: '',
  phone: '',
  idCardFrontUrl: '',
  idCardBackUrl: ''
})

const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '真实姓名需为2到20个字符', trigger: 'blur' }
  ],
  idCardNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  idCardFrontUrl: [{ required: true, message: '请上传身份证正面照片', trigger: 'change' }],
  idCardBackUrl: [{ required: true, message: '请上传身份证反面照片', trigger: 'change' }]
}

const statusText = computed(() => {
  const status = verification.value?.verificationStatus
  if (status === 0) return '待审核'
  if (status === 1) return '已认证'
  if (status === 2) return '已驳回'
  return '未认证'
})

const statusTagType = computed(() => {
  const status = verification.value?.verificationStatus
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
})

const fullUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : BASE_URL + url
}

const maskIdCard = (value) => {
  if (!value) return ''
  return value.replace(/^(.{6}).+(.{4})$/, '$1********$2')
}

const load = async () => {
  await authStore.initAuth()
  const userId = authStore.user?.userId
  form.phone = currentPhone.value
  if (!userId) return
  const result = await getUserVerification(userId)
  verification.value = result?.data || null
  if (verification.value?.verificationStatus !== undefined) {
    form.realName = verification.value.realName || ''
    form.idCardNumber = verification.value.idCardNumber || ''
    form.idCardFrontUrl = verification.value.idCardFrontUrl || ''
    form.idCardBackUrl = verification.value.idCardBackUrl || ''
    frontPreview.value = fullUrl(form.idCardFrontUrl)
    backPreview.value = fullUrl(form.idCardBackUrl)
  }
}

const triggerFile = (type) => {
  if (uploading.value || verification.value?.verificationStatus === 0) return
  if (type === 'front') frontInput.value?.click()
  else backInput.value?.click()
}

const handleFileChange = async (event, type) => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.warning('请上传 JPG/PNG 格式图片')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 10MB')
    return
  }
  uploading.value = true
  try {
    const result = await uploadVerificationImage(file)
    if (result?.code !== 200) throw new Error(result?.message || '上传失败')
    if (type === 'front') {
      form.idCardFrontUrl = result.data
      frontPreview.value = fullUrl(result.data)
      formRef.value?.clearValidate('idCardFrontUrl')
    } else {
      form.idCardBackUrl = result.data
      backPreview.value = fullUrl(result.data)
      formRef.value?.clearValidate('idCardBackUrl')
    }
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (form.phone !== currentPhone.value) {
    ElMessage.warning('实名认证手机号必须与账号绑定手机号一致')
    return
  }
  submitting.value = true
  try {
    const result = await submitUserVerification({ ...form })
    if (result?.code === 200) {
      ElMessage.success('实名认证申请已提交')
      await load()
    } else {
      ElMessage.error(result?.message || '提交失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleBack = () => {
  goBack(router, '/auth-center')
}

onMounted(load)
</script>

<style scoped>
.page { min-height: calc(100vh - 70px); padding: 40px 20px; background: #f5f7fb; }
.panel { width: min(900px, 100%); margin: 0 auto; background: #fff; border-radius: 10px; padding: 30px; box-shadow: 0 8px 24px rgba(15, 23, 42, .08); }
.header { display: flex; justify-content: space-between; align-items: flex-start; gap: 20px; margin-bottom: 24px; }
.header h1 { margin: 0 0 8px; color: #0f172a; font-size: 28px; }
.header p { margin: 0; color: #64748b; }
.form { display: grid; gap: 18px; }
.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 18px; }
.upload-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 18px; }
.upload-card { height: 210px; border: 1px dashed #94a3b8; border-radius: 8px; background: #f8fafc; display: flex; align-items: center; justify-content: center; cursor: pointer; overflow: hidden; }
.upload-card:hover { border-color: #2563eb; background: #eff6ff; }
.upload-card img { width: 100%; height: 100%; object-fit: cover; }
.placeholder { text-align: center; color: #64748b; }
.placeholder span { display: block; font-size: 34px; line-height: 1; color: #2563eb; }
.placeholder p { margin: 8px 0 0; font-weight: 700; }
.actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.primary-btn, .ghost-btn { border: 0; border-radius: 6px; padding: 11px 18px; font-weight: 800; cursor: pointer; }
.primary-btn { background: #2563eb; color: #fff; }
.primary-btn:disabled { opacity: .55; cursor: not-allowed; }
.ghost-btn { background: #e2e8f0; color: #334155; }
.readonly-box { display: grid; gap: 12px; background: #f8fafc; border-radius: 8px; padding: 18px; }
.readonly-box div { display: flex; justify-content: space-between; gap: 20px; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px; }
.readonly-box div:last-child { border-bottom: 0; padding-bottom: 0; }
.readonly-box span { color: #64748b; }
.reject-box { margin-bottom: 18px; background: #fff1f2; border: 1px solid #fecdd3; border-radius: 8px; padding: 14px 16px; color: #9f1239; }
.reject-box p { margin: 6px 0 0; }
@media (max-width: 760px) {
  .grid, .upload-grid { grid-template-columns: 1fr; }
  .panel { padding: 22px; }
}
</style>
