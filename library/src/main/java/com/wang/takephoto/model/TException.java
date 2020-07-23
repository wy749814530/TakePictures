package com.wang.takephoto.model;

/**
 * Author: JPH
 * Date: 2016/7/26 10:53
 */
public class TException extends Exception {
    TExceptionType exceptionType;

    public TException(TExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getDetailMessage() {
        return exceptionType.toString();
    }
}
