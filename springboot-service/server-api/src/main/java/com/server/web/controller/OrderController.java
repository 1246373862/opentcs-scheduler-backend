package com.server.web.controller;

import com.server.common.core.controller.BaseController;
import com.server.common.core.domain.AjaxResult;
import com.server.common.core.page.TableDataInfo;
import com.server.web.model.dto.TransportOrderCreationDTO;
import com.server.web.model.dto.UpdateTransportOrderIntendedVehicleDTO;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(value = "订单管理相关接口", tags = "订单管理相关接口")
@RequestMapping("/order")
public class OrderController extends BaseController {

  @Resource
  private OrderService orderService;
  @ApiOperation("分页获取订单列表")
  @GetMapping("/page")
  public TableDataInfo page(String name) {
    startPage();
    return getDataTable(orderService.getOrders(name));
  }

  @ApiOperation("根据编号查询订单")
  @GetMapping("/select")
  public AjaxResult selectOneByNumber(String name) {
    return AjaxResult.success(orderService.selectOneByNumber(name));
  }

  @ApiOperation("撤回订单")
  @PutMapping("/withdraw")
  public AjaxResult withdrawOrder(@RequestBody  String number) {
    orderService.withdrawByOrderId(number);
    return AjaxResult.success();
  }
  @ApiOperation("创建订单")
  @PostMapping("/create")
  public AjaxResult createOrder(@RequestBody TransportOrderCreationDTO creationDTO) {
    orderService.createOrder(creationDTO);
    return AjaxResult.success();
  }
  @ApiOperation("修改订单的预执行车辆")
  @PutMapping("/update")
  public AjaxResult update(@RequestBody UpdateTransportOrderIntendedVehicleDTO dto) {
    orderService.updateTransportOrderIntendedVehicle(dto);
    return AjaxResult.success();
  }
}
