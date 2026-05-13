<template>
  <div class="dialog-overlay" @click="closeDialog">
    <div class="dialog-content" @click.stop>
      <h3>确认操作</h3>
      <p>您确定要执行"{{ actionText }}"操作吗？</p>
      
      <div class="reason-section">
        <label>操作原因（必填）</label>
        <textarea 
          v-model="form.reason" 
          placeholder="请输入操作原因，至少10个字符"
          :maxlength="300"
          rows="3"
        ></textarea>
        <div class="char-count">{{ form.reason.length }}/300</div>
      </div>
      
      <div class="actions">
        <button @click="closeDialog" class="btn btn-secondary">取消</button>
        <button 
          @click="submitAction" 
          :disabled="form.reason.length < 10" 
          class="btn btn-primary"
        >
          确认操作
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    evaluationId: {
      type: Number,
      required: true
    },
    actionType: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      form: {
        reason: ''
      }
    };
  },
  computed: {
    actionText() {
      const actions = {
        HIDE: '隐藏评价',
        DELETE: '删除评价',
        RESTORE: '恢复评价',
        FEATURE: '设为精选',
        UNFEATURE: '取消精选',
        REJECT_REPORT: '驳回举报'
      };
      return actions[this.actionType] || this.actionType;
    }
  },
  methods: {
    closeDialog() {
      this.$emit('close');
    },
    
    async submitAction() {
      if (this.form.reason.length < 10) return;
      
      this.$emit('confirmed', this.form.reason);
    }
  }
}
</script>

<style scoped>
.reason-section {
  margin: 20px 0;
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
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
</style>
