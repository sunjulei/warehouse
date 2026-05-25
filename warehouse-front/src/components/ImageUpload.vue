<template>
  <div class="image-upload">
    <el-upload
      class="avatar-uploader"
      :action="uploadUrl"
      name="mf"
      :show-file-list="false"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
      :with-credentials="true"
      accept="image/*"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <div v-else class="upload-placeholder">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <span class="upload-text">点击上传</span>
      </div>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getImageUrl } from '@/api/file'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits(['update:modelValue'])

const uploadUrl = '/api/file/uploadFile'

const imageUrl = computed(() => {
  if (!props.modelValue) return ''
  return getImageUrl(props.modelValue)
})

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const handleSuccess = (res: any) => {
  if (res.path) {
    emit('update:modelValue', res.path)
  }
}
</script>

<style scoped>
.image-upload {
  display: inline-block;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 2px dashed var(--border-color);
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  overflow: hidden;
  transition: all var(--transition-base);
  background: var(--bg-tertiary);
  position: relative;
}

:deep(.el-upload) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: var(--primary-color);
  background: rgba(var(--primary-rgb), 0.04);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.avatar-uploader:hover .avatar {
  transform: scale(1.05);
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
}

.upload-icon {
  font-size: 32px;
  color: var(--text-secondary);
  transition: all var(--transition-base);
}

.avatar-uploader:hover .upload-icon {
  color: var(--primary-color);
  transform: scale(1.1);
}

.upload-text {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  transition: color var(--transition-base);
}

.avatar-uploader:hover .upload-text {
  color: var(--primary-color);
}

/* 上传成功后的动画 */
.avatar-uploader:active {
  transform: scale(0.98);
}
</style>
