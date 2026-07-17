<template>
  <div class="login-container">
    <div class="login-background">
      <div class="grid-overlay"></div>
      <div class="noise-overlay"></div>
      <!-- Warehouse rack silhouettes -->
      <div class="rack rack-1"></div>
      <div class="rack rack-2"></div>
      <div class="rack rack-3"></div>
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    <el-card class="login-card animate-scale-in">
      <div class="login-header">
        <div class="header-mesh"></div>
        <div class="logo-wrapper">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="logo-svg">
            <path d="M20 7L12 3L4 7V17L12 21L20 17V7Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
            <path d="M12 12L20 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
            <path d="M12 12V21" stroke="currentColor" stroke-width="1.5"/>
            <path d="M12 12L4 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2 class="login-title">仓图</h2>
        <p class="login-subtitle">WareMap</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin" class="login-form">
        <el-form-item prop="loginname" class="animate-item-1">
          <el-input
            v-model="form.loginname"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            class="login-input"
          />
        </el-form-item>
        <el-form-item prop="pwd" class="animate-item-2">
          <el-input
            v-model="form.pwd"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            class="login-input"
          />
        </el-form-item>
        <el-form-item prop="code" class="animate-item-3">
          <div class="captcha-row">
            <el-input
              v-model="form.code"
              placeholder="验证码"
              prefix-icon="Key"
              size="large"
              class="login-input captcha-input"
            />
            <div class="captcha-wrapper" @click="refreshCaptcha" title="点击刷新">
              <img :src="captchaUrl" class="captcha-img" />
              <div class="captcha-overlay">
                <el-icon><Refresh /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item class="animate-item-4">
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>WareMap 仓库管理系统 &copy; {{ new Date().getFullYear() }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'
import { ElMessage, type FormInstance } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaUrl = ref('')
const captchaBlobUrl = ref<string | null>(null)

const form = ref({
  loginname: '',
  pwd: '',
  code: ''
})

const rules = {
  loginname: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  pwd: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async () => {
  // 释放旧的 blob URL，防止内存泄漏
  if (captchaBlobUrl.value) {
    URL.revokeObjectURL(captchaBlobUrl.value)
    captchaBlobUrl.value = null
  }
  try {
    const res = await request.get('/login/getCode', {
      responseType: 'blob',
      timeout: 10000
    })
    const blob = res.data as Blob
    const url = URL.createObjectURL(blob)
    captchaBlobUrl.value = url
    captchaUrl.value = url
  } catch {
    captchaUrl.value = '/warehouse/login/getCode?t=' + Date.now()
  }
}

onUnmounted(() => {
  // 组件卸载时释放 blob URL
  if (captchaBlobUrl.value) {
    URL.revokeObjectURL(captchaBlobUrl.value)
  }
})

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await authStore.login(form.value.loginname, form.value.pwd, form.value.code)
      if (res.code === 200) {
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        refreshCaptcha()
        form.value.code = ''
      }
    } catch {
      refreshCaptcha()
      form.value.code = ''
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: var(--bg-secondary);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
  z-index: 0;
}

.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--border-lighter) 1px, transparent 1px),
    linear-gradient(90deg, var(--border-lighter) 1px, transparent 1px);
  background-size: 60px 60px;
  z-index: 1;
}

.noise-overlay {
  position: absolute;
  inset: 0;
  opacity: 0.03;
  z-index: 2;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  background-repeat: repeat;
  background-size: 256px 256px;
}

.rack { display: none; }
.shape { display: none; }

.login-card {
  width: 400px;
  padding: 0;
  border-radius: var(--border-radius-2xl);
  box-shadow: var(--shadow-xl);
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
  position: relative;
  z-index: 10;
  overflow: hidden;
}

.login-card :deep(.el-card__body) {
  padding: 0;
}

.login-header {
  background: var(--bg-primary);
  padding: 36px var(--spacing-2xl) 28px;
  text-align: center;
  position: relative;
  border-bottom: 1px solid var(--border-light);
}

.logo-wrapper {
  width: 56px;
  height: 56px;
  margin: 0 auto var(--spacing-md);
  background: var(--primary-gradient);
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.logo-wrapper:hover {
  transform: rotate(-5deg) scale(1.05);
}

.logo-svg {
  width: 28px;
  height: 28px;
  color: #fff;
}

.login-title {
  color: var(--text-primary);
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 4px;
  position: relative;
  z-index: 1;
  letter-spacing: 3px;
}

.login-subtitle {
  color: var(--text-placeholder);
  font-size: var(--font-size-sm);
  margin: 0;
  letter-spacing: 3px;
  position: relative;
  z-index: 1;
  font-weight: 400;
  text-transform: uppercase;
}

.login-form {
  padding: 24px var(--spacing-2xl) 16px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.login-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
  box-shadow: 0 0 0 1px var(--border-color) inset;
  transition: all 0.2s ease;
  padding: 8px 16px;
  height: 44px;
  background: var(--bg-primary);
}

.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset;
}

.login-input :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px var(--primary-color) inset, 0 0 0 3px rgba(var(--primary-rgb), 0.08) inset;
}

.login-input :deep(.el-input__prefix) {
  color: var(--text-placeholder);
  margin-right: 8px;
}

.login-input :deep(.el-input__prefix .el-icon) {
  font-size: 18px;
}

.login-input :deep(.el-input__inner) {
  font-size: 14px;
  color: var(--text-primary);
}

.login-input :deep(.el-input__inner::placeholder) {
  color: var(--text-placeholder);
}

.captcha-row {
  display: flex;
  gap: var(--spacing-md);
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-wrapper {
  position: relative;
  height: 44px;
  min-width: 120px;
  cursor: pointer;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  border: 1px solid var(--border-color);
  transition: all 0.2s ease;
  background: var(--bg-primary);
}

.captcha-wrapper:hover {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(var(--primary-rgb), 0.08);
}

.captcha-img {
  height: 100%;
  width: 100%;
  object-fit: cover;
}

.captcha-overlay {
  position: absolute;
  inset: 0;
  background: rgba(var(--primary-rgb), 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.2s ease;
  backdrop-filter: blur(4px);
}

.captcha-wrapper:hover .captcha-overlay {
  opacity: 1;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 14px;
  font-weight: 600;
  border-radius: var(--border-radius-md);
  background: var(--primary-gradient);
  border: none;
  box-shadow: 0 2px 8px var(--primary-shadow);
  transition: all 0.2s ease;
  letter-spacing: 4px;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px var(--primary-shadow);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  padding: var(--spacing-sm) var(--spacing-lg) var(--spacing-md);
  font-size: 12px;
  color: var(--text-placeholder);
  letter-spacing: 0.5px;
}

.animate-scale-in {
  animation: scaleIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.92) translateY(16px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.animate-item-1 { animation: fadeIn 0.4s ease-out 0.1s both; }
.animate-item-2 { animation: fadeIn 0.4s ease-out 0.2s both; }
.animate-item-3 { animation: fadeIn 0.4s ease-out 0.3s both; }
.animate-item-4 { animation: fadeIn 0.4s ease-out 0.4s both; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
