<template>
  <div class="tabs-bar">
    <div class="tabs-scroll">
      <div
        v-for="tab in tabs"
        :key="tab.path"
        class="tab-item"
        :class="{ active: tab.path === route.path }"
        @click="router.push(tab.path)"
      >
        <span class="tab-dot" v-if="tab.path === route.path"></span>
        <span class="tab-title">{{ tab.title }}</span>
        <el-icon v-if="tab.path !== '/dashboard'" class="tab-close" @click.stop="closeTab(tab.path)">
          <Close />
        </el-icon>
      </div>
    </div>
    <el-dropdown trigger="click" @command="handleCommand" class="tabs-action">
      <el-icon class="tabs-action-icon"><ArrowDown /></el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="closeOther">关闭其他</el-dropdown-item>
          <el-dropdown-item command="closeAll">关闭全部</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface Tab {
  path: string
  title: string
}

const route = useRoute()
const router = useRouter()

const STORAGE_KEY = 'tabs_visited'
const loadTabs = (): Tab[] => {
  try {
    return JSON.parse(sessionStorage.getItem(STORAGE_KEY) || '[]')
  } catch {
    return []
  }
}

const tabs = ref<Tab[]>(loadTabs())

if (!tabs.value.find(t => t.path === '/dashboard')) {
  tabs.value.unshift({ path: '/dashboard', title: '首页' })
}

const saveTabs = () => {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(tabs.value))
}

const addTab = () => {
  const title = (route.meta?.title as string) || ''
  if (!title || route.path === '/login') return
  if (!tabs.value.find(t => t.path === route.path)) {
    tabs.value.push({ path: route.path, title })
    saveTabs()
  }
}

const closeTab = (path: string) => {
  const idx = tabs.value.findIndex(t => t.path === path)
  if (idx === -1) return
  tabs.value.splice(idx, 1)
  saveTabs()
  if (route.path === path) {
    const next = tabs.value[idx] || tabs.value[tabs.value.length - 1]
    if (next) router.push(next.path)
  }
}

const handleCommand = (command: string) => {
  if (command === 'closeOther') {
    tabs.value = tabs.value.filter(t => t.path === '/dashboard' || t.path === route.path)
    saveTabs()
  } else if (command === 'closeAll') {
    tabs.value = tabs.value.filter(t => t.path === '/dashboard')
    saveTabs()
    if (route.path !== '/dashboard') router.push('/dashboard')
  }
}

watch(() => route.path, addTab, { immediate: true })
</script>

<style scoped>
.tabs-bar {
  display: flex;
  align-items: center;
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  padding: 0 8px;
  height: 36px;
  flex-shrink: 0;
  transition: background-color var(--transition-base), border-color var(--transition-base);
}

.tabs-scroll {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
}

.tabs-scroll::-webkit-scrollbar {
  display: none;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 0 12px;
  height: 26px;
  border-radius: 5px;
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: all 0.15s;
  border: 1px solid transparent;
  position: relative;
}

.tab-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-color);
  flex-shrink: 0;
  animation: pulse 2s ease-in-out infinite;
}

.tab-item:hover {
  color: var(--primary-color);
  background: var(--primary-subtle);
}

.tab-item.active {
  color: var(--primary-color);
  background: var(--primary-subtle);
  border-color: rgba(var(--primary-rgb), 0.2);
  font-weight: 500;
}

.tab-close {
  font-size: 12px;
  border-radius: 50%;
  padding: 1px;
  transition: all 0.15s;
}

.tab-close:hover {
  background: rgba(var(--primary-rgb), 0.15);
  color: var(--primary-color);
}

.tab-item.active .tab-close:hover {
  background: rgba(var(--primary-rgb), 0.2);
}

.tabs-action {
  margin-left: 4px;
  flex-shrink: 0;
}

.tabs-action-icon {
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.15s;
}

.tabs-action-icon:hover {
  color: var(--primary-color);
  background: var(--primary-subtle);
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
</style>
