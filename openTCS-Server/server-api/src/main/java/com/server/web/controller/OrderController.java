package com.server.web.controller;

import com.server.common.core.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "订单管理", tags = "订单管理")
@RequestMapping("/order")
public class OrderController extends BaseController {

}
