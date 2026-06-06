<template>
  <el-popover
    placement="bottom-end"
    :width="280"
    trigger="click"
    :show-arrow="false"
    :offset="8"
    popper-class="theme-settings-popover"
  >
    <template #reference>
      <button class="settings-trigger" title="主题设置">
        <el-icon :size="18"><Setting /></el-icon>
      </button>
    </template>

    <div class="settings-panel">
      <!-- 日夜模式 -->
      <div class="settings-section">
        <div class="section-label">外观</div>
        <div class="mode-toggle">
          <button
            class="mode-btn"
            :class="{ active: themeStore.mode === 'light' }"
            @click="themeStore.setMode('light')"
          >
            <el-icon :size="16"><Sunny /></el-icon>
            <span>浅色</span>
          </button>
          <button
            class="mode-btn"
            :class="{ active: themeStore.mode === 'dark' }"
            @click="themeStore.setMode('dark')"
          >
            <el-icon :size="16"><Moon /></el-icon>
            <span>深色</span>
          </button>
        </div>
      </div>

      <!-- 布局切换 -->
      <div class="settings-section">
        <div class="section-label">布局</div>
        <div class="mode-toggle">
          <button
            class="mode-btn"
            :class="{ active: themeStore.layout === 'sidebar' }"
            @click="themeStore.setLayout('sidebar')"
          >
            <el-icon :size="16"><Menu /></el-icon>
            <span>侧边栏</span>
          </button>
          <button
            class="mode-btn"
            :class="{ active: themeStore.layout === 'topmenu' }"
            @click="themeStore.setLayout('topmenu')"
          >
            <el-icon :size="16"><Grid /></el-icon>
            <span>顶部菜单</span>
          </button>
        </div>
      </div>

      <!-- 主题颜色 -->
      <div class="settings-section">
        <div class="section-label">主题色</div>
        <div class="color-grid">
          <button
            v-for="color in themeColors"
            :key="color.value"
            class="color-item"
            :class="{ active: themeStore.themeColor === color.value }"
            @click="themeStore.setThemeColor(color.value)"
          >
            <span class="color-circle" :style="{ background: color.hex }" />
            <span class="color-name">{{ color.label }}</span>
          </button>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/stores/theme'
import type { ThemeColor } from '@/stores/theme'

const themeStore = useThemeStore()

const themeColors: { value: ThemeColor; hex: string; label: string }[] = [
  { value: 'rose', hex: '#e11d48', label: '玫瑰红' },
  { value: 'indigo', hex: '#4f46e5', label: '靛蓝' },
  { value: 'emerald', hex: '#059669', label: '翡翠绿' },
  { value: 'navy', hex: '#1e3a5f', label: '藏青' }
]
</script>

<style>
/* Global popover styles — Element Plus teleports popover to body */
.theme-settings-popover {
  border-radius: var(--border-radius-md) !important;
  border: 1px solid var(--border-light) !important;
  box-shadow: var(--shadow-xl) !important;
  padding: 16px !important;
  background: var(--bg-primary) !important;
}
</style>

<style scoped>
.settings-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-sm);
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  outline: none;
  padding: 0;
}

.settings-trigger:hover {
  color: var(--primary-color);
  border-color: rgba(var(--primary-rgb), 0.3);
  background: var(--primary-subtle);
}

.settings-trigger:active {
  transform: scale(0.92);
}

.settings-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.settings-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.mode-toggle {
  display: flex;
  gap: 6px;
  background: var(--bg-tertiary);
  border-radius: var(--border-radius-sm);
  padding: 3px;
}

.mode-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 6px 0;
  border: none;
  border-radius: 4px;
  background: transparent;
  color: var(--text-secondary);
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  transition: all var(--transition-fast);
  outline: none;
}

.mode-btn:hover {
  color: var(--text-primary);
}

.mode-btn.active {
  background: var(--bg-primary);
  color: var(--primary-color);
  box-shadow: var(--shadow-xs);
  font-weight: 500;
}

.color-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.color-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border: 1px solid var(--border-lighter);
  border-radius: var(--border-radius-sm);
  background: var(--bg-primary);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  outline: none;
}

.color-item:hover {
  border-color: var(--border-color);
  background: var(--bg-tertiary);
}

.color-item.active {
  border-color: var(--primary-color);
  background: var(--primary-subtle);
}

.color-circle {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  flex-shrink: 0;
}

.color-item.active .color-circle {
  box-shadow: 0 0 0 2px var(--bg-primary), 0 0 0 3.5px var(--primary-color);
}

.color-name {
  font-size: 13px;
  color: var(--text-primary);
}
</style>
