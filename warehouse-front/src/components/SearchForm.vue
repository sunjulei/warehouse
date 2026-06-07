<template>
  <el-form :model="modelValue" inline @submit.prevent class="search-form">
    <slot />
    <el-form-item class="search-actions">
      <el-button type="primary" @click="$emit('search')" class="search-btn">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button @click="$emit('reset')" class="reset-btn">
        <el-icon><Refresh /></el-icon> 重置
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: Record<string, any>
}>()

defineEmits(['search', 'reset'])
</script>

<style scoped>
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: var(--spacing-md);
  padding: var(--spacing-xl) var(--spacing-2xl);
  background: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-lighter);
  position: relative;
}

.search-form::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: var(--spacing-2xl);
  right: var(--spacing-2xl);
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--border-color) 20%, var(--border-color) 80%, transparent 100%);
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-secondary);
  padding-right: var(--spacing-sm);
  font-size: var(--font-size-sm);
}

.search-actions {
  margin-left: auto;
}

.search-btn {
  background: var(--primary-gradient);
  border: none;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 22px;
  font-weight: 500;
  border-radius: var(--border-radius-md);
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px var(--primary-shadow);
}

.reset-btn {
  border-color: var(--border-color);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 22px;
  font-weight: 500;
  border-radius: var(--border-radius-md);
}

.reset-btn:hover {
  color: var(--primary-color);
  border-color: var(--primary-light);
  background: var(--primary-subtle);
}
</style>
