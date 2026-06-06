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
        <h2 class="login-title">仓储脉搏</h2>
        <p class="login-subtitle">Warehouse Pulse</p>
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
        <span>仓储脉搏管理系统 &copy; {{ new Date().getFullYear() }}</span>
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
    captchaUrl.value = URL.createObjectURL(res.data as Blob)
  } catch {
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
  background: linear-gradient(160deg, #0c0a09 0%, #1c1917 30%, #1a1a2e 60%, #16213e 100%);
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
    linear-gradient(rgba(13, 148, 136, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(13, 148, 136, 0.04) 1px, transparent 1px);
  background-size: 60px 60px;
  z-index: 1;
}

.noise-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.12;
  z-index: 2;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  background-repeat: repeat;
  background-size: 256px 256px;
}

/* Warehouse rack silhouettes */
.rack {
  position: absolute;
  z-index: 3;
  opacity: 0.06;
}

.rack-1 {
  left: -20px;
  bottom: 0;
  width: 200px;
  height: 350px;
  background:
    repeating-linear-gradient(0deg, transparent, transparent 60px, rgba(13, 148, 136, 0.3) 60px, rgba(13, 148, 136, 0.3) 62px),
    repeating-linear-gradient(90deg, transparent, transparent 80px, rgba(13, 148, 136, 0.2) 80px, rgba(13, 148, 136, 0.2) 82px);
  transform: perspective(400px) rotateY(10deg);
}

.rack-2 {
  right: -10px;
  bottom: 0;
  width: 180px;
  height: 300px;
  background:
    repeating-linear-gradient(0deg, transparent, transparent 50px, rgba(13, 148, 136, 0.25) 50px, rgba(13, 148, 136, 0.25) 52px),
    repeating-linear-gradient(90deg, transparent, transparent 70px, rgba(13, 148, 136, 0.15) 70px, rgba(13, 148, 136, 0.15) 72px);
  transform: perspective(400px) rotateY(-8deg);
}

.rack-3 {
  left: 15%;
  top: 0;
  width: 160px;
  height: 120px;
  background:
    repeating-linear-gradient(0deg, transparent, transparent 35px, rgba(13, 148, 136, 0.2) 35px, rgba(13, 148, 136, 0.2) 37px),
    repeating-linear-gradient(90deg, transparent, transparent 55px, rgba(13, 148, 136, 0.15) 55px, rgba(13, 148, 136, 0.15) 57px);
}

.shape {
  position: absolute;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(13, 148, 136, 0.1);
  z-index: 3;
}

.shape-1 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(13, 148, 136, 0.12) 0%, transparent 70%);
  border-radius: 50%;
  top: 15%;
  right: 15%;
  animation: float1 12s ease-in-out infinite;
}

.shape-2 {
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, rgba(13, 148, 136, 0.08) 0%, transparent 70%);
  border-radius: 50%;
  bottom: 20%;
  left: 20%;
  animation: float2 10s ease-in-out infinite;
}

.shape-3 {
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  top: 40%;
  left: 10%;
  animation: float3 8s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-20px, -30px) scale(1.1); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(25px, -20px); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(15px, -25px) scale(1.15); }
}

.login-card {
  width: 420px;
  padding: 0;
  border-radius: 16px;
  box-shadow: var(--shadow-2xl), 0 0 60px rgba(13, 148, 136, 0.1);
  background: rgba(28, 25, 23, 0.85);
  backdrop-filter: blur(40px) saturate(180%);
  -webkit-backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(13, 148, 136, 0.15);
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
  border-radius: 16px;
  padding: 1px;
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.3), rgba(13, 148, 136, 0.05));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.login-card :deep(.el-card__body) {
  padding: 0;
}

.login-header {
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.15) 0%, rgba(13, 148, 136, 0.05) 100%);
  padding: 32px var(--spacing-xl) 28px;
  text-align: center;
  position: relative;
  overflow: hidden;
  border-bottom: 1px solid rgba(13, 148, 136, 0.1);
}

.header-mesh {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(13, 148, 136, 0.08) 1px, transparent 1px),
    radial-gradient(circle at 80% 20%, rgba(13, 148, 136, 0.06) 1px, transparent 1px);
  background-size: 24px 24px, 32px 32px;
}

.logo-wrapper {
  width: 60px;
  height: 60px;
  margin: 0 auto var(--spacing-md);
  background: var(--primary-gradient);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(13, 148, 136, 0.4);
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.logo-wrapper:hover {
  transform: rotate(-5deg) scale(1.05);
}

.logo-svg {
  width: 30px;
  height: 30px;
  color: #fff;
}

.login-title {
  color: #fafaf9;
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 4px;
  position: relative;
  z-index: 1;
  letter-spacing: 3px;
}

.login-subtitle {
  color: rgba(168, 162, 158, 0.7);
  font-size: var(--font-size-sm);
  margin: 0;
  letter-spacing: 3px;
  position: relative;
  z-index: 1;
  font-weight: 300;
  text-transform: uppercase;
}

.login-form {
  padding: 24px var(--spacing-xl) 16px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.login-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(68, 64, 60, 0.5) inset;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 8px 16px;
  height: 46px;
  background: rgba(41, 37, 36, 0.6);
}

.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(13, 148, 136, 0.4) inset;
}

.login-input :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px rgba(13, 148, 136, 0.6) inset, 0 0 16px rgba(13, 148, 136, 0.1);
  background: rgba(41, 37, 36, 0.8);
}

.login-input :deep(.el-input__prefix) {
  color: rgba(168, 162, 158, 0.6);
  margin-right: 8px;
}

.login-input :deep(.el-input__prefix .el-icon) {
  font-size: 18px;
}

.login-input :deep(.el-input__inner) {
  font-size: 14px;
  color: #fafaf9;
}

.login-input :deep(.el-input__inner::placeholder) {
  color: rgba(120, 113, 108, 0.8);
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
  height: 46px;
  min-width: 120px;
  cursor: pointer;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid rgba(68, 64, 60, 0.5);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(41, 37, 36, 0.6);
}

.captcha-wrapper:hover {
  border-color: rgba(13, 148, 136, 0.5);
  box-shadow: 0 0 16px rgba(13, 148, 136, 0.15);
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
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.85), rgba(13, 148, 136, 0.7));
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
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 10px;
  background: var(--primary-gradient);
  border: none;
  box-shadow: 0 4px 16px rgba(13, 148, 136, 0.35);
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
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
  transition: left 0.5s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(13, 148, 136, 0.45);
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  padding: var(--spacing-sm) var(--spacing-lg) var(--spacing-md);
  font-size: 12px;
  color: rgba(120, 113, 108, 0.5);
  letter-spacing: 0.5px;
}

.animate-scale-in {
  animation: scaleIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.88) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.animate-item-1 { animation: fadeIn 0.5s ease-out 0.1s both; }
.animate-item-2 { animation: fadeIn 0.5s ease-out 0.2s both; }
.animate-item-3 { animation: fadeIn 0.5s ease-out 0.3s both; }
.animate-item-4 { animation: fadeIn 0.5s ease-out 0.4s both; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}
</style>
