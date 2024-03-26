/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.access.rmi.services;

import java.rmi.RemoteException;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.components.kernel.services.PeripheralDispatcherService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.data.peripherals.PeripheralJob;

/**
 * The default implementation of the peripheral dispatcher service.
 * Delegates method invocations to the corresponding remote service.
 */
class RemotePeripheralDispatcherServiceProxy
    extends AbstractRemoteServiceProxy<RemotePeripheralDispatcherService>
    implements PeripheralDispatcherService {

  /**
   * Creates a new instance.
   */
  RemotePeripheralDispatcherServiceProxy() {
  }

  @Override
  public void dispatch()
      throws KernelRuntimeException {
    checkServiceAvailability();

    try {
      getRemoteService().dispatch(getClientId());
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }

  @Override
  public void withdrawByLocation(TCSResourceReference<Location> locationRef)
      throws ObjectUnknownException, KernelRuntimeException {
    checkServiceAvailability();

    try {
      getRemoteService().withdrawByLocation(getClientId(), locationRef);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }

  @Override
  public void withdrawByPeripheralJob(TCSObjectReference<PeripheralJob> jobRef)
      throws ObjectUnknownException, KernelRuntimeException {
    checkServiceAvailability();

    try {
      getRemoteService().withdrawByPeripheralJob(getClientId(), jobRef);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }
}
