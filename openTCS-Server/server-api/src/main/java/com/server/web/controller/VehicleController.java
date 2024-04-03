package com.server.web.controller;

import com.server.common.core.controller.BaseController;
import com.server.common.core.domain.AjaxResult;
import com.server.common.core.page.TableDataInfo;
import com.server.web.model.dto.VehicleInitDTO;
import com.server.web.model.dto.VehiclePausedDTO;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.service.VehiclesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.opentcs.access.KernelServicePortal;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "车辆管理相关接口", tags = "车辆管理相关接口")
@RequestMapping("/vehicle")
public class VehicleController
    extends BaseController {
@Resource
private VehiclesService vehiclesService;


  @ApiOperation("分页获取车辆列表")
  @GetMapping("/page")
  public TableDataInfo page(VehiclesQueryDTO vehiclesQueryDTO) {
    startPage();
    return getDataTable(vehiclesService.page(vehiclesQueryDTO));
  }

  @ApiOperation("开启适配器使能和初始化位置")
  @PostMapping("/init")
  public AjaxResult initVehicle(@RequestBody VehicleInitDTO vehiclesQueryDTO) {
    vehiclesService.initVehicle(vehiclesQueryDTO);
    return AjaxResult.success();
  }


  @ApiOperation("恢复或暂停车辆")
  @PostMapping("/paused")
  public AjaxResult paused(@RequestBody VehiclePausedDTO vehiclePausedDTO) {
    vehiclesService.paused(vehiclePausedDTO);
    return AjaxResult.success();
  }
}
