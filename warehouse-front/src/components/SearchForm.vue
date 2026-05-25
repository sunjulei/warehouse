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
  padding: var(--spacing-lg);
  background: var(--bg-tertiary);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-md);
  border-left: 3px solid rgba(var(--primary-rgb), 0.6);
  position: relative;
  overflow: hidden;
}

.search-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 20% 50%, rgba(var(--primary-rgb), 0.03) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(var(--primary-rgb), 0.02) 0%, transparent 40%);
  pointer-events: none;
  z-index: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
  position: relative;
  z-index: 1;
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-primary);
  padding-right: var(--spacing-sm);
  font-size: var(--font-size-sm);
}

.search-form :deep(.el-input__wrapper),
.search-form :deep(.el-select__wrapper) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  background: var(--bg-primary);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.search-form :deep(.el-input__wrapper:hover),
.search-form :deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset, 0 2px 4px rgba(0, 0, 0, 0.04);
}

.search-form :deep(.el-input__wrapper:focus-within),
.search-form :deep(.el-select__wrapper:focus-within) {
  box-shadow: 0 0 0 1px var(--primary-color) inset, 0 0 0 3px rgba(var(--primary-rgb), 0.1) inset;
}

.search-actions {
  margin-left: auto;
  position: relative;
  z-index: 1;
}

.search-btn {
  background: var(--primary-gradient);
  border-color: var(--primary-color);
  box-shadow: 0 2px 6px rgba(var(--primary-rgb), 0.3);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 20px;
  font-weight: 500;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.4);
}

.search-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(var(--primary-rgb), 0.3);
}

.reset-btn {
  border-color: var(--border-color);
  color: var(--text-regular);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 20px;
  font-weight: 500;
}

.reset-btn:hover {
  color: var(--primary-color);
  border-color: var(--primary-light);
  background: rgba(var(--primary-rgb), 0.04);
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.reset-btn:active {
  transform: translateY(0);
}
</style>
