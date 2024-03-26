/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.opentcs.components.kernel.services.RouterService;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.model.Path;
import org.opentcs.kernel.extensions.servicewebapi.KernelExecutorWrapper;

/**
 * Handles requests related to paths.
 */
public class PathHandler {

  private final TCSObjectService objectService;
  private final RouterService routerService;
  private final KernelExecutorWrapper executorWrapper;

  /**
   * Creates a new instance.
   *
   * @param objectService Used to retrieve path instances.
   * @param routerService Used to update path locks.
   * @param executorWrapper Executes calls via the kernel executor and waits for the outcome.
   */
  @Inject
  public PathHandler(TCSObjectService objectService,
                     RouterService routerService,
                     KernelExecutorWrapper executorWrapper) {
    this.objectService = requireNonNull(objectService, "objectService");
    this.routerService = requireNonNull(routerService, "routerService");
    this.executorWrapper = requireNonNull(executorWrapper, "executorWrapper");
  }

  /**
   * Updates the locked state of the path with the given name.
   *
   * @param pathName The name of the path to update.
   * @param lockedValue The path's new locked state (a boolean as a string).
   * @throws ObjectUnknownException If a path with the given name could not be found.
   */
  public void updatePathLock(@Nonnull String pathName, String lockedValue)
      throws ObjectUnknownException {
    executorWrapper.callAndWait(() -> {
      Path path = objectService.fetchObject(Path.class, pathName);
      if (path == null) {
        throw new ObjectUnknownException("Unknown path: " + pathName);
      }

      routerService.updatePathLock(path.getReference(), Boolean.parseBoolean(lockedValue));
    });
  }
}
