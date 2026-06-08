<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">人员管理</h1>
      <p class="page-header-desc">管理系统用户账号和权限</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="用户名">
          <el-input v-model="searchParams.name" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="searchParams.address" placeholder="地址" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select v-model="searchParams.deptid" :data="deptTree" :props="{ label: 'title', value: 'id', children: 'children' }" placeholder="全部" clearable filterable check-strictly />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllUser" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="头像" width="60">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.imgpath ? getImageUrl(row.imgpath) : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="loginname" label="登录名" />
        <el-table-column prop="deptname" label="部门" />
        <el-table-column prop="leadername" label="直属领导" />
        <el-table-column prop="address" label="地址" />
        <el-table-column label="性别" width="60">
          <template #default="{ row }">{{ row.sex === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column label="可用" width="60">
          <template #default="{ row }">
            <el-tag :type="row.available === 1 ? 'success' : 'danger'" size="small">
              {{ row.available === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ordernum" label="排序" width="60" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="success" link @click="handleAssignRole(row)">分配角色</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #toolbar>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 添加
          </el-button>
        </template>
      </CrudTable>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="userRules" width="600px" @success="tableRef?.reload()">
      <template #default="{ formData, isEdit: editMode }">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" @blur="handleNameChange(formData)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录名" prop="loginname">
              <el-input v-model="formData.loginname" :disabled="editMode" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="deptid">
              <el-tree-select v-model="formData.deptid" :data="deptTree" :props="{ label: 'title', value: 'id', children: 'children' }" placeholder="选择部门" clearable filterable check-strictly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="直属领导" prop="mgr">
              <el-select v-model="formData.mgr" placeholder="选择领导" clearable filterable>
                <el-option v-for="u in deptUsers" :key="u.id" :label="u.name" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="formData.sex">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序号">
              <el-input-number v-model="formData.ordernum" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="formData.address" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
        <el-form-item label="是否可用">
          <el-radio-group v-model="formData.available">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="头像">
          <ImageUpload v-model="formData.imgpath" />
        </el-form-item>
      </template>
    </CrudDialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="600px">
      <el-table :data="allRoles" @selection-change="handleRoleSelectionChange" ref="roleTableRef">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色名" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveUserRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import ImageUpload from '@/components/ImageUpload.vue'
import { loadAllUser, addUser, updateUser, deleteUser, resetPwd, changeChineseToPinyin, loadUsersByDeptId, initRoleByUserId, saveUserRole } from '@/api/user'
import { loadDeptManagerLeftTreeJson } from '@/api/dept'
import { getImageUrl } from '@/api/file'

const tableRef = ref()
const dialogRef = ref()
const roleTableRef = ref()
const isEdit = ref(false)
const deptTree = ref<any[]>([])
const deptUsers = ref<any[]>([])
const allRoles = ref<any[]>([])
const roleDialogVisible = ref(false)
const currentUserId = ref(0)
const selectedRoles = ref<any[]>([])

const searchParams = reactive({ name: '', address: '', deptid: null as number | null })

const userRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  loginname: [{ required: true, message: '请输入登录名', trigger: 'blur' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.name = ''; searchParams.address = ''; searchParams.deptid = null }
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({ sex: 1, available: 1, ordernum: 1, imgpath: '' }, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateUser(data) : addUser(data)

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
  await deleteUser(row.id)
  tableRef.value?.reload()
}

const handleResetPwd = async (row: any) => {
  await ElMessageBox.confirm(`确认重置 ${row.name} 的密码为123456？`, '提示', { type: 'warning' })
  const res: any = await resetPwd(row.id)
  if (res.code === 200) ElMessage.success('重置成功')
}

const handleNameChange = async (formData: any) => {
  if (formData.name && !formData.loginname) {
    try {
      const res: any = await changeChineseToPinyin(formData.name)
      if (res.value) formData.loginname = res.value
    } catch {}
  }
}

const handleAssignRole = async (row: any) => {
  currentUserId.value = row.id
  try {
    const res: any = await initRoleByUserId(row.id)
    allRoles.value = res.data || []
    // 设置已选中
    roleDialogVisible.value = true
    setTimeout(() => {
      allRoles.value.forEach((r: any) => {
        if (r.LAY_CHECKED === true) {
          roleTableRef.value?.toggleRowSelection(r, true)
        }
      })
    }, 100)
  } catch {}
}

const handleRoleSelectionChange = (rows: any[]) => {
  selectedRoles.value = rows
}

const handleSaveUserRole = async () => {
  const ids = selectedRoles.value.map((r: any) => r.id)
  const res: any = await saveUserRole(currentUserId.value, ids)
  if (res.code === 200) {
    ElMessage.success('分配成功')
    roleDialogVisible.value = false
  }
}

onMounted(async () => {
  try {
    const res: any = await loadDeptManagerLeftTreeJson()
    deptTree.value = res.data || []
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
</style>
