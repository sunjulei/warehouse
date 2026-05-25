<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">图标管理</h1>
      <p class="page-header-desc">查看可用图标库</p>
    </div>
    <el-card>
      <template #header>
        <div style="display:flex;align-items:center;justify-content:space-between">
          <span>Element Plus 图标一览</span>
          <el-input v-model="keyword" placeholder="搜索图标" clearable style="width:240px" prefix-icon="Search" />
        </div>
      </template>
      <div class="icon-grid">
        <div v-for="name in filteredIcons" :key="name" class="icon-item" @click="handleCopy(name)">
          <el-icon :size="28"><component :is="name" /></el-icon>
          <span class="icon-name">{{ name }}</span>
        </div>
      </div>
      <el-empty v-if="filteredIcons.length === 0" description="没有匹配的图标" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const allIcons = Object.keys(ElementPlusIconsVue).sort()
const keyword = ref('')

const filteredIcons = computed(() => {
  if (!keyword.value) return allIcons
  const kw = keyword.value.toLowerCase()
  return allIcons.filter(name => name.toLowerCase().includes(kw))
})

const handleCopy = (name: string) => {
  navigator.clipboard.writeText(name).then(() => {
    ElMessage.success(`已复制: ${name}`)
  }).catch(() => {
    ElMessage.info(name)
  })
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

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: var(--spacing-md);
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-sm);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  background: var(--bg-primary);
}

.icon-item:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(var(--primary-rgb), 0.04);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.icon-item:active {
  transform: translateY(0);
}

.icon-name {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  text-align: center;
  word-break: break-all;
  line-height: 1.3;
  transition: color var(--transition-fast);
}

.icon-item:hover .icon-name {
  color: var(--primary-color);
}
</style>
