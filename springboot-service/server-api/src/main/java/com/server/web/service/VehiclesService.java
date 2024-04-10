package com.server.web.service;

import com.server.web.model.dto.VehicleInitDTO;
import com.server.web.model.dto.VehiclePausedDTO;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.model.vo.MyVehiclesVO;
import com.server.web.model.vo.VehiclesInfoVO;
import java.util.List;

public interface VehiclesService {

  List<MyVehiclesVO> page(VehiclesQueryDTO vehiclesQueryDTO);

  void initVehicle(VehicleInitDTO vehiclesQueryDTO);

  void paused(VehiclePausedDTO vehiclePausedDTO);
}
