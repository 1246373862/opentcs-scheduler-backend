package com.server.web.model.dto;

import lombok.Data;
import org.opentcs.access.to.order.DestinationCreationTO;

import java.util.List;

@Data
public class TransportOrderCreationDTO {

    private Boolean dispensable;

    private String vehicleName;

    private List<DestinationCreationDTO> destinations;
}
