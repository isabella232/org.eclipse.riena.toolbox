/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.assemblyeditor.api;

import java.util.List;
import java.util.Set;

import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

public interface IPluginXmlParser {
	List<AssemblyNode> parseDocument(BundleNode bundle);

	Set<RCPView> getRcpViews(BundleNode bundle);

	/**
	 * Returns all registered RCP-Perspectives in this bundles plugin.xml with
	 * the class-name
	 * <code>org.eclipse.riena.navigation.ui.swt.views.SubApplicationView</code>
	 * 
	 * @param bundleNode
	 * @return
	 */
	Set<RCPPerspective> getRcpPerspectives(BundleNode bundleNode);

	boolean unregisterView(SubModuleNode subModule);

	boolean unregisterPerspective(SubApplicationNode subApplication);
}
