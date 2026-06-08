<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">店员提成</h1>
      <p class="page-header-desc">提成规则配置、月度提成计算与历史记录查询</p>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 计算提成 -->
      <el-tab-pane label="计算提成" name="calc">
        <div class="calc-hero">
          <div class="calc-hero-icon">
            <el-icon :size="56" color="#409EFF"><DataAnalysis /></el-icon>
          </div>
          <h2>月度提成计算</h2>
          <p>选择月份和提成规则，系统将自动汇总该月所有销售记录，按规则计算每位店员的提成金额</p>
          <el-button type="primary" size="large" @click="openCalcDialog" class="calc-hero-btn">
            <el-icon style="margin-right: 8px;"><Cpu /></el-icon> 开始计算
          </el-button>
        </div>

        <!-- 最近一次计算结果 -->
        <template v-if="records.length > 0">
          <div class="result-summary">
            <div class="summary-item summary-blue">
              <el-icon :size="24"><User /></el-icon>
              <div>
                <div class="summary-val">{{ records.length }}</div>
                <div class="summary-label">参与店员</div>
              </div>
            </div>
            <div class="summary-item summary-green">
              <el-icon :size="24"><TrendCharts /></el-icon>
              <div>
                <div class="summary-val">¥{{ totalSales.toFixed(2) }}</div>
                <div class="summary-label">总销售额</div>
              </div>
            </div>
            <div class="summary-item summary-orange">
              <el-icon :size="24"><Wallet /></el-icon>
              <div>
                <div class="summary-val">¥{{ totalCommission.toFixed(2) }}</div>
                <div class="summary-label">总提成</div>
              </div>
            </div>
          </div>

          <el-card shadow="never">
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-weight: 600; font-size: 15px;">{{ lastCalcMonth }} 提成明细</span>
                <el-tag type="info" size="small">共 {{ records.length }} 条</el-tag>
              </div>
            </template>
            <el-table :data="records" border stripe>
              <el-table-column type="index" label="#" width="50" align="center" />
              <el-table-column prop="operator" label="店员" width="120" />
              <el-table-column label="月销售额" width="140" align="right">
                <template #default="{ row }">
                  <span style="font-weight: 600;">¥{{ row.totalSales?.toFixed(2) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="totalOrders" label="订单数" width="80" align="center" />
              <el-table-column label="提成比例" width="100" align="center">
                <template #default="{ row }">
                  <el-tag type="primary" size="small" effect="plain">{{ row.commissionRate }}%</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="提成金额" width="140" align="right">
                <template #default="{ row }">
                  <span style="color: #E6A23C; font-weight: 700; font-size: 16px;">¥{{ row.commissionAmount?.toFixed(2) }}</span>
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
          </el-card>
        </template>
      </el-tab-pane>

      <!-- 提成记录 -->
      <el-tab-pane label="提成记录" name="records">
        <el-card shadow="never">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
            <el-date-picker
              v-model="queryMonth"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              clearable
            />
            <el-button type="primary" @click="loadHistoryRecords">
              <el-icon><Search /></el-icon> 查询
            </el-button>
          </div>

          <template v-if="historyRecords.length > 0">
            <div class="result-summary" style="margin-bottom: 16px;">
              <div class="summary-item summary-blue">
                <el-icon :size="24"><User /></el-icon>
                <div>
                  <div class="summary-val">{{ historyRecords.length }}</div>
                  <div class="summary-label">参与店员</div>
                </div>
              </div>
              <div class="summary-item summary-green">
                <el-icon :size="24"><TrendCharts /></el-icon>
                <div>
                  <div class="summary-val">¥{{ historyTotalSales.toFixed(2) }}</div>
                  <div class="summary-label">总销售额</div>
                </div>
              </div>
              <div class="summary-item summary-orange">
                <el-icon :size="24"><Wallet /></el-icon>
                <div>
                  <div class="summary-val">¥{{ historyTotalCommission.toFixed(2) }}</div>
                  <div class="summary-label">总提成</div>
                </div>
              </div>
            </div>
            <el-table :data="historyRecords" border stripe>
              <el-table-column type="index" label="#" width="50" align="center" />
              <el-table-column prop="operator" label="店员" width="120" />
              <el-table-column label="月销售额" width="140" align="right">
                <template #default="{ row }">
                  <span style="font-weight: 600;">¥{{ row.totalSales?.toFixed(2) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="totalOrders" label="订单数" width="80" align="center" />
              <el-table-column label="提成比例" width="100" align="center">
                <template #default="{ row }">
                  <el-tag type="primary" size="small" effect="plain">{{ row.commissionRate }}%</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="提成金额" width="140" align="right">
                <template #default="{ row }">
                  <span style="color: #E6A23C; font-weight: 700; font-size: 16px;">¥{{ row.commissionAmount?.toFixed(2) }}</span>
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
          </template>
          <el-empty v-else description="选择月份查询历史提成记录" />
        </el-card>
      </el-tab-pane>

      <!-- 提成规则 -->
      <el-tab-pane label="提成规则" name="rules">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: 600;">提成规则配置</span>
              <el-button type="primary" @click="handleAddRule">
                <el-icon><Plus /></el-icon> 新增规则
              </el-button>
            </div>
          </template>

          <el-empty v-if="rules.length === 0" description="暂无提成规则，点击上方按钮新增" />

          <div v-for="rule in rules" :key="rule.id" class="rule-card">
            <div class="rule-header">
              <div class="rule-info">
                <el-tag :type="rule.type === 'fixed' ? 'primary' : 'warning'" size="small" effect="dark" round>
                  {{ rule.type === 'fixed' ? '固定比例' : '阶梯' }}
                </el-tag>
                <span class="rule-name">{{ rule.name }}</span>
                <span v-if="rule.type === 'fixed' && rule.rate" class="rule-rate">{{ rule.rate }}%</span>
                <el-tag v-if="rule.status === 1" type="success" size="small">启用</el-tag>
                <el-tag v-else type="info" size="small">禁用</el-tag>
              </div>
              <div class="rule-actions">
                <el-button type="primary" link size="small" @click="handleEditRule(rule)">编辑</el-button>
                <el-button v-if="rule.type === 'tiered'" type="warning" link size="small" @click="handleEditTiers(rule)">配置阶梯</el-button>
                <el-button v-if="rule.status === 0" type="success" link size="small" @click="handleToggleStatus(rule, 1)">启用</el-button>
                <el-button v-if="rule.status === 1" type="info" link size="small" @click="handleToggleStatus(rule, 0)">禁用</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteRule(rule)">删除</el-button>
              </div>
            </div>

            <div v-if="rule.type === 'tiered' && rule.tiers?.length" class="rule-tiers">
              <el-table :data="rule.tiers" size="small" border>
                <el-table-column label="销售额区间" min-width="200">
                  <template #default="{ row }">
                    ¥{{ row.minAmount?.toFixed(0) }} {{ row.maxAmount ? '- ¥' + row.maxAmount?.toFixed(0) : ' 以上' }}
                  </template>
                </el-table-column>
                <el-table-column prop="rate" label="提成比例" width="100" align="center">
                  <template #default="{ row }">{{ row.rate }}%</template>
                </el-table-column>
              </el-table>
            </div>
            <div v-if="rule.type === 'tiered' && !rule.tiers?.length" class="rule-tiers-empty">
              暂未配置阶梯明细，点击「配置阶梯」添加
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 计算提成弹窗 -->
    <el-dialog v-model="calcDialogVisible" title="计算月度提成" width="480px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="月份" required>
          <el-date-picker v-model="calcForm.month" type="month" value-format="YYYY-MM" placeholder="选择月份" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="提成规则" required>
          <el-select v-model="calcForm.ruleId" placeholder="选择提成规则" style="width: 100%;">
            <el-option v-for="r in rules.filter(r => r.status === 1)" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div style="margin-top: 12px; display: flex; align-items: center; gap: 8px; color: #F56C6C; font-size: 13px;">
        <el-icon><WarningFilled /></el-icon>
        <span>计算将覆盖该月已有的提成记录</span>
      </div>
      <template #footer>
        <el-button @click="calcDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCalculate" :loading="calculating">
          <el-icon v-if="!calculating" style="margin-right: 4px;"><Cpu /></el-icon>
          {{ calculating ? '计算中...' : '开始计算' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑规则弹窗 -->
    <el-dialog v-model="ruleDialogVisible" :title="editingRule.id ? '编辑规则' : '新增规则'" width="480px">
      <el-form :model="editingRule" label-width="100px">
        <el-form-item label="规则名称" required>
          <el-input v-model="editingRule.name" placeholder="如：销售提成A" />
        </el-form-item>
        <el-form-item label="提成类型" required>
          <el-radio-group v-model="editingRule.type">
            <el-radio value="fixed">固定比例</el-radio>
            <el-radio value="tiered">阶梯提成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="editingRule.type === 'fixed'" label="提成比例" required>
          <el-input-number v-model="editingRule.rate" :min="0" :max="100" :precision="2" :step="0.5" />
          <span style="margin-left: 8px; color: var(--text-secondary);">%</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editingRule.remark" type="textarea" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRule" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 阶梯配置弹窗 -->
    <el-dialog v-model="tierDialogVisible" title="配置阶梯提成" width="620px" top="6vh">
      <div style="margin-bottom: 12px; color: var(--text-secondary); font-size: 13px;">
        按销售额区间设置不同提成比例，系统自动匹配适用区间。最高销售额留空表示不设上限。
      </div>
      <div style="max-height: 400px; overflow-y: auto;">
        <div v-for="(tier, index) in editingTiers" :key="index" style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px; padding: 10px; border: 1px solid var(--border-color-lighter, #ebeef5); border-radius: 6px;">
          <div style="flex: 1;">
            <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 4px;">最低销售额 (¥)</div>
            <el-input-number v-model="tier.minAmount" :min="0" :precision="0" size="small" controls-position="right" style="width: 100%;" />
          </div>
          <div style="flex: 1;">
            <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 4px;">最高销售额 (¥)</div>
            <el-input-number v-model="tier.maxAmount" :min="0" :precision="0" size="small" controls-position="right" style="width: 100%;" placeholder="不限" />
          </div>
          <div style="width: 120px;">
            <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 4px;">提成比例 (%)</div>
            <el-input-number v-model="tier.rate" :min="0" :max="100" :precision="2" :step="0.5" size="small" controls-position="right" style="width: 100%;" />
          </div>
          <el-button type="danger" :icon="Delete" link style="margin-top: 18px;" @click="editingTiers.splice(index, 1)" />
        </div>
      </div>
      <el-button style="margin-top: 12px;" @click="editingTiers.push({ minAmount: 0, maxAmount: null, rate: 0 })">
        <el-icon><Plus /></el-icon> 添加阶梯
      </el-button>
      <template #footer>
        <el-button @click="tierDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTiers" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, Plus, Delete, Cpu, User, TrendCharts, Wallet, Search, WarningFilled } from '@element-plus/icons-vue'
import { loadAllRules, saveRule, deleteRule, saveTiers, calculate, loadRecords, confirmRecord } from '@/api/commission'

const activeTab = ref('calc')
const calculating = ref(false)
const records = ref<any[]>([])
const lastCalcMonth = ref('')
const rules = ref<any[]>([])

// 计算弹窗
const calcDialogVisible = ref(false)
const calcForm = reactive({ month: '', ruleId: undefined as number | undefined })

// 查询记录
const queryMonth = ref('')
const historyRecords = ref<any[]>([])

// 规则弹窗
const ruleDialogVisible = ref(false)
const editingRule = reactive<any>({ name: '', type: 'fixed', rate: null, remark: '' })
const saving = ref(false)

// 阶梯弹窗
const tierDialogVisible = ref(false)
const editingTiers = ref<any[]>([])
const currentTierRuleId = ref<number | null>(null)

const totalSales = computed(() => records.value.reduce((sum, r) => sum + (r.totalSales || 0), 0))
const totalCommission = computed(() => records.value.reduce((sum, r) => sum + (r.commissionAmount || 0), 0))
const historyTotalSales = computed(() => historyRecords.value.reduce((sum, r) => sum + (r.totalSales || 0), 0))
const historyTotalCommission = computed(() => historyRecords.value.reduce((sum, r) => sum + (r.commissionAmount || 0), 0))

async function loadRules() {
  try {
    const res: any = await loadAllRules()
    rules.value = res.data || []
  } catch {}
}

function openCalcDialog() {
  const now = new Date()
  calcForm.month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  const enabled = rules.value.find((r: any) => r.status === 1)
  calcForm.ruleId = enabled?.id
  calcDialogVisible.value = true
}

async function handleCalculate() {
  if (!calcForm.month) return ElMessage.warning('请选择月份')
  if (!calcForm.ruleId) return ElMessage.warning('请选择提成规则')
  try {
    await ElMessageBox.confirm(
      `即将对 ${calcForm.month} 的销售数据执行提成计算，该月已有的提成记录将被覆盖，是否继续？`,
      '确认计算',
      { confirmButtonText: '确认计算', cancelButtonText: '取消', type: 'warning' }
    )
  } catch { return }
  calculating.value = true
  try {
    const res: any = await calculate({ yearMonth: calcForm.month, ruleId: calcForm.ruleId })
    if (res.code === 200) {
      ElMessage.success(res.msg)
      calcDialogVisible.value = false
      lastCalcMonth.value = calcForm.month
      const loadRes: any = await loadRecords({ yearMonth: calcForm.month })
      records.value = loadRes.data || []
    } else {
      ElMessage.error(res.msg)
    }
  } catch {} finally {
    calculating.value = false
  }
}

async function loadHistoryRecords() {
  if (!queryMonth.value) {
    historyRecords.value = []
    return
  }
  try {
    const res: any = await loadRecords({ yearMonth: queryMonth.value })
    historyRecords.value = res.data || []
  } catch {}
}

async function handleConfirm(row: any, status: number) {
  const text = status === 1 ? '确认' : '标记为已发放'
  try {
    await ElMessageBox.confirm(`确认将 ${row.operator} 的提成${text}？`, '确认')
    await confirmRecord({ id: row.id, status })
    ElMessage.success('操作成功')
    if (activeTab.value === 'calc' && lastCalcMonth.value) {
      const res: any = await loadRecords({ yearMonth: lastCalcMonth.value })
      records.value = res.data || []
    }
    if (activeTab.value === 'records' && queryMonth.value) {
      await loadHistoryRecords()
    }
  } catch {}
}

// ===== 规则管理 =====

function handleAddRule() {
  Object.assign(editingRule, { id: null, name: '', type: 'fixed', rate: null, remark: '' })
  ruleDialogVisible.value = true
}

function handleEditRule(rule: any) {
  Object.assign(editingRule, {
    id: rule.id,
    name: rule.name,
    type: rule.type,
    rate: rule.rate,
    remark: rule.remark
  })
  ruleDialogVisible.value = true
}

async function handleSaveRule() {
  if (!editingRule.name) return ElMessage.warning('请输入规则名称')
  if (editingRule.type === 'fixed' && !editingRule.rate) return ElMessage.warning('请输入提成比例')
  saving.value = true
  try {
    const res: any = await saveRule({
      id: editingRule.id,
      name: editingRule.name,
      type: editingRule.type,
      rate: editingRule.type === 'fixed' ? editingRule.rate : null,
      remark: editingRule.remark
    })
    if (res.code === 200) {
      ElMessage.success(editingRule.id ? '修改成功' : '添加成功')
      ruleDialogVisible.value = false
      await loadRules()
    } else {
      ElMessage.error(res.msg)
    }
  } catch {} finally {
    saving.value = false
  }
}

async function handleDeleteRule(rule: any) {
  try {
    await ElMessageBox.confirm(`确认删除规则「${rule.name}」？关联的阶梯明细也会一并删除。`, '确认删除', { type: 'warning' })
    await deleteRule({ id: rule.id })
    ElMessage.success('删除成功')
    await loadRules()
  } catch {}
}

async function handleToggleStatus(rule: any, status: number) {
  try {
    await saveRule({ id: rule.id, status })
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
    await loadRules()
  } catch {}
}

// ===== 阶梯配置 =====

function handleEditTiers(rule: any) {
  currentTierRuleId.value = rule.id
  editingTiers.value = (rule.tiers || []).map((t: any) => ({
    id: t.id,
    minAmount: t.minAmount,
    maxAmount: t.maxAmount,
    rate: t.rate
  }))
  if (editingTiers.value.length === 0) {
    editingTiers.value.push({ minAmount: 0, maxAmount: null, rate: 0 })
  }
  tierDialogVisible.value = true
}

async function handleSaveTiers() {
  for (const tier of editingTiers.value) {
    if (tier.rate == null || tier.rate <= 0) return ElMessage.warning('每级提成比例必须大于0')
  }
  saving.value = true
  try {
    const res: any = await saveTiers({
      ruleId: currentTierRuleId.value,
      tiers: editingTiers.value.map((t: any) => ({
        minAmount: t.minAmount,
        maxAmount: t.maxAmount,
        rate: t.rate
      }))
    })
    if (res.code === 200) {
      ElMessage.success('阶梯配置保存成功')
      tierDialogVisible.value = false
      await loadRules()
    } else {
      ElMessage.error(res.msg)
    }
  } catch {} finally {
    saving.value = false
  }
}

watch(activeTab, (tab) => {
  if (tab === 'records' && queryMonth.value) {
    loadHistoryRecords()
  }
})

onMounted(() => {
  const now = new Date()
  queryMonth.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  loadRules()
  loadHistoryRecords()
})
</script>

<style scoped>
.rule-card {
  border: 1px solid var(--border-color-light, #e4e7ed);
  border-radius: var(--border-radius-md, 8px);
  padding: 16px;
  margin-bottom: 12px;
  transition: box-shadow 0.2s;
}
.rule-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.rule-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}
.rule-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.rule-name {
  font-weight: 600;
  font-size: 15px;
}
.rule-rate {
  color: var(--text-secondary, #909399);
  font-size: 13px;
}
.rule-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
.rule-tiers {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color-light, #e4e7ed);
}
.rule-tiers-empty {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color-light, #e4e7ed);
  color: var(--text-placeholder, #c0c4cc);
  font-size: 13px;
}

/* 计算提成英雄区 */
.calc-hero {
  text-align: center;
  padding: 60px 20px 40px;
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f0ff 100%);
  border-radius: 12px;
  margin-bottom: 24px;
}
.calc-hero-icon {
  margin-bottom: 16px;
}
.calc-hero h2 {
  margin: 0 0 8px;
  font-size: 22px;
  color: #303133;
}
.calc-hero p {
  margin: 0 0 24px;
  font-size: 14px;
  color: #909399;
  max-width: 480px;
  margin-left: auto;
  margin-right: auto;
}
.calc-hero-btn {
  height: 48px;
  font-size: 16px;
  padding: 0 40px;
}

/* 汇总统计 */
.result-summary {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.summary-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 24px;
  border-radius: 12px;
  color: #fff;
}
.summary-blue { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.summary-green { background: linear-gradient(135deg, #67C23A, #85ce61); }
.summary-orange { background: linear-gradient(135deg, #E6A23C, #ebb563); }
.summary-val {
  font-size: var(--font-size-4xl);
  font-weight: 700;
  line-height: 1.2;
}
.summary-label {
  font-size: 13px;
  opacity: 0.9;
}
</style>

<style>
[data-theme="dark"] .calc-hero {
  background: linear-gradient(135deg, #1a2332 0%, #1f1a2e 100%);
}
[data-theme="dark"] .calc-hero h2 {
  color: #e7e5e4;
}
[data-theme="dark"] .calc-hero p {
  color: #a8a29e;
}
</style>
