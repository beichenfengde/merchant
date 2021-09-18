package com.huiminpay.transaction.api.service;

import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/13 18:19
 */
public interface PayChannelParamServiceApi {

    //保存第三方支付渠道参数
    public PayChannelParamDTO addPayChannelParam(PayChannelParamDTO payChannelParamDTO) throws BusinessException;

    //根据应用id，平台服务类型，获取支付渠道信息
    public List<PayChannelParamDTO> findPayChannelParamByAppIdAndPlatformCode(String appId,String platformCode) throws BusinessException;

    //根据应用id，平台服务类型，第三方支付渠道代码，获取支付渠道参数信息
    public PayChannelParamDTO findPayChannelParamByAppPlatformCodeAndPayChannel(String appId,String platformCode,String payChannel) throws BusinessException;
}
