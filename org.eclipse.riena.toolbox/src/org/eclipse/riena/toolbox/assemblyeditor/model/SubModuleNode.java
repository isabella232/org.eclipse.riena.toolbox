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

import org.eclipse.riena.toolbox.Util;

/**
 * This class represents a SubModule in the Riena-Navigation.
 * 
 */
public class SubModuleNode extends AbstractTypedNode<SubModuleNode> {
	private final List<SubModuleNode> subModules;
	private RCPView rcpView;
	private String controller;
	private boolean shared;
	private String icon;
	private boolean selectable = true;
	private boolean requiresPreparation;
	private boolean visible = true;
	private boolean expanded;

	public SubModuleNode(final AbstractAssemblyNode parent) {
		super(parent);
		subModules = new ArrayList<SubModuleNode>();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isRequiresPreparation() {
		return requiresPreparation;
	}

	public void setRequiresPreparation(final boolean requiresPreparation) {
		this.requiresPreparation = requiresPreparation;
	}

	public boolean hasViewClass() {
		if (null != getRcpView()) {
			return Util.isGiven(getRcpView().getViewClass());
		}
		return false;
	}

	public RCPView getRcpView() {
		return rcpView;
	}

	public void setRcpView(final RCPView view) {
		this.rcpView = view;
	}

	public String getController() {
		return controller;
	}

	public void setController(final String controller) {
		this.controller = controller;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(final boolean shared) {
		this.shared = shared;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(final String icon) {
		this.icon = icon;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(final boolean selectable) {
		this.selectable = selectable;
	}

	@Override
	public List<SubModuleNode> getChildren() {
		return subModules;
	}

	@Override
	public boolean add(final SubModuleNode e) {
		return subModules.add(e);
	}

	@Override
	protected String getTreeLabelValue() {
		return name;
	}

	@Override
	public String toString() {
		return "SubModuleNode [subModules=" + subModules + ", rcpView=" + rcpView + ", controller=" + controller + ", shared=" + shared //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ ", icon=" + icon + ", selectable=" + selectable + ", requiresPreparation=" + requiresPreparation + ", nodeId=" + nodeId //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ ", name=" + name + "]"; //$NON-NLS-1$
	}
}
