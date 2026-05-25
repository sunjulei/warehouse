<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">商品分类</h1>
      <p class="page-header-desc">管理商品分类信息</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="分类名">
          <el-input v-model="searchParams.catename" placeholder="分类名" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllCategory" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="catename" label="分类名" />
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
        <el-form-item label="分类名" prop="catename">
          <el-input v-model="formData.catename" />
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
import { loadAllCategory, addCategory, updateCategory, deleteCategory } from '@/api/category'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)

const searchParams = reactive({
  catename: ''
})

const rules = {
  catename: [{ required: true, message: '请输入分类名', trigger: 'blur' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.catename = ''
}
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateCategory(data) : addCategory(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
  await deleteCategory(row.id)
  tableRef.value?.reload()
}
</script>
