<template>
  <el-container class="layout-container">
    <el-header class="top-header" height="auto">
      <div class="top-header-inner">
        <div class="top-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="logo-svg">
              <path d="M20 7L12 3L4 7V17L12 21L20 17V7Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
              <path d="M12 12L20 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
              <path d="M12 12V21" stroke="currentColor" stroke-width="1.5"/>
              <path d="M12 12L4 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
              <path d="M8 5L16 9" stroke="currentColor" stroke-width="1" opacity="0.5"/>
            </svg>
          </div>
          <span class="logo-text">仓储脉搏</span>
        </div>

        <el-scrollbar class="top-menu-scrollbar">
          <el-menu
            :default-active="currentRoute"
            mode="horizontal"
            :ellipsis="false"
            class="top-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/dashboard" class="top-menu-item">
              <el-icon><HomeFilled /></el-icon>
              <template #title>控制台</template>
            </el-menu-item>

            <template v-for="menu in authStore.menus" :key="menu.id">
              <el-menu-item
                v-if="menu.href"
                :index="menu.href"
                class="top-menu-item"
              >
                <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
                <template #title>{{ menu.title }}</template>
              </el-menu-item>
              <el-sub-menu
                v-else-if="menu.children && menu.children.length > 0"
                :index="menu.id + ''"
                class="top-sub-menu"
              >
                <template #title>
                  <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
                  <span>{{ menu.title }}</span>
                </template>
                <el-menu-item
                  v-for="child in menu.children"
                  :key="child.id"
                  :index="child.href"
                >
                  <el-icon v-if="isElementIcon(child.icon)"><component :is="child.icon" /></el-icon>
                  <template #title>{{ child.title }}</template>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item
                v-else
                :index="menu.href"
                class="top-menu-item"
              >
                <el-icon v-if="isElementIcon(menu.icon)"><component :is="menu.icon" /></el-icon>
                <template #title>{{ menu.title }}</template>
              </el-menu-item>
            </template>
          </el-menu>
        </el-scrollbar>

        <div class="top-header-right">
          <button class="icon-btn" @click="themeStore.toggle()" :title="isDark ? '日间模式' : '夜间模式'">
            <transition name="theme-icon" mode="out-in">
              <el-icon v-if="isDark" key="moon"><Moon /></el-icon>
              <el-icon v-else key="sunny"><Sunny /></el-icon>
            </transition>
          </button>
          <button class="icon-btn" @click="themeStore.toggleLayout()" title="切换侧边栏布局">
            <el-icon><Grid /></el-icon>
          </button>
          <div class="header-time">
            <el-icon class="time-icon"><Clock /></el-icon>
            <span class="time-text">{{ currentTime }}</span>
          </div>
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <el-avatar :size="30" :src="userAvatar" class="user-avatar">
                <el-icon :size="14"><User /></el-icon>
              </el-avatar>
              <span class="username">{{ authStore.user?.name || '用户' }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人信息
                </el-dropdown-item>
                <el-dropdown-item command="changePassword">
                  <el-icon><Lock /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <TabsBar />
    <el-main class="layout-main">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { getImageUrl } from '@/api/file'
import TabsBar from './components/TabsBar.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const themeStore = useThemeStore()

const currentRoute = computed(() => route.path)
const isDark = computed(() => themeStore.mode === 'dark')

const userAvatar = computed(() => {
  const imgpath = authStore.user?.imgpath
  return imgpath ? getImageUrl(imgpath) : ''
})

const isElementIcon = (icon: string | undefined | null): boolean => {
  if (!icon) return false
  return /^[A-Z][a-zA-Z]*$/.test(icon)
}

const handleMenuSelect = (index: string) => {
  if (index && index !== route.path) {
    router.push(index)
  }
}

const currentTime = ref('')
let timer: ReturnType<typeof setInterval> | null = null

const updateTime = () => {
  const now = new Date()
  const h = String(now.getHours()).padStart(2, '0')
  const m = String(now.getMinutes()).padStart(2, '0')
  const s = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${h}:${m}:${s}`
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const handleCommand = async (command: string) => {
  if (command === 'profile') {
    router.push('/system/user/profile')
  } else if (command === 'changePassword') {
    router.push('/system/user/changePassword')
  } else if (command === 'logout') {
    await authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
  flex-direction: column;
}

/* ─── Top Header ─────────────────────────── */
.top-header {
  background: var(--sidebar-bg);
  padding: 0;
  flex-shrink: 0;
  position: relative;
  z-index: 10;
}

.top-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(var(--primary-rgb), 0.3), transparent);
  pointer-events: none;
}

.top-header-inner {
  display: flex;
  align-items: center;
  height: 52px;
  padding: 0 var(--spacing-md);
  gap: var(--spacing-sm);
}

/* ─── Logo ───────────────────────────────── */
.top-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  padding-right: var(--spacing-md);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  margin-right: var(--spacing-xs);
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: var(--primary-gradient);
  border-radius: var(--border-radius-xs);
  box-shadow: 0 2px 8px rgba(var(--primary-rgb), 0.4);
  flex-shrink: 0;
}

.logo-svg {
  width: 18px;
  height: 18px;
  color: #fff;
}

.logo-text {
  color: #fafaf9;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 2px;
  white-space: nowrap;
}

/* ─── Horizontal Menu ────────────────────── */
.top-menu-scrollbar {
  flex: 1;
  overflow: hidden;
  min-width: 0;
}

.top-menu {
  border-bottom: none;
  background: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: var(--sidebar-hover);
  --el-menu-text-color: var(--sidebar-text);
  --el-menu-active-color: var(--sidebar-text-active);
  --el-menu-hover-text-color: var(--sidebar-text-active);
  height: 52px;
}

.top-menu-item {
  height: 52px;
  line-height: 52px;
  border-radius: 0;
  margin: 0;
  position: relative;
  font-size: 13px;
}

.top-menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 24px;
  height: 2px;
  background: var(--primary-gradient);
  border-radius: 1px;
  transition: transform var(--transition-fast);
}

.top-menu-item.is-active {
  background: var(--sidebar-active) !important;
  color: var(--sidebar-text-active) !important;
  font-weight: 500;
}

.top-menu-item.is-active::after {
  transform: translateX(-50%) scaleX(1);
}

.top-menu-item:hover {
  background: var(--sidebar-hover) !important;
  color: var(--sidebar-text-active) !important;
}

:deep(.el-sub-menu__title) {
  height: 52px;
  line-height: 52px;
  color: var(--sidebar-text);
  font-size: 13px;
}

:deep(.el-sub-menu__title:hover) {
  background: var(--sidebar-hover) !important;
  color: var(--sidebar-text-active) !important;
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: var(--sidebar-text-active) !important;
}

:deep(.el-menu--horizontal .el-sub-menu .el-menu) {
  background: var(--bg-elevated);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xl);
  padding: 4px;
  min-width: 160px;
}

:deep(.el-menu--horizontal .el-sub-menu .el-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
  border-radius: var(--border-radius-sm);
  color: var(--text-regular);
  font-size: 13px;
}

:deep(.el-menu--horizontal .el-sub-menu .el-menu .el-menu-item:hover) {
  background: var(--primary-subtle);
  color: var(--primary-color);
}

:deep(.el-menu--horizontal .el-sub-menu .el-menu .el-menu-item.is-active) {
  color: var(--primary-color);
  font-weight: 500;
}

/* ─── Header Right ───────────────────────── */
.top-header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
  padding-left: var(--spacing-sm);
  border-left: 1px solid rgba(255, 255, 255, 0.06);
  margin-left: var(--spacing-xs);
}

.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: var(--border-radius-sm);
  background: rgba(255, 255, 255, 0.04);
  color: var(--sidebar-text);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-size: 16px;
  font-family: inherit;
  outline: none;
}

.icon-btn:hover {
  background: var(--sidebar-hover);
  color: var(--sidebar-text-active);
  border-color: rgba(var(--primary-rgb), 0.2);
}

.header-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--sidebar-text);
  font-size: 12px;
  font-variant-numeric: tabular-nums;
  font-family: var(--font-family-mono);
  padding: 4px 8px;
  border-radius: var(--border-radius-sm);
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.time-icon {
  font-size: 12px;
  opacity: 0.5;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-fast);
}

.user-info:hover {
  background: var(--sidebar-hover);
}

.user-avatar {
  border: 1.5px solid rgba(255, 255, 255, 0.1);
  background: var(--primary-gradient);
}

.user-info:hover .user-avatar {
  border-color: rgba(var(--primary-rgb), 0.4);
}

.username {
  font-size: 13px;
  color: var(--sidebar-text);
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color var(--transition-fast);
}

.user-info:hover .username {
  color: var(--sidebar-text-active);
}

.arrow-icon {
  color: var(--sidebar-text);
  transition: all var(--transition-fast);
  font-size: 12px;
  opacity: 0.5;
}

.user-info:hover .arrow-icon {
  transform: rotate(180deg);
  opacity: 1;
}

/* ─── Main Content ───────────────────────── */
.layout-main {
  background: var(--bg-secondary);
  padding: var(--spacing-lg);
  overflow-y: auto;
  flex: 1;
  transition: background-color var(--transition-base);
}

.layout-main::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.layout-main::-webkit-scrollbar-track {
  background: transparent;
}

.layout-main::-webkit-scrollbar-thumb {
  background: rgba(var(--primary-rgb), 0.15);
  border-radius: 3px;
}

.layout-main::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--primary-rgb), 0.3);
}

/* ─── Transitions ────────────────────────── */
.theme-icon-enter-active,
.theme-icon-leave-active {
  transition: all 0.2s ease;
}

.theme-icon-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.6);
}

.theme-icon-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.6);
}
</style>
