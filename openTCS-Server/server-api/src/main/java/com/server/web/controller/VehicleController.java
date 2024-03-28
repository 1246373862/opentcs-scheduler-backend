package com.server.web.controller;

import com.server.common.core.controller.BaseController;
import com.server.common.core.page.TableDataInfo;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.service.VehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.opentcs.access.KernelServicePortal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "车辆管理", tags = "车辆管理")
@RequestMapping("/vehicle")
public class VehicleController
    extends BaseController {
@Resource
private VehicleService vehicleService;
  @Resource
  private KernelServicePortal kernelServicePortal;

  @ApiOperation("分页获取车辆列表")
  @GetMapping("/page")
  public TableDataInfo page(VehiclesQueryDTO vehiclesQueryDTO) {
    startPage();
    return getDataTable(vehicleService.page(vehiclesQueryDTO));
  }

}
