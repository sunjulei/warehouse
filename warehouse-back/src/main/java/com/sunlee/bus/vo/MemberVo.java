package com.sunlee.bus.vo;

import com.sunlee.bus.entity.Member;
import lombok.Data;

@Data
public class MemberVo extends Member {
    private Integer page = 1;
    private Integer limit = 10;
}
