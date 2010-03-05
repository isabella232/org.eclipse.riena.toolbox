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

import org.eclipse.riena.toolbox.Util;


/**
 * This class represents a SubModule in the Riena-Navigation.
 *
 */
public class SubModuleNode extends AbstractTypedNode<SubModuleNode> {
	private List<SubModuleNode> subModules;
	private String instanceId;
	private RCPView rcpView;
	private String controller;
	private boolean shared;
	private String icon;
	private boolean selectable;
	private String label;

	public SubModuleNode(AbstractAssemblyNode parent) {
		super(parent);
		subModules = new ArrayList<SubModuleNode>();
	}

	public boolean hasViewClass(){
		if (null != getRcpView()){
			return Util.isGiven(getRcpView().getViewClass());
		}
		return false;
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

	public RCPView getRcpView() {
		return rcpView;
	}

	public void setRcpView(RCPView view) {
		this.rcpView = view;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
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
		return "SubModuleNode [controller=" + controller + ", icon=" + icon
				+ ", instanceId=" + instanceId + ", label=" + label
				+ ", prefix=" + getPrefix() + ", selectable=" + selectable
				+ ", shared=" + shared + ", subModules=" + subModules
				+ ", suffix=" + getSuffix() + ", viewId=" + rcpView + "]";
	}

	@Override
	public String getTreeLabel() {
		return name;
	}
}
