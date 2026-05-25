<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">权限管理</h1>
      <p class="page-header-desc">管理系统权限和访问控制</p>
    </div>
    <el-row :gutter="16" style="height: calc(100vh - 160px)">
    <el-col :span="6" style="height: 100%">
      <TreePanel ref="treeRef" title="权限管理" :load-api="loadPermissionManagerLeftTreeJson" @node-click="handleNodeClick" />
    </el-col>
    <el-col :span="18">
      <el-card>
        <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
          <el-form-item label="权限名">
            <el-input v-model="searchParams.title" placeholder="权限名" clearable />
          </el-form-item>
          <el-form-item label="权限码">
            <el-input v-model="searchParams.percode" placeholder="权限码" clearable />
          </el-form-item>
        </SearchForm>

        <CrudTable ref="tableRef" :load-api="loadAllPermission" :search-params="searchParams">
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="权限名" />
          <el-table-column prop="percode" label="权限码" />
          <el-table-column prop="ordernum" label="排序" width="60" />
          <el-table-column label="可用" width="60">
            <template #default="{ row }">
              <el-tag :type="row.available === 1 ? 'success' : 'danger'" size="small">
                {{ row.available === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
    </el-col>
  </el-row>

  <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="rules" width="500px" @success="handleDialogSuccess">
    <template #default="{ formData }">
      <el-form-item label="上级权限" prop="pid">
        <el-tree-select v-model="formData.pid" :data="permTreeForSelect" :props="{ label: 'title', value: 'id', children: 'children' }" placeholder="选择上级权限" clearable check-strictly />
      </el-form-item>
      <el-form-item label="权限名" prop="title">
        <el-input v-model="formData.title" />
      </el-form-item>
      <el-form-item label="权限码" prop="percode">
        <el-input v-model="formData.percode" placeholder="如: goods:create" />
      </el-form-item>
      <el-form-item label="排序号">
        <el-input-number v-model="formData.ordernum" :min="0" />
      </el-form-item>
      <el-form-item label="是否可用">
        <el-radio-group v-model="formData.available">
          <el-radio :value="1">是</el-radio>
          <el-radio :value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
    </template>
  </CrudDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import TreePanel from '@/components/TreePanel.vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import { loadPermissionManagerLeftTreeJson, loadAllPermission, addPermission, updatePermission, deletePermission, loadPermissionMaxOrderNum } from '@/api/permission'

const treeRef = ref()
const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const selectedPermId = ref<number | null>(null)
const permTreeForSelect = ref<any[]>([])

const searchParams = reactive({ title: '', percode: '', id: null as number | null })
const rules = {
  title: [{ required: true, message: '请输入权限名', trigger: 'blur' }],
  percode: [{ required: true, message: '请输入权限码', trigger: 'blur' }]
}

const handleNodeClick = (data: any) => {
  selectedPermId.value = data.id
  searchParams.id = data.id
  tableRef.value?.reload()
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.title = ''; searchParams.percode = ''; searchParams.id = selectedPermId.value }

const loadPermTree = async () => {
  try {
    const res: any = await loadPermissionManagerLeftTreeJson()
    permTreeForSelect.value = res.data || []
  } catch {
    permTreeForSelect.value = []
  }
}

const handleAdd = async () => {
  isEdit.value = false
  await loadPermTree()
  try {
    const res: any = await loadPermissionMaxOrderNum()
    dialogRef.value?.open({ pid: selectedPermId.value, available: 1, ordernum: res.value || 1 }, false)
  } catch {
    dialogRef.value?.open({ pid: selectedPermId.value, available: 1 }, false)
  }
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  await loadPermTree()
  dialogRef.value?.open(row, true)
}
const handleSubmitApi = (data: any) => isEdit.value ? updatePermission(data) : addPermission(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该权限？', '提示', { type: 'warning' })
  await deletePermission(row.id)
  tableRef.value?.reload()
  treeRef.value?.loadData()
}

const handleDialogSuccess = () => {
  tableRef.value?.reload()
  treeRef.value?.loadData()
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
