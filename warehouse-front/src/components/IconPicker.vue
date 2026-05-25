<template>
  <el-popover :visible="visible" placement="bottom-start" :width="400" trigger="click" @update:visible="visible = $event">
    <template #reference>
      <el-input :model-value="modelValue" placeholder="点击选择图标" readonly clearable @clear="$emit('update:modelValue', '')">
        <template #prefix>
          <el-icon v-if="modelValue"><component :is="modelValue" /></el-icon>
        </template>
      </el-input>
    </template>
    <div class="icon-picker-popover">
      <el-input v-model="keyword" placeholder="搜索图标" clearable size="small" style="margin-bottom: 8px;" />
      <div class="icon-picker-grid">
        <div
          v-for="name in filteredIcons"
          :key="name"
          class="icon-picker-item"
          :class="{ active: name === modelValue }"
          @click="handleSelect(name)"
        >
          <el-icon :size="20"><component :is="name" /></el-icon>
        </div>
      </div>
      <el-empty v-if="filteredIcons.length === 0" description="没有匹配的图标" :image-size="40" />
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

defineProps<{ modelValue: string }>()
const emit = defineEmits<{ 'update:modelValue': [value: string] }>()

const visible = ref(false)
const keyword = ref('')
const allIcons = Object.keys(ElementPlusIconsVue).sort()

const filteredIcons = computed(() => {
  if (!keyword.value) return allIcons
  const kw = keyword.value.toLowerCase()
  return allIcons.filter(name => name.toLowerCase().includes(kw))
})

const handleSelect = (name: string) => {
  emit('update:modelValue', name)
  visible.value = false
  keyword.value = ''
}
</script>

<style scoped>
.icon-picker-popover {
  max-height: 320px;
}
.icon-picker-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 4px;
  max-height: 260px;
  overflow-y: auto;
}
.icon-picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid transparent;
}
.icon-picker-item:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
  border-color: #409eff;
}
.icon-picker-item.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
</style>
