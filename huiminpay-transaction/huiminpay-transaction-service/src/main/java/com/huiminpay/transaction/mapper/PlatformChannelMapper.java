package com.huiminpay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.entity.PlatformChannel;
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
public interface PlatformChannelMapper extends BaseMapper<PlatformChannel> {


}
