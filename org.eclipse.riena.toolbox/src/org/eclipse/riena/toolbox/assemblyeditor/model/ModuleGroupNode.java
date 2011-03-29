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
package org.eclipse.riena.toolbox.assemblyeditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a ModuleGroup in the Riena-Navigation.
 * 
 */
public class ModuleGroupNode extends AbstractTypedNode<ModuleNode> {
	private final List<ModuleNode> modules;

	public ModuleGroupNode(final AbstractAssemblyNode parent) {
		super(parent);
		modules = new ArrayList<ModuleNode>();
	}

	@Override
	public List<ModuleNode> getChildren() {
		return modules;
	}

	@Override
	public boolean add(final ModuleNode e) {
		return modules.add(e);
	}

	@Override
	protected String getTreeLabelValue() {
		return name;
	}

	@Override
	public String toString() {
		return "ModuleGroupNode [modules=" + modules + ", nodeId=" + nodeId + ", name=" + name + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
