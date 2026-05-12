<script setup>
import { onMounted, ref } from 'vue'
import { getAuthApplications } from '../../api/admin'

const applications = ref([])

const statusText = (status) => ({ 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '-')
const typeText = (type) => ({ 0: '首次入驻', 1: '资料变更' }[type] || '-')

onMounted(async () => {
  const result = await getAuthApplications()
  applications.value = result.code === 200 ? result.data || [] : []
})
</script>

<template>
  <main class="page-shell">
    <div class="page-head">
      <h1>认证审核</h1>
    </div>

    <section class="content-card">
      <div class="card-title">认证申请（{{ applications.length }}个）</div>
      <table class="data-table">
        <thead>
          <tr>
            <th>申请ID</th><th>用户</th><th>电话</th><th>律所</th><th>执业证号</th><th>执业开始</th><th>类型</th><th>状态</th><th>申请时间</th><th>审核时间</th><th>驳回原因</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in applications" :key="item.application_id">
            <td>{{ item.application_id }}</td>
            <td>{{ item.username || item.user_id }}</td>
            <td>{{ item.phone || '-' }}</td>
            <td>{{ item.law_firm }}</td>
            <td>{{ item.license_no }}</td>
            <td>{{ item.practice_start_year }}</td>
            <td>{{ typeText(item.apply_type) }}</td>
            <td>{{ statusText(item.status) }}</td>
            <td>{{ item.create_time }}</td>
            <td>{{ item.audit_time || '-' }}</td>
            <td>{{ item.reject_reason || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<style scoped>
.page-shell { padding: 48px 46px; }
.page-head { margin-bottom: 32px; }
h1 { margin: 0; color: #1d8fd6; font-size: 31px; font-weight: 800; }
.content-card { background: #fff; border-radius: 6px; padding: 34px 42px; box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08); overflow-x: auto; }
.card-title { margin-bottom: 24px; font-size: 23px; font-weight: 800; }
.data-table { min-width: 1320px; width: 100%; border-collapse: collapse; }
.data-table th { background: #f7f7fb; font-size: 16px; text-align: left; padding: 18px; }
.data-table td { border-top: 1px solid #e6e8ef; padding: 16px 18px; font-size: 15px; }
</style>
