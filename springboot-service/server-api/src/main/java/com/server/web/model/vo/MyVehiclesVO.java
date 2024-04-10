package com.server.web.model.vo;

import lombok.Data;
import org.opentcs.data.model.Vehicle;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.GetVehicleResponseTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MyVehiclesVO {


  private String name;

  private Map<String, String> properties = new HashMap<>();

  private int length;

  private int energyLevelGood;

  private int energyLevelCritical;

  private int energyLevel;

  private Vehicle.IntegrationLevel integrationLevel = Vehicle.IntegrationLevel.TO_BE_RESPECTED;

  private boolean paused;

  private Vehicle.ProcState procState = Vehicle.ProcState.IDLE;

  private Integer procStatus;

  private String transportOrder;

  private String currentPosition;

  private GetVehicleResponseTO.PrecisePosition precisePosition;

  private Vehicle.State state = Vehicle.State.UNKNOWN;

  private Integer status;

  private List<RoutePath> allocatedResources = new ArrayList<>();

  private List<RoutePath> claimedResources = new ArrayList<>();

  private List<String> allowedOrderTypes = new ArrayList<>();

  private String envelopeKey;
}
