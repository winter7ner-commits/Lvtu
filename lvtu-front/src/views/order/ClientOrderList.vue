<template>
  <div class="client-order-list">
    <!-- 第一行：订单状态筛选 -->
    <div class="status-filter-row">
      <div class="filter-label">订单状态：</div>
      <div class="filter-buttons">
        <button
          v-for="status in statusOptions"
          :key="status.value"
          :class="['status-btn', `status-btn-${status.value || 'all'}`, { active: selectedStatus === status.value }]"
          @click="selectStatus(status.value)"
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
        <button
          class="clear-business-btn"
          :disabled="selectedBusinesses.length === 0"
          @click="clearBusinesses"
        >
          清空
        </button>
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
        { label: '待客户确认', value: 'pending_customer_confirmation' },
        { label: '待评价', value: 'pending_review' },
        { label: '已完成', value: 'completed' },
        { label: '已取消', value: 'cancelled' },
        { label: '平台介入', value: 'intervention' }
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
  watch: {
    '$route.query.status': {
      immediate: true,
      handler(status) {
        const normalizedStatus = Array.isArray(status) ? status[0] : status
        const validStatuses = this.statusOptions.map((item) => item.value)
        this.selectedStatus = validStatuses.includes(normalizedStatus) ? normalizedStatus : ''
      }
    }
  },
  methods: {
    selectStatus(status) {
      const query = { ...this.$route.query }

      if (status) {
        query.status = status
      } else {
        delete query.status
      }

      this.$router.replace({
        name: 'ClientOrderList',
        query
      })
    },
    toggleBusiness(businessValue) {
      const index = this.selectedBusinesses.indexOf(businessValue)
      if (index > -1) {
        this.selectedBusinesses.splice(index, 1)
      } else {
        this.selectedBusinesses.push(businessValue)
      }
    },
    clearBusinesses() {
      this.selectedBusinesses = []
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
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
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
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
}

.status-btn {
  border-color: var(--status-border);
  color: var(--status-color);
}

.business-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.business-btn.active {
  background-color: #1890ff;
  border-color: #1890ff;
  color: white;
}

.status-btn.active,
.status-btn:hover {
  background-color: var(--status-bg);
  border-color: var(--status-border);
  color: var(--status-color);
}

.status-btn-all {
  --status-bg: #f6f8fb;
  --status-border: #cfd8e3;
  --status-color: #344054;
}

.status-btn-pending_payment {
  --status-bg: #fff7e6;
  --status-border: #ffd591;
  --status-color: #ad6800;
}

.status-btn-paid {
  --status-bg: #e6f4ff;
  --status-border: #91caff;
  --status-color: #0958d9;
}

.status-btn-processing {
  --status-bg: #eef4ff;
  --status-border: #a4bcfd;
  --status-color: #1d4ed8;
}

.status-btn-pending_customer_confirmation {
  --status-bg: #f3e8ff;
  --status-border: #d8b4fe;
  --status-color: #7e22ce;
}

.status-btn-pending_review {
  --status-bg: #fff1f3;
  --status-border: #fecdd6;
  --status-color: #c01048;
}

.status-btn-completed {
  --status-bg: #f0fdf4;
  --status-border: #bbf7d0;
  --status-color: #15803d;
}

.status-btn-cancelled {
  --status-bg: #fff1f0;
  --status-border: #ffa39e;
  --status-color: #b42318;
}

.status-btn-intervention {
  --status-bg: #fef3c7;
  --status-border: #fbbf24;
  --status-color: #92400e;
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
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  flex-wrap: wrap;
}

.business-filter {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
  min-width: 300px;
}

.clear-business-btn {
  padding: 6px 14px;
  border: 1px solid #d9d9d9;
  background-color: #fff;
  border-radius: 8px;
  color: #595959;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.clear-business-btn:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
  background-color: #e6f7ff;
}

.clear-business-btn:disabled {
  color: #bfbfbf;
  cursor: not-allowed;
  background-color: #f5f5f5;
}

.search-box {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
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
  border-radius: 8px;
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
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  padding: 20px;
  overflow: hidden;
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
