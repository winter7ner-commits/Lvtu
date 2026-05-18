<script setup>
import { computed, onMounted, ref } from 'vue'
import { getUsers } from '../../api/admin'

const users = ref([])
const keyword = ref('')

const typeText = (type) => ({ 1: '普通用户', 2: '律师', 3: '管理员' }[type] || '-')
const statusText = (status) => ({ 0: '禁用', 1: '正常' }[status] || '-')
const authText = (status) => ({ 0: '未认证', 1: '已认证', 2: '驳回' }[status] || '-')

const filteredUsers = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return users.value
  return users.value.filter((item) =>
    [item.username, item.phone, item.email, item.region].some((field) =>
      String(field || '').toLowerCase().includes(value)
    )
  )
})

onMounted(async () => {
  const result = await getUsers()
  users.value = result.code === 200 ? result.data || [] : []
})
</script>

<template>
  <main class="page-shell">
    <div class="page-head">
      <h1>用户管理</h1>
      <input v-model="keyword" class="search-input" placeholder="搜索用户名、电话、邮箱、地区" />
    </div>

    <section class="content-card">
      <div class="card-title">用户列表（{{ filteredUsers.length }}个）</div>
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th><th>用户名</th><th>电话</th><th>邮箱</th><th>类型</th><th>状态</th><th>认证</th><th>地区</th><th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredUsers" :key="item.user_id">
            <td>{{ item.user_id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.phone || '-' }}</td>
            <td>{{ item.email || '-' }}</td>
            <td>{{ typeText(item.user_type) }}</td>
            <td>{{ statusText(item.status) }}</td>
            <td>{{ authText(item.auth_status) }}</td>
            <td>{{ item.region || '-' }}</td>
            <td>{{ item.created_time }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<style scoped>
.page-shell { padding: 48px 46px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; }
h1 { margin: 0; color: #1d8fd6; font-size: 31px; font-weight: 800; }
.search-input { width: 360px; border: 1px solid #d8dee9; border-radius: 4px; padding: 12px 14px; font-size: 15px; }
.content-card { background: #fff; border-radius: 6px; padding: 34px 42px; box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08); overflow-x: auto; }
.card-title { margin-bottom: 24px; font-size: 23px; font-weight: 800; }
.data-table { min-width: 1120px; width: 100%; border-collapse: collapse; }
.data-table th { background: #f7f7fb; font-size: 16px; text-align: left; padding: 18px; }
.data-table td { border-top: 1px solid #e6e8ef; padding: 16px 18px; font-size: 15px; }
</style>
