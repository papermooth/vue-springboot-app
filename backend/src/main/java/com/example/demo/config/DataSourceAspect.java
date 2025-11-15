package com.example.demo.config;

import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    /**
     * 写操作切点：save、update、delete、insert等方法
     */
    @Pointcut("execution(* com.example.demo.repository.*.save*(..)) || " +
              "execution(* com.example.demo.repository.*.update*(..)) || " +
              "execution(* com.example.demo.repository.*.delete*(..)) || " +
              "execution(* com.example.demo.repository.*.insert*(..)) || " +
              "execution(* com.example.demo.repository.*.create*(..)) || " +
              "execution(* com.example.demo.repository.*.remove*(..))")
    public void writePointCut() {}

    /**
     * 读操作切点：get、find、list、query、count等方法
     */
    @Pointcut("execution(* com.example.demo.repository.*.get*(..)) || " +
              "execution(* com.example.demo.repository.*.find*(..)) || " +
              "execution(* com.example.demo.repository.*.list*(..)) || " +
              "execution(* com.example.demo.repository.*.query*(..)) || " +
              "execution(* com.example.demo.repository.*.count*(..)) || " +
              "execution(* com.example.demo.repository.*.search*(..)) || " +
              "execution(* com.example.demo.repository.*.select*(..))")
    public void readPointCut() {}

    /**
     * 对写操作设置主数据源
     */
    @Before("writePointCut()")
    public void setWriteDataSource() {
        DataSourceContextHolder.setDataSourceKey(DataSourceType.MASTER.name());
        DataSourceContextHolder.setWriteOperation(true);
        logger.debug("设置数据源为: 主库");
    }

    /**
     * 对读操作设置数据源
     * 实际路由由DataSourceConfig中的determineCurrentLookupKey决定
     */
    @Before("readPointCut()")
    public void setReadDataSource() {
        DataSourceContextHolder.setWriteOperation(false);
        // 不直接设置数据源，让路由策略决定最终使用哪个数据源
        logger.debug("准备执行读操作，将根据路由策略选择数据源");
    }

    /**
     * 方法执行完成后清理线程本地变量
     */
    @After("writePointCut() || readPointCut()")
    public void clearDataSource() {
        DataSourceContextHolder.clearAll();
        logger.debug("清理数据源上下文");
    }
}