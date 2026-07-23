<template>
  <div class="crud-table">
    <div class="table-toolbar">
      <slot name="toolbar" />
    </div>
    <el-table
      ref="tableRef"
      :data="tableData"
      :border="true"
      :stripe="true"
      v-loading="loading"
      @selection-change="handleSelectionChange"
      class="data-table"
      style="width: 100%"
    >
      <slot />
    </el-table>
    <div class="table-pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.limit"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { DataGridView } from '@/types/api'

const props = defineProps<{
  loadApi: (params: any) => Promise<any>
  searchParams?: Record<string, any>
}>()

const emit = defineEmits(['selection-change'])

const tableRef = ref()
const tableData = ref<any[]>([])
const loading = ref(false)
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const selectedRows = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...props.searchParams
    }
    const res: DataGridView = await props.loadApi(params)
    tableData.value = res.data || []
    pagination.total = res.count || 0
  } catch {
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const reload = () => {
  pagination.page = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleSizeChange = () => {
  pagination.page = 1
  loadData()
}

const handleSelectionChange = (rows: any[]) => {
  selectedRows.value = rows
  emit('selection-change', rows)
}

// 首载一次；之后由 SearchForm 的 search/reset 事件显式调用 reload()，
// 不再 deep watch searchParams（否则搜索框每敲一个字就发一次请求）
onMounted(() => {
  loadData()
})

defineExpose({ reload, loadData, selectedRows, pagination })
</script>

<style scoped>
.crud-table {
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.table-toolbar {
  margin-bottom: var(--spacing-lg);
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  align-items: center;
  padding: var(--spacing-sm) 0;
}

.data-table {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: all var(--transition-base);
}

.table-pagination {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-md) 0;
  border-top: 1px solid var(--border-lighter);
}

:deep(.el-table__header-wrapper) {
  th {
    background-color: var(--bg-tertiary) !important;
    color: var(--text-secondary);
    font-weight: 600;
    font-size: var(--font-size-xs);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    padding: 12px 0;
    border-bottom: 1px solid var(--border-light);
  }
}

:deep(.el-table__body-wrapper) {
  .el-table__row {
    transition: all var(--transition-fast);

    &:hover > td {
      background-color: var(--primary-subtle) !important;
    }

    td {
      padding: 10px 0;
      transition: background-color var(--transition-fast);
    }
  }
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: var(--bg-tertiary);
}

:deep(.el-table--border) {
  border-color: var(--border-lighter);

  th.el-table__cell,
  td.el-table__cell {
    border-right-color: var(--border-lighter);
  }
}

:deep(.el-table__empty-block) {
  min-height: 200px;
}

:deep(.el-table__empty-text) {
  color: var(--text-placeholder);
  font-size: var(--font-size-base);
}
</style>
