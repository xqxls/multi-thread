package com.xqxls.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 胡卓
 * @create 2023-05-08 16:07
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BatchInTable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 插入内容
     */
    private String str;

}
