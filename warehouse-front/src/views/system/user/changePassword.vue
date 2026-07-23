<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">修改密码</h1>
      <p class="page-header-desc">更改登录密码</p>
    </div>
    <el-row :gutter="24" justify="center">
      <el-col :span="10">
        <el-card class="pwd-card" shadow="hover">
          <div class="pwd-header">
            <div class="pwd-icon-wrap">
              <el-icon :size="32"><Lock /></el-icon>
            </div>
            <h2 class="pwd-title">修改密码</h2>
            <p class="pwd-subtitle">为了您的账户安全，请设置强度较高的密码</p>
          </div>
          <el-divider />
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="pwd-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入原密码" prefix-icon="Lock" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPwdOne">
              <el-input v-model="form.newPwdOne" type="password" show-password placeholder="请输入新密码" prefix-icon="Key" />
            </el-form-item>
            <el-form-item label="确认密码" prop="newPwdTwo">
              <el-input v-model="form.newPwdTwo" type="password" show-password placeholder="请再次输入新密码" prefix-icon="Key" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" class="submit-btn">
                <el-icon><Check /></el-icon> 确认修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { changePassword } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const form = ref({ oldPassword: '', newPwdOne: '', newPwdTwo: '' })

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPwdOne: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  newPwdTwo: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value && value !== form.value.newPwdOne) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    const res: any = await changePassword(form.value.oldPassword, form.value.newPwdOne, form.value.newPwdTwo)
    if (res.code === 200) {
      ElMessage.success('修改成功，请重新登录')
      // 密码已变更，旧会话作废，清除本地登录状态后跳转登录页
      await authStore.logout()
      router.push('/login')
    }
  })
}
</script>

<style scoped>
.page-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}

.page-container :deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
}

.pwd-card {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.pwd-header {
  text-align: center;
  padding: var(--spacing-md) 0;
}

.pwd-icon-wrap {
  width: 64px;
  height: 64px;
  margin: 0 auto var(--spacing-md);
  background: rgba(var(--primary-rgb), 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
}

.pwd-title {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-xs);
}

.pwd-subtitle {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0;
}

.pwd-form {
  padding: var(--spacing-md) 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-primary);
}

:deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset;
}

:deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px var(--primary-color) inset, 0 0 0 3px rgba(var(--primary-rgb), 0.1) inset;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: var(--font-size-base);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: var(--border-radius-md);
  background: var(--primary-gradient);
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(var(--primary-rgb), 0.3);
  transition: all var(--transition-fast);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(var(--primary-rgb), 0.4);
}

:deep(.el-divider) {
  margin: var(--spacing-md) 0;
}
</style>
