package com.sky.exception;

public class DeletionNotAllowedException extends BaseException {

    /**
     * 作用: 执行DeletionNotAllowedException相关逻辑。
     * 输入: String msg。
     * 输出: public。
     */
    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

}
