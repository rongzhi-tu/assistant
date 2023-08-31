package com.bsoft.assistant.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: cl2
 * @time: 2023/5/30 9:58
 */
@Data
public class Page implements Serializable {
    private static final long serialVersionUID = 1L;
    //当前页
    private int pageNum;
    //每页的数量
    private int pageSize;
}