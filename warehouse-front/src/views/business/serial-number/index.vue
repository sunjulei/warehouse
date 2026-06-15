<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">序列号管理</h1>
      <p class="page-header-desc">管理商品序列号，追踪在库、已售、已退状态</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="序列号">
          <el-input v-model="searchParams.serialNumber" placeholder="输入序列号" clearable />
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="searchParams.goodsid" placeholder="选择商品" clearable filterable>
            <el-option
              v-for="item in goodsList"
              :key="item.id"
              :label="item.goodsname"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="选择状态" clearable>
            <el-option label="在库" :value="0" />
            <el-option label="已售" :value="1" />
            <el-option label="已退" :value="2" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllSerialNumber" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="serialNumber" label="序列号" width="200" />
        <el-table-column prop="goodsname" label="商品名称" />
        <el-table-column prop="size" label="规格" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="instockTime" label="入库时间" width="180" />
        <el-table-column prop="outstockTime" label="出库时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="序列号" prop="serialNumber">
          <el-input v-model="formData.serialNumber" placeholder="请输入序列号" />
        </el-form-item>
        <el-form-item label="商品" prop="goodsid">
          <el-select v-model="formData.goodsid" placeholder="选择商品" filterable>
            <el-option
              v-for="item in goodsList"
              :key="item.id"
              :label="item.goodsname"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="备注信息" />
        </el-form-item>
      </template>
    </CrudDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadAllSerialNumber, addSerialNumber, updateSerialNumber, deleteSerialNumber } from '@/api/serialNumber'
import { loadAllGoodsForSelect } from '@/api/goods'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)

const goodsList = ref<any[]>([])

const searchParams = reactive({
  serialNumber: '',
  goodsid: null as number | null,
  status: null as number | null
})

const rules = {
  serialNumber: [{ required: true, message: '请输入序列号', trigger: 'blur' }],
  goodsid: [{ required: true, message: '请选择商品', trigger: 'change' }]
}

const statusLabel = (status: number) => {
  const map: Record<number, string> = { 0: '在库', 1: '已售', 2: '已退' }
  return map[status] ?? '未知'
}

const statusTagType = (status: number) => {
  const map: Record<number, string> = { 0: 'success', 1: 'info', 2: 'danger' }
  return map[status] ?? 'info'
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.serialNumber = ''
  searchParams.goodsid = null
  searchParams.status = null
}
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateSerialNumber(data) : addSerialNumber(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该序列号？', '提示', { type: 'warning' })
  await deleteSerialNumber(row.id)
  tableRef.value?.reload()
}

onMounted(async () => {
  try {
    const res = await loadAllGoodsForSelect()
    goodsList.value = res.data || []
  } catch (e) {
    console.error('加载商品列表失败:', e)
  }
})
</script>
