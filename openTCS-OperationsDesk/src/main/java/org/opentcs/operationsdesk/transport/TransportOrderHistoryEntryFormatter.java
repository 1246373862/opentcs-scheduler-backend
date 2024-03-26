/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.operationsdesk.transport;

import static java.util.Objects.requireNonNull;
import java.util.Optional;
import org.opentcs.components.plantoverview.ObjectHistoryEntryFormatter;
import org.opentcs.data.ObjectHistory;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.data.order.TransportOrderHistoryCodes;
import org.opentcs.operationsdesk.util.I18nPlantOverviewOperating;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * A formatter for history events/entries related to {@link TransportOrder}s.
 */
public class TransportOrderHistoryEntryFormatter
    implements ObjectHistoryEntryFormatter {

  /**
   * A bundle providing localized strings.
   */
  private final ResourceBundleUtil bundle
      = ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.TODETAIL_PATH);

  /**
   * Creates a new instance.
   */
  public TransportOrderHistoryEntryFormatter() {
  }

  @Override
  public Optional<String> apply(ObjectHistory.Entry entry) {
    requireNonNull(entry, "entry");

    switch (entry.getEventCode()) {
      case TransportOrderHistoryCodes.ORDER_CREATED:
        return Optional.of(
            bundle.getString("transportOrderHistoryEntryFormatter.code_orderCreated.text")
        );

      case TransportOrderHistoryCodes.ORDER_DISPATCHING_DEFERRED:
        return Optional.of(
            bundle.getString(
                "transportOrderHistoryEntryFormatter.code_orderDispatchingDeferred.text"
            )
            + " " + entry.getSupplement().toString()
        );

      case TransportOrderHistoryCodes.ORDER_DISPATCHING_RESUMED:
        return Optional.of(
            bundle.getString(
                "transportOrderHistoryEntryFormatter.code_orderDispatchingResumed.text"
            )
        );

      case TransportOrderHistoryCodes.ORDER_ASSIGNED_TO_VEHICLE:
        return Optional.of(
            bundle.getString("transportOrderHistoryEntryFormatter.code_orderAssignedToVehicle.text")
            + " '" + entry.getSupplement().toString() + "'"
        );

      case TransportOrderHistoryCodes.ORDER_RESERVED_FOR_VEHICLE:
        return Optional.of(
            bundle.getString(
                "transportOrderHistoryEntryFormatter.code_orderReservedForVehicle.text"
            )
            + " '" + entry.getSupplement().toString() + "'"
        );

      case TransportOrderHistoryCodes.ORDER_PROCESSING_VEHICLE_CHANGED:
        return Optional.of(
            bundle.getString(
                "transportOrderHistoryEntryFormatter.code_orderProcVehicleChanged.text"
            )
            + " '" + entry.getSupplement().toString() + "'"
        );

      case TransportOrderHistoryCodes.ORDER_DRIVE_ORDER_FINISHED:
        return Optional.of(
            bundle.getString("transportOrderHistoryEntryFormatter.code_driveOrderFinished.text")
        );

      case TransportOrderHistoryCodes.ORDER_REACHED_FINAL_STATE:
        return Optional.of(
            bundle.getString("transportOrderHistoryEntryFormatter.code_orderReachedFinalState.text")
        );

      default:
        return Optional.empty();
    }
  }

}
