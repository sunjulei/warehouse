<template>
  <div class="header">
    <div class="header-left">
      <el-icon class="collapse-btn" @click="$emit('toggleCollapse')">
        <Fold v-if="!isCollapse" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand" trigger="click">
        <span class="user-info">
          <el-avatar :size="32" :src="userAvatar" class="user-avatar">
            <el-icon :size="16"><User /></el-icon>
          </el-avatar>
          <span class="username">{{ authStore.user?.name || '用户' }}</span>
          <el-icon class="arrow-icon"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item command="changePassword">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getImageUrl } from '@/api/file'

defineProps<{ isCollapse: boolean }>()
defineEmits(['toggleCollapse'])

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const userAvatar = computed(() => {
  const imgpath = authStore.user?.imgpath
  return imgpath ? getImageUrl(imgpath) : ''
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
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  padding: 8px;
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.collapse-btn::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: var(--primary-gradient);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.collapse-btn:hover {
  color: var(--primary-color);
  background: rgba(var(--primary-rgb), 0.08);
  transform: scale(1.05);
}

.collapse-btn:hover::after {
  opacity: 0.08;
}

.collapse-btn:active {
  transform: scale(0.95);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  position: relative;
}

.user-info::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: var(--bg-secondary);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.user-info:hover::before {
  opacity: 1;
}

.user-info:hover {
  transform: translateY(-1px);
}

.user-avatar {
  border: 2px solid var(--border-light);
  transition: all var(--transition-base);
  background: var(--primary-gradient);
  position: relative;
  z-index: 1;
}

.user-info:hover .user-avatar {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(var(--primary-rgb), 0.15), 0 2px 8px rgba(var(--primary-rgb), 0.2);
  transform: scale(1.05);
}

.username {
  font-size: var(--font-size-base);
  color: var(--text-primary);
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  position: relative;
  z-index: 1;
  transition: color var(--transition-fast);
}

.user-info:hover .username {
  color: var(--primary-color);
}

.arrow-icon {
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  font-size: 12px;
  position: relative;
  z-index: 1;
}

.user-info:hover .arrow-icon {
  transform: rotate(180deg);
  color: var(--primary-color);
}

:deep(.el-breadcrumb) {
  display: flex;
  align-items: center;
}

:deep(.el-breadcrumb__separator) {
  color: var(--text-placeholder);
  font-weight: 400;
  margin: 0 4px;
}

:deep(.el-breadcrumb__inner) {
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  font-size: var(--font-size-base);
  position: relative;
}

:deep(.el-breadcrumb__inner::after) {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--primary-gradient);
  transition: width var(--transition-fast);
}

:deep(.el-breadcrumb__inner:hover) {
  color: var(--primary-color);
}

:deep(.el-breadcrumb__inner:hover::after) {
  width: 100%;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner::after) {
  display: none;
}

:deep(.el-dropdown) {
  outline: none;
}

:deep(.el-dropdown-menu) {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-light);
  padding: 4px;
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.95);
}

:deep(.el-dropdown-menu__item) {
  border-radius: var(--border-radius-sm);
  padding: 8px 12px;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dropdown-menu__item:hover) {
  background: rgba(var(--primary-rgb), 0.08);
  color: var(--primary-color);
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
  transition: transform var(--transition-fast);
}

:deep(.el-dropdown-menu__item:hover .el-icon) {
  transform: scale(1.1);
}
</style>
