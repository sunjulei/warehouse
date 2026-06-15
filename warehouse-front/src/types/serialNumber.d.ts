export interface SerialNumber {
  id: number
  serialNumber: string
  goodsid: number
  inportid?: number
  status: number
  instockTime?: string
  outstockTime?: string
  remark?: string
  isdelete?: number
  // 非数据库字段
  goodsname?: string
  size?: string
}
