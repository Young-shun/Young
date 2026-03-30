package com.sky.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BaseException {

    /**
     * 作用: 执行AccountLockedException相关逻辑。
     * 输入: 无。
     * 输出: public。
     */
    public AccountLockedException() {
    }

    /**
     * 作用: 执行AccountLockedException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public AccountLockedException(String msg) {
        super(msg);
    }

}
