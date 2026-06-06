# 仓储脉搏 移动端重新设计方案

日期：2026-06-06

## 1. 背景与目标

当前 uni-app x 移动端有 4 个底部 tab（首页、业务、查询、我的），功能入口分散在多个导航页面中，且完全未接入后端的菜单权限体系。

**目标**：
- 精简为 2 个底部 tab（工作台 + 我的），提升操作效率
- 接入后端 RBAC 权限，根据用户角色动态显示/隐藏功能入口
- 右上角增加公告入口，对接后端通知模块

## 2. 核心设计决策

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 底部 Tab | 2个：工作台 + 我的 | 精简导航，移动端功能不需要4个tab |
| 工作台布局 | 宫格导航 + 统计卡片 | 类企业微信工作台风格，一屏展示全部功能 |
| 权限控制 | 隐藏无权限功能 | 移动端屏幕小，不显示比置灰更清晰 |
| 公告 | 铃铛图标 → 列表页 → 详情页 | 完整的公告阅读体验 |
| 我的 Tab | 保持现状 + 操作日志 | 补齐设计规范中缺失的日志功能 |

## 3. 技术方案

### 3.1 架构：静态路由 + 动态权限过滤

- 所有页面在 `pages.json` 中静态注册（uni-app x 要求）
- 工作台宫格列表在代码中定义，每项绑定后端菜单 `href` 作为权限标识
- 登录后从 `/login/currentUser` 获取 `menus` 和 `permissions` 存入 auth store
- 工作台渲染时根据权限过滤宫格项
- 超级管理员（`permissions` 包含 `"*:*"`）显示全部

### 3.2 页面结构变更

**新增页面**：
- `pages/workbench/index.uvue` — 工作台（替代原首页+业务中心+查询中心）
- `pages/notice/list.uvue` — 公告列表
- `pages/notice/detail.uvue` — 公告详情
- `pages/profile/operation-log.uvue` — 操作日志

**保留页面**：
- `pages/login/login.uvue` — 登录
- `pages/business/goods.uvue` — 商品管理
- `pages/business/goods-detail.uvue` — 商品详情
- `pages/business/inport.uvue` — 进货入库
- `pages/business/sales.uvue` — 销售开单
- `pages/business/stocktake.uvue` — 盘点管理
- `pages/business/stocktake-item.uvue` — 盘点录入
- `pages/query/customer-detail.uvue` — 客户详情
- `pages/query/provider-detail.uvue` — 供应商详情
- `pages/profile/index.uvue` — 我的
- `pages/profile/profile-edit.uvue` — 编辑资料
- `pages/profile/change-password.uvue` — 修改密码

**删除页面**：
- `pages/index/index.uvue` — 被工作台替代
- `pages/business/index.uvue` — 业务中心导航页不再需要
- `pages/query/index.uvue` — 查询中心导航页不再需要

### 3.3 Tab Bar 配置

```json
{
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#409EFF",
    "backgroundColor": "#FFFFFF",
    "borderStyle": "black",
    "list": [
      {
        "pagePath": "pages/workbench/index",
        "text": "工作台",
        "iconPath": "static/tab-home.png",
        "selectedIconPath": "static/tab-home-active.png"
      },
      {
        "pagePath": "pages/profile/index",
        "text": "我的",
        "iconPath": "static/tab-profile.png",
        "selectedIconPath": "static/tab-profile-active.png"
      }
    ]
  }
}
```

### 3.4 工作台页面设计

**导航栏**：标题"工作台"，右侧铃铛图标（公告入口）

**统计区域**（2×2 宫格卡片）：

| 卡片 | 数据来源 | 颜色 |
|------|----------|------|
| 商品总数 | `loadDashboardStats().totalGoods` | 主色 #409EFF |
| 库存预警 | `loadDashboardStats().warningCount` | 警告 #E6A23C |
| 今日入库 | `loadDashboardStats().todayInport` | 成功 #67C23A |
| 今日销售 | `loadDashboardStats().todaySales` | 信息 #909399 |

**功能宫格区域**（标题"常用功能"）：

| 宫格 | 标题 | 跳转页面 | 权限 menuHref（对应后端 HREF_MAP） |
|------|------|----------|-------------------------------------|
| 📥 | 进货入库 | /business/inport | /business/inport |
| 🛒 | 销售开单 | /business/sales | /business/sales |
| 📋 | 盘点管理 | /business/stocktake | /business/stocktake（见下方说明） |
| 📦 | 商品管理 | /business/goods | /business/goods |
| 👥 | 客户查询 | /query/customer | /business/customer |
| 🏭 | 供应商查询 | /query/provider | /business/provider |

**注意**：后端 HREF_MAP 中客户/供应商的映射是 `/business/customer` 和 `/business/provider`，移动端页面路由用 `/query/customer` 和 `/query/provider`，但权限判断时使用 HREF_MAP 中的值进行匹配。

**盘点权限说明**：后端 HREF_MAP 中没有盘点路由条目。需要在 `LoginController` 的 HREF_MAP 中新增 `/bus/toStocktakeManager` -> `/business/stocktake` 映射，并确保数据库 `sys_permission` 表中有对应的盘点菜单记录。如果盘点菜单在数据库中不存在，则盘点功能默认对所有登录用户可见（降级处理）。

**库存预警区域**（标题"库存预警" + 查看更多）：
- 最多显示 5 条低库存商品
- 每条显示商品名 + 当前库存数（红色）
- 点击跳转商品详情

### 3.5 权限映射机制

后端 `/login/currentUser` 返回的 `menus` 树中，每个节点有 `href` 字段（已被 HREF_MAP 转换为前端路由）。工作台宫格配置中每项绑定 `menuHref`，渲染时递归匹配。

```
用户 menus 树：
├── 业务管理 (href: null, 有 children)
│   ├── 客户管理 (href: /query/customer)
│   ├── 供应商管理 (href: /query/provider)
│   ├── 商品管理 (href: /business/goods)
│   ├── 进货入库 (href: /business/inport)
│   ├── 销售出库 (href: /business/sales)
│   └── ...
└── 系统管理 (href: null, 有 children)
    └── ...
```

过滤逻辑（伪代码）：
```
function isMenuAllowed(menuHref, userMenus):
    for each menu in userMenus:
        if menu.href == menuHref: return true
        if menu.children and isMenuAllowed(menuHref, menu.children): return true
    return false

visibleGrids = WORKBENCH_GRID.filter(item =>
    isSuperAdmin() or isMenuAllowed(item.menuHref, userMenus)
)
```

**降级策略**：如果后端返回的 menus 为空或某个功能在 HREF_MAP 中无对应条目（如盘点），则该功能默认对所有登录用户可见。这确保即使权限配置不完整，移动端功能不会被意外隐藏。

### 3.6 Auth Store 改造

扩展 `stores/auth.uts`：

```typescript
interface MenuItem {
  id: number
  parentId: number
  title: string
  icon: string
  href: string
  spread: boolean
  children?: MenuItem[]
}

// 新增状态
let menus: MenuItem[] = []
let permissions: string[] = []

// 新增方法
function setMenus(data: MenuItem[]): void
function setPermissions(data: string[]): void
function getMenus(): MenuItem[]
function getPermissions(): string[]
function isSuperAdmin(): boolean  // permissions 包含 "*:*"
function hasMenu(href: string): boolean  // 递归检查 menus 树
function hasPermission(code: string): boolean  // 检查 permissions 列表
```

`fetchCurrentUser()` 改造：解析 API 返回的 `menus` 和 `permissions`，调用 `setMenus()` 和 `setPermissions()` 存储。同时持久化到 localStorage 以便恢复。

### 3.7 公告模块

**新增 API**（`api/notice.uts`）：
- `loadAllNotice(params)` — 分页公告列表
- `loadNoticeById(id)` — 公告详情

**新增页面**：
- `pages/notice/list.uvue` — 公告列表，展示标题+发布时间，支持下拉刷新
- `pages/notice/detail.uvue` — 公告详情，展示标题+正文+时间

**工作台集成**：导航栏右侧铃铛图标，点击跳转公告列表页。

### 3.8 操作日志页面

`pages/profile/operation-log.uvue`：
- 调用 `loginfo/loadAll` API
- 列表展示操作类型、操作人、操作时间
- 支持分页加载

### 3.9 后端改动（最小化）

1. **公告接口已就绪**：`NoticeController` 已有 `loadAllNotice`（列表）和 `loadNoticeById`（详情）接口，无需新增。
2. **HREF_MAP 新增盘点映射**：在 `LoginController.java` 的 HREF_MAP 中新增 `/bus/toStocktakeManager` -> `/business/stocktake`。
3. **数据库菜单记录**：确认 `sys_permission` 表中是否有盘点菜单记录（type=menu, href=/bus/toStocktakeManager）。如果没有，需要通过菜单管理页面新增。
4. **无需新增路由映射**：客户/供应商的 HREF_MAP 已存在（`/business/customer`、`/business/provider`），移动端权限匹配时使用这些值即可。

## 4. 实施步骤

1. **改造 auth store** — 扩展 menus/permissions 存储和方法
2. **新增公告 API** — 创建 `api/notice.uts`
3. **创建工作台页面** — `pages/workbench/index.uvue`
4. **创建公告页面** — `pages/notice/list.uvue` + `pages/notice/detail.uvue`
5. **创建操作日志页面** — `pages/profile/operation-log.uvue`
6. **更新 pages.json** — 路由注册 + tab bar 配置
7. **改造我的页面** — 新增操作日志入口
8. **清理旧页面** — 删除废弃的导航页面和相关代码
9. **适配 HREF_MAP** — 确保后端菜单 href 与移动端路由匹配
10. **测试验证** — 超管/普通用户权限过滤、公告功能、操作日志
