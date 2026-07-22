# 经验教训（运行验证后记录）

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
