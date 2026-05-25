<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">供应商管理</h1>
      <p class="page-header-desc">管理供应商信息和联系方式</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="供应商名">
          <el-input v-model="searchParams.providername" placeholder="供应商名" clearable />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="searchParams.connectionperson" placeholder="联系人" clearable />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="searchParams.phone" placeholder="电话" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllProvider" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="providername" label="供应商名" />
        <el-table-column prop="connectionperson" label="联系人" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="address" label="地址" />
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
        <el-form-item label="供应商名" prop="providername">
          <el-input v-model="formData.providername" />
        </el-form-item>
        <el-form-item label="联系人" prop="connectionperson">
          <el-input v-model="formData.connectionperson" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" />
        </el-form-item>
      </template>
    </CrudDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadAllProvider, addProvider, updateProvider, deleteProvider } from '@/api/provider'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)

const searchParams = reactive({
  providername: '',
  connectionperson: '',
  phone: ''
})

const rules = {
  providername: [{ required: true, message: '请输入供应商名', trigger: 'blur' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.providername = ''
  searchParams.connectionperson = ''
  searchParams.phone = ''
}
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateProvider(data) : addProvider(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该供应商？', '提示', { type: 'warning' })
  await deleteProvider(row.id)
  tableRef.value?.reload()
}
</script>
