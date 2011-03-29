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

import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

public interface ICodeGenerator {
	public RCPView generateView(SubModuleNode subModule);

	public String generateController(SubModuleNode subModule);

	public void deleteControllerClass(SubModuleNode subModule);

	public void deleteViewClass(SubModuleNode subModule);
}
