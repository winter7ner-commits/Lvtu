<template>
  <div class="dialog-overlay" @click="closeDialog">
    <div class="dialog-content" @click.stop>
      <h3>举报评价</h3>
      <p>该评价将提交给管理员审核</p>
      
      <div class="reason-section">
        <label>举报原因</label>
        <div class="reason-options">
          <div 
            v-for="reason in reasons" 
            :key="reason.value"
            class="reason-option"
            :class="{ active: form.reason === reason.value }"
            @click="form.reason = reason.value"
          >
            {{ reason.label }}
          </div>
        </div>
      </div>
      
      <div class="description-section">
        <label>详细说明（选填）</label>
        <textarea 
          v-model="form.description" 
          placeholder="请说明举报原因，帮助我们更好地处理"
          maxlength="300"
        ></textarea>
        <div class="char-count">{{ form.description.length }}/300</div>
      </div>
      
      <div class="actions">
        <button @click="closeDialog" class="btn btn-secondary">取消</button>
        <button @click="submitReport" :disabled="!form.reason" class="btn btn-primary">
          提交举报
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { createReport } from '../../services/reportService';

export default {
  props: {
    evaluationId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      form: {
        evaluationId: this.evaluationId,
        reason: '',
        description: ''
      },
      reasons: [
        { value: 'ABUSE', label: '辱骂' },
        { value: 'FALSE', label: '虚假' },
        { value: 'AD', label: '广告' },
        { value: 'PRIVACY', label: '隐私' }
      ]
    };
  },
  methods: {
    closeDialog() {
      this.$emit('close');
    },
    
    async submitReport() {
      if (!this.form.reason) return;
      
      try {
        await createReport(this.form);
        this.$emit('submitted');
        this.closeDialog();
      } catch (error) {
        console.error('举报提交失败:', error);
        alert('举报提交失败，请重试');
      }
    }
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: white;
  width: 100%;
  max-width: 500px;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.reason-section {
  margin-bottom: 20px;
}

.reason-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 8px;
}

.description-section {
  margin-bottom: 20px;
}

textarea {
  width: 100%;
  height: 100px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  margin-top: 8px;
}

.char-count {
  text-align: right;
  color: #888;
  font-size: 12px;
  margin-top: 5px;
}

.actions {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.reason-option {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.reason-option.active {
  background: #e3f2fd;
  border-color: #4a6cf7;
  color: #4a6cf7;
}
</style>
