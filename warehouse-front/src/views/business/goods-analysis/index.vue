<template>
  <div>
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">进销商品分析</h1>
      <p class="page-header-desc">按时间段统计进货与销售的商品明细</p>
    </div>

    <!-- 进货商品分析 -->
    <el-card class="chart-card animate-fade-in-up delay-100">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <div class="header-icon-wrap inport-icon-wrap">
              <el-icon class="header-icon"><Download /></el-icon>
            </div>
            <div class="header-text-group">
              <span class="header-title">进货商品统计</span>
              <span class="header-subtitle">选择时间段查看进货商品明细</span>
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
      <el-table :data="inportPageData" stripe class="goods-table" v-loading="inportLoading">
        <el-table-column prop="goodsname" label="商品名称" min-width="180" />
        <el-table-column prop="totalNumber" label="数量" width="120" align="center" />
        <el-table-column prop="price" label="单价(元)" width="140" align="center">
          <template #default="{ row }">
            {{ formatPrice(row.price) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额(元)" width="160" align="right">
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
    </el-card>

    <!-- 销售商品分析 -->
    <el-card class="chart-card animate-fade-in-up delay-200">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <div class="header-icon-wrap sales-icon-wrap">
              <el-icon class="header-icon"><ShoppingCart /></el-icon>
            </div>
            <div class="header-text-group">
              <span class="header-title">销售商品统计</span>
              <span class="header-subtitle">选择时间段查看销售商品明细</span>
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
      <el-table :data="salesPageData" stripe class="goods-table" v-loading="salesLoading">
        <el-table-column prop="goodsname" label="商品名称" min-width="180" />
        <el-table-column prop="totalNumber" label="数量" width="120" align="center" />
        <el-table-column prop="price" label="单价(元)" width="140" align="center">
          <template #default="{ row }">
            {{ formatPrice(row.price) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额(元)" width="160" align="right">
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
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { inportGoodsAnalysis, salesGoodsAnalysis } from '@/api/report'
import { getDateRange } from '@/utils/dateRange'

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

let inportChart: echarts.ECharts | null = null
let salesChart: echarts.ECharts | null = null

const formatPrice = (val: any) => {
  const num = Number(val)
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

const initCharts = () => {
  if (inportChartRef.value) {
    inportChart = echarts.init(inportChartRef.value)
  }
  if (salesChartRef.value) {
    salesChart = echarts.init(salesChartRef.value)
  }
}

const buildBarOption = (title: string, names: string[], amounts: number[], color: string) => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
    formatter: (params: any) => {
      const p = params[0]
      return `${p.name}<br/>金额：<b>¥${Number(p.value).toLocaleString()}</b>`
    }
  },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: names,
    axisLabel: { rotate: names.length > 8 ? 30 : 0, fontSize: 12 }
  },
  yAxis: {
    type: 'value',
    axisLabel: { formatter: (v: number) => v >= 10000 ? (v / 10000) + '万' : v + '' }
  },
  series: [{
    name: title,
    type: 'bar',
    barMaxWidth: 40,
    data: amounts,
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
  if (!inportDateRange.value || inportDateRange.value.length < 2) return
  inportLoading.value = true
  if (inportChart) inportChart.showLoading()
  try {
    const res: any = await inportGoodsAnalysis(inportType.value, inportDateRange.value[0], inportDateRange.value[1])
    const data = res.data || []
    inportAllData.value = data
    inportPage.value = 1
    const top10 = data.slice(0, 10)
    inportChart?.setOption(buildBarOption(
      '进货金额',
      top10.map((d: any) => d.goodsname || '未知'),
      top10.map((d: any) => Number(d.totalAmount) || 0),
      '#409EFF'
    ))
  } catch {
    inportAllData.value = []
    inportChart?.setOption(buildBarOption('进货金额', [], [], '#409EFF'))
  } finally {
    inportLoading.value = false
    if (inportChart) inportChart.hideLoading()
  }
}

const loadSalesData = async () => {
  if (!salesDateRange.value || salesDateRange.value.length < 2) return
  salesLoading.value = true
  if (salesChart) salesChart.showLoading()
  try {
    const res: any = await salesGoodsAnalysis(salesType.value, salesDateRange.value[0], salesDateRange.value[1])
    const data = res.data || []
    salesAllData.value = data
    salesPage.value = 1
    const top10 = data.slice(0, 10)
    salesChart?.setOption(buildBarOption(
      '销售金额',
      top10.map((d: any) => d.goodsname || '未知'),
      top10.map((d: any) => Number(d.totalAmount) || 0),
      '#67C23A'
    ))
  } catch {
    salesAllData.value = []
    salesChart?.setOption(buildBarOption('销售金额', [], [], '#67C23A'))
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
  height: 320px;
}
.goods-table {
  margin-top: 16px;
}
.amount-text {
  font-weight: 600;
  color: #e6a23c;
}
.table-pagination {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
