package com.huiminpay.merchant.handler;

import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.domain.RestErrorResponse;
import com.huiminpay.common.cache.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class) //指定该全局异常处理类
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse processException(Exception exception) {
        //判断异常是否为自定义异常
        if (exception instanceof BusinessException) {
            BusinessException be = (BusinessException) exception;
            RestErrorResponse restErrorResponse = new RestErrorResponse(be.getErrorCode().getCode(), be.getErrorCode().getDesc());
            return restErrorResponse;
        }
        return new RestErrorResponse(CommonErrorCode.UNKNOWN.getCode(), CommonErrorCode.UNKNOWN.getDesc());
    }
}
