package com.server.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.server.system.mapper.TbTransportOrderMapper;
import com.server.system.domain.TbTransportOrder;
import com.server.system.service.ITbTransportOrderService;

/**
 * 订单表Service业务层处理
 *
 * @author Ying
 * @date 2024-04-11
 */
@Service
public class TbTransportOrderServiceImpl implements ITbTransportOrderService
{
    @Autowired
    private TbTransportOrderMapper tbTransportOrderMapper;

    /**
     * 查询订单表
     *
     * @param id 订单表主键
     * @return 订单表
     */
    @Override
    public TbTransportOrder selectTbTransportOrderById(String name)
    {
        return tbTransportOrderMapper.selectTbTransportOrderById(name);
    }

    /**
     * 查询订单表列表
     *
     * @param tbTransportOrder 订单表
     * @return 订单表
     */
    @Override
    public List<TbTransportOrder> selectTbTransportOrderList(TbTransportOrder tbTransportOrder)
    {
        return tbTransportOrderMapper.selectTbTransportOrderList(tbTransportOrder);
    }

    /**
     * 新增订单表
     *
     * @param tbTransportOrder 订单表
     * @return 结果
     */
    @Override
    public int insertTbTransportOrder(TbTransportOrder tbTransportOrder)
    {
        return tbTransportOrderMapper.insertTbTransportOrder(tbTransportOrder);
    }

    /**
     * 修改订单表
     *
     * @param tbTransportOrder 订单表
     * @return 结果
     */
    @Override
    public int updateTbTransportOrder(TbTransportOrder tbTransportOrder)
    {
        return tbTransportOrderMapper.updateTbTransportOrder(tbTransportOrder);
    }

    /**
     * 批量删除订单表
     *
     * @param ids 需要删除的订单表主键
     * @return 结果
     */
    @Override
    public int deleteTbTransportOrderByIds(Long[] ids)
    {
        return tbTransportOrderMapper.deleteTbTransportOrderByIds(ids);
    }

    /**
     * 删除订单表信息
     *
     * @param id 订单表主键
     * @return 结果
     */
    @Override
    public int deleteTbTransportOrderById(Long id)
    {
        return tbTransportOrderMapper.deleteTbTransportOrderById(id);
    }
}
