/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.drivers.peripherals;

import javax.annotation.Nonnull;
import org.opentcs.components.Lifecycle;
import org.opentcs.data.peripherals.PeripheralJob;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.annotations.ScheduledApiChange;

/**
 * Provides high-level methods for the system to control a peripheral device.
 */
public interface PeripheralController
    extends Lifecycle {

  /**
   * Lets the peripheral device associated with this controller process the given job.
   * The callback is used to inform about the successful or failed completion of the job.
   *
   * @param job The job to process.
   * @param callback The callback to use.
   * @throws IllegalStateException If this peripheral device associated with this controller
   * cannot process the job.
   */
  void process(@Nonnull PeripheralJob job, @Nonnull PeripheralJobCallback callback)
      throws IllegalStateException;

  /**
   * Aborts the current job, if any.
   * <p>
   * Whether a job can actually be aborted depends on the actual peripheral/job semantics.
   * The callback for the current job may still be called to indicate the job has failed, but it is
   * not strictly expected to.
   * The kernel will ignore calls to the callback after calling this method.
   * </p>
   */
  @ScheduledApiChange(when = "6.0", details = "Default implementation will be removed")
  default void abortJob() {
  }

  /**
   * Checks if the peripheral device would be able to process the given job, taking into account
   * its current state.
   *
   * @param job A job that might have to be processed.
   * @return An {@link ExplainedBoolean} telling if the peripheral device would be able to process
   * the job.
   */
  @Nonnull
  ExplainedBoolean canProcess(@Nonnull PeripheralJob job);

  /**
   * Sends a {@link PeripheralAdapterCommand} to the communication adapter.
   *
   * @param command The adapter command to be sent.
   */
  void sendCommAdapterCommand(@Nonnull PeripheralAdapterCommand command);
}
