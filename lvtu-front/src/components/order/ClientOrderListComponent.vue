<template>
  <div class="client-order-list-component">
    <!-- 订单表头 -->
    <div class="order-table-header">
      <div class="col col-order-id">订单号</div>
      <div class="col col-business-type">业务类型</div>
      <div class="col col-lawyer-name">律师名称</div>
      <div class="col col-status">订单状态</div>
      <div class="col col-amount">金额</div>
      <div class="col col-date">创建时间</div>
      <div class="col col-action">操作</div>
    </div>

    <!-- 订单列表 -->
    <div v-if="!loading && filteredOrders.length > 0" class="order-list">
      <div
        v-for="order in paginatedOrders"
        :key="order.id"
        class="order-item"
      >
        <div class="col col-order-id">{{ order.orderId }}</div>
        <div class="col col-business-type">
          <span class="business-type-badge">{{ getBusinessTypeName(order.businessType) }}</span>
        </div>
        <div class="col col-lawyer-name">{{ order.lawyerName }}</div>
        <div class="col col-status">
          <span :class="['status-badge', `status-${order.status}`]">
            {{ getStatusName(order.status) }}
          </span>
        </div>
        <div class="col col-amount">¥{{ order.amount }}</div>
        <div class="col col-date">{{ formatDate(order.createTime) }}</div>
        <div class="col col-action">
          <button class="action-btn view-btn" @click="handleView(order.id)">
            查看
          </button>
          <button
            v-if="order.status === 'pending_payment'"
            class="action-btn pay-btn"
            @click="handlePay(order.id)"
          >
            支付
          </button>
          <button
            v-if="order.status === 'pending_review'"
            class="action-btn review-btn"
            @click="handleReview(order)"
          >
            评价
          </button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading && filteredOrders.length === 0" class="empty-state">
      <div class="empty-icon">📋</div>
      <p class="empty-text">暂无订单</p>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p class="loading-text">加载中...</p>
    </div>

    <!-- 分页 -->
    <div v-if="filteredOrders.length > 0" class="pagination">
      <button
        :disabled="currentPage === 1"
        class="pagination-btn"
        @click="currentPage--"
      >
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} 页</span>
      <button
        :disabled="currentPage >= totalPages"
        class="pagination-btn"
        @click="currentPage++"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
import { getUserOrders } from '@/api/order'
import { getPaymentByOrderId, updatePaymentStatus } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'ClientOrderListComponent',
  props: {
    status: {
      type: String,
      default: ''
    },
    businesses: {
      type: Array,
      default: () => []
    },
    keyword: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      orders: [],
      loading: false,
      businessTypeMap: {
        online_consult: '在线法律咨询',
        phone_consult: '电话法律咨询',
        doc_writing: '文书代写',
        contract_review: '合同审核',
        marriage_family: '婚姻家事',
        litigation: '诉讼代理'
      },
      statusMap: {
        pending_payment: '待支付',
        paid: '已支付',
        processing: '处理中',
        pending_customer_confirmation: '待客户确认',
        pending_review: '待评价',
        completed: '已完成',
        cancelled: '已取消',
        intervention: '平台介入'
      }
    }
  },
  mounted() {
    this.fetchOrders()
  },
  computed: {
    filteredOrders() {
      let filtered = this.orders

      // 按状态筛选
      if (this.status) {
        filtered = filtered.filter(order => order.status === this.status)
      }

      // 按业务类型筛选
      if (this.businesses.length > 0) {
        filtered = filtered.filter(order =>
          this.businesses.includes(order.businessType)
        )
      }

      // 按关键词搜索
      if (this.keyword) {
        const keyword = this.keyword.toLowerCase()
        filtered = filtered.filter(order =>
          String(order.orderId || '').toLowerCase().includes(keyword) ||
          String(order.lawyerName || '').toLowerCase().includes(keyword)
        )
      }

      return filtered
    },
    totalPages() {
      return Math.ceil(this.filteredOrders.length / this.pageSize)
    },
    paginatedOrders() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredOrders.slice(start, end)
    }
  },
  watch: {
    status() {
      this.currentPage = 1
      this.fetchOrders()
    },
    businesses() {
      this.currentPage = 1
      this.fetchOrders()
    },
    keyword() {
      this.currentPage = 1
      this.fetchOrders()
    }
  },
  methods: {
    async fetchOrders() {
      this.loading = true

      try {
        const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
        const userId = currentUser?.userId || currentUser?.id || userInfo?.id || Number(localStorage.getItem('userId'))

        if (!userId) {
          ElMessage.error('请先登录')
          this.orders = []
          return
        }

        const response = await getUserOrders(userId)
        const rows = response?.code === 200 ? response.data || [] : response?.data || []
        this.orders = rows.map(this.normalizeOrder)
      } catch (error) {
        console.error('获取订单列表出错:', error)
        ElMessage.error('网络连接失败，请检查后端服务是否正常')
        this.orders = []
      } finally {
        this.loading = false
      }
    },
    normalizeOrder(order) {
      const businessTypeMap = {
        101: 'online_consult',
        102: 'phone_consult',
        103: 'doc_writing',
        104: 'contract_review',
        105: 'marriage_family',
        106: 'litigation'
      }
      const statusMap = {
        待支付: 'pending_payment',
        已支付: 'paid',
        待接单: 'paid',
        处理中: 'processing',
        待顾客确认: 'pending_customer_confirmation',
        待客户确认: 'pending_customer_confirmation',
        待评价: 'pending_review',
        已完成: 'completed',
        已取消: 'cancelled',
        平台介入: 'intervention'
      }

      return {
        ...order,
        id: order.orderId || order.id,
        businessType: order.businessType || businessTypeMap[order.serviceTypeId] || order.serviceTypeId,
        lawyerName: order.lawyerName || (order.lawyerId ? `律师${order.lawyerId}` : '-'),
        status: statusMap[order.status] || order.status || '',
        amount: order.amount || order.totalAmount || 0,
        createTime: order.createTime || order.createdTime
      }
    },
    getBusinessTypeName(type) {
      return this.businessTypeMap[type] || type
    },
    getStatusName(status) {
      return this.statusMap[status] || status
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleDateString('zh-CN') : '-'
    },
    handleView(orderId) {
      console.log('查看订单:', orderId)
      this.$router.push({
        name: 'ClientOrderDetail',
        params: { orderId }
      })
    },
    handleReview(order) {
      this.$router.push({
        name: 'EvaluationDashboard',
        query: {
          orderId: order.id,
          lawyerId: order.lawyerId || ''
        }
      })
    },
    async handlePay(orderId) {
      try {
        await ElMessageBox.confirm('确认支付该订单吗？支付后订单将进入待接单状态。', '确认支付', {
          confirmButtonText: '确认支付',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const paymentResponse = await getPaymentByOrderId(orderId)
        const payment = paymentResponse?.code === 200 ? paymentResponse.data : paymentResponse?.data
        if (!payment?.paymentId) {
          ElMessage.error('未找到该订单的支付记录')
          return
        }

        const response = await updatePaymentStatus(payment.paymentId, '已支付')
        if (response?.code === 200) {
          ElMessage.success('支付成功，订单已进入待接单')
          await this.fetchOrders()
        } else {
          ElMessage.error(response?.message || '支付失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error?.response?.data?.message || '支付失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.client-order-list-component {
  width: 100%;
}

/* 表头 */
.order-table-header {
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr 1fr 0.8fr 1.2fr 1fr;
  gap: 15px;
  padding: 15px 20px;
  background-color: #f6f8fb;
  border: 1px solid #e8edf5;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
}

/* 列宽定义 */
.col {
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-order-id {
  grid-column: 1;
}

.col-business-type {
  grid-column: 2;
}

.col-lawyer-name {
  grid-column: 3;
}

.col-status {
  grid-column: 4;
}

.col-amount {
  grid-column: 5;
}

.col-date {
  grid-column: 6;
}

.col-action {
  grid-column: 7;
}

/* 订单项 */
.order-item {
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr 1fr 0.8fr 1.2fr 1fr;
  gap: 15px;
  padding: 15px 20px;
  border: 1px solid #edf1f7;
  border-radius: 10px;
  align-items: center;
  transition: all 0.2s ease;
  margin-bottom: 10px;
}

.order-item:hover {
  background-color: #fafcff;
  border-color: #d6e4ff;
  box-shadow: 0 6px 18px rgba(24, 144, 255, 0.08);
}

/* 业务类型标签 */
.business-type-badge {
  display: inline-block;
  padding: 4px 8px;
  background-color: #e6f7ff;
  color: #0050b3;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
}

/* 状态标签 */
.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.status-pending_payment {
  background-color: #fff7e6;
  color: #ad6800;
}

.status-paid {
  background-color: #e6f4ff;
  color: #0958d9;
}

.status-processing {
  background-color: #eef4ff;
  color: #1d4ed8;
}

.status-pending_customer_confirmation {
  background-color: #f3e8ff;
  color: #7e22ce;
}

.status-pending_review {
  background-color: #fff1f3;
  color: #c01048;
}

.status-completed {
  background-color: #f0fdf4;
  color: #15803d;
}

.status-cancelled {
  background-color: #fff1f0;
  color: #b42318;
}

.status-intervention {
  background-color: #fef3c7;
  color: #92400e;
}

/* 操作按钮 */
.action-btn {
  padding: 4px 12px;
  margin-right: 8px;
  border: 1px solid #d9d9d9;
  background-color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.view-btn {
  border-color: #1890ff;
  color: #1890ff;
}

.view-btn:hover {
  background-color: #e6f7ff;
}

.pay-btn {
  background-color: #1890ff;
  border-color: #1890ff;
  color: white;
}

.pay-btn:hover {
  background-color: #0050b3;
  border-color: #0050b3;
}

.review-btn {
  background-color: #c01048;
  border-color: #c01048;
  color: white;
}

.review-btn:hover {
  background-color: #a11043;
  border-color: #a11043;
  color: white;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background-color: #fafcff;
  border: 1px dashed #dbe4f0;
  border-radius: 10px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.empty-text {
  color: #999;
  font-size: 16px;
  margin: 0;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background-color: #fafcff;
  border: 1px dashed #dbe4f0;
  border-radius: 10px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f0f0f0;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination-btn {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background-color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .order-table-header,
  .order-item {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  }

  .col-amount {
    display: none;
  }
}

@media (max-width: 768px) {
  .order-table-header,
  .order-item {
    grid-template-columns: 1fr 1fr 1fr;
    gap: 10px;
  }

  .col-business-type,
  .col-date,
  .col-amount {
    display: none;
  }

  .order-item {
    padding: 10px 15px;
  }

  .action-btn {
    padding: 3px 8px;
    font-size: 11px;
    margin-right: 4px;
  }
}
</style>
