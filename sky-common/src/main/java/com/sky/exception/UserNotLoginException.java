package com.sky.exception;

public class UserNotLoginException extends BaseException {

    /**
     * 作用: 执行UserNotLoginException相关逻辑。
     * 输入: 无。
     * 输出: public。
     */
    public UserNotLoginException() {
    }

    /**
     * 作用: 执行UserNotLoginException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public UserNotLoginException(String msg) {
        super(msg);
    }

}
