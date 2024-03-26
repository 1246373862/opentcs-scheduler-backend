/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.base.event;

import java.util.EventObject;
import org.opentcs.guing.base.model.elements.AbstractConnection;

/**
 * An event for changes on connections.
 */
public class ConnectionChangeEvent
    extends EventObject {

  /**
   * Creates a new instance of ConnectionChangeEvent.
   *
   * @param connection The connection that has changed.
   */
  public ConnectionChangeEvent(AbstractConnection connection) {
    super(connection);
  }
}
