<template>
  <Teleport to="body">
    <div v-if="visible" class="print-doc-root">
      <div class="print-doc">
        <h1 class="print-title">{{ title }}</h1>

        <div v-if="orderNo" class="print-barcode">
          <svg ref="barcodeRef"></svg>
        </div>

        <div class="print-meta">
          <div v-for="(item, index) in meta" :key="index" class="print-meta-item">
            <span class="print-meta-label">{{ item.label }}:</span>
            <span class="print-meta-value">{{ item.value ?? '-' }}</span>
          </div>
        </div>

        <table class="print-table">
          <thead>
            <tr>
              <th class="col-index">#</th>
              <th
                v-for="col in columns"
                :key="col.key"
                :style="{ textAlign: col.align || 'left', width: col.width }"
              >
                {{ col.label }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, rowIndex) in rows" :key="rowIndex">
              <td class="col-index">{{ rowIndex + 1 }}</td>
              <td
                v-for="col in columns"
                :key="col.key"
                :style="{ textAlign: col.align || 'left' }"
              >
                {{ col.format ? col.format(row) : row[col.key] ?? '-' }}
              </td>
            </tr>
            <tr v-if="rows.length === 0">
              <td :colspan="columns.length + 1" class="print-empty">暂无明细</td>
            </tr>
          </tbody>
        </table>

        <div v-if="summary.length > 0" class="print-summary">
          <span v-for="(item, index) in summary" :key="index" class="print-summary-item">
            {{ item.label }}: <strong>{{ item.value }}</strong>
          </span>
        </div>

        <div v-if="remark" class="print-remark">备注: {{ remark }}</div>

        <div class="print-signatures">
          <span v-for="(s, index) in signatures" :key="index">{{ s }}: ________________</span>
        </div>

        <div class="print-footer">打印时间: {{ printTime }}</div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import JsBarcode from 'jsbarcode'

export interface PrintColumn {
  key: string
  label: string
  align?: 'left' | 'center' | 'right'
  width?: string
  format?: (row: any) => string
}

export interface PrintMetaItem {
  label: string
  value: string | number | null | undefined
}

const props = withDefaults(defineProps<{
  visible: boolean
  title: string
  orderNo?: string
  meta?: PrintMetaItem[]
  columns: PrintColumn[]
  rows: any[]
  summary?: PrintMetaItem[]
  remark?: string
  signatures?: string[]
}>(), {
  orderNo: '',
  meta: () => [],
  summary: () => [],
  remark: '',
  signatures: () => ['制单人', '签收人', '日期']
})

const emit = defineEmits(['update:visible'])

const barcodeRef = ref<SVGElement>()
const printTime = ref('')

const renderBarcode = () => {
  if (props.orderNo && barcodeRef.value) {
    JsBarcode(barcodeRef.value, props.orderNo, {
      format: 'CODE128',
      width: 1.6,
      height: 40,
      displayValue: true,
      fontSize: 13,
      margin: 0,
      textMargin: 2,
      lineColor: '#000'
    })
  }
}

watch(() => props.visible, async (v) => {
  if (!v) return
  printTime.value = new Date().toLocaleString('zh-CN', { hour12: false })
  await nextTick()
  renderBarcode()
  // 隐藏系统 UI，只输出打印模板
  document.body.classList.add('printing')
  await nextTick()
  window.print()
})

// 打印对话框关闭（打印或取消）后恢复界面
const handleAfterPrint = () => {
  document.body.classList.remove('printing')
  emit('update:visible', false)
}

onMounted(() => window.addEventListener('afterprint', handleAfterPrint))
onUnmounted(() => {
  window.removeEventListener('afterprint', handleAfterPrint)
  document.body.classList.remove('printing')
})
</script>

<style>
/* 打印时隐藏页面上除打印模板外的所有内容（el-dialog 等也直接挂在 body 下） */
.print-doc-root {
  display: none;
}

@media print {
  body.printing > *:not(.print-doc-root) {
    display: none !important;
  }

  body.printing .print-doc-root {
    display: block !important;
  }

  @page {
    margin: 12mm;
  }
}
</style>

<style scoped>
.print-doc {
  font-family: 'SimSun', 'Songti SC', serif;
  color: #000;
  background: #fff;
  padding: 8px;
  font-size: 13px;
}

.print-title {
  text-align: center;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 8px;
  margin: 0 0 8px;
}

.print-barcode {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.print-meta {
  display: flex;
  flex-wrap: wrap;
  border-top: 1.5px solid #000;
  border-bottom: 1px solid #000;
  padding: 6px 0;
  margin-bottom: 10px;
}

.print-meta-item {
  width: 33.33%;
  padding: 2px 0;
  font-size: 13px;
}

.print-meta-label {
  color: #333;
  margin-right: 4px;
}

.print-meta-value {
  font-weight: 600;
}

.print-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 10px;
}

.print-table th,
.print-table td {
  border: 1px solid #000;
  padding: 5px 8px;
  font-size: 13px;
}

.print-table th {
  background: #f0f0f0;
  font-weight: 700;
}

.print-table .col-index {
  width: 36px;
  text-align: center;
}

.print-empty {
  text-align: center;
  color: #666;
}

.print-summary {
  display: flex;
  justify-content: flex-end;
  gap: 28px;
  padding: 6px 0;
  border-top: 1px solid #000;
  border-bottom: 1px solid #000;
  margin-bottom: 10px;
  font-size: 14px;
}

.print-summary-item strong {
  font-size: 15px;
}

.print-remark {
  font-size: 13px;
  margin-bottom: 14px;
  word-break: break-all;
}

.print-signatures {
  display: flex;
  justify-content: space-between;
  margin-top: 28px;
  font-size: 13px;
}

.print-footer {
  margin-top: 16px;
  text-align: right;
  font-size: 11px;
  color: #666;
}
</style>
