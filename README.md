# 仓库管理系统

前后端分离的仓库管理系统，适用于小型仓库的进销存管理。

## 项目简介

---

本系统是一款面向小型仓库的进销存管理系统，帮助仓库人员高效完成采购入库、销售出库、退货及库存查询等核心业务。首页数据看板实时展示商品总数、库存预警、今日进销概况等关键指标，快速掌握运营状态。

系统支持客户、供应商、商品分类及档案管理，完整覆盖进货、进货退货、销售、销售退货全流程。内置进销金额、商品及利润三大分析报表，支持按今天、本周、本月、本季度、本年或自定义时间范围进行多维度查询与图表展示。同时具备完善的权限管理功能，支持用户、角色、部门、菜单灵活配置。采用主流技术架构开发，具备良好的可维护性与可扩展性。

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Spring Boot 3.5.5 + Java 21 + MyBatis-Plus + SA-Token + MySQL |
| 前端 | Vue 3 + TypeScript + Element Plus + Vite + ECharts |

## 功能模块

### 系统管理
- 用户管理、角色管理、部门管理
- 菜单管理、权限管理（RBAC）
- 公告管理、登录日志、缓存管理
- 个人信息、修改密码

### 业务管理
- **基础数据**：客户管理、供应商管理、商品分类、商品管理
- **进货管理**：商品入库，记录进货价格、数量、供应商
- **退货管理**：进货退货
- **销售管理**：商品出库，记录销售价格、数量、客户
- **销售退货**：销售退货

### 报表统计
- **进销金额分析**：按时间段统计进货/销售金额趋势（柱状图）
- **进销商品分析**：按时间段统计商品明细（柱状图 + 表格，支持分页）
- **利润分析**：按时间段对比销售额、进货成本，计算毛利润和毛利率（柱状图 + 折线图）
- 所有报表支持：今天、昨天、本周、本月、本季、本年、自定义日期范围

### 首页看板
- 商品总数、库存预警数、今日进货、今日销售
- 最近操作日志
- 库存预警商品列表

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.6+
- MySQL 5.7+
- Node.js 16+

### 数据库初始化
```bash
mysql -u root -p
CREATE DATABASE warehouse DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
EXIT;
mysql -u root -p warehouse < warehouse.sql
```

### 后端启动
```bash
mvn clean package
mvn spring-boot:run
# 或
java -jar target/warehouse-0.0.1-SNAPSHOT.jar
```
后端运行在 `http://localhost:8888`

### 前端启动
```bash
cd warehouse-frontend
npm install
npm run dev
```
前端运行在 `http://localhost:5173`，自动代理 `/api/*` 到后端。

### 生产构建
```bash
cd warehouse-frontend
npm run build
# 产物在 warehouse-frontend/dist/
```

## 默认账号

| 用户名 | 密码 | 角色 |
|---|---|---|
| 超级管理员 | 123456 | 超级管理员 |

## 项目结构

```
warehouse-master/
├── src/main/java/com/sunlee/
│   ├── WarehouseApplication.java          # 启动类
│   ├── bus/                               # 业务模块
│   │   ├── controller/                    # 控制层
│   │   ├── entity/                        # 实体类
│   │   ├── mapper/                        # MyBatis Mapper
│   │   ├── service/                       # 业务层
│   │   ├── vo/                            # 视图对象
│   │   └── cache/                         # 业务缓存切面
│   └── sys/                               # 系统模块
│       ├── controller/                    # 控制层
│       ├── entity/                        # 实体类
│       ├── mapper/                        # MyBatis Mapper
│       ├── service/                       # 业务层
│       ├── vo/                            # 视图对象
│       ├── common/                        # 公共类（DataGridView, ResultObj 等）
│       ├── config/                        # 配置（SA-Token, Springdoc, MyBatis-Plus）
│       └── cache/                         # 缓存切面
├── src/main/resources/
│   ├── application.yml                    # 应用配置
│   └── mapper/{bus,sys}/                  # MyBatis XML
├── warehouse-frontend/
│   ├── src/
│   │   ├── api/                           # 接口请求
│   │   ├── components/                    # 公共组件
│   │   ├── composables/                   # 组合式函数
│   │   ├── layout/                        # 布局组件
│   │   ├── router/                        # 路由配置
│   │   ├── stores/                        # Pinia 状态管理
│   │   ├── types/                         # TypeScript 类型
│   │   ├── utils/                         # 工具函数
│   │   └── views/                         # 页面组件
│   ├── vite.config.ts                     # Vite 配置
│   └── package.json
├── warehouse.sql                          # 数据库脚本
├── pom.xml                                # Maven 配置
└── README.md
```

## 接口说明

所有接口以 `/api` 为前缀（前端 Vite 代理会去掉 `/api`）。

| 模块 | 前缀 | 说明 |
|---|---|---|
| 认证 | `/login` | 登录、登出、当前用户 |
| 系统 | `/user`, `/role`, `/dept`, `/menu`, `/permission`, `/notice` | 系统管理 CRUD |
| 业务 | `/customer`, `/provider`, `/category`, `/goods` | 基础数据 CRUD |
| 业务 | `/inport`, `/outport`, `/sales`, `/salesback` | 进销存操作 |
| 报表 | `/report` | 数据分析接口 |
| 文件 | `/file` | 文件上传/预览 |

## 数据库表

| 表名 | 说明 |
|---|---|
| `sys_user` | 用户表 |
| `sys_role` | 角色表 |
| `sys_dept` | 部门表 |
| `sys_menu` | 菜单表 |
| `sys_permission` | 权限表（菜单 + 按钮权限） |
| `sys_role_permission` | 角色-权限关联 |
| `sys_user_role` | 用户-角色关联 |
| `sys_notice` | 公告表 |
| `sys_loginfo` | 登录日志 |
| `bus_goods` | 商品表 |
| `bus_category` | 商品分类表 |
| `bus_customer` | 客户表 |
| `bus_provider` | 供应商表 |
| `bus_inport` | 进货记录表 |
| `bus_outport` | 退货记录表 |
| `bus_sales` | 销售记录表 |
| `bus_salesback` | 销售退货表 |
| `bus_operation_log` | 操作日志表 |

## 许可证

MIT License
