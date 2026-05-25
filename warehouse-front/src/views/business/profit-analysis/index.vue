<template>
  <div>
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">利润分析</h1>
      <p class="page-header-desc">按时间段统计销售额、进货成本与毛利润</p>
    </div>

    <el-card class="chart-card animate-fade-in-up delay-100">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <div class="header-icon-wrap profit-icon-wrap">
              <el-icon class="header-icon"><TrendCharts /></el-icon>
            </div>
            <div class="header-text-group">
              <span class="header-title">利润趋势</span>
              <span class="header-subtitle">销售额 - 进货成本 = 毛利润</span>
            </div>
          </div>
          <div class="card-header-right">
            <el-radio-group v-model="queryType" size="small" @change="onTypeChange">
              <el-radio-button value="today">今天</el-radio-button>
              <el-radio-button value="yesterday">昨天</el-radio-button>
              <el-radio-button value="week">本周</el-radio-button>
              <el-radio-button value="month">本月</el-radio-button>
              <el-radio-button value="quarter">本季</el-radio-button>
              <el-radio-button value="year">本年</el-radio-button>
              <el-radio-button value="custom">自定义</el-radio-button>
            </el-radio-group>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="small"
              value-format="YYYY-MM-DD"
              :disabled="queryType !== 'custom'"
              @change="loadData"
              style="margin-left: 8px;"
            />
          </div>
        </div>
      </template>

      <!-- 汇总卡片 -->
      <div class="summary-row">
        <div class="summary-item">
          <span class="summary-label">总销售额</span>
          <span class="summary-value sales">¥{{ formatMoney(totalSales) }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">总进货成本</span>
          <span class="summary-value inport">¥{{ formatMoney(totalInport) }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">总毛利润</span>
          <span class="summary-value" :class="totalProfit >= 0 ? 'profit' : 'loss'">¥{{ formatMoney(totalProfit) }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">毛利率</span>
          <span class="summary-value" :class="totalProfit >= 0 ? 'profit' : 'loss'">{{ profitRate }}%</span>
        </div>
      </div>

      <div ref="chartRef" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { profitAnalysis } from '@/api/report'
import { getDateRange } from '@/utils/dateRange'

const queryType = ref('month')
const dateRange = ref<string[]>([])
const chartRef = ref<HTMLElement>()
const loading = ref(false)

const salesData = ref<number[]>([])
const inportData = ref<number[]>([])
const profitData = ref<number[]>([])

let chart: echarts.ECharts | null = null

const totalSales = computed(() => salesData.value.reduce((a, b) => a + b, 0))
const totalInport = computed(() => inportData.value.reduce((a, b) => a + b, 0))
const totalProfit = computed(() => totalSales.value - totalInport.value)
const profitRate = computed(() => {
  if (totalSales.value === 0) return '0.00'
  return ((totalProfit.value / totalSales.value) * 100).toFixed(2)
})

const formatMoney = (val: number) => val.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')

const initChart = () => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
  }
}

const buildOption = (labels: string[], sales: number[], inport: number[], profit: number[]) => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'cross' },
    formatter: (params: any) => {
      let html = `<b>${params[0].name}</b><br/>`
      params.forEach((p: any) => {
        const val = Number(p.value).toLocaleString()
        html += `${p.marker} ${p.seriesName}：<b>¥${val}</b><br/>`
      })
      return html
    }
  },
  legend: { data: ['销售额', '进货成本', '毛利润'], top: 0 },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true, top: 40 },
  xAxis: {
    type: 'category',
    data: labels,
    axisLabel: { rotate: labels.length > 15 ? 45 : 0 }
  },
  yAxis: {
    type: 'value',
    axisLabel: { formatter: (v: number) => v >= 10000 ? (v / 10000) + '万' : v + '' }
  },
  series: [
    {
      name: '销售额',
      type: 'bar',
      barMaxWidth: 30,
      data: sales,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#67C23A' },
          { offset: 1, color: '#67C23A66' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    },
    {
      name: '进货成本',
      type: 'bar',
      barMaxWidth: 30,
      data: inport,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#E6A23C' },
          { offset: 1, color: '#E6A23C66' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    },
    {
      name: '毛利润',
      type: 'line',
      smooth: true,
      data: profit,
      lineStyle: { width: 3, color: '#409EFF' },
      itemStyle: { color: '#409EFF' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.25)' },
          { offset: 1, color: 'rgba(64,158,255,0.02)' }
        ])
      },
      markLine: {
        silent: true,
        data: [{ yAxis: 0 }],
        lineStyle: { color: '#909399', type: 'dashed' },
        label: { show: false }
      }
    }
  ]
})

const onTypeChange = (val: string) => {
  if (val !== 'custom') {
    const range = getDateRange(val)
    dateRange.value = [range.startDate, range.endDate]
    loadData()
  }
}

const loadData = async () => {
  if (!dateRange.value || dateRange.value.length < 2) return
  loading.value = true
  if (chart) chart.showLoading()
  try {
    const res: any = await profitAnalysis(queryType.value, dateRange.value[0], dateRange.value[1])
    const labels = res.labels || []
    inportData.value = res.inportValues || []
    salesData.value = res.salesValues || []
    profitData.value = res.profitValues || []
    chart?.setOption(buildOption(labels, salesData.value, inportData.value, profitData.value))
  } catch {
    inportData.value = []
    salesData.value = []
    profitData.value = []
    chart?.setOption(buildOption([], [], [], []))
  } finally {
    loading.value = false
    if (chart) chart.hideLoading()
  }
}

const handleResize = () => chart?.resize()

onMounted(async () => {
  await nextTick()
  initChart()
  const range = getDateRange(queryType.value)
  dateRange.value = [range.startDate, range.endDate]
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}
.page-header-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}
.page-header-desc {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}
.chart-card {
  margin-bottom: 20px;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.card-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.card-header-right {
  display: flex;
  align-items: center;
}
.header-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.profit-icon-wrap {
  background: linear-gradient(135deg, #409EFF, #79bbff);
  color: #fff;
}
.header-icon {
  font-size: 18px;
}
.header-text-group {
  display: flex;
  flex-direction: column;
}
.header-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}
.header-subtitle {
  font-size: 12px;
  color: #94a3b8;
}
.summary-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.summary-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 20px;
  background: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  min-width: 140px;
}
.summary-label {
  font-size: 12px;
  color: var(--text-secondary);
}
.summary-value {
  font-size: 20px;
  font-weight: 700;
}
.summary-value.sales {
  color: #67C23A;
}
.summary-value.inport {
  color: #E6A23C;
}
.summary-value.profit {
  color: #409EFF;
}
.summary-value.loss {
  color: #F56C6C;
}
.chart-container {
  width: 100%;
  height: 400px;
}
</style>
