package com.server.web.service.impl;

import com.server.web.kernel.KernelServiceConfig;
import com.server.web.model.vo.OrdersVO;
import com.server.web.service.OrderService;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.access.rmi.KernelServicePortalBuilder;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.GetTransportOrderResponseTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

  private static final KernelServicePortal kernelServicePortal = KernelServiceConfig.getKernelServicePortal();
  private static final TransportOrderService transportOrderService = kernelServicePortal.getTransportOrderService();

  @Override
  public List<OrdersVO> getOrders() {

    return transportOrderService.fetchObjects(TransportOrder.class).stream()
        .map(OrdersVO::fromTransportOrder)
        .collect(Collectors.toList());
  }
}
