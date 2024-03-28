/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.vehicles;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Queue;
import java.util.Set;
import javax.annotation.Nonnull;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.TCSResource;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleController;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Null-object implementation of {@link VehicleController}.
 */
public class NullVehicleController
    implements VehicleController {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(NullVehicleController.class);
  /**
   * The associated vehicle's name.
   */
  private final String vehicleName;

  /**
   * Creates a new instance.
   *
   * @param vehicleName The associated vehicle's name.
   */
  public NullVehicleController(@Nonnull String vehicleName) {
    this.vehicleName = requireNonNull(vehicleName, "vehicleName");
  }

  @Override
  public void initialize() {
  }

  @Override
  public boolean isInitialized() {
    return true;
  }

  @Override
  public void terminate() {
  }

  @Override
  public void setTransportOrder(TransportOrder newOrder) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  @Deprecated
  public void setDriveOrder(DriveOrder newOrder, Map<String, String> orderProperties) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  @Deprecated
  public void updateDriveOrder(DriveOrder newOrder, Map<String, String> orderProperties) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  public void abortTransportOrder(boolean immediate) {
    if (immediate) {
      clearDriveOrder();
    }
    else {
      abortDriveOrder();
    }
  }

  @Override
  @Deprecated
  public void clearDriveOrder() {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  @Deprecated
  public void abortDriveOrder() {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  @Deprecated
  public void clearCommandQueue() {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  public ExplainedBoolean canProcess(TransportOrder order) {
    return new ExplainedBoolean(false, "NullVehicleController");
  }

  @Override
  @Deprecated
  public ExplainedBoolean canProcess(List<String> operations) {
    return new ExplainedBoolean(false, "NullVehicleController");
  }

  @Override
  public void sendCommAdapterMessage(Object message) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  public void sendCommAdapterCommand(AdapterCommand command) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  public String getId() {
    return vehicleName;
  }

  @Override
  public TCSObjectReference<Vehicle> getRelatedVehicle() {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
    return null;
  }

  @Override
  public boolean allocationSuccessful(Set<TCSResource<?>> resources) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
    return false;
  }

  @Override
  public void allocationFailed(Set<TCSResource<?>> resources) {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
  }

  @Override
  public Queue<MovementCommand> getCommandsSent() {
    LOG.warn("No comm adapter attached to vehicle {}", vehicleName);
    return new ArrayDeque<>();
  }
}
