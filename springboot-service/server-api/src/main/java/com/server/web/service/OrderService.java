package com.server.web.service;

import com.server.system.domain.TbTransportOrder;
import com.server.web.model.dto.TransportOrderCreationDTO;
import com.server.web.model.dto.UpdateTransportOrderIntendedVehicleDTO;
import com.server.web.model.vo.OrdersVO;

import java.util.List;

public interface OrderService {

  List<TbTransportOrder> getOrders(String number);

void withdrawByOrderId(String number);

void  createOrder(TransportOrderCreationDTO creationDTO);

  TbTransportOrder selectOneByNumber(String number);

 TbTransportOrder convert2entity(OrdersVO vo);

 void updateTransportOrderIntendedVehicle(UpdateTransportOrderIntendedVehicleDTO dto);
}
