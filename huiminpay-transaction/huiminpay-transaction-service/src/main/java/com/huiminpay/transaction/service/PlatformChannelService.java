package com.huiminpay.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.exception.BusinessCast;
import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.transaction.api.dto.PlatformChannelDTO;
import com.huiminpay.transaction.api.service.PlatformChannelServiceApi;
import com.huiminpay.transaction.convert.PlatformChannelConvert;
import com.huiminpay.transaction.entity.AppPlatformChannel;
import com.huiminpay.transaction.entity.PlatformChannel;
import com.huiminpay.transaction.mapper.AppPlatformChannelMapper;
import com.huiminpay.transaction.mapper.PlatformChannelMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/11 15:22
 */

@Service
public class PlatformChannelService implements PlatformChannelServiceApi {

    @Autowired
    private PlatformChannelMapper platformChannelMapper;


    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;


    @Override
    @Transactional
    public List<PlatformChannelDTO> findPlatformChannelList() throws BusinessException {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        List<PlatformChannelDTO> platformChannelDTOS = PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);

        return platformChannelDTOS;
    }

    @Override
    @Transactional
    public void bindApp2Plateform(String appId, String paltformChannel) throws BusinessException {
        //判断应用是否已经和平台绑定服务类型
        AppPlatformChannel appPlatformChannelDb = appPlatformChannelMapper.selectOne(new QueryWrapper<AppPlatformChannel>()
                .lambda().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, paltformChannel));
        if (appPlatformChannelDb == null) {
            AppPlatformChannel appPlatformChannel = new AppPlatformChannel();
            appPlatformChannel.setAppId(appId);
            appPlatformChannel.setPlatformChannel(paltformChannel);
            appPlatformChannelMapper.insert(appPlatformChannel);
        }
    }

    @Override
    @Transactional
    public int queryBindApp2Platform(String appId, String platformChannelCodes) throws BusinessException {
        //必要参数校验
        if (appId == null || platformChannelCodes == null) {
            BusinessCast.cast(CommonErrorCode.E_110006);
        }
        Integer count = appPlatformChannelMapper.selectCount(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes));
        if (count > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
