package com.huiminpay.merchant.service;

import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.exception.BusinessCast;
import com.huiminpay.common.cache.exception.BusinessException;
import com.huiminpay.common.cache.exception.ExceptionCast;
import com.huiminpay.common.cache.util.PhoneUtil;
import com.huiminpay.common.cache.util.QiniuUtil;
import com.huiminpay.merchant.api.MerchantServiceApi;
import com.huiminpay.merchant.convert.MerchantRegisterConvert;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.vo.MerchantRegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;


/**
 * @AUTHOR: yadong
 * @DATE: 2021/8/19 11:11
 * @DESC:
 */
@Service
@Slf4j
public class MerchantService {
    @Autowired
    private RestTemplate restTemplate;

    @Reference
    private MerchantServiceApi merchantServiceApi;

    //http://localhost:56085/sailing/
    @Value("${sailing.url}")
    private String url;

    @Value("${yun.qiniu.accessKey}")
    private String accessKeyId;
    @Value("${yun.qiniu.secretKey}")
    private String secretKeySecret;
    @Value("${yun.qiniu.bucket}")
    private String bucket;
    @Value("${yun.qiniu.domain}")
    private String domain;

    public MerchantRegisterVO register(MerchantRegisterVO merchantRegisterVO) {

        if (merchantRegisterVO == null) {
            BusinessCast.cast(CommonErrorCode.E_100101);
        }
        if (StringUtils.isEmpty(merchantRegisterVO.getMobile())) {
            BusinessCast.cast(CommonErrorCode.E_100112);
        }
        if (!PhoneUtil.isMatches(merchantRegisterVO.getMobile())) {
            BusinessCast.cast(CommonErrorCode.E_100109);
        }
        if (StringUtils.isEmpty(merchantRegisterVO.getUsername())) {
            BusinessCast.cast(CommonErrorCode.E_100110);
        }
        if (StringUtils.isEmpty(merchantRegisterVO.getPassword())) {
            BusinessCast.cast(CommonErrorCode.E_100111);
        }


        //????????????sailing?????????????????????
        checkMsmCode(merchantRegisterVO.getVerifiykey(), merchantRegisterVO.getVerifiyCode());

        MerchantDTO merchantDTO = new MerchantDTO();
        BeanUtils.copyProperties(merchantRegisterVO, merchantDTO);
//         MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.vo2dto(merchantRegisterVO);

        //????????????????????????????????????service??????????????????
        merchantServiceApi.registerMerchant(merchantDTO);
        return merchantRegisterVO;
    }

    ////????????????sailing?????????????????????
    private void checkMsmCode(String key, String code) {
        // http://localhost:56085/sailing/verify?name=sms&verificationCode=qq&verificationKey=22
        String msmUrl = url + "verify?name=sms&verificationCode=" + code + "&verificationKey=" + key;
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(msmUrl,
                    HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            if (responseEntity == null) {
                log.error("?????????????????????");
//                throw new RuntimeException("?????????????????????");
                throw new BusinessException(CommonErrorCode.E_100102);
            }
            Map entityBody = responseEntity.getBody();
            if (entityBody == null || entityBody.get("result") == null) {
                log.error("?????????????????????");
//                throw new RuntimeException("?????????????????????");
                throw new BusinessException(CommonErrorCode.E_100102);
            }
            boolean result = (boolean) entityBody.get("result");
            if (!result) {
                log.error("?????????????????????");
//                throw new RuntimeException("?????????????????????");
                throw new BusinessException(CommonErrorCode.E_100102);
            }
        } catch (Exception e) {
            log.error("?????????????????????:{}", e.getMessage());
//            throw new RuntimeException("?????????????????????");
//            throw new BusinessException(CommonErrorCode.E_100102);
            ExceptionCast.cast(CommonErrorCode.E_100102);
        }


    }

    public MerchantDTO queryMerchantById(Long merchantId) {
        MerchantDTO merchantDTO = merchantServiceApi.findMerchantById(merchantId);
        return merchantDTO;
    }


    public String upload(MultipartFile multipartFile) {

        if (multipartFile == null) {
            BusinessCast.cast(CommonErrorCode.E_110006);
        }
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() +
                    originalFilename.substring(originalFilename.lastIndexOf("."));

            byte[] bytes = multipartFile.getBytes();
            //?????????????????????
            QiniuUtil.upload(accessKeyId, secretKeySecret, bucket, fileName, bytes);

            return domain + fileName;
        } catch (Exception e) {
            log.error("??????????????????:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
