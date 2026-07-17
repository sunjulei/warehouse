# 序列号管理功能设计文档

## 1. 背景与目标

### 1.1 背景

当前仓库管理系统以商品数量为核心进行库存管理，无法满足对高价值单品（如手机、数码设备）的精细化管理需求。每一件商品需要被独立追踪，以支持售后保修、防伪验证、库存盘点等业务。

### 1.2 目标

新增序列号管理功能，实现：

1. **全生命周期追踪**：从进货入库到销售出库，再到退货回库，完整记录每个序列号的流转路径。
2. **售后保修 / 防伪验证**：通过序列号可查询商品来源、销售记录、客户信息。
3. **替代数量管理（针对特定商品）**：开启序列号管理的商品，库存数量由在库序列号数量驱动，实现账实一致。

## 2. 设计原则

- **按商品配置**：只有标记为"管理序列号"的商品才强制录入序列号，避免对所有商品一刀切。
- **强一致性**：序列号状态即库存，销售、退货、盘点等操作必须同步更新序列号状态。
- **集中管理**：所有序列号状态变更统一通过 `SerialNumberService` 完成，不分散在各业务代码中。
- **可追溯**：所有状态变更记录到 `bus_serial_number_log`，支持售后追溯。
- **分阶段实施**：本次先完成 PC 管理端，uni-app x 移动端后续扩展。

## 3. 业务范围

### 3.1 启用规则

在商品档案中增加开关：

- `is_serial_managed`：是否管理序列号。
- `return_resaleable`：退货后是否直接回库可售。

只有 `is_serial_managed = 1` 的商品在业务操作中强制序列号管理。

### 3.2 涉及业务环节

| 环节 | 是否需要序列号 | 说明 |
|------|---------------|------|
| 进货入库 | 是 | 批量录入序列号，状态变为"在库可用" |
| 销售出库 | 是 | 从在库可用序列号中选择或扫描 |
| 零售收银 | 是 | 同销售出库 |
| 销售/零售退货 | 是 | 校验序列号是否属于已售订单，并按配置改状态 |
| 库存盘点 | 是 | 扫描实盘序列号与系统序列号比对，生成盘盈盘亏 |
| 其他出库（调拨/报损） | 本期不做 | 后续按需扩展 |

## 4. 数据模型

### 4.1 `bus_serial_number`（序列号主表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | PK，自增 | 主键 |
| serial_no | varchar(64) | 非空，全局唯一索引 | 序列号 |
| goods_id | int | 非空，索引 | 商品 ID |
| status | tinyint | 非空，默认 0 | 0=在库可用, 1=已售, 2=退货待检, 3=报废, 4=锁定中 |
| provider_id | int | 可空 | 首次进货供应商 ID |
| inport_id | int | 可空 | 首次进货记录 ID |
| current_order_no | varchar(32) | 可空 | 当前关联订单号 |
| customer_id | int | 可空 | 当前客户 ID（已售时） |
| create_time | datetime | 默认当前时间 | 首次入库时间 |
| update_time | datetime | 默认当前时间，更新时刷新 | 最后更新时间 |

**索引建议**：

- `uk_serial_no`：唯一索引，保证全局唯一。
- `idx_goods_id_status`：加速查询某商品可用序列号。
- `idx_current_order_no`：加速按订单追溯。

### 4.2 `bus_serial_number_log`（序列号操作日志表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | PK，自增 | 主键 |
| serial_no | varchar(64) | 非空，索引 | 序列号 |
| goods_id | int | 非空 | 商品 ID |
| action | varchar(20) | 非空 | inport/sale/return/saleback/retail/retailback/stocktake/scrap |
| from_status | tinyint | 可空 | 变更前状态 |
| to_status | tinyint | 非空 | 变更后状态 |
| order_no | varchar(32) | 可空 | 关联订单号 |
| operate_person | varchar(32) | 非空 | 操作人 |
| operate_time | datetime | 默认当前时间 | 操作时间 |
| remark | varchar(255) | 可空 | 备注 |

### 4.3 `bus_goods` 扩展字段

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| is_serial_managed | tinyint | 0 | 0=不管理, 1=管理序列号 |
| return_resaleable | tinyint | 1 | 0=退货后进入待检, 1=退货后直接回库可售 |

## 5. 状态流转

```
在库可用(0)
   │
   ├─ 销售/零售 ──→ 已售(1)
   │
   ├─ 盘点盘盈 ──→ 在库可用(0)
   │
   └─ 报废 ──→ 报废(3)

已售(1)
   │
   └─ 退货 ──→ 退货待检(2)  或  在库可用(0)
              （取决于 goods.return_resaleable）

退货待检(2)
   │
   ├─ 质检通过 ──→ 在库可用(0)
   │
   └─ 质检不通过/报废 ──→ 报废(3)
```

## 6. 核心流程

### 6.1 进货入库

1. 用户选择商品，输入进货数量。
2. 若商品 `is_serial_managed = 1`：
   - 前端弹出序列号录入弹窗。
   - 支持手动输入、Excel 导入、扫码枪扫描。
   - 校验序列号数量与进货数量一致。
   - 校验序列号全局唯一（不存在于 `bus_serial_number`）。
3. 后端保存进货记录。
4. 调用 `ISerialNumberService.batchInport(...)`：
   - 批量插入序列号，status = 0。
   - 记录 provider_id、inport_id。
   - 写入 `bus_serial_number_log`。

### 6.2 销售/零售出库

1. 用户选择商品，输入销售数量。
2. 若商品 `is_serial_managed = 1`：
   - 前端弹出序列号选择弹窗。
   - 列表展示当前 `status = 0` 且 `goods_id = xxx` 的序列号。
   - 支持勾选、扫码枪/摄像头扫描快速选择。
   - 校验已选数量与销售数量一致。
3. 后端保存销售/零售记录。
4. 调用 `ISerialNumberService.batchSale(...)`：
   - 将选定序列号 status 改为 1。
   - 记录 customer_id、current_order_no。
   - 写入日志。

### 6.3 销售/零售退货

1. 用户输入退货订单或扫描序列号。
2. 后端校验序列号当前状态为 `已售(1)`，且属于该订单/客户。
3. 根据商品 `return_resaleable`：
   - =1：直接改为 `在库可用(0)`。
   - =0：改为 `退货待检(2)`，后续人工质检后调整为 `在库可用(0)` 或 `报废(3)`。
4. 调用 `ISerialNumberService.batchReturn(...)` 更新状态并记录日志。

### 6.4 库存盘点

1. 创建盘点单时，系统快照当前在库序列号。
2. 盘点时扫描实际序列号。
3. 比对结果：
   - 实盘有序号、系统无 → 盘盈。
   - 系统有序号、实盘无 → 盘亏。
   - 都有的 → 正常。
4. 用户确认盘点结果后：
   - 盘盈：新增序列号记录，status = 0。
   - 盘亏：将缺失序列号 status 改为 3（报废）或保留待处理。
5. 所有变更写入日志。

## 7. 后端设计

### 7.1 新增模块

包路径：`com.sunlee.bus.serial`

| 文件 | 说明 |
|------|------|
| `entity/SerialNumber.java` | 序列号实体 |
| `entity/SerialNumberLog.java` | 序列号日志实体 |
| `mapper/SerialNumberMapper.java` | 序列号 Mapper |
| `mapper/SerialNumberLogMapper.java` | 序列号日志 Mapper |
| `service/ISerialNumberService.java` | 序列号 Service 接口 |
| `service/impl/SerialNumberServiceImpl.java` | 序列号 Service 实现 |
| `controller/SerialNumberController.java` | 序列号 Controller |
| `vo/SerialNumberVo.java` | 查询 VO |

### 7.2 新增 Mapper XML

- `mapper/bus/SerialNumberMapper.xml`
- `mapper/bus/SerialNumberLogMapper.xml`

### 7.3 关键 Service 方法

```java
public interface ISerialNumberService {
    /**
     * 批量入库
     */
    ResultObj batchInport(Integer goodsId, Integer providerId, Integer inportId,
                          List<String> serialNos, String operatePerson);

    /**
     * 批量销售
     */
    ResultObj batchSale(Integer goodsId, Integer customerId, String orderNo,
                        List<String> serialNos, String operatePerson);

    /**
     * 批量零售
     */
    ResultObj batchRetail(Integer goodsId, String orderNo,
                          List<String> serialNos, String operatePerson);

    /**
     * 批量退货
     */
    ResultObj batchReturn(String orderNo, List<String> serialNos,
                          String operatePerson, boolean directResaleable);

    /**
     * 查询商品可用序列号
     */
    List<SerialNumber> getAvailableByGoodsId(Integer goodsId);

    /**
     * 序列号全生命周期追溯
     */
    List<SerialNumberLog> getTrace(String serialNo);

    /**
     * 校验序列号是否可用
     */
    boolean checkAvailable(String serialNo, Integer goodsId);

    /**
     * 人工调整状态
     */
    ResultObj manualAdjustStatus(Integer id, Integer newStatus, String remark, String operatePerson);
}
```

### 7.4 新增 Controller 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/serial/loadAllSerialNumber` | GET | 分页查询序列号列表 |
| `/serial/checkSerialNo` | GET | 校验序列号是否已存在 |
| `/serial/getAvailableSerialNumbers` | GET | 获取某商品可用序列号 |
| `/serial/getSerialNumberTrace` | GET | 查询序列号追溯日志 |
| `/serial/manualAdjustStatus` | POST | 人工调整序列号状态 |

### 7.5 现有 Service 改动

| Service | 改动点 |
|---------|--------|
| `InportServiceImpl` | 保存进货记录后调用 `batchInport` |
| `SalesServiceImpl` | 保存销售记录后调用 `batchSale` |
| `RetailServiceImpl` | 保存零售记录后调用 `batchRetail` |
| `SalesbackServiceImpl` | 退货校验后调用 `batchReturn` |
| `RetailbackServiceImpl` | 退货校验后调用 `batchReturn` |
| `StocktakeServiceImpl` | 盘点确认时调用序列号盘盈盘亏处理 |

## 8. 前端设计

### 8.1 新增页面

| 页面 | 路径 | 说明 |
|------|------|------|
| 序列号管理 | `views/business/serial-number/index.vue` | 列表、查询、状态查看 |
| 序列号追溯 | `views/business/serial-number/trace.vue` 或弹窗 | 单序列号全生命周期日志 |

### 8.2 现有页面改造

| 页面 | 改动 |
|------|------|
| `views/business/goods/index.vue` | 增加"是否管理序列号"、"退货后是否直接回库"开关 |
| `views/business/inport/index.vue` | 序列号商品进货时弹窗录入序列号 |
| `views/business/inport-order/index.vue` | 同上 |
| `views/business/sales/index.vue` | 销售时弹窗选择序列号 |
| `views/business/sales-order/index.vue` | 同上 |
| `views/business/sales-pos/index.vue` | 同上，强调扫码 |
| `views/business/retail/index.vue` | 零售时弹窗选择序列号 |
| `views/business/retail-order/index.vue` | 同上 |
| `views/business/salesback/index.vue` | 退货时扫描/输入序列号 |
| `views/business/retailback/index.vue` | 同上 |
| `views/business/stocktake/index.vue` | 盘点时扫描序列号并生成差异 |

### 8.3 新增组件

| 组件 | 说明 |
|------|------|
| `components/SerialNumberInput.vue` | 序列号录入：手动输入、Excel 导入、扫码 |
| `components/SerialNumberSelector.vue` | 序列号选择：从可用列表选择、扫码 |

### 8.4 新增前端资源

- `src/api/serialNumber.ts`
- `src/types/serialNumber.d.ts`

## 9. 权限设计

新增权限标识：

| 权限 | 说明 |
|------|------|
| `serial:view` | 查看序列号列表 |
| `serial:trace` | 序列号追溯 |
| `serial:adjust` | 人工调整序列号状态 |

业务操作权限复用现有菜单权限，如 `inport:add`、`sales:add`、`stocktake:manage` 等。

## 10. 异常处理

| 异常场景 | 处理方式 |
|----------|----------|
| 序列号已存在 | 返回明确错误，列出重复序列号 |
| 序列号数量与业务数量不一致 | 拒绝提交，提示必须一一对应 |
| 销售时选择非可用序列号 | 拒绝提交，提示序列号不可用 |
| 退货时序列号不属于该订单 | 拒绝提交，提示无法退货 |
| 高并发重复销售同一序列号 | 数据库唯一索引 + 乐观锁防止 |
| Excel 导入格式错误 | 返回错误行号及原因 |

## 11. 数据库变更脚本

```sql
-- 扩展商品表
ALTER TABLE bus_goods
    ADD COLUMN is_serial_managed TINYINT NOT NULL DEFAULT 0 COMMENT '是否管理序列号 0=否 1=是',
    ADD COLUMN return_resaleable TINYINT NOT NULL DEFAULT 1 COMMENT '退货后是否直接回库 0=待检 1=直接可售';

-- 序列号主表
CREATE TABLE bus_serial_number (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    serial_no VARCHAR(64) NOT NULL COMMENT '序列号',
    goods_id INT NOT NULL COMMENT '商品ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0=在库可用 1=已售 2=退货待检 3=报废 4=锁定中',
    provider_id INT DEFAULT NULL COMMENT '首次进货供应商ID',
    inport_id INT DEFAULT NULL COMMENT '首次进货记录ID',
    current_order_no VARCHAR(32) DEFAULT NULL COMMENT '当前关联订单号',
    customer_id INT DEFAULT NULL COMMENT '当前客户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_serial_no (serial_no),
    KEY idx_goods_id_status (goods_id, status),
    KEY idx_current_order_no (current_order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='序列号主表';

-- 序列号操作日志表
CREATE TABLE bus_serial_number_log (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    serial_no VARCHAR(64) NOT NULL COMMENT '序列号',
    goods_id INT NOT NULL COMMENT '商品ID',
    action VARCHAR(20) NOT NULL COMMENT '操作类型',
    from_status TINYINT DEFAULT NULL COMMENT '变更前状态',
    to_status TINYINT NOT NULL COMMENT '变更后状态',
    order_no VARCHAR(32) DEFAULT NULL COMMENT '关联订单号',
    operate_person VARCHAR(32) NOT NULL COMMENT '操作人',
    operate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    KEY idx_serial_no (serial_no),
    KEY idx_goods_id (goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='序列号操作日志表';
```

## 12. 实施阶段

本次实施只覆盖 **PC 端（warehouse-front）**，uni-app x 移动端后续单独规划。

建议开发顺序：

1. 数据库表创建 + 商品表字段扩展。
2. 后端序列号基础模块（Entity、Mapper、Service、Controller）。
3. 商品档案页面增加序列号开关。
4. 进货流程接入序列号录入。
5. 销售/零售流程接入序列号选择。
6. 退货流程接入序列号校验与回库。
7. 盘点流程接入序列号扫描比对。
8. 序列号管理列表页面 + 追溯页面。
9. 集成测试与异常场景验证。

## 13. 待后续扩展

- uni-app x 移动端序列号扫码录入/选择。
- 序列号批量导入模板下载。
- 序列号保修期管理。
- 序列号批次号管理。
- 序列号与供应商质保单关联。
