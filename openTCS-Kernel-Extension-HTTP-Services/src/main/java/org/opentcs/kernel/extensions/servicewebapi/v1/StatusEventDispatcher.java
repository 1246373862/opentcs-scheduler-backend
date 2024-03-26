/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1;

import java.util.Collection;
import static java.util.Objects.requireNonNull;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.inject.Inject;
import org.opentcs.access.Kernel;
import org.opentcs.access.KernelStateTransitionEvent;
import org.opentcs.components.Lifecycle;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.data.peripherals.PeripheralJob;
import org.opentcs.kernel.extensions.servicewebapi.ServiceWebApiConfiguration;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.GetEventsResponseTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.getevents.OrderStatusMessage;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.getevents.PeripheralJobStatusMessage;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.getevents.StatusMessage;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.getevents.VehicleStatusMessage;
import static org.opentcs.util.Assertions.checkInRange;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides descriptions of recent events.
 */
public class StatusEventDispatcher
    implements Lifecycle,
               EventHandler {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(StatusEventDispatcher.class);
  /**
   * The interface configuration.
   */
  private final ServiceWebApiConfiguration configuration;
  /**
   * Where we register for application events.
   */
  private final EventSource eventSource;
  /**
   * The events collected.
   */
  private final SortedMap<Long, StatusMessage> events = new TreeMap<>();
  /**
   * The number of events collected so far.
   */
  private long eventCount;
  /**
   * Whether this instance is initialized.
   */
  private boolean initialized;
  /**
   * Whether we are collecting events.
   */
  private boolean eventCollectingOn;

  @Inject
  public StatusEventDispatcher(ServiceWebApiConfiguration configuration,
                               @ApplicationEventBus EventSource eventSource) {
    this.configuration = requireNonNull(configuration, "configuration");
    this.eventSource = requireNonNull(eventSource, "eventSource");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    eventSource.subscribe(this);

    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }

    eventSource.unsubscribe(this);

    initialized = false;
  }

  @Override
  public void onEvent(Object event) {
    if (event instanceof KernelStateTransitionEvent) {
      handleStateTransition((KernelStateTransitionEvent) event);
    }

    if (!eventCollectingOn) {
      return;
    }

    if (event instanceof TCSObjectEvent) {
      handleObjectEvent((TCSObjectEvent) event);
    }
  }

  /**
   * Provides a list of events within the given range, waiting at most <code>timeout</code>
   * milliseconds for new events if there currently aren't any.
   *
   * @param minSequenceNo The minimum sequence number for accepted events.
   * @param maxSequenceNo The maximum sequence number for accepted events.
   * @param timeout The maximum time to wait for events (in ms) if there currently aren't any.
   * @return A list of events within the given range.
   */
  public GetEventsResponseTO fetchEvents(long minSequenceNo, long maxSequenceNo, long timeout)
      throws IllegalArgumentException {
    checkInRange(minSequenceNo, 0, Long.MAX_VALUE, "minSequenceNo");
    checkInRange(maxSequenceNo, minSequenceNo, Long.MAX_VALUE, "maxSequenceNo");
    checkInRange(timeout, 0, Long.MAX_VALUE, "timeout");

    GetEventsResponseTO result = new GetEventsResponseTO();
    synchronized (events) {
      Collection<StatusMessage> messages = events.subMap(minSequenceNo, maxSequenceNo).values();
      if (messages.isEmpty()) {
        try {
          events.wait(timeout);
        }
        catch (InterruptedException exc) {
          LOG.warn("Unexpectedly interrupted", exc);
        }
      }
      messages = events.subMap(minSequenceNo, maxSequenceNo).values();
      result.getStatusMessages().addAll(messages);
    }
    return result;
  }

  private void handleStateTransition(KernelStateTransitionEvent event) {
    boolean wasOn = eventCollectingOn;
    eventCollectingOn
        = event.getEnteredState() == Kernel.State.OPERATING && event.isTransitionFinished();

    // When switching collecting of events on, ensure we start clean.
    if (!wasOn && eventCollectingOn) {
      synchronized (events) {
        eventCount = 0;
        events.clear();
      }
    }
  }

  private void handleObjectEvent(TCSObjectEvent event) {
    TCSObject<?> object = event.getCurrentOrPreviousObjectState();
    if (object instanceof TransportOrder) {
      synchronized (events) {
        addOrderStatusMessage((TransportOrder) object, eventCount);
        eventCount++;
        cleanUpEvents();
        events.notifyAll();
      }
    }
    else if (object instanceof Vehicle) {
      synchronized (events) {
        addVehicleStatusMessage((Vehicle) object, eventCount);
        eventCount++;
        cleanUpEvents();
        events.notifyAll();
      }
    }
    else if (object instanceof PeripheralJob) {
      synchronized (events) {
        addPeripheralStatusMessage((PeripheralJob) object, eventCount);
        eventCount++;
        cleanUpEvents();
        events.notifyAll();
      }
    }
  }

  private void addOrderStatusMessage(TransportOrder order, long sequenceNumber) {
    events.put(sequenceNumber, OrderStatusMessage.fromTransportOrder(order, sequenceNumber));
  }

  private void addVehicleStatusMessage(Vehicle vehicle, long sequenceNumber) {
    events.put(sequenceNumber, VehicleStatusMessage.fromVehicle(vehicle, sequenceNumber));
  }

  private void addPeripheralStatusMessage(PeripheralJob job, long sequenceNumber) {
    events.put(sequenceNumber, PeripheralJobStatusMessage.fromPeripheralJob(job, sequenceNumber));
  }

  private void cleanUpEvents() {
    // XXX Sanitize maxEventCount
    int maxEventCount = configuration.statusEventsCapacity();
    while (events.size() > maxEventCount) {
      events.remove(events.firstKey());
    }
  }
}
