<template>
  <div class="app-container">
    <h1 class="mb-4">律途评价系统</h1>
    
    <div class="row">
      <!-- 评价表单 -->
      <div class="col-md-6 mb-4">
        <div class="card">
          <div class="card-header">
            <i class="fas fa-edit me-2"></i>创建评价
          </div>
          <div class="card-body">
            <EvaluationForm />
          </div>
        </div>
      </div>
      
      <!-- 评价列表 -->
      <div class="col-md-6 mb-4">
        <div class="card">
          <div class="card-header">
            <i class="fas fa-list me-2"></i>评价列表
          </div>
          <div class="card-body">
            <EvaluationList :lawyer-id="2001" :can-report="true" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 管理面板 (仅管理员) -->
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header">
            <i class="fas fa-user-shield me-2"></i>管理员面板
          </div>
          <div class="card-body">
            <AdminPanel />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EvaluationForm from './components/EvaluationForm.vue';
import EvaluationList from './components/EvaluationList.vue';
import AdminPanel from './components/AdminPanel.vue';

export default {
  components: {
    EvaluationForm,
    EvaluationList,
    AdminPanel
  }
}
</script>
