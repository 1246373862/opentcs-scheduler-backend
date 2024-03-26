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
 * This interface declares the methods that a driver communicating with and controlling a
 * peripheral device must implement.
 */
public interface PeripheralCommAdapter
    extends Lifecycle {

  /**
   * Enables this comm adapter, i.e. turns it on.
   */
  void enable();

  /**
   * Disables this comm adapter, i.e. turns it off.
   */
  void disable();

  /**
   * Checks whether this communication adapter is enabled.
   *
   * @return {@code true} if, and only if, this communication adapter is enabled.
   */
  boolean isEnabled();

  /**
   * Returns a model of the peripheral device's and its communication adapter's attributes.
   *
   * @return A model of the peripheral device's and its communication adapter's attributes.
   */
  @Nonnull
  PeripheralProcessModel getProcessModel();

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
   * Processes the given job by sending it or a representation that the peripheral device
   * understands to the peripheral device itself.
   *
   * @param job The job to process.
   * @param callback The callback to use to report back about the successful or failed completion of
   * the job.
   */
  void process(@Nonnull PeripheralJob job, @Nonnull PeripheralJobCallback callback);

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
   * Executes the given {@link PeripheralAdapterCommand}.
   *
   * @param command The command to execute.
   */
  void execute(@Nonnull PeripheralAdapterCommand command);
}
