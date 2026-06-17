<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders } from '../../api/admin'
import { getPendingSettlements, paySettlement } from '../../api/settlement'

const loading = ref(false)
const payingId = ref(null)
const settlements = ref([])

const totalAmount = computed(() => {
  return settlements.value.reduce((sum, item) => sum + Number(item.amount || 0), 0)
})

const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')
const rowValue = (row, camelKey, snakeKey = camelKey) => row?.[camelKey] ?? row?.[snakeKey]
const lawyerDisplayName = (row) => rowValue(row, 'lawyerName', 'lawyer_name') || '-'

const loadSettlements = async () => {
  loading.value = true
  try {
    const [response, orderResponse] = await Promise.all([
      getPendingSettlements(),
      getOrders().catch(() => null)
    ])
    const orderRows = orderResponse?.code === 200 ? orderResponse.data || [] : []
    const orderMap = new Map(orderRows.map((order) => [String(order.order_id), order]))
    const rows = response?.code === 200 ? response.data || [] : []
    settlements.value = rows.map((item) => {
      const order = orderMap.get(String(rowValue(item, 'orderId', 'order_id')))
      return {
        ...item,
        lawyerName: rowValue(item, 'lawyerName', 'lawyer_name') || order?.lawyer_name || ''
      }
    })
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '结算记录加载失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async (settlement) => {
  try {
    await ElMessageBox.confirm(`确认向律师 ${lawyerDisplayName(settlement)} #${settlement.lawyerId} 结算 ${formatMoney(settlement.amount)} 吗？`, '确认结算', {
      confirmButtonText: '确认结算',
      cancelButtonText: '取消',
      type: 'warning'
    })
    payingId.value = settlement.id
    const response = await paySettlement(settlement.id)
    if (response?.code === 200) {
      ElMessage.success('结算成功')
      await loadSettlements()
    } else {
      ElMessage.error(response?.message || '结算失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '结算失败')
    }
  } finally {
    payingId.value = null
  }
}

onMounted(loadSettlements)
</script>

<template>
  <main class="settlement-page">
    <section class="page-head">
      <div>
        <p class="eyebrow">结算管理</p>
        <h1>律师待结算</h1>
        <p class="subtext">用户确认完成后生成结算记录，管理员在这里完成打款标记。</p>
      </div>
      <el-button type="primary" @click="loadSettlements">刷新</el-button>
    </section>

    <section class="summary-band">
      <div>
        <span>待结算笔数</span>
        <strong>{{ settlements.length }}</strong>
      </div>
      <div>
        <span>待结算金额</span>
        <strong>{{ formatMoney(totalAmount) }}</strong>
      </div>
    </section>

    <section class="table-shell" v-loading="loading">
      <el-empty v-if="!loading && settlements.length === 0" description="暂无待结算记录" />
      <el-table v-else :data="settlements" style="width: 100%">
        <el-table-column prop="id" label="结算ID" min-width="100" />
        <el-table-column prop="orderId" label="订单ID" min-width="110" />
        <el-table-column label="律师姓名" min-width="130">
          <template #default="{ row }">{{ lawyerDisplayName(row) }}</template>
        </el-table-column>
        <el-table-column prop="lawyerId" label="律师ID" min-width="110" />
        <el-table-column label="金额" min-width="120">
          <template #default="{ row }">{{ formatMoney(row.amount) }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :loading="payingId === row.id" @click="handlePay(row)">结算</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </main>
</template>

<style scoped>
.settlement-page {
  max-width: 1180px;
  margin: 0 auto;
  padding: 32px 20px 56px;
  color: #172033;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-end;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-weight: 700;
}

h1 {
  margin: 0;
  font-size: 30px;
}

.subtext {
  margin: 10px 0 0;
  color: #667085;
}

.summary-band {
  display: grid;
  grid-template-columns: repeat(2, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.summary-band div,
.table-shell {
  background: #fff;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  box-shadow: 0 8px 22px rgba(30, 64, 175, 0.06);
}

.summary-band div {
  padding: 18px;
}

.summary-band span {
  display: block;
  color: #667085;
  font-size: 13px;
  margin-bottom: 8px;
}

.summary-band strong {
  font-size: 24px;
  color: #1d4ed8;
}

.table-shell {
  min-height: 280px;
  padding: 18px;
}
</style>
