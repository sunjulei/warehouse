# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Warehouse Management System (仓库管理系统) — frontend-backend separated architecture:
- **Backend**: Spring Boot 3.5.5 + Java 21 + MyBatis-Plus + SA-Token + MySQL
- **Frontend**: Vue 3 + TypeScript + Element Plus + Vite (in `warehouse-frontend/`)

## Build & Run

```bash
# Backend
mvn clean package                    # Build JAR
mvn spring-boot:run                  # Start on port 8888
mysql -u root -p < warehouse.sql     # Init database

# Frontend
cd warehouse-frontend
npm install                          # Install dependencies
npm run dev                          # Dev server on port 5173 (proxies /api/* to :8888)
npm run build                        # Production build → dist/
```

## Architecture

### Backend (`src/main/java/com/sunlee/`)

Two-module layered MVC:
- **`sys/`** — System: users, roles, permissions, departments, menus, notices, login logs, cache
- **`bus/`** — Business: customers, providers, goods, inport, outport, sales, salesback

Each module: `Controller → Service → ServiceImpl → Mapper → MyBatis XML`

**Standard response DTOs** (`sys/common/`):
- `DataGridView` — table data: `{code: 0, msg: "", count: N, data: [...]}`
- `ResultObj` — operation result: `{code: 200|-1, msg: "..."}`

### Frontend (`warehouse-frontend/src/`)

```
api/          — One file per backend controller (login.ts, user.ts, goods.ts, etc.)
types/        — TypeScript interfaces matching Java entities
stores/       — Pinia stores (auth.ts for user/menus/permissions)
router/       — Vue Router with static routes, auth guard
layout/       — Sidebar + Header + main content area
components/   — CrudTable, SearchForm, CrudDialog, TreePanel, ImageUpload
views/        — Page components (login, dashboard, system/*, business/*)
composables/  — usePermission() for button-level access control
utils/        — Axios instance (request.ts) with interceptors
```

**Auth flow**: SA-Token based (cookie). Login → `StpUtil.login()` + session stores user. `/api/login/currentUser` returns user + menus + permissions. Frontend stores in Pinia auth store.

## Key Patterns

**AOP Caching**: `CacheAspect` / `BusinessCacheAspect` intercept service calls, store in `CachePool.CACHE_CONTAINER` HashMap.

**SA-Token Security**: `SaTokenConfigure.java` configures interceptor chain. Unauthenticated API requests return JSON 401. `CorsConfig` allows Vue dev server origin.

**File Uploads**: `POST /file/uploadFile` saves with `_temp` suffix. Entity save renames to permanent. Images served via `GET /file/showImageByPath?path=`.

**Frontend API proxy**: Vite dev server proxies `/api/*` → `localhost:8888/*` (strips `/api` prefix).

## Database

MySQL `warehouse` schema, 15 tables. Prefixes: `bus_*` (business), `sys_*` (system). Schema + seed data in `warehouse.sql`. Connection pool: Alibaba Druid.

## Code Conventions

- Java entities: Lombok (`@Data`, `@TableName`, `@TableField`)
- MyBatis-Plus `BaseMapper` with XML in `src/main/resources/mapper/{bus,sys}/`
- `@MapperScan(basePackages = {"com.sunlee.*.mapper"})` on main class
- Constants: `sys/common/Constast.java`
- Swagger UI: `/swagger-ui/index.html`
- Frontend components use `<script setup lang="ts">` pattern
- API modules: one function per backend endpoint, typed with `DataGridView`/`ResultObj`

## Key Files

| File | Purpose |
|---|---|
| `WarehouseApplication.java` | Entry point, `@MapperScan` |
| `application.yml` | Server port 8888, datasource, SA-Token, Thymeleaf disabled |
| `SaTokenConfigure.java` | SA-Token interceptor chain |
| `SaSessionAdapter.java` | Adapts SA-Token session to HttpSession interface |
| `CorsConfig.java` | CORS for Vue dev server |
| `LoginController.java` | Login, captcha, currentUser, logout endpoints |
| `DataGridView.java` / `ResultObj.java` | Standard response DTOs |
| `warehouse-frontend/vite.config.ts` | Vite proxy config |
| `warehouse-frontend/src/utils/request.ts` | Axios instance with interceptors |
| `warehouse-frontend/src/stores/auth.ts` | Auth state (user, menus, permissions) |
| `warehouse-frontend/src/router/index.ts` | Routes + auth guard |
