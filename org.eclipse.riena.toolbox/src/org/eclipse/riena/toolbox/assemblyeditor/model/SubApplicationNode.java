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
 * This class represents a SubApplication in Riena-Navigation.
 *
 */
public class SubApplicationNode extends AbstractTypedNode<ModuleGroupNode> {

	private List<ModuleGroupNode> moduleGroups;
	private String instanceId;
	private String icon;
	private String perspective;
	private String label;
	

	public SubApplicationNode(AbstractAssemblyNode parent) {
		super(parent);
		this.moduleGroups = new ArrayList<ModuleGroupNode>();
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

	public String getPerspective() {
		return perspective;
	}

	public void setPerspective(String view) {
		this.perspective = view;
	}

	public List<ModuleGroupNode> getChildren() {
		return moduleGroups;
	}

	@Override
	public boolean add(ModuleGroupNode arg0) {
		return moduleGroups.add(arg0);
	}

	@Override
	public String toString() {
		return "SubApplicationNode [icon=" + icon + ", instanceId="
				+ instanceId + ", label=" + label + ", moduleGroups="
				+ moduleGroups + ", typeId=" + getTypeId() + ", view=" + perspective + "]";
	}

	@Override
	public String getTreeLabel() {
		return name;
	}


}
