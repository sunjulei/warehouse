<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">会员列表</h1>
      <p class="page-header-desc">管理会员信息和储值卡</p>
    </div>

    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="姓名">
          <el-input v-model="searchParams.name" placeholder="会员姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchParams.phone" placeholder="手机号" clearable />
        </el-form-item>
        <el-form-item label="卡号">
          <el-input v-model="searchParams.memberNo" placeholder="会员卡号" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllMember" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="memberNo" label="会员卡号" width="160" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="balance" label="余额" width="100" align="right">
          <template #default="{ row }">
            <span style="color: var(--success-color); font-weight: 600;">¥{{ row.balance?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.level)" effect="dark" round>{{ levelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleRecharge(row)">充值</el-button>
            <el-button type="warning" link @click="handleConsume(row)">消费</el-button>
            <el-button type="success" link @click="handleRecords(row)">记录</el-button>
            <el-button type="info" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #toolbar>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 添加会员
          </el-button>
        </template>
      </CrudTable>
    </el-card>

    <!-- 添加/编辑会员弹窗 -->
    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="memberRules" width="500px" @success="tableRef?.reload()">
      <template #default="{ formData }">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio label="男" />
            <el-radio label="女" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </template>
    </CrudDialog>

    <!-- 充值/消费弹窗 -->
    <el-dialog v-model="moneyDialogVisible" :title="moneyDialogTitle" width="400px">
      <el-form label-width="80px">
        <el-form-item label="会员">
          <span style="font-weight: 600;">{{ currentMember?.name }} ({{ currentMember?.memberNo }})</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span style="color: var(--success-color); font-weight: 600;">¥{{ currentMember?.balance?.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="moneyAmount" :min="0.01" :precision="2" style="width: 200px;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="moneyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMoneySubmit" :loading="moneyLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 充值/消费记录弹窗 -->
    <el-dialog v-model="recordsDialogVisible" title="储值卡流水" width="800px">
      <el-table :data="memberRecords" border stripe size="small">
        <el-table-column prop="type" label="类型" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '充值' ? 'success' : row.type === '消费' ? 'primary' : 'warning'" size="small" effect="dark" round>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="110" align="right">
          <template #default="{ row }">
            <span :style="{ color: row.type === '消费' ? 'var(--danger-color)' : 'var(--success-color)', fontWeight: 600 }">
              {{ row.type === '消费' ? '-' : '+' }}¥{{ row.amount?.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作后余额" width="110" align="right">
          <template #default="{ row }">¥{{ row.balanceAfter?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="80" align="center" />
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import {
  loadAllMember, addMember, updateMember,
  recharge, consume, loadMemberRecords, deleteMember,
  loadLevelRules
} from '@/api/member'

const tableRef = ref()
const dialogRef = ref()
const searchParams = reactive({ name: '', phone: '', memberNo: '' })

const moneyDialogVisible = ref(false)
const moneyDialogTitle = ref('')
const moneyType = ref<'recharge' | 'consume'>('recharge')
const currentMember = ref<any>(null)
const moneyAmount = ref(100)
const moneyLoading = ref(false)

const recordsDialogVisible = ref(false)
const memberRecords = ref<any[]>([])

const memberRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

// 等级规则（用于显示等级名称）
const levelRules = ref<any[]>([])

function levelText(level: number) {
  const found = levelRules.value.find((r: any) => r.levelValue === level)
  return found ? found.levelName : ({ 1: '普通', 2: '银卡', 3: '金卡', 4: '钻石' }[level] ?? '普通')
}

function levelTag(level: number) {
  return { 1: 'info', 2: '', 3: 'warning', 4: 'danger' }[level] ?? 'info'
}

function handleSearch() { tableRef.value?.reload() }
function handleReset() {
  searchParams.name = ''; searchParams.phone = ''; searchParams.memberNo = ''
  tableRef.value?.reload()
}

function handleAdd() { dialogRef.value?.open() }
function handleEdit(row: any) { dialogRef.value?.open(row) }
const handleSubmitApi = (data: any) => data.id ? updateMember(data) : addMember(data)

function handleRecharge(row: any) {
  currentMember.value = row
  moneyType.value = 'recharge'
  moneyDialogTitle.value = '会员充值'
  moneyAmount.value = 100
  moneyDialogVisible.value = true
}

function handleConsume(row: any) {
  currentMember.value = row
  moneyType.value = 'consume'
  moneyDialogTitle.value = '会员消费'
  moneyAmount.value = 100
  moneyDialogVisible.value = true
}

async function handleMoneySubmit() {
  moneyLoading.value = true
  try {
    const api = moneyType.value === 'recharge' ? recharge : consume
    const res: any = await api({ memberId: currentMember.value.id, amount: moneyAmount.value })
    if (res.code === 200) {
      ElMessage.success(res.msg)
      moneyDialogVisible.value = false
      tableRef.value?.reload()
    } else {
      ElMessage.error(res.msg)
    }
  } catch {} finally {
    moneyLoading.value = false
  }
}

async function handleRecords(row: any) {
  recordsDialogVisible.value = true
  try {
    const res: any = await loadMemberRecords({ memberId: row.id })
    memberRecords.value = res.data || []
  } catch { memberRecords.value = [] }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除会员「${row.name}」？`, '确认', { type: 'warning' })
    await deleteMember({ id: row.id })
    ElMessage.success('删除成功')
    tableRef.value?.reload()
  } catch {}
}

onMounted(() => {
  loadLevelData()
})

async function loadLevelData() {
  try {
    const res: any = await loadLevelRules()
    levelRules.value = res.data || []
  } catch {}
}
</script>
