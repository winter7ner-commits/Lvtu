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
import { createEvaluation } from '../services/evaluationService';

export default {
  components: {
    StarRating
  },
  data() {
    return {
      form: {
        orderId: 1001,
        lawyerId: 2001,
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
      
      this.isSubmitting = true;
      this.error = null;
      
      try {
        await createEvaluation(this.form);
        alert('评价提交成功！');
        this.resetForm();
      } catch (error) {
        this.error = error.response?.data?.message || '提交失败，请重试';
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
        orderId: 1001,
        lawyerId: 2001,
        professionalScore: 5,
        responsivenessScore: 5,
        attitudeScore: 5,
        content: '',
        isAnonymous: false
      };
    }
  }
}
</script>
