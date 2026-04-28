<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  lawyer: {
    type: Object,
    required: true
  }
})

const goDetail = () => {
  router.push(`/lawyer/${props.lawyer.lawyerId}`)
}
</script>

<template>
  <div class="card" @click="goDetail">
    <!-- 左侧图片 -->
    <img src="https://via.placeholder.com/120x120" class="avatar" />

    <!-- 右侧信息 -->
    <div class="info">
      <div class="top">
        <h3>{{ lawyer.name }}</h3>
        <el-rate
          v-model="lawyer.rating"
          disabled
          show-score
          text-color="#1890ff"
        />
      </div>

      <p class="firm">
        <span class="label">律所：</span>
        {{ lawyer.lawFirm }}
      </p>
      <p class="exp">
        <span class="label">执业年限：</span>
        {{ lawyer.practiceYears }} 年
      </p>

      <p class="description">
        {{ lawyer.description }}
      </p>

      <!-- 专业领域标签 -->
      <div v-if="lawyer.specialties && lawyer.specialties.length > 0" class="tags">
        <el-tag
          v-for="specialty in lawyer.specialties"
          :key="specialty.id || specialty.specialtyId"
          type="info"
          size="small"
        >
          {{ specialty.name }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 卡片 */
.card {
  display: flex;
  padding: 20px;
  border-radius: 12px;
  background: white;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.08);
  border: 1px solid #e8eef7;
  align-items: flex-start;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.12);
  border-color: #1890ff;
}

/* 图片 */
.avatar {
  width: 120px;
  height: 120px;
  border-radius: 10px;
  margin-right: 20px;
  background-color: #f0f4f8;
  object-fit: cover;
  flex-shrink: 0;
}

/* 信息 */
.info {
  flex: 1;
  min-width: 0;
}

.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 15px;
}

.top h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  margin: 0;
  white-space: nowrap;
}

.info p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.label {
  font-weight: 600;
  color: #333;
}

.firm {
  color: #595959;
}

.exp {
  color: #8c8c8c;
}

.description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 12px 0 0 0;
}

/* 标签 */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.tags :deep(.el-tag) {
  background-color: #e6f7ff;
  border-color: #91d5ff;
  color: #0050b3;
  font-size: 12px;
  padding: 4px 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card {
    flex-direction: column;
    text-align: center;
  }

  .avatar {
    margin-right: 0;
    margin-bottom: 15px;
    width: 100px;
    height: 100px;
  }

  .top {
    flex-direction: column;
    align-items: center;
  }
}
</style>
