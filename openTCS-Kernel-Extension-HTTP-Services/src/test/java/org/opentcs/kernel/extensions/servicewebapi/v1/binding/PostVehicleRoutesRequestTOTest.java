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

/**
 * Unit tests for {@link PostVehicleRoutesRequestTO}.
 */
public class PostVehicleRoutesRequestTOTest {

  private JsonBinder jsonBinder;

  @BeforeEach
  public void setUp() {
    jsonBinder = new JsonBinder();
  }

  @Test
  public void jsonSample() {
    Approvals.verify(
        jsonBinder.toJson(new PostVehicleRoutesRequestTO(List.of("C", "F")).setSourcePoint("A"))
    );
  }
}
