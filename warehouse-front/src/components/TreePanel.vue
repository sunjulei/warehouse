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
}

.tree-panel:hover {
  box-shadow: var(--shadow-md);
}

.tree-header {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.tree-icon {
  color: var(--primary-color);
  font-size: 18px;
}

.tree-title {
  font-weight: 600;
  font-size: var(--font-size-base);
  color: var(--text-primary);
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
  height: 36px;
  border-radius: var(--border-radius-sm);
  margin: 2px 0;
  transition: all var(--transition-fast);
}

:deep(.el-tree-node__content:hover) {
  background-color: var(--primary-subtle);
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: var(--primary-subtle);
  color: var(--primary-color);
  font-weight: 500;
  position: relative;
}

:deep(.el-tree-node.is-current > .el-tree-node__content::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
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
