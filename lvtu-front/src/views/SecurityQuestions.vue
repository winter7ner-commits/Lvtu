<template>
  <div class="security-questions-page">
    <div class="security-questions-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-bg-glow glow1"></div>
        <div class="header-bg-glow glow2"></div>
        <div class="header-content">
          <button class="back-btn" @click="goBack">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
            返回
          </button>
          <div class="title-section">
            <div class="title-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                <circle cx="12" cy="12" r="10"/>
                <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
            </div>
            <div>
              <h2>安全问答设置</h2>
              <p>设置安全问题，用于找回密码和身份验证</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 安全问答卡片 -->
      <div class="questions-card glass">
        <div class="card-header">
          <h3>设置您的安全问题</h3>
          <p>请选择并回答以下安全问题，这些信息将用于验证您的身份</p>
        </div>

        <form @submit.prevent="handleSubmit" class="questions-form">
          <!-- 问题1 -->
          <div class="question-group">
            <label class="question-label">
              <span class="label-text">问题 1</span>
              <span class="required">*</span>
            </label>
            <select v-model="formData.question1" class="question-select" :class="{ error: errors.question1 }">
              <option value="">请选择一个问题</option>
              <option value="birthplace">您的出生地是哪里？</option>
              <option value="firstPet">您第一只宠物的名字是什么？</option>
              <option value="motherName">您母亲的姓名是什么？</option>
              <option value="childhoodFriend">您童年最好的朋友叫什么？</option>
              <option value="firstSchool">您的第一所学校叫什么？</option>
            </select>
            <div v-if="errors.question1" class="error-message">{{ errors.question1 }}</div>
            
            <input
              v-if="formData.question1"
              type="text"
              v-model="formData.answer1"
              placeholder="请输入您的答案"
              class="answer-input"
              :class="{ error: errors.answer1 }"
            />
            <div v-if="errors.answer1" class="error-message">{{ errors.answer1 }}</div>
          </div>

          <!-- 问题2 -->
          <div class="question-group">
            <label class="question-label">
              <span class="label-text">问题 2</span>
              <span class="required">*</span>
            </label>
            <select v-model="formData.question2" class="question-select" :class="{ error: errors.question2 }">
              <option value="">请选择一个问题</option>
              <option value="favoriteBook">您最喜欢的书是什么？</option>
              <option value="firstJob">您的第一份工作是什么？</option>
              <option value="fatherName">您父亲的姓名是什么？</option>
              <option value="favoriteFood">您最喜欢的食物是什么？</option>
              <option value="dreamCity">您最想去哪个城市？</option>
            </select>
            <div v-if="errors.question2" class="error-message">{{ errors.question2 }}</div>
            
            <input
              v-if="formData.question2"
              type="text"
              v-model="formData.answer2"
              placeholder="请输入您的答案"
              class="answer-input"
              :class="{ error: errors.answer2 }"
            />
            <div v-if="errors.answer2" class="error-message">{{ errors.answer2 }}</div>
          </div>

          <!-- 问题3 -->
          <div class="question-group">
            <label class="question-label">
              <span class="label-text">问题 3</span>
              <span class="required">*</span>
            </label>
            <select v-model="formData.question3" class="question-select" :class="{ error: errors.question3 }">
              <option value="">请选择一个问题</option>
              <option value="favoriteMovie">您最喜欢的电影是什么？</option>
              <option value="firstCar">您的第一辆车是什么品牌？</option>
              <option value="childhoodHero">您童年的偶像是谁？</option>
              <option value="favoriteSport">您最喜欢的运动是什么？</option>
              <option value="bestVacation">您最难忘的一次旅行是去哪里？</option>
            </select>
            <div v-if="errors.question3" class="error-message">{{ errors.question3 }}</div>
            
            <input
              v-if="formData.question3"
              type="text"
              v-model="formData.answer3"
              placeholder="请输入您的答案"
              class="answer-input"
              :class="{ error: errors.answer3 }"
            />
            <div v-if="errors.answer3" class="error-message">{{ errors.answer3 }}</div>
          </div>

          <!-- 提交按钮 -->
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="goBack">取消</button>
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? '保存中...' : '保存设置' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 提示信息 -->
      <div class="tips-box glass">
        <div class="tips-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
        </div>
        <div class="tips-content">
          <h4>温馨提示</h4>
          <ul>
            <li>请选择不容易被他人猜到的问题和答案</li>
            <li>答案区分大小写，请牢记您输入的内容</li>
            <li>建议定期更新您的安全问题</li>
            <li>不要将答案告诉他人，确保账户安全</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(false)

const formData = reactive({
  question1: '',
  answer1: '',
  question2: '',
  answer2: '',
  question3: '',
  answer3: ''
})

const errors = reactive({
  question1: '',
  answer1: '',
  question2: '',
  answer2: '',
  question3: '',
  answer3: ''
})

// 验证表单
const validateForm = () => {
  let isValid = true
  
  // 清空错误信息
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })

  // 验证问题1
  if (!formData.question1) {
    errors.question1 = '请选择第一个问题'
    isValid = false
  }
  if (!formData.answer1.trim()) {
    errors.answer1 = '请输入答案'
    isValid = false
  } else if (formData.answer1.trim().length < 2) {
    errors.answer1 = '答案至少需要2个字符'
    isValid = false
  }

  // 验证问题2
  if (!formData.question2) {
    errors.question2 = '请选择第二个问题'
    isValid = false
  }
  if (!formData.answer2.trim()) {
    errors.answer2 = '请输入答案'
    isValid = false
  } else if (formData.answer2.trim().length < 2) {
    errors.answer2 = '答案至少需要2个字符'
    isValid = false
  }

  // 验证问题3
  if (!formData.question3) {
    errors.question3 = '请选择第三个问题'
    isValid = false
  }
  if (!formData.answer3.trim()) {
    errors.answer3 = '请输入答案'
    isValid = false
  } else if (formData.answer3.trim().length < 2) {
    errors.answer3 = '答案至少需要2个字符'
    isValid = false
  }

  // 检查问题是否重复
  const questions = [formData.question1, formData.question2, formData.question3]
  const uniqueQuestions = new Set(questions.filter(q => q))
  if (uniqueQuestions.size < questions.filter(q => q).length) {
    ElMessage.warning('请不要选择重复的问题')
    isValid = false
  }

  return isValid
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true
  try {
    // 这里可以调用后端API保存安全问答
    // await saveSecurityQuestions(formData)
    
    // 暂时保存到 localStorage（实际项目中应该保存到后端）
    localStorage.setItem('securityQuestions', JSON.stringify(formData))
    
    ElMessage.success('安全问答设置成功！')
    setTimeout(() => {
      goBack()
    }, 1500)
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/security-settings')
}
</script>

<style scoped>
.security-questions-page {
  min-height: calc(100vh - 70px);
  padding: 40px 20px;
  background: radial-gradient(circle at top left, rgba(168,183,201,0.25), transparent 40%),
    radial-gradient(circle at bottom right, rgba(125,148,172,0.18), transparent 45%),
    linear-gradient(180deg, #F5F8FB 0%, #EEF3F7 100%);
  position: relative;
  overflow: hidden;
}

.security-questions-container {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  margin-bottom: 40px;
  position: relative;
  padding: 30px 0;
}

.header-bg-glow {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  filter: blur(80px);
  z-index: -1;
}

.glow1 {
  background: rgba(168,183,201,0.18);
  top: -100px;
  left: -100px;
}

.glow2 {
  background: rgba(125,148,172,0.16);
  bottom: -100px;
  right: -100px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-icon {
  width: 56px;
  height: 56px;
  background: rgba(125,148,172,0.2);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5F748B;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(125,148,172,0.15);
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
  padding: 10px 18px;
  border-radius: 20px;
  transition: all 0.3s ease;
  width: fit-content;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.1);
}

.back-btn:hover {
  background: rgba(255,255,255,0.6);
  transform: translateX(-2px);
  box-shadow: 0 6px 16px rgba(30, 64, 175, 0.15);
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

/* 安全问答卡片 */
.questions-card {
  background: rgba(255,255,255,0.42);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255,255,255,0.28);
  box-shadow: 0 10px 30px rgba(95,116,139,0.10);
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 24px;
}

.card-header {
  margin-bottom: 32px;
}

.card-header h3 {
  color: #42566E;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.card-header p {
  color: #7D94AC;
  font-size: 14px;
  margin: 0;
}

/* 表单样式 */
.questions-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.question-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #42566E;
}

.required {
  color: #ff4d4f;
}

.question-select {
  padding: 12px 16px;
  border: 1px solid rgba(125,148,172,0.3);
  border-radius: 12px;
  font-size: 14px;
  color: #42566E;
  background: rgba(255,255,255,0.6);
  outline: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.question-select:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.question-select.error {
  border-color: #ff4d4f;
}

.answer-input {
  padding: 12px 16px;
  border: 1px solid rgba(125,148,172,0.3);
  border-radius: 12px;
  font-size: 14px;
  color: #42566E;
  background: rgba(255,255,255,0.6);
  outline: none;
  transition: all 0.3s ease;
}

.answer-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.answer-input.error {
  border-color: #ff4d4f;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

/* 按钮组 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.btn-primary {
  padding: 12px 32px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 12px 32px;
  border: 1px solid rgba(125,148,172,0.3);
  border-radius: 12px;
  background: rgba(255,255,255,0.6);
  color: #5F748B;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: rgba(255,255,255,0.8);
  border-color: rgba(125,148,172,0.5);
}

/* 提示框 */
.tips-box {
  background: rgba(255,255,255,0.42);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255,255,255,0.28);
  box-shadow: 0 10px 30px rgba(95,116,139,0.10);
  border-radius: 24px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.tips-icon {
  width: 48px;
  height: 48px;
  background: rgba(255,249,244,0.8);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff7875;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(255,120,117,0.15);
}

.tips-content h4 {
  color: #42566E;
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
}

.tips-content ul {
  margin: 0;
  padding-left: 20px;
  color: #7D94AC;
  font-size: 14px;
  line-height: 1.8;
}

.tips-content li {
  margin-bottom: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .security-questions-page {
    padding: 20px 12px;
  }

  .questions-card {
    padding: 24px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
  }

  .tips-box {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .page-header h2 {
    font-size: 28px;
  }

  .title-icon {
    width: 48px;
    height: 48px;
  }
}
</style>