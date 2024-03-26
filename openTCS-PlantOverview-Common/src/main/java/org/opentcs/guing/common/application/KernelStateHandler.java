/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.application;

import org.opentcs.access.Kernel;

/**
 * Listener interface implemented by classes interested in changes of a
 * connected kernel's state.
 */
public interface KernelStateHandler {

  /**
   * Informs the handler that the kernel is now in the given state.
   *
   * @param state The new state.
   */
  void enterKernelState(Kernel.State state);
}
