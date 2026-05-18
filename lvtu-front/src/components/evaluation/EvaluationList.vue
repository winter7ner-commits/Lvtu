<template>
  <div class="evaluation-list">
    <div v-if="loading" class="loading">加载中...</div>
    
    <div v-else-if="evaluations.length === 0" class="empty">
      暂无评价
    </div>
    
    <div v-else class="list">
      <div v-for="item in evaluations" :key="item.evaluationId" class="evaluation-item">
        <div class="header">
          <div class="user-info">
            <div v-if="!item.isAnonymous" class="user-name">{{ item.userName }}</div>
            <div v-else class="user-name">匿名用户</div>
            <div class="time">{{ formatDate(item.createdTime) }}</div>
          </div>
          
          <div v-if="item.isFeatured" class="featured-tag">精选</div>
        </div>
        
        <div class="scores">
          <div class="score">
            <span>专业度:</span>
            <StarRating :value="item.professionalScore" :readOnly="true" />
          </div>
          <div class="score">
            <span>响应速度:</span>
            <StarRating :value="item.responsivenessScore" :readOnly="true" />
          </div>
          <div class="score">
            <span>服务态度:</span>
            <StarRating :value="item.attitudeScore" :readOnly="true" />
          </div>
          <div class="total-score">综合评分: {{ item.totalScore }}</div>
        </div>
        
        <div v-if="item.content" class="content">
          {{ item.content }}
        </div>
        
        <div v-if="item.reportCount > 0" class="report-count">
          已被举报 {{ item.reportCount }} 次
        </div>
        
        <div class="actions" v-if="canReport && item.status === '正常'">
          <button @click="openReportDialog(item.evaluationId)" class="report-btn">
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
import StarRating from './StarRating.vue';
import ReportDialog from './ReportDialog.vue';
import { getEvaluationsByLawyer } from '../../services/evaluationService';

export default {
  components: {
    StarRating,
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
  async created() {
    await this.loadEvaluations();
  },
  methods: {
    async loadEvaluations() {
      this.loading = true;
      try {
        const data = await getEvaluationsByLawyer(this.lawyerId, 1, 10);
        this.evaluations = data.list;
      } catch (error) {
        console.error('加载评价失败:', error);
        alert('加载评价失败');
      } finally {
        this.loading = false;
      }
    },
    
    openReportDialog(evaluationId) {
      this.selectedEvaluationId = evaluationId;
      this.showReportDialog = true;
    },
    
    handleReportSubmitted() {
      alert('举报已提交，我们会尽快处理');
      this.loadEvaluations();
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    }
  }
}
</script>
