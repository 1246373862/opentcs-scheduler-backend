package com.server.system.domain;

import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.common.annotation.Excel;
import com.server.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 订单表对象 tb_transport_order
 *
 * @author Ying
 * @date 2024-04-11
 */
@Data
public class TbTransportOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String name;

    /** 能否撤回 */
    @Excel(name = "能否撤回")
    private Boolean dispensable;

    /** 订单类型 */
    @Excel(name = "订单类型")
    private String type;

    /** 订单状态 */
    @Excel(name = "订单状态")
    private String state;

    /** 预定执行车辆 */
    @Excel(name = "预定执行车辆")
    private String intendedVehicle;

    /** 实际执行车辆 */
    @Excel(name = "实际执行车辆")
    private String processingVehicle;

    /** 目的地 */
    @Excel(name = "目的地")
    private String destinations;

    /** 创建时间 */

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime creationTime;

    /** 截止时间 */

    @Excel(name = "截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime deadline;

    /** 完成时间 */

    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime finishedTime;


}
