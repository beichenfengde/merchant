package com.huiminpay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.exception.BusinessCast;
import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.common.cache.util.PhoneUtil;
import com.huiminpay.merchant.api.MerchantServiceApi;

import com.huiminpay.merchant.convert.MerchantConvert;
import com.huiminpay.merchant.convert.StaffConvert;
import com.huiminpay.merchant.convert.StoreConvert;
import com.huiminpay.merchant.dto.MerchantDTO;

import com.huiminpay.merchant.dto.StaffDTO;
import com.huiminpay.merchant.dto.StoreDTO;
import com.huiminpay.merchant.dto.StoreStaffDTO;
import com.huiminpay.merchant.entity.Merchant;
import com.huiminpay.merchant.entity.Staff;
import com.huiminpay.merchant.entity.Store;
import com.huiminpay.merchant.entity.StoreStaff;
import com.huiminpay.merchant.mapper.MerchantMapper;

import com.huiminpay.merchant.mapper.StaffMapper;
import com.huiminpay.merchant.mapper.StoreMapper;
import com.huiminpay.merchant.mapper.StoreStaffMapper;
import com.huiminpay.user.api.TenantService;

import com.huiminpay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.huiminpay.user.api.dto.tenant.TenantDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/8/17 15:39
 * @DESC:
 */
@Service
@Log4j2
public class MerchantService implements MerchantServiceApi {


    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private StoreStaffMapper storeStaffMapper;

    @Reference
    private TenantService tenantService;

    @Override
    public MerchantDTO findMerchantById(Long id) {

        Merchant merchant = merchantMapper.selectById(id);

        MerchantDTO merchantDTO = new MerchantDTO();
        //????????????
        BeanUtils.copyProperties(merchant, merchantDTO);
        return merchantDTO;
    }

    @Override
    @Transactional
    public MerchantDTO registerMerchant(MerchantDTO merchantDTO) {

        //??????????????????
        if (merchantDTO == null || StringUtils.isEmpty(merchantDTO.getMobile())) {
            new RuntimeException("?????????????????????");
        }

        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            BusinessCast.cast(CommonErrorCode.E_100109);
        }
        if (StringUtils.isEmpty(merchantDTO.getUsername())) {
            BusinessCast.cast(CommonErrorCode.E_100110);
        }
        //?????????????????????
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if (count > 0) {
            BusinessCast.cast(CommonErrorCode.E_100113);
        }

        //????????????sass??????(?????????????????????????????????????????????)
        CreateTenantRequestDTO createTenantRequestDTO = new CreateTenantRequestDTO();
        createTenantRequestDTO.setUsername(merchantDTO.getUsername());
        createTenantRequestDTO.setTenantTypeCode("huimin-merchant");
        createTenantRequestDTO.setPassword(merchantDTO.getPassword());
        createTenantRequestDTO.setName(merchantDTO.getUsername());
        createTenantRequestDTO.setMobile(merchantDTO.getMobile());
        createTenantRequestDTO.setBundleCode("huimin-merchant");
        //????????????sass??????
        TenantDTO tenantDTO = tenantService.createTenantAndAccount(createTenantRequestDTO);
        if (tenantDTO == null || tenantDTO.getId() == 0) {
            BusinessCast.cast(CommonErrorCode.CUSTOM);
        }

        Long tenantId = tenantDTO.getId();
        //???dto??????????????????
        // Merchant merchant = new Merchant();
        // BeanUtils.copyProperties(merchantDTO, merchant);

        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        merchant.setTenantId(tenantId);
        //??????????????????0-????????????1-?????????????????????2-???????????????3-????????????
        merchant.setAuditStatus("0");

        //????????????
        merchantMapper.insert(merchant);

        //????????????
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchant.getId());
        storeDTO.setStoreName("?????????");
        this.createStore(storeDTO);
        //?????????????????????
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setStoreId(merchant.getId());
        staffDTO.setUsername(merchant.getUsername());
        staffDTO.setMobile(merchant.getMobile());
        this.createStaff(staffDTO);

        //????????????????????????
        this.bindStaffToStore(storeDTO.getId(), staffDTO.getId());


        //merchantDTO.setId(merchant.getId());
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    @Override
    @Transactional
    public MerchantDTO applyMerchant(Long merchantId, MerchantDTO merchantDTO) {
        if (merchantDTO == null || merchantId == 0L || merchantId == null) {
            BusinessCast.cast(CommonErrorCode.E_110006);
        }

        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        merchant.setAuditStatus("1");
        merchant.setId(merchantId);

        merchantMapper.updateById(merchant);
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * ????????????
     *
     * @param storeDTO
     * @return
     * @throws BusinessException
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        log.info("?????????????????????" + JSON.toJSONString(store));
        storeMapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }

    /**
     * ????????????
     *
     * @param staffDTO
     * @return
     * @throws BusinessException
     */
    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {
        //????????????????????????????????????
        String mobile = staffDTO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //????????????id???????????????????????????
        if (isExistStaffByMobile(mobile, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        //2.???????????????????????????
        String username = staffDTO.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //????????????id????????????????????????
        if (isExistStaffByUserName(username, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_100114);
        }
        Staff entity = StaffConvert.INSTANCE.dto2entity(staffDTO);
        log.info("?????????????????????");
        staffMapper.insert(entity);
        return StaffConvert.INSTANCE.entity2dto(entity);
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param mobile ?????????
     * @return
     */
    private boolean isExistStaffByMobile(String mobile, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getMobile, mobile).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param userName
     * @param merchantId
     * @return
     */
    private boolean isExistStaffByUserName(String userName, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getUsername, userName).eq(Staff::getMerchantId,
                merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStoreId(storeId);
        storeStaff.setStaffId(staffId);
        storeStaffMapper.insert(storeStaff);
    }
}
