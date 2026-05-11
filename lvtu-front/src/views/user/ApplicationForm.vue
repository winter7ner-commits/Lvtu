<template>
  <div class="page">
    <div class="bg-glow glow1"></div>
    <div class="bg-glow glow2"></div>

    <div class="form-card">
      <div class="header">
        <div>
          <h1>{{ applyType === 0 ? '律师认证申请' : '律师资料修改' }}</h1>
          <p class="desc">请填写真实律师执业信息，平台将进行人工审核</p>
        </div>
        <div class="type-badge">{{ applyType === 0 ? '首次入驻' : '资料变更' }}</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="custom-form">
        <div class="form-grid">
          <el-form-item label="律师执业证号" prop="licenseNo">
            <el-input
                v-model="form.licenseNo"
                placeholder="请输入17位数字执业证号"
                maxlength="17"
                show-word-limit
                style="width: 280px"
            />
          </el-form-item>

          <el-form-item label="所属律所" prop="lawFirm">
            <el-input v-model="form.lawFirm" placeholder="请输入律所名称" style="width: 100px" />
          </el-form-item>

          <el-form-item label="执业开始年份" prop="practiceStartYear">
            <el-input-number
                v-model="form.practiceStartYear"
                :min="1980"
                :max="currentYear"
                controls-position="right"
                style="width: 100px"
            />
          </el-form-item>

          <el-form-item label="律师执业证照片" prop="licenseUrl" class="full">
            <div class="upload-area" :class="{ 'has-image': displayImageUrl, uploading }">
              <template v-if="!displayImageUrl">
                <div class="upload-placeholder" @click="triggerUpload">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="17 8 12 3 7 8"/>
                    <line x1="12" y1="3" x2="12" y2="15"/>
                  </svg>
                  <p>点击上传执业证书照片</p>
                  <span>支持 JPG / PNG，≤10MB</span>
                </div>
              </template>
              <template v-else>
                <img :src="displayImageUrl" class="preview-image" @error="handleImageError" />
                <button class="replace-btn" @click.stop="triggerUpload">重新上传</button>
              </template>
              <div v-if="uploading" class="uploading-mask">
                <div class="spinner"></div>
                <span>上传中...</span>
              </div>
            </div>
            <input ref="fileInputRef" type="file" accept="image/jpeg,image/jpg,image/png" style="display:none" @change="handleFileChange" />
          </el-form-item>
        </div>

        <div class="actions">
          <button type="button" class="ghost-btn" @click="goBack">返回</button>
          <button type="button" class="primary-btn" :class="{ submitting }" :disabled="submitting || isPending" @click="submit">
            <svg v-if="submitting" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="2" x2="12" y2="6"/>
              <line x1="12" y1="18" x2="12" y2="22"/>
              <line x1="4.93" y1="4.93" x2="7.76" y2="7.76"/>
              <line x1="16.24" y1="16.24" x2="19.07" y2="19.07"/>
              <line x1="2" y1="12" x2="6" y2="12"/>
              <line x1="18" y1="12" x2="22" y2="12"/>
            </svg>
            {{ submitting ? '提交中...' : (isPending ? '已有审核中申请' : '提交申请') }}
          </button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitApplication, uploadLicenseImage, getMyApplication } from '@/api/application'

const router = useRouter()
const route = useRoute()
const applyType = Number(route.query.apply_type ?? 0)
const currentYear = new Date().getFullYear()

const formRef = ref()
const fileInputRef = ref()
const submitting = ref(false)
const uploading = ref(false)
const displayImageUrl = ref('')
const isPending = ref(false)

const BASE_URL = 'http://localhost:8080'

const form = reactive({
  userId: null,
  licenseNo: '',
  lawFirm: '',
  practiceStartYear: currentYear - 5,
  licenseUrl: '',
  applyType
})

const getCurrentUserId = () => {
  const stored = localStorage.getItem('userId')
  return stored ? Number(stored) : null
}

const rules = {
  licenseNo: [
    { required: true, message: '请输入执业证号', trigger: 'blur' },
    { len: 17, message: '执业证号必须为17位', trigger: 'blur' },
    { pattern: /^\d{17}$/, message: '执业证号只能为17位数字', trigger: 'blur' }
  ],
  lawFirm: [{ required: true, message: '请输入律所名称', trigger: 'blur' }],
  practiceStartYear: [{ required: true, message: '请选择年份', trigger: 'change' }],
  licenseUrl: [{ required: true, message: '请上传证件照片', trigger: 'change' }]
}

// 检查是否有审核中的申请（禁止提交）
const checkPending = async () => {
  if (!form.userId) return
  try {
    const res = await getMyApplication(form.userId)
    const data = res?.data?.data || res?.data
    if (data && data.status === 0) {
      isPending.value = true
      ElMessage.warning('您已有审核中的申请，请等待结果')
    }
  } catch {}
}

// 加载已有申请记录（用于资料变更 或 重新申请）
const loadExisting = async () => {
  if (!form.userId) return
  try {
    const res = await getMyApplication(form.userId)
    const data = res?.data?.data || res?.data
    if (data && data.licenseNo) {
      // 如果有历史申请记录（无论是驳回、通过、还是审核中），都填充进来供修改
      form.licenseNo = data.licenseNo || ''
      form.lawFirm = data.lawFirm || ''
      form.practiceStartYear = data.practiceStartYear || currentYear - 5
      if (data.licenseUrl) {
        form.licenseUrl = data.licenseUrl
        displayImageUrl.value = data.licenseUrl.startsWith('http') ? data.licenseUrl : BASE_URL + data.licenseUrl
      }
      // 如果是重新申请（applyType=0 且有历史记录），可提示
      if (applyType === 0 && data.status === 2) {
        ElMessage.info('已自动填充上次被驳回的申请信息，请修改后重新提交')
      }
    }
  } catch {}
}

const triggerUpload = () => fileInputRef.value?.click()

const handleFileChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.warning('请上传 JPG/PNG 格式')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 10MB')
    return
  }
  uploading.value = true
  try {
    const res = await uploadLicenseImage(file)
    let relativeUrl = res?.data?.data || res?.data
    if (typeof relativeUrl === 'string') {
      form.licenseUrl = relativeUrl
      displayImageUrl.value = relativeUrl.startsWith('http') ? relativeUrl : BASE_URL + relativeUrl
      formRef.value?.clearValidate('licenseUrl')
      ElMessage.success('上传成功')
    } else throw new Error('上传失败')
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '上传失败')
    form.licenseUrl = ''
    displayImageUrl.value = ''
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

const handleImageError = () => {
  ElMessage.warning('图片加载失败，请重新上传')
  displayImageUrl.value = ''
  form.licenseUrl = ''
}

const submit = () => {
  if (isPending.value) {
    ElMessage.warning('已有审核中申请，无法提交')
    return
  }
  formRef.value.validate(async (valid) => {
    if (!valid) return
    form.userId = getCurrentUserId()
    submitting.value = true
    try {
      await submitApplication({ ...form })
      ElMessage.success('提交成功')
      router.push('/application-status')
    } catch (err) {
      ElMessage.error(err?.response?.data?.message || '提交失败')
    } finally {
      submitting.value = false
    }
  })
}

const goBack = () => {
  router.push('/auth-center')
}

onMounted(async () => {
  form.userId = getCurrentUserId()
  await checkPending()
  // 无论是资料变更（applyType=1）还是重新申请（applyType=0 且无审核中），都尝试加载历史记录
  if (applyType === 1 || (applyType === 0 && !isPending.value)) {
    await loadExisting()
  }
})
</script>

<style scoped>
/* 整体留白与白色卡片 */
.page {
  min-height: 100vh;
  padding: 32px 80px;
  background: radial-gradient(circle at top left, rgba(168,183,201,0.25), transparent 40%),
  radial-gradient(circle at bottom right, rgba(125,148,172,0.18), transparent 45%),
  linear-gradient(180deg, #F5F8FB 0%, #EEF3F7 100%);
  position: relative;
}
.bg-glow {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  filter: blur(90px);
}
.glow1 { background: rgba(168,183,201,0.18); top: -120px; left: -100px; }
.glow2 { background: rgba(125,148,172,0.15); bottom: -120px; right: -100px; }

.form-card {
  max-width: 900px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  position: relative;
  z-index: 2;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}
h1 {
  color: #1a2c3e;
  font-size: 28px;
  margin: 0 0 8px 0;
}
.desc {
  color: #6c7a8a;
  margin: 0;
}
.type-badge {
  padding: 8px 20px;
  border-radius: 30px;
  background: #e8edf2;
  color: #5a6e7c;
  font-size: 14px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, auto));
  gap: 24px 40px;
  align-items: start;
}
.full {
  grid-column: span 2;
}


.upload-area {
  border-radius: 16px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  border: 2px dashed #d0d8e3;
  transition: all 0.2s;
  background: #f8fafc;
  position: relative;
}
.upload-area:hover {
  border-color: #1a73e8;
  background: #f0f7ff;
}
.upload-area.has-image {
  border-style: solid;
  border-color: #52c41a;
  background: #f6ffed;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.upload-placeholder p {
  color: #5a6e7c;
  margin: 0;
}
.upload-placeholder span {
  color: #8c9aab;
  font-size: 12px;
}
.preview-image {
  width: 100%;
  max-height: 240px;
  object-fit: contain;
  border-radius: 12px;
}
.replace-btn {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0,0,0,0.7);
  color: white;
  border: none;
  border-radius: 24px;
  padding: 6px 14px;
  cursor: pointer;
  font-size: 12px;
}
.uploading-mask {
  position: absolute;
  inset: 0;
  background: rgba(255,255,255,0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-radius: 16px;
}
.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid #e0e6ed;
  border-top-color: #1a73e8;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
}
.primary-btn, .ghost-btn {
  border: none;
  padding: 10px 24px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.primary-btn {
  background: #1a73e8;
  color: white;
}
.primary-btn:hover:not(:disabled) {
  background: #1557b0;
  transform: translateY(-1px);
}
.primary-btn.submitting {
  opacity: 0.7;
  cursor: not-allowed;
}
.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.ghost-btn {
  background: #f0f2f5;
  color: #5a6e7c;
}
.ghost-btn:hover {
  background: #e4e9ef;
}

/* 修改 el-form-item 内的输入框短一些 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}
:deep(.el-input__wrapper),
:deep(.el-input-number) {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  border: 1px solid #e0e6ed;
  width: 280px;
}
:deep(.el-input-number) {
  width: 280px;
}
:deep(.el-form-item__label) {
  color: #5a6e7c;
  font-weight: 500;
}

</style>