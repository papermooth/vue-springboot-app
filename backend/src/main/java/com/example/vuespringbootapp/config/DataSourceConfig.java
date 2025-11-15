package com.example.vuespringbootapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * MySQL主从读写分离配置类
 */
@Configuration
public class DataSourceConfig {

    /**
     * 数据源上下文，用于存储当前线程使用的数据源类型
     */
    public static class DataSourceContextHolder {
        private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

        public static void setDataSource(String dataSource) {
            CONTEXT_HOLDER.set(dataSource);
        }

        public static String getDataSource() {
            return CONTEXT_HOLDER.get();
        }

        public static void clearDataSource() {
            CONTEXT_HOLDER.remove();
        }
    }

    /**
     * 自定义路由数据源，根据上下文选择不同的数据源
     */
    public static class RoutingDataSource extends AbstractRoutingDataSource {
        private final Random random = new Random();

        @Override
        protected Object determineCurrentLookupKey() {
            String dataSource = DataSourceContextHolder.getDataSource();
            if (dataSource == null || "master".equals(dataSource)) {
                return "master";
            } else if ("slave".equals(dataSource)) {
                // 随机选择一个从节点，实现简单的负载均衡
                return random.nextBoolean() ? "slave1" : "slave2";
            }
            return "master";
        }
    }

    /**
     * 配置主数据源
     */
    @Bean("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置从数据源1
     */
    @Bean("slave1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave1")
    public DataSource slave1DataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置从数据源2
     */
    @Bean("slave2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave2")
    public DataSource slave2DataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置动态数据源，设置为主数据源
     */
    @Primary
    @Bean("dataSource")
    public DataSource dynamicDataSource(DataSource masterDataSource, DataSource slave1DataSource, DataSource slave2DataSource) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slave1", slave1DataSource);
        targetDataSources.put("slave2", slave2DataSource);
        
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        
        return routingDataSource;
    }
}