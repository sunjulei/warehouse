<template>
  <el-dialog
    v-model="visible"
    :width="width"
    :close-on-click-modal="false"
    @close="handleClose"
    class="crud-dialog"
    destroy-on-close
    append-to-body
  >
    <template #header>
      <div class="dialog-header">
        <div class="dialog-header-icon" :class="isEdit ? 'edit' : 'add'">
          <el-icon :size="20"><component :is="isEdit ? 'Edit' : 'Plus'" /></el-icon>
        </div>
        <div class="dialog-header-text">
          <span class="dialog-title">{{ isEdit ? '编辑' : '新增' }}</span>
          <span class="dialog-subtitle">{{ isEdit ? '修改已有数据' : '填写以下信息' }}</span>
        </div>
      </div>
    </template>

    <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px" :label-position="labelPosition" class="dialog-form">
      <slot :form-data="formData" :is-edit="isEdit" />
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-btn">
          <el-icon><Close /></el-icon> 取消
        </el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit" class="submit-btn">
          <el-icon v-if="!submitting"><Check /></el-icon>
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'

const props = defineProps<{
  width?: string
  rules?: Record<string, any>
  submitApi: (data: any) => Promise<any>
  labelPosition?: 'left' | 'right' | 'top'
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formData = ref<any>({})

const open = (data?: any, edit = false) => {
  isEdit.value = edit
  formData.value = data ? { ...data } : {}
  visible.value = true
}

const handleClose = () => {
  formRef.value?.resetFields()
  formData.value = {}
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const res: any = await props.submitApi(formData.value)
      if (res.code === 200) {
        ElMessage.success(res.msg || '操作成功')
        visible.value = false
        emit('success')
      }
    } catch {} finally {
      submitting.value = false
    }
  })
}

defineExpose({ open, formData })
</script>

<style scoped>
.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
}

.dialog-header-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.dialog-header-icon.add {
  background: var(--primary-gradient);
}

.dialog-header-icon.edit {
  background: var(--accent-gradient);
}

.dialog-header-text {
  display: flex;
  flex-direction: column;
}

.dialog-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-primary);
}

.dialog-subtitle {
  font-size: var(--font-size-xs);
  color: var(--text-placeholder);
  margin-top: 2px;
}

.dialog-form {
  padding: 8px 0;
}

:deep(.el-dialog) {
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-2xl);
  background-color: var(--bg-primary);
}

:deep(.el-dialog__header) {
  padding: 16px 24px;
  margin: 0;
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-primary);
}

:deep(.el-dialog__headerbtn) {
  top: 16px;
  right: 16px;
  width: 28px;
  height: 28px;
  border-radius: var(--border-radius-sm);
  transition: all 0.15s;
}

:deep(.el-dialog__headerbtn:hover) {
  background: var(--danger-bg);
  color: var(--danger-color);
}

:deep(.el-dialog__body) {
  padding: 20px 24px;
  max-height: 60vh;
  overflow-y: auto;
  color: var(--text-regular);
}

:deep(.el-dialog__footer) {
  padding: 14px 24px;
  border-top: 1px solid var(--border-light);
  background: var(--bg-primary);
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn {
  border-radius: var(--border-radius-sm);
  padding: 8px 20px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.cancel-btn:hover {
  color: var(--primary-color);
  border-color: var(--primary-light);
}

.submit-btn {
  border-radius: var(--border-radius-sm);
  padding: 8px 24px;
  font-weight: 500;
  background: var(--primary-gradient);
  border: none;
  display: flex;
  align-items: center;
  gap: 4px;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px var(--primary-shadow);
}

.submit-btn:active {
  transform: translateY(0);
}
</style>
