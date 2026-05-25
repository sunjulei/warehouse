<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">缓存管理</h1>
      <p class="page-header-desc">管理系统缓存和性能</p>
    </div>
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <el-icon class="card-header-icon"><Coin /></el-icon>
            <span>缓存管理</span>
          </div>
          <div class="card-header-actions">
            <el-button type="primary" @click="handleSync">
              <el-icon><Refresh /></el-icon> 同步缓存
            </el-button>
            <el-button type="danger" @click="handleClearAll">
              <el-icon><Delete /></el-icon> 清空缓存
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="cacheData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="key" label="缓存键" />
        <el-table-column label="缓存值">
          <template #default="{ row }">
            <el-text truncated>{{ row.value }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loadAllCache, deleteCache, removeAllCache, syncCache } from '@/api/cache'

const loading = ref(false)
const cacheData = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await loadAllCache()
    const data = res.data || []
    cacheData.value = Object.keys(data).map(key => ({ key, value: JSON.stringify(data[key]) }))
  } catch {} finally {
    loading.value = false
  }
}

const handleSync = async () => {
  const res: any = await syncCache()
  if (res.code === 200) {
    ElMessage.success('同步成功')
    loadData()
  }
}

const handleClearAll = async () => {
  await ElMessageBox.confirm('确认清空所有缓存？', '提示', { type: 'warning' })
  const res: any = await removeAllCache()
  if (res.code === 200) {
    ElMessage.success('清空成功')
    loadData()
  }
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该缓存？', '提示', { type: 'warning' })
  const res: any = await deleteCache(row.key)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  }
}

onMounted(() => loadData())
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

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-weight: 600;
  font-size: var(--font-size-lg);
  color: var(--text-primary);
}

.card-header-icon {
  color: var(--primary-color);
  font-size: 20px;
}

.card-header-actions {
  display: flex;
  gap: var(--spacing-sm);
}

:deep(.el-table) {
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--bg-secondary) !important;
  color: var(--text-primary);
  font-weight: 600;
}

:deep(.el-table .el-table__row:hover > td) {
  background-color: rgba(var(--primary-rgb), 0.04) !important;
}
</style>
