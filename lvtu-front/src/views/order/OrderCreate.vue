<template>
  <div class="page">
    <div :class="['order-workspace', { 'no-form-selected': !currentForm }]">
      <aside v-if="currentForm" class="form-side-nav" aria-label="服务表单导航">
        <p>表单导航</p>
        <button
          v-for="item in formNavItems"
          :key="item.key"
          type="button"
          :class="['nav-anchor', { active: activeNavKey === item.key }]"
          @click="scrollToFormTarget(item)"
        >
          <span>{{ item.indexLabel }}</span>
          {{ item.label }}
        </button>
      </aside>

      <div ref="orderCreateRef" class="order-create">
        <div class="header">
          <div>
            <p class="eyebrow">法律服务</p>
            <h2>创建订单</h2>
          </div>

          <div class="service-select-area">
            <div class="service-selector">
              <button
                v-for="service in visibleServiceOptions"
                :key="service.value"
                type="button"
                :class="['service-option', { active: selectedServiceType === service.value }]"
                @click="selectServiceType(service.value)"
              >
                <span>{{ service.label }}</span>
                <small>{{ service.desc }}</small>
              </button>
            </div>
            <p v-if="serviceTypeError" class="service-error">请选择服务类型</p>
          </div>
        </div>

        <div class="form-container">
          <component v-if="currentForm" :is="currentForm" />
          <div v-else class="empty-service-panel">
            <strong>请选择服务类型</strong>
            <span>选择后即可填写发布法律服务表单。</span>
          </div>
        </div>
      </div>

      <aside class="form-tools" aria-label="页面工具">
        <button type="button" class="back-top-btn" @click="scrollToTop">
          <span>↑</span>
          回到顶部
        </button>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import FormOnlineConsult from '@/components/order/FormOnlineConsult.vue'
import FormPhoneConsult from '@/components/order/FormPhoneConsult.vue'
import FormDocWriting from '@/components/order/FormDocWriting.vue'
import FormContractReview from '@/components/order/FormContractReview.vue'
import FormMarriageFamily from '@/components/order/FormMarriageFamily.vue'
import FormLitigation from '@/components/order/FormLitigation.vue'

const route = useRoute()

const selectedServiceType = ref('')
const orderCreateRef = ref(null)
const activeNavKey = ref('section-0')
const serviceTypeError = ref(false)
const isDirectLawyerOrder = computed(() => !!(route.query.targetLawyerId || route.query.lawyerId))

const serviceOptions = [
  { label: '在线法律咨询', value: 'ONLINE_CONSULT', desc: '图文沟通' },
  { label: '电话法律咨询', value: 'PHONE_CONSULT', desc: '预约通话' },
  { label: '文书代写', value: 'DOCUMENT_WRITING', desc: '起草修改' },
  { label: '合同审核', value: 'CONTRACT_REVIEW', desc: '风险排查' },
  { label: '婚姻家事', value: 'MARRIAGE_FAMILY', desc: '家事纠纷' },
  { label: '诉讼代理', value: 'LITIGATION_AGENT', desc: '案件代理' }
]

const serviceSectionLabels = {
  ONLINE_CONSULT: '图文咨询信息',
  PHONE_CONSULT: '电话咨询信息',
  DOCUMENT_WRITING: '文书撰写需求',
  CONTRACT_REVIEW: '合同审核信息',
  MARRIAGE_FAMILY: '婚姻家事信息',
  LITIGATION_AGENT: '诉讼案件信息'
}

const serviceOptionValues = serviceOptions.map((item) => item.value)

const visibleServiceOptions = computed(() => {
  if (isDirectLawyerOrder.value) {
    return serviceOptions.filter((item) => item.value === 'ONLINE_CONSULT')
  }
  return serviceOptions
})

const formNavItems = computed(() => [
  { key: 'section-0', indexLabel: '1', label: '实名信息', target: 'section', index: 0 },
  { key: 'section-1', indexLabel: '2', label: '联系信息', target: 'section', index: 1 },
  { key: 'section-2', indexLabel: '3', label: '基础信息', target: 'section', index: 2 },
  {
    key: 'section-3',
    indexLabel: '4',
    label: serviceSectionLabels[selectedServiceType.value] || '服务信息',
    target: 'section',
    index: 3
  },
  { key: 'section-4', indexLabel: '5', label: '材料上传', target: 'section', index: 4 },
  { key: 'section-5', indexLabel: '6', label: '授权确认', target: 'section', index: 5 },
  { key: 'actions', indexLabel: '✓', label: '提交申请', target: 'selector', selector: '.form-actions' }
])

const getFormTargetElement = (item) => {
  if (!orderCreateRef.value) return null

  if (item.target === 'section') {
    return orderCreateRef.value.querySelectorAll('.form-section')[item.index] || null
  }

  return orderCreateRef.value.querySelector(item.selector)
}

const updateActiveNav = () => {
  if (!currentForm.value) return

  const offset = 118
  let currentKey = formNavItems.value[0]?.key || ''

  for (const item of formNavItems.value) {
    const element = getFormTargetElement(item)
    if (!element) continue

    if (element.getBoundingClientRect().top <= offset) {
      currentKey = item.key
    }
  }

  activeNavKey.value = currentKey
}

const scrollToFormTarget = async (item) => {
  await nextTick()
  const element = getFormTargetElement(item)
  if (!element) return

  activeNavKey.value = item.key
  element.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const selectServiceType = async (serviceType) => {
  if (isDirectLawyerOrder.value && serviceType !== 'ONLINE_CONSULT') {
    selectedServiceType.value = 'ONLINE_CONSULT'
    serviceTypeError.value = false
    return
  }
  selectedServiceType.value = serviceType
  serviceTypeError.value = false
  activeNavKey.value = 'section-0'
  await nextTick()
  updateActiveNav()
}

onMounted(() => {
  if (isDirectLawyerOrder.value) {
    selectedServiceType.value = 'ONLINE_CONSULT'
    serviceTypeError.value = false
  } else if (serviceOptionValues.includes(route.query.type)) {
    selectedServiceType.value = route.query.type
    serviceTypeError.value = false
  } else {
    serviceTypeError.value = true
  }

  nextTick(updateActiveNav)
  window.addEventListener('scroll', updateActiveNav, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', updateActiveNav)
})

watch(() => route.query.type, (newType) => {
  if (isDirectLawyerOrder.value) {
    selectedServiceType.value = 'ONLINE_CONSULT'
    serviceTypeError.value = false
    activeNavKey.value = 'section-0'
    nextTick(updateActiveNav)
  } else if (newType) {
    selectedServiceType.value = serviceOptionValues.includes(newType) ? newType : ''
    serviceTypeError.value = !selectedServiceType.value
    activeNavKey.value = 'section-0'
    nextTick(updateActiveNav)
  } else {
    selectedServiceType.value = ''
    serviceTypeError.value = true
    activeNavKey.value = 'section-0'
  }
})

const currentForm = computed(() => {
  switch (selectedServiceType.value) {
    case 'ONLINE_CONSULT':
      return FormOnlineConsult
    case 'PHONE_CONSULT':
      return FormPhoneConsult
    case 'DOCUMENT_WRITING':
      return FormDocWriting
    case 'CONTRACT_REVIEW':
      return FormContractReview
    case 'MARRIAGE_FAMILY':
      return FormMarriageFamily
    case 'LITIGATION_AGENT':
      return FormLitigation
    default:
      return null
  }
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f5f5;
}

.order-workspace {
  display: grid;
  grid-template-columns: 168px minmax(0, 1040px) 112px;
  gap: 20px;
  align-items: start;
  width: min(1360px, 100%);
  margin: 0 auto;
}

.order-workspace.no-form-selected {
  grid-template-columns: minmax(0, 1040px) 112px;
  justify-content: center;
}

.form-side-nav,
.form-tools {
  position: sticky;
  top: 92px;
}

.form-side-nav {
  padding: 14px;
  border: 1px solid #e5eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.form-side-nav p {
  margin: 0 0 10px;
  color: #172033;
  font-size: 14px;
  font-weight: 800;
}

.nav-anchor {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  margin: 0;
  padding: 9px 10px;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: #475467;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  text-align: left;
  transition: all 0.2s ease;
}

.nav-anchor + .nav-anchor {
  margin-top: 4px;
}

.nav-anchor span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: #f2f4f7;
  color: #667085;
  font-size: 12px;
  flex: 0 0 auto;
}

.nav-anchor:hover,
.nav-anchor.active {
  background: #eff6ff;
  color: #1d4ed8;
}

.nav-anchor:hover span,
.nav-anchor.active span {
  background: #2563eb;
  color: #ffffff;
}

.form-tools {
  display: flex;
  justify-content: center;
  margin-top: 320px;
  top: calc(100vh - 150px);
}

.back-top-btn {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 82px;
  padding: 12px 10px;
  border: 1px solid #d6e4ff;
  border-radius: 14px;
  background: #ffffff;
  color: #1d4ed8;
  cursor: pointer;
  font-size: 13px;
  font-weight: 800;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  transition: all 0.2s ease;
}

.back-top-btn span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 999px;
  background: #eff6ff;
  font-size: 18px;
  line-height: 1;
}

.back-top-btn:hover {
  background: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
  transform: translateY(-1px);
}

.back-top-btn:hover span {
  background: rgba(255, 255, 255, 0.18);
}

.order-create {
  width: 100%;
  margin: 0;
  padding: 24px;
  background: #fff;
  border: 1px solid #e5eaf3;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #edf1f7;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
}

.header h2 {
  margin: 0;
  color: #172033;
  font-size: 28px;
}

.service-select-area {
  flex: 1;
  max-width: 520px;
}

.service-selector {
  display: grid;
  grid-template-columns: repeat(3, minmax(130px, 1fr));
  gap: 10px;
}

.service-option {
  min-height: 58px;
  padding: 10px 12px;
  border: 1px solid #dbe4f0;
  border-radius: 12px;
  background: #fafcff;
  color: #344054;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.service-option span,
.service-option small {
  display: block;
}

.service-option span {
  font-size: 14px;
  font-weight: 700;
}

.service-option small {
  margin-top: 4px;
  color: #667085;
}

.service-option:hover {
  border-color: #2563eb;
  color: #1d4ed8;
}

.service-option.active {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
  box-shadow: 0 6px 18px rgba(37, 99, 235, 0.12);
}

.service-error {
  margin: 8px 0 0;
  color: #f56c6c;
  font-size: 13px;
  font-weight: 700;
}

.form-container {
  padding: 0;
}

.empty-service-panel {
  display: flex;
  min-height: 260px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px dashed #bfd4ff;
  border-radius: 14px;
  background: #f8fbff;
  color: #667085;
  text-align: center;
}

.empty-service-panel strong {
  color: #172033;
  font-size: 20px;
}

.empty-service-panel span {
  font-size: 14px;
}

:deep(.form-container) {
  max-width: none;
  padding: 0;
}

:deep(.el-form) {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

:deep(.form-section) {
  margin-bottom: 0;
  border: 1px solid #e5eaf3;
  border-radius: 14px;
  overflow: hidden;
  scroll-margin-top: 92px;
}

:deep(.form-section .el-card__header) {
  padding: 16px 20px;
  background: #f6f8fb;
  color: #172033;
  font-weight: 700;
}

:deep(.form-section .el-card__body) {
  padding: 20px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner),
:deep(.el-cascader .el-input__wrapper) {
  border-radius: 10px;
}

:deep(.checkbox-list) {
  gap: 8px;
}

:deep(.risk-alert) {
  margin-bottom: 0;
  border-radius: 12px;
  scroll-margin-top: 92px;
}

:deep(.form-actions) {
  margin-top: 0;
  padding-top: 6px;
  scroll-margin-top: 92px;
}

@media (max-width: 1240px) {
  .order-workspace {
    grid-template-columns: 1fr;
  }

  .form-side-nav,
  .form-tools {
    position: static;
  }

  .form-side-nav {
    display: flex;
    align-items: center;
    gap: 8px;
    overflow-x: auto;
  }

  .form-side-nav p {
    flex: 0 0 auto;
    margin: 0 4px 0 0;
  }

  .nav-anchor {
    flex: 0 0 auto;
    width: auto;
  }

  .nav-anchor + .nav-anchor {
    margin-top: 0;
  }

  .form-tools {
    justify-content: flex-end;
    order: 3;
    margin-top: 0;
  }

  .back-top-btn {
    flex-direction: row;
    width: auto;
    padding: 10px 14px;
  }
}

@media (max-width: 900px) {
  .header {
    flex-direction: column;
  }

  .service-error {
    margin-top: 8px;
  }

  .service-select-area {
    width: 100%;
    max-width: none;
  }

  .service-selector {
    width: 100%;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .order-create {
    padding: 18px 14px;
  }

  .service-selector {
    grid-template-columns: 1fr;
  }
}
</style>
