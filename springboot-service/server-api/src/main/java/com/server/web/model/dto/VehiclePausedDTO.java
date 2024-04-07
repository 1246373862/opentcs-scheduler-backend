package com.server.web.model.dto;

import lombok.Data;


public class VehiclePausedDTO {


  private String name;

  private boolean paused;

  public String getName() {
    return name;
  }

  public boolean isPaused() {
    return paused;
  }
}
