package com.xqxls.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 胡卓
 * @create 2023-05-08 16:11
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BatchInTable2 {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 插入内容
     */
    private String str;
}
