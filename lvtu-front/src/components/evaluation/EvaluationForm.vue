<template>
  <div class="evaluation-form">
    <div class="score-section">
      <div class="score-item">
        <label>专业度评分</label>
        <StarRating v-model="form.professionalScore" :max="5" />
      </div>
      
      <div class="score-item">
        <label>响应速度</label>
        <StarRating v-model="form.responsivenessScore" :max="5" />
      </div>
      
      <div class="score-item">
        <label>服务态度</label>
        <StarRating v-model="form.attitudeScore" :max="5" />
      </div>
    </div>
    
    <div class="content-section">
      <textarea 
        v-model="form.content" 
        placeholder="请分享您的服务体验（选填）"
        maxlength="500"
      ></textarea>
      <div class="char-count">{{ form.content.length }}/500</div>
    </div>
    
    <div class="anonymous-section">
      <input type="checkbox" id="anonymous" v-model="form.isAnonymous">
      <label for="anonymous">匿名评价</label>
    </div>
    
    <button @click="submitEvaluation" :disabled="isSubmitting" class="submit-btn">
      {{ isSubmitting ? '提交中...' : '提交评价' }}
    </button>
    
    <div v-if="error" class="alert alert-danger mt-3">
      {{ error }}
    </div>
  </div>
</template>

<script>
import StarRating from './StarRating.vue';
import { createEvaluation } from '../../services/evaluationService';
import { updateOrderStatus } from '@/api/order';

export default {
  components: {
    StarRating
  },
  emits: ['submitted'],
  props: {
    userId: {
      type: Number,
      default: null
    },
    orderId: {
      type: Number,
      default: 1001
    },
    lawyerId: {
      type: Number,
      default: 2001
    }
  },
  data() {
    return {
      form: {
        userId: this.userId,
        orderId: this.orderId,
        lawyerId: this.lawyerId,
        professionalScore: 5,
        responsivenessScore: 5,
        attitudeScore: 5,
        content: '',
        isAnonymous: false
      },
      isSubmitting: false,
      error: null
    };
  },
  methods: {
    async submitEvaluation() {
      if (!this.validateForm()) return;
      if (!this.form.userId) {
        this.error = '未获取到当前用户，请重新登录后再评价';
        return;
      }
      
      this.isSubmitting = true;
      this.error = null;
      
      try {
        const evaluation = await createEvaluation(this.form);
        const statusResponse = await updateOrderStatus(this.form.orderId, '已完成');
        if (statusResponse?.code !== 200 || statusResponse?.data === false) {
          throw new Error(statusResponse?.message || '订单状态更新失败');
        }
        alert('评价提交成功！');
        this.$emit('submitted', evaluation);
        this.resetForm();
      } catch (error) {
        this.error = error.response?.data?.message || error.message || '提交失败，请重试';
        console.error('评价提交失败:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
    
    validateForm() {
      const { professionalScore, responsivenessScore, attitudeScore } = this.form;
      
      if (professionalScore < 1 || professionalScore > 5 ||
          responsivenessScore < 1 || responsivenessScore > 5 ||
          attitudeScore < 1 || attitudeScore > 5) {
        alert('评分必须在1-5分之间');
        return false;
      }
      
      return true;
    },
    
    resetForm() {
      this.form = {
        userId: this.userId,
        orderId: this.orderId,
        lawyerId: this.lawyerId,
        professionalScore: 5,
        responsivenessScore: 5,
        attitudeScore: 5,
        content: '',
        isAnonymous: false
      };
    }
  },
  watch: {
    userId() {
      this.form.userId = this.userId;
    },
    orderId() {
      this.form.orderId = this.orderId;
    },
    lawyerId() {
      this.form.lawyerId = this.lawyerId;
    }
  }
}
</script>
