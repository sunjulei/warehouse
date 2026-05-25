<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">个人信息</h1>
      <p class="page-header-desc">查看和编辑个人资料</p>
    </div>
    <el-row :gutter="24">
      <el-col :span="8">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-avatar-section">
            <div class="avatar-ring">
              <el-avatar :size="96" :src="avatarUrl" class="profile-avatar">
                <el-icon :size="40"><User /></el-icon>
              </el-avatar>
            </div>
            <h3 class="profile-name">{{ form.name || '用户' }}</h3>
            <p class="profile-loginname">@{{ form.loginname || '-' }}</p>
            <el-divider />
            <div class="profile-info-list">
              <div class="profile-info-item">
                <el-icon><Location /></el-icon>
                <span>{{ form.address || '未设置地址' }}</span>
              </div>
              <div class="profile-info-item">
                <el-icon><ChatDotSquare /></el-icon>
                <span>{{ form.remark || '暂无备注' }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="card-header-icon"><Edit /></el-icon>
              <span>编辑个人信息</span>
            </div>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
            <el-form-item label="头像">
              <ImageUpload v-model="form.imgpath" />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="登录名">
              <el-input v-model="form.loginname" disabled />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="form.address" placeholder="请输入地址" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave" class="save-btn">
                <el-icon><Check /></el-icon> 保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import { getNowUser, updateUserInfo } from '@/api/user'
import { getImageUrl } from '@/api/file'

const formRef = ref<FormInstance>()
const form = ref<any>({})
const rules = { name: [{ required: true, message: '请输入姓名', trigger: 'blur' }] }

const avatarUrl = computed(() => {
  return form.value.imgpath ? getImageUrl(form.value.imgpath) : ''
})

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    const res: any = await updateUserInfo(form.value)
    if (res.code === 200) ElMessage.success('修改成功')
  })
}

onMounted(async () => {
  try {
    const res: any = await getNowUser()
    form.value = res || {}
  } catch {}
})
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

.profile-card {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  height: 100%;
}

.profile-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-md);
}

.avatar-ring {
  padding: 4px;
  border-radius: 50%;
  background: var(--primary-gradient);
  margin-bottom: var(--spacing-md);
}

.profile-avatar {
  border: 3px solid var(--bg-primary);
  background: var(--bg-tertiary);
}

.profile-name {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-xs);
}

.profile-loginname {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0;
}

.profile-info-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.profile-info-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--text-regular);
  font-size: var(--font-size-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-tertiary);
  border-radius: var(--border-radius-md);
}

.profile-info-item .el-icon {
  color: var(--primary-color);
  font-size: 16px;
  flex-shrink: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.card-header-icon {
  color: var(--primary-color);
  font-size: 18px;
}

.profile-form {
  max-width: 500px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-primary);
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset;
}

:deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px var(--primary-color) inset, 0 0 0 3px rgba(var(--primary-rgb), 0.1) inset;
}

.save-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 28px;
  border-radius: var(--border-radius-md);
  background: var(--primary-gradient);
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(var(--primary-rgb), 0.3);
  transition: all var(--transition-fast);
}

.save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(var(--primary-rgb), 0.4);
}

:deep(.el-divider) {
  width: 100%;
  margin: var(--spacing-md) 0;
}
</style>
