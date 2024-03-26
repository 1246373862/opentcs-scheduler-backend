/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.operationsdesk.transport;

import javax.inject.Singleton;
import org.opentcs.customizations.plantoverview.PlantOverviewInjectionModule;
import org.opentcs.guing.common.transport.OrderTypeSuggestionsPool;

/**
 * A Guice module for the transport order type suggestions.
 */
public class OrderTypeSuggestionsModule
    extends PlantOverviewInjectionModule {

  /**
   * Creates a new instance.
   */
  public OrderTypeSuggestionsModule() {
  }

  @Override
  protected void configure() {
    orderTypeSuggestionsBinder().addBinding()
        .to(DefaultOrderTypeSuggestions.class)
        .in(Singleton.class);

    bind(OrderTypeSuggestionsPool.class).in(Singleton.class);
  }
}
