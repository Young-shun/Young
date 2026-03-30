package com.sky.exception;

/**
 * 套餐启用失败异常
 */
public class SetmealEnableFailedException extends BaseException {

    public SetmealEnableFailedException(){}

    /**
     * 作用: 执行SetmealEnableFailedException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public SetmealEnableFailedException(String msg){
        super(msg);
    }
}
