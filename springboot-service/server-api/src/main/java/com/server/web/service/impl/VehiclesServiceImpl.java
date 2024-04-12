package com.server.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.server.common.utils.bean.BeanUtils;
import com.server.common.utils.http.HttpUtils;
import com.server.web.common.enums.VehicleProcStatusEnum;
import com.server.web.common.enums.VehicleStatusEnum;
import com.server.web.kernel.KernelServiceConfig;
import com.server.web.model.dto.VehicleInitDTO;
import com.server.web.model.dto.VehiclePausedDTO;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.model.vo.MyVehiclesVO;
import com.server.web.model.vo.OrdersVO;
import com.server.web.model.vo.RoutePath;
import com.server.web.model.vo.VehiclesInfoVO;
import com.server.web.service.VehiclesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentcs.access.KernelServicePortal;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.model.TCSResourceReference;
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
  public List<MyVehiclesVO> page(VehiclesQueryDTO vehiclesQueryDTO) {
    if (vehiclesQueryDTO.getName() != null && vehiclesQueryDTO.getProcStatus() != null) {
      List<VehiclesInfoVO> list = process(getALL());
      List<VehiclesInfoVO> res = new ArrayList<>();
      for (VehiclesInfoVO vehiclesInfoVO : list) {
        if (vehiclesInfoVO.getName().equals(vehiclesQueryDTO.getName()) && vehiclesInfoVO.getStatus().equals(vehiclesQueryDTO.getProcStatus())) {
          res.add(vehiclesInfoVO);
        }
      }
      return convert(res);
    } else if (vehiclesQueryDTO.getName() != null) {
      List<VehiclesInfoVO> vehiclesInfoVOS = new ArrayList<>();
      try {
        VehiclesInfoVO vehiclesInfoVO = JSON.parseObject(HttpUtils.sendGet("http://localhost:55200/v1/vehicles/" + vehiclesQueryDTO.getName()), VehiclesInfoVO.class);
        vehiclesInfoVOS.add(vehiclesInfoVO);
        return convert(process(vehiclesInfoVOS));
      } catch (NullPointerException e) {
        return new ArrayList<>();
      }

    } else if (vehiclesQueryDTO.getProcStatus() != null) {
      VehicleProcStatusEnum vehicleProcStatusEnum = VehicleProcStatusEnum.getVehicleProcStatusEnumByCode(vehiclesQueryDTO.getProcStatus());
      return convert(process(JSON.parseArray(HttpUtils.sendGet("http://localhost:55200/v1/vehicles?procState=" + vehicleProcStatusEnum.getMessage()), VehiclesInfoVO.class)));
    } else {
      return convert(getALL());
    }

  }

  @Override
  public void initVehicle(VehicleInitDTO vehiclesQueryDTO) {
    //获取车辆实例
    Vehicle vehicle = vehiclesService.fetchObject(Vehicle.class, vehiclesQueryDTO.getName());
    vehiclesService.enableCommAdapter(vehicle.getReference());
    vehiclesService.sendCommAdapterCommand(vehicle.getReference(), new SetPositionCommand(vehiclesQueryDTO.getPoint()));
    vehiclesService.updateVehicleIntegrationLevel(vehicle.getReference(), Vehicle.IntegrationLevel.TO_BE_UTILIZED);
  }

  @Override
  public void paused(VehiclePausedDTO vehiclePausedDTO) {
    Vehicle vehicle = vehiclesService.fetchObject(Vehicle.class, vehiclePausedDTO.getName());
    vehiclesService.updateVehiclePaused(vehicle.getReference(), vehiclePausedDTO.isPaused());
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
    Set<Vehicle> vehicles = vehiclesService.fetchObjects(Vehicle.class);
    return process(vehicles.stream().map(vehicle -> {
      VehiclesInfoVO vehiclesInfoVO = new VehiclesInfoVO();
      BeanUtils.copyProperties(vehicle, vehiclesInfoVO);
      for (Set<TCSResourceReference<?>> str:vehicle.getAllocatedResources()){
        List<String> list = new ArrayList<>();
        for (TCSResourceReference<?> str1:str){
          list.add(str1.getName());
        }
        vehiclesInfoVO.getAllocatedResources().add(list);
      }
      for (Set<TCSResourceReference<?>> str:vehicle.getClaimedResources()){
        List<String> list = new ArrayList<>();
        for (TCSResourceReference<?> str1:str){
          list.add(str1.getName());
        }
        vehiclesInfoVO.getClaimedResources().add(list);
      }
      if(vehicle.getCurrentPosition()!=null){
        vehiclesInfoVO.setCurrentPosition(vehicle.getCurrentPosition().getName());
      }

      return vehiclesInfoVO;
    }).collect(Collectors.toList()));
  }

  /**
   * 路由资源转换
   * @param origin
   * @return
   */

  private List<MyVehiclesVO> convert(List<VehiclesInfoVO> origin){
    List<MyVehiclesVO> res = new ArrayList<>();
    for (VehiclesInfoVO vo:origin){
      MyVehiclesVO myVehiclesVO = new MyVehiclesVO();
      BeanUtils.copyProperties(vo,myVehiclesVO);
      res.add(myVehiclesVO);
      for (List<String> allocatedResources:vo.getAllocatedResources()){
        for (String allocatedResource:allocatedResources){
          if(allocatedResource.contains("---")){
            String[] split = allocatedResource.split(" --- ");
            RoutePath routePath = new RoutePath();
            routePath.setStart(split[0]);
            routePath.setEnd(split[1]);
            myVehiclesVO.getAllocatedResources().add(routePath);
            break;
          }
        }
      }
      for (List<String> claimedResources:vo.getClaimedResources()){
        for (String claimedResource:claimedResources){
          if(claimedResource.contains("---")){
            String[] split = claimedResource.split(" --- ");
            RoutePath routePath = new RoutePath();
            routePath.setStart(split[0]);
            routePath.setEnd(split[1]);
            myVehiclesVO.getClaimedResources().add(routePath);
          }
        }
      }
    }
    return res;
  }
}

