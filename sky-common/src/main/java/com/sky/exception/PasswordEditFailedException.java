package com.sky.exception;

/**
 * 密码修改失败异常
 */
public class PasswordEditFailedException extends BaseException{

    /**
     * 作用: 执行PasswordEditFailedException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public PasswordEditFailedException(String msg){
        super(msg);
    }

}
