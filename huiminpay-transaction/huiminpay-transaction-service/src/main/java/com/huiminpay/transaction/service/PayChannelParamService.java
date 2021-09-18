package com.huiminpay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huiminpay.common.cache.Cache;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.exception.BusinessCast;
import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.common.cache.util.RedisUtil;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.api.service.PayChannelParamServiceApi;
import com.huiminpay.transaction.convert.PayChannelParamConvert;
import com.huiminpay.transaction.entity.AppPlatformChannel;
import com.huiminpay.transaction.entity.PayChannelParam;
import com.huiminpay.transaction.mapper.AppPlatformChannelMapper;
import com.huiminpay.transaction.mapper.PayChannelParamMapper;
import lombok.extern.log4j.Log4j2;


import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Set;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/13 18:21
 */
@Service
@Log4j2
public class PayChannelParamService implements PayChannelParamServiceApi {

    @Autowired
    private PayChannelParamMapper payChannelParamMapper;

    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;

    @Autowired
    private Cache cache;

    @Override
    public PayChannelParamDTO addPayChannelParam(PayChannelParamDTO payChannelParamDTO) throws BusinessException {
        //判断必要参数
        if (payChannelParamDTO == null) {
            BusinessCast.cast(CommonErrorCode.E_110006);
        }
        PayChannelParam payChannelParam = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParamDTO);

        //根据应用id和平台支付类型过去应用与支付渠道的id
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, payChannelParamDTO.getAppId())
                .eq(AppPlatformChannel::getPlatformChannel, payChannelParamDTO.getPlatformChannelCode())
        );

        if (appPlatformChannel == null) {
            BusinessCast.cast(CommonErrorCode.E_200018);
        }
        payChannelParam.setAppPlatformChannelId(appPlatformChannel.getId());
        payChannelParamMapper.insert(payChannelParam);

        //redis缓存同步,吧redis库中记录删除

        try {
            this.updateRedis(payChannelParamDTO.getAppId(),payChannelParamDTO.getPlatformChannelCode());
        } catch (Exception e) {
            log.error("把支付参数存入redis缓存异常:{}",e.getMessage());
        }

        return PayChannelParamConvert.INSTANCE.entity2dto(payChannelParam);
    }

    private void updateRedis(String appId, String platformCode) {
        //先查询看redis库中是否存在，如果有删除，没有添加
        String key = RedisUtil.keyBuilder(appId, platformCode);

        //查询redis库
        if (cache.exists(key)) {
            cache.del(key);
        }

        //根据应用id和平台服务类型查询参数列表信息
        List<PayChannelParamDTO> lsit = this.findPayChannelParamByAppIdAndPlatformCode(appId, platformCode);
        if (lsit != null) {
            String json = JSON.toJSONString(lsit);
            cache.set(key,json);
        }

    }


    @Override
    public List<PayChannelParamDTO> findPayChannelParamByAppIdAndPlatformCode(String appId, String platformCode) {
        //校验
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(platformCode)) {
            BusinessCast.cast(CommonErrorCode.E_100101);
        }

        //先查redis缓存
        String key = RedisUtil.keyBuilder(appId, platformCode);
        String jsonParam = cache.get(key);
        try {
            if (StringUtils.isNotEmpty(jsonParam)) {
                List<PayChannelParamDTO> payChannelParamDTOS = JSON.parseArray(jsonParam, PayChannelParamDTO.class);
                return payChannelParamDTOS;
            }
        } catch (Exception e) {
            log.error("查询支付参数redis缓存异常：{}", e.getMessage());
        }

        //根据应用id和平台服务类型获取应用于平台服务类型中间表对象
        AppPlatformChannel appPlatformChannel = findAppPlatformChannelByAppIdAndplatform(appId, platformCode);
        if (appPlatformChannel == null) {
            BusinessCast.cast(CommonErrorCode.E_200019);
        }

        //根据中间表的id获取第三方参数列表信息
        List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannel.getId()));

//        List<PayChannelParamDTO> payChannelParamDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);

        String json = JSON.toJSONString(payChannelParams);
        List<PayChannelParamDTO> payChannelParamDTOS = JSON.parseArray(json, PayChannelParamDTO.class);

        //把数据库中的记录添加到redis缓存中
        try {
            cache.set(key,json);
        } catch (Exception e) {
            log.error("把支付参数存入redis缓存异常:{}", e.getMessage());
        }

        return payChannelParamDTOS;
    }

    private AppPlatformChannel findAppPlatformChannelByAppIdAndplatform(String appId, String platformCode) {
        //根据应用id和平台服务类型获取应用与平台服务类型中间表对象
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformCode)
        );


        return appPlatformChannel;
    }

    @Override
    public PayChannelParamDTO findPayChannelParamByAppPlatformCodeAndPayChannel(String appId, String platformCode, String payChannel) throws BusinessException {
        //必要参数校验
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(platformCode) || StringUtils.isEmpty(payChannel)) {
            BusinessCast.cast(CommonErrorCode.E_100101);
        }
        AppPlatformChannel appPlatformChannel = findAppPlatformChannelByAppIdAndplatform(appId, platformCode);

        if (appPlatformChannel == null) {
            BusinessCast.cast(CommonErrorCode.E_200019);
        }

        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannel.getId())
                .eq(PayChannelParam::getPayChannel, payChannel)
        );

        PayChannelParamDTO payChannelParamDTO = PayChannelParamConvert.INSTANCE.entity2dto(payChannelParam);

        return payChannelParamDTO;
    }
}
