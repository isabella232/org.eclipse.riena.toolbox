/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package $packageName$;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Very simple sub module view, that displays only a label with the text "Hello
 * World!".
 * 
 */
public class HelloWorldSubModuleView extends SubModuleView<HelloWorldSubModuleController> {

	public static final String ID = "$pluginId$.HelloWorldSubModuleView";

	/**
	 * Add a Text widget with the text "Hello World!" to the view.
	 */
	@Override
	public void basicCreatePartControl(Composite parent) {
		parent.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.SUB_MODULE_BACKGROUND));
		parent.setLayout(new GridLayout(2, false));
		GridDataFactory gdf = GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT);
		
		UIControlsFactory.createLabel(parent, "First Name:");
		Text txtFirst = UIControlsFactory.createText(parent, SWT.NONE, "txtFirst");
		gdf.applyTo(txtFirst);
		
		UIControlsFactory.createLabel(parent, "Last Name:");
		Text txtLast = UIControlsFactory.createText(parent, SWT.NONE, "txtLast");
		gdf.applyTo(txtLast);
	}

	@Override
	protected HelloWorldSubModuleController createController(ISubModuleNode subModuleNode) {
		return new HelloWorldSubModuleController(subModuleNode);
	}

}
