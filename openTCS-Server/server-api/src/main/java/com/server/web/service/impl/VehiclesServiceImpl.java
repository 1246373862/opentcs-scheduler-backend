package com.server.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.server.common.utils.http.HttpUtils;
import com.server.web.common.enums.VehicleProcStatusEnum;
import com.server.web.common.enums.VehicleStatusEnum;
import com.server.web.kernel.KernelServiceConfig;
import com.server.web.model.dto.VehicleInitDTO;
import com.server.web.model.dto.VehiclePausedDTO;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.model.vo.OrdersVO;
import com.server.web.model.vo.VehiclesInfoVO;
import com.server.web.service.VehiclesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opentcs.access.KernelServicePortal;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.virtualvehicle.commands.SetPositionCommand;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VehiclesServiceImpl
    implements VehiclesService {

private static final KernelServicePortal kernelServicePortal = KernelServiceConfig.getKernelServicePortal();

private static final VehicleService vehiclesService = kernelServicePortal.getVehicleService();
  @Override
  public List<VehiclesInfoVO> page(VehiclesQueryDTO vehiclesQueryDTO) {
    if (vehiclesQueryDTO.getName() != null && vehiclesQueryDTO.getProcStatus() != null) {
      List<VehiclesInfoVO> list = process(getALL());
      List<VehiclesInfoVO> res = new ArrayList<>();
      for (VehiclesInfoVO vehiclesInfoVO : list) {
        if (vehiclesInfoVO.getName().equals(vehiclesQueryDTO.getName()) && vehiclesInfoVO.getStatus().equals(vehiclesQueryDTO.getProcStatus())) {
          res.add(vehiclesInfoVO);
        }
      }
      return res;
    }
    else if (vehiclesQueryDTO.getName() != null) {
      List<VehiclesInfoVO> vehiclesInfoVOS = new ArrayList<>();
      try {
        VehiclesInfoVO vehiclesInfoVO = JSON.parseObject(HttpUtils.sendGet("http://localhost:55200/v1/vehicles/" + vehiclesQueryDTO.getName()), VehiclesInfoVO.class);
        vehiclesInfoVOS.add(vehiclesInfoVO);
        return process(vehiclesInfoVOS);
      }
      catch (NullPointerException e) {
        return new ArrayList<>();
      }

    }
    else if (vehiclesQueryDTO.getProcStatus() != null) {
      VehicleProcStatusEnum vehicleProcStatusEnum = VehicleProcStatusEnum.getVehicleProcStatusEnumByCode(vehiclesQueryDTO.getProcStatus());
      return process(JSON.parseArray(HttpUtils.sendGet("http://localhost:55200/v1/vehicles?procState=" + vehicleProcStatusEnum.getMessage()), VehiclesInfoVO.class));
    }
    else {
      return getALL();
    }

  }

  @Override
  public void initVehicle(VehicleInitDTO vehiclesQueryDTO) {
      //获取车辆实例
      Vehicle vehicle = vehiclesService.fetchObject(Vehicle.class,vehiclesQueryDTO.getName());
      vehiclesService.enableCommAdapter(vehicle.getReference());
      vehiclesService.sendCommAdapterCommand(vehicle.getReference(),new SetPositionCommand(vehiclesQueryDTO.getPoint()));
  }

  @Override
  public void paused(VehiclePausedDTO vehiclePausedDTO) {
    Vehicle vehicle = vehiclesService.fetchObject(Vehicle.class,vehiclePausedDTO.getName());
    vehiclesService.updateVehiclePaused(vehicle.getReference(),vehiclePausedDTO.isPaused());
  }

  private List<VehiclesInfoVO> process(List<VehiclesInfoVO> list) {
    for (VehiclesInfoVO vehiclesInfoVO : list) {
      VehicleProcStatusEnum vehicleProcStatusEnum = VehicleProcStatusEnum.getVehicleProcStatusEnumByMsg(vehiclesInfoVO.getProcState().toString());
      VehicleStatusEnum vehicleStatusEnum = VehicleStatusEnum.getVehicleStatusEnumByMsg(vehiclesInfoVO.getState().toString());
      vehiclesInfoVO.setProcStatus(vehicleProcStatusEnum.getCode());
      vehiclesInfoVO.setStatus(vehicleStatusEnum.getCode());
    }
    return list;
  }

  private List<VehiclesInfoVO> getALL() {
    return process(JSON.parseArray(HttpUtils.sendGet("http://localhost:55200/v1/vehicles"), VehiclesInfoVO.class));
  }

}

