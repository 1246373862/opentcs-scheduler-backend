/**
 * Copyright (c) The openTCS Authors.
 * <p>
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.routing.jgrapht;

import java.util.Collection;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;
import static org.opentcs.components.kernel.Router.PROPKEY_ROUTING_GROUP;

import java.util.Set;
import javax.annotation.Nonnull;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.opentcs.components.kernel.routing.Edge;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Group;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.routing.PointRouter;
import org.opentcs.strategies.basic.routing.PointRouterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates {@link PointRouter} instances with algorithm implementations created by subclasses.
 */
public abstract class AbstractPointRouterFactory
    implements PointRouterFactory {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(AbstractPointRouterFactory.class);
  /**
   * The object service providing the model data.
   */
  private final TCSObjectService objectService;
  /**
   * Maps the plant model to a graph.
   */
  private final ModelGraphMapper mapper;

  /**
   * Creates a new instance.
   *
   * @param objectService The object service providing model data.
   * @param mapper        Maps the plant model to a graph.
   */
  public AbstractPointRouterFactory(@Nonnull TCSObjectService objectService,
                                    @Nonnull ModelGraphMapper mapper) {
    this.objectService = requireNonNull(objectService, "objectService");
    this.mapper = requireNonNull(mapper, "mapper");
  }

  @Override
  public PointRouter createPointRouter(Vehicle vehicle) {
    requireNonNull(vehicle, "vehicle");

//    long timeStampBefore = System.currentTimeMillis();
//
//    Set<Point> points = objectService.fetchObjects(Point.class);
//    Graph<String, Edge> graph = mapper.translateModel(points,
//        objectService.fetchObjects(Path.class),
//        vehicle);
//    PointRouter router = new ShortestPathPointRouter(createShortestPathAlgorithm(graph), points);
////    PointRouter router = createPointRouterInternal(vehicle, points);
//    // Make a single request for a route from one point to a different one to make sure the
//    // point router is primed. (Some implementations are initialized lazily.)
//    if (points.size() >= 2) {
//      Iterator<Point> pointIter = points.iterator();
//      router.getRouteSteps(pointIter.next(), pointIter.next());
//    }
//
//    LOG.debug("Created point router for {} in {} milliseconds.",
//        vehicle.getName(),
//        System.currentTimeMillis() - timeStampBefore);
//
//    return router;
    long timeStampBefore = System.currentTimeMillis();

    Set<Point> points = objectService.fetchObjects(Point.class);


    PointRouter router = createPointRouterInternal(vehicle, points);


    LOG.debug("Created point router for {} in {} milliseconds.",
            vehicle.getName(),
            System.currentTimeMillis() - timeStampBefore);

    return router;
  }

  @Override
  public PointRouter createMyPointRouter(
      Vehicle vehicle, Collection<Point> unusablePoints) {
    requireNonNull(vehicle, "vehicle");
    requireNonNull(unusablePoints, "unusablePoints");
    long timeStampBefore = System.currentTimeMillis();
    Set<Point> points = objectService.fetchObjects(Point.class);
    for (Point pt : unusablePoints) {
      if (points.contains(pt)) {
        points.remove(pt);
      }
    }
    PointRouter router = createPointRouterInternal(vehicle, points);

    LOG.debug("Created point router for {} in {} milliseconds.",
            vehicle.getName(),
            System.currentTimeMillis() - timeStampBefore);

    return router;
  }

  private static final String DEFAULT_ROUTING_GROUP = "";
  private String getRoutingGroupOfVehicle(Vehicle vehicle) {
    String propVal = vehicle.getProperty(PROPKEY_ROUTING_GROUP);
    return propVal == null ? DEFAULT_ROUTING_GROUP : propVal.strip();
  }
  private PointRouter createPointRouterInternal(
          Vehicle vehicle,
          Collection<Point> points)
  {
    boolean usingGroupFilter = true;
    String groupName = getRoutingGroupOfVehicle(vehicle);

    if (groupName.isEmpty()) {
      usingGroupFilter = false;
    }
    if (usingGroupFilter) {
      Set<Group> allGroups = objectService.fetchObjects(Group.class);
      for (Group group: allGroups) {
        if (group.getName().equals(groupName)) {
          Set<TCSObjectReference<?>> members = group.getMembers();
          break;
        }
      }
    }

    Set<Path> allPaths = objectService.fetchObjects(Path.class);
    allPaths.removeIf(
        value->(
            points.contains(value.getSourcePoint())
                || points.contains(value.getDestinationPoint())));
    Graph<String, Edge> graph = mapper.translateModel(points, allPaths, vehicle);

    PointRouter router = new ShortestPathPointRouter(createShortestPathAlgorithm(graph), points);
    // Make a single request for a route from one point to a different one to make sure the
    // point router is primed. (Some implementations are initialized lazily.)
    if (points.size() >= 2) {
      Iterator<Point> pointIter = points.iterator();
      router.getRouteSteps(
          pointIter.next(),
          pointIter.next());
    }

    return router;
  }
  /**
   * Returns a shortest path algorithm implementation working on the given graph.
   *
   * @param graph The graph.
   * @return A shortest path algorithm implementation working on the given graph.
   */
  protected abstract ShortestPathAlgorithm<String, Edge> createShortestPathAlgorithm(
      Graph<String, Edge> graph);
}
