package com.sky.exception;

/**
 * 账号不存在异常
 */
public class AccountNotFoundException extends BaseException {

    /**
     * 作用: 执行AccountNotFoundException相关逻辑。
     * 输入: 无。
     * 输出: public。
     */
    public AccountNotFoundException() {
    }

    /**
     * 作用: 执行AccountNotFoundException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
