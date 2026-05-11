<template>
  <div class="order-create">
    <div class="header">
      <h2>创建订单</h2>
      
      <!-- 业务选择器 -->
      <div class="service-selector">
        <label for="service-type">选择服务类型：</label>
        <select id="service-type" v-model="selectedServiceType">
          <option value="ONLINE_CONSULT">在线法律咨询</option>
          <option value="PHONE_CONSULT">电话法律咨询</option>
          <option value="DOCUMENT_WRITING">文书代写</option>
          <option value="CONTRACT_REVIEW">合同审核</option>
          <option value="MARRIAGE_FAMILY">婚姻家事</option>
          <option value="LITIGATION_AGENT">诉讼代理</option>
        </select>
      </div>
    </div>

    <!-- 动态表单组件 -->
    <div class="form-container">
      <component :is="currentForm" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'

// 导入所有表单组件
import FormOnlineConsult from '@/components/order/FormOnlineConsult.vue'
import FormPhoneConsult from '@/components/order/FormPhoneConsult.vue'
import FormDocWriting from '@/components/order/FormDocWriting.vue'
import FormContractReview from '@/components/order/FormContractReview.vue'
import FormMarriageFamily from '@/components/order/FormMarriageFamily.vue'
import FormLitigation from '@/components/order/FormLitigation.vue'

const route = useRoute()

// 默认选择在线咨询
const selectedServiceType = ref('ONLINE_CONSULT')

// 根据路由参数初始化选中的服务类型
onMounted(() => {
  if (route.query.type) {
    selectedServiceType.value = route.query.type
  }
})

// 监听路由参数变化，若在同一个页面内点击 Header 的其他服务
watch(() => route.query.type, (newType) => {
  if (newType) {
    selectedServiceType.value = newType
  }
})

// 根据选择的类型动态渲染对应的表单组件
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
      return FormOnlineConsult // 默认表单
  }
})
</script>

<style scoped>
.order-create {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  color: #333;
}

.service-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.service-selector select {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  min-width: 150px;
}

.service-selector select:focus {
  border-color: #2563eb;
}

.form-container {
  padding: 10px 0;
}
</style>
