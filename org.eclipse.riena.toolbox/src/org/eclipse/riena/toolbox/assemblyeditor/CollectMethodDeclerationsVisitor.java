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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CollectMethodDeclerationsVisitor extends ASTVisitor {

	private final List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

	@Override
	public boolean visit(final MethodDeclaration node) {
		methods.add(node);
		return true;
	}

	public List<MethodDeclaration> getMethods() {
		return methods;
	}
}