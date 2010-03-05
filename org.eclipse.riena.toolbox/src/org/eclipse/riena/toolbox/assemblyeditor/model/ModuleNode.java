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
package org.eclipse.riena.toolbox.assemblyeditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Module in the Riena-Navigation.
 *
 */
public class ModuleNode extends AbstractTypedNode<SubModuleNode> {
	private List<SubModuleNode> subModules;
	private String instanceId;
	private String icon;
	private boolean uncloseable;
	private String label;
	

	public ModuleNode(AbstractAssemblyNode parent) {
		super(parent);
		this.subModules = new ArrayList<SubModuleNode>();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isUncloseable() {
		return uncloseable;
	}

	public void setUncloseable(boolean uncloseable) {
		this.uncloseable = uncloseable;
	}

	@Override
	public List<SubModuleNode> getChildren() {
		return subModules;
	}

	@Override
	public boolean add(SubModuleNode e) {
		return subModules.add(e);
	}

	@Override
	public String toString() {
		return "ModuleNode [icon=" + icon + ", instanceId=" + instanceId
				+ ", label=" + label + ", subModules=" + subModules
				+ ", typeId=" + getTypeId() + ", uncloseable=" + uncloseable + "]";
	}

	@Override
	public String getTreeLabel() {
		return name;
	}


}
