<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getRevisionLimitConfig,
  updateRevisionLimitConfig,
  getCancelCoolingDaysConfig,
  updateCancelCoolingDaysConfig
} from '../../api/admin'

const loading = ref(false)
const saving = ref(false)
const savingCancel = ref(false)
const maxRevisionRequestCount = ref(2)
const cancelCoolingDays = ref(7)

const load = async () => {
  loading.value = true
  try {
    const result = await getRevisionLimitConfig()
    if (result.code === 200) {
      maxRevisionRequestCount.value = Number(result.data?.maxRevisionRequestCount || 2)
    }
    const cancelResult = await getCancelCoolingDaysConfig()
    if (cancelResult.code === 200) {
      cancelCoolingDays.value = Number(cancelResult.data?.cancelCoolingDays || 7)
    }
  } finally {
    loading.value = false
  }
}

const saveCancelCoolingDays = async () => {
  const value = Number(cancelCoolingDays.value)
  if (Number.isNaN(value) || value < 1 || value > 30) {
    ElMessage.error('注销冷静期需要在 1 到 30 天之间')
    return
  }
  savingCancel.value = true
  try {
    const result = await updateCancelCoolingDaysConfig(value)
    if (result.code === 200) {
      cancelCoolingDays.value = Number(result.data?.cancelCoolingDays || value)
      ElMessage.success('注销冷静期已保存')
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } finally {
    savingCancel.value = false
  }
}

const save = async () => {
  const value = Number(maxRevisionRequestCount.value)
  if (Number.isNaN(value) || value < 0 || value > 10) {
    ElMessage.error('修改次数上限需要在 0 到 10 之间')
    return
  }
  saving.value = true
  try {
    const result = await updateRevisionLimitConfig(value)
    if (result.code === 200) {
      maxRevisionRequestCount.value = Number(result.data?.maxRevisionRequestCount || value)
      ElMessage.success('系统设置已保存')
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <main class="page-shell">
    <section class="page-head">
      <div>
        <p class="eyebrow">系统配置</p>
        <h1>系统设置</h1>
        <p>配置会影响订单流程和平台规则，只有超级管理员可以修改。</p>
      </div>
      <button class="primary-btn" type="button" @click="load">刷新</button>
    </section>

    <section class="settings-card">
      <div class="setting-main">
        <span>订单修改次数上限</span>
        <strong>用户最多可申请 {{ maxRevisionRequestCount }} 次修改</strong>
        <p>超过次数后，用户仍有争议时进入平台介入，由客服专员处理。</p>
      </div>
      <div class="setting-control">
        <input v-model.number="maxRevisionRequestCount" type="number" min="0" max="10" :disabled="loading" />
        <button class="primary-btn" type="button" :disabled="saving || loading" @click="save">
          {{ saving ? '保存中' : '保存设置' }}
        </button>
      </div>
    </section>

    <section class="settings-card">
      <div class="setting-main">
        <span>账号注销冷静期</span>
        <strong>用户申请注销后 {{ cancelCoolingDays }} 天正式生效</strong>
        <p>冷静期内用户可登录并取消注销；冷静期结束后账号不可恢复，历史订单和评价保留并脱敏展示。</p>
      </div>
      <div class="setting-control">
        <input v-model.number="cancelCoolingDays" type="number" min="1" max="30" :disabled="loading" />
        <button class="primary-btn" type="button" :disabled="savingCancel || loading" @click="saveCancelCoolingDays">
          {{ savingCancel ? '保存中' : '保存设置' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page-shell { padding: 40px 46px 56px; }
.page-head { display: flex; justify-content: space-between; gap: 24px; align-items: flex-end; margin-bottom: 22px; }
.eyebrow { margin: 0 0 8px; color: #2563eb; font-weight: 800; }
.page-head h1 { margin: 0 0 8px; color: #0f172a; font-size: 30px; }
.page-head p { margin: 0; color: #64748b; }
.primary-btn { border: 0; border-radius: 4px; background: #2563eb; color: #fff; padding: 10px 18px; font-weight: 700; cursor: pointer; }
.primary-btn:disabled { opacity: .6; cursor: not-allowed; }
.settings-card { display: flex; justify-content: space-between; gap: 28px; align-items: center; background: #fff; border-radius: 8px; padding: 28px 32px; box-shadow: 0 8px 24px rgba(15, 23, 42, .08); margin-bottom: 18px; }
.setting-main { display: grid; gap: 8px; }
.setting-main span { color: #64748b; font-weight: 700; }
.setting-main strong { color: #0f172a; font-size: 22px; }
.setting-main p { margin: 0; color: #64748b; }
.setting-control { display: flex; gap: 12px; align-items: center; }
.setting-control input { width: 120px; border: 1px solid #cbd5e1; border-radius: 4px; padding: 10px 12px; font-size: 16px; }
</style>
