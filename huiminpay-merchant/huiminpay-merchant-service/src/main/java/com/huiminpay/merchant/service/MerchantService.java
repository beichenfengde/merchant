package com.huiminpay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.exception.BusinessCast;
import com.huiminpay.common.cache.util.PhoneUtil;
import com.huiminpay.merchant.api.MerchantServiceApi;

import com.huiminpay.merchant.convert.MerchantConvert;
import com.huiminpay.merchant.dto.MerchantDTO;

import com.huiminpay.merchant.entity.Merchant;
import com.huiminpay.merchant.mapper.MerchantMapper;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/8/17 15:39
 * @DESC:
 */
@org.apache.dubbo.config.annotation.Service
public class MerchantService implements MerchantServiceApi {


    @Autowired
    private MerchantMapper merchantMapper;




    @Override
    public MerchantDTO findMerchantById(Long id) {

        Merchant merchant = merchantMapper.selectById(id);

        MerchantDTO merchantDTO = new MerchantDTO();
        //对象对拷
        BeanUtils.copyProperties(merchant,merchantDTO);
        return merchantDTO;
    }

    @Override
    @Transactional
    public MerchantDTO registerMerchant(MerchantDTO merchantDTO) {

        //校验必要参数
        if (merchantDTO == null || StringUtils.isEmpty(merchantDTO.getMobile())) {
            new RuntimeException("传入参数有异常");
        }

        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            BusinessCast.cast(CommonErrorCode.E_100109);
        }
        if (StringUtils.isEmpty(merchantDTO.getUsername())) {
            BusinessCast.cast(CommonErrorCode.E_100110);
        }
        //手机号唯一校验
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if (count > 0) {
            BusinessCast.cast(CommonErrorCode.E_100113);
        }

        //把dto转换为实体类
        /*Merchant merchant = new Merchant();
        BeanUtils.copyProperties(merchantDTO, merchant);*/

        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);

        //设置审核状态0-未申请，1-已申请未审核，2-申请通过，3-申请拒绝
        merchant.setAuditStatus("0");
        merchantMapper.insert(merchant);

//        merchantDTO.setId(merchant.getId());
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    @Override
    @Transactional
    public MerchantDTO applyMerchant(Long merchantId, MerchantDTO merchantDTO) {
        //必要字段校验
        if (merchantDTO==null || merchantId ==null || merchantId == 0L){
            BusinessCast.cast(CommonErrorCode.E_110006);
        }
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);

        merchant.setId(merchantId);

        merchantMapper.updateById(merchant);

        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }
}
