<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">盘点管理</h1>
      <p class="page-header-desc">库存盘点，核对系统库存与实际库存</p>
    </div>

    <!-- 盘点单列表 -->
    <el-card v-if="!showDetail">
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部" clearable style="width: 150px;">
            <el-option label="进行中" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllStocktake" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="stocktakeNo" label="盘点单号" width="180" />
        <el-table-column prop="operator" label="盘点人" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" effect="dark" round>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="finishTime" label="完成时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="primary" link @click="handleViewDetail(row)">盘点</el-button>
            <el-button v-if="row.status === 0" type="danger" link @click="handleCancel(row)">取消</el-button>
            <el-button v-if="row.status !== 0" type="info" link @click="handleViewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
        <template #toolbar>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon> 新建盘点单
          </el-button>
        </template>
      </CrudTable>
    </el-card>

    <!-- 盘点明细 -->
    <el-card v-else>
      <template #header>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <div>
            <el-button @click="showDetail = false" style="margin-right: 12px;">
              <el-icon><ArrowLeft /></el-icon> 返回
            </el-button>
            <span style="font-weight: 600; font-size: 16px;">盘点单: {{ currentStocktake?.stocktakeNo }}</span>
            <el-tag :type="statusTag(currentStocktake?.status)" style="margin-left: 8px;" effect="dark" round>
              {{ statusText(currentStocktake?.status) }}
            </el-tag>
          </div>
          <div>
            <el-button :icon="Printer" @click="printVisible = true">打印</el-button>
            <template v-if="currentStocktake?.status === 0">
              <el-button @click="handleCopySystemNum">
                <el-icon><CopyDocument /></el-icon> 一键复制系统库存
              </el-button>
              <el-button type="success" @click="handleSaveItems">保存</el-button>
              <el-button type="primary" @click="handleSubmit">提交盘点</el-button>
            </template>
          </div>
        </div>
      </template>

      <el-table :data="stocktakeItems" border stripe>
        <el-table-column prop="goodsname" label="商品名称" min-width="150" />
        <el-table-column prop="providername" label="供应商" width="120" />
        <el-table-column prop="systemNum" label="系统库存" width="100" align="center">
          <template #default="{ row }">
            <span style="font-weight: 600;">{{ row.systemNum }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实际盘点" width="150" align="center">
          <template #default="{ row }">
            <el-input-number
              v-if="currentStocktake?.status === 0"
              v-model="row.actualNum"
              :min="0"
              size="small"
              controls-position="right"
              style="width: 120px;"
              @change="row.diffNum = (row.actualNum ?? 0) - row.systemNum"
            />
            <span v-else>{{ row.actualNum ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="差异" width="100" align="center">
          <template #default="{ row }">
            <template v-if="row.actualNum != null">
              <span :class="{ 'text-danger': row.diffNum < 0, 'text-success': row.diffNum > 0, 'text-muted': row.diffNum === 0 }">
                {{ row.diffNum > 0 ? '+' : '' }}{{ row.diffNum }}
              </span>
            </template>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" width="200">
          <template #default="{ row }">
            <el-input
              v-if="currentStocktake?.status === 0"
              v-model="row.remark"
              size="small"
              placeholder="备注"
            />
            <span v-else>{{ row.remark || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 打印模板 -->
    <PrintDocument
      v-model:visible="printVisible"
      title="盘点单"
      :order-no="currentStocktake?.stocktakeNo"
      :meta="printMeta"
      :columns="printColumns"
      :rows="stocktakeItems"
      :summary="printSummary"
      :remark="currentStocktake?.remark"
      :signatures="['盘点人', '复核人', '日期']"
    />

    <!-- 新建盘点单弹窗 -->
    <el-dialog v-model="createDialogVisible" title="新建盘点单" width="400px">
      <el-form label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" placeholder="盘点备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit" :loading="creating">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, CopyDocument, Printer } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import PrintDocument, { type PrintColumn, type PrintMetaItem } from '@/components/PrintDocument.vue'
import {
  loadAllStocktake,
  createStocktake,
  loadStocktakeItems,
  saveStocktakeItems,
  submitStocktake,
  cancelStocktake
} from '@/api/stocktake'

const tableRef = ref()
const searchParams = reactive({ status: undefined })
const showDetail = ref(false)
const currentStocktake = ref<any>(null)
const stocktakeItems = ref<any[]>([])
const createDialogVisible = ref(false)
const createForm = reactive({ remark: '' })
const creating = ref(false)

// 打印相关
const printVisible = ref(false)

const printMeta = computed<PrintMetaItem[]>(() => [
  { label: '盘点人', value: currentStocktake.value?.operator },
  { label: '状态', value: currentStocktake.value ? statusText(currentStocktake.value.status) : '-' },
  { label: '创建时间', value: currentStocktake.value?.createTime },
  { label: '完成时间', value: currentStocktake.value?.finishTime || '-' }
])

const printColumns: PrintColumn[] = [
  { key: 'goodsname', label: '商品名称' },
  { key: 'providername', label: '供应商', width: '110px' },
  { key: 'systemNum', label: '系统库存', align: 'center', width: '80px' },
  { key: 'actualNum', label: '实际盘点', align: 'center', width: '80px', format: (row) => row.actualNum ?? '-' },
  {
    key: 'diffNum', label: '差异', align: 'center', width: '80px',
    format: (row) => row.actualNum != null ? (row.diffNum > 0 ? `+${row.diffNum}` : `${row.diffNum}`) : '-'
  },
  { key: 'remark', label: '备注', format: (row) => row.remark || '-' }
]

const printSummary = computed<PrintMetaItem[]>(() => {
  let surplus = 0
  let shortage = 0
  for (const item of stocktakeItems.value) {
    if (item.diffNum > 0) surplus += item.diffNum
    if (item.diffNum < 0) shortage += -item.diffNum
  }
  return [
    { label: '商品种类', value: stocktakeItems.value.length },
    { label: '盘盈数量', value: surplus },
    { label: '盘亏数量', value: shortage }
  ]
})

function statusText(status: number) {
  return { 0: '进行中', 1: '已完成', 2: '已取消' }[status] ?? '未知'
}

function statusTag(status: number) {
  return { 0: 'warning', 1: 'success', 2: 'info' }[status] ?? 'info'
}

function handleSearch() { tableRef.value?.reload() }
function handleReset() { searchParams.status = undefined; tableRef.value?.reload() }

function handleCreate() {
  createForm.remark = ''
  createDialogVisible.value = true
}

async function handleCreateSubmit() {
  creating.value = true
  try {
    const res: any = await createStocktake({ remark: createForm.remark })
    ElMessage.success(res.msg || '创建成功')
    createDialogVisible.value = false
    tableRef.value?.reload()
  } catch {} finally {
    creating.value = false
  }
}

async function handleViewDetail(row: any) {
  currentStocktake.value = row
  showDetail.value = true
  try {
    const res: any = await loadStocktakeItems({ stocktakeId: row.id })
    stocktakeItems.value = res.data || []
  } catch {
    stocktakeItems.value = []
  }
}

function handleCopySystemNum() {
  stocktakeItems.value.forEach(item => {
    item.actualNum = item.systemNum
    item.diffNum = 0
  })
  ElMessage.success('已复制系统库存到实际盘点数')
}

async function handleSaveItems() {
  try {
    await saveStocktakeItems(stocktakeItems.value)
    ElMessage.success('保存成功')
  } catch {}
}

async function handleSubmit() {
  try {
    await ElMessageBox.confirm('提交后库存将被更新，确认提交？', '确认', { type: 'warning' })
    const res: any = await submitStocktake({ id: currentStocktake.value.id })
    ElMessage.success(res.msg || '提交成功')
    currentStocktake.value.status = 1
    tableRef.value?.reload()
  } catch {}
}

async function handleCancel(row: any) {
  try {
    await ElMessageBox.confirm('确认取消此盘点单？', '确认', { type: 'warning' })
    await cancelStocktake({ id: row.id })
    ElMessage.success('已取消')
    tableRef.value?.reload()
  } catch {}
}
</script>

<style scoped>
.text-danger { color: var(--danger-color, #f56c6c); font-weight: 600; }
.text-success { color: var(--success-color, #67c23a); font-weight: 600; }
.text-muted { color: var(--text-placeholder, #c0c4cc); }
</style>
