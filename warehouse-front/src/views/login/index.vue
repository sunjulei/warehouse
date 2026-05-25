<template>
  <div class="login-container">
    <div class="login-background">
      <div class="grid-overlay"></div>
      <div class="noise-overlay"></div>
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
      <div class="shape shape-5"></div>
      <div class="shape shape-6"></div>
    </div>
    <el-card class="login-card animate-scale-in">
      <div class="login-header">
        <div class="header-decoration"></div>
        <div class="logo-wrapper">
          <el-icon :size="36" color="#fff"><Box /></el-icon>
        </div>
        <h2 class="login-title">仓库管理系统</h2>
        <p class="login-subtitle">Warehouse Management System</p>
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
        <span>仓库管理系统 &copy; {{ new Date().getFullYear() }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'
import { ElMessage, type FormInstance } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaUrl = ref('')

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
  try {
    const res = await request.get('/login/getCode', {
      responseType: 'blob',
      timeout: 10000
    })
    captchaUrl.value = URL.createObjectURL(res)
  } catch {
    // fallback
    captchaUrl.value = '/api/login/getCode?t=' + Date.now()
  }
}

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
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 30%, #1e1b4b 60%, #312e81 100%);
  background-size: 400% 400%;
  animation: gradientShift 20s ease infinite;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  z-index: 1;
}

.noise-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.15;
  z-index: 2;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  background-repeat: repeat;
  background-size: 256px 256px;
}

.shape {
  position: absolute;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 3;
}

.shape-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(139, 92, 246, 0.2) 100%);
  border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  top: 10%;
  left: 10%;
  animation: float1 8s ease-in-out infinite;
}

.shape-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15) 0%, rgba(99, 102, 241, 0.15) 100%);
  border-radius: 60% 40% 30% 70% / 60% 30% 70% 40%;
  top: 60%;
  right: 10%;
  animation: float2 10s ease-in-out infinite;
}

.shape-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2) 0%, rgba(168, 85, 247, 0.2) 100%);
  border-radius: 50%;
  bottom: 10%;
  left: 30%;
  animation: float3 7s ease-in-out infinite;
}

.shape-4 {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.18) 0%, rgba(99, 102, 241, 0.18) 100%);
  border-radius: 40% 60% 60% 40% / 40% 40% 60% 60%;
  top: 30%;
  right: 30%;
  animation: float4 9s ease-in-out infinite;
}

.shape-5 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.15) 0%, rgba(139, 92, 246, 0.15) 100%);
  border-radius: 50%;
  top: 50%;
  left: 20%;
  animation: float5 11s ease-in-out infinite;
}

.shape-6 {
  width: 160px;
  height: 160px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.12) 0%, rgba(59, 130, 246, 0.12) 100%);
  border-radius: 30% 70% 50% 50% / 50% 50% 70% 30%;
  bottom: 20%;
  right: 20%;
  animation: float6 12s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg) scale(1);
  }
  33% {
    transform: translate(30px, -30px) rotate(120deg) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) rotate(240deg) scale(0.95);
  }
}

@keyframes float2 {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(-40px, -30px) rotate(180deg);
  }
}

@keyframes float3 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, -40px) scale(1.1);
  }
}

@keyframes float4 {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(-20px, -20px) rotate(90deg);
  }
  50% {
    transform: translate(10px, -30px) rotate(180deg);
  }
  75% {
    transform: translate(30px, -10px) rotate(270deg);
  }
}

@keyframes float5 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-30px, 20px) scale(1.15);
  }
}

@keyframes float6 {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(20px, -20px) rotate(60deg);
  }
  66% {
    transform: translate(-15px, 15px) rotate(120deg);
  }
}

.login-card {
  width: 440px;
  padding: 0;
  border-radius: var(--border-radius-2xl);
  box-shadow: var(--shadow-2xl), 0 0 80px rgba(99, 102, 241, 0.15);
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(40px) saturate(180%);
  -webkit-backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.5);
  position: relative;
  z-index: 10;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: var(--border-radius-2xl);
  padding: 1px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.1));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.login-card :deep(.el-card__body) {
  padding: 0;
}

.login-header {
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
  padding: 36px var(--spacing-xl) 32px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.login-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 50%, rgba(255, 255, 255, 0.15) 0%, transparent 50%);
  animation: headerGlow 6s ease-in-out infinite;
}

.login-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
}

.header-decoration {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 4s ease-in-out infinite;
}

.logo-wrapper {
  width: 68px;
  height: 68px;
  margin: 0 auto var(--spacing-md);
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  transform: rotate(-5deg);
  transition: transform 0.3s ease;
}

.logo-wrapper:hover {
  transform: rotate(0deg) scale(1.05);
}

.login-title {
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 6px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
  letter-spacing: 2px;
}

.login-subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: var(--font-size-sm);
  margin: 0;
  letter-spacing: 2px;
  position: relative;
  z-index: 1;
  font-weight: 300;
}

.login-form {
  padding: 28px var(--spacing-xl) 20px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.08) inset;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 8px 16px;
  height: 48px;
  background: rgba(248, 250, 252, 0.8);
}

.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(99, 102, 241, 0.3) inset;
  background: rgba(248, 250, 252, 1);
}

.login-input :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.5) inset, 0 0 20px rgba(99, 102, 241, 0.1);
  background: #fff;
}

.login-input :deep(.el-input__prefix) {
  color: #94a3b8;
  margin-right: 8px;
}

.login-input :deep(.el-input__prefix .el-icon) {
  font-size: 18px;
}

.login-input :deep(.el-input__inner) {
  font-size: 15px;
  color: #1e293b;
}

.login-input :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
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
  height: 48px;
  min-width: 130px;
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f8fafc;
}

.captcha-wrapper:hover {
  border-color: rgba(99, 102, 241, 0.5);
  box-shadow: 0 0 20px rgba(99, 102, 241, 0.15);
  transform: scale(1.02);
}

.captcha-img {
  height: 100%;
  width: 100%;
  object-fit: cover;
}

.captcha-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.8), rgba(99, 102, 241, 0.8));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s ease;
  backdrop-filter: blur(4px);
}

.captcha-wrapper:hover .captcha-overlay {
  opacity: 1;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
  border: none;
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 6px;
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.45);
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
}

.login-footer {
  text-align: center;
  padding: var(--spacing-sm) var(--spacing-lg) var(--spacing-md);
  font-size: 12px;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-scale-in {
  animation: scaleIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.85) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes headerGlow {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.8;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.1);
    opacity: 1;
  }
}

.animate-item-1 {
  animation: fadeIn 0.5s ease-out 0.1s both;
}

.animate-item-2 {
  animation: fadeIn 0.5s ease-out 0.2s both;
}

.animate-item-3 {
  animation: fadeIn 0.5s ease-out 0.3s both;
}

.animate-item-4 {
  animation: fadeIn 0.5s ease-out 0.4s both;
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>
