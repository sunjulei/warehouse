<template>
  <div class="page-container">
    <div class="page-header animate-fade-in-up">
      <h1 class="page-header-title">菜单管理</h1>
      <p class="page-header-desc">管理系统菜单和导航配置</p>
    </div>
    <el-row :gutter="16" style="height: calc(100vh - 160px)">
    <el-col :span="6" style="height: 100%">
      <TreePanel ref="treeRef" title="菜单管理" :load-api="loadMenuManagerLeftTreeJson" @node-click="handleNodeClick" />
    </el-col>
    <el-col :span="18">
      <el-card>
        <CrudTable ref="tableRef" :load-api="loadAllMenu" :search-params="searchParams">
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="菜单名" />
          <el-table-column prop="icon" label="图标" width="80">
            <template #default="{ row }">
              <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
            </template>
          </el-table-column>
          <el-table-column prop="href" label="路径" />
          <el-table-column label="展开" width="60">
            <template #default="{ row }">
              <el-tag :type="row.open === 1 ? 'success' : 'info'" size="small">
                {{ row.open === 1 ? '是' : '否' }}
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
      <el-form-item label="上级菜单" prop="pid">
        <el-tree-select v-model="formData.pid" :data="menuTreeForSelect" :props="{ label: 'title', value: 'id', children: 'children' }" placeholder="选择上级菜单" clearable check-strictly />
      </el-form-item>
      <el-form-item label="菜单名" prop="title">
        <el-input v-model="formData.title" />
      </el-form-item>
      <el-form-item label="图标">
        <IconPicker v-model="formData.icon" />
      </el-form-item>
      <el-form-item label="路径">
        <el-input v-model="formData.href" />
      </el-form-item>
      <el-form-item label="排序号">
        <el-input-number v-model="formData.ordernum" :min="0" />
      </el-form-item>
      <el-form-item label="是否展开">
        <el-radio-group v-model="formData.open">
          <el-radio :value="1">是</el-radio>
          <el-radio :value="0">否</el-radio>
        </el-radio-group>
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
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import IconPicker from '@/components/IconPicker.vue'
import { loadMenuManagerLeftTreeJson, loadAllMenu, addMenu, updateMenu, deleteMenu, loadMenuMaxOrderNum } from '@/api/menu'

const treeRef = ref()
const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const selectedMenuId = ref<number | null>(null)
const menuTreeForSelect = ref<any[]>([])

const searchParams = reactive({ id: null as number | null, title: '' })
const rules = { title: [{ required: true, message: '请输入菜单名', trigger: 'blur' }] }

const handleNodeClick = (data: any) => {
  selectedMenuId.value = data.id
  searchParams.id = data.id
  tableRef.value?.reload()
}

const loadMenuTree = async () => {
  try {
    const res: any = await loadMenuManagerLeftTreeJson()
    const children = res.data || []
    // 添加顶级菜单根节点（id=1），选择它表示将菜单设为顶级
    menuTreeForSelect.value = [{ id: 1, title: '顶级菜单', children }]
  } catch {
    menuTreeForSelect.value = []
  }
}

const handleAdd = async () => {
  isEdit.value = false
  await loadMenuTree()
  try {
    const res: any = await loadMenuMaxOrderNum()
    dialogRef.value?.open({ pid: selectedMenuId.value, available: 1, open: 1, ordernum: res.value || 1 }, false)
  } catch {
    dialogRef.value?.open({ pid: selectedMenuId.value, available: 1, open: 1 }, false)
  }
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  await loadMenuTree()
  dialogRef.value?.open(row, true)
}
const handleSubmitApi = (data: any) => isEdit.value ? updateMenu(data) : addMenu(data)
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该菜单？', '提示', { type: 'warning' })
  await deleteMenu(row.id)
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
