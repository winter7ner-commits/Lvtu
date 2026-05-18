<template>
  <section class="evaluation-dashboard">
    <div class="page-head">
      <div class="page-title">
        <p class="eyebrow">服务评价</p>
        <h1>评价本次法律服务</h1>
        <p>你的反馈会帮助律师改进服务，也会为之后的用户提供参考。</p>
      </div>
      <div class="head-actions">
        <button class="back-order-btn" @click="goBack">
          <span class="back-icon">‹</span>
          返回订单
        </button>
      </div>
    </div>

    <div class="dashboard-grid">
      <section class="panel form-panel">
        <header class="panel-header">
          <span>创建评价</span>
          <strong>订单 #{{ currentOrderId }}</strong>
        </header>
        <div class="panel-body">
          <EvaluationForm
            :order-id="currentOrderId"
            :lawyer-id="currentLawyerId"
            @submitted="handleSubmitted"
          />
        </div>
      </section>

      <section class="panel list-panel">
        <header class="panel-header">
          <span>律师历史评价</span>
          <strong>律师 #{{ currentLawyerId }}</strong>
        </header>
        <div class="panel-body">
          <EvaluationList :lawyer-id="currentLawyerId" :can-report="true" />
        </div>
      </section>
    </div>
  </section>
</template>

<script>
import EvaluationForm from '../../components/evaluation/EvaluationForm.vue'
import EvaluationList from '../../components/evaluation/EvaluationList.vue'

export default {
  components: {
    EvaluationForm,
    EvaluationList
  },
  computed: {
    currentOrderId() {
      return Number(this.$route.query.orderId) || 1001
    },
    currentLawyerId() {
      return Number(this.$route.query.lawyerId) || 2001
    }
  },
  methods: {
    goBack() {
      if (window.history.length > 1) {
        this.$router.back()
        return
      }

      this.$router.push({
        name: 'ClientOrderList',
        query: { status: 'pending_review' }
      })
    },
    handleSubmitted() {
      this.$router.push({
        name: 'ClientOrderDetail',
        params: { orderId: this.currentOrderId }
      })
    }
  }
}
</script>

<style scoped>
.evaluation-dashboard {
  width: min(1180px, calc(100% - 32px));
  margin: 0 auto;
  padding: 32px 0 56px;
  text-align: left;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 24px;
}

.page-title {
  min-width: 0;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-weight: 800;
}

.page-head h1 {
  margin: 0;
  font-size: 32px;
  line-height: 1.2;
  color: #172033;
}

.page-head p:last-child {
  margin: 10px 0 0;
  color: #667085;
}

.head-actions {
  display: flex;
  gap: 10px;
}

.back-order-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  border: 1px solid #d6e4ff;
  border-radius: 999px;
  background: #ffffff;
  color: #1d4ed8;
  padding: 9px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-order-btn:hover {
  background: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
}

.back-icon {
  font-size: 20px;
  line-height: 1;
  margin-right: 2px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(380px, 0.92fr) minmax(0, 1.08fr);
  gap: 20px;
  align-items: start;
}

.panel {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.07);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  padding: 14px 18px;
  border-bottom: 1px solid #e5e7eb;
  background: #f6f8fc;
  font-weight: 600;
  color: #1f2937;
}

.panel-header strong {
  padding: 4px 10px;
  border-radius: 999px;
  background: #e6f4ff;
  color: #0958d9;
  font-size: 12px;
}

.panel-body {
  padding: 18px;
}

:deep(.btn) {
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #ffffff;
  color: #1f2937;
  cursor: pointer;
  padding: 6px 12px;
}

:deep(.btn-primary),
:deep(.submit-btn) {
  border-color: #2563eb;
  background: #2563eb;
  color: #ffffff;
}

:deep(.btn-secondary) {
  background: #f3f4f6;
}

:deep(.btn-outline-warning) {
  border-color: #f59e0b;
  color: #92400e;
}

:deep(.btn-outline-danger) {
  border-color: #ef4444;
  color: #991b1b;
}

:deep(.btn-outline-success) {
  border-color: #10b981;
  color: #065f46;
}

:deep(.btn-outline-info) {
  border-color: #0ea5e9;
  color: #075985;
}

:deep(.btn-outline-secondary) {
  color: #374151;
}

:deep(.evaluation-form) {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

:deep(.score-section) {
  display: grid;
  gap: 14px;
}

:deep(.score-item) {
  padding: 14px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #fafcff;
}

:deep(.score-item label) {
  display: block;
  margin-bottom: 8px;
  color: #344054;
  font-weight: 700;
}

:deep(.content-section textarea) {
  width: 100%;
  min-height: 132px;
  padding: 12px 14px;
  border: 1px solid #dbe4f0;
  border-radius: 8px;
  resize: vertical;
  font: inherit;
}

:deep(.char-count) {
  margin-top: 6px;
  text-align: right;
  color: #98a2b3;
  font-size: 12px;
}

:deep(.anonymous-section) {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #475467;
}

:deep(.submit-btn) {
  width: 100%;
  padding: 12px 16px;
  border-radius: 8px;
  font-weight: 800;
}

:deep(.evaluation-list .list) {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

:deep(.evaluation-item) {
  padding: 16px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
}

:deep(.evaluation-item .header) {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

:deep(.user-name) {
  font-weight: 800;
  color: #172033;
}

:deep(.time) {
  margin-top: 4px;
  color: #98a2b3;
  font-size: 12px;
}

:deep(.featured-tag) {
  height: 24px;
  padding: 3px 9px;
  border-radius: 999px;
  background: #fff7e6;
  color: #ad6800;
  font-size: 12px;
  font-weight: 800;
}

:deep(.scores) {
  display: grid;
  gap: 8px;
}

:deep(.score) {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}

:deep(.total-score) {
  margin-top: 6px;
  color: #0958d9;
  font-weight: 800;
}

:deep(.content) {
  margin-top: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f8fafc;
  color: #344054;
  line-height: 1.7;
}

@media (max-width: 860px) {
  .page-head {
    flex-direction: column;
    align-items: stretch;
  }

  .back-order-btn {
    width: 100%;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
