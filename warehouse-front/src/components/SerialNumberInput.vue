<template>
  <el-dialog v-model="visible" title="序列号录入" width="600px" :close-on-click-modal="false">
    <div class="serial-input-container">
      <div class="input-area">
        <el-input
          v-model="currentSerial"
          placeholder="输入或扫描序列号后按回车"
          clearable
          @keyup.enter="handleAddSerial"
        >
          <template #append>
            <el-button @click="handleAddSerial">添加</el-button>
          </template>
        </el-input>
      </div>

      <div class="serial-list">
        <div class="list-header">
          <span>已录入序列号 ({{ serialList.length }}/{{ expectedCount }})</span>
          <el-button type="danger" link @click="handleClearAll">清空</el-button>
        </div>
        <el-table :data="displayList" border size="small" max-height="300">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="serialNumber" label="序列号" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.valid ? 'success' : 'danger'" size="small">
                {{ row.valid ? '有效' : row.error }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemove($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :disabled="!canSubmit" @click="handleSubmit">确认 ({{ validCount }}个)</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

interface SerialItem {
  serialNumber: string
  valid: boolean
  error?: string
}

const visible = ref(false)
const currentSerial = ref('')
const serialList = ref<SerialItem[]>([])
const errorMsg = ref('')
const expectedCount = ref(0)
const goodsId = ref<number | null>(null)

const emit = defineEmits<{
  (e: 'confirm', serialNumbers: string[]): void
}>()

const displayList = computed(() => serialList.value)
const validCount = computed(() => serialList.value.filter(s => s.valid).length)
const canSubmit = computed(() => validCount.value > 0 && validCount.value <= expectedCount.value)

const open = (gid: number, count: number) => {
  goodsId.value = gid
  expectedCount.value = count
  serialList.value = []
  currentSerial.value = ''
  errorMsg.value = ''
  visible.value = true
}

const handleAddSerial = () => {
  const sn = currentSerial.value.trim()
  if (!sn) return

  // 检查是否重复
  if (serialList.value.some(s => s.serialNumber === sn)) {
    ElMessage.warning('序列号已存在')
    return
  }

  // 检查数量
  if (serialList.value.length >= expectedCount.value) {
    ElMessage.warning('已达到预期数量')
    return
  }

  serialList.value.push({
    serialNumber: sn,
    valid: true
  })

  currentSerial.value = ''
  errorMsg.value = ''
}

const handleRemove = (index: number) => {
  serialList.value.splice(index, 1)
}

const handleClearAll = () => {
  serialList.value = []
}

const handleSubmit = () => {
  if (validCount.value !== expectedCount.value) {
    errorMsg.value = `序列号数量(${validCount.value})与预期数量(${expectedCount.value})不一致`
    return
  }
  emit('confirm', serialList.value.filter(s => s.valid).map(s => s.serialNumber))
  visible.value = false
}

defineExpose({ open })
</script>

<style scoped>
.serial-input-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-area {
  display: flex;
  gap: 8px;
}

.serial-list {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  padding: 12px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.error-msg {
  color: var(--el-color-danger);
  font-size: 13px;
}
</style>
