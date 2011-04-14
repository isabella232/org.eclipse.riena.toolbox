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

import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;

public interface IAssemblyDataProvider {
	void saveData(AssemblyModel model);

	/**
	 * Iterates over all Projects in the workspace and parses the plugin.xml if
	 * existent.
	 * 
	 * @return the AssemblyModel
	 */
	AssemblyModel createData();

	IPluginXmlRenderer getXmlRenderer();

	void setXmlRenderer(IPluginXmlRenderer xmlRenderer);

	IPluginXmlParser getXmlParser();

	void setXmlParser(IPluginXmlParser xmlParser);
}
