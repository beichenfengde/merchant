package com.huiminpay.transaction.convert;

import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.entity.PayChannel;
import com.huiminpay.transaction.entity.PayChannelParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayChannelConvert {

    PayChannelConvert INSTANCE= Mappers.getMapper(PayChannelConvert.class);

    PayChannelDTO entity2dto(PayChannel entity);

    PayChannel dto2entity(PayChannelDTO dto);

    List<PayChannelDTO> listentity2listdto(List<PayChannel> PayChannel);

    List<PayChannel> listdto2listentity(List<PayChannelDTO> PayChannelDTO);
}
