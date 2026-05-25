<template>
  <div class="sidebar">
    <div class="sidebar-noise"></div>
    <div class="sidebar-logo">
      <div class="logo-icon">
        <el-icon :size="isCollapse ? 24 : 28"><Box /></el-icon>
      </div>
      <transition name="fade">
        <span v-if="!isCollapse" class="logo-text">仓库管理系统</span>
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
          <template #title>后台首页</template>
        </el-menu-item>

        <template v-for="menu in authStore.menus" :key="menu.id">
          <!-- 有 href 的菜单项：提升为顶层可点击项 -->
          <el-menu-item
            v-if="menu.href"
            :index="menu.href"
            class="menu-item"
          >
            <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
          <!-- 无 href 的菜单项：作为分组，展示子菜单 -->
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
          <!-- 无 href 无子菜单：作为普通菜单项 -->
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
    <transition name="fade">
      <div v-if="!isCollapse" class="sidebar-footer">
        <span class="version-text">v1.0.0</span>
      </div>
    </transition>
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

// Element Plus 图标组件名都是 PascalCase（如 Menu、User），旧数据库存储的是 LayUI 格式
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

.sidebar-noise {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 1;
}

.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 0 16px;
  flex-shrink: 0;
  transition: all var(--transition-base);
  position: relative;
  z-index: 2;
}

.sidebar-logo::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  background: var(--primary-gradient);
  border-radius: var(--border-radius-md);
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.4), 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  flex-shrink: 0;
  transition: all var(--transition-base);
}

.sidebar-logo:hover .logo-icon {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(var(--primary-rgb), 0.5), 0 0 0 1px rgba(255, 255, 255, 0.15) inset;
}

.logo-text {
  white-space: nowrap;
  overflow: hidden;
  background: linear-gradient(135deg, #fff 0%, #c7d2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
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
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  height: 44px;
  line-height: 44px;
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 3px;
  height: 60%;
  background: var(--primary-gradient);
  border-radius: 0 2px 2px 0;
  transition: transform var(--transition-fast);
}

.menu-item:hover {
  background: var(--sidebar-hover) !important;
  backdrop-filter: blur(8px);
}

.menu-item.is-active {
  background: var(--sidebar-active) !important;
  color: var(--sidebar-text-active) !important;
  font-weight: 500;
  box-shadow: 0 0 12px rgba(var(--primary-rgb), 0.2);
  backdrop-filter: blur(8px);
}

.menu-item.is-active::before {
  transform: translateY(-50%) scaleY(1);
}

.sub-menu {
  margin: 2px 0;
}

:deep(.el-sub-menu__title) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  height: 44px;
  line-height: 44px;
  position: relative;
}

:deep(.el-sub-menu__title:hover) {
  background: var(--sidebar-hover) !important;
  backdrop-filter: blur(8px);
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
  background: rgba(0, 0, 0, 0.1) !important;
  border-radius: var(--border-radius-md);
  margin: 4px 8px;
  backdrop-filter: blur(4px);
}

:deep(.el-sub-menu .el-menu .el-menu-item) {
  padding-left: 48px !important;
  height: 40px;
  line-height: 40px;
  font-size: 13px;
}

.sidebar-footer {
  padding: 12px 16px;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
  z-index: 2;
}

.version-text {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
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
