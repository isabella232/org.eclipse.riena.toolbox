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
 * This node represents a Assembly in Riena.
 * 
 */
public class AssemblyNode extends AbstractAssemblyNode<AbstractTypedNode> {
	private String id;
	private String parentTypeId;
	private String name;
	private Integer autostartSequence;
	private String assembler;
	private String ref;
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

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentTypeId() {
		return parentTypeId;
	}

	public void setParentTypeId(String parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAutostartSequence() {
		return autostartSequence;
	}

	public void setAutostartSequence(Integer autostartSequence) {
		this.autostartSequence = autostartSequence;
	}

	public boolean add(AbstractTypedNode resultNode) {
		children.clear();
		children.add(resultNode);
		return true;
	}

	public List<AbstractTypedNode> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "AssemblyNode [assembler=" + assembler + ", autostartSequence=" + autostartSequence + ", id=" + id + ", name=" + name
				+ ", parentTypeId=" + parentTypeId + ", ref=" + ref + "]";
	}

	@Override
	public String getTreeLabel() {
		if (name != null && name.length()>0) {
			return name;
		}
		return "(id="+id+")";
	}

}
