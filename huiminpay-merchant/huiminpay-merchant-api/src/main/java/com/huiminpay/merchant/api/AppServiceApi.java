package com.huiminpay.merchant.api;

import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.merchant.dto.AppDTO;

import java.util.List;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/8 17:30
 */
public interface AppServiceApi {

    //添加应用
    public AppDTO creastApp(Long merchantId, AppDTO appDTO) throws BusinessException;

    public List<AppDTO> findAppsByMarchantId(Long merchantId) throws BusinessException;

    //
    public AppDTO findAppById(String appId) throws BusinessException;

}
