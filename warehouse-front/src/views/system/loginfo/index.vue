<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">日志管理</h1>
      <p class="page-header-desc">查看系统操作日志和记录</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="登录名">
          <el-input v-model="searchParams.loginname" placeholder="登录名" clearable />
        </el-form-item>
        <el-form-item label="IP">
          <el-input v-model="searchParams.loginip" placeholder="IP" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllLoginfo" :search-params="searchParams" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="loginname" label="登录名" />
        <el-table-column prop="loginip" label="IP" />
        <el-table-column prop="logintime" label="登录时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #toolbar>
          <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon> 批量删除
          </el-button>
        </template>
      </CrudTable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadAllLoginfo, deleteLoginfo, batchDeleteLoginfo } from '@/api/loginfo'

const tableRef = ref()
const selectedRows = ref<any[]>([])
const searchParams = reactive({ loginname: '', loginip: '' })

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.loginname = ''; searchParams.loginip = '' }
const handleSelectionChange = (rows: any[]) => { selectedRows.value = rows }
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  await deleteLoginfo(row.id)
  tableRef.value?.reload()
}
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确认删除 ${selectedRows.value.length} 条日志？`, '提示', { type: 'warning' })
  const ids = selectedRows.value.map((r: any) => r.id)
  await batchDeleteLoginfo(ids)
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
</style>
