# uniappx Bug 修复 + 试点重构设计文档

## 概述

修复 uniappx 项目中 7 个已确认的 Bug，并将 2 个高频页面（user.uvue、goods.uvue）重构为使用通用组件。

## 背景

uniappx 从 warehouse-front 迁移后存在三类问题：
- 页面布局错乱（滚动容器缺失/不一致、grid 宽度问题）
- 接口调用失败（API 签名错误、大量数据客户端过滤、无错误处理）
- 操作体验不合理（组件未复用导致代码冗余）

## 阶段一：Bug 修复（7 项）

### 1. sales.uvue 无法滚动

**文件**: `pages/business/sales.uvue`

**问题**: 页面没有 scroll-view 包裹，包含模式切换、商品列表、购物车等内容，在小屏幕设备上内容溢出无法操作。

**修复**: 添加 APP 条件编译的 scroll-view 包裹，与其他页面保持一致：
```html
<!-- #ifdef APP -->
<scroll-view style="flex:1">
<!-- #endif -->
  <view class="container">
    <!-- 现有内容 -->
  </view>
<!-- #ifdef APP -->
</scroll-view>
<!-- #endif -->
```

### 2. scroll-view 包裹方式不一致

**文件**: 6+ 个页面

**问题**: 部分页面用 APP 条件编译包裹，部分页面无条件包裹，部分页面完全不包裹。

**修复**: 统一所有页面使用 APP 条件编译模式。需要检查并修复以下页面：
- `business/sales.uvue` — 添加条件编译包裹
- `business/goods.uvue` — 改为条件编译包裹（当前无条件）
- `business/goods-detail.uvue` — 改为条件编译包裹（当前无条件）
- `profile/index.uvue` — 改为条件编译包裹（当前无条件）

### 3. workbench grid-item 宽度问题

**文件**: `pages/workbench/index.uvue`

**问题**: `width: 30%` + `gap: 12rpx` 导致 3 列布局右侧有明显空白，4 个 item 时第 4 个孤立换行。

**修复**: 改用 `flex: 1` + `max-width` 约束：
```css
.grid-item {
  flex: 1;
  max-width: 32%;
  min-width: 28%;
}
```

### 4. goods-card 间距不均

**文件**: `pages/business/inport-pos.uvue`, `pages/business/retail.uvue`

**问题**: `.goods-card` 有 `margin-bottom: 8rpx`，与父容器 `.goods-grid` 的 `gap: 16rpx` 叠加，导致行间距不一致。

**修复**: 移除 `.goods-card` 的 `margin-bottom`，仅依赖父容器的 `gap` 控制间距。

### 5. report.uvue API 签名错误

**文件**: `pages/business/report.uvue`

**问题**: `getApiFunc` 返回的函数签名为 `(type, startDate, endDate)`，但调用时传入 `tab.api` 作为第一个参数，导致 `apiName` 被冗余传递。

**修复**: 修正 `getApiFunc` 返回类型，调用时不传 `tab.api`：
```typescript
// 修正前
apiFunc(tab.api, startDate.value, endDate.value)
// 修正后
apiFunc(startDate.value, endDate.value)
```

同时修正 `getApiFunc` 的返回类型签名。

### 6. inport-pos/retail 加载 1000 条客户端过滤

**文件**: `pages/business/inport-pos.uvue`, `pages/business/retail.uvue`

**问题**: 加载最多 1000 条商品数据，然后在客户端按供应商 ID 过滤。效率低，数据量大时卡顿。

**修复**: 改为服务端分页 + 关键词搜索。加载商品时不预设 limit=1000，而是使用默认分页（limit=20），通过搜索关键词筛选商品。移除客户端过滤逻辑。

### 7. goods-detail 无错误处理

**文件**: `pages/business/goods-detail.uvue`

**问题**: 商品 ID 无效或 API 返回空数据时，页面显示默认空值（"未知"/"0.00"），无任何错误提示。

**修复**: 添加加载状态和错误处理：
- 加载中显示 loading 状态
- 加载失败显示错误提示 + 返回按钮
- 数据为空时显示"商品不存在"提示

## 阶段二：试点重构（2 个页面）

> **注意**: customer-manage.uvue 和 provider-manage.uvue 已经在使用 CrudList + CrudForm 组件（各约 85 行），无需重构。

### 1. user.uvue 重构（1047 行 → 预计 ~300 行）

**当前状态**: 手写搜索框、手写列表卡片、手写分页、手写表单弹窗、手写删除确认。代码 1047 行。

**重构方案**:
- **列表**: 使用 CrudList 组件，columns 定义展示字段（name, loginname, deptname, sex, available）
- **搜索**: 使用 CrudList 内置搜索，searchField="name"
- **新增/编辑表单**: 使用 CrudForm 组件，fields 定义表单字段
- **删除**: 使用 CrudList 内置删除（deleteApi）
- **保留自定义功能**:
  - 重置密码：在 CrudList 的 `#item` slot 中添加按钮
  - 角色分配：保留独立的角色分配弹窗（不纳入 CrudForm）
  - 部门选择：使用 CrudSelect 组件替代手写 picker

**CrudList 配置**:
```typescript
const columns = [
  { key: 'name', title: '用户名' },
  { key: 'loginname', title: '登录名' },
  { key: 'deptname', title: '部门' },
  { key: 'sex', title: '性别', formatter: (v) => v == 1 ? '男' : '女' },
  { key: 'available', title: '状态', formatter: (v) => v == 0 ? '启用' : '禁用' }
]
```

**CrudForm fields**:
```typescript
const fields = [
  { key: 'name', label: '用户名', type: 'text', required: true },
  { key: 'loginname', label: '登录名', type: 'text', required: true },
  { key: 'pwd', label: '密码', type: 'text', required: false, placeholder: '新增时默认123456' },
  { key: 'sex', label: '性别', type: 'select', options: [{label:'男',value:1},{label:'女',value:2}] },
  { key: 'deptid', label: '部门', type: 'select', options: deptOptions },
  { key: 'address', label: '地址', type: 'text' },
  { key: 'remark', label: '备注', type: 'textarea' },
  { key: 'available', label: '启用', type: 'switch' }
]
```

**保留的自定义弹窗**:
- 角色分配弹窗（checkbox 列表 + 保存）
- 重置密码确认（uni.showModal + API 调用）

### 2. goods.uvue 重构（155 行 → 预计 ~60 行）

**当前状态**: 使用 SearchBar + GoodsCard + EmptyState 组件，手动管理分页和数据加载。

**重构方案**:
- **列表 + 搜索**: 使用 CrudList 组件（只读模式：showAdd=false, deletable=false, searchable=true）
- **搜索字段**: searchField="goodsname"，searchPlaceholder="搜索商品名称"
- **item 渲染**: 使用 CrudList 的 `#item` slot 复用 GoodsCard 组件
- **点击行为**: CrudList 的 `@edit` 事件触发页面跳转到 goods-detail
- **移除**: SearchBar、EmptyState 组件引用（CrudList 内置空状态）

**CrudList 配置**:
```typescript
// 只读列表，无新增/删除，内置搜索
<CrudList
  ref="listRef"
  :loadData="loadAllGoods"
  :columns="[]"
  :searchable="true"
  searchField="goodsname"
  searchPlaceholder="搜索商品名称"
  :showAdd="false"
  :deletable="false"
  @edit="onItemClick"
>
  <template #item="{ item }">
    <GoodsCard :item="mapGoodsItem(item)" />
  </template>
</CrudList>
```

## 不在范围内

- 默认密码硬编码 — 用户明确不需要修复
- 验证码安全性 — 已在之前修复（captchaCode 直传方案）
- 其他页面的组件重构 — 等试点验证后再推广
- CrudSelect 组件集成到 user.uvue 的部门选择 — 可在后续迭代中进行

## 验证标准

- [ ] sales.uvue 在 APP 端可以正常滚动
- [ ] 所有页面的 scroll-view 包裹方式一致
- [ ] workbench 网格布局无明显空白
- [ ] inport-pos/retail 的商品卡片行间距一致
- [ ] report 页面各 tab 的报表数据正常加载
- [ ] inport-pos/retail 搜索商品时使用服务端分页
- [ ] goods-detail 加载失败时有错误提示
- [ ] user.uvue 使用 CrudList + CrudForm 后功能正常
- [ ] user.uvue 的角色分配和重置密码功能正常
- [ ] goods.uvue 使用 CrudList 后列表展示、搜索和跳转正常
