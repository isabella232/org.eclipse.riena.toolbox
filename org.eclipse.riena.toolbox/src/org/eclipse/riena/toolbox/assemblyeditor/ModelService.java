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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.api.IModelService;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractTypedNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

public class ModelService implements IModelService {

	private final static String CONST_APPLICATION = "application"; //$NON-NLS-1$

	public Set<String> getAllParentTypeIds(final AssemblyModel model) {
		final Set<String> typeIds = getAllTypeIds(model, null);
		typeIds.add(CONST_APPLICATION);
		return typeIds;
	}

	public Set<String> getAllTypeIds(final AssemblyModel model, final AbstractTypedNode<?> ignoreNode) {
		final Set<String> typeIds = new HashSet<String>();
		typeIds.add(CONST_APPLICATION);

		for (final BundleNode bundle : model.getChildren()) {
			for (final AssemblyNode ass : bundle.getChildren()) {
				for (final AbstractTypedNode<?> typeNode : ass.getChildren()) {

					if (!typeNode.equals(ignoreNode) && Util.isGiven(typeNode.getNodeId())) {
						typeIds.add(typeNode.getNodeId());
					}

					findTypeIds(typeIds, typeNode, ignoreNode);
				}
			}
		}
		return typeIds;
	}

	private void findTypeIds(final Set<String> ids, final AbstractTypedNode<?> parent,
			final AbstractTypedNode<?> ignoreNode) {
		for (final Object typeNode : parent.getChildren()) {
			final AbstractTypedNode<?> abs = (AbstractTypedNode<?>) typeNode;

			if (!typeNode.equals(ignoreNode)) {
				if (null != abs && Util.isGiven(abs.getNodeId())) {
					ids.add(abs.getNodeId());
				}
			}

			if (!abs.getChildren().isEmpty()) {
				findTypeIds(ids, abs, ignoreNode);
			}
		}
	}

	public SubModuleNode findSubModuleByClassName(final AssemblyModel model, final IProject project,
			final String className) {
		final BundleNode bundle = model.getBundle(project);

		if (null == bundle) {
			return null;
		}

		final ClassNameVisitor nodeVisitor = new ClassNameVisitor(className);

		for (final AssemblyNode ass : bundle.getChildren()) {
			for (final AbstractTypedNode<?> child : ass.getChildren()) {
				visitNode(child, nodeVisitor);
			}
		}
		return nodeVisitor.getFoundNode();
	}

	private void visitNode(final AbstractTypedNode<?> parent, final AssemblyNodeVisitor visitor) {
		for (final Object typeNode : parent.getChildren()) {
			final AbstractTypedNode<?> abs = (AbstractTypedNode<?>) typeNode;
			if (visitor.visit(abs)) {
				visitNode(abs, visitor);
			} else {
				return;
			}
		}
	}

	private final static class ClassNameVisitor implements AssemblyNodeVisitor {
		private final String className;
		private SubModuleNode foundNode;

		private ClassNameVisitor(final String className) {
			this.className = className;
		}

		public boolean visit(final AbstractTypedNode<?> node) {
			if (node instanceof SubModuleNode) {
				final SubModuleNode subMod = (SubModuleNode) node;
				if (className.equals(subMod.getController())) {
					foundNode = subMod;
					return false;
				}

				if (null != subMod.getRcpView() && className.equals(subMod.getRcpView().getViewClass())) {
					foundNode = subMod;
					return false;
				}
			}
			return true;
		}

		public SubModuleNode getFoundNode() {
			return foundNode;
		}
	}

	private interface AssemblyNodeVisitor {
		public boolean visit(AbstractTypedNode<?> node);
	}

}
