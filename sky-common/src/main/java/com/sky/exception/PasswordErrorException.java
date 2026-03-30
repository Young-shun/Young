package com.sky.exception;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BaseException {

    /**
     * 作用: 执行PasswordErrorException相关逻辑。
     * 输入: 无。
     * 输出: public。
     */
    public PasswordErrorException() {
    }

    /**
     * 作用: 执行PasswordErrorException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public PasswordErrorException(String msg) {
        super(msg);
    }

}
