<template>
  <div class="client-order-list">
    <!-- 第一行：订单状态筛选 -->
    <div class="status-filter-row">
      <div class="filter-label">订单状态：</div>
      <div class="filter-buttons">
        <button
          v-for="status in statusOptions"
          :key="status.value"
          :class="['status-btn', { active: selectedStatus === status.value }]"
          @click="selectedStatus = status.value"
        >
          {{ status.label }}
        </button>
      </div>
    </div>

    <!-- 第二行：业务筛选和搜索框 -->
    <div class="filter-search-row">
      <div class="business-filter">
        <div class="filter-label">业务类型：</div>
        <div class="filter-buttons">
          <button
            v-for="business in businessOptions"
            :key="business.value"
            :class="['business-btn', { active: selectedBusinesses.includes(business.value) }]"
            @click="toggleBusiness(business.value)"
          >
            {{ business.label }}
          </button>
        </div>
      </div>

      <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索订单号、律师名称..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </div>
    </div>

    <!-- 第三行：订单列表组件 -->
    <div class="order-list-container">
      <ClientOrderListComponent
        :status="selectedStatus"
        :businesses="selectedBusinesses"
        :keyword="searchKeyword"
      />
    </div>
  </div>
</template>

<script>
import ClientOrderListComponent from '@/components/order/ClientOrderListComponent.vue'

export default {
  name: 'ClientOrderList',
  components: {
    ClientOrderListComponent
  },
  data() {
    return {
      selectedStatus: '',
      selectedBusinesses: [],
      searchKeyword: '',
      statusOptions: [
        { label: '全部', value: '' },
        { label: '待支付', value: 'pending_payment' },
        { label: '已支付', value: 'paid' },
        { label: '处理中', value: 'processing' },
        { label: '已完成', value: 'completed' },
        { label: '已取消', value: 'cancelled' }
      ],
      businessOptions: [
        { label: '在线法律咨询', value: 'online_consult' },
        { label: '电话法律咨询', value: 'phone_consult' },
        { label: '文书代写', value: 'doc_writing' },
        { label: '合同审核', value: 'contract_review' },
        { label: '婚姻家事', value: 'marriage_family' },
        { label: '诉讼代理', value: 'litigation' }
      ]
    }
  },
  methods: {
    toggleBusiness(businessValue) {
      const index = this.selectedBusinesses.indexOf(businessValue)
      if (index > -1) {
        this.selectedBusinesses.splice(index, 1)
      } else {
        this.selectedBusinesses.push(businessValue)
      }
    },
    handleSearch() {
      // 搜索逻辑由子组件处理
      console.log('搜索关键词:', this.searchKeyword)
    }
  }
}
</script>

<style scoped>
.client-order-list {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 第一行：订单状态 */
.status-filter-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-label {
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

.filter-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.status-btn,
.business-btn {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
}

.status-btn:hover,
.business-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.status-btn.active,
.business-btn.active {
  background-color: #1890ff;
  border-color: #1890ff;
  color: white;
}

/* 第二行：业务筛选和搜索 */
.filter-search-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
}

.business-filter {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
  min-width: 300px;
}

.search-box {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  width: 200px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.search-btn {
  padding: 8px 20px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.search-btn:hover {
  background-color: #0050b3;
}

/* 第三行：订单列表 */
.order-list-container {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .client-order-list {
    padding: 10px;
  }

  .status-filter-row,
  .filter-search-row {
    padding: 10px 15px;
    margin-bottom: 10px;
  }

  .filter-search-row {
    flex-direction: column;
    align-items: stretch;
  }

  .business-filter {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-box {
    width: 100%;
  }

  .search-input {
    flex: 1;
    width: auto;
    min-width: 0;
  }

  .filter-buttons {
    flex-wrap: wrap;
  }

  .status-btn,
  .business-btn {
    padding: 5px 12px;
    font-size: 12px;
  }

  .order-list-container {
    padding: 10px;
  }
}
</style>
