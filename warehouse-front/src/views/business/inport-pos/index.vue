<template>
  <div class="inport-pos-page">
    <el-row :gutter="16" class="full-height">
      <!-- 左侧商品选择区域 -->
      <el-col :span="14" class="left-panel">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span class="panel-title">商品选择</span>
              <el-select v-model="selectedProvider" placeholder="选择供应商" filterable style="width: 200px" @change="onProviderChange">
                <el-option v-for="p in providers" :key="p.id" :label="p.providername" :value="p.id" />
              </el-select>
            </div>
          </template>

          <!-- 搜索栏 -->
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品名称/拼音..."
              clearable
              prefix-icon="Search"
              size="large"
              @input="onSearchInput"
            />
          </div>

          <!-- 商品列表 -->
          <div class="goods-grid" v-loading="goodsLoading">
            <div
              v-for="goods in pagedGoodsList"
              :key="goods.id"
              class="goods-card"
              @click="addToCart(goods)"
            >
              <div class="goods-card-name">{{ goods.goodsname }}</div>
              <div class="goods-card-price">¥{{ goods.price?.toFixed(2) }}</div>
              <div class="goods-card-stock">库存: {{ goods.number }}</div>
            </div>
            <div v-if="pagedGoodsList.length === 0 && !goodsLoading" class="goods-empty">
              <el-empty v-if="!selectedProvider" description="请先选择供应商" :image-size="60" />
              <el-empty v-else description="暂无商品" :image-size="60" />
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="filteredGoodsList.length"
              layout="total, prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧购物车区域 -->
      <el-col :span="10" class="right-panel">
        <el-card class="panel-card cart-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span class="panel-title">进货清单</span>
              <el-button type="danger" link @click="clearCart" :disabled="cartItems.length === 0">
                清空
              </el-button>
            </div>
          </template>

          <!-- 购物车列表 -->
          <div class="cart-list" v-if="cartItems.length > 0">
            <div v-for="(item, index) in cartItems" :key="item.goodsid" class="cart-item">
              <div class="cart-item-info">
                <div class="cart-item-name">{{ item.goodsname }}</div>
                <div class="cart-item-provider" v-if="item.providername">{{ item.providername }}</div>
                <div class="cart-item-price">进货价: ¥{{ item.inportprice?.toFixed(2) }}</div>
              </div>
              <div class="cart-item-right">
                <div class="cart-item-subtotal">¥{{ (item.inportprice * item.number).toFixed(2) }}</div>
                <div class="cart-item-actions">
                  <el-input-number
                    v-model="item.number"
                    :min="1"
                    size="small"
                    style="width: 100px"
                    @change="(val: number) => onQtyChange(index, val)"
                  />
                  <el-button size="small" type="danger" circle @click="removeFromCart(index)">×</el-button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="cart-empty">
            <el-empty description="进货清单为空" :image-size="80" />
          </div>

          <!-- 付款方式 -->
          <div class="cart-paytype" v-if="cartItems.length > 0">
            <el-select v-model="paytype" placeholder="选择付款方式" style="width: 100%">
              <el-option label="现金" value="现金" />
              <el-option label="微信" value="微信" />
              <el-option label="支付宝" value="支付宝" />
              <el-option label="银行卡" value="银行卡" />
            </el-select>
          </div>

          <!-- 备注 -->
          <div class="cart-remark" v-if="cartItems.length > 0">
            <el-input v-model="remark" type="textarea" :rows="2" placeholder="备注信息（可选）" />
          </div>

          <!-- 汇总和结算 -->
          <div class="cart-summary" v-if="cartItems.length > 0">
            <div class="summary-row">
              <span>商品种类: <strong>{{ cartItems.length }}</strong></span>
              <span>总数量: <strong>{{ totalQuantity }}</strong></span>
            </div>
            <div class="summary-total">
              合计: <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <el-button
              type="success"
              size="large"
              class="submit-btn"
              @click="handleConfirm"
            >
              确认进货
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 序列号录入弹窗 -->
    <SerialNumberInput ref="serialInputRef" @confirm="handleSerialConfirm" />

    <!-- 确认进货弹窗 -->
    <el-dialog v-model="confirmDialogVisible" title="确认进货" width="600px">
      <div class="confirm-header">
        <span>供应商: <strong>{{ providers.find(p => p.id === selectedProvider)?.providername }}</strong></span>
        <span>付款方式: <strong>{{ paytype }}</strong></span>
      </div>

      <div class="confirm-goods-list">
        <el-table :data="cartItems" border style="width: 100%" max-height="400">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="goodsname" label="商品名称" />
          <el-table-column prop="inportprice" label="进货价" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.inportprice?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="number" label="数量" width="80" align="center" />
          <el-table-column label="小计" width="100" align="right">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600;">¥{{ (row.inportprice * row.number).toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="confirm-summary">
        <span>商品种类: <strong>{{ cartItems.length }}</strong> 种</span>
        <span>总数量: <strong>{{ totalQuantity }}</strong> 件</span>
        <span>总金额: <strong style="color: #f56c6c;">¥{{ totalAmount.toFixed(2) }}</strong></span>
      </div>

      <div class="confirm-remark" v-if="remark">
        <span>备注: {{ remark }}</span>
      </div>

      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleSubmit" :loading="submitting">确认结算</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { batchAddInport } from '@/api/inport'
import { loadAllProviderForSelect } from '@/api/provider'
import { loadGoodsByProviderId } from '@/api/goods'
import { batchInportSerialNumbers } from '@/api/serialNumber'
import SerialNumberInput from '@/components/SerialNumberInput.vue'

interface GoodsItem {
  id: number
  goodsname: string
  price: number
  number: number
  providername?: string
}

interface CartItem {
  goodsid: number
  goodsname: string
  providername: string
  inportprice: number
  number: number
}

const providers = ref<any[]>([])
const allGoodsList = ref<GoodsItem[]>([])
const selectedProvider = ref<number | null>(null)
const searchKeyword = ref('')
const cartItems = ref<CartItem[]>([])
const paytype = ref('现金')
const remark = ref('')
const submitting = ref(false)

// 序列号相关
const serialInputRef = ref()
const serialNumbersMap = ref<Map<number, string[]>>(new Map())
const currentSerialGoodsId = ref<number | null>(null)

// 分页相关
const goodsLoading = ref(false)
const currentPage = ref(1)
const pageSize = 30

// 购物车总数量
const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.number, 0)
})

// 购物车总金额
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.inportprice * item.number, 0)
})

// 过滤并排序后的商品列表（根据搜索关键词，库存少的优先）
const filteredGoodsList = computed(() => {
  let list = [...allGoodsList.value]

  // 搜索过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    list = list.filter(goods => goods.goodsname.toLowerCase().includes(keyword))
  }

  // 按库存排序（库存少的优先）
  list.sort((a, b) => a.number - b.number)

  return list
})

// 分页后的商品列表
const pagedGoodsList = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filteredGoodsList.value.slice(start, end)
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(filteredGoodsList.value.length / pageSize)
})

// 搜索输入
function onSearchInput() {
  currentPage.value = 1
}

// 页码变化
function handlePageChange(page: number) {
  currentPage.value = page
}

// 供应商变化时加载商品
async function onProviderChange(newVal: number | null) {
  // 如果购物车有数据，提示用户
  if (cartItems.value.length > 0) {
    try {
      await ElMessageBox.confirm(
        '切换供应商将清空当前进货清单，是否继续？',
        '提示',
        { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
      )
      // 用户确认，清空购物车并加载新供应商的商品
      cartItems.value = []
      currentPage.value = 1
      loadGoods()
    } catch {
      // 用户取消，恢复原来的供应商选择
      // 需要找到上一个供应商的ID
      const oldProvider = providers.value.find(p => p.id !== newVal)
      if (oldProvider) {
        selectedProvider.value = oldProvider.id
      }
    }
  } else {
    // 购物车为空，直接加载
    currentPage.value = 1
    loadGoods()
  }
}

// 添加到购物车
function addToCart(goods: GoodsItem) {
  const existing = cartItems.value.find(item => item.goodsid === goods.id)
  if (existing) {
    existing.number++
  } else {
    cartItems.value.push({
      goodsid: goods.id,
      goodsname: goods.goodsname,
      providername: goods.providername || '',
      inportprice: goods.price,
      number: 1
    })
  }
}

// 数量变化回调
function onQtyChange(index: number, val: number) {
  if (val <= 0) {
    cartItems.value.splice(index, 1)
  }
}

// 从购物车移除
function removeFromCart(index: number) {
  cartItems.value.splice(index, 1)
}

// 清空购物车
function clearCart() {
  cartItems.value = []
}

// 确认进货弹窗
const confirmDialogVisible = ref(false)

// 打开确认弹窗
function handleConfirm() {
  if (!selectedProvider.value) {
    ElMessage.warning('请先选择供应商')
    return
  }

  if (cartItems.value.length === 0) {
    ElMessage.warning('进货清单为空')
    return
  }

  confirmDialogVisible.value = true
}

// 序列号确认回调
function handleSerialConfirm(serialNumbers: string[]) {
  if (currentSerialGoodsId.value !== null) {
    serialNumbersMap.value.set(currentSerialGoodsId.value, serialNumbers)
  }
}

// 检查是否需要录入序列号
function checkSerialNumbers(): boolean {
  // 检查购物车中有序列号管理的商品
  const serialGoods = cartItems.value.filter(item => {
    const goods = allGoodsList.value.find(g => g.id === item.goodsid)
    return goods && (goods as any).isSerialManaged === 1
  })

  // 检查是否所有序列号管理商品都已录入序列号
  for (const item of serialGoods) {
    const serials = serialNumbersMap.value.get(item.goodsid)
    if (!serials || serials.length !== item.number) {
      return false
    }
  }
  return true
}

// 打开序列号录入
function openSerialInput(goodsId: number, count: number) {
  currentSerialGoodsId.value = goodsId
  serialInputRef.value?.open(goodsId, count)
}

// 提交结算
async function handleSubmit() {
  // 检查序列号管理商品是否已录入序列号
  const serialGoods = cartItems.value.filter(item => {
    const goods = allGoodsList.value.find(g => g.id === item.goodsid)
    return goods && (goods as any).isSerialManaged === 1
  })

  for (const item of serialGoods) {
    const serials = serialNumbersMap.value.get(item.goodsid)
    if (!serials || serials.length !== item.number) {
      ElMessage.warning(`商品"${item.goodsname}"需要录入${item.number}个序列号`)
      openSerialInput(item.goodsid, item.number)
      return
    }
  }

  try {
    submitting.value = true
    const data = cartItems.value.map(item => ({
      providerid: selectedProvider.value,
      goodsid: item.goodsid,
      number: item.number,
      inportprice: item.inportprice,
      paytype: paytype.value,
      remark: remark.value
    }))

    const res: any = await batchAddInport(data)
    if (res.code === 200) {
      // 处理序列号入库
      for (const item of serialGoods) {
        const serials = serialNumbersMap.value.get(item.goodsid)
        if (serials && serials.length > 0) {
          try {
            await batchInportSerialNumbers({
              goodsId: item.goodsid,
              serialNumbers: serials,
              inportId: res.data?.id || 0
            })
          } catch (e) {
            console.error('序列号入库失败:', e)
          }
        }
      }

      ElMessage.success('进货成功')
      confirmDialogVisible.value = false
      cartItems.value = []
      serialNumbersMap.value.clear()
      remark.value = ''
      loadGoods()
    }
  } catch (error) {
    console.error('进货失败:', error)
  } finally {
    submitting.value = false
  }
}

// 根据供应商加载商品列表
async function loadGoods() {
  if (!selectedProvider.value) {
    allGoodsList.value = []
    return
  }

  goodsLoading.value = true
  try {
    const res: any = await loadGoodsByProviderId(selectedProvider.value, 1)
    allGoodsList.value = res.data || []
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    goodsLoading.value = false
  }
}

onMounted(async () => {
  try {
    const [pRes] = await Promise.all([loadAllProviderForSelect()])
    providers.value = (pRes as any).data || []
    // 默认选择第一个供应商
    if (providers.value.length > 0) {
      selectedProvider.value = providers.value[0].id
      loadGoods()
    }
  } catch {}
})
</script>

<style scoped>
.inport-pos-page {
  height: calc(100vh - 120px);
  padding: 16px;
}

.full-height {
  height: 100%;
}

.left-panel,
.right-panel {
  height: 100%;
}

.panel-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.panel-card :deep(.el-card__header) {
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.panel-card :deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.search-bar {
  margin-bottom: 16px;
}

.goods-grid {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  align-content: start;
}

.goods-card {
  background: var(--el-fill-color-light);
  border: 2px solid transparent;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

[data-theme="dark"] .goods-card {
  background: #33302d;
}

.goods-card:hover {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

[data-theme="dark"] .goods-card:hover {
  background: rgba(244, 63, 94, 0.12);
}

.goods-card-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-card-price {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-color-danger);
  margin-bottom: 4px;
}

.goods-card-stock {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.goods-empty {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.cart-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
}

.cart-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-info {
  flex: 1;
}

.cart-item-name {
  font-size: 14px;
  color: var(--el-text-color-primary);
  margin-bottom: 2px;
}

.cart-item-provider {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 2px;
}

.cart-item-price {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.cart-item-right {
  text-align: right;
}

.cart-item-subtotal {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-color-danger);
  margin-bottom: 8px;
}

.cart-item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cart-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-paytype {
  margin-bottom: 16px;
}

.cart-remark {
  margin-bottom: 16px;
}

.cart-summary {
  border-top: 1px solid var(--el-border-color-light);
  padding-top: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.summary-row strong {
  color: var(--el-text-color-primary);
}

.summary-total {
  font-size: 14px;
  color: var(--el-text-color-regular);
  margin-bottom: 16px;
  text-align: right;
}

.total-price {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-color-danger);
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.confirm-header {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.confirm-header strong {
  color: #303133;
}

.confirm-goods-list {
  margin-bottom: 16px;
}

.confirm-summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

.confirm-summary strong {
  color: #303133;
}

.confirm-remark {
  margin-top: 12px;
  padding: 8px 12px;
  background-color: #fef0f0;
  border-radius: 4px;
  font-size: 14px;
  color: #909399;
}
</style>
