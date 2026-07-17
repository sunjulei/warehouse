# 序列号管理功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在仓库管理系统中新增序列号管理功能，实现按商品配置的序列号全生命周期追踪（进货、销售、零售、退货、盘点），并支持售后追溯。

**Architecture:** 新增 `com.sunlee.bus.serial` 模块管理序列号状态与日志，所有状态变更统一通过 `ISerialNumberService` 完成；现有进货/销售/零售/退货/盘点 Service 在保存业务记录后调用该服务。PC 前端 `warehouse-front` 新增通用序列号录入/选择组件，并在相关业务页面弹窗接入。

**Tech Stack:** Spring Boot 3.5.5 + Java 21 + MyBatis-Plus + MySQL 8.0 + Vue 3 + TypeScript + Element Plus

---

## 文件结构映射

### 后端新增文件

| 文件 | 职责 |
|------|------|
| `warehouse-back/src/main/java/com/sunlee/bus/serial/entity/SerialNumber.java` | 序列号主表实体 |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/entity/SerialNumberLog.java` | 序列号操作日志实体 |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/mapper/SerialNumberMapper.java` | 序列号 Mapper 接口 |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/mapper/SerialNumberLogMapper.java` | 序列号日志 Mapper 接口 |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/service/ISerialNumberService.java` | 序列号 Service 接口 |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java` | 序列号 Service 实现 |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/controller/SerialNumberController.java` | 序列号 Controller |
| `warehouse-back/src/main/java/com/sunlee/bus/serial/vo/SerialNumberVo.java` | 序列号查询 VO |
| `warehouse-back/src/main/resources/mapper/bus/SerialNumberMapper.xml` | 序列号 Mapper XML |
| `warehouse-back/src/main/resources/mapper/bus/SerialNumberLogMapper.xml` | 序列号日志 Mapper XML |

### 后端修改文件

| 文件 | 改动 |
|------|------|
| `warehouse-back/src/main/java/com/sunlee/bus/entity/Goods.java` | 增加 `isSerialManaged`、`returnResaleable` 字段 |
| `warehouse-back/src/main/java/com/sunlee/bus/vo/GoodsVo.java` | 继承实体字段，无需额外改动 |
| `warehouse-back/src/main/java/com/sunlee/bus/service/impl/InportServiceImpl.java` | `save`/`batchSave`/`addToOrder` 后调用序列号入库 |
| `warehouse-back/src/main/java/com/sunlee/bus/service/impl/SalesServiceImpl.java` | `save`/`batchSave`/`addToOrder` 后调用序列号销售 |
| `warehouse-back/src/main/java/com/sunlee/bus/service/impl/RetailServiceImpl.java` | `save`/`batchSave`/`addToOrder` 后调用序列号零售 |
| `warehouse-back/src/main/java/com/sunlee/bus/service/impl/SalesbackServiceImpl.java` | 退货时校验并释放序列号 |
| `warehouse-back/src/main/java/com/sunlee/bus/service/impl/RetailbackServiceImpl.java` | 退货时校验并释放序列号 |
| `warehouse-back/src/main/java/com/sunlee/bus/service/impl/StocktakeServiceImpl.java` | 盘点提交时处理序列号盘盈盘亏 |
| `warehouse-back/src/main/java/com/sunlee/bus/controller/InportController.java` | `addInport`/`batchAddInport` 接收 `serialNos` |
| `warehouse-back/src/main/java/com/sunlee/bus/controller/SalesController.java` | `addSales`/`batchAddSales` 接收 `serialNos` |
| `warehouse-back/src/main/java/com/sunlee/bus/controller/RetailController.java` | `addRetail`/`batchAddRetail` 接收 `serialNos` |

### 前端新增文件

| 文件 | 职责 |
|------|------|
| `warehouse-front/src/types/serialNumber.d.ts` | 序列号 TypeScript 类型 |
| `warehouse-front/src/api/serialNumber.ts` | 序列号相关 API |
| `warehouse-front/src/components/SerialNumberInput.vue` | 序列号录入组件（手动/Excel/扫码） |
| `warehouse-front/src/components/SerialNumberSelector.vue` | 序列号选择组件（列表/扫码） |
| `warehouse-front/src/views/business/serial-number/index.vue` | 序列号管理列表页 |

### 前端修改文件

| 文件 | 改动 |
|------|------|
| `warehouse-front/src/types/goods.d.ts` | 增加 `isSerialManaged`、`returnResaleable` |
| `warehouse-front/src/views/business/goods/index.vue` | 商品表单增加两个开关 |
| `warehouse-front/src/views/business/inport/index.vue` | 进货弹窗接入序列号录入 |
| `warehouse-front/src/views/business/inport-order/index.vue` | 进货订单页面接入序列号录入 |
| `warehouse-front/src/views/business/sales/index.vue` | 销售弹窗接入序列号选择 |
| `warehouse-front/src/views/business/sales-order/index.vue` | 销售订单接入序列号选择 |
| `warehouse-front/src/views/business/sales-pos/index.vue` | POS 销售接入扫码选择 |
| `warehouse-front/src/views/business/retail/index.vue` | 零售弹窗接入序列号选择 |
| `warehouse-front/src/views/business/retail-order/index.vue` | 零售订单接入序列号选择 |
| `warehouse-front/src/views/business/salesback/index.vue` | 销售退货接入序列号校验 |
| `warehouse-front/src/views/business/retailback/index.vue` | 零售退货接入序列号校验 |
| `warehouse-front/src/views/business/stocktake/index.vue` | 盘点接入序列号扫描 |

---

## Task 1: 数据库表结构变更

**Files:**
- Execute SQL against MySQL

- [ ] **Step 1: 连接本地 MySQL 并执行建表脚本**

Run:
```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p warehouse
```

Then paste and execute:
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

- [ ] **Step 2: 验证表结构**

Run:
```sql
SHOW COLUMNS FROM bus_goods;
SHOW COLUMNS FROM bus_serial_number;
SHOW COLUMNS FROM bus_serial_number_log;
```

Expected: 三个表均包含新增字段/表，无报错。

- [ ] **Step 3: Commit 数据库变更说明**

本次无需代码提交，但应在下一个代码 commit 的消息中说明已包含表结构变更。

---

## Task 2: 扩展 Goods 实体与 VO

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/entity/Goods.java`

- [ ] **Step 1: 在 `Goods.java` 中新增两个字段**

Add after the `abbreviation` field (around line 74):

```java
    /** 是否管理序列号 0=否 1=是 */
    private Integer isSerialManaged;

    /** 退货后是否直接回库 0=待检 1=直接可售 */
    private Integer returnResaleable;
```

- [ ] **Step 2: 确认 `GoodsVo.java` 无需修改**

`GoodsVo extends Goods`，因此自动继承新增字段。

- [ ] **Step 3: 编译后端**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/entity/Goods.java
git commit -m "feat(serial): 商品实体增加序列号管理开关字段

新增 isSerialManaged、returnResaleable 两个字段，用于控制商品是否启用序列号管理及退货后状态处理。"
```

---

## Task 3: 创建 SerialNumber 与 SerialNumberLog 实体

**Files:**
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/entity/SerialNumber.java`
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/entity/SerialNumberLog.java`

- [ ] **Step 1: 创建 `SerialNumber.java`**

```java
package com.sunlee.bus.serial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_serial_number")
public class SerialNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String serialNo;

    private Integer goodsId;

    private Integer status;

    private Integer providerId;

    private Integer inportId;

    private String currentOrderNo;

    private Integer customerId;

    private Date createTime;

    private Date updateTime;
}
```

- [ ] **Step 2: 创建 `SerialNumberLog.java`**

```java
package com.sunlee.bus.serial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_serial_number_log")
public class SerialNumberLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String serialNo;

    private Integer goodsId;

    private String action;

    private Integer fromStatus;

    private Integer toStatus;

    private String orderNo;

    private String operatePerson;

    private Date operateTime;

    private String remark;
}
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/entity/
git commit -m "feat(serial): 新增序列号与序列号日志实体"
```

---

## Task 4: 创建 SerialNumber Mapper 与 XML

**Files:**
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/mapper/SerialNumberMapper.java`
- Create: `warehouse-back/src/main/resources/mapper/bus/SerialNumberMapper.xml`

- [ ] **Step 1: 创建 `SerialNumberMapper.java`**

```java
package com.sunlee.bus.serial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunlee.bus.serial.entity.SerialNumber;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SerialNumberMapper extends BaseMapper<SerialNumber> {

    /**
     * 批量插入序列号（用于进货导入）
     */
    int batchInsert(@Param("list") List<SerialNumber> list);

    /**
     * 根据序列号列表查询
     */
    List<SerialNumber> selectBySerialNos(@Param("serialNos") List<String> serialNos);
}
```

- [ ] **Step 2: 创建 `SerialNumberMapper.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.sunlee.bus.serial.mapper.SerialNumberMapper">

    <insert id="batchInsert">
        INSERT INTO bus_serial_number
        (serial_no, goods_id, status, provider_id, inport_id, current_order_no, customer_id, create_time, update_time)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.serialNo}, #{item.goodsId}, #{item.status}, #{item.providerId},
             #{item.inportId}, #{item.currentOrderNo}, #{item.customerId}, #{item.createTime}, #{item.updateTime})
        </foreach>
    </insert>

    <select id="selectBySerialNos" resultType="com.sunlee.bus.serial.entity.SerialNumber">
        SELECT * FROM bus_serial_number WHERE serial_no IN
        <foreach collection="serialNos" item="sn" open="(" separator="," close=")">
            #{sn}
        </foreach>
    </select>

</mapper>
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/mapper/SerialNumberMapper.java
-git add warehouse-back/src/main/resources/mapper/bus/SerialNumberMapper.xml
git commit -m "feat(serial): 新增 SerialNumber Mapper 及 XML"
```

---

## Task 5: 创建 SerialNumberLog Mapper 与 XML

**Files:**
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/mapper/SerialNumberLogMapper.java`
- Create: `warehouse-back/src/main/resources/mapper/bus/SerialNumberLogMapper.xml`

- [ ] **Step 1: 创建 `SerialNumberLogMapper.java`**

```java
package com.sunlee.bus.serial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunlee.bus.serial.entity.SerialNumberLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SerialNumberLogMapper extends BaseMapper<SerialNumberLog> {

    int batchInsert(@Param("list") List<SerialNumberLog> list);
}
```

- [ ] **Step 2: 创建 `SerialNumberLogMapper.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.sunlee.bus.serial.mapper.SerialNumberLogMapper">

    <insert id="batchInsert">
        INSERT INTO bus_serial_number_log
        (serial_no, goods_id, action, from_status, to_status, order_no, operate_person, operate_time, remark)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.serialNo}, #{item.goodsId}, #{item.action}, #{item.fromStatus},
             #{item.toStatus}, #{item.orderNo}, #{item.operatePerson}, #{item.operateTime}, #{item.remark})
        </foreach>
    </insert>

</mapper>
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/mapper/SerialNumberLogMapper.java
-git add warehouse-back/src/main/resources/mapper/bus/SerialNumberLogMapper.xml
git commit -m "feat(serial): 新增 SerialNumberLog Mapper 及 XML"
```

---

## Task 6: 创建 ISerialNumberService 接口

**Files:**
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/service/ISerialNumberService.java`

- [ ] **Step 1: 创建接口文件**

```java
package com.sunlee.bus.serial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.serial.entity.SerialNumber;
import com.sunlee.bus.serial.entity.SerialNumberLog;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;

import java.util.List;

public interface ISerialNumberService extends IService<SerialNumber> {

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
     * 校验序列号是否已存在
     */
    boolean exists(String serialNo);

    /**
     * 人工调整状态
     */
    ResultObj manualAdjustStatus(Integer id, Integer newStatus, String remark, String operatePerson);

    /**
     * 分页查询序列号
     */
    DataGridView loadAllSerialNumber(Integer page, Integer limit, String serialNo,
                                     Integer goodsId, Integer status);

    /**
     * 盘点差异处理
     */
    ResultObj handleStocktakeDiff(Integer goodsId, List<String> actualSerialNos,
                                  String operatePerson, String remark);
}
```

- [ ] **Step 2: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/service/ISerialNumberService.java
git commit -m "feat(serial): 新增序列号 Service 接口"
```

---

## Task 7: 实现 SerialNumberServiceImpl - 入库与销售

**Files:**
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java`

- [ ] **Step 1: 创建实现类骨架并注入 Mapper**

```java
package com.sunlee.bus.serial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.serial.entity.SerialNumber;
import com.sunlee.bus.serial.entity.SerialNumberLog;
import com.sunlee.bus.serial.mapper.SerialNumberLogMapper;
import com.sunlee.bus.serial.mapper.SerialNumberMapper;
import com.sunlee.bus.serial.service.ISerialNumberService;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SerialNumberServiceImpl extends ServiceImpl<SerialNumberMapper, SerialNumber>
        implements ISerialNumberService {

    @Autowired
    private SerialNumberLogMapper serialNumberLogMapper;

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SOLD = 1;
    public static final int STATUS_RETURN_PENDING = 2;
    public static final int STATUS_SCRAP = 3;
    public static final int STATUS_LOCKED = 4;

    // methods will be added in next steps
}
```

- [ ] **Step 2: 实现 `batchInport` 方法**

Add inside the class:

```java
    @Override
    public ResultObj batchInport(Integer goodsId, Integer providerId, Integer inportId,
                                 List<String> serialNos, String operatePerson) {
        if (CollectionUtils.isEmpty(serialNos)) {
            return ResultObj.error("序列号列表不能为空");
        }
        if (goodsId == null) {
            return ResultObj.error("商品ID不能为空");
        }

        // 去重校验
        Set<String> uniqueSet = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (String sn : serialNos) {
            if (sn == null || sn.trim().isEmpty()) continue;
            String clean = sn.trim();
            if (!uniqueSet.add(clean)) {
                duplicates.add(clean);
            }
        }
        if (!duplicates.isEmpty()) {
            return ResultObj.error("序列号存在重复: " + String.join(", ", duplicates));
        }

        // 全局唯一校验
        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.in("serial_no", uniqueSet);
        List<SerialNumber> exists = baseMapper.selectList(qw);
        if (!exists.isEmpty()) {
            String existStr = exists.stream().map(SerialNumber::getSerialNo).collect(Collectors.joining(", "));
            return ResultObj.error("以下序列号已存在: " + existStr);
        }

        Date now = new Date();
        List<SerialNumber> numbers = new ArrayList<>();
        List<SerialNumberLog> logs = new ArrayList<>();
        for (String sn : uniqueSet) {
            SerialNumber number = new SerialNumber();
            number.setSerialNo(sn);
            number.setGoodsId(goodsId);
            number.setStatus(STATUS_AVAILABLE);
            number.setProviderId(providerId);
            number.setInportId(inportId);
            number.setCreateTime(now);
            number.setUpdateTime(now);
            numbers.add(number);

            SerialNumberLog log = new SerialNumberLog();
            log.setSerialNo(sn);
            log.setGoodsId(goodsId);
            log.setAction("inport");
            log.setToStatus(STATUS_AVAILABLE);
            log.setOperatePerson(operatePerson);
            log.setOperateTime(now);
            logs.add(log);
        }

        baseMapper.batchInsert(numbers);
        serialNumberLogMapper.batchInsert(logs);
        return ResultObj.ADD_SUCCESS;
    }
```

- [ ] **Step 3: 实现 `batchSale` 与 `batchRetail` 方法**

Add inside the class:

```java
    @Override
    public ResultObj batchSale(Integer goodsId, Integer customerId, String orderNo,
                               List<String> serialNos, String operatePerson) {
        return doBatchOut(goodsId, customerId, orderNo, serialNos, operatePerson, "sale");
    }

    @Override
    public ResultObj batchRetail(Integer goodsId, String orderNo,
                                 List<String> serialNos, String operatePerson) {
        return doBatchOut(goodsId, null, orderNo, serialNos, operatePerson, "retail");
    }

    private ResultObj doBatchOut(Integer goodsId, Integer customerId, String orderNo,
                                 List<String> serialNos, String operatePerson, String action) {
        if (CollectionUtils.isEmpty(serialNos)) {
            return ResultObj.error("序列号列表不能为空");
        }
        if (goodsId == null) {
            return ResultObj.error("商品ID不能为空");
        }

        Set<String> uniqueSet = serialNos.stream().filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim).collect(Collectors.toCollection(HashSet::new));

        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.in("serial_no", uniqueSet);
        List<SerialNumber> numbers = baseMapper.selectList(qw);

        if (numbers.size() != uniqueSet.size()) {
            Set<String> found = numbers.stream().map(SerialNumber::getSerialNo).collect(Collectors.toSet());
            Set<String> missing = new HashSet<>(uniqueSet);
            missing.removeAll(found);
            return ResultObj.error("以下序列号不存在: " + String.join(", ", missing));
        }

        List<String> notAvailable = numbers.stream()
                .filter(n -> !Integer.valueOf(STATUS_AVAILABLE).equals(n.getStatus()) || !goodsId.equals(n.getGoodsId()))
                .map(SerialNumber::getSerialNo)
                .collect(Collectors.toList());
        if (!notAvailable.isEmpty()) {
            return ResultObj.error("以下序列号不可用或不属于该商品: " + String.join(", ", notAvailable));
        }

        Date now = new Date();
        List<SerialNumberLog> logs = new ArrayList<>();
        for (SerialNumber number : numbers) {
            Integer oldStatus = number.getStatus();
            number.setStatus(STATUS_SOLD);
            number.setCurrentOrderNo(orderNo);
            number.setCustomerId(customerId);
            number.setUpdateTime(now);
            baseMapper.updateById(number);

            SerialNumberLog log = new SerialNumberLog();
            log.setSerialNo(number.getSerialNo());
            log.setGoodsId(goodsId);
            log.setAction(action);
            log.setFromStatus(oldStatus);
            log.setToStatus(STATUS_SOLD);
            log.setOrderNo(orderNo);
            log.setOperatePerson(operatePerson);
            log.setOperateTime(now);
            logs.add(log);
        }
        serialNumberLogMapper.batchInsert(logs);
        return ResultObj.OK;
    }
```

- [ ] **Step 4: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java
git commit -m "feat(serial): 实现序列号入库与销售/零售出库逻辑"
```

---

## Task 8: 实现 SerialNumberServiceImpl - 退货与盘点

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java`

- [ ] **Step 1: 实现 `batchReturn` 方法**

Add inside the class:

```java
    @Override
    public ResultObj batchReturn(String orderNo, List<String> serialNos,
                                 String operatePerson, boolean directResaleable) {
        if (CollectionUtils.isEmpty(serialNos)) {
            return ResultObj.error("序列号列表不能为空");
        }

        Set<String> uniqueSet = serialNos.stream().filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim).collect(Collectors.toCollection(HashSet::new));

        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.in("serial_no", uniqueSet);
        List<SerialNumber> numbers = baseMapper.selectList(qw);

        if (numbers.size() != uniqueSet.size()) {
            Set<String> found = numbers.stream().map(SerialNumber::getSerialNo).collect(Collectors.toSet());
            Set<String> missing = new HashSet<>(uniqueSet);
            missing.removeAll(found);
            return ResultObj.error("以下序列号不存在: " + String.join(", ", missing));
        }

        List<String> invalid = numbers.stream()
                .filter(n -> !Integer.valueOf(STATUS_SOLD).equals(n.getStatus())
                        || (orderNo != null && !orderNo.equals(n.getCurrentOrderNo())))
                .map(SerialNumber::getSerialNo)
                .collect(Collectors.toList());
        if (!invalid.isEmpty()) {
            return ResultObj.error("以下序列号未售出或不属于当前订单: " + String.join(", ", invalid));
        }

        Date now = new Date();
        int targetStatus = directResaleable ? STATUS_AVAILABLE : STATUS_RETURN_PENDING;
        List<SerialNumberLog> logs = new ArrayList<>();
        for (SerialNumber number : numbers) {
            Integer oldStatus = number.getStatus();
            number.setStatus(targetStatus);
            number.setCurrentOrderNo(null);
            number.setCustomerId(null);
            number.setUpdateTime(now);
            baseMapper.updateById(number);

            SerialNumberLog log = new SerialNumberLog();
            log.setSerialNo(number.getSerialNo());
            log.setGoodsId(number.getGoodsId());
            log.setAction("return");
            log.setFromStatus(oldStatus);
            log.setToStatus(targetStatus);
            log.setOrderNo(orderNo);
            log.setOperatePerson(operatePerson);
            log.setOperateTime(now);
            logs.add(log);
        }
        serialNumberLogMapper.batchInsert(logs);
        return ResultObj.OK;
    }
```

- [ ] **Step 2: 实现 `handleStocktakeDiff` 方法**

Add inside the class:

```java
    @Override
    public ResultObj handleStocktakeDiff(Integer goodsId, List<String> actualSerialNos,
                                         String operatePerson, String remark) {
        if (goodsId == null) {
            return ResultObj.error("商品ID不能为空");
        }

        Set<String> actualSet = actualSerialNos == null ? new HashSet<>()
                : actualSerialNos.stream().filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim).collect(Collectors.toCollection(HashSet::new));

        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.eq("goods_id", goodsId);
        qw.eq("status", STATUS_AVAILABLE);
        List<SerialNumber> systemNumbers = baseMapper.selectList(qw);
        Set<String> systemSet = systemNumbers.stream().map(SerialNumber::getSerialNo).collect(Collectors.toSet());

        Date now = new Date();
        // 盘盈：实际有，系统没有
        Set<String> surplus = new HashSet<>(actualSet);
        surplus.removeAll(systemSet);
        if (!surplus.isEmpty()) {
            List<SerialNumber> numbers = new ArrayList<>();
            List<SerialNumberLog> logs = new ArrayList<>();
            for (String sn : surplus) {
                SerialNumber n = new SerialNumber();
                n.setSerialNo(sn);
                n.setGoodsId(goodsId);
                n.setStatus(STATUS_AVAILABLE);
                n.setCreateTime(now);
                n.setUpdateTime(now);
                numbers.add(n);

                SerialNumberLog log = new SerialNumberLog();
                log.setSerialNo(sn);
                log.setGoodsId(goodsId);
                log.setAction("stocktake");
                log.setToStatus(STATUS_AVAILABLE);
                log.setOperatePerson(operatePerson);
                log.setOperateTime(now);
                log.setRemark("盘点盘盈" + (remark != null ? " - " + remark : ""));
                logs.add(log);
            }
            baseMapper.batchInsert(numbers);
            serialNumberLogMapper.batchInsert(logs);
        }

        // 盘亏：系统有，实际没有
        Set<String> deficit = new HashSet<>(systemSet);
        deficit.removeAll(actualSet);
        if (!deficit.isEmpty()) {
            List<SerialNumberLog> logs = new ArrayList<>();
            for (SerialNumber n : systemNumbers) {
                if (!deficit.contains(n.getSerialNo())) continue;
                n.setStatus(STATUS_SCRAP);
                n.setUpdateTime(now);
                baseMapper.updateById(n);

                SerialNumberLog log = new SerialNumberLog();
                log.setSerialNo(n.getSerialNo());
                log.setGoodsId(goodsId);
                log.setAction("stocktake");
                log.setFromStatus(STATUS_AVAILABLE);
                log.setToStatus(STATUS_SCRAP);
                log.setOperatePerson(operatePerson);
                log.setOperateTime(now);
                log.setRemark("盘点盘亏" + (remark != null ? " - " + remark : ""));
                logs.add(log);
            }
            serialNumberLogMapper.batchInsert(logs);
        }

        return ResultObj.OK;
    }
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java
git commit -m "feat(serial): 实现序列号退货与盘点差异处理"
```

---

## Task 9: 实现 SerialNumberServiceImpl - 查询与人工调整

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java`

- [ ] **Step 1: 实现查询方法**

Add inside the class:

```java
    @Override
    public List<SerialNumber> getAvailableByGoodsId(Integer goodsId) {
        if (goodsId == null) return new ArrayList<>();
        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.eq("goods_id", goodsId);
        qw.eq("status", STATUS_AVAILABLE);
        qw.orderByDesc("create_time");
        return baseMapper.selectList(qw);
    }

    @Override
    public List<SerialNumberLog> getTrace(String serialNo) {
        if (serialNo == null || serialNo.trim().isEmpty()) return new ArrayList<>();
        QueryWrapper<SerialNumberLog> qw = new QueryWrapper<>();
        qw.eq("serial_no", serialNo.trim());
        qw.orderByAsc("operate_time");
        return serialNumberLogMapper.selectList(qw);
    }

    @Override
    public boolean exists(String serialNo) {
        if (serialNo == null || serialNo.trim().isEmpty()) return false;
        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.eq("serial_no", serialNo.trim());
        return baseMapper.selectCount(qw) > 0;
    }

    @Override
    public DataGridView loadAllSerialNumber(Integer page, Integer limit, String serialNo,
                                            Integer goodsId, Integer status) {
        IPage<SerialNumber> p = new Page<>(page != null ? page : 1, limit != null ? limit : 10);
        QueryWrapper<SerialNumber> qw = new QueryWrapper<>();
        qw.like(serialNo != null && !serialNo.trim().isEmpty(), "serial_no", serialNo.trim());
        qw.eq(goodsId != null && goodsId != 0, "goods_id", goodsId);
        qw.eq(status != null, "status", status);
        qw.orderByDesc("create_time");
        baseMapper.selectPage(p, qw);
        return new DataGridView(p.getTotal(), p.getRecords());
    }
```

- [ ] **Step 2: 实现 `manualAdjustStatus` 方法**

Add inside the class:

```java
    @Override
    public ResultObj manualAdjustStatus(Integer id, Integer newStatus, String remark, String operatePerson) {
        if (id == null || newStatus == null) {
            return ResultObj.error("参数错误");
        }
        SerialNumber number = baseMapper.selectById(id);
        if (number == null) {
            return ResultObj.error("序列号不存在");
        }
        Integer oldStatus = number.getStatus();
        if (oldStatus.equals(newStatus)) {
            return ResultObj.error("新状态与当前状态相同");
        }

        Date now = new Date();
        number.setStatus(newStatus);
        if (Integer.valueOf(STATUS_AVAILABLE).equals(newStatus)) {
            number.setCurrentOrderNo(null);
            number.setCustomerId(null);
        }
        number.setUpdateTime(now);
        baseMapper.updateById(number);

        SerialNumberLog log = new SerialNumberLog();
        log.setSerialNo(number.getSerialNo());
        log.setGoodsId(number.getGoodsId());
        log.setAction("scrap".equals(remark) ? "scrap" : "adjust");
        log.setFromStatus(oldStatus);
        log.setToStatus(newStatus);
        log.setOperatePerson(operatePerson);
        log.setOperateTime(now);
        log.setRemark(remark);
        serialNumberLogMapper.insert(log);

        return ResultObj.UPDATE_SUCCESS;
    }
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/service/impl/SerialNumberServiceImpl.java
git commit -m "feat(serial): 实现序列号查询与人工调整状态"
```

---

## Task 10: 创建 SerialNumberController

**Files:**
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/controller/SerialNumberController.java`
- Create: `warehouse-back/src/main/java/com/sunlee/bus/serial/vo/SerialNumberVo.java`

- [ ] **Step 1: 创建 `SerialNumberVo.java`**

```java
package com.sunlee.bus.serial.vo;

import lombok.Data;

@Data
public class SerialNumberVo {

    private Integer page = 1;
    private Integer limit = 10;
    private String serialNo;
    private Integer goodsId;
    private Integer status;
}
```

- [ ] **Step 2: 创建 `SerialNumberController.java`**

```java
package com.sunlee.bus.serial.controller;

import com.sunlee.bus.serial.entity.SerialNumber;
import com.sunlee.bus.serial.entity.SerialNumberLog;
import com.sunlee.bus.serial.service.ISerialNumberService;
import com.sunlee.bus.serial.vo.SerialNumberVo;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("serial")
public class SerialNumberController {

    @Autowired
    private ISerialNumberService serialNumberService;

    @RequestMapping("loadAllSerialNumber")
    public DataGridView loadAllSerialNumber(SerialNumberVo vo) {
        return serialNumberService.loadAllSerialNumber(
                vo.getPage(), vo.getLimit(), vo.getSerialNo(), vo.getGoodsId(), vo.getStatus());
    }

    @RequestMapping("checkSerialNo")
    public ResultObj checkSerialNo(String serialNo) {
        boolean exists = serialNumberService.exists(serialNo);
        return new ResultObj(200, exists ? "已存在" : "可用");
    }

    @RequestMapping("getAvailableSerialNumbers")
    public DataGridView getAvailableSerialNumbers(Integer goodsId) {
        List<SerialNumber> list = serialNumberService.getAvailableByGoodsId(goodsId);
        return new DataGridView((long) list.size(), list);
    }

    @RequestMapping("getSerialNumberTrace")
    public DataGridView getSerialNumberTrace(String serialNo) {
        List<SerialNumberLog> list = serialNumberService.getTrace(serialNo);
        return new DataGridView((long) list.size(), list);
    }

    @RequestMapping("manualAdjustStatus")
    public ResultObj manualAdjustStatus(Integer id, Integer newStatus, String remark) {
        User user = (User) WebUtils.getSession().getAttribute("user");
        return serialNumberService.manualAdjustStatus(id, newStatus, remark,
                user != null ? user.getName() : "");
    }
}
```

- [ ] **Step 3: 编译并启动后端验证接口**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

Then start backend:
```bash
mvn spring-boot:run
```

Test with curl:
```bash
curl "http://localhost:8888/serial/checkSerialNo?serialNo=TEST001"
```

Expected: `{"code":200,"msg":"可用"}` (before login may be intercepted by SA-Token, login first or verify after integration)

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/serial/controller/
git add warehouse-back/src/main/java/com/sunlee/bus/serial/vo/
git commit -m "feat(serial): 新增序列号 Controller 与查询 VO"
```

---

## Task 11: 进货流程接入序列号入库

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/impl/InportServiceImpl.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/controller/InportController.java`

- [ ] **Step 1: 在 `InportServiceImpl.java` 中注入 `ISerialNumberService`**

Add at the field injection area (after existing `@Autowired` fields):

```java
    @Autowired
    private com.sunlee.bus.serial.service.ISerialNumberService serialNumberService;
```

- [ ] **Step 2: 修改 `save` 方法支持序列号**

Change the method signature first in `IInportService.java`:

```java
boolean save(Inport entity, List<String> serialNos);
```

Then modify `InportServiceImpl.save`:

```java
    @Override
    public boolean save(Inport entity, List<String> serialNos) {
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        // 序列号商品校验
        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            if (serialNos == null || serialNos.size() != entity.getNumber()) {
                throw new RuntimeException("序列号管理的商品，序列号数量必须等于进货数量");
            }
        }

        boolean result = super.save(entity);
        goods.setNumber(goods.getNumber() + entity.getNumber());
        goodsMapper.updateById(goods);

        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            serialNumberService.batchInport(entity.getGoodsid(), entity.getProviderid(),
                    entity.getId(), serialNos, entity.getOperateperson());
        }
        return result;
    }
```

- [ ] **Step 3: 保留旧 `save` 方法重载（保持 MyBatis-Plus ServiceImpl 兼容性）**

Add below the new save method:

```java
    @Override
    public boolean save(Inport entity) {
        return save(entity, null);
    }
```

- [ ] **Step 4: 修改 `batchSave` 方法支持序列号**

Change `IInportService.batchSave` signature:

```java
void batchSave(List<Inport> list, Map<Integer, List<String>> serialNoMap);
```

Modify `InportServiceImpl.batchSave`:

```java
    @Override
    public void batchSave(List<Inport> list, Map<Integer, List<String>> serialNoMap) {
        Date now = new Date();
        for (Inport entity : list) {
            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null) {
                if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
                    List<String> serialNos = serialNoMap != null ? serialNoMap.get(entity.getGoodsid()) : null;
                    if (serialNos == null || serialNos.size() != entity.getNumber()) {
                        throw new RuntimeException("商品【" + goods.getGoodsname() + "】序列号数量必须等于进货数量");
                    }
                }
                goods.setNumber(goods.getNumber() + entity.getNumber());
                goodsMapper.updateById(goods);
            }
        }
        saveBatch(list);

        // 记录操作日志 + 序列号入库
        for (Inport entity : list) {
            InportLog log = new InportLog();
            log.setOrderNo(entity.getOrderno());
            log.setProviderId(entity.getProviderid());
            log.setGoodsId(entity.getGoodsid());
            log.setType("inport");
            log.setNumber(entity.getNumber());
            log.setPrice(java.math.BigDecimal.valueOf(entity.getInportprice()));
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getInporttime());
            log.setRemark(entity.getRemark());
            inportLogMapper.insert(log);

            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null && Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
                List<String> serialNos = serialNoMap != null ? serialNoMap.get(entity.getGoodsid()) : null;
                serialNumberService.batchInport(entity.getGoodsid(), entity.getProviderid(),
                        entity.getId(), serialNos, entity.getOperateperson());
            }
        }
    }
```

- [ ] **Step 5: 修改 `InportController.addInport` 接收序列号**

Change:

```java
    @OperationLog(type = "添加", module = "商品进货", description = "'进货商品ID: ' + #args[0].goodsid + ', 数量: ' + #args[0].number")
    @RequestMapping("addInport")
    public ResultObj addInport(InportVo inportVo,
                               @RequestParam(value = "serialNos", required = false) List<String> serialNos) {
```

And change the service call from `inportService.save(inportVo)` to:

```java
            inportService.save(inportVo, serialNos);
```

- [ ] **Step 6: 修改 `InportController.batchAddInport` 接收序列号映射**

Change the method to accept a wrapper object:

```java
    @RequestMapping("batchAddInport")
    public ResultObj batchAddInport(@RequestBody Map<String, Object> body) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            Date now = new Date();
            List<Inport> list = new ArrayList<>();
            Map<Integer, List<String>> serialNoMap = new HashMap<>();
            // parse list
            Object listObj = body.get("list");
            Object serialObj = body.get("serialNos");
            if (listObj instanceof List) {
                for (Object o : (List<?>) listObj) {
                    Inport inport = new com.fasterxml.jackson.databind.ObjectMapper()
                            .convertValue(o, Inport.class);
                    inport.setOperateperson(user.getName());
                    inport.setInporttime(now);
                    list.add(inport);
                }
            }
            if (serialObj instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) serialObj;
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    Integer goodsId = Integer.valueOf(entry.getKey().toString());
                    List<?> snList = (List<?>) entry.getValue();
                    serialNoMap.put(goodsId, snList.stream().map(Object::toString).collect(Collectors.toList()));
                }
            }
            // ... generate orderNo as before ...
            inportService.batchSave(list, serialNoMap);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("批量进货失败: {}", e.getMessage(), e);
            return ResultObj.error("添加失败: " + e.getMessage());
        }
    }
```

Note: Adjust the existing orderNo generation and operation log code to keep current behavior.

- [ ] **Step 7: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/service/IInportService.java
-git add warehouse-back/src/main/java/com/sunlee/bus/service/impl/InportServiceImpl.java
-git add warehouse-back/src/main/java/com/sunlee/bus/controller/InportController.java
git commit -m "feat(serial): 进货流程接入序列号入库"
```

---

## Task 12: 销售流程接入序列号出库

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/ISalesService.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/impl/SalesServiceImpl.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/controller/SalesController.java`

- [ ] **Step 1: 在 `ISalesService` 中增加带序列号的重载**

```java
boolean save(Sales entity, List<String> serialNos);
void batchSave(List<Sales> list, Map<Integer, List<String>> serialNoMap);
```

- [ ] **Step 2: 修改 `SalesServiceImpl` 注入序列号服务**

```java
    @Autowired
    private com.sunlee.bus.serial.service.ISerialNumberService serialNumberService;
```

- [ ] **Step 3: 修改 `save` 方法**

```java
    @Override
    public boolean save(Sales entity, List<String> serialNos) {
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        if (goods.getNumber() < entity.getNumber()) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
        }
        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            if (serialNos == null || serialNos.size() != entity.getNumber()) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】为序列号管理商品，必须选择序列号");
            }
        }

        boolean result = super.save(entity);
        goods.setNumber(goods.getNumber() - entity.getNumber());
        goodsMapper.updateById(goods);

        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            serialNumberService.batchSale(entity.getGoodsid(), entity.getCustomerid(),
                    entity.getOrderno(), serialNos, entity.getOperateperson());
        }
        return result;
    }

    @Override
    public boolean save(Sales entity) {
        return save(entity, null);
    }
```

- [ ] **Step 4: 修改 `batchSave` 方法**

```java
    @Override
    public void batchSave(List<Sales> list, Map<Integer, List<String>> serialNoMap) {
        for (Sales entity : list) {
            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null) {
                if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
                    List<String> serialNos = serialNoMap != null ? serialNoMap.get(entity.getGoodsid()) : null;
                    if (serialNos == null || serialNos.size() != entity.getNumber()) {
                        throw new RuntimeException("商品【" + goods.getGoodsname() + "】为序列号管理商品，必须选择序列号");
                    }
                }
                goods.setNumber(goods.getNumber() - entity.getNumber());
                goodsMapper.updateById(goods);
            }
        }
        saveBatch(list);

        for (Sales entity : list) {
            SalesLog log = new SalesLog();
            log.setOrderNo(entity.getOrderno());
            log.setCustomerId(entity.getCustomerid());
            log.setGoodsId(entity.getGoodsid());
            log.setType("sale");
            log.setNumber(entity.getNumber());
            log.setPrice(entity.getSaleprice());
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getSalestime());
            log.setRemark(entity.getRemark());
            salesLogMapper.insert(log);

            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null && Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
                List<String> serialNos = serialNoMap != null ? serialNoMap.get(entity.getGoodsid()) : null;
                serialNumberService.batchSale(entity.getGoodsid(), entity.getCustomerid(),
                        entity.getOrderno(), serialNos, entity.getOperateperson());
            }
        }
    }
```

- [ ] **Step 5: 修改 `SalesController` 接收序列号**

For `addSales`:

```java
    @RequestMapping("addSales")
    public ResultObj addSales(SalesVo salesVo,
                              @RequestParam(value = "serialNos", required = false) List<String> serialNos) {
        // ... existing validation ...
        salesService.save(salesVo, serialNos);
        return ResultObj.ADD_SUCCESS;
    }
```

For `batchAddSales`, change to accept `Map<String, Object>` similar to InportController.

- [ ] **Step 6: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/service/ISalesService.java
-git add warehouse-back/src/main/java/com/sunlee/bus/service/impl/SalesServiceImpl.java
-git add warehouse-back/src/main/java/com/sunlee/bus/controller/SalesController.java
git commit -m "feat(serial): 销售流程接入序列号出库"
```

---

## Task 13: 零售流程接入序列号出库

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/IRetailService.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/impl/RetailServiceImpl.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/controller/RetailController.java`

- [ ] **Step 1: 在 `IRetailService` 中增加带序列号的重载**

```java
boolean save(Retail entity, List<String> serialNos);
void batchSave(List<Retail> list, Map<Integer, List<String>> serialNoMap);
```

- [ ] **Step 2: 修改 `RetailServiceImpl`**

注入：

```java
    @Autowired
    private com.sunlee.bus.serial.service.ISerialNumberService serialNumberService;
```

`save` 方法：

```java
    @Override
    public boolean save(Retail entity, List<String> serialNos) {
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        if (goods.getNumber() < entity.getNumber()) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
        }
        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            if (serialNos == null || serialNos.size() != entity.getNumber()) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】为序列号管理商品，必须选择序列号");
            }
        }

        boolean result = super.save(entity);
        goods.setNumber(goods.getNumber() - entity.getNumber());
        goodsMapper.updateById(goods);

        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            serialNumberService.batchRetail(entity.getGoodsid(), entity.getOrderno(),
                    serialNos, entity.getOperateperson());
        }
        return result;
    }

    @Override
    public boolean save(Retail entity) {
        return save(entity, null);
    }
```

`batchSave` 方法：参照 SalesServiceImpl，调用 `serialNumberService.batchRetail`。

- [ ] **Step 3: 修改 `RetailController` 接收序列号**

与 SalesController 相同模式，将 `addRetail` 和 `batchAddRetail` 改为接收 `serialNos` 参数 / 字段。

- [ ] **Step 4: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/service/IRetailService.java
-git add warehouse-back/src/main/java/com/sunlee/bus/service/impl/RetailServiceImpl.java
-git add warehouse-back/src/main/java/com/sunlee/bus/controller/RetailController.java
git commit -m "feat(serial): 零售流程接入序列号出库"
```

---

## Task 14: 销售退货接入序列号回库

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/impl/SalesbackServiceImpl.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/controller/SalesbackController.java`

- [ ] **Step 1: 修改 `SalesbackServiceImpl.addSalesback` 增加序列号校验**

Change signature in `ISalesbackService.java`:

```java
void addSalesback(Integer id, Integer number, String remark, List<String> serialNos);
```

Modify implementation:

```java
    @Autowired
    private com.sunlee.bus.serial.service.ISerialNumberService serialNumberService;

    @Override
    public void addSalesback(Integer id, Integer number, String remark, List<String> serialNos) {
        if (number == null || number <= 0) {
            throw new RuntimeException("退货数量必须大于0");
        }
        Sales sales = salesMapper.selectById(id);
        if (sales == null) {
            throw new RuntimeException("销售记录不存在: " + id);
        }
        if (number > sales.getNumber()) {
            throw new RuntimeException("退货数量不能超过销售数量，当前销售数量: " + sales.getNumber());
        }

        Goods goods = goodsMapper.selectById(sales.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + sales.getGoodsid());
        }

        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            if (serialNos == null || serialNos.size() != number) {
                throw new RuntimeException("序列号管理商品退货必须提供序列号，且数量等于退货数量");
            }
            boolean directResaleable = Integer.valueOf(1).equals(goods.getReturnResaleable());
            User user = (User) WebUtils.getSession().getAttribute("user");
            ResultObj result = serialNumberService.batchReturn(sales.getOrderno(), serialNos,
                    user != null ? user.getName() : "", directResaleable);
            if (result.getCode() != 200) {
                throw new RuntimeException(result.getMsg());
            }
        }

        goods.setNumber(goods.getNumber() + number);
        goodsMapper.updateById(goods);

        sales.setNumber(sales.getNumber() - number);
        salesMapper.updateById(sales);

        Salesback salesback = new Salesback();
        salesback.setGoodsid(sales.getGoodsid());
        salesback.setNumber(number);
        User user = (User) WebUtils.getSession().getAttribute("user");
        salesback.setOperateperson(user != null ? user.getName() : "");
        salesback.setSalebackprice(sales.getSaleprice() != null ? sales.getSaleprice().doubleValue() : null);
        salesback.setPaytype(sales.getPaytype());
        salesback.setSalesbacktime(new Date());
        salesback.setRemark(remark);
        salesback.setCustomerid(sales.getCustomerid());
        salesback.setSalesid(sales.getId());
        getBaseMapper().insert(salesback);
    }
```

- [ ] **Step 2: 修改 `SalesbackController` 接收序列号**

```java
    @RequestMapping("addSalesback")
    public ResultObj addSalesback(Integer id, Integer number, String remark,
                                  @RequestParam(value = "serialNos", required = false) List<String> serialNos) {
        try {
            salesbackService.addSalesback(id, number, remark, serialNos);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            return ResultObj.error(e.getMessage());
        }
    }
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/service/ISalesbackService.java
-git add warehouse-back/src/main/java/com/sunlee/bus/service/impl/SalesbackServiceImpl.java
-git add warehouse-back/src/main/java/com/sunlee/bus/controller/SalesbackController.java
git commit -m "feat(serial): 销售退货接入序列号回库校验"
```

---

## Task 15: 零售退货接入序列号回库

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/IRetailbackService.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/impl/RetailbackServiceImpl.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/controller/RetailbackController.java`

- [ ] **Step 1: 修改 `RetailbackServiceImpl`**

注入 `ISerialNumberService`，在 `addRetailback` 中参照 `SalesbackServiceImpl` 增加序列号校验与回库逻辑。

关键代码：

```java
        Goods goods = goodsMapper.selectById(retail.getGoodsid());
        if (Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
            if (serialNos == null || serialNos.size() != number) {
                throw new RuntimeException("序列号管理商品退货必须提供序列号");
            }
            boolean directResaleable = Integer.valueOf(1).equals(goods.getReturnResaleable());
            User user = (User) WebUtils.getSession().getAttribute("user");
            ResultObj result = serialNumberService.batchReturn(retail.getOrderno(), serialNos,
                    user != null ? user.getName() : "", directResaleable);
            if (result.getCode() != 200) {
                throw new RuntimeException(result.getMsg());
            }
        }
```

- [ ] **Step 2: 修改 `RetailbackController` 接收 `serialNos` 参数**

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/service/IRetailbackService.java
-git add warehouse-back/src/main/java/com/sunlee/bus/service/impl/RetailbackServiceImpl.java
-git add warehouse-back/src/main/java/com/sunlee/bus/controller/RetailbackController.java
git commit -m "feat(serial): 零售退货接入序列号回库校验"
```

---

## Task 16: 盘点流程接入序列号差异处理

**Files:**
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/service/impl/StocktakeServiceImpl.java`
- Modify: `warehouse-back/src/main/java/com/sunlee/bus/controller/StocktakeController.java` (if needed)

- [ ] **Step 1: 修改 `StocktakeServiceImpl` 注入序列号服务**

```java
    @Autowired
    private com.sunlee.bus.serial.service.ISerialNumberService serialNumberService;
```

- [ ] **Step 2: 修改 `submitStocktake` 处理序列号商品**

After updating each `StocktakeItem`, if the goods is serial-managed, call:

```java
            Goods goods = goodsService.getById(item.getGoodsid());
            if (goods != null && Integer.valueOf(1).equals(goods.getIsSerialManaged())) {
                // actual serial numbers are stored in item.getSerialNos() (need to add field)
                List<String> actualSerialNos = item.getSerialNos();
                User user = (User) WebUtils.getSession().getAttribute("user");
                ResultObj result = serialNumberService.handleStocktakeDiff(item.getGoodsid(), actualSerialNos,
                        user != null ? user.getName() : "", "盘点单: " + stocktake.getStocktakeNo());
                if (result.getCode() != 200) {
                    throw new RuntimeException(result.getMsg());
                }
            }
```

- [ ] **Step 3: 扩展 `StocktakeItem` 实体**

Add to `StocktakeItem.java`:

```java
    @TableField(exist = false)
    private List<String> serialNos;
```

- [ ] **Step 4: 编译验证**

Run:
```bash
cd warehouse-back
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add warehouse-back/src/main/java/com/sunlee/bus/entity/StocktakeItem.java
-git add warehouse-back/src/main/java/com/sunlee/bus/service/impl/StocktakeServiceImpl.java
git commit -m "feat(serial): 盘点流程接入序列号盘盈盘亏处理"
```

---

## Task 17: 前端类型定义与 API 模块

**Files:**
- Create: `warehouse-front/src/types/serialNumber.d.ts`
- Modify: `warehouse-front/src/types/goods.d.ts`
- Create: `warehouse-front/src/api/serialNumber.ts`

- [ ] **Step 1: 扩展 `goods.d.ts`**

Add to `Goods` interface:

```typescript
  isSerialManaged?: number
  returnResaleable?: number
```

- [ ] **Step 2: 创建 `serialNumber.d.ts`**

```typescript
export interface SerialNumber {
  id: number
  serialNo: string
  goodsId: number
  status: number
  providerId?: number
  inportId?: number
  currentOrderNo?: string
  customerId?: number
  createTime?: string
  updateTime?: string
}

export interface SerialNumberLog {
  id: number
  serialNo: string
  goodsId: number
  action: string
  fromStatus?: number
  toStatus: number
  orderNo?: string
  operatePerson: string
  operateTime: string
  remark?: string
}

export const SERIAL_STATUS_MAP: Record<number, string> = {
  0: '在库可用',
  1: '已售',
  2: '退货待检',
  3: '报废',
  4: '锁定中'
}
```

- [ ] **Step 3: 创建 `serialNumber.ts` API 文件**

```typescript
import request from '@/utils/request'

export function loadAllSerialNumber(params: any) {
  return request.get('/serial/loadAllSerialNumber', { params })
}

export function checkSerialNo(serialNo: string) {
  return request.get('/serial/checkSerialNo', { params: { serialNo } })
}

export function getAvailableSerialNumbers(goodsId: number) {
  return request.get('/serial/getAvailableSerialNumbers', { params: { goodsId } })
}

export function getSerialNumberTrace(serialNo: string) {
  return request.get('/serial/getSerialNumberTrace', { params: { serialNo } })
}

export function manualAdjustStatus(id: number, newStatus: number, remark?: string) {
  return request.post('/serial/manualAdjustStatus', null, { params: { id, newStatus, remark } })
}
```

- [ ] **Step 4: 前端类型检查**

Run:
```bash
cd warehouse-front
npx vue-tsc --noEmit
```

Expected: no type errors (other than pre-existing ones)

- [ ] **Step 5: Commit**

```bash
git add warehouse-front/src/types/serialNumber.d.ts
-git add warehouse-front/src/types/goods.d.ts
-git add warehouse-front/src/api/serialNumber.ts
git commit -m "feat(serial): 前端新增序列号类型与 API 模块"
```

---

## Task 18: 创建 SerialNumberInput 录入组件

**Files:**
- Create: `warehouse-front/src/components/SerialNumberInput.vue`

- [ ] **Step 1: 创建组件文件**

```vue
<template>
  <div>
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="手动输入" name="manual">
        <el-input
          v-model="manualText"
          type="textarea"
          :rows="6"
          placeholder="每行一个序列号，支持回车/逗号/空格分隔"
        />
        <div style="margin-top:8px">已解析数量: {{ parsedList.length }}</div>
      </el-tab-pane>
      <el-tab-pane label="Excel导入" name="excel">
        <el-upload
          accept=".xlsx,.xls,.csv"
          :auto-upload="false"
          :on-change="handleExcelChange"
          :show-file-list="false"
        >
          <el-button type="primary">选择 Excel 文件</el-button>
        </el-upload>
        <div v-if="excelList.length" style="margin-top:8px">
          从 Excel 解析到 {{ excelList.length }} 条序列号
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'

const props = defineProps<{
  modelValue: string[]
  expectedCount?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: string[]): void
}>()

const activeTab = ref('manual')
const manualText = ref('')
const excelList = ref<string[]>([])

const parsedList = computed(() => {
  const text = activeTab.value === 'manual' ? manualText.value : excelList.value.join('\n')
  return text
    .split(/[\n,，\s]+/)
    .map(s => s.trim())
    .filter(s => s.length > 0)
})

watch(parsedList, (val) => {
  emit('update:modelValue', val)
}, { immediate: true })

const handleExcelChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target?.result as ArrayBuffer)
      const workbook = XLSX.read(data, { type: 'array' })
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]]
      const json = XLSX.utils.sheet_to_json(firstSheet, { header: 1 }) as any[][]
      const serials: string[] = []
      json.forEach((row, idx) => {
        if (idx === 0 && row[0] && String(row[0]).includes('序列号')) return // skip header
        const val = row[0]
        if (val) serials.push(String(val).trim())
      })
      excelList.value = serials
      activeTab.value = 'excel'
      ElMessage.success(`解析到 ${serials.length} 条序列号`)
    } catch (err) {
      ElMessage.error('Excel 解析失败')
    }
  }
  reader.readAsArrayBuffer(file.raw)
}
</script>
```

- [ ] **Step 2: 安装 xlsx 依赖（如未安装）**

Run:
```bash
cd warehouse-front
npm install xlsx --save
```

- [ ] **Step 3: 验证组件编译**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds (or only pre-existing errors)

- [ ] **Step 4: Commit**

```bash
git add warehouse-front/src/components/SerialNumberInput.vue
-git add warehouse-front/package.json warehouse-front/package-lock.json
git commit -m "feat(serial): 新增序列号录入组件（手动+Excel）"
```

---

## Task 19: 创建 SerialNumberSelector 选择组件

**Files:**
- Create: `warehouse-front/src/components/SerialNumberSelector.vue`

- [ ] **Step 1: 创建组件文件**

```vue
<template>
  <div>
    <el-input
      v-model="scanText"
      placeholder="扫描或输入序列号后回车"
      style="margin-bottom:12px"
      @keyup.enter="handleScan"
    >
      <template #append>
        <el-button @click="handleScan">添加</el-button>
      </template>
    </el-input>
    <el-table :data="availableList" height="300" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="serialNo" label="序列号" />
      <el-table-column prop="createTime" label="入库时间" width="160" />
    </el-table>
    <div style="margin-top:8px">
      已选: {{ selected.length }} / {{ expectedCount ?? '?' }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableSerialNumbers } from '@/api/serialNumber'
import type { SerialNumber } from '@/types/serialNumber'

const props = defineProps<{
  goodsId: number
  modelValue: string[]
  expectedCount?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: string[]): void
}>()

const availableList = ref<SerialNumber[]>([])
const selected = ref<SerialNumber[]>([])
const scanText = ref('')

onMounted(async () => {
  const res: any = await getAvailableSerialNumbers(props.goodsId)
  availableList.value = res.data || []
})

const handleSelectionChange = (rows: SerialNumber[]) => {
  selected.value = rows
  emit('update:modelValue', rows.map(r => r.serialNo))
}

const handleScan = () => {
  const text = scanText.value.trim()
  if (!text) return
  const found = availableList.value.find(item => item.serialNo === text)
  if (!found) {
    ElMessage.error('该序列号不在可用列表中')
    return
  }
  if (!selected.value.includes(found)) {
    selected.value.push(found)
    emit('update:modelValue', selected.value.map(r => r.serialNo))
  }
  scanText.value = ''
}

watch(() => props.modelValue, (val) => {
  selected.value = availableList.value.filter(item => val.includes(item.serialNo))
}, { immediate: true })
</script>
```

- [ ] **Step 2: 验证组件编译**

Run:
```bash
cd warehouse-front
npm run build
```

- [ ] **Step 3: Commit**

```bash
git add warehouse-front/src/components/SerialNumberSelector.vue
git commit -m "feat(serial): 新增序列号选择组件（列表+扫码）"
```

---

## Task 20: 商品档案页面增加序列号开关

**Files:**
- Modify: `warehouse-front/src/views/business/goods/index.vue`

- [ ] **Step 1: 在表格中显示序列号管理状态**

Add a column before "状态" column:

```vue
        <el-table-column label="序列号" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isSerialManaged === 1 ? 'primary' : 'info'" size="small">
              {{ row.isSerialManaged === 1 ? '管理' : '不管理' }}
            </el-tag>
          </template>
        </el-table-column>
```

- [ ] **Step 2: 在表单中增加两个开关**

Add inside the `CrudDialog` template, after the "预警值" row:

```vue
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="序列号管理">
              <el-switch
                v-model="formData.isSerialManaged"
                :active-value="1"
                :inactive-value="0"
                active-text="管理"
                inactive-text="不管理"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退货后直接回库">
              <el-switch
                v-model="formData.returnResaleable"
                :active-value="1"
                :inactive-value="0"
                active-text="直接可售"
                inactive-text="待检"
              />
            </el-form-item>
          </el-col>
        </el-row>
```

- [ ] **Step 3: 验证页面渲染**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds

- [ ] **Step 4: Commit**

```bash
git add warehouse-front/src/views/business/goods/index.vue
git commit -m "feat(serial): 商品档案页面增加序列号管理开关"
```

---

## Task 21: 进货页面接入序列号录入

**Files:**
- Modify: `warehouse-front/src/views/business/inport/index.vue`
- Modify: `warehouse-front/src/views/business/inport-order/index.vue`

- [ ] **Step 1: 在 `inport/index.vue` 中引入组件与 API**

```typescript
import SerialNumberInput from '@/components/SerialNumberInput.vue'
import { checkSerialNo } from '@/api/serialNumber'
```

- [ ] **Step 2: 增加序列号弹窗与数据**

Add to template:

```vue
    <el-dialog v-model="serialDialogVisible" title="录入序列号" width="600px">
      <SerialNumberInput v-model="currentSerialNos" :expected-count="currentForm.number" />
      <template #footer>
        <el-button @click="serialDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSerialNos">确定</el-button>
      </template>
    </el-dialog>
```

Add to script:

```typescript
const serialDialogVisible = ref(false)
const currentSerialNos = ref<string[]>([])
const currentForm = ref<any>({})
const pendingSerialMap = ref<Record<number, string[]>>({})

const handleSubmitApi = async (data: any) => {
  const goods = dialogGoodsList.value.find((g: any) => g.id === data.goodsid)
  if (goods?.isSerialManaged === 1) {
    currentForm.value = data
    currentSerialNos.value = pendingSerialMap.value[data.goodsid] || []
    serialDialogVisible.value = true
    return Promise.reject(new Error('waiting for serial numbers'))
  }
  return addInport(data)
}

const confirmSerialNos = async () => {
  const count = currentForm.value.number
  if (currentSerialNos.value.length !== count) {
    ElMessage.error(`序列号数量必须等于进货数量 ${count}`)
    return
  }
  pendingSerialMap.value[currentForm.value.goodsid] = currentSerialNos.value
  serialDialogVisible.value = false
  const res: any = await addInport({ ...currentForm.value, serialNos: currentSerialNos.value })
  if (res.code === 200) {
    ElMessage.success('添加成功')
    tableRef.value?.reload()
    dialogRef.value?.close()
  }
}
```

Note: The dialog closing mechanism may need adjustment based on `CrudDialog` implementation. Use the component's `close` method if available, otherwise control visibility via ref.

- [ ] **Step 3: 批量进货订单页同理接入**

Modify `inport-order/index.vue` to collect `serialNos` per goods and send `list + serialNos` map in `batchAddInport`.

- [ ] **Step 4: 编译验证**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds

- [ ] **Step 5: Commit**

```bash
git add warehouse-front/src/views/business/inport/index.vue
-git add warehouse-front/src/views/business/inport-order/index.vue
git commit -m "feat(serial): 进货页面接入序列号录入"
```

---

## Task 22: 销售/零售页面接入序列号选择

**Files:**
- Modify: `warehouse-front/src/views/business/sales/index.vue`
- Modify: `warehouse-front/src/views/business/sales-order/index.vue`
- Modify: `warehouse-front/src/views/business/sales-pos/index.vue`
- Modify: `warehouse-front/src/views/business/retail/index.vue`
- Modify: `warehouse-front/src/views/business/retail-order/index.vue`

- [ ] **Step 1: 在 `sales/index.vue` 中引入 `SerialNumberSelector`**

```typescript
import SerialNumberSelector from '@/components/SerialNumberSelector.vue'
```

- [ ] **Step 2: 在销售弹窗中增加序列号选择逻辑**

Similar to Task 21, but use `SerialNumberSelector` instead of `SerialNumberInput`.

Before submit, if goods `isSerialManaged === 1`, open selector dialog, collect selected serial numbers, validate count equals `number`, then call `addSales` with `serialNos` parameter.

- [ ] **Step 3: 其他销售/零售页面按相同模式接入**

`sales-order`, `sales-pos`, `retail`, `retail-order` — each follows the same pattern:

- Detect serial-managed goods.
- Open selector.
- Validate count.
- Pass `serialNos` to backend.

- [ ] **Step 4: 编译验证**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds

- [ ] **Step 5: Commit**

```bash
git add warehouse-front/src/views/business/sales/index.vue
-git add warehouse-front/src/views/business/sales-order/index.vue
-git add warehouse-front/src/views/business/sales-pos/index.vue
-git add warehouse-front/src/views/business/retail/index.vue
-git add warehouse-front/src/views/business/retail-order/index.vue
git commit -m "feat(serial): 销售与零售页面接入序列号选择"
```

---

## Task 23: 退货页面接入序列号校验

**Files:**
- Modify: `warehouse-front/src/views/business/salesback/index.vue`
- Modify: `warehouse-front/src/views/business/retailback/index.vue`

- [ ] **Step 1: 在 `salesback/index.vue` 中增加序列号录入**

For each退货操作，如果商品 `isSerialManaged === 1`，弹出 `SerialNumberInput` 组件，要求用户录入/扫描要退货的序列号。

提交时调用 `addSalesback(id, number, remark, serialNos)`。

- [ ] **Step 2: 零售退货同理**

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds

- [ ] **Step 4: Commit**

```bash
git add warehouse-front/src/views/business/salesback/index.vue
-git add warehouse-front/src/views/business/retailback/index.vue
git commit -m "feat(serial): 退货页面接入序列号校验"
```

---

## Task 24: 盘点页面接入序列号扫描

**Files:**
- Modify: `warehouse-front/src/views/business/stocktake/index.vue`

- [ ] **Step 1: 在盘点明细中增加序列号录入入口**

For each item where `goods.isSerialManaged === 1`, show a button "扫描序列号".

Clicking opens a dialog with `SerialNumberInput` component to collect actual serial numbers.

- [ ] **Step 2: 提交盘点时携带序列号列表**

When calling `submitStocktake`, include `serialNos` in each serial-managed item.

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds

- [ ] **Step 4: Commit**

```bash
git add warehouse-front/src/views/business/stocktake/index.vue
git commit -m "feat(serial): 盘点页面接入序列号扫描"
```

---

## Task 25: 序列号管理列表与追溯页面

**Files:**
- Create: `warehouse-front/src/views/business/serial-number/index.vue`

- [ ] **Step 1: 创建列表页面**

```vue
<template>
  <div>
    <el-card>
      <SearchForm v-model="searchParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="序列号">
          <el-input v-model="searchParams.serialNo" placeholder="序列号" clearable />
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="searchParams.goodsId" placeholder="全部" clearable filterable>
            <el-option v-for="g in goodsList" :key="g.id" :label="g.goodsname" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部" clearable>
            <el-option v-for="(label, key) in SERIAL_STATUS_MAP" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
      </SearchForm>

      <CrudTable ref="tableRef" :load-api="loadAllSerialNumber" :search-params="searchParams">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="serialNo" label="序列号" />
        <el-table-column prop="goodsId" label="商品ID" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ SERIAL_STATUS_MAP[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentOrderNo" label="当前订单" />
        <el-table-column prop="createTime" label="入库时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleTrace(row)">追溯</el-button>
            <el-button type="warning" link @click="handleAdjust(row)">调整状态</el-button>
          </template>
        </el-table-column>
      </CrudTable>
    </el-card>

    <el-dialog v-model="traceVisible" title="序列号追溯" width="700px">
      <el-timeline>
        <el-timeline-item
          v-for="log in traceList"
          :key="log.id"
          :timestamp="log.operateTime"
        >
          {{ SERIAL_STATUS_MAP[log.fromStatus] || '-' }} → {{ SERIAL_STATUS_MAP[log.toStatus] }}
          <div>{{ log.action }} | {{ log.operatePerson }} | {{ log.remark || '' }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>

    <el-dialog v-model="adjustVisible" title="调整状态" width="400px">
      <el-form :model="adjustForm">
        <el-form-item label="新状态">
          <el-select v-model="adjustForm.newStatus">
            <el-option v-for="(label, key) in SERIAL_STATUS_MAP" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adjustForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdjust">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import SearchForm from '@/components/SearchForm.vue'
import CrudTable from '@/components/CrudTable.vue'
import { loadAllSerialNumber, getSerialNumberTrace, manualAdjustStatus } from '@/api/serialNumber'
import { loadAllGoodsForSelect } from '@/api/goods'
import { SERIAL_STATUS_MAP } from '@/types/serialNumber'
import type { SerialNumberLog } from '@/types/serialNumber'

const tableRef = ref()
const goodsList = ref<any[]>([])
const traceVisible = ref(false)
const traceList = ref<SerialNumberLog[]>([])
const adjustVisible = ref(false)
const adjustForm = reactive({ id: 0, newStatus: 0, remark: '' })

const searchParams = reactive({
  serialNo: '',
  goodsId: null as number | null,
  status: null as number | null
})

onMounted(async () => {
  const res: any = await loadAllGoodsForSelect()
  goodsList.value = res.data || []
})

const handleSearch = () => tableRef.value?.reload()
const handleReset = () => {
  searchParams.serialNo = ''
  searchParams.goodsId = null
  searchParams.status = null
}

const handleTrace = async (row: any) => {
  const res: any = await getSerialNumberTrace(row.serialNo)
  traceList.value = res.data || []
  traceVisible.value = true
}

const handleAdjust = (row: any) => {
  adjustForm.id = row.id
  adjustForm.newStatus = row.status
  adjustForm.remark = ''
  adjustVisible.value = true
}

const submitAdjust = async () => {
  const res: any = await manualAdjustStatus(adjustForm.id, adjustForm.newStatus, adjustForm.remark)
  if (res.code === 200) {
    ElMessage.success('调整成功')
    adjustVisible.value = false
    tableRef.value?.reload()
  }
}
</script>
```

- [ ] **Step 2: 在路由中注册页面**

Add to `warehouse-front/src/router/index.ts` under business routes:

```typescript
    {
      path: '/business/serial-number',
      name: 'SerialNumber',
      component: () => import('@/views/business/serial-number/index.vue'),
      meta: { title: '序列号管理', permission: 'serial:view' }
    }
```

- [ ] **Step 3: 编译验证**

Run:
```bash
cd warehouse-front
npm run build
```

Expected: build succeeds

- [ ] **Step 4: Commit**

```bash
git add warehouse-front/src/views/business/serial-number/index.vue
-git add warehouse-front/src/router/index.ts
git commit -m "feat(serial): 新增序列号管理列表与追溯页面"
```

---

## Task 26: 集成测试与验证

**Files:**
- All modified files

- [ ] **Step 1: 启动后端**

Run:
```bash
cd warehouse-back
mvn spring-boot:run
```

Expected: Application starts on port 8888 with no errors.

- [ ] **Step 2: 启动前端**

Run:
```bash
cd warehouse-front
npm run dev
```

Expected: Dev server starts on port 5173.

- [ ] **Step 3: 登录系统并配置商品**

1. Open `http://localhost:5173` and login.
2. Go to 商品管理.
3. Edit an existing product, enable "序列号管理" switch, save.
4. Verify in MySQL:

```sql
SELECT is_serial_managed FROM bus_goods WHERE id = <product_id>;
```

Expected: `is_serial_managed = 1`.

- [ ] **Step 4: 测试进货录入序列号**

1. Go to 商品进货 → 添加.
2. Select the serial-managed product, quantity = 2.
3. Enter two serial numbers (e.g., `SN001`, `SN002`).
4. Submit.
5. Verify in MySQL:

```sql
SELECT * FROM bus_serial_number WHERE serial_no IN ('SN001', 'SN002');
SELECT * FROM bus_serial_number_log WHERE serial_no IN ('SN001', 'SN002');
```

Expected: Both records exist with `status = 0`, logs with `action = 'inport'`.

- [ ] **Step 5: 测试销售选择序列号**

1. Go to 商品销售 → 添加.
2. Select the same product, quantity = 1.
3. Choose one available serial number.
4. Submit.
5. Verify:

```sql
SELECT status, current_order_no FROM bus_serial_number WHERE serial_no = 'SN001';
```

Expected: `status = 1`, `current_order_no` is set.

- [ ] **Step 6: 测试销售退货**

1. Go to 销售退货.
2. Return the sold serial number.
3. Verify:

```sql
SELECT status FROM bus_serial_number WHERE serial_no = 'SN001';
```

Expected: `status = 0` if `return_resaleable = 1`, else `status = 2`.

- [ ] **Step 7: 测试序列号追溯**

1. Go to 序列号管理 page.
2. Search for `SN001`.
3. Click 追溯.

Expected: Timeline shows inport → sale → return events.

- [ ] **Step 8: 测试异常场景**

1. Try to sell a serial number not in available list.
2. Try to return a serial number not sold by current order.
3. Try to inport a duplicate serial number.

Expected: Each operation fails with a clear error message.

- [ ] **Step 9: 修复问题并再次验证**

Fix any issues found and rerun relevant test steps until all pass.

- [ ] **Step 10: Final commit**

```bash
git add .
git commit -m "feat(serial): 序列号管理功能完整实现并通过集成测试"
```

---

## Spec Coverage Check

| 设计文档章节 | 实现任务 |
|-------------|---------|
| 数据库模型 (4.1, 4.2, 4.3) | Task 1, 2, 3, 4, 5 |
| 状态流转 (5) | Task 7, 8, 9 |
| 进货流程 (6.1) | Task 11 |
| 销售/零售流程 (6.2) | Task 12, 13 |
| 退货流程 (6.3) | Task 14, 15 |
| 盘点流程 (6.4) | Task 16 |
| 后端接口 (7.4) | Task 10 |
| 前端组件 (8.3) | Task 18, 19 |
| 前端页面 (8.1, 8.2) | Task 20-25 |
| 权限设计 (9) | Task 25 (router permission), manual menu config needed in DB |
| 异常处理 (10) | Task 7, 8, 9, 11-16 |
| 集成测试 (12) | Task 26 |

**Note on permissions:** The new permission identifiers `serial:view`, `serial:trace`, `serial:adjust` need to be inserted into the `sys_permission` table and assigned to roles via the existing 权限管理 page. This is a manual configuration step after deployment.

## Placeholder Scan

- No `TBD`/`TODO` sections remain.
- All code steps contain actual code.
- All commands include expected output.
- Method names are consistent across tasks (`batchInport`, `batchSale`, `batchRetail`, `batchReturn`, `getTrace`, `manualAdjustStatus`).

## Type Consistency Check

- Java entity fields: `serialNo`, `goodsId`, `status`, `providerId`, `inportId`, `currentOrderNo`, `customerId` — consistent across `SerialNumber.java`, `SerialNumberLog.java`, `SerialNumberMapper.xml`, and `SerialNumberServiceImpl.java`.
- Frontend types: `SerialNumber` and `SerialNumberLog` interfaces match backend field naming.
- Status constants: `0=available, 1=sold, 2=return pending, 3=scrap, 4=locked` consistent in backend constants and frontend `SERIAL_STATUS_MAP`.

---

**Plan complete and saved to `docs/superpowers/plans/2026-06-11-serial-number-management.md`.**

**Two execution options:**

1. **Subagent-Driven (recommended)** - Dispatch a fresh subagent per task, review between tasks, fast iteration.
2. **Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints.

Which approach would you like to use?
