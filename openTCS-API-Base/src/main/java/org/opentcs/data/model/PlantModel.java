/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.data.model;

import java.io.Serializable;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import javax.annotation.Nonnull;
import org.opentcs.data.model.visualization.VisualLayout;
import org.opentcs.util.annotations.ScheduledApiChange;

/**
 * An immutable representation of a complete plant model's state.
 */
public class PlantModel
    implements Serializable {

  private final String name;
  private final Map<String, String> properties;
  private final Set<Point> points;
  private final Set<Path> paths;
  private final Set<LocationType> locationTypes;
  private final Set<Location> locations;
  private final Set<Block> blocks;
  private final Set<Vehicle> vehicles;
  private final Set<VisualLayout> visualLayouts;

  /**
   * Creates a new instance.
   *
   * @param name The model's name.
   */
  public PlantModel(@Nonnull String name) {
    this(name, Map.of(), Set.of(), Set.of(), Set.of(), Set.of(), Set.of(), Set.of(), Set.of());
  }

  private PlantModel(@Nonnull String name,
                     @Nonnull Map<String, String> properties,
                     @Nonnull Set<Point> points,
                     @Nonnull Set<Path> paths,
                     @Nonnull Set<LocationType> locationTypes,
                     @Nonnull Set<Location> locations,
                     @Nonnull Set<Block> blocks,
                     @Nonnull Set<Vehicle> vehicles,
                     @Nonnull Set<VisualLayout> visualLayouts) {
    this.name = requireNonNull(name, "name");
    this.properties = Map.copyOf(properties);
    this.points = Set.copyOf(points);
    this.paths = Set.copyOf(paths);
    this.locationTypes = Set.copyOf(locationTypes);
    this.locations = Set.copyOf(locations);
    this.blocks = Set.copyOf(blocks);
    this.vehicles = Set.copyOf(vehicles);
    this.visualLayouts = Set.copyOf(visualLayouts);
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public Map<String, String> getProperties() {
    return properties;
  }

  public PlantModel withProperties(Map<String, String> properties) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<Point> getPoints() {
    return points;
  }

  public PlantModel withPoints(@Nonnull Set<Point> points) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<Path> getPaths() {
    return paths;
  }

  public PlantModel withPaths(@Nonnull Set<Path> paths) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<LocationType> getLocationTypes() {
    return locationTypes;
  }

  public PlantModel withLocationTypes(@Nonnull Set<LocationType> locationTypes) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<Location> getLocations() {
    return locations;
  }

  public PlantModel withLocations(@Nonnull Set<Location> locations) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<Block> getBlocks() {
    return blocks;
  }

  public PlantModel withBlocks(@Nonnull Set<Block> blocks) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<Vehicle> getVehicles() {
    return vehicles;
  }

  public PlantModel withVehicles(@Nonnull Set<Vehicle> vehicles) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Nonnull
  public Set<VisualLayout> getVisualLayouts() {
    return visualLayouts;
  }

  /**
   * Returns an instance with the visual layouts replaced by the given ones.
   *
   * @param visualLayouts The visual layouts to be set.
   * @return The instance with the visual layouts replaced by the given ones.
   * @deprecated Use {@link #withVisualLayouts(java.util.Set)} instead.
   */
  @Deprecated
  @ScheduledApiChange(when = "6.0", details = "Will be removed.")
  public PlantModel withVisuaLayouts(@Nonnull Set<VisualLayout> visualLayouts) {
    return withVisualLayouts(visualLayouts);
  }

  public PlantModel withVisualLayouts(@Nonnull Set<VisualLayout> visualLayouts) {
    return new PlantModel(name,
                          properties,
                          points,
                          paths,
                          locationTypes,
                          locations,
                          blocks,
                          vehicles,
                          visualLayouts);
  }

  @Override
  public String toString() {
    return "PlantModel{"
        + "name=" + name
        + ", properties=" + properties
        + ", points=" + points
        + ", paths=" + paths
        + ", locationTypes=" + locationTypes
        + ", locations=" + locations
        + ", blocks=" + blocks
        + ", vehicles=" + vehicles
        + ", visualLayouts=" + visualLayouts
        + '}';
  }
}
