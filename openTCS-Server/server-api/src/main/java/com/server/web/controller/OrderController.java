package com.server.web.controller;

import com.server.common.core.controller.BaseController;
import com.server.common.core.page.TableDataInfo;
import com.server.web.model.dto.VehiclesQueryDTO;
import com.server.web.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(value = "订单管理相关接口", tags = "订单管理相关接口")
@RequestMapping("/order")
public class OrderController extends BaseController {

  @Resource
  private OrderService orderService;
  @ApiOperation("分页获取订单列表")
  @GetMapping("/page")
  public TableDataInfo page() {
    startPage();
    return getDataTable(orderService.getOrders());
  }
}
