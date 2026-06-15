package com.sunlee.bus.vo;

import com.sunlee.bus.entity.SerialNumber;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 序列号查询值对象
 *
 * @author sunlee
 * @since 2026-06-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SerialNumberVo extends SerialNumber {

    private Integer page = 1;

    private Integer limit = 10;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
