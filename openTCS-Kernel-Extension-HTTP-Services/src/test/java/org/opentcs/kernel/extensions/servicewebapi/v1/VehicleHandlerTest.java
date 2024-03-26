/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import org.opentcs.components.kernel.services.RouterService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.management.AttachmentInformation;
import org.opentcs.kernel.extensions.servicewebapi.KernelExecutorWrapper;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.GetVehicleResponseTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.PostVehicleRoutesRequestTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.PutVehicleAllowedOrderTypesTO;

/**
 * Unit tests for {@link VehicleHandler}.
 */
class VehicleHandlerTest {

  private VehicleService vehicleService;
  private RouterService routerService;
  private KernelExecutorWrapper executorWrapper;

  private VehicleHandler handler;

  private Vehicle vehicle;
  private VehicleCommAdapterDescription adapterDescriptionMock;
  private AttachmentInformation attachmentInfo;

  @BeforeEach
  public void setUp() {
    vehicleService = mock();
    routerService = mock();
    executorWrapper = new KernelExecutorWrapper(Executors.newSingleThreadExecutor());

    handler = new VehicleHandler(vehicleService, routerService, executorWrapper);

    vehicle = new Vehicle("some-vehicle");
    adapterDescriptionMock = new MockVehicleCommAdapterDescription();

    attachmentInfo = new AttachmentInformation(
        vehicle.getReference(),
        List.of(adapterDescriptionMock),
        adapterDescriptionMock
    );

    given(vehicleService.fetchObject(Vehicle.class, "some-vehicle"))
        .willReturn(vehicle);
    given(vehicleService.fetchAttachmentInformation(vehicle.getReference()))
        .willReturn(attachmentInfo);
  }

  @Test
  public void attachMockVehicleAdapter() {
    // Act
    handler.putVehicleCommAdapter(
        "some-vehicle",
        MockVehicleCommAdapterDescription.class.getName()
    );

    // Assert
    then(vehicleService)
        .should()
        .attachCommAdapter(vehicle.getReference(), adapterDescriptionMock);
  }


  @Test
  public void throwOnAttachAdapterForUnknownVehicle() {
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(
            () -> handler.putVehicleCommAdapter(
                "some-unknown-vehicle",
                MockVehicleCommAdapterDescription.class.getName()
            )
        );
  }

  @Test
  public void throwOnAttachUnknownAdapter() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> handler.putVehicleCommAdapter(
                "some-vehicle",
                "some-unknown-adapter-class-name"
            )
        );
  }

  @Test
  public void enableCommAdapter() {
    handler.putVehicleCommAdapterEnabled("some-vehicle", "true");

    then(vehicleService).should().enableCommAdapter(vehicle.getReference());
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "flase", "some-value-that-is-not-true"})
  public void disableCommAdapterOnAnyNontrueValue(String value) {
    handler.putVehicleCommAdapterEnabled("some-vehicle", value);

    then(vehicleService).should().disableCommAdapter(vehicle.getReference());
  }

  @ParameterizedTest
  @ValueSource(strings = {"true ", "false"})
  public void throwOnEnableUnknownVehicle(String value) {
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(() -> handler.putVehicleCommAdapterEnabled("some-unknown-vehicle", value));
  }

  @Test
  public void fetchAttachmentInformation() {
    assertThat(handler.getVehicleCommAdapterAttachmentInformation("some-vehicle"))
        .isSameAs(attachmentInfo);
  }

  @Test
  public void throwOnFetchInfoForUnknownLocation() {
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(
            () -> handler.getVehicleCommAdapterAttachmentInformation("some-unknown-vehicle")
        );
  }

  @ParameterizedTest
  @EnumSource(Vehicle.ProcState.class)
  public void retrieveVehiclesByProcState(Vehicle.ProcState procState) {
    // Arrange
    Vehicle vehicleWithProcState = vehicle.withProcState(procState);
    given(vehicleService.fetchObjects(ArgumentMatchers.<Class<Vehicle>>any(), any()))
        .willReturn(Set.of(vehicleWithProcState));

    // Act & Assert
    List<GetVehicleResponseTO> result = handler.getVehiclesState(procState.name());
    MatcherAssert.assertThat(result, hasSize(1));
    then(vehicleService).should().fetchObjects(ArgumentMatchers.<Class<Vehicle>>any(), any());
  }

  @Test
  public void throwOnRetrieveVehiclesForUnknownProcState() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> handler.getVehiclesState("some-unknown-proc-state"));
  }

  @Test
  public void retrieveVehicleByName() {
    // Act & Assert: happy path
    GetVehicleResponseTO result = handler.getVehicleStateByName("some-vehicle");
    MatcherAssert.assertThat(result, is(notNullValue()));
    then(vehicleService).should().fetchObject(Vehicle.class, "some-vehicle");

    // Act & Assert: nonexistent vehicle
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(() -> handler.getVehicleStateByName("some-other-vehicle"));
  }

  @ParameterizedTest
  @EnumSource(Vehicle.IntegrationLevel.class)
  public void updateVehicleIntegrationLevel(Vehicle.IntegrationLevel integrationLevel) {
    handler.putVehicleIntegrationLevel("some-vehicle", integrationLevel.name());
    then(vehicleService)
        .should()
        .updateVehicleIntegrationLevel(vehicle.getReference(), integrationLevel);
  }

  @Test
  public void throwOnUpdateIntegrationLevelForUnknownVehicleOrIntegrationLevel() {
    // Act & Assert: nonexistent vehicle
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(() -> handler.putVehicleIntegrationLevel("some-unknown-vehicle",
                                                             "TO_BE_UTILIZED"));

    // Act & Assert: unknown integration level
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> handler.putVehicleIntegrationLevel("some-vehicle",
                                                             "some-unknown-integration-level"));
  }

  @Test
  public void pauseVehicle() {
    handler.putVehiclePaused("some-vehicle", "true");

    then(vehicleService).should().updateVehiclePaused(vehicle.getReference(), true);
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "flase", "some-value-that-is-not-true"})
  public void unpauseVehicleOnAnyNontrueValue(String value) {
    handler.putVehiclePaused("some-vehicle", value);

    then(vehicleService).should().updateVehiclePaused(vehicle.getReference(), false);
  }

  @ParameterizedTest
  @ValueSource(strings = {"true ", "false"})
  public void throwOnPauseUnknownVehicle(String value) {
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(() -> handler.putVehiclePaused("some-unknown-vehicle", value));
  }

  @Test
  public void setVehicleEnvelopeKey() {
    handler.putVehicleEnvelopeKey("some-vehicle", "some-key");

    then(vehicleService).should().updateVehicleEnvelopeKey(vehicle.getReference(), "some-key");
  }

  @Test
  public void nullVehicleEnvelopeKey() {
    handler.putVehicleEnvelopeKey("some-vehicle", null);

    then(vehicleService).should().updateVehicleEnvelopeKey(vehicle.getReference(), null);
  }

  @Test
  public void throwOnSetEnvelopeUnknownVehicle() {
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(() -> handler.putVehicleEnvelopeKey("some-unknown-vehicle", "some-key"));
  }

  @Test
  public void updateVehicleAllowedOrderTypes() {
    // Act
    handler.putVehicleAllowedOrderTypes(
        "some-vehicle",
        new PutVehicleAllowedOrderTypesTO(List.of("some-order-type", "some-other-order-type"))
    );

    // Assert
    @SuppressWarnings("unchecked")
    ArgumentCaptor<Set<String>> captor = ArgumentCaptor.forClass(Set.class);
    then(vehicleService)
        .should()
        .updateVehicleAllowedOrderTypes(eq(vehicle.getReference()), captor.capture());
    assertThat(captor.getValue())
        .hasSize(2)
        .contains("some-order-type", "some-other-order-type");
  }

  @Test
  public void throwOnUpdateAllowedOrderTypesForUnknownVehicle() {
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(
            () -> handler.putVehicleAllowedOrderTypes("some-unknown-vehicle",
                                                      new PutVehicleAllowedOrderTypesTO(List.of()))
        );
  }

  @Test
  public void retrieveVehicleRoutesForCurrentPosition() {
    // Arrange
    Point vehiclePosition = new Point("some-point");
    Point destinationPoint1 = new Point("some-destination-point");
    Point destinationPoint2 = new Point("some-destination-point-2");
    Vehicle vehicleWithPosition = vehicle.withCurrentPosition(vehiclePosition.getReference());
    given(vehicleService.fetchObject(Point.class, "some-point"))
        .willReturn(vehiclePosition);
    given(vehicleService.fetchObject(Point.class, "some-destination-point"))
        .willReturn(destinationPoint1);
    given(vehicleService.fetchObject(Point.class, "some-destination-point-2"))
        .willReturn(destinationPoint2);
    given(vehicleService.fetchObject(Vehicle.class, "some-vehicle"))
        .willReturn(vehicleWithPosition);

    // Act & Assert: happy path
    handler.getVehicleRoutes(
        "some-vehicle",
        new PostVehicleRoutesRequestTO(
            List.of("some-destination-point", "some-destination-point-2")
        )
    );

    then(routerService)
        .should()
        .computeRoutes(
            vehicle.getReference(),
            vehiclePosition.getReference(),
            Set.of(destinationPoint1.getReference(), destinationPoint2.getReference())
        );

    // Act & Assert: nonexistent vehicle
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(
            () -> handler.getVehicleRoutes(
                "some-unknown-vehicle",
                new PostVehicleRoutesRequestTO(List.of("some-destination-point"))
            )
        );

    // Act & Assert: nonexistent destination point
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(
            () -> handler.getVehicleRoutes(
                "some-vehicle",
                new PostVehicleRoutesRequestTO(List.of("some-unknown-destination-point"))
            )
        );

    // Act & Assert: unknown vehicle position
    given(vehicleService.fetchObject(Vehicle.class, "some-vehicle"))
        .willReturn(vehicle);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> handler.getVehicleRoutes(
                "some-vehicle",
                new PostVehicleRoutesRequestTO(List.of("some-destination-point"))
            )
        );
  }

  @Test
  public void retrieveVehicleRoutesForPositionProvidedInRequest() {
    // Arrange
    Point sourcePoint = new Point("some-source-point");
    Point destinationPoint1 = new Point("some-destination-point");
    Point destinationPoint2 = new Point("some-destination-point-2");
    given(vehicleService.fetchObject(Point.class, "some-source-point"))
        .willReturn(sourcePoint);
    given(vehicleService.fetchObject(Point.class, "some-destination-point"))
        .willReturn(destinationPoint1);
    given(vehicleService.fetchObject(Point.class, "some-destination-point-2"))
        .willReturn(destinationPoint2);

    // Act & Assert: happy path
    handler.getVehicleRoutes(
        "some-vehicle",
        new PostVehicleRoutesRequestTO(
            List.of("some-destination-point", "some-destination-point-2")
        ).setSourcePoint("some-source-point")
    );

    then(routerService)
        .should()
        .computeRoutes(
            vehicle.getReference(),
            sourcePoint.getReference(),
            Set.of(destinationPoint1.getReference(), destinationPoint2.getReference())
        );

    // Act & Assert: nonexistent source point
    assertThatExceptionOfType(ObjectUnknownException.class)
        .isThrownBy(
            () -> handler.getVehicleRoutes(
                "some-vehicle",
                new PostVehicleRoutesRequestTO(List.of("some-destination-point"))
                    .setSourcePoint("some-unknown-source-point")
            )
        );
  }

  public static class MockVehicleCommAdapterDescription
      extends VehicleCommAdapterDescription {

    @Override
    public String getDescription() {
      return "some-vehicle-comm-adapter";
    }

    @Override
    public boolean isSimVehicleCommAdapter() {
      return false;
    }
  }
}
