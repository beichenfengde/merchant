package com.huiminpay.merchant.controller;

import com.huiminpay.transaction.api.dto.PlatformChannelDTO;
import com.huiminpay.transaction.api.service.PlatformChannelServiceApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/11 15:15
 */

@Api(tags = "惠民支付服务类型管理，包含增删改查......")
@RestController
public class PlatformChannelController {

    @Reference
    private PlatformChannelServiceApi platformChannelServiceApi;

    @ApiOperation("查询服务类型列表信息")
    @GetMapping("/my/platform-channels")
    public List<PlatformChannelDTO> findPlatforms() {
        List<PlatformChannelDTO> platformChannelList = platformChannelServiceApi.findPlatformChannelList();
        return platformChannelList;
    }

    @ApiOperation("应用绑定服务类型")
    @PostMapping("/my/apps/{appId}/platform-channels")
    @ApiImplicitParams(
            {@ApiImplicitParam(value = "应用Id", name = "appId", dataType = "String", paramType = "path"),
                    @ApiImplicitParam(value = "服务类型code", name = "platformChannel", dataType = "String", paramType = "query")
            })
    public void bindApp2Platform(@PathVariable String appId, String platformChannel) {
        platformChannelServiceApi.bindApp2Plateform(appId, platformChannel);
    }

    @ApiOperation("是否查询应用绑定服务类型")
    @GetMapping("/my/merchants/apps/platformchannels")
    @ApiImplicitParams(
            {@ApiImplicitParam(value = "应用Id", name = "appId", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(value = "服务类型code", name = "platformChannelCodes", dataType = "String", paramType = "query")
            })
    public int queryBindApp2Platform(String appId,String platformChannelCodes) {
        return platformChannelServiceApi.queryBindApp2Platform(appId, platformChannelCodes);
    }

}
