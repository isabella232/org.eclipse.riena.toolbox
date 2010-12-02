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
package org.eclipse.riena.toolbox.assemblyeditor.api;

import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

/**
 * Factory for the different nodes that sets the default values.
 * 
 */
public interface INodeFactory {
	AssemblyNode createAssembly(BundleNode model);

	SubApplicationNode createSubApplication(AbstractAssemblyNode parent, BundleNode model);

	ModuleGroupNode createModuleGroup(AbstractAssemblyNode parent, BundleNode model);

	ModuleNode createModule(AbstractAssemblyNode parent, BundleNode model);

	SubModuleNode createSubModule(AbstractAssemblyNode parent, BundleNode model);

	RCPPerspective createRcpPerspective(SubApplicationNode parent);

}
