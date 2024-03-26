/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.routing.jgrapht;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.opentcs.components.kernel.routing.Edge;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.Route;
import org.opentcs.strategies.basic.routing.PointRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computes routes between points using a JGraphT-based shortest path algorithm.
 * <p>
 * <em>Note that this implementation does not integrate static routes.</em>
 * </p>
 */
public class ShortestPathPointRouter
    implements PointRouter {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ShortestPathPointRouter.class);

  private final ShortestPathAlgorithm<String, Edge> algo;

  private final Map<String, Point> points = new HashMap<>();

  public ShortestPathPointRouter(ShortestPathAlgorithm<String, Edge> algo,
                                 Collection<Point> points) {
    this.algo = requireNonNull(algo, "algo");
    requireNonNull(points, "points");

    for (Point point : points) {
      this.points.put(point.getName(), point);
    }

  }

  @Override
  public List<Route.Step> getRouteSteps(Point srcPoint, Point destPoint) {
    requireNonNull(srcPoint, "srcPoint");
    requireNonNull(destPoint, "destPoint");

    long timeBefore = System.currentTimeMillis();
    if (Objects.equals(srcPoint.getName(), destPoint.getName())) {
      return new ArrayList<>();
    }

    GraphPath<String, Edge> graphPath = algo.getPath(srcPoint.getName(), destPoint.getName());
    if (graphPath == null) {
      return null;
    }

    List<Route.Step> result = translateToSteps(graphPath);

    LOG.debug("Looking up route from {} to {} took {} milliseconds.",
              srcPoint.getName(),
              destPoint.getName(),
              System.currentTimeMillis() - timeBefore);

    return result;
  }

  @Override
  public long getCosts(TCSObjectReference<Point> srcPointRef,
                       TCSObjectReference<Point> destPointRef) {
    requireNonNull(srcPointRef, "srcPointRef");
    requireNonNull(destPointRef, "destPointRef");

    if (Objects.equals(srcPointRef.getName(), destPointRef.getName())) {
      return 0;
    }

    GraphPath<String, Edge> graphPath = algo.getPath(srcPointRef.getName(),
                                                          destPointRef.getName());
    if (graphPath == null) {
      return INFINITE_COSTS;
    }

    return (long) graphPath.getWeight();
  }

  private List<Route.Step> translateToSteps(GraphPath<String, Edge> graphPath) {
    List<Edge> edges = graphPath.getEdgeList();
    List<Route.Step> result = new ArrayList<>(edges.size());

    int routeIndex = 0;
    for (Edge edge : edges) {
      Point sourcePoint = points.get(graphPath.getGraph().getEdgeSource(edge));
      Point destPoint = points.get(graphPath.getGraph().getEdgeTarget(edge));

      result.add(new Route.Step(edge.getPath(),
                                sourcePoint,
                                destPoint,
                                orientation(edge, sourcePoint),
                                routeIndex));
      routeIndex++;
    }

    return result;
  }

  private Vehicle.Orientation orientation(Edge edge, Point graphSourcePoint) {
    return Objects.equals(edge.getPath().getSourcePoint(), graphSourcePoint.getReference())
        ? Vehicle.Orientation.FORWARD
        : Vehicle.Orientation.BACKWARD;
  }
}
