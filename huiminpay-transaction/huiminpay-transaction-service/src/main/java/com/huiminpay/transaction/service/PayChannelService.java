package com.huiminpay.transaction.service;


import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.api.service.PayChannelServiceApi;
import com.huiminpay.transaction.convert.PayChannelConvert;
import com.huiminpay.transaction.entity.PayChannel;
import com.huiminpay.transaction.mapper.PayChannelMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/11 15:22
 */

@Service
public class PayChannelService implements PayChannelServiceApi {

    @Autowired
    private PayChannelMapper payChannelMapper;

    @Override
    public List<PayChannelDTO> findPayChannelsByPlatformCode(String platformCode) {
        List<PayChannel> payChannels = payChannelMapper.findPayChannelsByPlatformCode(platformCode);
        List<PayChannelDTO> payChannelDTOS = PayChannelConvert.INSTANCE.listentity2listdto(payChannels);
        return payChannelDTOS;
    }
}
