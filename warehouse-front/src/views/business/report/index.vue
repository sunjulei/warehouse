<template>
  <div>
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">报表管理</h1>
      <p class="page-header-desc">进货与销售数据分析</p>
    </div>

    <el-card class="chart-card animate-fade-in-up delay-100">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <div class="header-icon-wrap inport-icon-wrap">
              <el-icon class="header-icon"><Download /></el-icon>
            </div>
            <div class="header-text-group">
              <span class="header-title">进销金额分析</span>
              <span class="header-subtitle">按时间段统计进货金额</span>
            </div>
          </div>
          <div class="card-header-right">
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
              style="margin-left: 8px;"
            />
          </div>
        </div>
      </template>
      <div ref="inportChartRef" class="chart-container"></div>
    </el-card>

    <el-card class="chart-card animate-fade-in-up delay-200">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <div class="header-icon-wrap sales-icon-wrap">
              <el-icon class="header-icon"><ShoppingCart /></el-icon>
            </div>
            <div class="header-text-group">
              <span class="header-title">销售分析</span>
              <span class="header-subtitle">按时间段统计销售金额</span>
            </div>
          </div>
          <div class="card-header-right">
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
              style="margin-left: 8px;"
            />
          </div>
        </div>
      </template>
      <div ref="salesChartRef" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { inportAnalysis, salesAnalysis } from '@/api/report'
import { getDateRange } from '@/utils/dateRange'

const inportType = ref('month')
const salesType = ref('month')
const inportDateRange = ref<string[]>([])
const salesDateRange = ref<string[]>([])
const inportChartRef = ref<HTMLElement>()
const salesChartRef = ref<HTMLElement>()

let inportChart: echarts.ECharts | null = null
let salesChart: echarts.ECharts | null = null

const initCharts = () => {
  if (inportChartRef.value) {
    inportChart = echarts.init(inportChartRef.value)
  }
  if (salesChartRef.value) {
    salesChart = echarts.init(salesChartRef.value)
  }
}

const buildBarOption = (title: string, labels: string[], values: number[], color: string) => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
    formatter: (params: any) => {
      const p = params[0]
      return `${p.name}<br/>金额：<b>¥${p.value.toLocaleString()}</b>`
    }
  },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: labels,
    axisLabel: { rotate: labels.length > 15 ? 45 : 0 }
  },
  yAxis: {
    type: 'value',
    axisLabel: { formatter: (v: number) => v >= 10000 ? (v / 10000) + '万' : v + '' }
  },
  series: [{
    name: title,
    type: 'bar',
    barMaxWidth: 40,
    data: values,
    itemStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color },
        { offset: 1, color: color + '66' }
      ]),
      borderRadius: [4, 4, 0, 0]
    }
  }]
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
  if (!inportChart || !inportDateRange.value || inportDateRange.value.length < 2) return
  inportChart.showLoading()
  try {
    const res: any = await inportAnalysis(inportType.value, inportDateRange.value[0], inportDateRange.value[1])
    inportChart.setOption(buildBarOption('进货金额', res.labels || [], res.values || [], '#409EFF'))
  } catch {
    inportChart.setOption(buildBarOption('进货金额', [], [], '#409EFF'))
  } finally {
    inportChart.hideLoading()
  }
}

const loadSalesData = async () => {
  if (!salesChart || !salesDateRange.value || salesDateRange.value.length < 2) return
  salesChart.showLoading()
  try {
    const res: any = await salesAnalysis(salesType.value, salesDateRange.value[0], salesDateRange.value[1])
    salesChart.setOption(buildBarOption('销售金额', res.labels || [], res.values || [], '#67C23A'))
  } catch {
    salesChart.setOption(buildBarOption('销售金额', [], [], '#67C23A'))
  } finally {
    salesChart.hideLoading()
  }
}

const handleResize = () => {
  inportChart?.resize()
  salesChart?.resize()
}

onMounted(async () => {
  await nextTick()
  initCharts()
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
.inport-icon-wrap {
  background: linear-gradient(135deg, #409EFF, #79bbff);
  color: #fff;
}
.sales-icon-wrap {
  background: linear-gradient(135deg, #67C23A, #95d475);
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
.chart-container {
  width: 100%;
  height: 360px;
}
</style>
