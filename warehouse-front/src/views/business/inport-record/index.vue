<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="订单号">
          <el-input v-model="searchParams.orderNo" placeholder="输入订单号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="searchParams.providerid" placeholder="全部" clearable filterable style="width: 160px">
            <el-option v-for="p in providers" :key="p.id" :label="p.providername" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchParams.recordType" placeholder="全部" clearable style="width: 120px">
            <el-option label="进货" value="inport" />
            <el-option label="加货" value="add" />
            <el-option label="退货" value="return" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadReturnAddRecords" :search-params="searchParams">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '退货' ? 'info' : row.typeTag" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="providerName" label="供应商" width="120" />
        <el-table-column prop="goodsName" label="商品名称" />
        <el-table-column prop="goodsSize" label="规格" width="100" />
        <el-table-column prop="number" label="数量" width="80" align="center" />
        <el-table-column prop="price" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operatePerson" label="操作员" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="160" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </CrudTable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadReturnAddRecords } from '@/api/inport'
import { loadAllProviderForSelect } from '@/api/provider'

const tableRef = ref()
const providers = ref<any[]>([])
const searchParams = reactive({
  orderNo: null as string | null,
  providerid: null as number | null,
  recordType: null as string | null
})

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.orderNo = null
  searchParams.providerid = null
  searchParams.recordType = null
}

onMounted(async () => {
  try {
    const [pRes] = await Promise.all([loadAllProviderForSelect()])
    providers.value = (pRes as any).data || []
  } catch {}
})
</script>

<style scoped>
</style>
