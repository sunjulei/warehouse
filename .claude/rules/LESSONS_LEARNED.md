# 经验教训（运行验证后记录）

## 2026-07-23 第二/三批修复（账实错误 + 并发一致性）

### 退货流程统一（行为变更）
- **两套退货流程已统一为新流程**：退货唯一入口是订单页的 `/sales|inport|retail/returnSingleGoods` 和 `returnOrder`，流水写 `bus_*_log`。老的 `addSalesback/cancelSalesback/addOutport/cancelOutport/addRetailback/cancelRetailback` 六个写接口已删除（404），`loadAllSalesback/loadAllOutport/loadAllRetailback` 保留为只读历史查询。前端老页面（business/sales、salesback、retailback、inport 四个目录）及对应 api 文件已删除。
- 菜单迁移：`mysql/migration-20260723.sql`（隐藏菜单135"零售退货"，补 166/167 零售订单/零售退回记录菜单并授权角色1/13）。**需手动在远程库执行**（共享库写操作需用户批准）。warehouse.sql 种子本来就正确（无135、有166/167），是远程库漂移。
- bus_salesback/bus_outport/bus_retailback 的 isdelete 已补 `@TableLogic`（实体里 import 了注解但一直没加在字段上）；自定义 XML 不受影响仍需显式写 isdelete=0。

### 账实规则（改代码前必读）
- **编辑单据禁止更换商品**（Inport/Sales/Retail.updateById 直接抛异常）：库存按商品维度差量调整，换商品必然错账。已退完（order_status=1）的记录禁止编辑，防止静默复活已退完订单。
- 退货扣减单据剩余数量一律走 `decreaseRemaining`（原子 UPDATE + `number>=#{n} AND order_status=0` 条件），返回 0 = 超退/已退完。**禁止**再回到 `setNumber(getNumber()-n) + updateById`。
- 盘点提交改为"原子认领（status 0→1 条件更新）+ 按差异量 increase/decreaseStock"，**禁止**把 actualNum 盲写进 goods.number（会覆盖盘点期间的正常出入库）。
- 会员余额走 MemberMapper 的 addRecharge/deductBalance/addBalance 原子 SQL；等级只升不降且只回写 level 字段（updateById 整个实体会把并发更新覆盖回去）。
- 序列号状态流转走 markSoldIfInStock/markReturnedIfSold 原子条件 UPDATE。

### 验证技巧
- **验证码可以自己 OCR**：`curl -c cookies /warehouse/login/getCaptchaBase64` → base64 解码成 PNG → Read 工具直接看图识码 → 带 cookie + captchaId 登录。自动化全流程测试又可行了。
- 后端 context-path 是 `/warehouse`（application.yml），curl 忘了带会全 404，连白名单接口也是 404 而不是 401。
- SA-Token 拦截器先于路由匹配执行：未登录时任何路径（包括已删除的接口）都返回 401，要区分 404 必须带登录态测。
- 共享库上验证业务流：挑真实单据操作完**按原值回滚**（UPDATE 带旧值条件守卫 + 删测试流水），既验证代码又不留脏数据。

### 前端坑（第四批）
- `request.ts` 的 blob 分支已直接返回 Blob 本体（response.data），调用方再取 `.data` 就是 undefined——登录验证码此前因此永远走 fallback URL。blob 响应的返回值就是 Blob。
- `CrudTable` 的搜索约定：SearchForm 发 search/reset 事件 → 父组件显式调 `tableRef.reload()`；不要在 CrudTable 里 deep watch searchParams（每敲一个字发一次请求）。
- 401 拦截器里清登录态要动态 `import('@/stores/auth')`，否则 request.ts ↔ stores/auth ↔ api/login 循环依赖。
- POS 页购物车商品的数量上限要存加购时的库存快照（maxNumber），不能查当前页商品列表（翻页后查不到会 fallback 成 9999 被绕过）。

## 2026-07-23 第一批安全漏洞修复（验证码/越权/权限粒度/Druid）

### 行为变更（影响既有测试与运维习惯）
- **验证码绕过通道已删除**：`code=x&captchaCode=x` 不再可用（该参数本身就是漏洞）。curl 自动化登录目前没有替代通道——验证码只存服务端 session 且已改为一次性使用（取出即删），密码错误也会消耗验证码，前端登录失败必须刷新验证码图片。如需自动化测试通道，应加 test-only profile 而不是在登录逻辑里留后门。
- **Druid 控制台默认关闭**：dev/test 环境需 `DRUID_CONSOLE_ENABLED=true` + `DRUID_PWD=<强密码>` 才能开启；且 `/druid/**` 已移出免登录白名单，需先登录系统。
- **dev profile 数据库密码无默认值**：`MYSQL_PASSWORD` 环境变量必填。
- **未登录请求现在返回 HTTP 401 + JSON**（GlobalExceptionHandler 新增 NotLoginException 处理）；此前是 Tomcat 500 HTML 错误页，前端 request.ts 的 401 拦截实际从未触发过。
- **权限校验改为操作级**：URL→权限码映射与 sys_permission 种子一致（user:view/create/update/delete 等），`user:view` 不再放行 `user:delete`。sys/bus 常用模块全部显式映射，未映射路径默认放行但会打 WARN 日志（新增 Controller 必须补映射）。`loadDashboardStats`/`loadRecentOperations`/公告查看/个人中心等列入"登录即可用"白名单，避免普通用户首页坏掉。
- **序列号/操作日志接口**（serialNumber:*、operationLog:*）种子库无对应权限码，现在仅超管可用——如果普通业务员需要，要先在 sys_permission 里补权限码并授权。

### 环境坑
- devtools 热重启连远程 MySQL 时报 `Public Key Retrieval is not allowed` 导致重启失败（URL 里明明有 allowPublicKeyRetrieval=true，冷启动正常、热重启失败，疑似 caching_sha2 与 Druid 重建连接池的时序问题）。遇到后端改了代码但行为没变，先看日志是不是热重启挂了，直接全量重启。
- IDEA 运行配置的环境变量存在 `.idea/workspace.xml` 的 `<env name=...>` 里，可用 grep 提取出来给命令行 mvn 用。

## 2026-07-18 库存原子更新 + 查询优化（#7/#8）

### 数据库环境
- **远程开发库** `175.178.96.252/warehouse` 的结构会落后于代码：序列号功能上线后其 DDL（bus_goods 两列、bus_serial_number_log 等）从未同步到该库，warehouse.sql 也漏了 bus_serial_number_log。教训：**新功能的 DDL 必须同时①合入 mysql/warehouse.sql ②生成增量迁移脚本（mysql/migration-YYYYMMDD.sql）③同步到远程库**。
- 本地 MySQL（127.0.0.1）没有 warehouse 库，业务数据只在远程库。
- 排查 schema drift 的最快方式：调一个 MyBatis-Plus 自动查询接口（如 loadAllGoods），错误信息里的 `SELECT ... FROM ...` 会列出实体期望的全部列，与 `SHOW COLUMNS` 对比即得差异。

### 工具链（本机）
- shell 中无 JAVA_HOME / mvn：JDK21 在 `D:/Android Studio/jbr`，Maven 在 `C:/Users/Administrator/.m2/wrapper/dists/apache-maven-3.9.12/57e1dc81/bin/mvn.cmd`。
- 启动命令：`JAVA_HOME=... mvn spring-boot:run -P test`（test profile 用 MYSQL_* 环境变量连远程库；dev profile 连 127.0.0.1，本机无库）。
- `mvn spring-boot:run` 后台停止后，fork 的 Java 子进程不会随之退出，需 `taskkill //PID <pid> //F`（netstat 查 8899 端口占用 PID）。

### API 测试技巧
- 登录验证码绕过：传 `code=x&captchaCode=x`（移动端通道，captchaCode 直接作为期望值参与比对），便于 curl 自动化测试。
- Git Bash 中 curl `-d` 带中文会按 GBK 编码导致后端 400（Invalid UTF-8 start byte），测试数据一律用 ASCII。
- 后端身份：SA-Token cookie（curl `-c/-b` 即可），无需手动带 token 头。

### 代码模式
- 库存变动统一走 `GoodsMapper.decreaseStock/increaseStock`（原子 UPDATE），decreaseStock 返回 0 = 库存不足，Service 抛异常回滚。**禁止**再回到 `goods.setNumber() + updateById` 的 read-modify-write 写法。
- 自定义 XML 不走 MyBatis-Plus `@TableLogic`，必须显式写 `isdelete = 0`。
- 订单分组查询用 `MIN(CASE WHEN order_status=1 THEN 1 ELSE 0 END)` 表示"全部退完"，与 Java allMatch 语义一致。
