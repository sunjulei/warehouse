import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '后台首页' }
        },
        {
          path: 'system/user',
          name: 'UserManager',
          component: () => import('@/views/system/user/index.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'system/user/profile',
          name: 'UserProfile',
          component: () => import('@/views/system/user/profile.vue'),
          meta: { title: '个人信息' }
        },
        {
          path: 'system/user/changePassword',
          name: 'ChangePassword',
          component: () => import('@/views/system/user/changePassword.vue'),
          meta: { title: '修改密码' }
        },
        {
          path: 'system/role',
          name: 'RoleManager',
          component: () => import('@/views/system/role/index.vue'),
          meta: { title: '角色管理' }
        },
        {
          path: 'system/dept',
          name: 'DeptManager',
          component: () => import('@/views/system/dept/index.vue'),
          meta: { title: '部门管理' }
        },
        {
          path: 'system/menu',
          name: 'MenuManager',
          component: () => import('@/views/system/menu/index.vue'),
          meta: { title: '菜单管理' }
        },
        {
          path: 'system/permission',
          name: 'PermissionManager',
          component: () => import('@/views/system/permission/index.vue'),
          meta: { title: '权限管理' }
        },
        {
          path: 'system/notice',
          name: 'NoticeManager',
          component: () => import('@/views/system/notice/index.vue'),
          meta: { title: '公告管理' }
        },
        {
          path: 'system/loginfo',
          name: 'LoginfoManager',
          component: () => import('@/views/system/loginfo/index.vue'),
          meta: { title: '登录日志' }
        },
        {
          path: 'system/operation-log',
          name: 'OperationLogManager',
          component: () => import('@/views/system/operation-log/index.vue'),
          meta: { title: '操作日志' }
        },
        {
          path: 'system/cache',
          name: 'CacheManager',
          component: () => import('@/views/system/cache/index.vue'),
          meta: { title: '缓存管理' }
        },
        {
          path: 'system/icon',
          name: 'IconManager',
          component: () => import('@/views/system/icon/index.vue'),
          meta: { title: '图标管理' }
        },
        {
          path: 'business/customer',
          name: 'CustomerManager',
          component: () => import('@/views/business/customer/index.vue'),
          meta: { title: '客户管理' }
        },
        {
          path: 'business/provider',
          name: 'ProviderManager',
          component: () => import('@/views/business/provider/index.vue'),
          meta: { title: '供应商管理' }
        },
        {
          path: 'business/category',
          name: 'CategoryManager',
          component: () => import('@/views/business/category/index.vue'),
          meta: { title: '商品分类' }
        },
        {
          path: 'business/goods',
          name: 'GoodsManager',
          component: () => import('@/views/business/goods/index.vue'),
          meta: { title: '商品管理' }
        },
        {
          path: 'business/inport',
          name: 'InportManager',
          component: () => import('@/views/business/inport/index.vue'),
          meta: { title: '进货管理' }
        },
        {
          path: 'business/outport',
          name: 'OutportManager',
          component: () => import('@/views/business/outport/index.vue'),
          meta: { title: '退货管理' }
        },
        {
          path: 'business/sales',
          name: 'SalesManager',
          component: () => import('@/views/business/sales/index.vue'),
          meta: { title: '销售管理' }
        },
        {
          path: 'business/salesback',
          name: 'SalesbackManager',
          component: () => import('@/views/business/salesback/index.vue'),
          meta: { title: '销售退货' }
        },
        {
          path: 'business/retail',
          name: 'RetailManager',
          component: () => import('@/views/business/retail/index.vue'),
          meta: { title: '散客零售' }
        },
        {
          path: 'business/retailback',
          name: 'RetailbackManager',
          component: () => import('@/views/business/retailback/index.vue'),
          meta: { title: '零售退货' }
        },
        {
          path: 'business/report',
          name: 'ReportManager',
          component: () => import('@/views/business/report/index.vue'),
          meta: { title: '报表管理' }
        },
        {
          path: 'business/inport-analysis',
          name: 'InportAnalysis',
          component: () => import('@/views/business/inport-analysis/index.vue'),
          meta: { title: '进销金额分析' }
        },
        {
          path: 'business/goods-analysis',
          name: 'GoodsAnalysis',
          component: () => import('@/views/business/goods-analysis/index.vue'),
          meta: { title: '进销商品分析' }
        },
        {
          path: 'business/profit-analysis',
          name: 'ProfitAnalysis',
          component: () => import('@/views/business/profit-analysis/index.vue'),
          meta: { title: '利润分析' }
        },
        {
          path: 'business/stocktake',
          name: 'StocktakeManager',
          component: () => import('@/views/business/stocktake/index.vue'),
          meta: { title: '盘点管理' }
        },
        {
          path: 'business/member',
          name: 'MemberManager',
          component: () => import('@/views/business/member/index.vue'),
          meta: { title: '会员管理' }
        }
      ]
    }
  ]
})

NProgress.configure({ showSpinner: false })

router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  document.title = ((to.meta.title as string) || '仓库管理系统') + ' - 仓库管理系统'

  if (to.path === '/login') {
    next()
    return
  }

  const authStore = useAuthStore()

  if (!authStore.isLoggedIn) {
    try {
      await authStore.fetchCurrentUser()
      next()
    } catch (e) {
      console.error('路由守卫认证失败:', e)
      next('/login')
    }
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
