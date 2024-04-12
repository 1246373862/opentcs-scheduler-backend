package com.server.web.service.impl;

import com.server.web.kernel.KernelServiceConfig;
import com.server.web.service.MapService;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.components.kernel.services.PlantModelService;
import org.opentcs.components.kernel.services.RouterService;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.PlantModel;
import org.opentcs.data.model.Point;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
public class MapServiceImpl implements MapService {

  private static final KernelServicePortal kernelServicePortal = KernelServiceConfig.getKernelServicePortal();


  @Override
  public Set<Point> getPoints() {
    PlantModelService plantModelService = kernelServicePortal.getPlantModelService();
    Set<Point> res = plantModelService.fetchObjects(Point.class);
    return res;
  }

  @Override
  public Set<Location> getLocations() {
    PlantModelService plantModelService = kernelServicePortal.getPlantModelService();
    return plantModelService.fetchObjects(Location.class);
  }

  @Override
  public PlantModel getMap() {
    PlantModelService plantModelService = kernelServicePortal.getPlantModelService();
    PlantModel map = plantModelService.getPlantModel();
    return map;
  }
}
