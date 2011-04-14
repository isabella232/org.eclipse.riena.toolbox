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

import java.util.Set;

import org.eclipse.core.resources.IProject;

import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractTypedNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

/**
 * Services for retrieving Information of the domainmodel.
 * 
 */
public interface IModelService {
	Set<String> getAllParentTypeIds(AssemblyModel model);

	Set<String> getAllTypeIds(AssemblyModel model, AbstractTypedNode<?> nodesToIgnore);

	SubModuleNode findSubModuleByClassName(AssemblyModel model, IProject project, String className);
}
