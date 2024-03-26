/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.components.properties;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import javax.inject.Singleton;
import org.opentcs.customizations.plantoverview.PlantOverviewInjectionModule;
import org.opentcs.guing.base.components.properties.type.AbstractComplexProperty;
import org.opentcs.guing.base.components.properties.type.KeyValueProperty;
import org.opentcs.guing.base.components.properties.type.KeyValueSetProperty;
import org.opentcs.guing.base.components.properties.type.LinkActionsProperty;
import org.opentcs.guing.base.components.properties.type.LocationTypeActionsProperty;
import org.opentcs.guing.base.components.properties.type.OrderTypesProperty;
import org.opentcs.guing.base.components.properties.type.PeripheralOperationsProperty;
import org.opentcs.guing.base.components.properties.type.SymbolProperty;
import org.opentcs.guing.common.components.dialogs.DetailsDialogContent;
import org.opentcs.guing.common.components.properties.PropertiesComponentsFactory;
import org.opentcs.guing.common.components.properties.SelectionPropertiesComponent;
import org.opentcs.guing.common.components.properties.panel.KeyValuePropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.KeyValueSetPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.LinkActionsEditorPanel;
import org.opentcs.guing.common.components.properties.panel.LocationTypeActionsEditorPanel;
import org.opentcs.guing.common.components.properties.panel.OrderTypesPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.PeripheralOperationsPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.PropertiesPanelFactory;
import org.opentcs.guing.common.components.properties.panel.SymbolPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.table.CellEditorFactory;
import org.opentcs.modeleditor.application.menus.MenuItemComponentsFactory;

/**
 * A Guice module for this package.
 */
public class PropertiesInjectionModule
    extends PlantOverviewInjectionModule {

  /**
   * Creates a new instance.
   */
  public PropertiesInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(PropertiesPanelFactory.class));
    install(new FactoryModuleBuilder().build(CellEditorFactory.class));
    install(new FactoryModuleBuilder().build(PropertiesComponentsFactory.class));
    install(new FactoryModuleBuilder().build(MenuItemComponentsFactory.class));

    MapBinder<Class<? extends AbstractComplexProperty>, DetailsDialogContent> dialogContentMapBinder
        = MapBinder.newMapBinder(binder(),
                                 new TypeLiteral<Class<? extends AbstractComplexProperty>>() {
                             },
                                 new TypeLiteral<DetailsDialogContent>() {
                             });
    dialogContentMapBinder
        .addBinding(KeyValueProperty.class)
        .to(KeyValuePropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(KeyValueSetProperty.class)
        .to(KeyValueSetPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(LocationTypeActionsProperty.class)
        .to(LocationTypeActionsEditorPanel.class);
    dialogContentMapBinder
        .addBinding(LinkActionsProperty.class)
        .to(LinkActionsEditorPanel.class);
    dialogContentMapBinder
        .addBinding(SymbolProperty.class)
        .to(SymbolPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(OrderTypesProperty.class)
        .to(OrderTypesPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(PeripheralOperationsProperty.class)
        .to(PeripheralOperationsPropertyEditorPanel.class);

    bind(SelectionPropertiesComponent.class)
        .in(Singleton.class);

  }

}
