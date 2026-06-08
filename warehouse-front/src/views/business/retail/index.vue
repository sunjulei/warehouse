<template>
  <div class="retail-pos-page">
    <el-row :gutter="16" class="full-height">
      <!-- 左侧商品选择区域 -->
      <el-col :span="14" class="left-panel">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span class="panel-title">商品选择</span>
              <el-tag type="info" size="large">散客零售</el-tag>
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
              v-for="goods in goodsList"
              :key="goods.id"
              class="goods-card"
              :class="{ 'goods-card-disabled': goods.number <= 0 }"
              @click="addToCart(goods)"
            >
              <div class="goods-card-name">{{ goods.goodsname }}</div>
              <div class="goods-card-provider" v-if="goods.providername">{{ goods.providername }}</div>
              <div class="goods-card-price">¥{{ goods.price?.toFixed(2) }}</div>
              <div class="goods-card-stock">库存: {{ goods.number }}</div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[30]"
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
              <span class="panel-title">购物车</span>
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
                <div class="cart-item-price">¥{{ item.retailprice?.toFixed(2) }}</div>
              </div>
              <div class="cart-item-right">
                <div class="cart-item-subtotal">¥{{ (item.retailprice * item.number).toFixed(2) }}</div>
                <div class="cart-item-actions">
                  <el-input-number
                    v-model="item.number"
                    :min="1"
                    :max="getMaxStock(item.goodsid)"
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
            <el-empty description="购物车为空" :image-size="80" />
          </div>

          <!-- 支付方式 -->
          <div class="cart-paytype" v-if="cartItems.length > 0">
            <span class="paytype-label">支付方式</span>
            <el-radio-group v-model="paytype">
              <el-radio-button value="现金">现金</el-radio-button>
              <el-radio-button value="微信">微信</el-radio-button>
              <el-radio-button value="支付宝">支付宝</el-radio-button>
              <el-radio-button value="银行卡">银行卡</el-radio-button>
            </el-radio-group>
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
              确认结算
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 确认结算弹窗 -->
    <el-dialog v-model="confirmDialogVisible" title="确认结算" width="600px">
      <div class="confirm-header">
        <span>支付方式: <strong>{{ paytype }}</strong></span>
      </div>

      <div class="confirm-goods-list">
        <el-table :data="cartItems" border style="width: 100%" max-height="400">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="goodsname" label="商品名称" />
          <el-table-column prop="providername" label="供应商" width="120" />
          <el-table-column prop="retailprice" label="售价" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.retailprice?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="number" label="数量" width="80" align="center" />
          <el-table-column label="小计" width="100" align="right">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600;">¥{{ (row.retailprice * row.number).toFixed(2) }}</span>
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
import { ElMessage } from 'element-plus'
import { batchAddRetail } from '@/api/retail'
import { loadGoodsForPOS } from '@/api/goods'

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
  retailprice: number
  number: number
}

const goodsList = ref<GoodsItem[]>([])
const searchKeyword = ref('')
const cartItems = ref<CartItem[]>([])
const remark = ref('')
const paytype = ref('现金')
const submitting = ref(false)

// 分页相关
const goodsLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(30)
const total = ref(0)

// 搜索防抖定时器
let searchTimer: number | null = null

// 购物车总数量
const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.number, 0)
})

// 购物车总金额
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.retailprice * item.number, 0)
})

// 搜索输入防抖
function onSearchInput() {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = window.setTimeout(() => {
    currentPage.value = 1
    loadGoods()
  }, 300)
}

// 页码变化
function handlePageChange(page: number) {
  currentPage.value = page
  loadGoods()
}

// 添加到购物车
function addToCart(goods: GoodsItem) {
  if (goods.number <= 0) {
    ElMessage.warning('该商品库存不足')
    return
  }

  const existing = cartItems.value.find(item => item.goodsid === goods.id)
  if (existing) {
    if (existing.number >= goods.number) {
      ElMessage.warning('已达到库存上限')
      return
    }
    existing.number++
  } else {
    cartItems.value.push({
      goodsid: goods.id,
      goodsname: goods.goodsname,
      providername: goods.providername || '',
      retailprice: goods.price,
      number: 1
    })
  }
}

// 获取商品最大库存
function getMaxStock(goodsid: number): number {
  const goods = goodsList.value.find(g => g.id === goodsid)
  return goods ? goods.number : 9999
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

// 确认结算弹窗
const confirmDialogVisible = ref(false)

// 打开确认弹窗
function handleConfirm() {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  confirmDialogVisible.value = true
}

// 提交结算
async function handleSubmit() {
  try {
    submitting.value = true
    const data = cartItems.value.map(item => ({
      goodsid: item.goodsid,
      number: item.number,
      retailprice: item.retailprice,
      paytype: paytype.value,
      remark: remark.value
    }))

    const res: any = await batchAddRetail(data)
    if (res.code === 200) {
      ElMessage.success('结算成功')
      confirmDialogVisible.value = false
      cartItems.value = []
      remark.value = ''
      // 重新加载商品库存
      loadGoods()
    }
  } catch (error) {
    console.error('结算失败:', error)
  } finally {
    submitting.value = false
  }
}

// 加载商品列表
async function loadGoods() {
  goodsLoading.value = true
  try {
    const res: any = await loadGoodsForPOS({
      page: currentPage.value,
      limit: pageSize.value,
      keyword: searchKeyword.value.trim() || undefined
    })
    goodsList.value = res.data || []
    total.value = res.count || 0
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    goodsLoading.value = false
  }
}

onMounted(() => {
  loadGoods()
})
</script>

<style scoped>
.retail-pos-page {
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

.goods-card-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.goods-card-disabled:hover {
  border-color: transparent;
  background: var(--el-fill-color-light);
}

[data-theme="dark"] .goods-card-disabled:hover {
  background: #33302d;
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

.goods-card-provider {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
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
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.paytype-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
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
