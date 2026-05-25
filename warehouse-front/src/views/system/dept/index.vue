<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">部门管理</h1>
      <p class="page-header-desc">管理组织架构和部门信息</p>
    </div>
    <el-row :gutter="16" style="height: calc(100vh - 160px)">
    <el-col :span="6" style="height: 100%">
      <TreePanel ref="treeRef" title="部门管理" :load-api="loadDeptManagerLeftTreeJson" @node-click="handleNodeClick" />
    </el-col>
    <el-col :span="18">
      <el-card>
        <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
          <el-form-item label="部门名">
            <el-input v-model="searchParams.name" placeholder="部门名" clearable />
          </el-form-item>
        </SearchForm>

        <CrudTable ref="tableRef" :load-api="loadAllDept" :search-params="searchParams">
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="部门名" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="可用" width="60">
            <template #default="{ row }">
              <el-tag :type="row.available === 1 ? 'success' : 'danger'" size="small">
                {{ row.available === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ordernum" label="排序" width="60" />
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
      <el-form-item label="上级部门" prop="pid">
        <el-tree-select v-model="formData.pid" :data="deptTreeForSelect" :props="{ label: 'title', value: 'id', children: 'children' }" placeholder="选择上级部门" clearable check-strictly />
      </el-form-item>
      <el-form-item label="部门名" prop="title">
        <el-input v-model="formData.title" />
      </el-form-item>
      <el-form-item label="地址">
        <el-input v-model="formData.address" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" />
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
import { loadDeptManagerLeftTreeJson, loadAllDept, addDept, updateDept, deleteDept, loadDeptMaxOrderNum } from '@/api/dept'

const treeRef = ref()
const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const selectedDeptId = ref<number | null>(null)
const deptTreeForSelect = ref<any[]>([])

const searchParams = reactive({ name: '', id: null as number | null })
const rules = { title: [{ required: true, message: '请输入部门名', trigger: 'blur' }] }

const handleNodeClick = (data: any) => {
  selectedDeptId.value = data.id
  searchParams.id = data.id
  tableRef.value?.reload()
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => { searchParams.name = ''; searchParams.id = selectedDeptId.value }

const loadDeptTree = async () => {
  try {
    const res: any = await loadDeptManagerLeftTreeJson()
    deptTreeForSelect.value = res.data || []
  } catch {
    deptTreeForSelect.value = []
  }
}

const handleAdd = async () => {
  isEdit.value = false
  await loadDeptTree()
  try {
    const res: any = await loadDeptMaxOrderNum()
    dialogRef.value?.open({ pid: selectedDeptId.value, available: 1, ordernum: res.value || 1 }, false)
  } catch {
    dialogRef.value?.open({ pid: selectedDeptId.value, available: 1 }, false)
  }
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  await loadDeptTree()
  dialogRef.value?.open(row, true)
}
const handleSubmitApi = (data: any) => isEdit.value ? updateDept(data) : addDept(data)

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该部门？', '提示', { type: 'warning' })
  await deleteDept(row.id)
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
