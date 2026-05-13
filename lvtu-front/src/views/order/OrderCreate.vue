<template>
  <div class="page">
    <div class="order-create">
      <div class="header">
        <div>
          <p class="eyebrow">法律服务</p>
          <h2>创建订单</h2>
        </div>

        <div class="service-selector">
          <button
            v-for="service in serviceOptions"
            :key="service.value"
            type="button"
            :class="['service-option', { active: selectedServiceType === service.value }]"
            @click="selectedServiceType = service.value"
          >
            <span>{{ service.label }}</span>
            <small>{{ service.desc }}</small>
          </button>
        </div>
      </div>

      <div class="form-container">
        <component :is="currentForm" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import FormOnlineConsult from '@/components/order/FormOnlineConsult.vue'
import FormPhoneConsult from '@/components/order/FormPhoneConsult.vue'
import FormDocWriting from '@/components/order/FormDocWriting.vue'
import FormContractReview from '@/components/order/FormContractReview.vue'
import FormMarriageFamily from '@/components/order/FormMarriageFamily.vue'
import FormLitigation from '@/components/order/FormLitigation.vue'

const route = useRoute()

const selectedServiceType = ref('ONLINE_CONSULT')

const serviceOptions = [
  { label: '在线法律咨询', value: 'ONLINE_CONSULT', desc: '图文沟通' },
  { label: '电话法律咨询', value: 'PHONE_CONSULT', desc: '预约通话' },
  { label: '文书代写', value: 'DOCUMENT_WRITING', desc: '起草修改' },
  { label: '合同审核', value: 'CONTRACT_REVIEW', desc: '风险排查' },
  { label: '婚姻家事', value: 'MARRIAGE_FAMILY', desc: '家事纠纷' },
  { label: '诉讼代理', value: 'LITIGATION_AGENT', desc: '案件代理' }
]

onMounted(() => {
  if (route.query.type) {
    selectedServiceType.value = route.query.type
  }
})

watch(() => route.query.type, (newType) => {
  if (newType) {
    selectedServiceType.value = newType
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
      return FormOnlineConsult
  }
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f5f5;
}

.order-create {
  max-width: 1040px;
  margin: 0 auto;
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

.service-selector {
  display: grid;
  grid-template-columns: repeat(3, minmax(130px, 1fr));
  gap: 10px;
  flex: 1;
  max-width: 520px;
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

.form-container {
  padding: 0;
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
}

:deep(.form-actions) {
  margin-top: 0;
  padding-top: 6px;
}

@media (max-width: 900px) {
  .header {
    flex-direction: column;
  }

  .service-selector {
    width: 100%;
    max-width: none;
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
