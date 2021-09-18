package com.huiminpay.transaction.api.service;

import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.transaction.api.dto.PayChannelDTO;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/13 17:49
 */
public interface PayChannelServiceApi {

    //根据平台服务类型获取第三方支付渠道
    public List<PayChannelDTO> findPayChannelsByPlatformCode(String platformCode);
}
