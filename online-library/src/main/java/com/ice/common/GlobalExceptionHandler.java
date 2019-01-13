package com.ice.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public TMsg<String> handler(Exception exception, HttpServletRequest request) {
        String requestURI = "";
        if(null != request){
            requestURI = request.getRequestURI();
        }
        TException tException;
        if (exception instanceof TException) {
            tException = (TException) exception;
            log.error("[{}] request catch custom error code:{} desc:{}", requestURI, tException.getCode(), tException.getDesc());
        } else {
            tException = new TException(Constant.SYSTEM_ERROR, "系统错误");
            log.error("[{}] request catch system error {}", requestURI, exception.getMessage());
        }
        // 开发时将错误打印在控制台
        exception.printStackTrace();
        return new TMsg<>(tException.getCode(), tException.getDesc(), null);
    }
}