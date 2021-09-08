package com.huiminpay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.exception.BusinessCast;
import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.merchant.api.AppServiceApi;
import com.huiminpay.merchant.convert.AppConvert;
import com.huiminpay.merchant.dto.AppDTO;
import com.huiminpay.merchant.entity.App;
import com.huiminpay.merchant.entity.Merchant;
import com.huiminpay.merchant.mapper.AppMapper;
import com.huiminpay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * @AUTHOR: zhaochen
 * @DATE: 2021/9/8 17:32
 */
@Service
public class AppService implements AppServiceApi {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public AppDTO creastApp(Long merchantId, AppDTO appDTO) throws BusinessException {
        //必要参数校验
        if (appDTO == null || merchantId == null || merchantId == 0L) {
            BusinessCast.cast(CommonErrorCode.E_100101);
        }
        //校验商户信息
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            BusinessCast.cast(CommonErrorCode.E_200002);
        }
        //设置审核状态0-未申请，1-已申请未审核，2-申请通过，3-申请拒绝
        if (!"2".equals(merchant.getAuditStatus())) {
            BusinessCast.cast(CommonErrorCode.E_200003);
        }

        //校检用户名是否存在
        boolean flag = isExistAppName(merchantId, appDTO.getAppName());
        if (flag) {
            BusinessCast.cast(CommonErrorCode.E_200004);
        }

        //添加应用
        appDTO.setMerchantId(merchantId);
        App app = AppConvert.INSTANCE.dto2entity(appDTO);
        String uuid = UUID.randomUUID().toString();
        app.setAppId(uuid);
        appMapper.insert(app);

        return AppConvert.INSTANCE.entity2dto(app);
    }


    @Override
    public List<AppDTO> findAppsByMarchantId(Long merchantId) throws BusinessException {
        //必要参数校检
        if (merchantId == null || merchantId == 0L) {
            BusinessCast.cast(CommonErrorCode.E_110006);
        }
        List<App> apps = appMapper.selectList(new QueryWrapper<App>().lambda().eq(App::getMerchantId, merchantId));
        List<AppDTO> appDTOS = AppConvert.INSTANCE.listentity2listdto(apps);

        return appDTOS;

    }

    @Override
    public AppDTO findAppById(String appId) throws BusinessException {
        //必要参数校检
        if (appId == null) {
            BusinessCast.cast(CommonErrorCode.E_110001);
        }
        App app = appMapper.selectOne(new LambdaQueryWrapper<App>().eq(App::getAppId, appId));
        AppDTO appDTO = AppConvert.INSTANCE.entity2dto(app);
        return appDTO;
    }

    //校检用户名是否存在
    private boolean isExistAppName(Long merchantId, String appName) {
        Integer count = appMapper.selectCount(new LambdaQueryWrapper<App>()
                .eq(App::getAppName, appName)
                .eq(App::getMerchantId, merchantId));
        if (count > 0) {
            return true;
        }
        return false;
    }

}
