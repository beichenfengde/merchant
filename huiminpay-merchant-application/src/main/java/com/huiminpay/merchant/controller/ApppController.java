package com.huiminpay.merchant.controller;

import com.huiminpay.common.cache.util.SecurityUtil;
import com.huiminpay.merchant.api.AppServiceApi;
import com.huiminpay.merchant.dto.AppDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/8 17:58
 */
@RestController
@Api(tags = "应用模块管理，应用添加，查询等操作。。。。。。")
public class ApppController {

    @Reference
    private AppServiceApi appServiceApi;

    @ApiOperation("添加应用")
    @PostMapping("/my/apps")
    @ApiImplicitParam(name = "appDTO", value = "应用信息", dataType = "AppDTO", paramType = "body", required = true)
    public AppDTO createApp(@RequestBody AppDTO appDTO) {
        Long merchantId = SecurityUtil.getMerchantId();
        AppDTO app = appServiceApi.creastApp(merchantId, appDTO);
        return app;
    }

    @ApiOperation("根据商户Id获取应用列表信息")
    @GetMapping("/my/app")
    public List<AppDTO> findApps() {
        Long merchantId = SecurityUtil.getMerchantId();
        List<AppDTO> list = appServiceApi.findAppsByMarchantId(merchantId);
        return list;

    }

    @ApiOperation("根据应用id查询用户信息")
    @GetMapping("/my/apps/{appId}")
    @ApiImplicitParam(name = "appId",value = "应用Id",dataType = "String",paramType = "path",required = true)
    public AppDTO findAppById(@PathVariable("appId") String appId) {
        AppDTO app = appServiceApi.findAppById(appId);
        return app;
    }
}
