package com.server.web.model.vo;

import lombok.Data;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.shared.DestinationState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Data
public class OrdersVO {

  private boolean dispensable;

  private String name = "";

  private String peripheralReservationToken;

  private String wrappingSequence;

  private String type = "";

  private TransportOrder.State state = TransportOrder.State.RAW;

  private String intendedVehicle;

  private String processingVehicle;

  private List<DestinationState> destinations = new ArrayList<>();

  public OrdersVO() {
  }

  public boolean isDispensable() {
    return dispensable;
  }

  public OrdersVO setDispensable(boolean dispensable) {
    this.dispensable = dispensable;
    return this;
  }

  public String getName() {
    return name;
  }

  public OrdersVO setName(String name) {
    this.name = requireNonNull(name, "name");
    return this;
  }

  public String getPeripheralReservationToken() {
    return peripheralReservationToken;
  }

  public OrdersVO setPeripheralReservationToken(
      String peripheralReservationToken) {
    this.peripheralReservationToken = peripheralReservationToken;
    return this;
  }

  public String getWrappingSequence() {
    return wrappingSequence;
  }

  public OrdersVO setWrappingSequence(String wrappingSequence) {
    this.wrappingSequence = wrappingSequence;
    return this;
  }

  public String getType() {
    return type;
  }

  public OrdersVO setType(String type) {
    this.type = type;
    return this;
  }

  public TransportOrder.State getState() {
    return state;
  }

  public OrdersVO setState(TransportOrder.State state) {
    this.state = requireNonNull(state, "state");
    return this;
  }

  public String getIntendedVehicle() {
    return intendedVehicle;
  }

  public OrdersVO setIntendedVehicle(String intendedVehicle) {
    this.intendedVehicle = intendedVehicle;
    return this;
  }

  public String getProcessingVehicle() {
    return processingVehicle;
  }

  public OrdersVO setProcessingVehicle(String processingVehicle) {
    this.processingVehicle = processingVehicle;
    return this;
  }

  public List<DestinationState> getDestinations() {
    return destinations;
  }

  public OrdersVO setDestinations(List<DestinationState> destinations) {
    this.destinations = requireNonNull(destinations, "destinations");
    return this;
  }

  /**
   * Creates a new instance from a <code>TransportOrder</code>.
   *
   * @param transportOrder The transport order to create an instance from.
   * @return A new instance containing the data from the given transport order.
   */
  public static OrdersVO fromTransportOrder(TransportOrder transportOrder) {
    if (transportOrder == null) {
      return null;
    }
    OrdersVO transportOrderState = new OrdersVO();
    transportOrderState.setDispensable(transportOrder.isDispensable());
    transportOrderState.setName(transportOrder.getName());
    transportOrderState.setPeripheralReservationToken(
        transportOrder.getPeripheralReservationToken()
    );
    transportOrderState.setWrappingSequence(
        nameOfNullableReference(transportOrder.getWrappingSequence())
    );
    transportOrderState.setType(transportOrder.getType());
    transportOrderState.setDestinations(transportOrder.getAllDriveOrders()
        .stream()
        .map(driveOrder -> DestinationState.fromDriveOrder(driveOrder))
        .collect(Collectors.toList()));
    transportOrderState.setIntendedVehicle(
        nameOfNullableReference(transportOrder.getIntendedVehicle()));
    transportOrderState.setProcessingVehicle(
        nameOfNullableReference(transportOrder.getProcessingVehicle()));
    transportOrderState.setState(transportOrder.getState());
    return transportOrderState;
  }

  private static String nameOfNullableReference(@Nullable TCSObjectReference<?> reference) {
    return reference == null ? null : reference.getName();
  }
}
