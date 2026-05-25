<template>
  <div class="tree-panel">
    <div class="tree-header">
      <el-icon class="tree-icon"><Folder /></el-icon>
      <span class="tree-title">{{ title }}</span>
    </div>
    <el-scrollbar class="tree-scrollbar">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="{ label: 'title', children: 'children' }"
        node-key="id"
        :default-expand-all="true"
        highlight-current
        @node-click="handleNodeClick"
        class="tree-content"
      />
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { TreeNode } from '@/types/api'

const props = defineProps<{
  title: string
  loadApi: () => Promise<any>
}>()

const emit = defineEmits(['node-click'])

const treeRef = ref()
const treeData = ref<TreeNode[]>([])

const handleNodeClick = (data: TreeNode) => {
  emit('node-click', data)
}

const loadData = async () => {
  try {
    const res: any = await props.loadApi()
    if (res.data) {
      treeData.value = res.data
    }
  } catch {}
}

onMounted(() => {
  loadData()
})

defineExpose({ loadData, treeRef })
</script>

<style scoped>
.tree-panel {
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-lg);
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
  overflow: hidden;
  position: relative;
}

.tree-panel:hover {
  box-shadow: var(--shadow-md);
  border-color: rgba(var(--primary-rgb), 0.2);
}

.tree-header {
  padding: var(--spacing-md) var(--spacing-lg);
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-tertiary) 100%);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
  position: relative;
}

.tree-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, rgba(var(--primary-rgb), 0.3) 0%, transparent 100%);
}

.tree-icon {
  color: var(--primary-color);
  font-size: 18px;
  transition: transform var(--transition-fast);
}

.tree-panel:hover .tree-icon {
  transform: scale(1.05);
}

.tree-title {
  font-weight: 600;
  font-size: var(--font-size-base);
  color: var(--text-primary);
  letter-spacing: 0.3px;
}

.tree-scrollbar {
  flex: 1;
  overflow: hidden;
}

.tree-content {
  padding: var(--spacing-sm);
  background: transparent;
}

:deep(.el-tree-node__content) {
  height: 38px;
  border-radius: var(--border-radius-md);
  margin: 2px 0;
  transition: all var(--transition-fast);
  padding-right: var(--spacing-sm);
  position: relative;
}

:deep(.el-tree-node__content:hover) {
  background-color: rgba(var(--primary-rgb), 0.06);
}

:deep(.el-tree-node__content:active) {
  background-color: rgba(var(--primary-rgb), 0.08);
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: rgba(var(--primary-rgb), 0.1);
  color: var(--primary-color);
  font-weight: 500;
  box-shadow: 0 0 0 1px rgba(var(--primary-rgb), 0.2) inset;
  position: relative;
}

:deep(.el-tree-node.is-current > .el-tree-node__content::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 0 2px 2px 0;
}

:deep(.el-tree-node__expand-icon) {
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  font-size: 14px;
}

:deep(.el-tree-node__expand-icon:hover) {
  color: var(--primary-color);
}

:deep(.el-tree-node__expand-icon.expanded) {
  transform: rotate(90deg);
  color: var(--primary-color);
}

:deep(.el-tree-node__label) {
  font-size: var(--font-size-sm);
  color: var(--text-regular);
  transition: color var(--transition-fast);
}

:deep(.el-tree-node.is-current > .el-tree-node__content .el-tree-node__label) {
  color: var(--primary-color);
}

:deep(.el-tree-node__children) {
  padding-left: var(--spacing-sm);
}
</style>
