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
 * This class represents a ModuleGroup in the Riena-Navigation.
 *
 */
public class ModuleGroupNode extends AbstractTypedNode<ModuleNode> {

	private List<ModuleNode> modules;
	private String instanceId;
	
	public ModuleGroupNode(AbstractAssemblyNode parent) {
		super(parent);
		modules = new ArrayList<ModuleNode>();
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	@Override
	public List<ModuleNode> getChildren() {
		return modules;
	}

	
	@Override
	public boolean add(ModuleNode e) {
		return modules.add(e);
	}

	@Override
	public String toString() {
		return "ModuleGroupNode [instanceId=" + instanceId + ", modules="
				+ modules + ", typeId=" + getTypeId() + "]";
	}

	@Override
	public String getTreeLabel() {
		return name;
	}


}
