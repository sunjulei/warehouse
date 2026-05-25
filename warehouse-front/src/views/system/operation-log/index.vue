<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">操作日志</h1>
      <p class="page-header-desc">查看业务操作日志记录</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="操作类型">
          <el-select v-model="searchParams.type" placeholder="操作类型" clearable>
            <el-option label="添加" value="添加" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
          </el-select>
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="searchParams.module" placeholder="模块" clearable />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="searchParams.operateperson" placeholder="操作人" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllOperationLog" :search-params="searchParams">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="type" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="operateperson" label="操作人" width="100" />
        <el-table-column prop="operatetime" label="操作时间" width="170" />
      </CrudTable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadAllOperationLog } from '@/api/operationLog'

const tableRef = ref()

const searchParams = reactive({
  type: '',
  module: '',
  operateperson: ''
})

const getTagType = (type: string) => {
  const map: Record<string, string> = { '添加': 'success', '修改': 'warning', '删除': 'danger' }
  return map[type] || 'info'
}

const handleSearch = () => tableRef.value?.reload()

const handleReset = () => {
  searchParams.type = ''
  searchParams.module = ''
  searchParams.operateperson = ''
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
