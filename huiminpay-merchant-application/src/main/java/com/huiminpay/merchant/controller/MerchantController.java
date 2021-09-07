package com.huiminpay.merchant.controller;

import com.huiminpay.common.cache.util.SecurityUtil;
import com.huiminpay.merchant.api.MerchantServiceApi;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.service.MerchantService;
import com.huiminpay.merchant.service.SmsService;
import com.huiminpay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/8/17 15:45
 * @DESC:
 */
@RestController
@Api("商户管理系统controller接口，包含商户注册，资质申请，获取验证码等。。。" )
public class MerchantController {

    @Reference
    private MerchantServiceApi merchantServiceApi;

    @Autowired
    private SmsService smsService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping("find/{id}")
    public MerchantDTO findMerchantById(@PathVariable("id") Long id){
       return merchantServiceApi.findMerchantById(id);
    }

    @GetMapping("sms")
    @ApiImplicitParam(name = "phone" ,value = "手机号",dataType = "String",required = true,paramType="query")
    public String getSendSms(@RequestParam("phone") String mobile){
        String msmCodeKey = smsService.sendMsmCode(mobile);
        return msmCodeKey;
    }

    @ApiOperation("商户注册")
    @ApiImplicitParam(name = "merchantRegisterVO" ,value = "商户注册信息",dataType = "MerchantRegisterVO",required = true,paramType="body")
    @PostMapping("/merchants/register")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegisterVO){
        MerchantRegisterVO registerVO = merchantService.register(merchantRegisterVO);
        return registerVO;
    }



}
