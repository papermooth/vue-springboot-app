package com.example.demo.config;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = ThreadLocal.withInitial(() -> DataSourceType.MASTER.name());
    private static final ThreadLocal<Boolean> WRITE_OPERATION_HOLDER = ThreadLocal.withInitial(() -> false);

    public static void setDataSourceKey(String dataSourceKey) {
        CONTEXT_HOLDER.set(dataSourceKey);
    }

    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }

    public static void setWriteOperation(boolean writeOperation) {
        WRITE_OPERATION_HOLDER.set(writeOperation);
    }

    public static boolean isWriteOperation() {
        return WRITE_OPERATION_HOLDER.get();
    }

    public static void clearWriteOperation() {
        WRITE_OPERATION_HOLDER.remove();
    }

    public static void clearAll() {
        CONTEXT_HOLDER.remove();
        WRITE_OPERATION_HOLDER.remove();
    }
}