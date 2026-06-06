<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">我的提成</h1>
      <p class="page-header-desc">查看个人月度提成明细</p>
    </div>

    <el-card>
      <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 20px;">
        <el-date-picker
          v-model="selectedMonth"
          type="month"
          placeholder="选择月份"
          value-format="YYYY-MM"
          clearable
          @change="loadData"
        />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>

      <!-- 汇总卡片 -->
      <div v-if="records.length > 0" style="display: flex; gap: 16px; margin-bottom: 20px;">
        <div class="stat-card">
          <div class="stat-label">累计销售额</div>
          <div class="stat-value">¥{{ totalSales.toFixed(2) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">累计订单数</div>
          <div class="stat-value">{{ totalOrders }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">累计提成</div>
          <div class="stat-value" style="color: var(--success-color);">¥{{ totalCommission.toFixed(2) }}</div>
        </div>
      </div>

      <el-table :data="records" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="yearMonth" label="月份" width="120" />
        <el-table-column label="月销售额" width="140" align="right">
          <template #default="{ row }">
            <span style="font-weight: 600;">¥{{ row.totalSales?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalOrders" label="订单数" width="80" align="center" />
        <el-table-column label="提成比例" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.commissionRate }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="提成金额" width="140" align="right">
          <template #default="{ row }">
            <span style="color: var(--success-color); font-weight: 700; font-size: 15px;">¥{{ row.commissionAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="['warning','','success'][row.status]" effect="dark" round>
              {{ ['待确认','已确认','已发放'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="records.length === 0" style="padding: 60px 0;">
        <el-empty description="暂无提成记录" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { loadMyCommission } from '@/api/commission'

const selectedMonth = ref('')
const records = ref<any[]>([])

const totalSales = computed(() => records.value.reduce((sum, r) => sum + (r.totalSales || 0), 0))
const totalOrders = computed(() => records.value.reduce((sum, r) => sum + (r.totalOrders || 0), 0))
const totalCommission = computed(() => records.value.reduce((sum, r) => sum + (r.commissionAmount || 0), 0))

async function loadData() {
  try {
    const params: any = {}
    if (selectedMonth.value) params.yearMonth = selectedMonth.value
    const res: any = await loadMyCommission(params)
    records.value = res.data || []
  } catch {
    records.value = []
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.stat-card {
  flex: 1;
  padding: 20px;
  border-radius: var(--border-radius-md, 8px);
  background: var(--bg-color-page, #f5f7fa);
  text-align: center;
}
.stat-label {
  font-size: 13px;
  color: var(--text-secondary, #909399);
  margin-bottom: 8px;
}
.stat-value {
  font-size: var(--font-size-4xl);
  font-weight: 700;
  color: var(--text-primary, #303133);
}
</style>
