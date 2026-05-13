<template>
  <div class="admin-panel" v-if="isAdmin">
    <h2>评价管理</h2>
    
    <div class="filters">
      <div class="filter-item">
        <label>状态</label>
        <select v-model="filters.status">
          <option value="">全部</option>
          <option value="NORMAL">正常</option>
          <option value="HIDDEN">隐藏</option>
          <option value="DELETED">已删除</option>
          <option value="PENDING">待审核</option>
        </select>
      </div>
      
      <div class="filter-item">
        <label>是否精选</label>
        <select v-model="filters.isFeatured">
          <option value="">全部</option>
          <option value="true">是</option>
          <option value="false">否</option>
        </select>
      </div>
      
      <div class="filter-item">
        <label>举报次数</label>
        <select v-model="filters.reportCount">
          <option value="">全部</option>
          <option value="1">≥1次</option>
          <option value="3">≥3次</option>
          <option value="5">≥5次</option>
        </select>
      </div>
    </div>
    
    <div class="evaluation-list">
      <div v-for="item in filteredEvaluations" :key="item.evaluationId" class="evaluation-item">
        <div class="header">
          <div class="info">
            <h3>评价ID: {{ item.evaluationId }}</h3>
            <div class="meta">
              <span>律师ID: {{ item.lawyerId }}</span>
              <span>用户: {{ item.userName }}</span>
              <span>状态: {{ item.status }}</span>
              <span v-if="item.reportCount > 0" class="report-count">举报: {{ item.reportCount }}次</span>
            </div>
          </div>
          
          <div class="actions">
            <button @click="showLogs(item.evaluationId)" class="btn btn-sm btn-outline-secondary">操作日志</button>
            
            <div class="action-buttons">
              <button 
                v-if="item.status !== 'HIDDEN'" 
                @click="handleAction(item.evaluationId, 'HIDE')" 
                class="btn btn-sm btn-outline-warning"
              >
                隐藏
              </button>
              
              <button 
                v-if="item.status !== 'DELETED'" 
                @click="handleAction(item.evaluationId, 'DELETE')" 
                class="btn btn-sm btn-outline-danger"
              >
                删除
              </button>
              
              <button 
                v-if="item.status === 'HIDDEN'" 
                @click="handleAction(item.evaluationId, 'RESTORE')" 
                class="btn btn-sm btn-outline-success"
              >
                恢复
              </button>
              
              <button 
                v-if="!item.isFeatured" 
                @click="handleAction(item.evaluationId, 'FEATURE')" 
                class="btn btn-sm btn-outline-info"
              >
                设为精选
              </button>
              
              <button 
                v-if="item.isFeatured" 
                @click="handleAction(item.evaluationId, 'UNFEATURE')" 
                class="btn btn-sm btn-outline-secondary"
              >
                取消精选
              </button>
            </div>
          </div>
        </div>
        
        <div class="content">
          <div class="scores">
            <div>专业度: {{ item.professionalScore }}分</div>
            <div>响应速度: {{ item.responsivenessScore }}分</div>
            <div>服务态度: {{ item.attitudeScore }}分</div>
            <div class="total">综合评分: {{ item.totalScore }}</div>
          </div>
          
          <div v-if="item.content" class="text-content">
            {{ item.content }}
          </div>
          
          <div v-if="item.reportCount > 0" class="reports">
            <h4>举报记录</h4>
            <div v-for="(report, index) in getReports(item.evaluationId)" :key="index" class="report-item">
              <div class="reason">{{ report.reason }}: {{ report.description }}</div>
              <div class="status">{{ report.status }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 操作日志弹窗 -->
    <LogDialog 
      v-if="showLogDialog" 
      :evaluation-id="selectedEvaluationId"
      @close="showLogDialog = false"
    />
    
    <!-- 操作确认弹窗 -->
    <ActionDialog 
      v-if="showActionDialog" 
      :evaluation-id="selectedEvaluationId"
      :action-type="selectedActionType"
      @close="showActionDialog = false"
      @confirmed="confirmAction"
    />
  </div>
</template>

<script>
import LogDialog from './LogDialog.vue';
import ActionDialog from './ActionDialog.vue';
import { getLogsByEvaluationId, handleEvaluation } from '../services/adminService';
import { getReportsByEvaluationId } from '../services/reportService';

export default {
  components: {
    LogDialog,
    ActionDialog
  },
  data() {
    return {
      isAdmin: true,
      evaluations: [
        {
          evaluationId: 1,
          orderId: 1001,
          userId: 1001,
          userName: '测试用户',
          lawyerId: 2001,
          professionalScore: 5,
          responsivenessScore: 4,
          attitudeScore: 5,
          totalScore: 4.7,
          content: '律师非常专业，解答了我的所有疑问',
          isAnonymous: false,
          status: '正常',
          reportCount: 1,
          isFeatured: false,
          createdTime: new Date(),
          updatedTime: new Date()
        },
        {
          evaluationId: 2,
          orderId: 1002,
          userId: 1002,
          userName: '张三',
          lawyerId: 2001,
          professionalScore: 4,
          responsivenessScore: 5,
          attitudeScore: 4,
          totalScore: 4.3,
          content: '响应很及时，态度很好',
          isAnonymous: true,
          status: '正常',
          reportCount: 0,
          isFeatured: true,
          createdTime: new Date(),
          updatedTime: new Date()
        }
      ],
      reports: [
        {
          reportId: 1,
          evaluationId: 1,
          reporterId: 1002,
          reason: '广告',
          description: '包含联系方式',
          status: '待处理',
          createdTime: new Date()
        }
      ],
      filters: {
        status: '',
        isFeatured: '',
        reportCount: ''
      },
      showLogDialog: false,
      showActionDialog: false,
      selectedEvaluationId: null,
      selectedActionType: null
    };
  },
  computed: {
    filteredEvaluations() {
      return this.evaluations.filter(item => {
        // 状态过滤
        if (this.filters.status && item.status !== this.filters.status) {
          return false;
        }
        
        // 精选过滤
        if (this.filters.isFeatured !== '' && 
            String(item.isFeatured) !== this.filters.isFeatured) {
          return false;
        }
        
        // 举报次数过滤
        if (this.filters.reportCount && 
            item.reportCount < Number(this.filters.reportCount)) {
          return false;
        }
        
        return true;
      });
    }
  },
  methods: {
    getReports(evaluationId) {
      return this.reports.filter(r => r.evaluationId === evaluationId);
    },
    
    showLogs(evaluationId) {
      this.selectedEvaluationId = evaluationId;
      this.showLogDialog = true;
    },
    
    handleAction(evaluationId, actionType) {
      this.selectedEvaluationId = evaluationId;
      this.selectedActionType = actionType;
      this.showActionDialog = true;
    },
    
    async confirmAction(reason) {
      try {
        await handleEvaluation({
          evaluationId: this.selectedEvaluationId,
          actionType: this.selectedActionType,
          reason: reason
        });
        alert('操作成功');
        this.loadEvaluations();
      } catch (error) {
        console.error('操作失败:', error);
        alert('操作失败，请重试');
      } finally {
        this.showActionDialog = false;
      }
    },
    
    loadEvaluations() {
      this.evaluations = this.evaluations.map(e => ({ ...e }));
    }
  }
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.report-item {
  background: #fff8e1;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 8px;
}

.reason {
  color: #e65100;
  margin-bottom: 5px;
}

.status {
  font-size: 12px;
  color: #666;
}
</style>
