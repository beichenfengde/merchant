package com.huiminpay.merchant.controller;

import com.huiminpay.common.cache.util.SecurityUtil;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.api.service.PayChannelParamServiceApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/13 18:37
 */
@RestController
@Api("第三方支付渠道参数操作")
public class PayChannelParamController {

    @Reference
    private PayChannelParamServiceApi payChannelParamServiceApi;

    @PostMapping("/my/pay-channel-params")
    @ApiOperation("保存第三方支付渠道参数")
    @ApiImplicitParam(name = "payChannelParamDTO", value = "第三方支付信息", dataType = "PayChannelParamDTO", paramType = "body")
    public PayChannelParamDTO addPayChannelParam(@RequestBody PayChannelParamDTO payChannelParamDTO) {
        Long merchantId = SecurityUtil.getMerchantId();
        payChannelParamDTO.setMerchantId(merchantId);
        return payChannelParamServiceApi.addPayChannelParam(payChannelParamDTO);
    }

    @ApiOperation("根据应用id，平台服务类型，获取支付渠道参数信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", dataType = "String", paramType = "path", required = true),
            @ApiImplicitParam(name = "platformCode", value = "服务类型", dataType = "String", paramType = "path", required = true)})
    @GetMapping("/my/pay-channel-params/apps/{appId}/platform-channels/{platformCode}")
    public List<PayChannelParamDTO> findPayChannelParamByAppIdAndfPlatformCode(@PathVariable String appId,@PathVariable String platformCode) {
        List<PayChannelParamDTO> payChannelParamByAppIdAndPlatformCode = payChannelParamServiceApi.findPayChannelParamByAppIdAndPlatformCode(appId, platformCode);
        return payChannelParamByAppIdAndPlatformCode;

    }


    @ApiOperation("根据应用id，平台服务类型，第三方支付渠道代码,获取支付渠道参数信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", dataType = "String", paramType = "path", required = true),
            @ApiImplicitParam(name = "payChannel", value = "第三方支付渠道代码", dataType = "String", paramType = "path", required = true),
            @ApiImplicitParam(name = "platformCode", value = "服务类型", dataType = "String", paramType = "path", required = true)})
    @GetMapping("/my/pay-channel-params/apps/{appId}/platform-channels/{platformCode}/pay-channel/{payChannel}")
    public PayChannelParamDTO findPayChannelParamByAppIdAndfPlatformCodeAndPayChannel(@PathVariable String appId,@PathVariable String platformCode,@PathVariable String payChannel) {
        PayChannelParamDTO payChannelParamByAppPlatformCodeAndPayChannel = payChannelParamServiceApi.findPayChannelParamByAppPlatformCodeAndPayChannel(appId, platformCode, payChannel);
        return payChannelParamByAppPlatformCodeAndPayChannel;

    }


}
