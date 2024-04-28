package com.server.web.service;

import com.server.system.domain.TbTransportOrder;
import com.server.web.model.dto.TransportOrderCreationDTO;
import com.server.web.model.dto.UpdateTransportOrderIntendedVehicleDTO;
import com.server.web.model.vo.OrdersVO;

import java.util.List;

public interface OrderService {


    /**
     * 获取订单
     *
     * @param number 订单编号
     * @return
     */
    List<TbTransportOrder> getOrders(String number);

    /**
     * 撤回订单
     *
     * @param number 订单编号
     */
    void withdrawByOrderId(String number);

    /**
     * 创建订单
     *
     * @param creationDTO 信息
     */
    void createOrder(TransportOrderCreationDTO creationDTO);

    /**
     * 根据编号获取订单
     *
     * @param number 订单编号
     * @return
     */
    TbTransportOrder selectOneByNumber(String number);

    /**
     * vo转实体类
     *
     * @param vo
     * @return
     */
    TbTransportOrder convert2entity(OrdersVO vo);

    /**
     * 更改订单预执行车辆
     *
     * @param dto
     */
    void updateTransportOrderIntendedVehicle(UpdateTransportOrderIntendedVehicleDTO dto);
}
