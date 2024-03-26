/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.binding;

import java.util.List;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.extensions.servicewebapi.JsonBinder;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.shared.Property;

/**
 */
public class GetOrderSequenceResponseTOTest {

  private JsonBinder jsonBinder;

  @BeforeEach
  public void setUp() {
    jsonBinder = new JsonBinder();
  }

  @Test
  public void jsonSample() {
    GetOrderSequenceResponseTO to = new GetOrderSequenceResponseTO("some-order-sequence")
        .setType("Charge")
        .setOrders(List.of("some-order", "another-order", "order-3"))
        .setFinishedIndex(3)
        .setComplete(false)
        .setFinished(false)
        .setFailureFatal(true)
        .setIntendedVehicle("some-vehicle")
        .setProcessingVehicle(null)
        .setProperties(List.of(new Property("some-key", "some-value"),
                               new Property("another-key", "another-value")));

    Approvals.verify(jsonBinder.toJson(to));
  }
}
