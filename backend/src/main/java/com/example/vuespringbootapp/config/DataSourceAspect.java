package com.example.vuespringbootapp.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切面，用于自动路由读写操作到相应的数据源
 */
@Aspect
@Component
public class DataSourceAspect {

    /**
     * 定义切点，拦截所有的Service层方法
     */
    @Pointcut("execution(* com.example.vuespringbootapp.service.*.*(..))")
    public void servicePointCut() {}

    /**
     * 环绕通知，根据方法名判断是读操作还是写操作
     */
    @Around("servicePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName().toLowerCase();

        try {
            // 根据方法名判断操作类型
            if (isWriteOperation(methodName)) {
                // 写操作使用主数据源
                DataSourceConfig.DataSourceContextHolder.setDataSource("master");
            } else {
                // 读操作使用从数据源
                DataSourceConfig.DataSourceContextHolder.setDataSource("slave");
            }
            
            // 执行原方法
            return point.proceed();
        } finally {
            // 清理数据源上下文
            DataSourceConfig.DataSourceContextHolder.clearDataSource();
        }
    }

    /**
     * 判断是否为写操作
     */
    private boolean isWriteOperation(String methodName) {
        // 常见的写操作方法名关键词
        String[] writeKeywords = {"insert", "save", "update", "delete", "remove", "create", "add", "modify", "upsert"};
        
        for (String keyword : writeKeywords) {
            if (methodName.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
}