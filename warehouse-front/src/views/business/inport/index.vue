<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">入库管理</h1>
      <p class="page-header-desc">管理商品入库记录</p>
    </div>
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

      <CrudTable ref="tableRef" :load-api="loadAllInport" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="providername" label="供应商" />
        <el-table-column prop="goodsname" label="商品" />
        <el-table-column prop="number" label="数量" width="80" />
        <el-table-column prop="inportprice" label="进货价" width="80" />
        <el-table-column prop="paytype" label="付款方式" width="100" />
        <el-table-column prop="inporttime" label="进货时间" width="160" />
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
        <el-form-item label="供应商" prop="providerid">
          <el-select v-model="formData.providerid" placeholder="选择供应商" filterable @change="handleProviderChange(formData)">
            <el-option v-for="p in providers" :key="p.id" :label="p.providername" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="goodsid">
          <el-select v-model="formData.goodsid" placeholder="选择商品" filterable :disabled="!formData.providerid">
            <el-option v-for="g in dialogGoodsList" :key="g.id" :label="g.goodsname" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="number">
          <el-input-number v-model="formData.number" :min="1" />
        </el-form-item>
        <el-form-item label="进货价" prop="inportprice">
          <el-input-number v-model="formData.inportprice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="付款方式" prop="paytype">
          <el-select v-model="formData.paytype" filterable>
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
    <el-dialog v-model="backDialogVisible" title="退货" width="400px">
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
import { loadAllInport, addInport, updateInport, deleteInport } from '@/api/inport'
import { addOutport } from '@/api/outport'
import { loadAllProviderForSelect } from '@/api/provider'
import { loadAllGoodsForSelect, loadGoodsByProviderId } from '@/api/goods'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const providers = ref<any[]>([])
const goodsList = ref<any[]>([])
const dialogGoodsList = ref<any[]>([])
const backDialogVisible = ref(false)
const backForm = reactive({ id: 0, number: 1, remark: '' })

const searchParams = reactive({ providerid: null as number | null, goodsid: null as number | null })

const rules = {
  providerid: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  goodsid: [{ required: true, message: '请选择商品', trigger: 'change' }],
  number: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  inportprice: [{ required: true, message: '请输入进货价', trigger: 'blur' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.providerid = null; searchParams.goodsid = null }
const handleAdd = () => { isEdit.value = false; dialogGoodsList.value = []; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; loadGoodsForProvider(row.providerid); dialogRef.value?.open(row, true) }
const handleProviderChange = (formData: any) => {
  formData.goodsid = null
  loadGoodsForProvider(formData.providerid)
}
const loadGoodsForProvider = async (providerid: number) => {
  if (!providerid) { dialogGoodsList.value = []; return }
  try {
    const res: any = await loadGoodsByProviderId(providerid, 1)
    dialogGoodsList.value = res.data || []
  } catch { dialogGoodsList.value = [] }
}
const handleSubmitApi = (data: any) => isEdit.value ? updateInport(data) : addInport(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除？删除后将同时删除该进货单的所有退货记录。', '提示', { type: 'warning' })
  await deleteInport(row.id)
  tableRef.value?.reload()
}
const handleBack = (row: any) => {
  backForm.id = row.id
  backForm.number = 1
  backForm.remark = ''
  backDialogVisible.value = true
}
const handleBackSubmit = async () => {
  const res: any = await addOutport(backForm.id, backForm.number, backForm.remark)
  if (res.code === 200) {
    ElMessage.success('退货成功')
    backDialogVisible.value = false
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
