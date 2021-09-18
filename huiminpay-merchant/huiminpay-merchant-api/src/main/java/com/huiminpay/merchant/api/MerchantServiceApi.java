package com.huiminpay.merchant.api;

import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.dto.StaffDTO;
import com.huiminpay.merchant.dto.StoreDTO;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/8/17 15:34
 * @DESC:
 */
public interface MerchantServiceApi {

    public MerchantDTO findMerchantById(Long id);

    //商户注册
    public MerchantDTO registerMerchant(MerchantDTO merchantDTO);


    public MerchantDTO applyMerchant(Long merchantId, MerchantDTO merchantDTO);

    /**
     * 商户下新增门店
     * @param storeDTO
     */
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;

    /**
     * 商户新增员工
     * @param staffDTO
     */
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;

    /**
     * 为门店设置管理员
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException;



}
