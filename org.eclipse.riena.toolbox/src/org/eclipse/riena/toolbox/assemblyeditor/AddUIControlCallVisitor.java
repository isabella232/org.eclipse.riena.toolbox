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

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * Collects all addUIControl-Calls
 * 
 */
public class AddUIControlCallVisitor extends ASTVisitor {

	private final Set<String> variables = new HashSet<String>();

	@Override
	public boolean visit(final MethodInvocation node) {
		if ("addUIControl".equals(node.getName().getFullyQualifiedName())) { //$NON-NLS-1$
			if (!node.arguments().isEmpty()) {
				final Object obj = node.arguments().get(0);

				if (obj instanceof SimpleName) {
					final SimpleName sn = (SimpleName) obj;
					final String varName = sn.getFullyQualifiedName();
					variables.add(varName);
				}
			}
		}
		return true;
	}

	public Set<String> getVariables() {
		return variables;
	}
}