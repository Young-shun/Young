package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 作用: 执行setCurrentId相关逻辑。
     * 输入: Long id。
     * 输出: 无。
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 作用: 执行getCurrentId相关逻辑。
     * 输入: 无。
     * 输出: Long。
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 作用: 执行removeCurrentId相关逻辑。
     * 输入: 无。
     * 输出: 无。
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
