<template>
  <div class="dialog-overlay" @click="closeDialog">
    <div class="dialog-content" @click.stop>
      <h3>操作日志</h3>
      
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="logs.length === 0" class="empty">暂无操作记录</div>
      
      <div v-else class="logs-list">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          <div class="log-header">
            <strong>{{ log.actionType }}</strong>
            <span class="log-time">{{ formatDate(log.createdTime) }}</span>
          </div>
          <div class="log-admin">操作人: {{ log.adminName }}</div>
          <div v-if="log.reason" class="log-reason">原因: {{ log.reason }}</div>
        </div>
      </div>
      
      <div class="actions">
        <button @click="closeDialog" class="btn btn-secondary">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getLogsByEvaluationId } from '../../services/adminService';

export default {
  props: {
    evaluationId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      logs: [],
      loading: true
    };
  },
  async created() {
    await this.loadLogs();
  },
  methods: {
    async loadLogs() {
      this.loading = true;
      try {
        this.logs = await getLogsByEvaluationId(this.evaluationId);
      } catch (error) {
        console.error('加载日志失败:', error);
        alert('加载操作日志失败');
      } finally {
        this.loading = false;
      }
    },
    
    closeDialog() {
      this.$emit('close');
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours()}:${date.getMinutes()}`;
    }
  }
}
</script>

<style scoped>
.logs-list {
  max-height: 400px;
  overflow-y: auto;
}

.log-item {
  padding: 12px;
  margin-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.log-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.log-time {
  color: #888;
  font-size: 12px;
}

.log-admin {
  font-size: 13px;
  color: #555;
}

.log-reason {
  margin-top: 5px;
  padding: 5px;
  background: #f8f9fa;
  border-radius: 4px;
  font-size: 13px;
}
</style>
