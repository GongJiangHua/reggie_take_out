package com.jigong.reggie.dto;

import lombok.Data;

@Data
public class QueryDto {
    //有关后台订单管理的分页的入参的实体
    private int page;

    private int pageSize;

    private String number;

    private String beginTime;

    private String endTime;
}
