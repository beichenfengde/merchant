package com.huiminpay.transaction.api.service;

import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/11 15:20
 */
public interface PlatformChannelServiceApi {

    public List<PlatformChannelDTO> findPlatformChannelList() throws BusinessException;

    //绑定服务类型
    public void bindApp2Plateform(String appId, String paltformChannel) throws BusinessException;


    public int queryBindApp2Platform(String appId, String platformChannelCodes) throws BusinessException;
}
