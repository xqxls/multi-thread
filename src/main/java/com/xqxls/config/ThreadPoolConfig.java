package com.xqxls.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 胡卓
 * @create 2023-05-08 14:58
 * @Description
 */
@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {
    private static final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // 定义用于批量插入的线程数
    private static final int MAX_INSERT_POOL_SIZE = 8;

    @PostConstruct
    public void initialize() {
        log.info("Start ThreadPool");
        //配置核心线程数
        executor.setCorePoolSize(8);
        //配置最大线程数
        executor.setMaxPoolSize(16);
        //配置队列大小
        executor.setQueueCapacity(99999);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-");
        /*
           rejection-policy：当pool已经达到max size的时候，如何处理新任务
           CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化执行器
        executor.initialize();
    }

    // 获取线程执行器
    public ThreadPoolTaskExecutor getExecutor() {
        return executor;
    }

    // 获取最大线程数
    public int getMaxPoolSize() {
        return executor.getMaxPoolSize();
    }

    // 获取用于批量插入线程数
    public int getMaxInsertPoolSize() {
        return MAX_INSERT_POOL_SIZE;
    }
}
