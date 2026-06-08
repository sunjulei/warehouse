<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="订单号">
          <el-input v-model="searchParams.orderNo" placeholder="输入订单号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchParams.recordType" placeholder="全部" clearable style="width: 120px">
            <el-option label="零售" value="retail" />
            <el-option label="加货" value="add" />
            <el-option label="退货" value="return" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
            @change="onDateChange"
          />
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadReturnAddRecords" :search-params="searchParams">
        <el-table-column prop="orderNo" label="订单号" min-width="90" />
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.typeTag" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" label="商品名称" min-width="120" />
        <el-table-column prop="goodsSize" label="规格" width="100" />
        <el-table-column prop="number" label="数量" width="80" align="center" />
        <el-table-column prop="price" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paytype" label="支付方式" width="80" />
        <el-table-column prop="operatePerson" label="操作员" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="160" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </CrudTable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadReturnAddRecords } from '@/api/retail'

const tableRef = ref()
const dateRange = ref<[string, string] | null>(null)
const searchParams = reactive({
  orderNo: null as string | null,
  recordType: null as string | null,
  startTime: null as string | null,
  endTime: null as string | null
})

const onDateChange = (val: [string, string] | null) => {
  if (val) {
    searchParams.startTime = val[0]
    searchParams.endTime = val[1]
  } else {
    searchParams.startTime = null
    searchParams.endTime = null
  }
}

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.orderNo = null
  searchParams.recordType = null
  searchParams.startTime = null
  searchParams.endTime = null
  dateRange.value = null
}
</script>
