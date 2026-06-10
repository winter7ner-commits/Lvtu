<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>角色管理</h2>
      <p class="page-desc">创建后台账号，并分配超级管理员、认证审核员、运营管理员或客服专员角色。</p>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="后台角色">
          <el-select v-model="form.adminRole" placeholder="请选择后台角色">
            <el-option label="超级管理员" value="SUPER_ADMIN" />
            <el-option label="认证审核员" value="CERT_AUDITOR" />
            <el-option label="运营管理员" value="OPERATOR" />
            <el-option label="客服专员" value="CUSTOMER_SERVICE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">创建后台账号</el-button>
        </el-form-item>
      </el-form>
    </div>
    <section class="admin-list-card">
      <h3>后台账号</h3>
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th><th>用户名</th><th>手机号</th><th>邮箱</th><th>角色</th><th>状态</th><th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="adminLoading">
            <td colspan="7" class="empty">加载中...</td>
          </tr>
          <tr v-else-if="admins.length === 0">
            <td colspan="7" class="empty">暂无后台账号</td>
          </tr>
          <tr v-for="item in admins" v-else :key="item.user_id">
            <td>{{ item.user_id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.phone || '-' }}</td>
            <td>{{ item.email || '-' }}</td>
            <td><span class="role-tag">{{ roleText(item.admin_role) }}</span></td>
            <td>{{ item.status === 1 ? '正常' : '禁用' }}</td>
            <td>{{ item.created_time }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminRegister } from '../../api/auth'
import { getUsers } from '../../api/admin'
import { ADMIN_ROLE_LABELS } from '../../utils/adminPermissions'

const loading = ref(false)
const adminLoading = ref(false)
const admins = ref([])
const form = ref({
  username: '',
  password: '',
  email: '',
  phone: '',
  adminRole: 'OPERATOR'
})

const roleText = (role) => ADMIN_ROLE_LABELS[role || 'SUPER_ADMIN'] || role || '-'

const loadAdmins = async () => {
  adminLoading.value = true
  try {
    const result = await getUsers()
    const list = result.code === 200 ? result.data || [] : []
    admins.value = list.filter((item) => item.user_type === 3)
  } finally {
    adminLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password || !form.value.adminRole) {
    window.alert('请填写必需信息')
    return
  }
  loading.value = true
  try {
    const response = await adminRegister(form.value)
    if (response?.code === 200) {
      window.alert('后台账号创建成功')
      form.value = { username: '', password: '', email: '', phone: '', adminRole: 'OPERATOR' }
      await loadAdmins()
    } else {
      window.alert(response?.message || '创建失败')
    }
  } catch (error) {
    window.alert('创建失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(loadAdmins)
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 70px);
  display: grid;
  grid-template-columns: 420px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
  padding: 40px 16px;
  background: #f5f7fb;
}
.auth-card {
  width: 420px;
  padding: 32px;
  border-radius: 20px;
  background-color: #ffffff;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.08);
}
.auth-card h2 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #1f2a56;
}
.page-desc {
  margin: 0 0 24px;
  color: #64748b;
  line-height: 1.7;
}
.admin-list-card {
  min-width: 0;
  padding: 28px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.08);
  overflow-x: auto;
}
.admin-list-card h3 {
  margin: 0 0 20px;
  color: #1f2a56;
  font-size: 22px;
}
.data-table {
  min-width: 840px;
  width: 100%;
  border-collapse: collapse;
}
.data-table th {
  background: #f7f7fb;
  text-align: left;
  padding: 14px 16px;
}
.data-table td {
  border-top: 1px solid #e6e8ef;
  padding: 14px 16px;
}
.role-tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  border-radius: 4px;
  padding: 0 10px;
  background: #eef6ff;
  color: #1d4ed8;
  font-weight: 700;
  white-space: nowrap;
}
.empty {
  text-align: center;
  color: #94a3b8;
}
@media (max-width: 980px) {
  .auth-page {
    grid-template-columns: 1fr;
  }
  .auth-card {
    width: auto;
  }
}
</style>
