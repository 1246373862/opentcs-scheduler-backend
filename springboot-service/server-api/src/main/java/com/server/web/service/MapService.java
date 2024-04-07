package com.server.web.service;

import org.opentcs.data.model.Location;
import org.opentcs.data.model.Point;

import java.awt.*;
import java.util.List;
import java.util.Set;

public interface MapService {

  Set<Point> getPoints();

  Set<Location> getLocations();
}
