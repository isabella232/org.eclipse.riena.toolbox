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

import org.apache.velocity.util.StringUtils;
import org.eclipse.riena.toolbox.Util;

/**
 * This node represents a Assembly in Riena.
 * 
 */
public class AssemblyNode extends AbstractAssemblyNode<AbstractTypedNode> {
	private String id;
	private String assembler;
	private String parentNodeId;
	private Integer startOrder;
	
	private String prefix;
	private String suffix;
	private List<AbstractTypedNode> children;

	public AssemblyNode(AbstractAssemblyNode parent) {
		super(parent);
		children = new ArrayList<AbstractTypedNode>();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getAssembler() {
		return assembler;
	}

	public void setAssembler(String assembler) {
		this.assembler = assembler;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNodeTypeId() {
		return parentNodeId;
	}

	public void setNodeTypeId(String parentTypeId) {
		this.parentNodeId = parentTypeId;
	}

	public Integer getAutostartSequence() {
		return startOrder;
	}

	public void setAutostartSequence(Integer autostartSequence) {
		this.startOrder = autostartSequence;
	}

	@Override
	public boolean add(AbstractTypedNode resultNode) {
		children.add(resultNode);
		return true;
	}

	@Override
	public List<AbstractTypedNode> getChildren() {
		return children;
	}

	@Override
	public String getTreeLabel() {
		if (Util.isGiven(name)) {
			return name;
		}
		return "(id="+id+")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String toString() {
		return "AssemblyNode [id=" + id + ", assembler=" + assembler + ", parentNodeId=" + parentNodeId + ", name=" + name //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ ", startOrder=" + startOrder + ", prefix=" + prefix + ", suffix=" + suffix + ", children=" + children + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssemblyNode other = (AssemblyNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
