<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">业绩排名</h1>
      <p class="page-header-desc">销售员业绩和商品销量排名</p>
    </div>

    <!-- 时间筛选 -->
    <el-card class="filter-card" style="margin-bottom: 16px;">
      <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
        <el-radio-group v-model="activePeriod" @change="handlePeriodChange">
          <el-radio-button label="today">今天</el-radio-button>
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
          <el-radio-button label="quarter">本季度</el-radio-button>
          <el-radio-button label="year">本年</el-radio-button>
        </el-radio-group>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="YYYY-MM-DD"
          class="compact-range"
          @change="handleDateChange"
        />
      </div>
    </el-card>

    <el-row :gutter="16">
      <!-- 销售员排名 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-icon style="color: var(--primary-color);"><Trophy /></el-icon>
              <span style="font-weight: 600;">销售员业绩排名</span>
            </div>
          </template>
          <el-table :data="salesRankingData" stripe>
            <el-table-column label="排名" width="70" align="center">
              <template #default="{ row }">
                <div class="rank-badge" :class="'rank-' + row.rank">
                  <span v-if="row.rank <= 3" class="rank-icon">{{ ['🥇','🥈','🥉'][row.rank-1] }}</span>
                  <span v-else>{{ row.rank }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="operator" label="销售员" min-width="100" />
            <el-table-column prop="totalOrders" label="订单数" width="80" align="center" />
            <el-table-column prop="totalQuantity" label="销量" width="80" align="center" />
            <el-table-column label="销售额" width="120" align="right">
              <template #default="{ row }">
                <span style="color: var(--success-color); font-weight: 600;">¥{{ row.totalAmount }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="salesRankingData.length === 0" class="empty-state">
            <el-empty description="暂无数据" />
          </div>
        </el-card>
      </el-col>

      <!-- 商品销量排名 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-icon style="color: var(--primary-color);"><Goods /></el-icon>
              <span style="font-weight: 600;">商品销量排名</span>
            </div>
          </template>
          <el-table :data="goodsRankingData" stripe>
            <el-table-column label="排名" width="70" align="center">
              <template #default="{ row }">
                <div class="rank-badge" :class="'rank-' + row.rank">
                  <span v-if="row.rank <= 3" class="rank-icon">{{ ['🥇','🥈','🥉'][row.rank-1] }}</span>
                  <span v-else>{{ row.rank }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="goodsname" label="商品名称" min-width="120" />
            <el-table-column prop="totalQuantity" label="销量" width="80" align="center">
              <template #default="{ row }">
                <span style="font-weight: 600;">{{ row.totalQuantity }}</span>
              </template>
            </el-table-column>
            <el-table-column label="销售额" width="120" align="right">
              <template #default="{ row }">
                <span style="color: var(--success-color); font-weight: 600;">¥{{ row.totalAmount }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="goodsRankingData.length === 0" class="empty-state">
            <el-empty description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Trophy, Goods } from '@element-plus/icons-vue'
import { salesRanking, goodsRanking } from '@/api/performance'

const activePeriod = ref('month')
const dateRange = ref<string[]>([])
const salesRankingData = ref<any[]>([])
const goodsRankingData = ref<any[]>([])

function handlePeriodChange(period: string) {
  dateRange.value = []
  loadData(period)
}

function handleDateChange(val: string[] | null) {
  if (val && val.length === 2) {
    activePeriod.value = ''
    loadData(undefined, val[0], val[1])
  }
}

async function loadData(period?: string, start?: string, end?: string) {
  const params: any = {}
  if (period) params.period = period
  if (start) params.startTime = start
  if (end) params.endTime = end

  try {
    const [sr, gr] = await Promise.all([salesRanking(params), goodsRanking(params)])
    salesRankingData.value = (sr as any).data || []
    goodsRankingData.value = (gr as any).data || []
  } catch {}
}

onMounted(() => loadData('month'))
</script>

<style scoped>
.compact-range :deep(.el-range-editor) {
  width: 220px !important;
  min-width: 220px !important;
}
.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-size: 13px;
  font-weight: 600;
}
.rank-icon {
  font-size: 18px;
}
.rank-1 { color: #f5a623; }
.rank-2 { color: #b0b0b0; }
.rank-3 { color: #cd7f32; }
.empty-state {
  padding: 40px 0;
}
</style>
