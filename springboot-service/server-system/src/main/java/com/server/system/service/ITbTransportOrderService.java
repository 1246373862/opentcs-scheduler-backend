package com.server.system.service;

import java.util.List;
import com.server.system.domain.TbTransportOrder;

/**
 * 订单表Service接口
 *
 * @author Ying
 * @date 2024-04-11
 */
public interface ITbTransportOrderService
{
    /**
     * 查询订单表
     *
     * @param id 订单表主键
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
     * 批量删除订单表
     *
     * @param ids 需要删除的订单表主键集合
     * @return 结果
     */
    public int deleteTbTransportOrderByIds(Long[] ids);

    /**
     * 删除订单表信息
     *
     * @param id 订单表主键
     * @return 结果
     */
    public int deleteTbTransportOrderById(Long id);
}
