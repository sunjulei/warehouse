# 前端全面重新设计方案

**日期**: 2026-06-06
**版本**: 1.0
**范围**: 仓库管理系统全部 34 个页面 + 全局样式 + 组件库

---

## 1. 设计目标

重新设计仓库管理系统前端，打造**差异化风格**——摆脱常见的后台管理系统模板感，建立独特、有辨识度的视觉身份。

**核心原则**:
- 清新极简，不追求炫技，追求**精准的克制**
- 每个像素都有存在的理由
- 信息密度与留白的平衡——仓库系统需要高效，但不应该是丑的

---

## 2. 设计方向

### 2.1 美学基调

**Clean Minimal（清新极简）**

- 浅色暖白底色，不是纯白，是带微暖调的白（#fafaf9）
- 大量留白，让内容呼吸
- 视觉层级靠字号、字重、间距和微妙的色彩区分，不靠装饰
- 参考对象：Linear、Notion、Raycast

### 2.2 主色调

**默认：玫瑰红 Rose #e11d48**

用于：按钮、链接、数据高亮、进度条、指示条、图表主色

**4 种可切换主题色**（用户可在设置中切换）：

| 主题色 | 色值 | 语义 |
|--------|------|------|
| 靛蓝 Indigo | #4f46e5 | 专业、信任 |
| 翡翠绿 Emerald | #059669 | 增长、运转 |
| 玫瑰红 Rose | #e11d48 | 活力、醒目（默认） |
| 藏青 Navy | #1e3a5f | 沉稳、商务 |

### 2.3 完整色板

**Light Mode**:
```scss
// 背景
--bg-primary: #fafaf9;      // 页面底色（暖白）
--bg-secondary: #ffffff;     // 卡片背景
--bg-tertiary: #f5f5f3;     // 斑马纹行、代码块

// 文字
--text-primary: #1c1917;     // 主文字（深褐黑）
--text-secondary: #78716c;   // 次要文字
--text-tertiary: #a8a29e;    // 占位符、辅助

// 边框
--border-primary: #e8e5e0;   // 主边框
--border-secondary: #f0eeea; // 细分割线

// 主题色（通过 CSS 变量切换）
--accent: #e11d48;           // 当前主题色
--accent-light: #fef2f4;     // 主题色浅底（hover、选中态）
--accent-dark: #be123c;      // 主题色深色（hover 按钮）
```

**Dark Mode**:
```scss
--bg-primary: #1c1917;
--bg-secondary: #292524;
--bg-tertiary: #3a3633;
--text-primary: #fafaf9;
--text-secondary: #a8a29e;
--text-tertiary: #78716c;
--border-primary: #3a3633;
--border-secondary: #292524;
--accent: #f43f5e;           // 暗色模式下主题色略亮
--accent-light: rgba(244,63,94,0.12);
```

### 2.4 字体

```scss
--font-sans: 'Plus Jakarta Sans', 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', system-ui, sans-serif;
--font-mono: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
```

字号系统：
- 页面标题：20px / 600
- 区域标题：15px / 600
- 正文：13px / 400
- 表格：12px / 400
- 辅助文字：11px / 400
- 标签：10px / 500 / uppercase / letter-spacing: 1px

### 2.5 间距系统

```scss
--space-1: 4px;
--space-2: 8px;
--space-3: 12px;
--space-4: 16px;
--space-5: 20px;
--space-6: 24px;
--space-8: 32px;
--space-10: 40px;
--space-12: 48px;
```

### 2.6 圆角

```scss
--radius-sm: 6px;    // 按钮、标签
--radius-md: 8px;    // 输入框、小卡片
--radius-lg: 10px;   // 卡片、表格
--radius-xl: 12px;   // 弹窗、大卡片
--radius-full: 9999px; // 药丸形状
```

### 2.7 阴影

```scss
--shadow-sm: 0 1px 2px rgba(0,0,0,0.03);
--shadow-md: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.03);
--shadow-lg: 0 2px 8px rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.04);
--shadow-xl: 0 4px 16px rgba(0,0,0,0.06), 0 12px 40px rgba(0,0,0,0.06);
```

---

## 3. 布局系统

### 3.1 整体布局

保持经典的侧边栏 + 顶栏 + 内容区布局，但做以下优化：

- **侧边栏**：浅色（白色），宽度 220px，折叠后 64px
- **顶栏**：白色，高度 52px，底部细边框
- **内容区**：暖白底 #fafaf9，内边距 24px
- **标签栏**：保留，但样式更轻——浅灰底 + 细边框，当前标签用主题色文字

### 3.2 侧边栏

- 白色背景，右侧 1px 细边框
- Logo 区域：图标 + "仓储管理" 文字，高度 52px
- 菜单项：高度 36px，圆角 6px，左侧 3px 指示条标识当前项
- 当前项：左侧主题色指示条 + 浅主题色背景
- Hover：浅灰背景 #f5f5f3
- 折叠态：仅显示图标，tooltip 显示菜单名

### 3.3 顶栏

- 白色背景，底部 1px 边框
- 左侧：折叠按钮 + 面包屑
- 右侧：主题色切换器 + 布局切换 + 时钟 + 用户头像下拉

---

## 4. 组件规范

### 4.1 按钮

**主要按钮（Primary）**:
- 背景：主题色，文字白色
- Hover：主题色深色
- 圆角：6px，高度：32px，内边距：0 16px
- 图标 + 文字组合时，间距 6px

**次要按钮（Secondary）**:
- 背景：白色，边框 #e8e5e0，文字 #1c1917
- Hover：背景 #f5f5f3

**文字按钮（Text）**:
- 无背景无边框，文字主题色
- Hover：背景主题色浅底

**危险按钮（Danger）**:
- 背景 #dc2626，文字白色
- 用于删除操作

### 4.2 输入框

- 高度 32px，圆角 6px
- 边框 #e8e5e0，Focus 时边框主题色 + 微弱主题色阴影
- 占位符文字 #a8a29e
- 背景白色

### 4.3 数据表格

- 斑马纹：奇数行白色，偶数行 #fafaf8
- 表头：背景 #fafaf8，文字 #999，字号 11px，大写，字重 500
- 行高：44px
- 边框：行间 1px #f5f4f2
- Hover 行：背景 #fafaf9（微弱变化）
- 选中行：背景主题色浅底
- 操作列：文字按钮风格，主题色

### 4.4 弹窗/对话框

- 圆角 12px
- 阴影：shadow-xl
- 标题：15px / 600
- 内边距：24px
- 底部按钮区：顶部 1px 边框，右对齐
- 遮罩：rgba(0,0,0,0.3)

### 4.5 卡片

- 背景白色
- 圆角 10px
- 阴影：shadow-md
- 内边距：16px 或 20px
- 统计卡片：标题 10px 灰色 + 数值 24px 粗体 + 变化指标

### 4.6 标签/徽章

- 圆角 full（药丸）
- 背景：主题色浅底，文字：主题色
- 成功：#dcfce7 背景 + #166534 文字
- 警告：#fef3c7 背景 + #92400e 文字
- 危险：#fef2f4 背景 + #9f1239 文字

### 4.7 分页

- 圆角 6px
- 当前页：主题色背景，白色文字
- 其他页：白色背景，边框
- 简洁风格，显示：总条数 + 页码 + 每页条数选择

### 4.8 搜索表单

- 一行排列，输入框 + 按钮
- 左侧 3px 主题色指示条（已有样式，保留）
- 背景：白色卡片，阴影 sm
- 搜索按钮：主要按钮风格
- 重置按钮：文字按钮风格

---

## 5. 页面设计规范

### 5.1 登录页面

- 暖白背景 #fafaf9，居中卡片
- 卡片：白色，圆角 16px，阴影 lg
- 左侧：系统名称 + 简短描述 + 装饰图形
- 右侧：登录表单
- 输入框：大号（高度 40px）
- 登录按钮：全宽主要按钮
- 验证码：图片 + 刷新按钮

### 5.2 仪表盘

**布局**：数据优先风格

1. **问候区**：「早上好，管理员 👋」+ 日期 + 通知数量徽章
2. **统计卡片**：4 列网格
   - 商品总数（主题色数字）
   - 库存预警（红色数字 + 需处理提示）
   - 今日入库（数字 + 趋势箭头）
   - 今日销售（数字 + 趋势箭头）
3. **最近操作列表**：白色卡片，每行：状态点 + 描述 + 时间

### 5.3 CRUD 表格页面（通用模式）

适用于：用户管理、商品管理、客户管理、供应商管理、入库管理、销售管理等 12+ 个页面

**布局**：
1. **页面标题区**：标题（20px/600）+ 副标题描述（13px/400 灰色）
2. **搜索表单**：白色卡片，行内表单
3. **工具栏**：新增按钮（主要）+ 批量删除（危险，需选中才可用）
4. **数据表格**：斑马纹，分页
5. **操作列**：编辑（文字按钮）| 删除（文字按钮红色）

**新增/编辑弹窗**：
- 表单字段按逻辑分组
- 必填项标红星
- 底部：取消（次要按钮）+ 确定（主要按钮）

### 5.4 树形管理页面

适用于：部门管理、菜单管理、权限管理、商品分类

**布局**：
- 左侧：树形面板（白色卡片，240px 宽）
- 右侧：对应的数据表格或表单
- 树节点选中：主题色高亮

### 5.5 图表分析页面

适用于：报表管理、入库分析、商品分析、利润分析

**布局**：
1. **时间选择器**：今日/昨日/本周/本月/本季/本年/自定义（radio 按钮组）
2. **图表区域**：白色卡片，ECharts 图表
   - 图表主色：当前主题色
   - 渐变填充：主题色到透明
   - 网格线：#f0eeea
   - 坐标轴文字：#999
3. **数据表格**：下方显示明细数据

### 5.6 特殊功能页面

**零售页面（retail）**：
- 左侧：商品选择区（搜索 + 商品列表）
- 右侧：购物车（已选商品列表 + 数量修改 + 总价）
- 底部：结算按钮

**会员管理（member）**：
- 会员列表 + 充值/消费弹窗
- 会员等级规则配置（独立 tab）

**库存盘点（stocktake）**：
- 盘点单列表 + 创建盘点
- 盘点详情：系统数量 vs 实际数量对比表格

**提成管理（commission）**：
- 提成规则配置
- 提成记录表格 + 状态流转（待确认 → 已确认 → 已发放）

---

## 6. 主题色切换功能

### 6.1 实现方式

使用 CSS 自定义属性（CSS Variables）+ `data-theme-color` 属性：

```html
<html data-theme-color="rose">
```

所有主题色通过变量引用：
```scss
[data-theme-color="indigo"] { --accent: #4f46e5; --accent-light: #eef2ff; --accent-dark: #4338ca; }
[data-theme-color="emerald"] { --accent: #059669; --accent-light: #ecfdf5; --accent-dark: #047857; }
[data-theme-color="rose"] { --accent: #e11d48; --accent-light: #fef2f4; --accent-dark: #be123c; }
[data-theme-color="navy"] { --accent: #1e3a5f; --accent-light: #eff6ff; --accent-dark: #1e3a5f; }
```

### 6.2 UI 入口

在顶栏右侧增加主题色切换器：
- 4 个小圆点，点击切换
- 当前选中项有白色内圈标识
- 选择保存到 localStorage

### 6.3 覆盖范围

所有使用主题色的地方自动跟随：
- 按钮背景
- 链接文字
- 选中态指示条
- 图表主色
- 标签/徽章
- 分页当前页
- 进度条
- ECharts 渐变色

---

## 7. 深色模式

### 7.1 实现方式

保持现有的 `data-theme="dark"` 属性机制，更新所有色值。

### 7.2 设计原则

- 不是简单的颜色反转，而是独立调优的暗色色板
- 背景用深褐黑（#1c1917），不是纯黑
- 文字用暖白（#fafaf9），不是纯白
- 主题色在暗色模式下略亮，保证对比度
- 阴影在暗色模式下减弱，用边框代替

---

## 8. 动效规范

### 8.1 页面切换

- 路由切换：fadeIn + 微弱上移（200ms ease）
- 保持现有的 page-fade 过渡

### 8.2 弹窗

- 打开：scale 从 0.95 到 1 + fadeIn（200ms）
- 关闭：fadeOut（150ms）

### 8.3 表格行

- Hover：背景色渐变（150ms）
- 选中：背景色渐变（200ms）

### 8.4 按钮

- Hover：背景色渐变（150ms）
- 点击：scale 0.98（100ms）

### 8.5 列表加载

- 新增项：fadeInUp + 微弱上移（200ms），交错延迟 50ms

---

## 9. Element Plus 覆盖策略

全面覆盖 Element Plus 组件默认样式，使其融入我们的设计系统：

- **全局**：字体、圆角、颜色基础变量
- **el-button**：主要/次要/文字/危险按钮样式
- **el-input/el-select**：高度、边框、focus 态
- **el-table**：斑马纹、表头、行高、hover
- **el-dialog**：圆角、阴影、标题样式
- **el-form**：标签对齐、必填标记
- **el-pagination**：圆角、当前页样式
- **el-menu**：侧边栏菜单样式
- **el-tag**：药丸形状、颜色
- **el-message/el-notification**：圆角、图标
- **el-card**：阴影、圆角
- **el-breadcrumb**：分隔符、字号
- **el-dropdown**：圆角、阴影
- **el-tabs**：标签栏样式
- **el-tree**：选中态、连接线
- **el-upload**：上传区域样式
- **el-date-picker**：面板样式

---

## 10. 文件变更清单

### 新增文件
- `src/styles/themes.scss` — 4 种主题色变量定义
- `src/styles/dark-mode.scss` — 深色模式变量（从 variables.scss 分离）

### 修改文件
- `src/styles/variables.scss` — 更新色板、间距、圆角、阴影
- `src/styles/element-overrides.scss` — 全面更新 Element Plus 覆盖
- `src/styles/animations.scss` — 精简动效，保留核心
- `src/styles/index.scss` — 更新工具类
- `src/layout/SidebarLayout.vue` — 浅色侧边栏
- `src/layout/TopMenuLayout.vue` — 匹配新风格
- `src/layout/components/Sidebar.vue` — 浅色侧边栏样式
- `src/layout/components/Header.vue` — 新顶栏样式 + 主题色切换器
- `src/layout/components/TabsBar.vue` — 新标签栏样式
- `src/stores/theme.ts` — 增加 themeColor 状态
- `src/views/login/index.vue` — 新登录页设计
- `src/views/dashboard/index.vue` — 数据优先仪表盘
- `src/components/CrudTable.vue` — 斑马纹表格
- `src/components/CrudDialog.vue` — 新弹窗样式
- `src/components/SearchForm.vue` — 新搜索表单样式
- `src/components/TreePanel.vue` — 新树面板样式
- 所有 34 个 views/*.vue 页面 — 适配新设计系统

---

## 11. 不变的部分

- API 层（api/*.ts）— 不变
- 路由配置（router/index.ts）— 不变
- TypeScript 类型定义（types/*.ts）— 不变
- 工具函数（utils/*.ts）— 不变
- 权限逻辑（composables/usePermission.ts）— 不变
- 数据流和组件接口 — 不变
- 后端 API — 不变

---

## 12. 成功标准

1. 所有 34 个页面视觉风格统一，符合设计规范
2. 4 种主题色可无缝切换，无遗漏
3. 深色模式完整可用
4. 不破坏任何现有功能
5. 视觉上与常见的 Element Plus 后台模板有明显差异
6. 页面加载性能不退化
