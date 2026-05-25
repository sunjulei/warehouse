<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="供应商">
          <el-select v-model="searchParams.providerid" placeholder="全部" clearable filterable>
            <el-option v-for="p in providers" :key="p.id" :label="p.providername" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="searchParams.goodsid" placeholder="全部" clearable filterable>
            <el-option v-for="g in goodsList" :key="g.id" :label="g.goodsname" :value="g.id" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllOutport" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="providername" label="供应商" />
        <el-table-column prop="goodsname" label="商品" />
        <el-table-column prop="number" label="数量" width="80" />
        <el-table-column prop="outportprice" label="退货价" width="80" />
        <el-table-column prop="outporttime" label="退货时间" width="160" />
        <el-table-column prop="operateperson" label="操作员" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link @click="handleCancel(row)">取消</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
import { loadAllOutport, deleteOutport, cancelOutport } from '@/api/outport'
import { loadAllProviderForSelect } from '@/api/provider'
import { loadAllGoodsForSelect } from '@/api/goods'

const tableRef = ref()
const providers = ref<any[]>([])
const goodsList = ref<any[]>([])
const searchParams = reactive({ providerid: null as number | null, goodsid: null as number | null })

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.providerid = null; searchParams.goodsid = null }
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除？删除后不会回滚库存。', '提示', { type: 'warning' })
  await deleteOutport(row.id)
  tableRef.value?.reload()
}
const handleCancel = async (row: any) => {
  await ElMessageBox.confirm('确认取消？取消将回滚商品库存。', '提示', { type: 'warning' })
  const res: any = await cancelOutport(row.id)
  if (res.code === 200) {
    ElMessage.success('取消成功')
    tableRef.value?.reload()
  }
}

onMounted(async () => {
  try {
    const [pRes, gRes] = await Promise.all([loadAllProviderForSelect(), loadAllGoodsForSelect()])
    providers.value = (pRes as any).data || []
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
