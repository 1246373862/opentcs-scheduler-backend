/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.exchange.adapter;

import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.stream.Collectors;
import org.opentcs.access.to.model.PlantModelCreationTO;
import org.opentcs.access.to.model.PointCreationTO;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.TCSObject;
import org.opentcs.data.model.Couple;
import org.opentcs.data.model.Envelope;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Pose;
import org.opentcs.data.model.Triple;
import org.opentcs.guing.base.components.layer.LayerWrapper;
import org.opentcs.guing.base.components.properties.type.AngleProperty;
import org.opentcs.guing.base.components.properties.type.CoordinateProperty;
import org.opentcs.guing.base.components.properties.type.LengthProperty;
import org.opentcs.guing.base.model.ModelComponent;
import org.opentcs.guing.base.model.EnvelopeModel;
import org.opentcs.guing.base.model.elements.LayoutModel;
import org.opentcs.guing.base.model.elements.PointModel;
import org.opentcs.guing.common.model.SystemModel;

/**
 * An adapter for points.
 */
public class PointAdapter
    extends AbstractProcessAdapter {

  /**
   * Creates a new instance.
   */
  public PointAdapter() {
  }

  @Override  // OpenTCSProcessAdapter
  public void updateModelProperties(TCSObject<?> tcsObject,
                                    ModelComponent modelComponent,
                                    SystemModel systemModel,
                                    TCSObjectService objectService) {
    Point point = requireNonNull((Point) tcsObject, "tcsObject");
    PointModel model = (PointModel) modelComponent;

    // Name
    model.getPropertyName().setText(point.getName());

    // Position in model
    model.getPropertyModelPositionX().setValueAndUnit(point.getPose().getPosition().getX(),
                                                      LengthProperty.Unit.MM);
    model.getPropertyModelPositionY().setValueAndUnit(point.getPose().getPosition().getY(),
                                                      LengthProperty.Unit.MM);
    model.getPropertyVehicleOrientationAngle()
        .setValueAndUnit(point.getPose().getOrientationAngle(), AngleProperty.Unit.DEG);

    updateModelType(model, point);

    for (Map.Entry<String, Envelope> entry : point.getVehicleEnvelopes().entrySet()) {
      model.getPropertyVehicleEnvelopes().getValue().add(
          new EnvelopeModel(entry.getKey(), entry.getValue().getVertices())
      );
    }

    updateMiscModelProperties(model, point);
    updateModelLayoutProperties(model, point, systemModel.getLayoutModel());
  }

  @Override
  public PlantModelCreationTO storeToPlantModel(ModelComponent modelComponent,
                                                SystemModel systemModel,
                                                PlantModelCreationTO plantModel) {
    PlantModelCreationTO result = plantModel
        .withPoint(
            new PointCreationTO(modelComponent.getName())
                .withPose(
                    new Pose(
                        getKernelCoordinates((PointModel) modelComponent),
                        getKernelVehicleAngle((PointModel) modelComponent)
                    )
                )
                .withType(getKernelPointType((PointModel) modelComponent))
                .withVehicleEnvelopes(getKernelVehicleEnvelopes((PointModel) modelComponent))
                .withProperties(getKernelProperties(modelComponent))
                .withLayout(getLayout((PointModel) modelComponent))
        );

    unmarkAllPropertiesChanged(modelComponent);

    return result;
  }

  private void updateModelLayoutProperties(PointModel model, Point point, LayoutModel layoutModel) {
    model.getPropertyLayoutPosX().setText(String.valueOf(point.getLayout().getPosition().getX()));
    model.getPropertyLayoutPosY().setText(String.valueOf(point.getLayout().getPosition().getY()));
    model.getPropertyPointLabelOffsetX()
        .setText(String.valueOf(point.getLayout().getLabelOffset().getX()));
    model.getPropertyPointLabelOffsetY()
        .setText(String.valueOf(point.getLayout().getLabelOffset().getY()));
    LayerWrapper layerWrapper = layoutModel.getPropertyLayerWrappers()
        .getValue().get(point.getLayout().getLayerId());
    model.getPropertyLayerWrapper().setValue(layerWrapper);
  }

  @SuppressWarnings("deprecation")
  private void updateModelType(PointModel model, Point point) {
    PointModel.Type value;

    switch (point.getType()) {
      case PARK_POSITION:
        value = PointModel.Type.PARK;
        break;
      case REPORT_POSITION:
        value = PointModel.Type.REPORT;
        break;
      case HALT_POSITION:
      default:
        value = PointModel.Type.HALT;
    }

    model.getPropertyType().setValue(value);
  }

  private Point.Type getKernelPointType(PointModel model) {
    return convertPointType((PointModel.Type) model.getPropertyType().getValue());
  }

  private Triple getKernelCoordinates(PointModel model) {
    return convertToTriple(model.getPropertyModelPositionX(),
                           model.getPropertyModelPositionY());
  }

  private double getKernelVehicleAngle(PointModel model) {
    return model.getPropertyVehicleOrientationAngle().getValueByUnit(AngleProperty.Unit.DEG);
  }

  private Map<String, Envelope> getKernelVehicleEnvelopes(PointModel model) {
    return model.getPropertyVehicleEnvelopes().getValue().stream()
        .collect(
            Collectors.toMap(EnvelopeModel::getKey,
                             envelopeModel -> new Envelope(envelopeModel.getVertices()))
        );
  }

  private PointCreationTO.Layout getLayout(PointModel model) {
    return new PointCreationTO.Layout(
        new Couple(Long.parseLong(model.getPropertyLayoutPosX().getText()),
                   Long.parseLong(model.getPropertyLayoutPosY().getText())),
        new Couple(Long.parseLong(model.getPropertyPointLabelOffsetX().getText()),
                   Long.parseLong(model.getPropertyPointLabelOffsetY().getText())),
        model.getPropertyLayerWrapper().getValue().getLayer().getId()
    );
  }

  @SuppressWarnings("deprecation")
  private Point.Type convertPointType(PointModel.Type type) {
    requireNonNull(type, "type");
    switch (type) {
      case PARK:
        return Point.Type.PARK_POSITION;
      case REPORT:
        return Point.Type.REPORT_POSITION;
      case HALT:
        return Point.Type.HALT_POSITION;
      default:
        throw new IllegalArgumentException("Unhandled type: " + type);
    }
  }

  private Triple convertToTriple(CoordinateProperty cpx, CoordinateProperty cpy) {
    Triple result = new Triple((int) cpx.getValueByUnit(LengthProperty.Unit.MM),
                               (int) cpy.getValueByUnit(LengthProperty.Unit.MM),
                               0);

    return result;
  }
}
