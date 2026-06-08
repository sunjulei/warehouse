package com.sunlee.bus.vo;

import com.sunlee.bus.entity.Retail;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class RetailVo extends Retail {

    private Integer page = 1;

    private Integer limit = 10;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String orderNo;

    private String recordType;

}
