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
            <el-option v-for="g in goodsList" :key="g.id" :label="`${g.goodsname}${g.providername ? ' (' + g.providername + ')' : ''}`" :value="g.id" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllSales" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customername" label="客户" />
        <el-table-column prop="goodsname" label="商品" />
        <el-table-column prop="number" label="数量" width="80" />
        <el-table-column prop="saleprice" label="售价" width="80" />
        <el-table-column prop="salestime" label="销售时间" width="160" />
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
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 添加
          </el-button>
        </template>
      </CrudTable>
    </el-card>

    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="rules" width="500px" @success="tableRef?.reload()">
      <template #default="{ formData }">
        <el-form-item label="客户" prop="customerid">
          <el-select v-model="formData.customerid" placeholder="选择客户" filterable>
            <el-option v-for="c in customers" :key="c.id" :label="c.customername" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="goodsid">
          <el-select v-model="formData.goodsid" placeholder="选择商品" filterable>
            <el-option v-for="g in goodsList" :key="g.id" :label="`${g.goodsname}${g.providername ? ' (' + g.providername + ')' : ''}`" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="number">
          <el-input-number v-model="formData.number" :min="1" />
        </el-form-item>
        <el-form-item label="售价" prop="saleprice">
          <el-input-number v-model="formData.saleprice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </template>
    </CrudDialog>

    <el-dialog v-model="backDialogVisible" title="销售退货" width="400px">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadAllSales, addSales, updateSales, deleteSales } from '@/api/sales'
import { addSalesback } from '@/api/salesback'
import { loadAllCustomerForSelect } from '@/api/customer'
import { loadAllGoodsForSelect } from '@/api/goods'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const customers = ref<any[]>([])
const goodsList = ref<any[]>([])
const backDialogVisible = ref(false)
const backForm = reactive({ id: 0, number: 1, remark: '' })

const searchParams = reactive({ customerid: null as number | null, goodsid: null as number | null })

const rules = {
  customerid: [{ required: true, message: '请选择客户', trigger: 'change' }],
  goodsid: [{ required: true, message: '请选择商品', trigger: 'change' }],
  number: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  saleprice: [{ required: true, message: '请输入售价', trigger: 'blur' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.customerid = null; searchParams.goodsid = null }
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateSales(data) : addSales(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除？删除后将同时删除该销售单的所有退货记录。', '提示', { type: 'warning' })
  await deleteSales(row.id)
  tableRef.value?.reload()
}
const handleBack = (row: any) => {
  backForm.id = row.id
  backForm.number = 1
  backForm.remark = ''
  backDialogVisible.value = true
}
const handleBackSubmit = async () => {
  const res: any = await addSalesback(backForm.id, backForm.number, backForm.remark)
  if (res.code === 200) {
    ElMessage.success('退货成功')
    backDialogVisible.value = false
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
