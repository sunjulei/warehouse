<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">公告管理</h1>
      <p class="page-header-desc">管理系统公告和通知</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="标题">
          <el-input v-model="searchParams.title" placeholder="标题" clearable />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="searchParams.opername" placeholder="操作人" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllNotice" :search-params="searchParams" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="opername" label="操作人" width="120" />
        <el-table-column prop="createtime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #toolbar>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 添加
          </el-button>
          <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon> 批量删除
          </el-button>
        </template>
      </CrudTable>
    </el-card>

    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="rules" width="600px" @success="tableRef?.reload()">
      <template #default="{ formData }">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="6" />
        </el-form-item>
      </template>
    </CrudDialog>

    <el-dialog v-model="viewDialogVisible" title="公告详情" width="600px" class="notice-view-dialog">
      <div class="notice-view-header">
        <h3 class="notice-view-title">{{ viewNotice.title }}</h3>
        <div class="notice-view-meta">
          <el-icon><Clock /></el-icon>
          <span>{{ viewNotice.createtime }}</span>
          <span class="meta-divider">·</span>
          <el-icon><User /></el-icon>
          <span>{{ viewNotice.opername }}</span>
        </div>
      </div>
      <el-divider />
      <div class="notice-view-content" v-html="viewNotice.content"></div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadAllNotice, addNotice, updateNotice, deleteNotice, batchDeleteNotice } from '@/api/notice'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const selectedRows = ref<any[]>([])
const viewDialogVisible = ref(false)
const viewNotice = ref<any>({})

const searchParams = reactive({ title: '', opername: '' })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }] }

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.title = ''; searchParams.opername = '' }
const handleSelectionChange = (rows: any[]) => { selectedRows.value = rows }
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleView = (row: any) => { viewNotice.value = row; viewDialogVisible.value = true }
const handleSubmitApi = (data: any) => isEdit.value ? updateNotice(data) : addNotice(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  await deleteNotice(row.id)
  tableRef.value?.reload()
}
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确认删除 ${selectedRows.value.length} 条公告？`, '提示', { type: 'warning' })
  const ids = selectedRows.value.map((r: any) => r.id)
  await batchDeleteNotice(ids)
  tableRef.value?.reload()
}
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

.notice-view-header {
  padding: var(--spacing-sm) 0;
}

.notice-view-title {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm);
}

.notice-view-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.meta-divider {
  margin: 0 var(--spacing-xs);
  color: var(--text-placeholder);
}

:deep(.el-divider) {
  margin: var(--spacing-md) 0;
}

.notice-view-content {
  line-height: 1.8;
  color: var(--text-regular);
  font-size: var(--font-size-base);
}

.notice-view-content :deep(img) {
  max-width: 100%;
  border-radius: var(--border-radius-md);
}

.notice-view-content :deep(p) {
  margin: var(--spacing-sm) 0;
}
</style>
