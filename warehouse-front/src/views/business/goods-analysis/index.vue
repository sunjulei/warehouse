<template>
  <div class="analysis-page">
    <!-- Page Header -->
    <div class="page-header animate-fade-in-up">
      <div class="page-header-content">
        <h1 class="page-title">进销商品分析</h1>
        <p class="page-subtitle">按时间段统计进货与销售的商品明细</p>
      </div>
      <div class="page-header-decoration">
        <div class="decoration-dot dot-1"></div>
        <div class="decoration-dot dot-2"></div>
        <div class="decoration-dot dot-3"></div>
      </div>
    </div>

    <!-- Custom Tab Switcher -->
    <div class="tab-switcher animate-fade-in-up animate-delay-1">
      <div class="tab-track">
        <div class="tab-indicator" :class="activeTab"></div>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'inport' }"
          @click="switchTab('inport')"
        >
          <el-icon class="tab-btn-icon"><Download /></el-icon>
          <span>进货商品</span>
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'sales' }"
          @click="switchTab('sales')"
        >
          <el-icon class="tab-btn-icon"><ShoppingCart /></el-icon>
          <span>销售商品</span>
        </button>
      </div>
    </div>

    <!-- Inport Tab Content -->
    <div v-show="activeTab === 'inport'" class="tab-content">
      <div class="stats-row animate-fade-in-up">
        <div class="stat-card stat-inport">
          <div class="stat-icon-wrap">
            <el-icon><Download /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">总进货金额</span>
            <span class="stat-value">¥{{ formatMoney(inportTotalAmount) }}</span>
          </div>
        </div>
        <div class="stat-card stat-count">
          <div class="stat-icon-wrap">
            <el-icon><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">商品种类</span>
            <span class="stat-value">{{ inportAllData.length }}</span>
          </div>
        </div>
        <div class="stat-card stat-qty">
          <div class="stat-icon-wrap">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">总进货数量</span>
            <span class="stat-value">{{ inportTotalQty }}</span>
          </div>
        </div>
      </div>

      <div class="data-panel animate-fade-in-up animate-delay-2">
        <div class="panel-toolbar">
          <el-radio-group v-model="inportType" size="small" @change="onInportTypeChange">
            <el-radio-button value="today">今天</el-radio-button>
            <el-radio-button value="yesterday">昨天</el-radio-button>
            <el-radio-button value="week">本周</el-radio-button>
            <el-radio-button value="month">本月</el-radio-button>
            <el-radio-button value="quarter">本季</el-radio-button>
            <el-radio-button value="year">本年</el-radio-button>
            <el-radio-button value="custom">自定义</el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-model="inportDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="YYYY-MM-DD"
            :disabled="inportType !== 'custom'"
            @change="loadInportData"
            style="margin-left: 12px;"
          />
        </div>
        <div ref="inportChartRef" class="chart-container"></div>
        <div class="table-section">
          <div class="table-section-header">
            <span class="table-section-title">商品明细</span>
            <span class="table-section-count">共 {{ inportAllData.length }} 项</span>
          </div>
          <el-table :data="inportPageData" stripe class="goods-table" v-loading="inportLoading">
            <el-table-column type="index" label="#" width="50" align="center" />
            <el-table-column prop="goodsname" label="商品名称" min-width="180">
              <template #default="{ row }">
                <span class="goods-name">{{ row.goodsname || '未知' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalNumber" label="数量" width="100" align="center">
              <template #default="{ row }">
                <span class="qty-badge">{{ row.totalNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="120" align="right">
              <template #default="{ row }">
                <span class="price-text">¥{{ formatPrice(row.price) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="总金额" width="140" align="right">
              <template #default="{ row }">
                <span class="amount-text">¥{{ formatPrice(row.totalAmount) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="table-pagination"
            v-model:current-page="inportPage"
            v-model:page-size="inportPageSize"
            :page-sizes="[10, 20, 50]"
            :total="inportAllData.length"
            layout="total, sizes, prev, pager, next"
            @size-change="inportPage = 1"
          />
        </div>
      </div>
    </div>

    <!-- Sales Tab Content -->
    <div v-show="activeTab === 'sales'" class="tab-content">
      <div class="stats-row animate-fade-in-up">
        <div class="stat-card stat-sales">
          <div class="stat-icon-wrap">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">总销售金额</span>
            <span class="stat-value">¥{{ formatMoney(salesTotalAmount) }}</span>
          </div>
        </div>
        <div class="stat-card stat-count">
          <div class="stat-icon-wrap">
            <el-icon><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">商品种类</span>
            <span class="stat-value">{{ salesAllData.length }}</span>
          </div>
        </div>
        <div class="stat-card stat-qty">
          <div class="stat-icon-wrap">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">总销售数量</span>
            <span class="stat-value">{{ salesTotalQty }}</span>
          </div>
        </div>
      </div>

      <div class="data-panel animate-fade-in-up animate-delay-2">
        <div class="panel-toolbar">
          <el-radio-group v-model="salesType" size="small" @change="onSalesTypeChange">
            <el-radio-button value="today">今天</el-radio-button>
            <el-radio-button value="yesterday">昨天</el-radio-button>
            <el-radio-button value="week">本周</el-radio-button>
            <el-radio-button value="month">本月</el-radio-button>
            <el-radio-button value="quarter">本季</el-radio-button>
            <el-radio-button value="year">本年</el-radio-button>
            <el-radio-button value="custom">自定义</el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-model="salesDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="YYYY-MM-DD"
            :disabled="salesType !== 'custom'"
            @change="loadSalesData"
            style="margin-left: 12px;"
          />
        </div>
        <div ref="salesChartRef" class="chart-container"></div>
        <div class="table-section">
          <div class="table-section-header">
            <span class="table-section-title">商品明细</span>
            <span class="table-section-count">共 {{ salesAllData.length }} 项</span>
          </div>
          <el-table :data="salesPageData" stripe class="goods-table" v-loading="salesLoading">
            <el-table-column type="index" label="#" width="50" align="center" />
            <el-table-column prop="goodsname" label="商品名称" min-width="180">
              <template #default="{ row }">
                <span class="goods-name">{{ row.goodsname || '未知' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalNumber" label="数量" width="100" align="center">
              <template #default="{ row }">
                <span class="qty-badge">{{ row.totalNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="120" align="right">
              <template #default="{ row }">
                <span class="price-text">¥{{ formatPrice(row.price) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="总金额" width="140" align="right">
              <template #default="{ row }">
                <span class="amount-text">¥{{ formatPrice(row.totalAmount) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="table-pagination"
            v-model:current-page="salesPage"
            v-model:page-size="salesPageSize"
            :page-sizes="[10, 20, 50]"
            :total="salesAllData.length"
            layout="total, sizes, prev, pager, next"
            @size-change="salesPage = 1"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { inportGoodsAnalysis, salesGoodsAnalysis } from '@/api/report'
import { getDateRange } from '@/utils/dateRange'

const activeTab = ref<'inport' | 'sales'>('inport')
const inportType = ref('month')
const salesType = ref('month')
const inportDateRange = ref<string[]>([])
const salesDateRange = ref<string[]>([])
const inportChartRef = ref<HTMLElement>()
const salesChartRef = ref<HTMLElement>()
const inportAllData = ref<any[]>([])
const salesAllData = ref<any[]>([])
const inportLoading = ref(false)
const salesLoading = ref(false)

const inportPage = ref(1)
const inportPageSize = ref(10)
const salesPage = ref(1)
const salesPageSize = ref(10)

const inportPageData = computed(() => {
  const start = (inportPage.value - 1) * inportPageSize.value
  return inportAllData.value.slice(start, start + inportPageSize.value)
})

const salesPageData = computed(() => {
  const start = (salesPage.value - 1) * salesPageSize.value
  return salesAllData.value.slice(start, start + salesPageSize.value)
})

// Computed stats
const inportTotalAmount = computed(() => inportAllData.value.reduce((a, b) => a + (Number(b.totalAmount) || 0), 0))
const salesTotalAmount = computed(() => salesAllData.value.reduce((a, b) => a + (Number(b.totalAmount) || 0), 0))
const inportTotalQty = computed(() => inportAllData.value.reduce((a, b) => a + (Number(b.totalNumber) || 0), 0))
const salesTotalQty = computed(() => salesAllData.value.reduce((a, b) => a + (Number(b.totalNumber) || 0), 0))

let inportChart: echarts.ECharts | null = null
let salesChart: echarts.ECharts | null = null

const inportColor = { main: '#d97706', light: '#fbbf24' }
const salesColor = { main: '#059669', light: '#34d399' }

const formatPrice = (val: any) => {
  const num = Number(val)
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

const formatMoney = (val: number) => {
  if (val >= 10000) return (val / 10000).toFixed(2) + ' 万'
  return val.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const switchTab = (tab: 'inport' | 'sales') => {
  activeTab.value = tab
  nextTick(() => {
    inportChart?.resize()
    salesChart?.resize()
  })
}

const buildChartOption = (title: string, names: string[], amounts: number[], color: typeof inportColor) => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
    backgroundColor: 'rgba(28, 25, 23, 0.9)',
    borderColor: 'transparent',
    textStyle: { color: '#fafaf9', fontSize: 13 },
    formatter: (params: any) => {
      const p = params[0]
      return `<div style="font-weight:600;margin-bottom:4px">${p.name}</div>
              <div style="display:flex;align-items:center;gap:6px">
                <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${color.main}"></span>
                ${title}：<b style="font-variant-numeric:tabular-nums">¥${Number(p.value).toLocaleString()}</b>
              </div>`
    }
  },
  grid: { left: '3%', right: '4%', bottom: '8%', top: '8%', containLabel: true },
  xAxis: {
    type: 'category',
    data: names,
    axisLine: { lineStyle: { color: '#e8e5e0' } },
    axisTick: { show: false },
    axisLabel: {
      color: '#78716c',
      fontSize: 12,
      rotate: names.length > 6 ? 25 : 0,
      margin: 12
    }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#f5f4f2', type: 'dashed' } },
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: {
      color: '#a8a29e',
      fontSize: 12,
      formatter: (v: number) => v >= 10000 ? (v / 10000) + '万' : v + ''
    }
  },
  series: [{
    name: title,
    type: 'bar',
    barMaxWidth: 36,
    data: amounts,
    itemStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: color.main },
        { offset: 1, color: color.light + '88' }
      ]),
      borderRadius: [6, 6, 0, 0]
    },
    emphasis: {
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: color.main },
          { offset: 1, color: color.light }
        ])
      }
    },
    animationDelay: (idx: number) => idx * 50
  }],
  animationEasing: 'cubicOut' as const,
  animationDuration: 800
})

const onInportTypeChange = (val: string) => {
  if (val !== 'custom') {
    const range = getDateRange(val)
    inportDateRange.value = [range.startDate, range.endDate]
    loadInportData()
  }
}

const onSalesTypeChange = (val: string) => {
  if (val !== 'custom') {
    const range = getDateRange(val)
    salesDateRange.value = [range.startDate, range.endDate]
    loadSalesData()
  }
}

const loadInportData = async () => {
  if (!inportDateRange.value || inportDateRange.value.length < 2) return
  inportLoading.value = true
  if (inportChart) inportChart.showLoading({
    text: '',
    color: inportColor.main,
    maskColor: 'rgba(255, 255, 255, 0.8)'
  })
  try {
    const res: any = await inportGoodsAnalysis(inportType.value, inportDateRange.value[0], inportDateRange.value[1])
    const data = res.data || []
    inportAllData.value = data
    inportPage.value = 1
    const top10 = data.slice(0, 10)
    inportChart?.setOption(buildChartOption(
      '进货金额',
      top10.map((d: any) => d.goodsname || '未知'),
      top10.map((d: any) => Number(d.totalAmount) || 0),
      inportColor
    ))
  } catch {
    inportAllData.value = []
    inportChart?.setOption(buildChartOption('进货金额', [], [], inportColor))
  } finally {
    inportLoading.value = false
    if (inportChart) inportChart.hideLoading()
  }
}

const loadSalesData = async () => {
  if (!salesDateRange.value || salesDateRange.value.length < 2) return
  salesLoading.value = true
  if (salesChart) salesChart.showLoading({
    text: '',
    color: salesColor.main,
    maskColor: 'rgba(255, 255, 255, 0.8)'
  })
  try {
    const res: any = await salesGoodsAnalysis(salesType.value, salesDateRange.value[0], salesDateRange.value[1])
    const data = res.data || []
    salesAllData.value = data
    salesPage.value = 1
    const top10 = data.slice(0, 10)
    salesChart?.setOption(buildChartOption(
      '销售金额',
      top10.map((d: any) => d.goodsname || '未知'),
      top10.map((d: any) => Number(d.totalAmount) || 0),
      salesColor
    ))
  } catch {
    salesAllData.value = []
    salesChart?.setOption(buildChartOption('销售金额', [], [], salesColor))
  } finally {
    salesLoading.value = false
    if (salesChart) salesChart.hideLoading()
  }
}

const handleResize = () => {
  inportChart?.resize()
  salesChart?.resize()
}

onMounted(async () => {
  await nextTick()
  if (inportChartRef.value) {
    inportChart = echarts.init(inportChartRef.value)
  }
  if (salesChartRef.value) {
    salesChart = echarts.init(salesChartRef.value)
  }
  const inportRange = getDateRange(inportType.value)
  inportDateRange.value = [inportRange.startDate, inportRange.endDate]
  const salesRange = getDateRange(salesType.value)
  salesDateRange.value = [salesRange.startDate, salesRange.endDate]
  loadInportData()
  loadSalesData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  inportChart?.dispose()
  salesChart?.dispose()
})
</script>

<style scoped>
.analysis-page {
  padding: 0;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-xl);
}
.page-header-content {
  flex: 1;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  letter-spacing: -0.02em;
  font-family: var(--font-family);
}
.page-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 400;
}
.page-header-decoration {
  display: flex;
  gap: 6px;
  align-items: center;
  padding-top: 6px;
}
.decoration-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  opacity: 0.6;
}
.dot-1 { background: var(--primary-color); }
.dot-2 { background: var(--accent-color); }
.dot-3 { background: var(--success-color); }

/* Custom Tab Switcher */
.tab-switcher {
  margin-bottom: var(--spacing-xl);
}
.tab-track {
  display: inline-flex;
  position: relative;
  background: var(--bg-tertiary);
  border-radius: var(--border-radius-xl);
  padding: 4px;
  gap: 4px;
}
.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  height: calc(100% - 8px);
  width: calc(50% - 4px);
  border-radius: var(--border-radius-lg);
  background: var(--bg-primary);
  box-shadow: var(--shadow-md);
  transition: transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 0;
}
.tab-indicator.sales {
  transform: translateX(100%);
}
.tab-btn {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  border: none;
  background: transparent;
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  transition: color var(--transition-base);
  white-space: nowrap;
  font-family: inherit;
}
.tab-btn-icon {
  font-size: 16px;
}
.tab-btn.active {
  color: var(--text-primary);
  font-weight: 600;
}
.tab-btn:hover:not(.active) {
  color: var(--text-regular);
}

/* Stats Row */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  transition: all var(--transition-base);
}
.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}
.stat-icon-wrap {
  width: 42px;
  height: 42px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.stat-inport .stat-icon-wrap {
  background: rgba(217, 119, 6, 0.1);
  color: #d97706;
}
.stat-sales .stat-icon-wrap {
  background: rgba(5, 150, 105, 0.1);
  color: #059669;
}
.stat-count .stat-icon-wrap {
  background: rgba(var(--primary-rgb), 0.08);
  color: var(--primary-color);
}
.stat-qty .stat-icon-wrap {
  background: rgba(2, 132, 199, 0.1);
  color: #0284c7;
}
.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 400;
}
.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

/* Data Panel */
.data-panel {
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  padding: var(--spacing-xl);
  transition: box-shadow var(--transition-base);
}
.data-panel:hover {
  box-shadow: var(--shadow-md);
}
.panel-toolbar {
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  flex-wrap: wrap;
  gap: 8px;
}
.chart-container {
  width: 100%;
  height: 340px;
  margin-bottom: var(--spacing-xl);
}

/* Table Section */
.table-section {
  border-top: 1px solid var(--border-lighter);
  padding-top: var(--spacing-lg);
}
.table-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}
.table-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
.table-section-count {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  padding: 2px 10px;
  border-radius: var(--border-radius-full);
}
.goods-table {
  border-radius: var(--border-radius-md);
  overflow: hidden;
}
.goods-name {
  font-weight: 500;
  color: var(--text-regular);
}
.qty-badge {
  display: inline-block;
  padding: 1px 8px;
  background: var(--bg-tertiary);
  border-radius: var(--border-radius-xs);
  font-size: 13px;
  font-weight: 500;
  color: var(--text-regular);
  font-variant-numeric: tabular-nums;
}
.price-text {
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}
.amount-text {
  font-weight: 600;
  color: var(--accent-dark);
  font-variant-numeric: tabular-nums;
}
.table-pagination {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-lighter);
}

/* Stagger Animation Delays */
.animate-delay-1 { animation-delay: 0.06s; }
.animate-delay-2 { animation-delay: 0.12s; }

/* Responsive */
@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  .panel-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
  .tab-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
}
</style>
