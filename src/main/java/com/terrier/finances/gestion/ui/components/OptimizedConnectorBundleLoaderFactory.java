/**
 * 
 */
package com.terrier.finances.gestion.ui.components;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.server.widgetsetutils.ConnectorBundleLoaderFactory;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Bundle Loader Optimisé
 * @author vzwingma
 *
 */
public class OptimizedConnectorBundleLoaderFactory extends ConnectorBundleLoaderFactory {
	private Set<String> eagerConnectors = new HashSet<String>();
	{
		eagerConnectors.add(com.vaadin.client.ui.ui.UIConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.gridlayout.GridLayoutConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.textfield.TextFieldConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.passwordfield.PasswordFieldConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.orderedlayout.VerticalLayoutConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.label.LabelConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.customcomponent.CustomComponentConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.button.ButtonConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.combobox.ComboBoxConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.datefield.InlineDateFieldConnector.class.getName());
//		eagerConnectors.add(com.vaadin.client.ui.table.TableConnector.class.getName());
//		eagerConnectors.add(com.vaadin.client.ui.treetable.TreeTableConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.orderedlayout.HorizontalLayoutConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.csslayout.CssLayoutConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.datefield.PopupDateFieldConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.window.WindowConnector.class.getName());
		eagerConnectors.add(com.vaadin.client.ui.panel.PanelConnector.class.getName());
	}

	@Override
	protected LoadStyle getLoadStyle(JClassType connectorType) {
		if (eagerConnectors.contains(connectorType.getQualifiedBinaryName())) {
			return LoadStyle.EAGER;
		} else {
			// Loads all other connectors immediately after the initial view has
			// been rendered
			return LoadStyle.DEFERRED;
		}
	}
}
