import { defineStore } from 'pinia'
import { login as loginApi, currentUser as currentUserApi, logout as logoutApi } from '@/api/login'
import type { User } from '@/types/user'
import type { TreeNode } from '@/types/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    menus: [] as TreeNode[],
    permissions: [] as string[],
    isLoggedIn: false
  }),
  actions: {
    async login(loginname: string, pwd: string, code: string) {
      const res: any = await loginApi({ loginname, pwd, code })
      if (res.code === 200) {
        await this.fetchCurrentUser()
      }
      return res
    },
    async fetchCurrentUser() {
      try {
        const res: any = await currentUserApi()
        if (res && res.code === 200) {
          this.user = res.user || null
          this.menus = res.menus || []
          this.permissions = res.permissions || []
          this.isLoggedIn = true
        } else {
          console.error('currentUser返回异常:', res)
          throw new Error(res?.msg || '获取用户信息失败')
        }
      } catch (e) {
        console.error('fetchCurrentUser失败:', e)
        this.user = null
        this.menus = []
        this.permissions = []
        this.isLoggedIn = false
        throw e
      }
    },
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.user = null
        this.menus = []
        this.permissions = []
        this.isLoggedIn = false
      }
    }
  }
})
