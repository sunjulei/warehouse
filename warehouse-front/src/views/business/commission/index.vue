<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">店员提成</h1>
      <p class="page-header-desc">提成规则配置与月度提成计算</p>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 提成记录 -->
      <el-tab-pane label="提成记录" name="records">
        <el-card>
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
            <el-date-picker
              v-model="selectedMonth"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              @change="loadCommissionRecords"
            />
            <el-select v-model="selectedRuleId" placeholder="选择提成规则" style="width: 200px;">
              <el-option v-for="r in rules" :key="r.id" :label="r.name" :value="r.id" />
            </el-select>
            <el-button type="primary" @click="handleCalculate" :loading="calculating">
              <el-icon><Calculator /></el-icon> 计算提成
            </el-button>
          </div>

          <el-table :data="records" border stripe>
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="operator" label="店员" width="120" />
            <el-table-column label="月销售额" width="130" align="right">
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
            <el-table-column label="提成金额" width="130" align="right">
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
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button v-if="row.status === 0" type="primary" link @click="handleConfirm(row, 1)">确认</el-button>
                <el-button v-if="row.status === 1" type="success" link @click="handleConfirm(row, 2)">标记已发放</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="records.length === 0" style="padding: 40px 0;">
            <el-empty description="选择月份后点击「计算提成」" />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 提成规则 -->
      <el-tab-pane label="提成规则" name="rules">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: 600;">提成规则配置</span>
              <el-button type="primary" size="small" @click="handleAddRule">新增规则</el-button>
            </div>
          </template>
          <el-collapse v-model="activeRuleId">
            <el-collapse-item v-for="rule in rules" :key="rule.id" :name="rule.id">
              <template #title>
                <div style="display: flex; align-items: center; gap: 8px; width: 100%;">
                  <el-tag :type="rule.type === 'fixed' ? 'primary' : 'warning'" size="small" effect="dark" round>
                    {{ rule.type === 'fixed' ? '固定比例' : '阶梯' }}
                  </el-tag>
                  <span style="font-weight: 600;">{{ rule.name }}</span>
                  <span v-if="rule.rate" style="color: var(--text-secondary); margin-left: 8px;">{{ rule.rate }}%</span>
                  <el-tag v-if="rule.status === 1" type="success" size="small" style="margin-left: auto;">启用</el-tag>
                  <el-tag v-else type="info" size="small" style="margin-left: auto;">禁用</el-tag>
                </div>
              </template>
              <div v-if="rule.type === 'tiered' && rule.tiers?.length" style="padding: 8px 0;">
                <el-table :data="rule.tiers" size="small" border>
                  <el-table-column label="销售额区间" min-width="200">
                    <template #default="{ row }">
                      ¥{{ row.minAmount?.toFixed(0) }} {{ row.maxAmount ? '- ¥' + row.maxAmount?.toFixed(0) : '以上' }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="rate" label="提成比例" width="100" align="center">
                    <template #default="{ row }">{{ row.rate }}%</template>
                  </el-table-column>
                </el-table>
              </div>
              <div v-if="rule.type === 'fixed'" style="padding: 8px 0; color: var(--text-secondary);">
                固定提成比例: {{ rule.rate }}%
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calculator } from '@element-plus/icons-vue'
import { loadAllRules, calculate, loadRecords, confirmRecord } from '@/api/commission'

const activeTab = ref('records')
const selectedMonth = ref('')
const selectedRuleId = ref<number | undefined>()
const calculating = ref(false)
const records = ref<any[]>([])
const rules = ref<any[]>([])
const activeRuleId = ref<number[]>([])

async function loadRules() {
  try {
    const res: any = await loadAllRules()
    rules.value = res.data || []
    const enabled = rules.value.find((r: any) => r.status === 1)
    if (enabled) selectedRuleId.value = enabled.id
  } catch {}
}

async function loadCommissionRecords() {
  if (!selectedMonth.value) return
  try {
    const res: any = await loadRecords({ yearMonth: selectedMonth.value })
    records.value = res.data || []
  } catch {}
}

async function handleCalculate() {
  if (!selectedMonth.value) return ElMessage.warning('请选择月份')
  if (!selectedRuleId.value) return ElMessage.warning('请选择提成规则')
  calculating.value = true
  try {
    const res: any = await calculate({ yearMonth: selectedMonth.value, ruleId: selectedRuleId.value })
    if (res.code === 200) {
      ElMessage.success(res.msg)
      await loadCommissionRecords()
    } else {
      ElMessage.error(res.msg)
    }
  } catch {} finally {
    calculating.value = false
  }
}

async function handleConfirm(row: any, status: number) {
  const text = status === 1 ? '确认' : '标记为已发放'
  try {
    await ElMessageBox.confirm(`确认将 ${row.operator} 的提成${text}？`, '确认')
    await confirmRecord({ id: row.id, status })
    ElMessage.success('操作成功')
    await loadCommissionRecords()
  } catch {}
}

function handleAddRule() {
  ElMessage.info('新增规则功能开发中')
}

onMounted(() => {
  const now = new Date()
  selectedMonth.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  loadRules().then(() => loadCommissionRecords())
})
</script>
