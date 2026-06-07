<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="客户">
          <el-select v-model="searchParams.customerid" placeholder="全部" clearable filterable style="width: 200px">
            <el-option v-for="c in customers" :key="c.id" :label="c.customername" :value="c.id" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllOrders" :search-params="searchParams" :row-class-name="tableRowClassName">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="itemCount" label="商品种类" width="80" align="center" />
        <el-table-column prop="totalNumber" label="总数量" width="80" align="center" />
        <el-table-column prop="totalAmount" label="总金额" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="salestime" label="销售时间" width="160" />
        <el-table-column prop="operateperson" label="操作员" width="100" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.orderStatus === 1 ? 'info' : 'success'" size="small">
              {{ row.orderStatus === 1 ? '已退完' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            <el-button type="success" link @click="handleAddGoods(row)" :disabled="row.orderStatus === 1">加货</el-button>
            <el-button type="warning" link @click="handleViewRecords(row)">记录</el-button>
            <el-button type="danger" link @click="handleOpenReturn(row)" :disabled="row.orderStatus === 1">退货</el-button>
          </template>
        </el-table-column>
      </CrudTable>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="800px">
      <div class="order-detail-header" v-if="currentOrder">
        <span>订单号: <strong>{{ currentOrder.orderNo }}</strong></span>
        <span>客户: <strong>{{ currentOrder.customerName }}</strong></span>
        <span>时间: <strong>{{ currentOrder.salestime }}</strong></span>
      </div>

      <el-table :data="mergedOrderDetails" border style="width: 100%">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="goodsname" label="商品名称" />
        <el-table-column prop="size" label="规格" width="100" />
        <el-table-column prop="number" label="数量" width="80" align="center" />
        <el-table-column prop="saleprice" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.saleprice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ (row.saleprice * row.number).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="order-detail-footer" v-if="mergedOrderDetails.length > 0">
        <span>共 <strong>{{ mergedOrderDetails.length }}</strong> 种商品</span>
        <span>总数量: <strong>{{ orderDetailTotalQty }}</strong></span>
        <span>总金额: <strong style="color: #f56c6c;">¥{{ orderDetailTotalAmount.toFixed(2) }}</strong></span>
      </div>
    </el-dialog>

    <!-- 退货弹窗 -->
    <el-dialog v-model="returnDialogVisible" title="退货" width="900px" @close="resetReturnForm">
      <div class="return-header" v-if="currentOrder">
        <span>订单号: <strong>{{ currentOrder.orderNo }}</strong></span>
        <span>客户: <strong>{{ currentOrder.customerName }}</strong></span>
      </div>

      <!-- 退货模式选择 -->
      <div class="return-mode">
        <el-radio-group v-model="returnMode">
          <el-radio-button value="single">单个商品退货</el-radio-button>
          <el-radio-button value="all">整单退货</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 单个商品退货 -->
      <div v-if="returnMode === 'single'" class="return-single">
        <el-table :data="returnableItems" border style="width: 100%">
          <el-table-column prop="goodsname" label="商品名称" />
          <el-table-column prop="size" label="规格" width="100" />
          <el-table-column prop="saleprice" label="单价" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.saleprice?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="可退数量" width="100" align="center">
            <template #default="{ row }">
              {{ row.number }}
            </template>
          </el-table-column>
          <el-table-column label="退货数量" width="150" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.returnQty"
                :min="0"
                :max="row.number"
                size="small"
                style="width: 120px"
              />
            </template>
          </el-table-column>
          <el-table-column label="退货金额" width="100" align="right">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600;">¥{{ (row.saleprice * (row.returnQty || 0)).toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="return-single-summary" v-if="returnableItems.length > 0">
          <span>退货金额: <strong style="color: #f56c6c;">¥{{ returnSingleTotalAmount.toFixed(2) }}</strong></span>
        </div>
      </div>

      <!-- 整单退货 -->
      <div v-if="returnMode === 'all'" class="return-all">
        <el-alert title="整单退货将退还未退货的所有商品，退货后将恢复所有商品库存。" type="warning" :closable="false" show-icon />

        <el-table :data="returnableItems" border style="width: 100%; margin-top: 16px">
          <el-table-column prop="goodsname" label="商品名称" />
          <el-table-column prop="size" label="规格" width="100" />
          <el-table-column prop="number" label="数量" width="80" align="center" />
          <el-table-column prop="saleprice" label="单价" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.saleprice?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="小计" width="100" align="right">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600;">¥{{ (row.saleprice * row.number).toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="return-all-summary" v-if="returnableItems.length > 0">
          <span>共 <strong>{{ returnableItems.length }}</strong> 种商品</span>
          <span>总数量: <strong>{{ returnAllTotalQty }}</strong></span>
          <span>总金额: <strong style="color: #f56c6c;">¥{{ returnAllTotalAmount.toFixed(2) }}</strong></span>
        </div>
      </div>

      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReturnSubmit" :loading="returnSubmitting" :disabled="returnMode === 'single' && !hasReturnQty">
          确认退货
        </el-button>
      </template>
    </el-dialog>

    <!-- 加货弹窗 -->
    <el-dialog v-model="addGoodsDialogVisible" title="加货" width="700px" @close="resetAddForm">
      <div class="add-goods-header" v-if="currentOrder">
        <span>订单号: <strong>{{ currentOrder.orderNo }}</strong></span>
        <span>客户: <strong>{{ currentOrder.customerName }}</strong></span>
      </div>

      <div v-for="(item, index) in addForm.items" :key="index" class="add-item-row">
        <el-form-item :label="'商品' + (index + 1)" class="add-item-form">
          <div class="add-item-content">
            <el-select v-model="item.goodsid" placeholder="选择商品" filterable style="width: 240px" :fit-input-width="false" @change="(val: number) => onGoodsChange(index, val)">
              <el-option
                v-for="g in goodsList"
                :key="g.id"
                :label="`${g.goodsname}${g.providername ? ' (' + g.providername + ')' : ''}`"
                :value="g.id"
              />
            </el-select>
            <el-input-number v-model="item.number" :min="1" style="width: 100px" />
            <el-input-number v-model="item.saleprice" :min="0" :precision="2" placeholder="售价" style="width: 120px" />
            <span class="add-item-subtotal">¥{{ (item.number * item.saleprice).toFixed(2) }}</span>
            <el-button type="danger" :icon="Delete" circle @click="removeAddItem(index)" />
          </div>
        </el-form-item>
      </div>

      <el-button type="primary" link @click="addAddItem" style="margin-bottom: 16px">
        <el-icon><Plus /></el-icon> 添加商品
      </el-button>

      <div class="add-summary">
        <span>总数量: <strong>{{ addTotalQuantity }}</strong> 件</span>
        <span>总金额: <strong>¥{{ addTotalAmount.toFixed(2) }}</strong></span>
      </div>

      <template #footer>
        <el-button @click="addGoodsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddGoodsSubmit" :loading="addSubmitting">确认加货</el-button>
      </template>
    </el-dialog>

    <!-- 操作记录弹窗 -->
    <el-dialog v-model="recordsDialogVisible" title="操作记录" width="900px">
      <div class="records-header" v-if="currentOrder">
        <span>订单号: <strong>{{ currentOrder.orderNo }}</strong></span>
        <span>客户: <strong>{{ currentOrder.customerName }}</strong></span>
      </div>

      <el-table :data="orderRecords" border style="width: 100%" v-loading="recordsLoading">
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '退货' ? 'info' : row.typeTag" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" label="商品名称" />
        <el-table-column prop="goodsSize" label="规格" width="100" />
        <el-table-column prop="number" label="数量" width="80" align="center" />
        <el-table-column prop="price" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operatePerson" label="操作员" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="160" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadAllOrders, loadOrderDetail, returnSingleGoods, returnOrder, addToOrder, loadReturnAddRecords } from '@/api/sales'
import { loadAllCustomerForSelect } from '@/api/customer'
import { loadAllGoodsForSelect } from '@/api/goods'

const tableRef = ref()
const customers = ref<any[]>([])
const goodsList = ref<any[]>([])
const searchParams = reactive({ customerid: null as number | null })

const detailDialogVisible = ref(false)
const currentOrder = ref<any>(null)
const orderDetails = ref<any[]>([])

// 合并相同商品的详情
const mergedOrderDetails = computed(() => {
  const map = new Map()
  for (const item of orderDetails.value) {
    if (item.number <= 0) continue // 跳过已退货的
    const key = `${item.goodsid}_${item.saleprice}`
    if (map.has(key)) {
      const existing = map.get(key)
      existing.number += item.number
    } else {
      map.set(key, { ...item })
    }
  }
  return Array.from(map.values())
})

// 操作记录相关
const recordsDialogVisible = ref(false)
const recordsLoading = ref(false)
const orderRecords = ref<any[]>([])

// 退货相关
const returnDialogVisible = ref(false)
const returnSubmitting = ref(false)
const returnMode = ref<'single' | 'all'>('single')
const returnableItems = ref<any[]>([])

// 单个退货总金额
const returnSingleTotalAmount = computed(() => {
  return returnableItems.value.reduce((sum, item) => {
    return sum + item.saleprice * (item.returnQty || 0)
  }, 0)
})

// 是否有退货数量
const hasReturnQty = computed(() => {
  return returnableItems.value.some(item => item.returnQty > 0)
})

// 加货相关
const addGoodsDialogVisible = ref(false)
const addSubmitting = ref(false)
const addForm = reactive({
  items: [{ goodsid: null as number | null, number: 1, saleprice: 0 }] as { goodsid: number | null; number: number; saleprice: number }[]
})

// 订单详情汇总
const orderDetailTotalQty = computed(() => {
  return mergedOrderDetails.value.reduce((sum, item) => sum + item.number, 0)
})

const orderDetailTotalAmount = computed(() => {
  return mergedOrderDetails.value.reduce((sum, item) => sum + item.saleprice * item.number, 0)
})

// 整单退货汇总
const returnAllTotalQty = computed(() => {
  return returnableItems.value.reduce((sum, item) => sum + item.number, 0)
})

const returnAllTotalAmount = computed(() => {
  return returnableItems.value.reduce((sum, item) => sum + item.saleprice * item.number, 0)
})

// 加货汇总
const addTotalQuantity = computed(() => {
  return addForm.items.reduce((sum, item) => sum + (item.number || 0), 0)
})

const addTotalAmount = computed(() => {
  return addForm.items.reduce((sum, item) => sum + (item.number || 0) * (item.saleprice || 0), 0)
})

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.customerid = null }

// 行样式：已退完的订单显示灰色背景
const tableRowClassName = ({ row }: { row: any }) => {
  if (row.orderStatus === 1) {
    return 'returned-row'
  }
  return ''
}

// 查看订单详情
const handleViewDetail = async (row: any) => {
  currentOrder.value = row
  try {
    const res: any = await loadOrderDetail(row.orderNo)
    orderDetails.value = res.data || []
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

// 查看操作记录
const handleViewRecords = async (row: any) => {
  currentOrder.value = row
  recordsDialogVisible.value = true
  recordsLoading.value = true
  try {
    const res: any = await loadReturnAddRecords({ orderNo: row.orderNo, page: 1, limit: 100 })
    orderRecords.value = res.data || []
  } catch (error) {
    console.error('加载操作记录失败:', error)
  } finally {
    recordsLoading.value = false
  }
}

// 打开退货弹窗
const handleOpenReturn = async (row: any) => {
  currentOrder.value = row
  resetReturnForm()
  returnDialogVisible.value = true

  // 加载订单详情，筛选可退货的商品
  try {
    const res: any = await loadOrderDetail(row.orderNo)
    const details = res.data || []
    // 合并相同商品
    const map = new Map()
    for (const item of details) {
      if (item.number <= 0 || item.orderStatus === 1) continue
      const key = `${item.goodsid}_${item.saleprice}`
      if (map.has(key)) {
        const existing = map.get(key)
        existing.number += item.number
        existing.ids.push(item.id)
      } else {
        map.set(key, { ...item, ids: [item.id], returnQty: 0 })
      }
    }
    returnableItems.value = Array.from(map.values())
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

// 重置退货表单
const resetReturnForm = () => {
  returnMode.value = 'single'
  returnableItems.value = []
}

// 确认退货
const handleReturnSubmit = async () => {
  returnSubmitting.value = true
  try {
    if (returnMode.value === 'single') {
      // 单个商品退货 - 遍历有退货数量的商品
      const itemsToReturn = returnableItems.value.filter(item => item.returnQty > 0)
      if (itemsToReturn.length === 0) {
        ElMessage.warning('请设置要退货的数量')
        return
      }

      for (const item of itemsToReturn) {
        // 对每条记录分别退货
        let remaining = item.returnQty
        for (const salesId of item.ids) {
          if (remaining <= 0) break
          const detail = orderDetails.value.find((d: any) => d.id === salesId)
          if (!detail || detail.number <= 0) continue
          const qty = Math.min(remaining, detail.number)
          await returnSingleGoods(salesId, qty)
          remaining -= qty
        }
      }
      ElMessage.success('退货成功')
      returnDialogVisible.value = false
      tableRef.value?.reload()
    } else {
      // 整单退货
      await ElMessageBox.confirm(
        `确认整单退货？将退还未退货的所有商品，总金额¥${returnAllTotalAmount.value.toFixed(2)}。`,
        '确认整单退货',
        { type: 'warning' }
      )
      const res: any = await returnOrder(currentOrder.value.orderNo)
      if (res.code === 200) {
        ElMessage.success('整单退货成功')
        returnDialogVisible.value = false
        tableRef.value?.reload()
      }
    }
  } catch (error) {
    console.error('退货失败:', error)
  } finally {
    returnSubmitting.value = false
  }
}

// 加货相关函数
const handleAddGoods = (row: any) => {
  currentOrder.value = row
  resetAddForm()
  addGoodsDialogVisible.value = true
}

const addAddItem = () => {
  addForm.items.push({ goodsid: null, number: 1, saleprice: 0 })
}

const removeAddItem = (index: number) => {
  if (addForm.items.length > 1) {
    addForm.items.splice(index, 1)
  }
}

const resetAddForm = () => {
  addForm.items = [{ goodsid: null, number: 1, saleprice: 0 }]
}

const onGoodsChange = (index: number, goodsid: number) => {
  const goods = goodsList.value.find((g: any) => g.id === goodsid)
  if (goods) {
    addForm.items[index].saleprice = goods.price || 0
  }
}

const handleAddGoodsSubmit = async () => {
  const validItems = addForm.items.filter(item => item.goodsid && item.number > 0)
  if (validItems.length === 0) {
    ElMessage.warning('请至少添加一个商品')
    return
  }

  try {
    addSubmitting.value = true
    const data = validItems.map(item => ({
      orderno: currentOrder.value.orderNo,
      customerid: currentOrder.value.customerId,
      goodsid: item.goodsid,
      number: item.number,
      saleprice: item.saleprice
    }))

    const res: any = await addToOrder(data)
    if (res.code === 200) {
      ElMessage.success('加货成功')
      addGoodsDialogVisible.value = false
      tableRef.value?.reload()
    }
  } catch (error) {
    console.error('加货失败:', error)
  } finally {
    addSubmitting.value = false
  }
}

onMounted(async () => {
  try {
    const [cRes, gRes] = await Promise.all([loadAllCustomerForSelect(), loadAllGoodsForSelect()])
    customers.value = (cRes as any).data || []
    goodsList.value = (gRes as any).data || []
  } catch {}
})
</script>

<style>
/* 已退完订单的灰色背景 */
.el-table .returned-row {
  background-color: #f5f5f5 !important;
  color: #999;
}

.el-table .returned-row:hover > td {
  background-color: #e8e8e8 !important;
}
</style>

<style scoped>
.order-detail-header {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.order-detail-header strong {
  color: #303133;
}

.order-detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  margin-top: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.order-detail-footer strong {
  color: #303133;
}

.return-header {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.return-header strong {
  color: #303133;
}

.return-mode {
  margin-bottom: 16px;
}

.return-goods-info {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
  line-height: 2;
}

.return-goods-info strong {
  color: #303133;
}

.return-amount {
  margin-top: 16px;
  font-size: 14px;
  color: #606266;
  text-align: right;
}

.return-amount strong {
  font-size: 18px;
}

.return-single-summary {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.return-single-summary strong {
  font-size: 18px;
}

.return-all-summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  margin-top: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.return-all-summary strong {
  color: #303133;
}

.add-goods-header {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.add-goods-header strong {
  color: #303133;
}

.add-item-row {
  margin-bottom: 16px;
}

.add-item-form {
  margin-bottom: 0;
}

.add-item-content {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.add-item-subtotal {
  min-width: 80px;
  text-align: right;
  font-weight: 600;
  color: #f56c6c;
  line-height: 32px;
}

.add-summary {
  display: flex;
  justify-content: flex-end;
  gap: 32px;
  padding: 16px;
  margin-top: 8px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.add-summary strong {
  color: #f56c6c;
  font-size: 16px;
}

.records-header {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.records-header strong {
  color: #303133;
}
</style>
