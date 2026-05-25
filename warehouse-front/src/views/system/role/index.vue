<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">角色管理</h1>
      <p class="page-header-desc">管理系统角色和权限分配</p>
    </div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="角色名">
          <el-input v-model="searchParams.name" placeholder="角色名" clearable />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="searchParams.remark" placeholder="备注" clearable />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllRole" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色名" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="可用" width="80">
          <template #default="{ row }">
            <el-tag :type="row.available === 1 ? 'success' : 'danger'" size="small">
              {{ row.available === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createtime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleAssignPermission(row)">分配权限</el-button>
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

    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="rules" width="500px" @success="tableRef?.reload()">
      <template #default="{ formData }">
        <el-form-item label="角色名" prop="name">
          <el-input v-model="formData.name" />
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
      </template>
    </CrudDialog>

    <!-- 分配权限弹窗 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-tree
        ref="permTreeRef"
        :data="permTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedKeys"
        :props="{ label: 'title', children: 'children' }"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadAllRole, addRole, updateRole, deleteRole, initPermissionByRoleId, saveRolePermission } from '@/api/role'

const tableRef = ref()
const dialogRef = ref()
const permTreeRef = ref()
const isEdit = ref(false)
const permDialogVisible = ref(false)
const permTree = ref<any[]>([])
const checkedKeys = ref<number[]>([])
const currentRoleId = ref(0)

const searchParams = reactive({ name: '', remark: '' })
const rules = { name: [{ required: true, message: '请输入角色名', trigger: 'blur' }] }

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.name = ''; searchParams.remark = '' }
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({ available: 1 }, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateRole(data) : addRole(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' })
  await deleteRole(row.id)
  tableRef.value?.reload()
}

const handleAssignPermission = async (row: any) => {
  currentRoleId.value = row.id
  try {
    const res: any = await initPermissionByRoleId(row.id)
    const nodes = res.data || []
    permTree.value = buildTree(nodes, 0)
    checkedKeys.value = nodes.filter((n: any) => n.checkArr === '1').map((n: any) => n.id)
    permDialogVisible.value = true
  } catch {}
}

const buildTree = (nodes: any[], parentId: number): any[] => {
  return nodes
    .filter((n: any) => n.parentId === parentId)
    .map((n: any) => ({ ...n, children: buildTree(nodes, n.id) }))
}

const handleSavePermission = async () => {
  const checked = permTreeRef.value?.getCheckedKeys() || []
  const halfChecked = permTreeRef.value?.getHalfCheckedKeys() || []
  const ids = [...checked, ...halfChecked]
  const res: any = await saveRolePermission(currentRoleId.value, ids)
  if (res.code === 200) {
    ElMessage.success('分配成功')
    permDialogVisible.value = false
  }
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
</style>
