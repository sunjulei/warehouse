<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="客户">
          <el-select v-model="searchParams.customerid" placeholder="全部" clearable filterable>
            <el-option v-for="c in customers" :key="c.id" :label="c.customername" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="searchParams.goodsid" placeholder="全部" clearable filterable>
            <el-option v-for="g in goodsList" :key="g.id" :label="g.goodsname" :value="g.id" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllSalesback" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customername" label="客户" />
        <el-table-column prop="goodsname" label="商品" />
        <el-table-column prop="number" label="数量" width="80" />
        <el-table-column prop="salebackprice" label="退货价" width="80" />
        <el-table-column prop="salesbacktime" label="退货时间" width="160" />
        <el-table-column prop="operateperson" label="操作员" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </CrudTable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadAllSalesback, cancelSalesback } from '@/api/salesback'
import { loadAllCustomerForSelect } from '@/api/customer'
import { loadAllGoodsForSelect } from '@/api/goods'

const tableRef = ref()
const customers = ref<any[]>([])
const goodsList = ref<any[]>([])
const searchParams = reactive({ customerid: null as number | null, goodsid: null as number | null })

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.customerid = null; searchParams.goodsid = null }
const handleCancel = async (row: any) => {
  await ElMessageBox.confirm('确认取消？取消将回滚商品库存。', '提示', { type: 'warning' })
  const res: any = await cancelSalesback(row.id)
  if (res.code === 200) {
    ElMessage.success('取消成功')
    tableRef.value?.reload()
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
