package com.huiminpay.merchant.convert;


import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MerchantConvert {

    MerchantConvert INSTANCE = Mappers.getMapper(MerchantConvert.class);

    public Merchant dto2entity(MerchantDTO merchantDTO);

    public MerchantDTO entity2dto(Merchant merchant);

    public List<MerchantDTO> listentity2listdto(List<Merchant> list);
}
