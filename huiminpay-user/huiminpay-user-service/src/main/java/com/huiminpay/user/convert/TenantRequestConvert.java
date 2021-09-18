package com.huiminpay.user.convert;

import com.huiminpay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.huiminpay.user.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantRequestConvert {

    TenantRequestConvert INSTANCE = Mappers.getMapper(TenantRequestConvert.class);

    CreateTenantRequestDTO entity2dto(Tenant entity);

    Tenant dto2entity(CreateTenantRequestDTO dto);
}
