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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class SWTControlInstantiationVisitor extends ASTVisitor {

	private final Set<String> controlBlackList;
	private final List<MethodDeclaration> methods;
	private final VariabelCache variabelCache;

	public SWTControlInstantiationVisitor(final String[] controlBlacklist, final List<MethodDeclaration> methods) {
		this.methods = methods;
		this.controlBlackList = new HashSet<String>(Arrays.asList(controlBlacklist));
		this.variabelCache = new VariabelCache();
	}

	private MethodDeclaration getMethodDeclaration(final MethodInvocation invocation) {
		for (final MethodDeclaration decl : methods) {
			if (decl.getName().getFullyQualifiedName().equals(invocation.getName().getFullyQualifiedName())) {
				return decl;
			}
		}
		return null;
	}

	@Override
	public boolean visit(final MethodInvocation node) {
		final MethodDeclaration decla = getMethodDeclaration(node);

		// if the methodDeclaration was found, it is a private method and has to processed
		if (null != decla) {
			decla.getBody().accept(this);
		}
		return super.visit(node);
	}

	private MethodDeclaration findDeclaringMethod(final ASTNode current) {
		if (null == current) {
			return null;
		}

		if (current instanceof MethodDeclaration) {
			return (MethodDeclaration) current;
		}

		return findDeclaringMethod(current.getParent());
	}

	@Override
	public boolean visit(final ClassInstanceCreation node) {
		final ITypeBinding binding = node.resolveTypeBinding();

		final String fullClassName = binding.getQualifiedName();
		if (null != fullClassName && fullClassName.startsWith("org.eclipse.swt.widgets")) { //$NON-NLS-1$

			if (controlBlackList.contains(fullClassName)) {
				return false;
			}

			final ASTNode parent = node.getParent();

			if (parent instanceof VariableDeclaration) {
				final VariableDeclaration decl = (VariableDeclaration) parent;

				final String varName = decl.getName().getFullyQualifiedName();

				final MethodDeclaration enclosingMethod = findDeclaringMethod(node);
				if (null == enclosingMethod) {
					System.err.println("could not detect enclosing method for " + node);
					return false;
				}

				if (!variabelCache.isRegistered(enclosingMethod, varName)) {
					generateAddUIControlCall(enclosingMethod, varName);
				}
			}
		}
		return super.visit(node);
	}

	@SuppressWarnings("unchecked")
	private void generateAddUIControlCall(final MethodDeclaration enclosingMethod, final String variableName) {
		final AST ast = enclosingMethod.getAST();

		final MethodInvocation methodAddUIControl = ast.newMethodInvocation();

		methodAddUIControl.setName(ast.newSimpleName("addUIControl")); //$NON-NLS-1$
		methodAddUIControl.arguments().add(ast.newSimpleName(variableName));
		final StringLiteral ridgetId = ast.newStringLiteral();
		ridgetId.setLiteralValue(variableName);
		methodAddUIControl.arguments().add(ridgetId);

		enclosingMethod.getBody().statements().add(ast.newExpressionStatement(methodAddUIControl));
	}

	/**
	 * Caches for each MethodDeclaration all calls to addUIControl.
	 * 
	 */
	private static class VariabelCache {
		private final Map<MethodDeclaration, Set<String>> cache;

		public VariabelCache() {
			cache = new HashMap<MethodDeclaration, Set<String>>();
		}

		/**
		 * Returns true if the given {@link MethodDeclaration} contains a call
		 * to addUIControl for the given variableName.
		 * 
		 * @param enclosingMethod
		 * @param variableName
		 * @return
		 */
		public boolean isRegistered(final MethodDeclaration enclosingMethod, final String variableName) {
			Set<String> variables = cache.get(enclosingMethod);

			if (null == variables) {

				final AddUIControlCallVisitor addUIControlVisitor = new AddUIControlCallVisitor();
				enclosingMethod.accept(addUIControlVisitor);
				variables = addUIControlVisitor.getVariables();
				cache.put(enclosingMethod, variables);
			}

			return variables.contains(variableName);
		}

	}
}
