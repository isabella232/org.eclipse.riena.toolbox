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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.BindingException;
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
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.ui.preferences.PreferenceConstants;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.swt.uibinding.SwtControlRidgetMapper;

public class UIControlVisitor extends ASTVisitor {

	private static final String CLASS_UI_CONTROLS_FACTORY = "UIControlsFactory"; //$NON-NLS-1$
	private static final String METHOD_ADD_UI_CONTROL = "addUIControl"; //$NON-NLS-1$
	private List<SwtControl> controls = new ArrayList<SwtControl>();
	private final List<MethodDeclaration> methods;
	private List<String> uiControlFactoryNames = new ArrayList<String>();

	/**
	 * List of controls that have to be ignored, when searching for SWT-Controls
	 * in the ViewClass
	 */
	private List<String> controlBlacklist = new ArrayList<String>();

	public UIControlVisitor(List<MethodDeclaration> methods) {
		this.methods = methods;
		this.uiControlFactoryNames.add(CLASS_UI_CONTROLS_FACTORY);

		String customUIControlsFactory = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.CONST_CUSTOM_UI_CONTROLS_FACTORY);
		if (Util.isGiven(customUIControlsFactory)) {
			uiControlFactoryNames.add(customUIControlsFactory);
		}

		String blackListString = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.CONST_CONFIGURE_RIDGETS_BLACKLIST);
		if (Util.isGiven(blackListString)) {
			controlBlacklist = Arrays.asList(blackListString.split(";"));
		}
	}

	private MethodDeclaration getMethodDeclaration(MethodInvocation invocation) {
		for (MethodDeclaration decl : methods) {
			if (decl.getName().getFullyQualifiedName().equals(invocation.getName().getFullyQualifiedName())) {
				return decl;
			}
		}
		return null;
	}

	private Class<?> getRidgetInterface(String swtControlClassName) {
		SwtControlRidgetMapper mapper = SwtControlRidgetMapper.getInstance();

		try {
			Class<?> clazz = Activator.getDefault().getBundle().loadClass(swtControlClassName);
			Class<? extends IRidget> ridget = mapper.getRidgetClass(clazz);
			Class<?> ridgetInterface = mapper.getPrimaryRidgetInterface(ridget);
			if (null != ridgetInterface) {
				return ridgetInterface;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (BindingException e) {
			return null;
		}
		return IRidget.class;
	}

	@Override
	public boolean visit(MethodInvocation methodCall) {

		MethodDeclaration decla = getMethodDeclaration(methodCall);

		if (null != decla) {
			decla.getBody().accept(this);
		}

		IMethodBinding methodBinding = methodCall.resolveMethodBinding();
		String className = methodBinding.getDeclaringClass().getName();
		String superClassName = methodBinding.getDeclaringClass().getSuperclass().getName();
		Expression exp = methodCall.getExpression();
		if (uiControlFactoryNames.contains(className) || uiControlFactoryNames.contains(superClassName)) {
			List<?> args = methodCall.arguments();

			if (args.isEmpty()) {
				System.err.println("call to UIControlsFactory without arguments: " + methodCall);
				return true;
			}

			Object lastArgument = args.get(args.size() - 1);

			IMethodBinding typeBind = methodCall.resolveMethodBinding();
			// TODO annotate bindingId in UIControlsFactory to detect the correct parameter

			if (lastArgument instanceof SimpleName) {
				SimpleName sm = (SimpleName) lastArgument;
				String value = getConstantStringFromSimpleName(sm);
				if (null != value) {
					String swtControlClassName = typeBind.getReturnType().getQualifiedName();
					Class<?> ridgetClass = getRidgetInterface(swtControlClassName);
					if (null != ridgetClass) {
						controls.add(new SwtControl(swtControlClassName, value, ridgetClass));
					}
				} else {
					System.err.println("Parameter is not a constant " + sm);
				}
			} else if (lastArgument instanceof org.eclipse.jdt.core.dom.QualifiedName) {
				// TODO use the constant in the Controller, instead of extracting the value
				org.eclipse.jdt.core.dom.QualifiedName sm = (org.eclipse.jdt.core.dom.QualifiedName) lastArgument;
				String value = getConstantStringFromSimpleName(sm.getName());
				if (null != value) {
					String swtControlClassName = typeBind.getReturnType().getQualifiedName();
					Class<?> ridgetClass = getRidgetInterface(swtControlClassName);
					if (null != ridgetClass) {
						controls.add(new SwtControl(swtControlClassName, value, ridgetClass));
					}
				} else {
					System.err.println("Parameter is not a constant " + sm);
				}
			} else if (lastArgument instanceof StringLiteral) {
				StringLiteral sm = (StringLiteral) lastArgument;

				String swtControlClassName = typeBind.getReturnType().getQualifiedName();
				Class<?> ridgetClass = getRidgetInterface(swtControlClassName);
				if (null != ridgetClass) {
					controls.add(new SwtControl(swtControlClassName, sm.getLiteralValue(), ridgetClass));
				}
			} else {
				System.err.println("unknown arg type " + lastArgument);
			}
		} else if (METHOD_ADD_UI_CONTROL.equals(methodCall.getName().getIdentifier())) {
			List<?> args = methodCall.arguments();

			if (args.size() < 2) {
				System.err.println("call to addUIControl without arguments: " + methodCall);
				return true;
			}

			SimpleName swtControl = (SimpleName) args.get(0);

			IBinding swtControlBinding = swtControl.resolveBinding();

			if (swtControlBinding instanceof IVariableBinding) {
				IVariableBinding decl = ((IVariableBinding) swtControlBinding).getVariableDeclaration();
				ITypeBinding type = decl.getType();
				StringLiteral ridgetId = (StringLiteral) args.get(1);
				String swtControlClassName = type.getQualifiedName();
				controls.add(new SwtControl(swtControlClassName, ridgetId.getLiteralValue(),
						getRidgetInterface(swtControlClassName)));
			}
		} else {
			if (null != exp) {
				if (Platform.inDebugMode()) {
					System.out.println("DEBUG: expression type " + exp.getClass() + " node: " + methodCall);
				}
			}
		}

		return true;
	}

	private String getConstantStringFromSimpleName(SimpleName simpleName) {
		IBinding swtControlBinding = simpleName.resolveBinding();
		if (swtControlBinding instanceof IVariableBinding) {
			IVariableBinding decl = ((IVariableBinding) swtControlBinding).getVariableDeclaration();
			Object contValue = decl.getConstantValue();
			return null != contValue ? contValue.toString() : null;
		}
		return null;
	}

	public List<SwtControl> getControls() {
		List<SwtControl> out = new ArrayList<SwtControl>();
		for (SwtControl control : controls) {
			if (!controlBlacklist.contains(control.getSwtControlClassName())) {
				out.add(control);
			} else {
				System.out.println("UIControlVisitor.getControls() ignoreControl " + control.getSwtControlClassName());
			}
		}
		return out;
	}
}