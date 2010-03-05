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
	
	private Set<String> controlBlackList;
	private List<MethodDeclaration> methods;
	private VariabelCache variabelCache;
	
	public SWTControlInstantiationVisitor(String[] controlBlacklist, List<MethodDeclaration> methods) {
		this.methods = methods;
		this.controlBlackList = new HashSet<String>(Arrays.asList(controlBlacklist));
		this.variabelCache = new VariabelCache();
	}
	
	private MethodDeclaration getMethodDeclaration(MethodInvocation invocation){
		for (MethodDeclaration decl: methods){
			if (decl.getName().getFullyQualifiedName().equals(invocation.getName().getFullyQualifiedName())){
				return decl;
			}
		}
		return null;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		MethodDeclaration decla = getMethodDeclaration(node);
		
		// if the methodDeclaration was found, it is a private method and has to processed
		if (null != decla){
			decla.getBody().accept(this);
		}
		return super.visit(node);
	}
	
	private MethodDeclaration findDeclaringMethod(ASTNode current){
		if (null == current){
			return null;
		}
		
		if (current instanceof MethodDeclaration){
			return (MethodDeclaration) current;
		}
		
		return  findDeclaringMethod(current.getParent());
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		ITypeBinding binding = node.resolveTypeBinding();
		
		String fullClassName = binding.getQualifiedName();
		if (null != fullClassName &&
			fullClassName.startsWith("org.eclipse.swt.widgets")){
			
			if (controlBlackList.contains(fullClassName)){
				return false;
			}
			
			ASTNode parent = node.getParent();
			
			if (parent instanceof VariableDeclaration){
				VariableDeclaration decl = (VariableDeclaration) parent;
				
				String varName = decl.getName().getFullyQualifiedName();

				
				MethodDeclaration enclosingMethod = findDeclaringMethod(node);
				if (null == enclosingMethod){
					System.err.println("could not detect enclosing method for " + node);
					return false;
				}
				
				if (!variabelCache.isRegistered(enclosingMethod, varName)){
					generateAddUIControlCall(enclosingMethod, varName);
				}
			}
		}
		return super.visit(node);
	}
	
	private void generateAddUIControlCall(MethodDeclaration enclosingMethod, String variableName){
		AST ast = enclosingMethod.getAST();
		
		MethodInvocation methodAddUIControl = ast.newMethodInvocation();
		
		methodAddUIControl.setName(ast.newSimpleName("addUIControl"));
		methodAddUIControl.arguments().add(ast.newSimpleName(variableName));
		StringLiteral ridgetId = ast.newStringLiteral();
		ridgetId.setLiteralValue(variableName);
		methodAddUIControl.arguments().add(ridgetId);
		
		enclosingMethod.getBody().statements().add(ast.newExpressionStatement(methodAddUIControl));
	}
	
	/**
	 * Caches for each MethodDeclaration all calls to addUIControl. 
	 *
	 */
	private static class VariabelCache{
		private Map<MethodDeclaration, Set<String>> cache;
		
		public VariabelCache() {
			cache = new HashMap<MethodDeclaration, Set<String>>();
		}
		
		/**
		 * Returns true if the given {@link MethodDeclaration} contains a call to addUIControl for the given variableName.
		 * 
		 * @param enclosingMethod
		 * @param variableName
		 * @return
		 */
		public boolean isRegistered(MethodDeclaration enclosingMethod, String variableName){
			Set<String> variables = cache.get(enclosingMethod);
			
			if (null == variables){
			
				AddUIControlCallVisitor addUIControlVisitor = new AddUIControlCallVisitor();
				enclosingMethod.accept(addUIControlVisitor);
				variables = addUIControlVisitor.getVariables();
				cache.put(enclosingMethod, variables);
			}
			
			return variables.contains(variableName);
		}
		
	}
}
