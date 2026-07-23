# 经验教训（运行验证后记录）

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
