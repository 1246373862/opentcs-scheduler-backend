package com.server.web.job;

import com.server.common.utils.bean.BeanUtils;
import com.server.system.domain.TbTransportOrder;
import com.server.system.service.ITbTransportOrderService;
import com.server.web.kernel.KernelServiceConfig;
import com.server.web.model.vo.OrdersVO;
import com.server.web.service.OrderService;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.data.order.TransportOrder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class TransportOrderUpdateTask {
  @Resource
  OrderService orderService;
  @Resource
  private ScheduledExecutorService scheduledExecutorService;
  @Resource
  ITbTransportOrderService tbTransportOrderService;
  private static final KernelServicePortal kernelServicePortal = KernelServiceConfig.getKernelServicePortal();
  private static final TransportOrderService transportOrderService = kernelServicePortal.getTransportOrderService();

  @Scheduled(cron = "*/5 * * * * *")
  public void projectInstanceArchivingHandler() {
    //从内核中拿数据
    scheduledExecutorService.submit(() -> {
      Set<TransportOrder> transportOrders = transportOrderService.fetchObjects(TransportOrder.class);
      for (TransportOrder transportOrder : transportOrders) {
        OrdersVO vo = OrdersVO.fromTransportOrder(transportOrder);
        TbTransportOrder tbTransportOrder = tbTransportOrderService.selectTbTransportOrderById(vo.getName());
        if (tbTransportOrder != null) {
          TbTransportOrder kernel = orderService.convert2entity(vo);
          BeanUtils.copyProperties(kernel, tbTransportOrder, "id");
          tbTransportOrderService.updateTbTransportOrder(tbTransportOrder);
        } else {
          TbTransportOrder newOrder = new TbTransportOrder();
          TbTransportOrder kernel = orderService.convert2entity(vo);
          BeanUtils.copyProperties(kernel, newOrder);
          tbTransportOrderService.insertTbTransportOrder(kernel);
        }
      }
    });
  }

}
