package com.huiminpay.merchant.convert;


import com.huiminpay.merchant.dto.AppDTO;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.entity.App;
import com.huiminpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AppConvert {

    AppConvert INSTANCE = Mappers.getMapper(AppConvert.class);

    public App dto2entity(AppDTO appDTO);

    public AppDTO entity2dto(App app);

    public List<AppDTO> listentity2listdto(List<App> apps);
}
