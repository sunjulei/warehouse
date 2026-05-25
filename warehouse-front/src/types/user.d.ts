export interface User {
  id: number
  name: string
  loginname: string
  pwd?: string
  address: string
  sex: number
  remark: string
  deptid: number
  hiredate: string
  mgr: number
  available: number
  ordernum: number
  type: number
  imgpath: string
  salt?: string
  leadername?: string
  deptname?: string
}
