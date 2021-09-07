package com.huiminpay.common.cache.exception;

import com.huiminpay.common.cache.domain.ErrorCode;

public class ExceptionCast {
    public static void cast(ErrorCode errorCode) {
        throw new BusinessException(errorCode);
    }
}
