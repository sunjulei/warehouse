<template>
  <div class="dashboard">
    <!-- 顶部问候 + 统计卡片 -->
    <div class="dashboard-top">
      <div class="greeting-bar">
        <div class="greeting-text">
          <span class="greeting-hello">{{ greeting }}，{{ authStore.user?.name || '管理员' }}</span>
          <span class="greeting-sub">仓图 · 实时数据概览</span>
        </div>
      </div>
      <div class="stats-row">
        <div class="stat-card" v-for="stat in statCards" :key="stat.key">
          <div class="stat-icon-wrap" :style="{ background: stat.bg, color: stat.color }">
            <el-icon :size="20"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 中间：公告 + 库存预警 -->
    <div class="dashboard-mid">
      <!-- 公告通知 -->
      <div class="panel notice-panel">
        <div class="panel-header">
          <div class="panel-header-left">
            <el-icon class="panel-icon"><Bell /></el-icon>
            <span class="panel-title">公告通知</span>
          </div>
          <el-tag v-if="notices.length > 0" type="primary" size="small" effect="plain" round>{{ notices.length }} 条</el-tag>
        </div>
        <div class="panel-body">
          <div v-if="notices.length === 0" class="empty-mini">
            <el-icon :size="28"><Bell /></el-icon>
            <span>暂无公告</span>
          </div>
          <div v-else class="notice-list">
            <div
              v-for="item in notices"
              :key="item.id"
              class="notice-item"
              @click="handleNoticeClick(item)"
            >
              <div class="notice-dot"></div>
              <div class="notice-body">
                <span class="notice-title">{{ item.title }}</span>
                <span class="notice-meta">{{ item.opername }} · {{ item.createtime }}</span>
              </div>
              <el-icon class="notice-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 库存预警 -->
      <div class="panel warning-panel">
        <div class="panel-header">
          <div class="panel-header-left">
            <el-icon class="panel-icon warning"><Warning /></el-icon>
            <span class="panel-title">库存预警</span>
          </div>
          <el-tag v-if="warningGoods.length > 0" type="danger" size="small" effect="dark" round>{{ warningGoods.length }} 项</el-tag>
        </div>
        <div class="panel-body">
          <div v-if="warningGoods.length === 0" class="empty-mini success">
            <el-icon :size="28"><CircleCheck /></el-icon>
            <span>所有商品库存正常</span>
          </div>
          <el-table
            v-else
            :data="warningGoods"
            size="small"
            stripe
            :header-cell-style="{ background: 'var(--bg-tertiary)', color: 'var(--text-primary)', fontWeight: '600', fontSize: '12px' }"
          >
            <el-table-column prop="goodsname" label="商品" min-width="80" show-overflow-tooltip />
            <el-table-column prop="number" label="库存" width="60" align="center">
              <template #default="{ row }">
                <span class="danger-num">{{ row.number }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="dangernum" label="预警值" width="60" align="center">
              <template #default="{ row }">
                <span class="warn-num">{{ row.dangernum }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 底部：操作记录 -->
    <div class="panel ops-panel animate-fade-in-up">
      <div class="panel-header">
        <div class="panel-header-left">
          <el-icon class="panel-icon"><List /></el-icon>
          <span class="panel-title">最近操作记录</span>
          <span v-if="authStore.user && authStore.user.type !== 0" class="panel-hint">仅显示我的</span>
        </div>
        <el-tag v-if="totalOps > 0" type="info" size="small" effect="plain" round>{{ totalOps }} 条</el-tag>
      </div>
      <div class="panel-body">
        <div v-if="operations.length === 0" class="empty-mini">
          <el-icon :size="28"><DocumentRemove /></el-icon>
          <span>暂无操作记录</span>
        </div>
        <template v-else>
          <el-table
            :data="operations"
            size="small"
            stripe
            :header-cell-style="{ background: 'var(--bg-tertiary)', color: 'var(--text-primary)', fontWeight: '600', fontSize: '12px' }"
          >
            <el-table-column prop="type" label="类型" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="typeTagMap[row.type] || 'info'" size="small" effect="dark" round>
                  {{ row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="module" label="模块" width="80" align="center" />
            <el-table-column prop="description" label="操作描述" min-width="160" show-overflow-tooltip />
            <el-table-column prop="operateperson" label="操作人" width="70" align="center" />
            <el-table-column prop="operatetime" label="时间" width="160">
              <template #default="{ row }">
                <span class="ops-time">{{ row.operatetime }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div class="ops-pagination">
            <el-pagination
              small
              layout="prev, pager, next"
              :total="totalOps"
              :page-size="opsPageSize"
              v-model:current-page="opsCurrentPage"
              @current-change="handleOpsPageChange"
            />
          </div>
        </template>
      </div>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="noticeDialogVisible" :title="currentNotice?.title" width="520px">
      <div style="color: var(--text-secondary); font-size: 13px; margin-bottom: 16px;">
        <el-icon><Clock /></el-icon> {{ currentNotice?.createtime }} · {{ currentNotice?.opername }}
      </div>
      <div style="line-height: 1.8; font-size: 14px; color: var(--text-regular); white-space: pre-wrap;">{{ currentNotice?.content || '暂无内容' }}</div>
      <template #footer>
        <el-button type="primary" @click="noticeDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { loadAllWarningGoods, loadDashboardStats } from '@/api/goods'
import { loadAllOperationLog } from '@/api/operationLog'
import { loadAllNotice } from '@/api/notice'

const authStore = useAuthStore()

const operations = ref<any[]>([])
const totalOps = ref(0)
const opsCurrentPage = ref(1)
const opsPageSize = 8
const typeTagMap: Record<string, string> = {
  '添加': 'success',
  '修改': 'primary',
  '删除': 'danger'
}
const notices = ref<any[]>([])
const noticeDialogVisible = ref(false)
const currentNotice = ref<any>(null)
const warningGoods = ref<any[]>([])
const goodsTotal = ref(0)
const todayInport = ref(0)
const todaySales = ref(0)

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 12) return '早上好 ☀️'
  if (hour < 14) return '中午好 🌤️'
  if (hour < 18) return '下午好 🌅'
  return '晚上好 🌙'
})

const statCards = computed(() => [
  { key: 'total', label: '商品总数', value: goodsTotal.value, icon: 'Box', bg: 'var(--accent-bg)', color: 'var(--primary-color)' },
  { key: 'warning', label: '低库存预警', value: warningGoods.value.length, icon: 'WarningFilled', bg: 'var(--warning-bg)', color: 'var(--warning-color)' },
  { key: 'inport', label: '今日入库', value: todayInport.value, icon: 'Download', bg: 'var(--success-bg)', color: 'var(--success-color)' },
  { key: 'sales', label: '今日销售', value: todaySales.value, icon: 'ShoppingCart', bg: 'var(--info-bg)', color: 'var(--info-color)' }
])

onMounted(async () => {
  try {
    await fetchOps(1)
  } catch {}

  try {
    const noticeRes: any = await loadAllNotice({ page: 1, limit: 5 })
    if (noticeRes.data) {
      notices.value = noticeRes.data
    }
  } catch {}

  try {
    const goodsRes: any = await loadAllWarningGoods()
    if (goodsRes.data) {
      warningGoods.value = goodsRes.data
    }
  } catch {}

  try {
    const statsRes: any = await loadDashboardStats()
    if (statsRes) {
      goodsTotal.value = statsRes.goodsTotal || 0
      todayInport.value = statsRes.todayInport || 0
      todaySales.value = statsRes.todaySales || 0
    }
  } catch {}
})

async function fetchOps(page: number) {
  opsCurrentPage.value = page
  const params: any = { page, limit: opsPageSize }
  // 超级管理员(type=0)看全部，其他用户只看自己的操作记录
  if (authStore.user && authStore.user.type !== 0) {
    params.operateperson = authStore.user.name
  }
  const res: any = await loadAllOperationLog(params)
  if (res.data) {
    operations.value = res.data
    totalOps.value = res.count || 0
  }
}

function handleOpsPageChange(page: number) {
  fetchOps(page)
}

function handleNoticeClick(item: any) {
  currentNotice.value = item
  noticeDialogVisible.value = true
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* ─── Top: Greeting + Stats ─────────────── */
.dashboard-top {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.greeting-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.greeting-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.greeting-hello {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
}

.greeting-sub {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}

.stat-card {
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg) var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-number {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.1;
  font-variant-numeric: tabular-nums;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  font-weight: 500;
}

/* ─── Middle: Notice + Warning ──────────── */
.dashboard-mid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

/* ─── Panel (shared) ────────────────────── */
.panel {
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--border-lighter);
  flex-shrink: 0;
}

.panel-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.panel-icon {
  font-size: 18px;
  color: var(--primary-color);
}

.panel-icon.warning {
  color: var(--warning-color);
}

.panel-title {
  font-weight: 600;
  font-size: var(--font-size-base);
  color: var(--text-primary);
}

.panel-hint {
  font-size: var(--font-size-xs);
  color: var(--text-placeholder);
  font-weight: 400;
  margin-left: var(--spacing-xs);
}

.panel-body {
  padding: var(--spacing-sm) var(--spacing-lg);
  flex: 1;
  overflow: auto;
  max-height: 320px;
}

/* ─── Notice List ───────────────────────── */
.notice-list {
  display: flex;
  flex-direction: column;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-xs);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
}

.notice-item:hover {
  background: var(--primary-subtle);
}

.notice-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-color);
  opacity: 0.4;
  flex-shrink: 0;
  transition: opacity var(--transition-fast);
}

.notice-item:hover .notice-dot {
  opacity: 1;
}

.notice-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.notice-title {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color var(--transition-fast);
}

.notice-item:hover .notice-title {
  color: var(--primary-color);
}

.notice-meta {
  font-size: var(--font-size-xs);
  color: var(--text-placeholder);
}

.notice-arrow {
  color: var(--text-placeholder);
  opacity: 0;
  transform: translateX(-4px);
  transition: all var(--transition-fast);
  flex-shrink: 0;
  font-size: 14px;
}

.notice-item:hover .notice-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--primary-color);
}

/* ─── Warning Table ─────────────────────── */
.danger-num {
  color: var(--danger-color);
  font-weight: 600;
}

.warn-num {
  color: var(--text-secondary);
}

/* ─── Ops Panel ─────────────────────────── */
.ops-panel {
  /* full width, no grid constraint */
}

.ops-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.ops-pagination {
  display: flex;
  justify-content: center;
  padding-top: var(--spacing-sm);
}

/* ─── Empty State (compact) ─────────────── */
.empty-mini {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-2xl) 0;
  color: var(--text-placeholder);
  font-size: var(--font-size-sm);
}

.empty-mini.success {
  color: var(--success-color);
}

.empty-mini .el-icon {
  opacity: 0.4;
}

.empty-mini.success .el-icon {
  opacity: 0.6;
}

/* ─── Deep Overrides ────────────────────── */
:deep(.el-table__row:hover > td) {
  background-color: var(--primary-subtle) !important;
}

:deep(.el-card__header) {
  padding: var(--spacing-md) var(--spacing-lg);
}

:deep(.el-card__body) {
  padding: var(--spacing-sm) var(--spacing-lg);
}

/* ─── Animation ─────────────────────────── */
.animate-fade-in-up {
  animation: fadeInUp 0.4s ease both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
