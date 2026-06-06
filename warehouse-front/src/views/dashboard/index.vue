<template>
  <div class="dashboard">
    <div class="welcome-banner animate-fade-in-up">
      <div class="banner-grid"></div>
      <div class="banner-mesh"></div>
      <div class="welcome-content">
        <p class="welcome-greeting">{{ greeting }}</p>
        <h2 class="welcome-title">{{ authStore.user?.name || '管理员' }}</h2>
        <p class="welcome-desc">仓储脉搏 · 实时数据概览</p>
      </div>
      <div class="welcome-decoration">
        <div class="deco-ring deco-1"></div>
        <div class="deco-ring deco-2"></div>
        <div class="deco-block deco-3"></div>
        <div class="deco-block deco-4"></div>
      </div>
    </div>

    <div class="stats-row animate-fade-in-up delay-100">
      <div class="stat-card stat-total">
        <div class="stat-icon-wrap stat-icon-total">
          <el-icon class="stat-icon"><Box /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ goodsTotal }}</span>
          <span class="stat-label">商品总数</span>
        </div>
        <div class="stat-bar stat-bar-total"></div>
      </div>
      <div class="stat-card stat-warning">
        <div class="stat-icon-wrap stat-icon-warning">
          <el-icon class="stat-icon"><WarningFilled /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ warningGoods.length }}</span>
          <span class="stat-label">低库存预警</span>
        </div>
        <div class="stat-bar stat-bar-warning"></div>
      </div>
      <div class="stat-card stat-inport">
        <div class="stat-icon-wrap stat-icon-inport">
          <el-icon class="stat-icon"><Download /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ todayInport }}</span>
          <span class="stat-label">今日入库</span>
        </div>
        <div class="stat-bar stat-bar-inport"></div>
      </div>
      <div class="stat-card stat-sales">
        <div class="stat-icon-wrap stat-icon-sales">
          <el-icon class="stat-icon"><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ todaySales }}</span>
          <span class="stat-label">今日销售</span>
        </div>
        <div class="stat-bar stat-bar-sales"></div>
      </div>
    </div>

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-card class="dashboard-card notice-card animate-fade-in-up delay-150" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-header-left">
                <div class="header-icon-wrap notice-icon-wrap">
                  <el-icon class="header-icon"><Bell /></el-icon>
                </div>
                <div class="header-text-group">
                  <span class="header-title">公告通知</span>
                  <span class="header-subtitle">系统公告与通知信息</span>
                </div>
              </div>
              <el-tag v-if="notices.length > 0" type="primary" size="small" class="count-tag" effect="plain" round>
                {{ notices.length }} 条
              </el-tag>
            </div>
          </template>
          <div v-if="notices.length === 0" class="empty-state">
            <div class="empty-icon-wrap">
              <el-icon class="empty-icon"><Bell /></el-icon>
            </div>
            <span class="empty-text">暂无公告</span>
          </div>
          <div v-else class="notice-list">
            <div v-for="(item, index) in notices" :key="item.id" class="notice-item" :style="{ animationDelay: (index * 0.08) + 's' }" @click="handleNoticeClick(item)">
              <div class="notice-dot"></div>
              <div class="notice-content">
                <div class="notice-title">{{ item.title }}</div>
                <div class="notice-time">
                  <el-icon><Clock /></el-icon>
                  {{ item.createtime }} · {{ item.opername }}
                </div>
              </div>
              <el-icon class="notice-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="dashboard-card ops-card animate-fade-in-up delay-200" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-header-left">
                <div class="header-icon-wrap ops-icon-wrap">
                  <el-icon class="header-icon"><List /></el-icon>
                </div>
                <div class="header-text-group">
                  <span class="header-title">最近操作记录</span>
                  <span class="header-subtitle">进货、销售、退货等动态</span>
                </div>
              </div>
              <el-tag v-if="totalOps > 0" type="info" size="small" class="count-tag" effect="plain" round>
                {{ totalOps }} 条
              </el-tag>
            </div>
          </template>
          <div v-if="operations.length === 0" class="empty-state">
            <div class="empty-icon-wrap">
              <el-icon class="empty-icon"><DocumentRemove /></el-icon>
            </div>
            <span class="empty-text">暂无操作记录</span>
            <span class="empty-sub-text">还没有任何业务操作</span>
          </div>
          <template v-else>
            <el-table
              :data="operations"
              size="small"
              stripe
              class="ops-table"
              :header-cell-style="{ background: 'var(--bg-tertiary)', color: 'var(--text-primary)', fontWeight: '600' }"
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
              <el-table-column prop="operatetime" label="时间" min-width="140">
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
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="dashboard-card warning-card animate-fade-in-up delay-300" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="card-header-left">
                <div class="header-icon-wrap warning-icon-wrap">
                  <el-icon class="header-icon"><Warning /></el-icon>
                </div>
                <div class="header-text-group">
                  <span class="header-title">库存预警商品</span>
                  <span class="header-subtitle">低于安全库存的商品</span>
                </div>
              </div>
              <el-tag v-if="warningGoods.length > 0" type="danger" size="small" class="count-tag" effect="dark" round>
                {{ warningGoods.length }} 项
              </el-tag>
            </div>
          </template>
          <div v-if="warningGoods.length === 0" class="empty-state">
            <div class="empty-icon-wrap empty-icon-success">
              <el-icon class="empty-icon"><CircleCheck /></el-icon>
            </div>
            <span class="empty-text">暂无预警</span>
            <span class="empty-sub-text">所有商品库存均处于安全水平</span>
          </div>
          <el-table
            v-else
            :data="warningGoods"
            size="small"
            stripe
            class="warning-table"
            :header-cell-style="{ background: 'var(--bg-tertiary)', color: 'var(--text-primary)', fontWeight: '600' }"
          >
            <el-table-column prop="goodsname" label="商品名称" min-width="100">
              <template #default="{ row }">
                <div class="goods-name">
                  <el-icon class="goods-icon"><Goods /></el-icon>
                  {{ row.goodsname }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="providername" label="供应商" min-width="100" show-overflow-tooltip />
            <el-table-column prop="number" label="当前库存" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.number <= row.dangernum ? 'danger' : 'success'" size="small" effect="dark" round>
                  {{ row.number }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="dangernum" label="预警值" width="80" align="center">
              <template #default="{ row }">
                <span class="danger-num">{{ row.dangernum }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

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
const opsPageSize = 5
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
  const res: any = await loadAllOperationLog({ page, limit: opsPageSize })
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
  padding: var(--spacing-xs);
}

/* ─── Welcome Banner ─────────────────────── */
.welcome-banner {
  background: var(--bg-primary);
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-2xl) var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  border: 1px solid var(--border-light);
}

.banner-grid,
.banner-mesh,
.welcome-decoration { display: none; }

.welcome-content {
  position: relative;
  z-index: 2;
}

.welcome-greeting {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0 0 var(--spacing-xs);
  letter-spacing: 1px;
  font-weight: 500;
}

.welcome-title {
  font-size: var(--font-size-4xl);
  font-weight: 700;
  margin: 0 0 var(--spacing-sm);
  color: var(--text-primary);
  letter-spacing: var(--letter-spacing-tight);
  line-height: var(--line-height-tight);
}

.welcome-desc {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

/* ─── Stat Cards ─────────────────────────── */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.stat-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0.5;
}

.stat-bar-total { background: var(--primary-gradient); }
.stat-bar-warning { background: linear-gradient(135deg, #d97706, #dc2626); }
.stat-bar-inport { background: linear-gradient(135deg, #059669, #10b981); }
.stat-bar-sales { background: linear-gradient(135deg, #0284c7, #0ea5e9); }

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

.stat-icon-total {
  background: var(--accent-bg);
  color: var(--primary-color);
}

.stat-icon-warning {
  background: var(--warning-bg);
  color: var(--warning-color);
}

.stat-icon-inport {
  background: var(--success-bg);
  color: var(--success-color);
}

.stat-icon-sales {
  background: var(--info-bg);
  color: var(--info-color);
}

.stat-icon {
  font-size: 22px;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-number {
  font-size: var(--font-size-3xl);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.1;
  letter-spacing: var(--letter-spacing-tight);
  font-variant-numeric: tabular-nums;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  font-weight: 500;
}

/* ─── Dashboard Cards ────────────────────── */
.dashboard-card {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);
}

.dashboard-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.header-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notice-icon-wrap,
.ops-icon-wrap {
  background: var(--accent-bg);
  color: var(--primary-color);
}

.warning-icon-wrap {
  background: var(--warning-bg);
  color: var(--warning-color);
}

.header-icon {
  font-size: 18px;
}

.header-text-group {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.header-title {
  font-weight: 600;
  font-size: var(--font-size-base);
  color: var(--text-primary);
  line-height: 1.3;
}

.header-subtitle {
  font-size: var(--font-size-xs);
  color: var(--text-placeholder);
  font-weight: 400;
}

.count-tag {
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* ─── Notice List ────────────────────────── */
.notice-list {
  display: flex;
  flex-direction: column;
}

.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-lighter);
  transition: all var(--transition-fast);
  cursor: pointer;
  position: relative;
  border-radius: var(--border-radius-sm);
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background: var(--primary-subtle);
}

.notice-dot {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--primary-color);
  opacity: 0.4;
  transition: all var(--transition-fast);
}

.notice-item:hover .notice-dot {
  opacity: 1;
}

.notice-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  padding-left: var(--spacing-md);
}

.notice-title {
  font-size: var(--font-size-base);
  color: var(--text-primary);
  font-weight: 500;
  transition: color var(--transition-fast);
}

.notice-item:hover .notice-title {
  color: var(--primary-color);
}

.notice-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-arrow {
  color: var(--text-placeholder);
  transition: all var(--transition-fast);
  opacity: 0;
  transform: translateX(-4px);
  flex-shrink: 0;
}

.notice-item:hover .notice-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--primary-color);
}

/* ─── Tables ─────────────────────────────── */
.ops-table,
.warning-table {
  border-radius: var(--border-radius-md);
  overflow: hidden;
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

.goods-name {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.goods-icon {
  color: var(--primary-color);
  font-size: 16px;
}

.danger-num {
  color: var(--danger-color);
  font-weight: 600;
}

/* ─── Empty State ────────────────────────── */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  gap: var(--spacing-sm);
}

.empty-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-subtle);
  margin-bottom: var(--spacing-xs);
}

.empty-icon-wrap.empty-icon-success {
  background: var(--success-bg);
}

.empty-icon {
  font-size: var(--font-size-4xl);
  color: var(--text-placeholder);
  opacity: 0.5;
}

.empty-icon-wrap.empty-icon-success .empty-icon {
  color: var(--success-color);
  opacity: 0.7;
}

.empty-text {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  font-weight: 500;
}

.empty-sub-text {
  font-size: var(--font-size-xs);
  color: var(--text-placeholder);
}

/* ─── Deep Overrides ─────────────────────── */
:deep(.el-table__row) {
  transition: all var(--transition-fast);
}

:deep(.el-table__row:hover > td) {
  background-color: var(--primary-subtle) !important;
}

:deep(.el-card__header) {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  padding: var(--spacing-md) var(--spacing-xl);
}

:deep(.el-card__body) {
  padding: var(--spacing-md) var(--spacing-xl);
}
</style>
