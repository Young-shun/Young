package com.sky.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {

    /**
     * 作用: 执行BaseException相关逻辑。
     * 输入: 无。
     * 输出: public。
     */
    public BaseException() {
    }

    /**
     * 作用: 执行BaseException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public BaseException(String msg) {
        super(msg);
    }

}
