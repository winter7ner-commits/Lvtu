<template>
  <div class="evaluation-list">
    <!-- 评分总览 -->
    <div v-if="!loading && evaluations.length > 0" class="rating-summary">
      <div class="summary-main">
        <div class="overall-score">
          <span class="score-number">{{ averageTotal }}</span>
          <span class="score-label">综合评分</span>
        </div>
        <div class="score-details">
          <div class="score-row">
            <span class="score-name">专业度</span>
            <el-rate :model-value="averageProfessional" disabled size="small" />
            <span class="score-value">{{ averageProfessional }}</span>
          </div>
          <div class="score-row">
            <span class="score-name">响应速度</span>
            <el-rate :model-value="averageResponsiveness" disabled size="small" />
            <span class="score-value">{{ averageResponsiveness }}</span>
          </div>
          <div class="score-row">
            <span class="score-name">服务态度</span>
            <el-rate :model-value="averageAttitude" disabled size="small" />
            <span class="score-value">{{ averageAttitude }}</span>
          </div>
        </div>
      </div>
      <div class="summary-footer">
        共 <strong>{{ totalCount }}</strong> 条评价
      </div>
    </div>

    <!-- 评价列表 -->
    <div v-if="loading" class="loading-state">
      <span class="loading-spinner"></span>
      加载评价中...
    </div>

    <div v-else-if="evaluations.length === 0" class="empty-state">
      <div class="empty-icon">📝</div>
      <p class="empty-title">暂无评价</p>
      <p class="empty-desc">该律师还没有收到用户评价</p>
    </div>

    <div v-else class="list">
      <div v-for="item in evaluations" :key="item.evaluationId" class="evaluation-card">
        <!-- 评价头部 -->
        <div class="card-header">
          <div class="user-info">
            <div class="user-avatar" :class="{ anonymous: item.isAnonymous }">
              {{ item.isAnonymous ? '匿' : getUserInitials(item.userName) }}
            </div>
            <div class="user-meta">
              <span class="user-name">{{ item.isAnonymous ? '匿名用户' : item.userName }}</span>
              <span class="eval-time">{{ formatDate(item.createdTime) }}</span>
            </div>
          </div>
          <div class="card-actions">
            <el-tag v-if="item.isFeatured" size="small" type="warning" effect="light" round>精选评价</el-tag>
            <el-tag v-else-if="item.totalScore >= 4.5" size="small" color="#ecfdf5" style="color:#059669;border-color:#a7f3d0;" round>好评</el-tag>
          </div>
        </div>

        <!-- 综合评分 -->
        <div class="card-total-score">
          <el-rate :model-value="item.totalScore" disabled />
          <span class="total-score-text">{{ item.totalScore }} 分</span>
        </div>

        <!-- 三维评分 -->
        <div class="card-scores">
          <div class="chip-score">
            <span class="chip-label">专业度</span>
            <el-rate :model-value="item.professionalScore" disabled size="small" />
          </div>
          <div class="chip-score">
            <span class="chip-label">响应速度</span>
            <el-rate :model-value="item.responsivenessScore" disabled size="small" />
          </div>
          <div class="chip-score">
            <span class="chip-label">服务态度</span>
            <el-rate :model-value="item.attitudeScore" disabled size="small" />
          </div>
        </div>

        <!-- 评价内容 -->
        <div v-if="item.content" class="card-content">
          <p>{{ item.content }}</p>
        </div>

        <!-- 评价底部 -->
        <div class="card-footer">
          <span class="order-ref">订单 #{{ item.orderId }}</span>
          <button
            v-if="canReport && item.status === '正常'"
            class="report-btn"
            @click="openReportDialog(item.evaluationId)"
          >
            举报
          </button>
        </div>
      </div>
    </div>

    <!-- 举报弹窗 -->
    <ReportDialog
      v-if="showReportDialog"
      :evaluation-id="selectedEvaluationId"
      @close="showReportDialog = false"
      @submitted="handleReportSubmitted"
    />
  </div>
</template>

<script>
import ReportDialog from './ReportDialog.vue';
import { getEvaluationsByLawyer } from '../../services/evaluationService';

export default {
  components: {
    ReportDialog
  },
  props: {
    lawyerId: {
      type: Number,
      required: true
    },
    canReport: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      evaluations: [],
      loading: true,
      showReportDialog: false,
      selectedEvaluationId: null
    };
  },
  computed: {
    totalCount() {
      return this.evaluations.length;
    },
    averageTotal() {
      if (!this.evaluations.length) return '0.0';
      const sum = this.evaluations.reduce((acc, e) => acc + (e.totalScore || 0), 0);
      return (sum / this.evaluations.length).toFixed(1);
    },
    averageProfessional() {
      if (!this.evaluations.length) return 0;
      const sum = this.evaluations.reduce((acc, e) => acc + (e.professionalScore || 0), 0);
      return Math.round((sum / this.evaluations.length) * 10) / 10;
    },
    averageResponsiveness() {
      if (!this.evaluations.length) return 0;
      const sum = this.evaluations.reduce((acc, e) => acc + (e.responsivenessScore || 0), 0);
      return Math.round((sum / this.evaluations.length) * 10) / 10;
    },
    averageAttitude() {
      if (!this.evaluations.length) return 0;
      const sum = this.evaluations.reduce((acc, e) => acc + (e.attitudeScore || 0), 0);
      return Math.round((sum / this.evaluations.length) * 10) / 10;
    }
  },
  async created() {
    await this.loadEvaluations();
  },
  methods: {
    async loadEvaluations() {
      this.loading = true;
      try {
        const data = await getEvaluationsByLawyer(this.lawyerId, 1, 10);
        this.evaluations = data.list || [];
      } catch (error) {
        console.error('加载评价失败:', error);
      } finally {
        this.loading = false;
      }
    },

    getUserInitials(name) {
      if (!name) return '用';
      return name.charAt(0);
    },

    openReportDialog(evaluationId) {
      this.selectedEvaluationId = evaluationId;
      this.showReportDialog = true;
    },

    handleReportSubmitted() {
      this.loadEvaluations();
    },

    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      const now = new Date();
      const diff = now - date;
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));

      if (days === 0) return '今天';
      if (days === 1) return '昨天';
      if (days < 7) return `${days} 天前`;
      if (days < 30) return `${Math.floor(days / 7)} 周前`;

      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  }
}
</script>

<style scoped>
/* ========== 全局 ========== */
.evaluation-list {
  font-size: 14px;
  color: #172033;
}

/* ========== 评分总览 ========== */
.rating-summary {
  background: linear-gradient(135deg, #f0f4ff 0%, #eef2ff 100%);
  border: 1px solid #d6e4ff;
  border-radius: 10px;
  padding: 20px 24px;
  margin-bottom: 20px;
}

.summary-main {
  display: flex;
  align-items: center;
  gap: 32px;
}

.overall-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;
}

.score-number {
  font-size: 40px;
  font-weight: 900;
  color: #1d4ed8;
  line-height: 1;
}

.score-label {
  font-size: 12px;
  color: #667085;
  margin-top: 4px;
  font-weight: 600;
}

.score-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.score-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-name {
  width: 56px;
  font-size: 13px;
  color: #475467;
  font-weight: 600;
  flex-shrink: 0;
}

.score-row :deep(.el-rate) {
  flex-shrink: 0;
}

.score-value {
  font-size: 13px;
  color: #344054;
  font-weight: 700;
  min-width: 24px;
}

.summary-footer {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid #d6e4ff;
  font-size: 13px;
  color: #667085;
}

.summary-footer strong {
  color: #1d4ed8;
  font-weight: 800;
}

/* ========== 加载状态 ========== */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 40px 0;
  color: #98a2b3;
  font-size: 14px;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid #e5eaf3;
  border-top-color: #1d4ed8;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: 40px 0;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
  opacity: 0.6;
}

.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: #344054;
  margin: 0 0 6px;
}

.empty-desc {
  font-size: 13px;
  color: #98a2b3;
  margin: 0;
}

/* ========== 评价卡片 ========== */
.list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.evaluation-card {
  background: #ffffff;
  border: 1px solid #e5eaf3;
  border-radius: 10px;
  padding: 20px 22px;
  transition: box-shadow 0.2s, border-color 0.2s;
}

.evaluation-card:hover {
  border-color: #bfdbfe;
  box-shadow: 0 4px 16px rgba(29, 78, 216, 0.08);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
  flex-shrink: 0;
}

.user-avatar.anonymous {
  background: linear-gradient(135deg, #9ca3af, #6b7280);
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 700;
  color: #172033;
}

.eval-time {
  font-size: 12px;
  color: #98a2b3;
}

.card-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

/* 综合评分 */
.card-total-score {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.card-total-score :deep(.el-rate) {
  line-height: 1;
}

.total-score-text {
  font-size: 15px;
  font-weight: 800;
  color: #f59e0b;
}

/* 三维评分 */
.card-scores {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 24px;
  margin-bottom: 12px;
  padding: 10px 14px;
  background: #f9fafc;
  border-radius: 8px;
}

.chip-score {
  display: flex;
  align-items: center;
  gap: 6px;
}

.chip-label {
  font-size: 12px;
  color: #667085;
  font-weight: 600;
  flex-shrink: 0;
}

.chip-score :deep(.el-rate) {
  line-height: 1;
}

.chip-score :deep(.el-rate__icon) {
  font-size: 14px !important;
  margin-right: 2px !important;
}

/* 评价内容 */
.card-content {
  margin-bottom: 12px;
  padding: 12px 16px;
  background: #f8faff;
  border-left: 3px solid #1d4ed8;
  border-radius: 0 8px 8px 0;
}

.card-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #344054;
  white-space: pre-line;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f2f5;
}

.order-ref {
  font-size: 12px;
  color: #98a2b3;
}

.report-btn {
  font-size: 12px;
  color: #98a2b3;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 4px;
  transition: all 0.2s;
}

.report-btn:hover {
  color: #ef4444;
  background: #fef2f2;
}

/* ========== 响应式 ========== */
@media (max-width: 640px) {
  .summary-main {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .overall-score {
    flex-direction: row;
    gap: 8px;
    align-items: baseline;
  }

  .score-number {
    font-size: 32px;
  }

  .score-label {
    margin-top: 0;
  }

  .evaluation-card {
    padding: 16px;
  }

  .card-scores {
    gap: 8px 16px;
  }

  .card-content {
    padding: 10px 14px;
  }
}
</style>
