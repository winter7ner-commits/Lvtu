<template>
  <div class="page">
    <div class="bg-glow glow1"></div>
    <div class="bg-glow glow2"></div>

    <div class="page-container">
      <div class="page-header">
        <div class="header-content">
          <button class="back-btn" @click="goBack">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
            返回
          </button>
          <h2>实名认证</h2>
          <p>请填写真实身份信息，平台将进行人工审核</p>
        </div>
      </div>

      <div class="form-card glass">
        <el-form :model="formData" label-position="top" class="custom-form">
          <div class="form-grid">
            <el-form-item label="真实姓名">
              <el-input
                v-model="formData.realName"
                placeholder="请输入您的真实姓名"
                :class="{ 'is-error': errors.realName }"
              />
              <div v-if="errors.realName" class="error-message">{{ errors.realName }}</div>
            </el-form-item>

            <el-form-item label="身份证号">
              <el-input
                v-model="formData.idCardNo"
                placeholder="请输入18位身份证号"
                maxlength="18"
                show-word-limit
                :class="{ 'is-error': errors.idCardNo }"
              />
              <div v-if="errors.idCardNo" class="error-message">{{ errors.idCardNo }}</div>
            </el-form-item>

            <el-form-item label="手机号码">
              <el-input
                v-model="formData.phone"
                placeholder="请输入11位手机号码"
                maxlength="11"
                show-word-limit
                :class="{ 'is-error': errors.phone }"
              />
              <div v-if="errors.phone" class="error-message">{{ errors.phone }}</div>
            </el-form-item>

            <el-form-item label="身份证照片" class="full">
              <div class="upload-area" :class="{ 'has-image': imageUrl, uploading: submitting }" @click="triggerFileInput">
                <template v-if="!imageUrl">
                  <div class="upload-placeholder">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="17 8 12 3 7 8"/>
                      <line x1="12" y1="3" x2="12" y2="15"/>
                    </svg>
                    <p>点击上传身份证照片</p>
                    <span>支持 JPG / PNG，≤10MB</span>
                  </div>
                </template>
                <template v-else>
                  <img :src="imageUrl" class="preview-image" @error="handleImageError" />
                  <button type="button" class="replace-btn" @click.stop.prevent="triggerFileInput">重新上传</button>
                </template>
                <div v-if="submitting" class="uploading-mask">
                  <div class="spinner"></div>
                  <span>上传中...</span>
                </div>
              </div>
              <input
                ref="fileInput"
                id="fileInput"
                type="file"
                accept="image/jpeg,image/png"
                style="display:none"
                @change="handleFileChange"
              />
              <div v-if="errors.idCardImage" class="error-message">{{ errors.idCardImage }}</div>
            </el-form-item>
          </div>

          <div class="actions">
            <button type="button" class="primary-btn" :disabled="submitting || isPending" @click="handleSubmit">
              <svg v-if="submitting" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="2" x2="12" y2="6"/>
                <line x1="12" y1="18" x2="12" y2="22"/>
                <line x1="4.93" y1="4.93" x2="7.76" y2="7.76"/>
                <line x1="16.24" y1="16.24" x2="19.07" y2="19.07"/>
                <line x1="2" y1="12" x2="6" y2="12"/>
                <line x1="18" y1="12" x2="22" y2="12"/>
              </svg>
              {{ submitting ? '提交中...' : (isPending ? '已有审核中申请' : '提交认证') }}
            </button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitRealNameApplication, uploadIdCardImage } from '@/api/realname'

const router = useRouter()
const fileInput = ref(null)
const imageUrl = ref('')
const submitting = ref(false)
const isPending = ref(false)

const formData = reactive({
  realName: '',
  idCardNo: '',
  phone: '',
  idCardImage: null
})

const errors = reactive({
  realName: '',
  idCardNo: '',
  phone: '',
  idCardImage: ''
})

const goBack = () => {
  router.go(-1)
}

const validateForm = () => {
  let isValid = true
  errors.realName = ''
  errors.idCardNo = ''
  errors.phone = ''
  errors.idCardImage = ''

  if (!formData.realName.trim()) {
    errors.realName = '请输入真实姓名'
    isValid = false
  }

  if (!formData.idCardNo.trim()) {
    errors.idCardNo = '请输入身份证号'
    isValid = false
  } else if (!/^\d{18}$/.test(formData.idCardNo)) {
    errors.idCardNo = '身份证号必须是18位数字'
    isValid = false
  }

  if (!formData.phone.trim()) {
    errors.phone = '请输入手机号码'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    errors.phone = '手机号码格式不正确'
    isValid = false
  }

  if (!formData.idCardImage) {
    errors.idCardImage = '请上传身份证照片'
    isValid = false
  }

  return isValid
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  formData.idCardImage = file
  imageUrl.value = URL.createObjectURL(file)
  errors.idCardImage = ''
}

const handleImageError = () => {
  imageUrl.value = ''
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  submitting.value = true
  try {
    const imageResponse = await uploadIdCardImage(formData.idCardImage)
    if (imageResponse?.code !== 200) {
      throw new Error(imageResponse?.message || '上传身份证照片失败')
    }

    const applicationData = {
      realName: formData.realName,
      idCardNo: formData.idCardNo,
      phone: formData.phone,
      idCardImageUrl: imageResponse.data
    }

    const response = await submitRealNameApplication(applicationData)
    if (response?.code === 200) {
      ElMessage.success('实名认证申请成功！')
      router.push('/auth-center')
    } else {
      throw new Error(response?.message || '实名认证申请失败')
    }
  } catch (error) {
    console.error('实名认证错误:', error)
    ElMessage.error(error.message || '实名认证失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: calc(100vh - 70px);
  padding: 40px 20px;
  background: radial-gradient(circle at top left, rgba(168,183,201,0.25), transparent 40%),
    radial-gradient(circle at bottom right, rgba(125,148,172,0.18), transparent 45%),
    linear-gradient(180deg, #F5F8FB 0%, #EEF3F7 100%);
  position: relative;
}

.bg-glow {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  filter: blur(100px);
  z-index: 0;
}
.glow1 { background: rgba(168,183,201,0.18); top: -120px; left: -100px; }
.glow2 { background: rgba(125,148,172,0.16); bottom: -120px; right: -100px; }

.page-container {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.page-header {
  margin-bottom: 32px;
  max-width: 680px;
  margin-left: auto;
  margin-right: auto;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,0.42);
  border: 1px solid rgba(255,255,255,0.28);
  color: #1e40af;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  width: fit-content;
}

.back-btn:hover {
  background: rgba(255,255,255,0.6);
  transform: translateX(-2px);
}

.page-header h2 {
  font-size: 32px;
  color: #42566E;
  margin: 0;
  font-weight: 600;
}

.page-header p {
  color: #6F8298;
  font-size: 14px;
  margin: 0;
}

.glass {
  background: rgba(255,255,255,0.42);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255,255,255,0.28);
  box-shadow: 0 10px 30px rgba(95,116,139,0.10);
  border-radius: 32px;
}

.form-card {
  max-width: 680px;
  margin: 0 auto;
  padding: 32px 28px;
  position: relative;
  z-index: 1;
}

.custom-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.full {
  grid-column: 1 / -1;
}



:deep(.el-form-item__label) {
  font-size: 13px;
  color: #8c8fa3;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 12px 16px;
  box-shadow: none;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover), :deep(.el-input__wrapper.is-focus) {
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
}

:deep(.el-input__inner) {
  font-size: 14px;
  color: #1f2a56;
  font-weight: 400;
}

:deep(.el-form-item__error) {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 4px;
}

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  border: 2px dashed #d9e1e8;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255,255,255,0.6);
  min-height: 200px;
  position: relative;
  overflow: hidden;
}

.upload-area:hover {
  border-color: #1e40af;
  background: rgba(255,255,255,0.8);
}

.upload-area.has-image {
  border-style: solid;
  padding: 0;
  height: 200px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.upload-placeholder svg {
  margin-bottom: 12px;
  color: #1e40af;
  width: 40px;
  height: 40px;
}

.upload-placeholder p {
  font-size: 16px;
  color: #42566E;
  font-weight: 600;
  margin-bottom: 8px;
}

.upload-placeholder span {
  font-size: 13px;
  color: #6F8298;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.replace-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255,255,255,0.28);
  border-radius: 10px;
  padding: 8px 16px;
  font-size: 13px;
  color: #1e40af;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1;
  box-shadow: 0 2px 6px rgba(30, 64, 175, 0.1);
}

.replace-btn:hover {
  background: rgba(30, 64, 175, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(30, 64, 175, 0.15);
}

.uploading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 2;
  border-radius: 16px;
}

.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid rgba(30, 64, 175, 0.1);
  border-radius: 50%;
  border-top-color: #1e40af;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.uploading-mask span {
  font-size: 15px;
  color: #42566E;
  font-weight: 600;
}

.error-message {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding-left: 16px;
  position: relative;
}

.error-message::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background-color: #ff4d4f;
  border-radius: 50%;
}

.actions {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.primary-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  background: #1e40af;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(30, 64, 175, 0.2);
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
  justify-content: center;
}

.primary-btn:hover {
  background: #1d3a9e;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
  transform: translateY(-2px);
}

.primary-btn:disabled {
  background: #a0aec0;
  box-shadow: none;
  cursor: not-allowed;
  transform: none;
}

.primary-btn.submitting {
  pointer-events: none;
}

.ghost-btn {
  padding: 12px 24px;
  border: 1px solid rgba(30, 64, 175, 0.2);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.6);
  color: #1e40af;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.ghost-btn:hover {
  background: rgba(30, 64, 175, 0.1);
  transform: translateY(-2px);
}

@media (max-width: 960px) {
  .page {
    padding: 32px 16px;
  }

  .page-container {
    max-width: 720px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 680px) {
  .page-header {
    margin-bottom: 24px;
  }

  .form-card {
    padding: 24px;
  }

  .actions {
    width: 100%;
  }

  .primary-btn,
  .ghost-btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page {
    padding: 24px 12px;
  }

  .header-main h2 {
    font-size: 26px;
  }

  .form-card {
    padding: 20px;
  }
}
</style>