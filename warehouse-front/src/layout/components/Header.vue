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
      <ThemeSettings />
      <div class="header-time">
        <el-icon class="time-icon"><Clock /></el-icon>
        <span class="time-text">{{ currentTime }}</span>
      </div>
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
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getImageUrl } from '@/api/file'
import ThemeSettings from './ThemeSettings.vue'

defineProps<{ isCollapse: boolean }>()
defineEmits(['toggleCollapse'])

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const userAvatar = computed(() => {
  const imgpath = authStore.user?.imgpath
  return imgpath ? getImageUrl(imgpath) : ''
})

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
  font-size: 18px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  padding: 6px;
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.collapse-btn:hover {
  color: var(--primary-color);
  background: var(--primary-subtle);
}

.collapse-btn:active {
  transform: scale(0.92);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.header-time {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  font-variant-numeric: tabular-nums;
  font-family: var(--font-family-mono);
  padding: 4px 10px;
  border-radius: var(--border-radius-sm);
  background: var(--bg-tertiary);
  border: 1px solid var(--border-light);
}

.time-icon {
  font-size: 14px;
  opacity: 0.6;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: 4px 10px;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

.user-info:hover {
  background: var(--primary-subtle);
}

.user-avatar {
  border: 2px solid var(--border-light);
  transition: all var(--transition-base);
  background: var(--primary-gradient);
}

.user-info:hover .user-avatar {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.15);
}

.username {
  font-size: var(--font-size-base);
  color: var(--text-primary);
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color var(--transition-fast);
}

.user-info:hover .username {
  color: var(--primary-color);
}

.arrow-icon {
  color: var(--text-placeholder);
  transition: all var(--transition-fast);
  font-size: 12px;
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
}

:deep(.el-breadcrumb__inner:hover) {
  color: var(--primary-color);
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--text-primary);
  font-weight: 600;
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
  background: var(--bg-primary);
}

:deep(.el-dropdown-menu__item) {
  border-radius: var(--border-radius-sm);
  padding: 8px 12px;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-regular);
}

:deep(.el-dropdown-menu__item:hover) {
  background: var(--primary-subtle);
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
