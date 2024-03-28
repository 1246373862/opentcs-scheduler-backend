package com.server.web.service;

import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.model.vo.VehiclesInfoVO;
import java.util.List;

public interface VehicleService {

  List<VehiclesInfoVO> page(VehiclesQueryDTO vehiclesQueryDTO);
}
