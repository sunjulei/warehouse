package com.sunlee.bus.vo;

import com.sunlee.bus.entity.OperationLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OperationLogVo extends OperationLog {

    private Integer page = 1;

    private Integer limit = 10;
}
