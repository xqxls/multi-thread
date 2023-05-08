package com.xqxls;

import com.xqxls.config.ThreadPoolConfig;
import com.xqxls.dao.BatchInTableDao;
import com.xqxls.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
@Slf4j
class MultiThreadApplicationTests {

    @Resource
    private BatchInTableDao batchInTableDao;

    @Resource
    private ThreadPoolConfig threadPool;

    @Resource
    private PlatformTransactionManager transactionManager;


    @Test
    void contextLoads() {
    }

    @Test
    public void createDataFile() throws IOException {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 1000000; i ++) {
            list.add("测试" + i);
        }
        JacksonUtil.objectToFile(list, "testBatch/test.json");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBatchIn2() throws IOException {
        // 读取造的数据
        List<String> list = JacksonUtil.fileToObject("testBatch/test.json", List.class);
        long allStart = System.currentTimeMillis();
        // 事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            long start = System.currentTimeMillis();

            List<List<String>> listList = ListUtils.partition(list, 2000);
            listList.forEach(item->{
                batchInTableDao.batchIn2(item);
            });
            System.out.println("插入耗时：" + (System.currentTimeMillis() - start));
            transactionManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            transactionManager.rollback(status);
        }
        System.out.println("总耗时：" + (System.currentTimeMillis() - allStart));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBatchIn() throws IOException, InterruptedException {
        List<String> list = JacksonUtil.fileToObject("testBatch/test.json", List.class);
        // 数据总量
        int dataNum = list.size();
        long allStart = System.currentTimeMillis();
        // 默认每次插入数据量
        int eachInsertSize = 2000;
        // 线程池用于插入的最大线程数
        int maxInsertPoolSize = threadPool.getMaxInsertPoolSize();
        if (dataNum > eachInsertSize * maxInsertPoolSize)  {
            eachInsertSize = dataNum / maxInsertPoolSize;
        }
        // 数据拆分
        List<List<String>> listList = ListUtils.partition(list, eachInsertSize);
        // 分配线作业线程数
        int threadNum = listList.size();

        // 回滚标志，线程安全
        AtomicBoolean rollbackFlag = new AtomicBoolean(false);
        // 事务计数阀，wait用于子线程内。计数阀清零表示所有事务业务已经结束，主线程可以继续向下走，子线程可以开始判断是否回滚
        CountDownLatch transactionLatch = new CountDownLatch(threadNum);

        listList.forEach(item -> executeInsert(item, rollbackFlag, transactionLatch));
        transactionLatch.await();

        if (rollbackFlag.get()) {
            System.out.println("回滚");
        }
        System.out.println("总耗时：" + (System.currentTimeMillis() - allStart));
        System.out.println("end");
    }

    private void executeInsert(List<String> list, AtomicBoolean rollbackFlag,
                               CountDownLatch transactionLatch) {
        threadPool.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "线程启动");

                // 后进线程获取到回滚标志，直接返回
                if (rollbackFlag.get())
                    return;

                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
                TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态

                try {
                    List<List<String>> listList = ListUtils.partition(list, 2000);
                    listList.forEach(item -> batchInTableDao.batchIn(item));
                    // 事务结束，计数阀计数减1
                    transactionLatch.countDown();

                    System.out.println(Thread.currentThread().getName() + "线程等待");
                    // 等待其他事务
                    transactionLatch.await();

                    // 判断回滚
                    if (rollbackFlag.get()) {
                        transactionManager.rollback(status);
                    } else {
                        transactionManager.commit(status);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    rollbackFlag.set(true);
                    // 事务结束，计数阀计数减1
                    transactionLatch.countDown();
                    transactionManager.rollback(status);
                }
                System.out.println(Thread.currentThread().getName() + "线程结束");
            }
        });
    }



}
