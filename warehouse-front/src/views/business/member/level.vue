<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">等级规则</h1>
      <p class="page-header-desc">配置会员等级规则和权益</p>
    </div>

    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: 600;">会员等级规则配置</span>
          <el-button type="primary" size="small" @click="handleAddLevel">
            <el-icon><Plus /></el-icon> 新增等级
          </el-button>
        </div>
      </template>
      <el-table :data="levelRules" border stripe>
        <el-table-column prop="sortOrder" label="排序" width="60" align="center" />
        <el-table-column prop="levelName" label="等级名称" width="120">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.levelValue)" effect="dark" round>{{ row.levelName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="达标条件" min-width="220">
          <template #default="{ row }">
            <span v-if="row.minRecharge > 0">充值满 ¥{{ row.minRecharge?.toFixed(0) }}</span>
            <span v-if="row.minRecharge > 0 && row.minConsume > 0" style="margin: 0 6px; color: #909399;">{{ row.conditionType === 2 ? '且' : '或' }}</span>
            <span v-if="row.minConsume > 0">消费满 ¥{{ row.minConsume?.toFixed(0) }}</span>
            <span v-if="!row.minRecharge && !row.minConsume" style="color: #909399;">无门槛</span>
          </template>
        </el-table-column>
        <el-table-column label="折扣率" width="100" align="center">
          <template #default="{ row }">{{ row.discountRate }}%</template>
        </el-table-column>
        <el-table-column label="积分倍率" width="100" align="center">
          <template #default="{ row }">{{ row.pointsRate }}x</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditLevel(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 等级规则编辑弹窗 -->
    <el-dialog v-model="levelDialogVisible" :title="editingLevel.id ? '编辑等级' : '新增等级'" width="680px">
      <el-form :model="editingLevel" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="等级名称" required>
              <el-input v-model="editingLevel.levelName" placeholder="如：银卡会员" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="等级值" required>
              <el-input-number v-model="editingLevel.levelValue" :min="1" :max="99" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序">
              <el-input-number v-model="editingLevel.sortOrder" :min="0" :max="99" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">达标条件</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="充值达标">
              <el-input-number v-model="editingLevel.minRecharge" :min="0" :precision="0" style="width: 100%;" placeholder="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="消费达标">
              <el-input-number v-model="editingLevel.minConsume" :min="0" :precision="0" style="width: 100%;" placeholder="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="达标关系">
              <el-select v-model="editingLevel.conditionType" style="width: 100%;">
                <el-option label="满足其一" :value="1" />
                <el-option label="同时满足" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">权益配置</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="折扣率">
              <el-input-number v-model="editingLevel.discountRate" :min="0" :max="100" :precision="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="积分倍率">
              <el-input-number v-model="editingLevel.pointsRate" :min="0" :max="100" :precision="1" :step="0.5" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="备注">
              <el-input v-model="editingLevel.remark" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="levelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveLevel" :loading="levelSaving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { loadLevelRules, saveLevelRule } from '@/api/member'

const levelRules = ref<any[]>([])
const levelDialogVisible = ref(false)
const editingLevel = reactive<any>({
  id: null, levelName: '', levelValue: 1,
  minRecharge: 0, minConsume: 0, conditionType: 1,
  discountRate: 100, pointsRate: 1,
  sortOrder: 0, remark: ''
})
const levelSaving = ref(false)

function levelTag(level: number) {
  return { 1: 'info', 2: '', 3: 'warning', 4: 'danger' }[level] ?? 'info'
}

async function loadLevelData() {
  try {
    const res: any = await loadLevelRules()
    levelRules.value = res.data || []
  } catch {}
}

function handleAddLevel() {
  Object.assign(editingLevel, {
    id: null, levelName: '', levelValue: levelRules.value.length + 1,
    minRecharge: 0, minConsume: 0, conditionType: 1,
    discountRate: 100, pointsRate: 1,
    sortOrder: levelRules.value.length + 1, remark: ''
  })
  levelDialogVisible.value = true
}

function handleEditLevel(row: any) {
  const { createTime, ...rest } = row
  Object.assign(editingLevel, rest)
  levelDialogVisible.value = true
}

async function handleSaveLevel() {
  if (!editingLevel.levelName) return ElMessage.warning('请输入等级名称')
  levelSaving.value = true
  try {
    const res: any = await saveLevelRule({ ...editingLevel })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      levelDialogVisible.value = false
      await loadLevelData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch {} finally {
    levelSaving.value = false
  }
}

onMounted(() => {
  loadLevelData()
})
</script>
