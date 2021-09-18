package com.huiminpay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiminpay.transaction.entity.PayChannel;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2019-11-15
 */
@Repository
public interface PayChannelMapper extends BaseMapper<PayChannel> {

    @Select("SELECT\n" +
            "\tpc.ID,\n" +
            "\tpc.CHANNEL_CODE,\n" +
            "\tpc.CHANNEL_NAME \n" +
            "FROM\n" +
            "\tpay_channel pc,\n" +
            "\tplatform_pay_channel ppc,\n" +
            "\tplatform_channel plc \n" +
            "WHERE\n" +
            "\tpc.CHANNEL_CODE = ppc.PAY_CHANNEL \n" +
            "\tAND ppc.PLATFORM_CHANNEL = plc.CHANNEL_CODE \n" +
            "\tAND ppc.PLATFORM_CHANNEL = #{platformCode}")
    public List<PayChannel> findPayChannelsByPlatformCode(String platformCode);
}
