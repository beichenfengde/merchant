package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.vo.MerchantRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MerchantRegisterConvert {

    //创建装换构建器
    MerchantRegisterConvert INSTANCE = Mappers.getMapper(MerchantRegisterConvert.class);

    public MerchantDTO vo2dto(MerchantRegisterVO merchantRegisterVO);

    public MerchantRegisterVO dto2vo(MerchantDTO merchantDTO);
}
