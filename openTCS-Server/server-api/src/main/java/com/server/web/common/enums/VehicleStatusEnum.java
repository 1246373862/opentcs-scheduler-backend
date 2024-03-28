package com.server.web.common.enums;

import lombok.Getter;

/**
 * 车辆运行状态枚举
 */
@Getter
public enum VehicleStatusEnum {
  UNKNOWN(0, "UNKNOWN"),
  UNAVAILABLE(1,"UNAVAILABLE"),
  ERROR(2,"ERROR"),
  IDLE(3,"IDLE"),
  EXECUTING(4,"EXECUTING"),
  CHARGING(5,"CHARGING"),
      ;
  /**
   * 状态码
   */
  private Integer code;

  /**
   * 状态名称
   */
  private String message;


  VehicleStatusEnum(Integer code, String message) {
    this.code = code;
    this.message = message;
  }


  /**
   * 通过枚举值获取方向类型枚举
   * @param code  枚举值
   * @return  方向类型枚举
   */
  public static VehicleStatusEnum getVehicleStatusEnumByCode(Integer code) {
    for (VehicleStatusEnum value : values()) {
      if (value.getCode().equals(code)) {
        return value;
      }
    }
    return null;
  }
  public static VehicleStatusEnum getVehicleStatusEnumByMsg(String msg) {
    for (VehicleStatusEnum value : values()) {
      if (value.getMessage().equals(msg)) {
        return value;
      }
    }
    return null;
  }
}
