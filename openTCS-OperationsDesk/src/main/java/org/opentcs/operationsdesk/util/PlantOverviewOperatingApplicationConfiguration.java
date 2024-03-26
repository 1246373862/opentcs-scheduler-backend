/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.operationsdesk.util;

import org.opentcs.components.plantoverview.LocationTheme;
import org.opentcs.components.plantoverview.VehicleTheme;
import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Provides methods to configure the PlantOverview application (in operating mode).
 */
@ConfigurationPrefix(PlantOverviewOperatingApplicationConfiguration.PREFIX)
public interface PlantOverviewOperatingApplicationConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "plantoverviewapp";

  @ConfigurationEntry(
      type = "String",
      description = {"The plant overview application's locale, as a BCP 47 language tag.",
                     "Examples: 'en', 'de', 'zh'"},
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_init_0")
  String locale();

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether the GUI window should be maximized on startup.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_size_0")
  boolean frameMaximized();

  @ConfigurationEntry(
      type = "Integer",
      description = "The GUI window's configured width in pixels.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_size_1")
  int frameBoundsWidth();

  @ConfigurationEntry(
      type = "Integer",
      description = "The GUI window's configured height in pixels.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_size_2")
  int frameBoundsHeight();

  @ConfigurationEntry(
      type = "Integer",
      description = "The GUI window's configured x-coordinate on screen in pixels.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_size_3")
  int frameBoundsX();

  @ConfigurationEntry(
      type = "Integer",
      description = "The GUI window's configured y-coordinate on screen in pixels.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_size_4")
  int frameBoundsY();

  @ConfigurationEntry(
      type = "Class name",
      description = {
        "The name of the class to be used for the location theme.",
        "Must be a class extending org.opentcs.components.plantoverview.LocationTheme"
      },
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "3_themes_0"
  )
  Class<? extends LocationTheme> locationThemeClass();

  @ConfigurationEntry(
      type = "Class name",
      description = {"The name of the class to be used for the vehicle theme.",
                     "Must be a class extending org.opentcs.components.plantoverview.VehicleTheme"},
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "3_themes_0")
  Class<? extends VehicleTheme> vehicleThemeClass();

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether reported precise positions should be ignored displaying vehicles.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "4_behaviour_0"
  )
  boolean ignoreVehiclePrecisePosition();

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether reported orientation angles should be ignored displaying vehicles.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "4_behaviour_1")
  boolean ignoreVehicleOrientationAngle();

  @ConfigurationEntry(
      type = "Integer",
      description = "The maximum number of most recent user notifications to be displayed.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "9_misc")
  int userNotificationDisplayCount();

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether the forced withdrawal context menu entry should be enabled.",
      changesApplied = ConfigurationEntry.ChangesApplied.INSTANTLY,
      orderKey = "10_misc")
  boolean allowForcedWithdrawal();
}
