<template>
  <div class="sidebar">
    <div class="sidebar-noise"></div>
    <div class="sidebar-logo">
      <div class="logo-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="logo-svg">
          <path d="M20 7L12 3L4 7V17L12 21L20 17V7Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
          <path d="M12 12L20 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
          <path d="M12 12V21" stroke="currentColor" stroke-width="1.5"/>
          <path d="M12 12L4 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
          <path d="M8 5L16 9" stroke="currentColor" stroke-width="1" opacity="0.5"/>
        </svg>
      </div>
      <transition name="fade">
        <span v-if="!isCollapse" class="logo-text">仓储脉搏</span>
      </transition>
    </div>
    <el-scrollbar class="sidebar-scrollbar">
      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/dashboard" class="menu-item">
          <el-icon><HomeFilled /></el-icon>
          <template #title>控制台</template>
        </el-menu-item>

        <template v-for="menu in authStore.menus" :key="menu.id">
          <el-menu-item
            v-if="menu.href"
            :index="menu.href"
            class="menu-item"
          >
            <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
          <el-sub-menu
            v-else-if="menu.children && menu.children.length > 0"
            :index="menu.id + ''"
            class="sub-menu"
          >
            <template #title>
              <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.id"
              :index="child.href"
              class="menu-item"
            >
              <el-icon v-if="isElementIcon(child.icon)"><component :is="child.icon" /></el-icon>
              <template #title>{{ child.title }}</template>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item
            v-else
            :index="menu.href"
            class="menu-item"
          >
            <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
    <div class="sidebar-bottom">
      <transition name="fade">
        <div v-if="!isCollapse" class="sidebar-footer">
          <span class="version-text">v1.3.0</span>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

defineProps<{ isCollapse: boolean }>()

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const currentRoute = computed(() => route.path)

const isElementIcon = (icon: string | undefined | null): boolean => {
  if (!icon) return false
  return /^[A-Z][a-zA-Z]*$/.test(icon)
}

const handleMenuSelect = (index: string) => {
  if (index && index !== route.path) {
    router.push(index)
  }
}
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.sidebar-logo {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 600;
  padding: 0 16px;
  flex-shrink: 0;
  border-bottom: 1px solid var(--border-light);
  transition: all var(--transition-base);
  position: relative;
  z-index: 2;
  letter-spacing: 1px;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: var(--primary-gradient);
  border-radius: var(--border-radius-sm);
  flex-shrink: 0;
  transition: all var(--transition-base);
}

.logo-svg {
  width: 18px;
  height: 18px;
  color: #fff;
}

.sidebar-logo:hover .logo-icon {
  transform: scale(1.05);
}

.logo-text {
  white-space: nowrap;
  overflow: hidden;
  color: var(--text-primary);
  letter-spacing: 2px;
}

.sidebar-scrollbar {
  flex: 1;
  overflow: hidden;
  position: relative;
  z-index: 2;
}

.sidebar-menu {
  border-right: none;
  padding: 8px;
  background: transparent;
}

.menu-item {
  margin: 2px 0;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-fast);
  height: 36px;
  line-height: 36px;
  position: relative;
  overflow: hidden;
  font-size: var(--font-size-base);
}

.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 3px;
  height: 55%;
  background: var(--primary-gradient);
  border-radius: 0 2px 2px 0;
  transition: transform var(--transition-fast);
}

.menu-item:hover {
  background: var(--sidebar-hover) !important;
}

.menu-item.is-active {
  background: var(--sidebar-active) !important;
  color: var(--sidebar-text-active) !important;
  font-weight: 500;
}

.menu-item.is-active::before {
  transform: translateY(-50%) scaleY(1);
}

.sub-menu {
  margin: 2px 0;
}

:deep(.el-sub-menu__title) {
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-fast);
  height: 36px;
  line-height: 36px;
  position: relative;
  font-size: var(--font-size-base);
}

:deep(.el-sub-menu__title:hover) {
  background: var(--sidebar-hover) !important;
}

:deep(.el-menu--collapse .el-sub-menu__title span),
:deep(.el-menu--collapse .el-sub-menu__title .el-sub-menu__icon-arrow) {
  display: none;
}

:deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 20px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: var(--sidebar-text);
}

:deep(.el-menu-item.is-active) {
  color: var(--sidebar-text-active);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  color: var(--sidebar-text-active);
}

:deep(.el-sub-menu .el-menu) {
  background: var(--bg-tertiary) !important;
  border-radius: var(--border-radius-sm);
  margin: 4px 8px;
}

:deep(.el-sub-menu .el-menu .el-menu-item) {
  padding-left: 48px !important;
  height: 34px;
  line-height: 34px;
  font-size: 13px;
}

.sidebar-bottom {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  border-top: 1px solid var(--border-light);
}

.sidebar-footer {
  padding: 8px 16px 10px;
  text-align: center;
}

.version-text {
  font-size: 11px;
  color: var(--text-placeholder);
  letter-spacing: 0.5px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-fast);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

</style>
