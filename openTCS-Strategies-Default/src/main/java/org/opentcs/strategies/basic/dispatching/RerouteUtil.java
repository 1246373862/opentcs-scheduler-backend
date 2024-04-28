/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.dispatching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.opentcs.components.kernel.Router;
import org.opentcs.components.kernel.services.InternalTransportOrderService;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.ReroutingType;
import org.opentcs.data.order.Route;
import org.opentcs.data.order.Route.Step;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleController;
import org.opentcs.drivers.vehicle.VehicleControllerPool;
import org.opentcs.strategies.basic.dispatching.DefaultDispatcherConfiguration.ReroutingImpossibleStrategy;
import static org.opentcs.strategies.basic.dispatching.DefaultDispatcherConfiguration.ReroutingImpossibleStrategy.IGNORE_PATH_LOCKS;
import org.opentcs.strategies.basic.dispatching.rerouting.ReroutingStrategy;
import org.opentcs.strategies.basic.dispatching.rerouting.VehiclePositionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides some utility methods used for rerouting vehicles.
 */
public class RerouteUtil {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RerouteUtil.class);
  /**
   * The router.
   */
  private final Router router;
  /**
   * The vehicle controller pool.
   */
  private final VehicleControllerPool vehicleControllerPool;
  /**
   * The object service.
   */
  private final InternalTransportOrderService transportOrderService;

  private final DefaultDispatcherConfiguration configuration;

  private final Map<ReroutingType, ReroutingStrategy> reroutingStrategies;

  private final VehiclePositionResolver vehiclePositionResolver;

  /**
   * Creates a new instance.
   *
   * @param router The router.
   * @param vehicleControllerPool The vehicle controller pool.
   * @param transportOrderService The object service.
   * @param configuration The configuration.
   * @param reroutingStrategies The rerouting strategies to select from.
   * @param vehiclePositionResolver Used to resolve the position of vehicles.
   */
  @Inject
  public RerouteUtil(Router router,
                     VehicleControllerPool vehicleControllerPool,
                     InternalTransportOrderService transportOrderService,
                     DefaultDispatcherConfiguration configuration,
                     Map<ReroutingType, ReroutingStrategy> reroutingStrategies,
                     VehiclePositionResolver vehiclePositionResolver) {
    this.router = requireNonNull(router, "router");
    this.vehicleControllerPool = requireNonNull(vehicleControllerPool, "vehicleControllerPool");
    this.transportOrderService = requireNonNull(transportOrderService, "transportOrderService");
    this.configuration = requireNonNull(configuration, "configuration");
    this.reroutingStrategies = requireNonNull(reroutingStrategies, "reroutingStrategies");
    this.vehiclePositionResolver = requireNonNull(vehiclePositionResolver,
        "vehiclePositionResolver");
  }

  public void reroute(Collection<Vehicle> vehicles) {
    for (Vehicle vehicle : vehicles) {
      reroute(vehicle);
    }
  }

  public void reroute(Vehicle vehicle) {
    requireNonNull(vehicle, "vehicle");
    LOG.debug("Trying to reroute vehicle '{}'...", vehicle.getName());

    if (!vehicle.isProcessingOrder()) {
      LOG.warn("{} can't be rerouted without processing a transport order.", vehicle.getName());
      return;
    }

    TransportOrder originalOrder = transportOrderService.fetchObject(TransportOrder.class,
        vehicle.getTransportOrder());

    Point rerouteSource = getFutureOrCurrentPosition(vehicle);

    // Get all unfinished drive order of the transport order the vehicle is processing
    List<DriveOrder> unfinishedOrders = getUnfinishedDriveOrders(originalOrder);

    // Try to get a new route for the unfinished drive orders from the point
    Optional<List<DriveOrder>> optOrders = tryReroute(unfinishedOrders,
        vehicle,
        rerouteSource);

    // Get the drive order with the new route or stick to the old one
    List<DriveOrder> newDriveOrders;
    if (optOrders.isPresent()) {
      newDriveOrders = optOrders.get();
      LOG.debug("Found a new route for {} from point {}: {}",
          vehicle.getName(),
          rerouteSource.getName(),
          newDriveOrders);
    }
    else {
      LOG.debug("Couldn't find a new route for {}. Updating the current one...",
          vehicle.getName());
      unfinishedOrders = updatePathLocks(unfinishedOrders);
      unfinishedOrders
          = markRestrictedSteps(unfinishedOrders,
          new ExecutionTest(configuration.reroutingImpossibleStrategy(),
              rerouteSource));
      newDriveOrders = unfinishedOrders;
    }

    adjustFirstDriveOrder(newDriveOrders, vehicle, originalOrder, rerouteSource);

    LOG.debug("Updating transport order {}...", originalOrder.getName());
    updateTransportOrder(originalOrder, newDriveOrders, vehicle);
  }
  private void adjustFirstDriveOrder(List<DriveOrder> newDriveOrders,
                                     Vehicle vehicle,
                                     TransportOrder originalOrder,
                                     Point rerouteSource) {
    // If the vehicle is currently processing a (drive) order (and not waiting to get the next
    // drive order) we need to take care of it
    if (vehicle.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)) {
      if (isPointDestinationOfOrder(rerouteSource, originalOrder.getCurrentDriveOrder())) {
        // The current drive order could not get rerouted, because the vehicle already
        // received all commands for it. Therefore we want to keep the original drive order.
        newDriveOrders.set(0, originalOrder.getCurrentDriveOrder());
      }
      else {
        // Restore the current drive order's history
        DriveOrder newCurrentOrder = mergeDriveOrders(originalOrder.getCurrentDriveOrder(),
            newDriveOrders.get(0),
            vehicle);
        newDriveOrders.set(0, newCurrentOrder);
      }
    }
  }
  private List<Path> stepsToPaths(List<Step> steps) {
    return steps.stream()
        .map(step -> step.getPath())
        .collect(Collectors.toList());
  }

  private List<Step> mergeSteps(List<Step> stepsA, List<Step> stepsB) {
    LOG.debug("Merging steps {} with {}", stepsToPaths(stepsA), stepsToPaths(stepsB));

    // Get the step where routeB starts to depart, i.e. the step where routeA and routeB share the
    // same source point
    Step branchingStep = findStepWithSource(stepsB.get(0).getSourcePoint(), stepsA);

    int branchingIndex = stepsA.indexOf(branchingStep);
    List<Step> mergedSteps = new ArrayList<>();
    mergedSteps.addAll(stepsA.subList(0, branchingIndex));
    mergedSteps.addAll(stepsB);

    // Update the steps route indices since they originate from two different drive orders
    mergedSteps = updateRouteIndices(mergedSteps);

    return mergedSteps;
  }

  private List<Step> updateRouteIndices(List<Step> steps) {
    List<Step> updatedSteps = new ArrayList<>();
    for (int i = 0; i < steps.size(); i++) {
      Step currStep = steps.get(i);
      updatedSteps.add(new Step(currStep.getPath(),
          currStep.getSourcePoint(),
          currStep.getDestinationPoint(),
          currStep.getVehicleOrientation(),
          i,
          currStep.isExecutionAllowed()));
    }
    return updatedSteps;
  }
  private Step findStepWithSource(Point sourcePoint, List<Step> steps) {
    LOG.debug("Looking for a step with source point {} in {}",
        sourcePoint,
        stepsToPaths(steps));
    return steps.stream()
        .filter(step -> Objects.equals(step.getSourcePoint(), sourcePoint))
        .findFirst()
        .get();
  }

  private Route mergeRoutes(Vehicle vehicle, Route routeA, Route routeB) {
    // Merge the route steps
    List<Step> mergedSteps = mergeSteps(routeA.getSteps(), routeB.getSteps());

    // Calculate the costs for merged route
    Point sourcePoint = mergedSteps.get(0).getSourcePoint();
    Point destinationPoint = mergedSteps.get(mergedSteps.size() - 1).getDestinationPoint();
    long costs = router.getCosts(vehicle, sourcePoint, destinationPoint);

    return new Route(mergedSteps, costs);
  }
  public DriveOrder mergeDriveOrders(DriveOrder orderA, DriveOrder orderB, Vehicle vehicle) {
    // Merge the drive order routes
    Route mergedRoute = mergeRoutes(vehicle, orderA.getRoute(), orderB.getRoute());

    DriveOrder mergedOrder = new DriveOrder(orderA.getDestination())
        .withState(orderA.getState())
        .withTransportOrder(orderA.getTransportOrder())
        .withRoute(mergedRoute);

    return mergedOrder;
  }
  private boolean isPointDestinationOfOrder(Point point, DriveOrder order) {
    if (point == null || order == null) {
      return false;
    }
    if (order.getRoute() == null) {
      return false;
    }
    return Objects.equals(point, order.getRoute().getFinalDestinationPoint());
  }

  public Optional<List<DriveOrder>> tryReroute(List<DriveOrder> driveOrders,
                                               Vehicle vehicle,
                                               Point sourcePoint) {
    LOG.debug("Trying to reroute drive orders for {} from {}: {}",
        vehicle.getName(),
        sourcePoint,
        driveOrders);
    Optional<List<DriveOrder>> optDriveOrders = router.getRoute(vehicle,
        sourcePoint,
        new TransportOrder("reroute-dummy",
            driveOrders));
    return optDriveOrders;
  }

  public List<DriveOrder> getUnfinishedDriveOrders(TransportOrder order) {
    List<DriveOrder> result = new ArrayList<>();
    result.add(order.getCurrentDriveOrder());
    result.addAll(order.getFutureDriveOrders());
    return result;
  }
  public Point getFutureOrCurrentPosition(Vehicle vehicle) {
    VehicleController controller = vehicleControllerPool.getVehicleController(vehicle.getName());
    if (controller.getCommandsSent().isEmpty()) {
      return transportOrderService.fetchObject(Point.class, vehicle.getCurrentPosition());
    }

    List<MovementCommand> commandsSent = new ArrayList<>(controller.getCommandsSent());
    LOG.debug("Commands sent: {}", commandsSent);
    MovementCommand lastCommandSend = commandsSent.get(commandsSent.size() - 1);
    return lastCommandSend.getStep().getDestinationPoint();
  }
  private List<DriveOrder> updatePathLocksAndRestrictions(Vehicle vehicle,
                                                          TransportOrder originalOrder) {
    LOG.debug("Couldn't find a new route for {}. Updating the current one...",
        vehicle.getName());
    // Get all unfinished drive order of the transport order the vehicle is processing
    List<DriveOrder> unfinishedOrders = new ArrayList<>();
    unfinishedOrders.add(originalOrder.getCurrentDriveOrder());
    unfinishedOrders.addAll(originalOrder.getFutureDriveOrders());

    unfinishedOrders = updatePathLocks(unfinishedOrders);
    unfinishedOrders = markRestrictedSteps(
        unfinishedOrders,
        new ExecutionTest(configuration.reroutingImpossibleStrategy(),
            vehiclePositionResolver.getFutureOrCurrentPosition(vehicle))
    );
    return unfinishedOrders;
  }

  private void updateTransportOrder(TransportOrder originalOrder,
                                    List<DriveOrder> newDriveOrders,
                                    Vehicle vehicle) {
    VehicleController controller = vehicleControllerPool.getVehicleController(vehicle.getName());

    // Restore the transport order's history
    List<DriveOrder> newOrders = new ArrayList<>();
    newOrders.addAll(originalOrder.getPastDriveOrders());
    newOrders.addAll(newDriveOrders);

    // Update the transport order's drive orders with the re-routed ones
    LOG.debug("{}: Updating drive orders with {}.", originalOrder.getName(), newOrders);
    transportOrderService.updateTransportOrderDriveOrders(originalOrder.getReference(),
        newOrders);

    // If the vehicle is currently processing a (drive) order (and not waiting to get the next
    // drive order) we need to update the vehicle's current drive order with the new one.
    if (vehicle.hasProcState(Vehicle.ProcState.PROCESSING_ORDER)) {
      controller.setTransportOrder(
          transportOrderService.fetchObject(TransportOrder.class, originalOrder.getReference())
      );
    }

    // Let the router know the vehicle selected another route
    router.selectRoute(vehicle, newOrders);
  }

  private List<DriveOrder> updatePathLocks(List<DriveOrder> orders) {
    List<DriveOrder> updatedOrders = new ArrayList<>();

    for (DriveOrder order : orders) {
      List<Step> updatedSteps = new ArrayList<>();

      for (Step step : order.getRoute().getSteps()) {
        Path path = transportOrderService.fetchObject(Path.class, step.getPath().getReference());
        updatedSteps.add(new Route.Step(path,
            step.getSourcePoint(),
            step.getDestinationPoint(),
            step.getVehicleOrientation(),
            step.getRouteIndex()));
      }

      Route updatedRoute = new Route(updatedSteps, order.getRoute().getCosts());

      DriveOrder updatedOrder = new DriveOrder(order.getDestination())
          .withRoute(updatedRoute)
          .withState(order.getState())
          .withTransportOrder(order.getTransportOrder());
      updatedOrders.add(updatedOrder);
    }

    return updatedOrders;
  }

  private List<DriveOrder> markRestrictedSteps(List<DriveOrder> orders,
                                               Predicate<Step> executionTest) {
    if (configuration.reroutingImpossibleStrategy() == IGNORE_PATH_LOCKS) {
      return orders;
    }
    if (!containsLockedPath(orders)) {
      return orders;
    }

    List<DriveOrder> updatedOrders = new ArrayList<>();
    for (DriveOrder order : orders) {
      List<Step> updatedSteps = new ArrayList<>();

      for (Step step : order.getRoute().getSteps()) {
        boolean executionAllowed = executionTest.test(step);
        LOG.debug("Marking path '{}' allowed: {}", step.getPath(), executionAllowed);
        updatedSteps.add(new Step(step.getPath(),
            step.getSourcePoint(),
            step.getDestinationPoint(),
            step.getVehicleOrientation(),
            step.getRouteIndex(),
            executionAllowed));
      }

      Route updatedRoute = new Route(updatedSteps, order.getRoute().getCosts());

      DriveOrder updatedOrder = new DriveOrder(order.getDestination())
          .withRoute(updatedRoute)
          .withState(order.getState())
          .withTransportOrder(order.getTransportOrder());
      updatedOrders.add(updatedOrder);
    }

    return updatedOrders;
  }

  private boolean containsLockedPath(List<DriveOrder> orders) {
    return orders.stream()
        .map(order -> order.getRoute().getSteps())
        .flatMap(steps -> steps.stream())
        .anyMatch(step -> step.getPath().isLocked());
  }

  private class ExecutionTest
      implements Predicate<Step> {

    /**
     * The current fallback strategy.
     */
    private final ReroutingImpossibleStrategy strategy;
    /**
     * The (earliest) point from which execution may not be allowed.
     */
    private final Point source;
    /**
     * Whether execution of a step is allowed.
     */
    private boolean executionAllowed = true;

    /**
     * Creates a new intance.
     *
     * @param strategy The current fallback strategy.
     * @param source The (earliest) point from which execution may not be allowed.
     */
    ExecutionTest(ReroutingImpossibleStrategy strategy, Point source) {
      this.strategy = requireNonNull(strategy, "strategy");
      this.source = requireNonNull(source, "source");
    }

    @Override
    public boolean test(Step step) {
      if (!executionAllowed) {
        return false;
      }

      switch (strategy) {
        case PAUSE_IMMEDIATELY:
          if (Objects.equals(step.getSourcePoint(), source)) {
            executionAllowed = false;
          }
          break;
        case PAUSE_AT_PATH_LOCK:
          if (step.getPath().isLocked()) {
            executionAllowed = false;
          }
          break;
        default:
          executionAllowed = true;
      }

      return executionAllowed;
    }
  }
}
