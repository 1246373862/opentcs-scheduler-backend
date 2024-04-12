package com.server.system.mapper;

import java.util.List;
import com.server.system.domain.TbTransportOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

/**
 * 订单表Mapper接口
 *
 * @author Ying
 * @date 2024-04-11
 */

public interface TbTransportOrderMapper
{
    /**
     * 查询订单表
     *
     * @param name 订单表主键
     * @return 订单表
     */

    public TbTransportOrder selectTbTransportOrderById(String name);

    /**
     * 查询订单表列表
     *
     * @param tbTransportOrder 订单表
     * @return 订单表集合
     */
    public List<TbTransportOrder> selectTbTransportOrderList(TbTransportOrder tbTransportOrder);

    /**
     * 新增订单表
     *
     * @param tbTransportOrder 订单表
     * @return 结果
     */
    public int insertTbTransportOrder(TbTransportOrder tbTransportOrder);

    /**
     * 修改订单表
     *
     * @param tbTransportOrder 订单表
     * @return 结果
     */
    public int updateTbTransportOrder(TbTransportOrder tbTransportOrder);

    /**
     * 删除订单表
     *
     * @param id 订单表主键
     * @return 结果
     */
    public int deleteTbTransportOrderById(Long id);

    /**
     * 批量删除订单表
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbTransportOrderByIds(Long[] ids);
}
