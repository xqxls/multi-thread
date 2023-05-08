package com.xqxls.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * MyBatis配置类
 * @Author: huzhuo
 * @Date: Created in 2023/4/25 22:10
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.xqxls.dao"})
public class MyBatisConfig {


}
