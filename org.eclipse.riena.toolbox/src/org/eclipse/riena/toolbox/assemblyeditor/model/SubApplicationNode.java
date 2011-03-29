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
 * This class represents a SubApplication in Riena-Navigation.
 * 
 */
public class SubApplicationNode extends AbstractTypedNode<ModuleGroupNode> {

	private final List<ModuleGroupNode> moduleGroups;
	private String perspective;
	private String icon;

	public SubApplicationNode(final AbstractAssemblyNode parent) {
		super(parent);
		this.moduleGroups = new ArrayList<ModuleGroupNode>();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(final String icon) {
		this.icon = icon;
	}

	public String getPerspective() {
		return perspective;
	}

	public void setPerspective(final String view) {
		this.perspective = view;
	}

	@Override
	public List<ModuleGroupNode> getChildren() {
		return moduleGroups;
	}

	@Override
	public boolean add(final ModuleGroupNode arg0) {
		return moduleGroups.add(arg0);
	}

	@Override
	protected String getTreeLabelValue() {
		return name;
	}

	@Override
	public String toString() {
		return "SubApplicationNode [moduleGroups=" + moduleGroups + ", perspective=" + perspective + ", icon=" + icon + ", nodeId=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ nodeId + ", name=" + name + "]"; //$NON-NLS-1$//$NON-NLS-2$
	}
}
