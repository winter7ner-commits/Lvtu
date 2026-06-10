<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getNotifications,
  markAllNotificationsRead,
  markNotificationRead
} from '@/api/notification'

const router = useRouter()
const loading = ref(false)
const notifications = ref([])

const currentUserId = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId || Number(localStorage.getItem('userId')) || null
})

const unreadCount = computed(() => notifications.value.filter((item) => !item.isRead).length)

const unwrap = (response) => response?.data?.data ?? response?.data ?? []

const loadNotifications = async () => {
  if (!currentUserId.value) return
  loading.value = true
  try {
    const response = await getNotifications(currentUserId.value)
    notifications.value = unwrap(response)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '消息加载失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

const openNotification = async (item) => {
  if (!currentUserId.value) return
  try {
    if (!item.isRead) {
      await markNotificationRead(item.id, currentUserId.value)
      item.isRead = true
      window.dispatchEvent(new Event('notifications-updated'))
    }
    if (item.relatedPath) {
      router.push(item.relatedPath)
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '消息处理失败')
  }
}

const markAllRead = async () => {
  if (!currentUserId.value || unreadCount.value === 0) return
  try {
    await markAllNotificationsRead(currentUserId.value)
    notifications.value = notifications.value.map((item) => ({ ...item, isRead: true }))
    window.dispatchEvent(new Event('notifications-updated'))
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '操作失败')
  }
}

onMounted(loadNotifications)
</script>

<template>
  <div class="notification-page">
    <section class="page-head">
      <div>
        <p class="eyebrow">消息中心</p>
        <h1>消息通知</h1>
        <p class="subtext">订单接单、服务结果、修改申请等关键进度都会在这里同步。</p>
      </div>
      <button class="mark-all-btn" :disabled="unreadCount === 0" @click="markAllRead">
        全部已读
      </button>
    </section>

    <section class="notification-shell" v-loading="loading">
      <div v-if="notifications.length" class="notification-list">
        <article
          v-for="item in notifications"
          :key="item.id"
          :class="['notification-card', { unread: !item.isRead }]"
          @click="openNotification(item)"
        >
          <div class="unread-dot"></div>
          <div class="notification-content">
            <div class="notification-title-row">
              <h2>{{ item.title }}</h2>
              <span>{{ formatTime(item.createdAt) }}</span>
            </div>
            <p>{{ item.content }}</p>
            <div v-if="item.relatedOrderId" class="order-link">订单 #{{ item.relatedOrderId }}</div>
          </div>
        </article>
      </div>
      <el-empty v-else description="暂无消息" />
    </section>
  </div>
</template>

<style scoped>
.notification-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
}

.page-head,
.notification-shell {
  width: 100%;
  max-width: 960px;
  margin-left: auto;
  margin-right: auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-end;
  margin-bottom: 22px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-weight: 700;
}

h1,
h2,
p {
  margin: 0;
}

h1 {
  font-size: 30px;
}

.subtext {
  margin-top: 10px;
  color: #667085;
}

.mark-all-btn {
  min-width: 96px;
  height: 40px;
  border: 0;
  border-radius: 8px;
  background: #2563eb;
  color: #ffffff;
  font-weight: 700;
  cursor: pointer;
}

.mark-all-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

.notification-shell {
  min-height: 420px;
  padding: 18px;
  background: #ffffff;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-card {
  position: relative;
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr);
  gap: 12px;
  padding: 16px;
  border: 1px solid #edf1f7;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}

.notification-card:hover {
  border-color: #bfdbfe;
  background: #f8fbff;
}

.notification-card.unread {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.unread-dot {
  width: 8px;
  height: 8px;
  margin-top: 8px;
  border-radius: 50%;
  background: transparent;
}

.notification-card.unread .unread-dot {
  background: #ef4444;
}

.notification-content {
  min-width: 0;
}

.notification-title-row {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 8px;
}

.notification-title-row h2 {
  color: #172033;
  font-size: 17px;
}

.notification-title-row span {
  flex-shrink: 0;
  color: #98a2b3;
  font-size: 13px;
}

.notification-content p {
  color: #475467;
  line-height: 1.7;
}

.order-link {
  margin-top: 10px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
}

@media (max-width: 640px) {
  .page-head {
    display: block;
  }

  .mark-all-btn {
    margin-top: 16px;
  }

  .notification-title-row {
    display: block;
  }

  .notification-title-row span {
    display: block;
    margin-top: 6px;
  }
}
</style>
