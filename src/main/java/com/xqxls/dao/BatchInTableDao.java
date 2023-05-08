package com.xqxls.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 胡卓
 * @create 2023-05-08 15:46
 * @Description
 */
@Mapper
public interface BatchInTableDao {

    void batchIn(@Param("list") List<String> list);

    void batchIn2(@Param("list") List<String> list);

}
