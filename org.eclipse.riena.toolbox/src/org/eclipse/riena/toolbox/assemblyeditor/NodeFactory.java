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
package org.eclipse.riena.toolbox.assemblyeditor;

import org.eclipse.core.runtime.Assert;

import org.eclipse.riena.toolbox.assemblyeditor.api.INodeFactory;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractTypedNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

public class NodeFactory implements INodeFactory {

	private static final String SEP = "."; //$NON-NLS-1$
	private final String SUFFIX_ASSEMBLY = "assembly"; //$NON-NLS-1$
	private final String SUFFIX_SUBAPPLICATION = "subapp"; //$NON-NLS-1$
	private final String SUFFIX_MODULE_GROUP = "modulegroup"; //$NON-NLS-1$
	private final String SUFFIX_MODULE = "module"; //$NON-NLS-1$
	private final String SUFFIX_SUBMODULE = "submodule"; //$NON-NLS-1$
	private final String SUFFIX_PERSPECTIVE = "perspective"; //$NON-NLS-1$

	private final String CONST_NEW_IDENTIFIER = "NEW"; //$NON-NLS-1$

	public AssemblyNode createAssembly(final BundleNode parentBundle) {
		final AssemblyNode ass = new AssemblyNode(parentBundle);
		setPreSuffixes(parentBundle, ass, SUFFIX_ASSEMBLY);
		ass.setName(CONST_NEW_IDENTIFIER);
		ass.setNodeTypeId("application");
		ass.setBundle(parentBundle);
		return ass;
	}

	public ModuleNode createModule(final AbstractAssemblyNode parent, final BundleNode parentBundle) {
		final ModuleNode node = new ModuleNode(parent);
		setPreSuffixes(parentBundle, node, SUFFIX_MODULE);
		node.setName(CONST_NEW_IDENTIFIER);
		node.setBundle(parentBundle);
		node.setCloseable(true);
		return node;
	}

	public ModuleGroupNode createModuleGroup(final AbstractAssemblyNode parent, final BundleNode parentBundle) {
		final ModuleGroupNode node = new ModuleGroupNode(parent);
		setPreSuffixes(parentBundle, node, SUFFIX_MODULE_GROUP);
		node.setName(CONST_NEW_IDENTIFIER);
		node.setBundle(parentBundle);
		return node;
	}

	public SubApplicationNode createSubApplication(final AbstractAssemblyNode parent, final BundleNode parentBundle) {
		final SubApplicationNode node = new SubApplicationNode(parent);
		setPreSuffixes(parentBundle, node, SUFFIX_SUBAPPLICATION);
		node.setBundle(parentBundle);
		node.setName(CONST_NEW_IDENTIFIER);
		return node;
	}

	public SubModuleNode createSubModule(final AbstractAssemblyNode parent, final BundleNode parentBundle) {
		final SubModuleNode node = new SubModuleNode(parent);
		setPreSuffixes(parentBundle, node, SUFFIX_SUBMODULE);
		node.setName(CONST_NEW_IDENTIFIER);
		node.setBundle(parentBundle);
		node.setSelectable(true);
		return node;
	}

	public RCPPerspective createRcpPerspective(final SubApplicationNode parent) {
		Assert.isNotNull(parent);
		Assert.isNotNull(parent.getName());

		final RCPPerspective persp = new RCPPerspective();
		final String id = parent.getBundle().getName() + SEP + parent.getName() + SEP + SUFFIX_PERSPECTIVE;
		persp.setId(id);
		persp.setName(id);
		persp.setPerspectiveClass(RCPPerspective.PERSPECTIVE_CLASS_NAME);
		return persp;
	}

	private void setPreSuffixes(final AbstractAssemblyNode parent, final AssemblyNode typedNode, final String suffix) {
		typedNode.setPrefix(parent.getName() + SEP);
		typedNode.setSuffix(SEP + suffix);
		typedNode.setId(typedNode.getPrefix() + CONST_NEW_IDENTIFIER + typedNode.getSuffix());

	}

	private void setPreSuffixes(final AbstractAssemblyNode parent, final AbstractTypedNode typedNode,
			final String suffix) {
		typedNode.setPrefix(parent.getName() + SEP);
		typedNode.setSuffix(SEP + suffix);
		typedNode.setNodeId(typedNode.getPrefix() + CONST_NEW_IDENTIFIER + typedNode.getSuffix());

	}

}
