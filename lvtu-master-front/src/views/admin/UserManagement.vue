<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUser, updateUserStatus, deleteUser } from '../../api/admin'
import { useAuthStore } from '../../store/auth'
import { ADMIN_ROLE_LABELS, ADMIN_ROLE_OPTIONS, normalizeAdminRole } from '../../utils/adminPermissions'

const authStore = useAuthStore()

const users = ref([])
const keyword = ref('')
const typeFilter = ref('')
const statusFilter = ref('')
const roleFilter = ref('')
const loading = ref(false)
const saving = ref(false)
const editVisible = ref(false)
const personalInfoExpanded = ref(false)
const editForm = ref({
  userId: null,
  username: '',
  phone: '',
  email: '',
  region: '',
  userType: 1,
  status: 1,
  adminRole: ''
})

const USER_TYPE_OPTIONS = [
  { label: '普通用户', value: 1 },
  { label: '律师', value: 2 },
  { label: '管理员', value: 3 }
]

const STATUS_OPTIONS = [
  { label: '冻结', value: 0 },
  { label: '正常', value: 1 },
  { label: '封禁', value: 2 },
  { label: '注销冷静期', value: 3 },
  { label: '已注销', value: 4 }
]

const LAWYER_AUTH_OPTIONS = [
  { label: '未申请', value: 0 },
  { label: '审核中', value: 1 },
  { label: '已通过', value: 2 },
  { label: '已拒绝', value: 3 }
]

const field = (item, camelKey, snakeKey = camelKey) => item?.[camelKey] ?? item?.[snakeKey]
const rowId = (item) => field(item, 'userId', 'user_id')
const rowType = (item) => Number(field(item, 'userType', 'user_type') || 0)
const rowStatus = (item) => Number(field(item, 'status') ?? -1)
const rowAuthStatus = (item) => Number(field(item, 'authStatus', 'auth_status') ?? -1)
const rowVerified = (item) => Boolean(field(item, 'isVerified', 'is_verified'))
const rowRole = (item) => normalizeAdminRole({
  userType: rowType(item),
  adminRole: field(item, 'adminRole', 'admin_role')
})

const optionText = (options, value) => options.find((item) => item.value === Number(value))?.label || '-'
const typeText = (type) => optionText(USER_TYPE_OPTIONS, type)
const statusText = (status) => optionText(STATUS_OPTIONS, status)
const roleText = (role) => ADMIN_ROLE_LABELS[role] || role || '-'
const lawyerAuthText = (status) => optionText(LAWYER_AUTH_OPTIONS, status)

const statusClass = (status) => {
  if (status === 1) return 'badge-success'
  if (status === 0 || status === 3) return 'badge-warning'
  if (status === 2 || status === 4) return 'badge-danger'
  return 'badge-default'
}

const authClass = (status) => {
  if (status === 2) return 'badge-success'
  if (status === 1) return 'badge-warning'
  if (status === 3) return 'badge-danger'
  return 'badge-default'
}

const formatDate = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

const emptyToNull = (value) => {
  const text = String(value ?? '').trim()
  return text || null
}

const isSelf = (item) => {
  const currentId = authStore.user?.userId ?? authStore.user?.user_id
  return String(rowId(item)) === String(currentId)
}

const filteredUsers = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  return users.value.filter((item) => {
    const textMatched = !value || [
      rowId(item),
      field(item, 'username'),
      field(item, 'phone'),
      field(item, 'email'),
      field(item, 'region')
    ].some((content) => String(content || '').toLowerCase().includes(value))
    const typeMatched = !typeFilter.value || rowType(item) === Number(typeFilter.value)
    const statusMatched = !statusFilter.value || rowStatus(item) === Number(statusFilter.value)
    const roleMatched = !roleFilter.value || rowRole(item) === roleFilter.value
    return textMatched && typeMatched && statusMatched && roleMatched
  })
})

const metrics = computed(() => ({
  total: users.value.length,
  normal: users.value.filter((item) => rowStatus(item) === 1).length,
  admins: users.value.filter((item) => rowType(item) === 3).length,
  lawyers: users.value.filter((item) => rowType(item) === 2).length,
  pendingLawyerAuth: users.value.filter((item) => rowAuthStatus(item) === 1).length
}))

const loadUsers = async () => {
  loading.value = true
  try {
    const result = await getUsers()
    users.value = result.code === 200 ? result.data || [] : []
    if (result.code !== 200) {
      ElMessage.error(result.message || '用户列表加载失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '用户列表加载失败')
  } finally {
    loading.value = false
  }
}

const openEdit = (item) => {
  editForm.value = {
    userId: rowId(item),
    username: field(item, 'username') || '',
    phone: field(item, 'phone') || '',
    email: field(item, 'email') || '',
    region: field(item, 'region') || '',
    userType: rowType(item),
    status: rowStatus(item),
    adminRole: rowRole(item)
  }
  editVisible.value = true
}

const saveEdit = async () => {
  if (!editForm.value.userId) return
  saving.value = true
  try {
    const payload = {
      phone: emptyToNull(editForm.value.phone),
      email: emptyToNull(editForm.value.email),
      region: emptyToNull(editForm.value.region),
      status: Number(editForm.value.status)
    }
    if (Number(editForm.value.userType) === 3) {
      payload.adminRole = editForm.value.adminRole
    }
    const result = await updateUser(editForm.value.userId, payload)
    if (result.code === 200) {
      ElMessage.success('用户信息已保存')
      editVisible.value = false
      await loadUsers()
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const setStatus = async (item, status) => {
  const actionText = status === 1 ? '恢复该用户' : '冻结该用户'
  const confirmed = await ElMessageBox.confirm(`确认${actionText}？`, '用户状态变更', {
    type: status === 1 ? 'success' : 'warning'
  }).then(() => true).catch(() => false)
  if (!confirmed) return
  try {
    const result = await updateUserStatus(rowId(item), status)
    if (result.code === 200) {
      ElMessage.success(status === 1 ? '用户已恢复' : '用户已冻结')
      await loadUsers()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '操作失败')
  }
}

const removeUser = async (item) => {
  const confirmed = await ElMessageBox.confirm(`确认删除用户「${field(item, 'username') || rowId(item)}」？`, '删除用户', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).then(() => true).catch(() => false)
  if (!confirmed) return
  try {
    const result = await deleteUser(rowId(item))
    if (result.code === 200) {
      ElMessage.success('用户已删除')
      await loadUsers()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '删除失败')
  }
}

onMounted(loadUsers)
</script>

<template>
  <main class="page-shell">
    <section class="page-head">
      <div>
        <h1>用户管理</h1>
        <p>超级管理员可查看用户状态、认证进度，并维护联系方式、账号状态和后台角色。</p>
      </div>
      <button class="primary-btn" type="button" :disabled="loading" @click="loadUsers">
        {{ loading ? '刷新中' : '刷新' }}
      </button>
    </section>

    <section class="summary-band">
      <div>
        <span>全部用户</span>
        <strong>{{ metrics.total }}</strong>
      </div>
      <div>
        <span>正常账号</span>
        <strong>{{ metrics.normal }}</strong>
      </div>
      <div>
        <span>后台管理员</span>
        <strong>{{ metrics.admins }}</strong>
      </div>
      <div>
        <span>律师用户</span>
        <strong>{{ metrics.lawyers }}</strong>
      </div>
      <div>
        <span>律师待审核</span>
        <strong>{{ metrics.pendingLawyerAuth }}</strong>
      </div>
    </section>

    <section class="content-card">
      <div class="card-title">
        <span>用户列表（{{ filteredUsers.length }}个）</span>
      </div>

      <div class="filters user-filters">
        <input v-model="keyword" class="search-input" placeholder="搜索 ID、用户名、电话、邮箱、地区" />
        <select v-model="typeFilter">
          <option value="">全部类型</option>
          <option v-for="item in USER_TYPE_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option v-for="item in STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <select v-model="roleFilter">
          <option value="">全部后台角色</option>
          <option v-for="item in ADMIN_ROLE_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <table v-else class="data-table user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>类型</th>
            <th>后台角色</th>
            <th>状态</th>
            <th>实名认证</th>
            <th>律师认证</th>
            <th>创建时间</th>
            <template v-if="personalInfoExpanded">
              <th class="personal-detail-head">
                <button class="collapse-column-btn" type="button" aria-label="收起个人信息" @click="personalInfoExpanded = false">&lt;</button>
                电话
              </th>
              <th>邮箱</th>
              <th>地区</th>
            </template>
            <th v-else class="personal-group-head compact">
              <button class="column-toggle" type="button" @click="personalInfoExpanded = true">
                <span>&gt;</span>
                个人信息
              </button>
            </th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filteredUsers.length === 0">
            <td :colspan="personalInfoExpanded ? 12 : 10" class="empty">暂无匹配用户</td>
          </tr>
          <template v-else>
            <tr v-for="item in filteredUsers" :key="rowId(item)">
              <td>{{ rowId(item) }}</td>
              <td class="strong-cell">{{ field(item, 'username') || '-' }}</td>
              <td>{{ typeText(rowType(item)) }}</td>
              <td>
                <span v-if="rowType(item) === 3" class="role-tag">{{ roleText(rowRole(item)) }}</span>
                <span v-else>-</span>
              </td>
              <td><span :class="['status-badge', statusClass(rowStatus(item))]">{{ statusText(rowStatus(item)) }}</span></td>
              <td>
                <span :class="['status-badge', rowVerified(item) ? 'badge-success' : 'badge-default']">
                  {{ rowVerified(item) ? '已实名' : '未实名' }}
                </span>
              </td>
              <td><span :class="['status-badge', authClass(rowAuthStatus(item))]">{{ lawyerAuthText(rowAuthStatus(item)) }}</span></td>
              <td>{{ formatDate(field(item, 'createdTime', 'created_time')) }}</td>
              <template v-if="personalInfoExpanded">
                <td>{{ field(item, 'phone') || '-' }}</td>
                <td>{{ field(item, 'email') || '-' }}</td>
                <td>{{ field(item, 'region') || '-' }}</td>
              </template>
              <td v-else class="collapsed-info"></td>
              <td>
                <div class="row-actions">
                  <button class="table-btn" type="button" @click="openEdit(item)">编辑</button>
                  <button
                    v-if="rowStatus(item) === 1"
                    class="cancel-btn"
                    type="button"
                    :disabled="isSelf(item)"
                    @click="setStatus(item, 0)"
                  >
                    冻结
                  </button>
                  <button
                    v-else-if="rowStatus(item) !== 4"
                    class="btn-success"
                    type="button"
                    :disabled="isSelf(item)"
                    @click="setStatus(item, 1)"
                  >
                    恢复
                  </button>
                  <button class="delete-btn" type="button" :disabled="isSelf(item)" @click="removeUser(item)">删除</button>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </section>

    <div v-if="editVisible" class="modal-mask" @click.self="editVisible = false">
      <div class="modal user-modal">
        <div class="modal-head">
          <div>
            <h2>编辑用户</h2>
            <p class="modal-sub">用户 #{{ editForm.userId }} · {{ typeText(editForm.userType) }}</p>
          </div>
          <button class="close-btn" type="button" @click="editVisible = false">×</button>
        </div>

        <div class="form-grid">
          <label>
            用户名
            <input v-model="editForm.username" disabled />
          </label>
          <label>
            电话
            <input v-model="editForm.phone" placeholder="未填写" />
          </label>
          <label>
            邮箱
            <input v-model="editForm.email" placeholder="未填写" />
          </label>
          <label>
            地区
            <input v-model="editForm.region" placeholder="未填写" />
          </label>
          <label>
            账号状态
            <select v-model.number="editForm.status">
              <option v-for="item in STATUS_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </label>
          <label v-if="Number(editForm.userType) === 3">
            后台角色
            <select v-model="editForm.adminRole">
              <option v-for="item in ADMIN_ROLE_OPTIONS" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </label>
        </div>

        <div class="modal-actions">
          <button class="cancel-btn" type="button" @click="editVisible = false">取消</button>
          <button class="save-btn" type="button" :disabled="saving" @click="saveEdit">
            {{ saving ? '保存中' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-head p {
  margin: 8px 0 0;
}

.user-filters {
  margin-bottom: 18px;
}

.search-input {
  width: min(360px, 100%);
  padding: 10px 12px;
}

.user-filters select {
  min-width: 150px;
  padding: 10px 12px;
}

.user-table {
  min-width: 1180px;
}

.user-table thead th {
  vertical-align: middle;
}

.personal-group-head {
  text-align: left;
}

.personal-group-head.compact {
  width: 160px;
}

.personal-detail-head {
  min-width: 128px;
  white-space: nowrap;
}

.column-toggle {
  min-height: 30px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid var(--admin-border);
  border-radius: 6px;
  background: #ffffff;
  color: var(--admin-primary-text);
  padding: 4px 10px;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
}

.column-toggle span {
  width: 12px;
  display: inline-flex;
  justify-content: center;
  color: var(--admin-text-muted);
}

.collapse-column-btn {
  width: 26px;
  height: 26px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  border: 1px solid var(--admin-border);
  border-radius: 6px;
  background: #ffffff;
  color: var(--admin-text-muted);
  font-weight: 900;
  cursor: pointer;
}

.strong-cell {
  color: var(--admin-text);
  font-weight: 800;
}

.collapsed-info {
  min-width: 160px;
  color: var(--admin-text-muted);
  font-size: 13px;
  font-weight: 700;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-width: 210px;
}

.empty,
.loading {
  padding: 34px;
  text-align: center;
  color: var(--admin-text-muted);
}

.user-modal {
  width: min(720px, 92vw);
}

.modal-sub {
  margin: 6px 0 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.form-grid label {
  display: grid;
  gap: 7px;
  color: var(--admin-text-secondary);
  font-weight: 800;
}

.form-grid input,
.form-grid select {
  min-height: 40px;
  padding: 9px 11px;
}

.form-grid input:disabled {
  background: var(--admin-panel);
  color: var(--admin-text-muted);
  cursor: not-allowed;
}

@media (max-width: 760px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
