<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="商品名">
          <el-input v-model="searchParams.goodsname" placeholder="商品名" clearable />
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="searchParams.productcode" placeholder="产品编码" clearable />
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="searchParams.providerid" placeholder="全部" clearable filterable>
            <el-option v-for="p in providers" :key="p.id" :label="p.providername" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchParams.categoryid" placeholder="全部" clearable filterable>
            <el-option v-for="c in categories" :key="c.id" :label="c.catename" :value="c.id" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllGoods" :search-params="searchParams">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image v-if="row.goodsimg" :src="getImageUrl(row.goodsimg)" style="width:50px;height:50px" fit="cover" :preview-src-list="[getImageUrl(row.goodsimg)]" />
          </template>
        </el-table-column>
        <el-table-column prop="goodsname" label="商品名" />
        <el-table-column prop="providername" label="供应商" />
        <el-table-column prop="categoryname" label="分类" />
        <el-table-column prop="productcode" label="产品编码" />
        <el-table-column prop="size" label="规格" />
        <el-table-column prop="goodspackage" label="单位" width="60" />
        <el-table-column prop="price" label="价格" width="80" />
        <el-table-column prop="number" label="库存" width="80" />
        <el-table-column prop="dangernum" label="预警值" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.available === 1 ? 'success' : 'danger'" size="small">
              {{ row.available === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button :type="row.available === 1 ? 'warning' : 'success'" link @click="handleToggleAvailable(row)">{{ row.available === 1 ? '下架' : '上架' }}</el-button>
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

    <CrudDialog ref="dialogRef" :submit-api="handleSubmitApi" :rules="rules" width="900px" label-position="top" @success="tableRef?.reload()">
      <template #default="{ formData }">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="商品名" prop="goodsname">
              <el-input v-model="formData.goodsname" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商" prop="providerid">
              <el-select v-model="formData.providerid" placeholder="选择供应商" filterable>
                <el-option v-for="p in providers" :key="p.id" :label="p.providername" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产品编码" prop="productcode">
              <el-input v-model="formData.productcode" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分类" prop="categoryid">
              <el-select v-model="formData.categoryid" placeholder="选择分类" filterable>
                <el-option v-for="c in categories" :key="c.id" :label="c.catename" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="产地编码" prop="promitcode">
              <el-input v-model="formData.promitcode" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格" prop="size">
              <el-input v-model="formData.size" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="goodspackage">
              <el-input v-model="formData.goodspackage" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="number">
              <el-input-number v-model="formData.number" :min="0" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预警值" prop="dangernum">
              <el-input-number v-model="formData.dangernum" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="formData.description" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="商品图片">
              <ImageUpload v-model="formData.goodsimg" />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
    </CrudDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import CrudDialog from '@/components/CrudDialog.vue'
import ImageUpload from '@/components/ImageUpload.vue'
import { loadAllGoods, addGoods, updateGoods, deleteGoods, updateGoodsAvailable } from '@/api/goods'
import { loadAllProviderForSelect } from '@/api/provider'
import { loadAllCategoryForSelect } from '@/api/category'
import { getImageUrl } from '@/api/file'

const tableRef = ref()
const dialogRef = ref()
const isEdit = ref(false)
const providers = ref<any[]>([])
const categories = ref<any[]>([])

const searchParams = reactive({
  goodsname: '',
  productcode: '',
  providerid: null as number | null,
  categoryid: null as number | null
})

const rules = {
  goodsname: [{ required: true, message: '请输入商品名', trigger: 'blur' }],
  providerid: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  categoryid: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.goodsname = ''
  searchParams.productcode = ''
  searchParams.providerid = null
  searchParams.categoryid = null
}
const handleAdd = () => { isEdit.value = false; dialogRef.value?.open({}, false) }
const handleEdit = (row: any) => { isEdit.value = true; dialogRef.value?.open(row, true) }
const handleSubmitApi = (data: any) => isEdit.value ? updateGoods(data) : addGoods(data)
const handleToggleAvailable = async (row: any) => {
  const newStatus = row.available === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确认${action}该商品？`, '提示', { type: 'warning' })
  await updateGoodsAvailable(row.id, newStatus)
  ElMessage.success(`${action}成功`)
  tableRef.value?.reload()
}
const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该商品？', '提示', { type: 'warning' })
  await deleteGoods(row.id, row.goodsimg)
  tableRef.value?.reload()
}

onMounted(async () => {
  try {
    const res: any = await loadAllProviderForSelect()
    providers.value = res.data || []
  } catch {}
  try {
    const res: any = await loadAllCategoryForSelect()
    categories.value = res.data || []
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
:deep(.el-col .el-form-item) {
  margin-bottom: 12px;
}
:deep(.el-input-number) {
  width: 100%;
}
</style>
