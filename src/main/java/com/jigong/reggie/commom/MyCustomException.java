package com.jigong.reggie.commom;

//  自定义异常
public class MyCustomException extends RuntimeException{

    public MyCustomException(String msg) {
        super(msg);
    }

}
