package com.sky.exception;

/**
 * 登录失败
 */
public class LoginFailedException extends BaseException{
    /**
     * 作用: 执行LoginFailedException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public LoginFailedException(String msg){
        super(msg);
    }
}
