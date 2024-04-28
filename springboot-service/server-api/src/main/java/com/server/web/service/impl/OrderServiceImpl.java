package com.server.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.server.common.utils.StringUtils;
import com.server.common.utils.bean.BeanUtils;
import com.server.system.domain.TbTransportOrder;
import com.server.system.mapper.TbTransportOrderMapper;
import com.server.system.service.ITbTransportOrderService;
import com.server.web.kernel.KernelServiceConfig;
import com.server.web.model.dto.DestinationCreationDTO;
import com.server.web.model.dto.TransportOrderCreationDTO;
import com.server.web.model.dto.UpdateTransportOrderIntendedVehicleDTO;
import com.server.web.model.vo.OrdersVO;
import com.server.web.service.OrderService;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.access.rmi.KernelServicePortalBuilder;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.services.DispatcherService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.GetTransportOrderResponseTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    ITbTransportOrderService tbTransportOrderService;

    private static final KernelServicePortal kernelServicePortal = KernelServiceConfig.getKernelServicePortal();
    private static final TransportOrderService transportOrderService = kernelServicePortal.getTransportOrderService();

    private static final VehicleService vehicleService = kernelServicePortal.getVehicleService();
    private static final DispatcherService dispatcherService = kernelServicePortal.getDispatcherService();

    @Override
    public List<TbTransportOrder> getOrders(String number) {
        if (!StringUtils.isEmpty(number)) {
            return this.selectByNumber(number);
        } else {
            return tbTransportOrderService.selectTbTransportOrderList(null);
        }

    }

    @Override
    public void withdrawByOrderId(String number) {
        TransportOrder order = vehicleService.fetchObject(TransportOrder.class, number);
        if (order.getProcessingVehicle() != null) {
            vehicleService.updateVehicleIntegrationLevel(order.getProcessingVehicle(),
                    Vehicle.IntegrationLevel.TO_BE_RESPECTED);
        }
        dispatcherService.withdrawByTransportOrder(order.getReference(), true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(TransportOrderCreationDTO creationDTO) {
        if (StringUtils.isEmpty(creationDTO.getVehicleName())) {
            creationDTO.setVehicleName(null);
        }
        String number = "Order-" + System.currentTimeMillis();
        List<DestinationCreationTO> destinationCreationTOS = new ArrayList<>();
        for (DestinationCreationDTO dto : creationDTO.getDestinations()) {
            DestinationCreationTO destinationCreationTO = new DestinationCreationTO(dto.getDestLocationName(), dto.getDestOperation());
            destinationCreationTOS.add(destinationCreationTO);
        }
        TransportOrderCreationTO orderTO
                = new TransportOrderCreationTO(number, destinationCreationTOS);
        orderTO = orderTO
                .withDispensable(creationDTO.getDispensable())
                .withIntendedVehicleName(creationDTO.getVehicleName())
                .withDeadline(Instant.now().plus(1, ChronoUnit.HOURS));
        transportOrderService.createTransportOrder(orderTO);
        // 订单入库
        TransportOrder order = vehicleService.fetchObject(TransportOrder.class, number);
        OrdersVO vo = OrdersVO.fromTransportOrder(order);
        TbTransportOrder tbTransportOrder = convert2entity(vo);
        tbTransportOrderService.insertTbTransportOrder(tbTransportOrder);
    }

    @Override
    public TbTransportOrder convert2entity(OrdersVO vo) {
        TbTransportOrder tbTransportOrder = new TbTransportOrder();
        BeanUtils.copyProperties(vo, tbTransportOrder, "id");
        tbTransportOrder.setDestinations(JSON.toJSONString(vo.getDestinations()));
        tbTransportOrder.setState(vo.getState().name());
        tbTransportOrder.setCreationTime(LocalDateTime.ofInstant(vo.getCreationTime().plusMillis(TimeUnit.HOURS.toMillis(0)), ZoneId.systemDefault()));
        tbTransportOrder.setDeadline(LocalDateTime.ofInstant(vo.getDeadline().plusMillis(TimeUnit.HOURS.toMillis(0)), ZoneId.systemDefault()));
        tbTransportOrder.setFinishedTime(LocalDateTime.ofInstant(vo.getCreationTime().plusMillis(TimeUnit.HOURS.toMillis(0)), ZoneId.systemDefault()));
        return tbTransportOrder;
    }

    @Override
    public void updateTransportOrderIntendedVehicle(UpdateTransportOrderIntendedVehicleDTO dto) {
        TransportOrder transportOrder = transportOrderService.fetchObject(TransportOrder.class, dto.getName());
        Vehicle vehicle = vehicleService.fetchObject(Vehicle.class, dto.getVehicleName());
        transportOrderService.updateTransportOrderIntendedVehicle(transportOrder.getReference(), vehicle.getReference());
    }

    @Override
    public TbTransportOrder selectOneByNumber(String number) {
        return tbTransportOrderService.selectTbTransportOrderById(number);
    }

    private List<TbTransportOrder> selectByNumber(String number) {
        TbTransportOrder tbTransportOrder = new TbTransportOrder();
        tbTransportOrder.setName(number);
        return tbTransportOrderService.selectTbTransportOrderList(tbTransportOrder);
    }
}
