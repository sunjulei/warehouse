<template>
  <el-dialog v-model="visible" title="选择序列号" width="700px" :close-on-click-modal="false">
    <div class="serial-selector-container">
      <div class="search-area">
        <el-input
          v-model="scanInput"
          placeholder="扫描或输入序列号快速选择"
          clearable
          @keyup.enter="handleScan"
        >
          <template #append>
            <el-button @click="handleScan">添加</el-button>
          </template>
        </el-input>
      </div>

      <div class="selected-info">
        已选择: {{ selectedList.length }}/{{ expectedCount }}
      </div>

      <el-table
        :data="availableList"
        border
        size="small"
        max-height="350"
        @selection-change="handleSelectionChange"
        ref="tableRef"
      >
        <el-table-column type="selection" width="50" :selectable="() => selectedList.length < expectedCount" />
        <el-table-column prop="serialNumber" label="序列号" />
        <el-table-column prop="instockTime" label="入库时间" width="180" />
        <el-table-column label="状态" width="80">
          <template #default>
            <el-tag type="success" size="small">在库</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :disabled="selectedList.length !== expectedCount" @click="handleSubmit">
        确认选择 ({{ selectedList.length }}个)
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableSerialNumbers } from '@/api/serialNumber'

interface SerialNumber {
  id: number
  serialNumber: string
  goodsid: number
  status: number
  instockTime: string
}

const visible = ref(false)
const scanInput = ref('')
const availableList = ref<SerialNumber[]>([])
const selectedList = ref<SerialNumber[]>([])
const expectedCount = ref(0)
const goodsId = ref<number | null>(null)
const tableRef = ref()

const emit = defineEmits<{
  (e: 'confirm', serialNumbers: string[]): void
}>()

const open = async (gid: number, count: number) => {
  goodsId.value = gid
  expectedCount.value = count
  selectedList.value = []
  scanInput.value = ''
  visible.value = true

  // 加载可用序列号
  try {
    const res: any = await getAvailableSerialNumbers(gid)
    availableList.value = res.data || []
  } catch (e) {
    console.error('加载序列号失败:', e)
    availableList.value = []
  }
}

const handleScan = () => {
  const sn = scanInput.value.trim()
  if (!sn) return

  // 查找匹配的可用序列号
  const found = availableList.value.find(s => s.serialNumber === sn)
  if (!found) {
    ElMessage.warning('序列号不存在或不可用')
    return
  }

  // 检查是否已选
  if (selectedList.value.some(s => s.serialNumber === sn)) {
    ElMessage.warning('序列号已选择')
    return
  }

  // 检查数量
  if (selectedList.value.length >= expectedCount.value) {
    ElMessage.warning('已达到预期数量')
    return
  }

  selectedList.value.push(found)
  scanInput.value = ''

  // 更新表格选中状态
  updateTableSelection()
}

const handleSelectionChange = (selection: SerialNumber[]) => {
  selectedList.value = selection
}

const updateTableSelection = () => {
  if (!tableRef.value) return
  // 清除所有选中
  tableRef.value.clearSelection()
  // 选中已选择的行
  selectedList.value.forEach(row => {
    tableRef.value.toggleRowSelection(row, true)
  })
}

const handleSubmit = () => {
  if (selectedList.value.length !== expectedCount.value) {
    ElMessage.warning(`需要选择${expectedCount.value}个序列号`)
    return
  }
  emit('confirm', selectedList.value.map(s => s.serialNumber))
  visible.value = false
}

defineExpose({ open })
</script>

<style scoped>
.serial-selector-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-area {
  display: flex;
  gap: 8px;
}

.selected-info {
  font-size: 14px;
  color: var(--el-text-color-regular);
}
</style>
