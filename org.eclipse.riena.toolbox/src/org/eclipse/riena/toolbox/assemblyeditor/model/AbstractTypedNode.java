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

import org.eclipse.riena.toolbox.Util;

/**
 * A Node that has a typeId
 * 
 * @param <T>
 *            the type of the childNode
 */
public abstract class AbstractTypedNode<T extends AbstractTypedNode> extends AbstractAssemblyNode<T> {

	public AbstractTypedNode(final AbstractAssemblyNode parent) {
		super(parent);
	}

	protected String nodeId;
	private String prefix;
	private String suffix;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(final String typeId) {
		this.nodeId = typeId;
	}

	public AssemblyNode getAssemblyNode() {
		return Util.findParentOfType(this, AssemblyNode.class);
	}

	@Override
	public String getTreeLabel() {
		final String treeLabelValue = getTreeLabelValue();
		return Util.isGiven(treeLabelValue) ? treeLabelValue : "(nodeId=" + nodeId + ")"; //$NON-NLS-1$ //$NON-NLS-2$;
	}

	protected abstract String getTreeLabelValue();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		final AssemblyNode ass = getAssemblyNode();
		result = prime * result + ((ass == null) ? 0 : ass.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractTypedNode other = (AbstractTypedNode) obj;
		final AssemblyNode ass = getAssemblyNode();
		final AssemblyNode otherAss = other.getAssemblyNode();

		if (ass == null) {
			if (otherAss != null)
				return false;
		} else if (!ass.equals(otherAss))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}
}
