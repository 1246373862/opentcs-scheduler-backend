package com.server.web.common.enums;

import lombok.Getter;

/**
 * 车辆任务状态枚举
 */
@Getter
public enum VehicleProcStatusEnum {
  UNAVAILABLE(0, "UNAVAILABLE"),
  IDLE(1,"IDLE"),
  AWAITING_ORDER(2,"AWAITING_ORDER"),
  PROCESSING_ORDER(3,"PROCESSING_ORDER"),
  ;
  /**
   * 状态码
   */
  private Integer code;

  /**
   * 状态名称
   */
  private String message;


  VehicleProcStatusEnum(Integer code, String message) {
    this.code = code;
    this.message = message;
  }


  /**
   * 通过枚举值获取方向类型枚举
   * @param code  枚举值
   * @return  方向类型枚举
   */
  public static VehicleProcStatusEnum getVehicleProcStatusEnumByCode(Integer code) {
    for (VehicleProcStatusEnum value : values()) {
      if (value.getCode().equals(code)) {
        return value;
      }
    }
    return null;
  }
  public static VehicleProcStatusEnum getVehicleProcStatusEnumByMsg(String msg) {
    for (VehicleProcStatusEnum value : values()) {
      if (value.getMessage().equals(msg)) {
        return value;
      }
    }
    return null;
  }

}
