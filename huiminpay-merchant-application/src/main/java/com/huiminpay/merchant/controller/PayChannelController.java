package com.huiminpay.merchant.controller;

import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.api.service.PayChannelServiceApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/13 18:04
 */
@RestController
@Api("第三方支付渠道操作，包含增删改查等等...")
public class PayChannelController {

    @Reference
    private PayChannelServiceApi payChannelServiceApi;

    @GetMapping("/my/pay-channels/platform-channels/{platformChannel}")
    @ApiOperation("根据平台服务类型获取第三方支付渠道列表信息")
    @ApiImplicitParam(name = "platformChannel", value = "平台服务类型",dataType = "Stirng",paramType = "path")
    public List<PayChannelDTO> findPayChannelsByPlatformChannel(@PathVariable String platformChannel){
        List<PayChannelDTO> payChannelsByPlatformCode = payChannelServiceApi.findPayChannelsByPlatformCode(platformChannel);
        return payChannelsByPlatformCode;
    }

}
