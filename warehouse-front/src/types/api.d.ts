export interface DataGridView<T = any> {
  code: number
  msg: string
  count: number
  data: T[]
}

export interface ResultObj {
  code: number
  msg: string
}

export interface TreeNode {
  id: number
  parentId: number
  title: string
  icon?: string
  href?: string
  spread?: boolean
  children?: TreeNode[]
  checkArr?: string
}
