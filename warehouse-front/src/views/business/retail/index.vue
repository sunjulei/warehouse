<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="商品">
          <el-select v-model="searchParams.goodsid" placeholder="全部" clearable filterable>
            <el-option v-for="g in goodsList" :key="g.id" :label="`${g.goodsname}${g.providername ? ' (' + g.providername + ')' : ''}`" :value="g.id" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllRetail" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="goodsname" label="商品" />
        <el-table-column prop="number" label="数量" width="80" />
        <el-table-column prop="retailprice" label="售价" width="80" />
        <el-table-column prop="retailtime" label="零售时间" width="160" />
        <el-table-column prop="operateperson" label="操作员" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleBack(row)">退货</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #toolbar>
          <el-button type="primary" @click="openBatchDialog">
            <el-icon><Plus /></el-icon> 添加
          </el-button>
        </template>
      </CrudTable>
    </el-card>

    <!-- 批量添加弹窗 -->
    <el-dialog v-model="batchDialogVisible" title="散客零售" width="800px" @close="resetBatchForm">
      <el-table :data="batchItems" border size="small">
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <el-select v-model="row.goodsid" placeholder="选择商品" filterable size="small" @change="onBatchGoodsChange(row)">
              <el-option v-for="g in goodsList" :key="g.id" :label="`${g.goodsname}${g.providername ? ' (' + g.providername + ')' : ''}`" :value="g.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="70" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.stock > 0 ? '#909399' : '#f56c6c' }">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.number" :min="1" :max="row.stock || undefined" size="small" controls-position="right" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column label="售价" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.retailprice" :min="0" :precision="2" size="small" controls-position="right" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="90" align="right">
          <template #default="{ row }">
            {{ (row.retailprice * row.number).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link @click="batchItems.splice($index, 1)" :disabled="batchItems.length <= 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px; display: flex; justify-content: space-between; align-items: center;">
        <el-button type="primary" plain size="small" @click="addBatchItem">
          <el-icon><Plus /></el-icon> 添加商品
        </el-button>
        <span style="font-size: 15px; font-weight: 600;">合计: ¥{{ batchTotal.toFixed(2) }}</span>
      </div>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit" :loading="batchSubmitting">结算</el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="rules" width="500px" @success="tableRef?.reload()">
      <template #default="{ formData }">
        <el-form-item label="商品" prop="goodsid">
          <el-select v-model="formData.goodsid" placeholder="选择商品" filterable @change="onGoodsChange(formData)">
            <el-option v-for="g in goodsList" :key="g.id" :label="`${g.goodsname}${g.providername ? ' (' + g.providername + ')' : ''}`" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="number">
          <el-input-number v-model="formData.number" :min="1" :max="selectedGoodsStock || undefined" />
          <span v-if="selectedGoodsStock > 0" style="margin-left: 8px; color: #909399; font-size: 13px;">库存: {{ selectedGoodsStock }}</span>
        </el-form-item>
        <el-form-item label="售价" prop="retailprice">
          <el-input-number v-model="formData.retailprice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="支付方式" prop="paytype">
          <el-select v-model="formData.paytype" placeholder="选择支付方式">
            <el-option label="现金" value="现金" />
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="银行卡" value="银行卡" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </template>
    </CrudDialog>

    <!-- 退货弹窗 -->
    <el-dialog v-model="backDialogVisible" title="零售退货" width="400px">
      <el-form :model="backForm" label-width="80px">
        <el-form-item label="退货数量">
          <el-input-number v-model="backForm.number" :min="1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="backForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="backDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBackSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadAllRetail, addRetail, updateRetail, deleteRetail, batchAddRetail } from '@/api/retail'
import { addRetailback } from '@/api/retailback'
import { loadAllGoodsForSelect } from '@/api/goods'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const goodsList = ref<any[]>([])
const backDialogVisible = ref(false)
const backForm = reactive({ id: 0, number: 1, remark: '' })
const selectedGoodsStock = ref(0)

// 批量添加相关
const batchDialogVisible = ref(false)
const batchSubmitting = ref(false)
const batchItems = reactive<any[]>([])

const searchParams = reactive({ goodsid: null as number | null })

const batchTotal = computed(() => {
  return batchItems.reduce((sum, item) => sum + (item.retailprice || 0) * (item.number || 0), 0)
})

const createEmptyItem = () => ({ goodsid: null, number: 1, retailprice: 0, stock: 0 })

const openBatchDialog = () => {
  batchItems.length = 0
  batchItems.push(createEmptyItem())
  batchDialogVisible.value = true
}

const resetBatchForm = () => {
  batchItems.length = 0
}

const addBatchItem = () => {
  batchItems.push(createEmptyItem())
}

const onBatchGoodsChange = (row: any) => {
  const goods = goodsList.value.find((g: any) => g.id === row.goodsid)
  if (goods) {
    row.retailprice = goods.price
    row.stock = goods.number
    if (row.number > goods.number) row.number = goods.number || 1
  } else {
    row.retailprice = 0
    row.stock = 0
  }
}

const handleBatchSubmit = async () => {
  const validItems = batchItems.filter(item => item.goodsid && item.number > 0 && item.retailprice >= 0)
  if (validItems.length === 0) {
    ElMessage.warning('请至少添加一件商品')
    return
  }
  for (const item of validItems) {
    if (item.stock > 0 && item.number > item.stock) {
      ElMessage.warning(`商品库存不足，当前库存: ${item.stock}`)
      return
    }
  }
  batchSubmitting.value = true
  try {
    const data = validItems.map(item => ({
      goodsid: item.goodsid,
      number: item.number,
      retailprice: item.retailprice,
      paytype: '现金',
      remark: ''
    }))
    const res: any = await batchAddRetail(data)
    if (res.code === 200) {
      ElMessage.success(`成功结算 ${validItems.length} 件商品`)
      batchDialogVisible.value = false
      tableRef.value?.reload()
    }
  } finally {
    batchSubmitting.value = false
  }
}

// 单条编辑相关
const onGoodsChange = (formData: any) => {
  const goods = goodsList.value.find((g: any) => g.id === formData.goodsid)
  if (goods) {
    formData.retailprice = goods.price
    selectedGoodsStock.value = goods.number
  } else {
    selectedGoodsStock.value = 0
  }
}

const rules = {
  goodsid: [{ required: true, message: '请选择商品', trigger: 'change' }],
  number: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  retailprice: [{ required: true, message: '请输入售价', trigger: 'blur' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.goodsid = null }
const handleEdit = (row: any) => {
  isEdit.value = true
  const goods = goodsList.value.find((g: any) => g.id === row.goodsid)
  selectedGoodsStock.value = goods ? goods.number : 0
  dialogRef.value?.open(row, true)
}
const handleSubmitApi = (data: any) => updateRetail(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除？删除后将同时删除该零售单的所有退货记录。', '提示', { type: 'warning' })
  await deleteRetail(row.id)
  tableRef.value?.reload()
}
const handleBack = (row: any) => {
  backForm.id = row.id
  backForm.number = 1
  backForm.remark = ''
  backDialogVisible.value = true
}
const handleBackSubmit = async () => {
  const res: any = await addRetailback(backForm.id, backForm.number, backForm.remark)
  if (res.code === 200) {
    ElMessage.success('退货成功')
    backDialogVisible.value = false
    tableRef.value?.reload()
  }
}

onMounted(async () => {
  try {
    const gRes = await loadAllGoodsForSelect()
    goodsList.value = (gRes as any).data || []
  } catch {}
})
</script>

<style scoped>
.page-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}
.page-container :deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
}
</style>
