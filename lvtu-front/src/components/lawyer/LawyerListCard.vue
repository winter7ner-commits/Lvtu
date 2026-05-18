<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  lawyer: {
    type: Object,
    required: true
  }
})

const rating = computed(() => Number(props.lawyer.rating || 0))
const practiceYears = computed(() => Number(props.lawyer.practiceYears || 0))

const specialtyNames = computed(() => {
  return (props.lawyer.specialties || [])
    .map((item) => item?.name || item?.specialtyName)
    .filter(Boolean)
})

const avatarUrl = computed(() => {
  return props.lawyer.avatarUrl || props.lawyer.avatar || props.lawyer.photoUrl || ''
})

const lawyerId = computed(() => props.lawyer.lawyerId || props.lawyer.id)

const initials = computed(() => {
  const name = props.lawyer.name || '律师'
  return name.slice(-2)
})

const goDetail = () => {
  if (!lawyerId.value) return

  router.push({
    name: 'LawyerDetail',
    params: { id: lawyerId.value }
  })
}
</script>

<template>
  <article class="lawyer-card" @click="goDetail">
    <div class="avatar-wrap">
      <img v-if="avatarUrl" :src="avatarUrl" class="avatar" :alt="lawyer.name" />
      <div v-else class="avatar-placeholder">{{ initials }}</div>
    </div>

    <div class="info">
      <div class="top-line">
        <div class="name-group">
          <h3>{{ lawyer.name || '未命名律师' }}</h3>
          <span class="verified-badge">认证律师</span>
        </div>
        <div class="rating-block">
          <el-rate
            :model-value="rating"
            disabled
            show-score
            text-color="#1d4ed8"
          />
        </div>
      </div>

      <div class="meta-grid">
        <div>
          <span>所在律所</span>
          <strong>{{ lawyer.lawFirm || '暂未填写' }}</strong>
        </div>
        <div>
          <span>执业年限</span>
          <strong>{{ practiceYears || '-' }} 年</strong>
        </div>
        <div>
          <span>服务评分</span>
          <strong>{{ rating.toFixed(1) }} 分</strong>
        </div>
      </div>

      <p class="description">
        {{ lawyer.description || '该律师暂未填写个人简介，可进入详情页查看执业信息。' }}
      </p>

      <div class="card-footer">
        <div v-if="specialtyNames.length" class="tags">
          <el-tag
            v-for="specialty in specialtyNames.slice(0, 5)"
            :key="specialty"
            size="small"
          >
            {{ specialty }}
          </el-tag>
        </div>
        <span v-else class="no-tags">暂无专长标签</span>

        <button type="button" class="detail-btn" @click.stop="goDetail">查看详情</button>
      </div>
    </div>
  </article>
</template>

<style scoped>
.lawyer-card {
  display: grid;
  grid-template-columns: 108px minmax(0, 1fr);
  gap: 18px;
  padding: 18px;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.lawyer-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 12px 26px rgba(37, 99, 235, 0.1);
  transform: translateY(-2px);
}

.avatar-wrap {
  width: 108px;
  height: 108px;
  border-radius: 12px;
  overflow: hidden;
  background: #eff6ff;
}

.avatar,
.avatar-placeholder {
  width: 100%;
  height: 100%;
}

.avatar {
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1d4ed8;
  font-size: 28px;
  font-weight: 800;
  background:
    linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(14, 165, 233, 0.1)),
    #eff6ff;
}

.info {
  min-width: 0;
}

.top-line {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: flex-start;
}

.name-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

h3 {
  margin: 0;
  color: #172033;
  font-size: 20px;
  line-height: 1.3;
}

.verified-badge {
  padding: 4px 9px;
  border-radius: 999px;
  background: #ecfdf3;
  color: #067647;
  font-size: 12px;
  font-weight: 800;
}

.rating-block {
  flex: 0 0 auto;
}

.meta-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.7fr 0.7fr;
  gap: 10px;
  margin-top: 14px;
}

.meta-grid div {
  padding: 10px 12px;
  border-radius: 8px;
  background: #f8fafc;
}

.meta-grid span {
  display: block;
  color: #667085;
  font-size: 12px;
}

.meta-grid strong {
  display: block;
  margin-top: 4px;
  color: #344054;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.description {
  display: -webkit-box;
  margin: 13px 0 0;
  overflow: hidden;
  color: #475467;
  font-size: 14px;
  line-height: 1.7;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-end;
  margin-top: 14px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tags :deep(.el-tag) {
  border-color: #bfdbfe;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 700;
}

.no-tags {
  color: #98a2b3;
  font-size: 13px;
}

.detail-btn {
  flex: 0 0 auto;
  padding: 8px 14px;
  border: 1px solid #2563eb;
  border-radius: 8px;
  background: #2563eb;
  color: #ffffff;
  cursor: pointer;
  font-weight: 700;
  transition: all 0.2s ease;
}

.detail-btn:hover {
  background: #1d4ed8;
  border-color: #1d4ed8;
}

@media (max-width: 720px) {
  .lawyer-card {
    grid-template-columns: 1fr;
  }

  .avatar-wrap {
    width: 88px;
    height: 88px;
  }

  .top-line,
  .card-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }

  .detail-btn {
    width: 100%;
  }
}
</style>
