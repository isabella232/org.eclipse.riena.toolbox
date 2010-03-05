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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.swt.uibinding.SwtControlRidgetMapper;


public class UIControlVisitor extends ASTVisitor{
	
	private static final String CLASS_UI_CONTROLS_FACTORY = "UIControlsFactory"; //$NON-NLS-1$
	private static final String METHOD_ADD_UI_CONTROL = "addUIControl"; //$NON-NLS-1$
	private List<SwtControl> controls = new ArrayList<SwtControl>();
	private final List<MethodDeclaration> methods;
	
	public UIControlVisitor(List<MethodDeclaration> methods) {
		this.methods = methods;
	}
	
	private MethodDeclaration getMethodDeclaration(MethodInvocation invocation){
		for (MethodDeclaration decl: methods){
			if (decl.getName().getFullyQualifiedName().equals(invocation.getName().getFullyQualifiedName())){
				return decl;
			}
		}
		return null;
	}
	
	private Class<?> getRidgetInterface(String swtControlClassName){
		SwtControlRidgetMapper mapper = SwtControlRidgetMapper.getInstance();
		
		try {
			Class<?> clazz = Activator.getDefault().getBundle().loadClass(swtControlClassName);
			Class<? extends IRidget> ridget = mapper.getRidgetClass(clazz);
			Class<?> ridgetInterface = mapper.getPrimaryRidgetInterface(ridget);
			if (null != ridgetInterface){
				return ridgetInterface;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return IRidget.class;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		MethodDeclaration decla = getMethodDeclaration(node);
		
		if (null != decla){
			decla.getBody().accept(this);
		}
		
		Expression exp = node.getExpression();
		if (null != exp && 
				 exp instanceof SimpleName){
			SimpleName name = (SimpleName)exp;
			if (CLASS_UI_CONTROLS_FACTORY.equals(name.getIdentifier())){
				List<?> args = node.arguments();
				
				if (args.isEmpty()){
					System.err.println("call to UIControlsFactory without arguments: " + node);
					return true;
				}
				
				Object lastArgument =  args.get(args.size()-1);
				
				IMethodBinding typeBind = node.resolveMethodBinding();
				if (lastArgument instanceof SimpleName){
					SimpleName sm = (SimpleName)lastArgument;
					String value = getConstantStringFromSimpleName(sm);
					if (null != value){
						String swtControlClassName = typeBind.getReturnType().getQualifiedName();
						controls.add(new SwtControl(swtControlClassName, value, getRidgetInterface(swtControlClassName)));
					}
					else System.err.println("Parameter is not a constant " + sm);
					
				}
				else if (lastArgument instanceof StringLiteral){
					StringLiteral sm = (StringLiteral)lastArgument;
					String swtControlClassName = typeBind.getReturnType().getQualifiedName();
					controls.add(new SwtControl(swtControlClassName, sm.getLiteralValue(), getRidgetInterface(swtControlClassName)));
				}
				else{
					System.err.println("unknown arg type " + lastArgument);
				}
			}
		}
		else if (METHOD_ADD_UI_CONTROL.equals(node.getName().getIdentifier())){
			List<?> args = node.arguments();

			if (args.size() < 2){
				System.err.println("call to addUIControl without arguments: " + node);
				return true; 
			}
			
			SimpleName swtControl = (SimpleName) args.get(0);
			
			IBinding swtControlBinding = swtControl.resolveBinding();
			
			if (swtControlBinding instanceof IVariableBinding){
				IVariableBinding decl = ((IVariableBinding)swtControlBinding).getVariableDeclaration();
				ITypeBinding type = decl.getType();
				StringLiteral ridgetId = (StringLiteral) args.get(1);
				String swtControlClassName = type.getQualifiedName();
				controls.add(new SwtControl(swtControlClassName, ridgetId.getLiteralValue(), getRidgetInterface(swtControlClassName)));
			}
		}
		else{
			if (null != exp){
				if (Platform.inDebugMode()){
					System.out.println("DEBUG: expression type " + exp.getClass() +" node: " + node);
				}
			}
		}
		
		return true;
	}
	
	private String getConstantStringFromSimpleName(SimpleName simpleName){
		IBinding swtControlBinding = simpleName.resolveBinding();
		if (swtControlBinding instanceof IVariableBinding){
			IVariableBinding decl = ((IVariableBinding)swtControlBinding).getVariableDeclaration();
			return  (String) decl.getConstantValue();
		}
		return null;
	}

	public List<SwtControl> getControls() {
		return controls;
	}
}