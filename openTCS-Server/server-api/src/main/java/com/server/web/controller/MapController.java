package com.server.web.controller;

import com.server.common.core.controller.BaseController;
import com.server.common.core.domain.AjaxResult;
import com.server.common.core.page.TableDataInfo;
import com.server.web.service.MapService;
import com.server.web.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(value = "地图相关接口", tags = "地图相关接口")
@RequestMapping("/map")
public class MapController extends BaseController {

  @Resource
  private MapService mapService;

  @ApiOperation("获取所有point")
  @GetMapping("/points")
  public AjaxResult getPoints() {
    return AjaxResult.success(mapService.getPoints());
  }

  @ApiOperation("获取所有location")
  @GetMapping("/locations")
  public AjaxResult getLocations() {
    return AjaxResult.success(mapService.getLocations());
  }
}
