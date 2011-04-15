/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
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
	private final List<SwtControl> controls = new ArrayList<SwtControl>();
	private final List<MethodDeclaration> methods;
	private final List<String> uiControlFactoryNames = new ArrayList<String>();

	/**
	 * List of controls that have to be ignored, when searching for SWT-Controls
	 * in the ViewClass
	 */
	private List<String> controlBlacklist = new ArrayList<String>();

	public UIControlVisitor(final List<MethodDeclaration> methods) {
		this.methods = methods;
		this.uiControlFactoryNames.add(CLASS_UI_CONTROLS_FACTORY);

		final String customUIControlsFactory = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.CONST_CUSTOM_UI_CONTROLS_FACTORY);
		if (Util.isGiven(customUIControlsFactory)) {
			uiControlFactoryNames.add(customUIControlsFactory);
		}

		final String blackListString = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.CONST_CONFIGURE_RIDGETS_BLACKLIST);
		if (Util.isGiven(blackListString)) {
			controlBlacklist = Arrays.asList(blackListString.split(";")); //$NON-NLS-1$
		}
	}

	private MethodDeclaration getMethodDeclaration(final MethodInvocation invocation) {
		for (final MethodDeclaration decl : methods) {
			if (decl.getName().getFullyQualifiedName().equals(invocation.getName().getFullyQualifiedName())) {
				return decl;
			}
		}
		return null;
	}

	private Class<?> getRidgetInterface(final String swtControlClassName) {
		final SwtControlRidgetMapper mapper = SwtControlRidgetMapper.getInstance();

		try {
			final Class<?> clazz = Activator.getDefault().getBundle().loadClass(swtControlClassName);
			final Class<? extends IRidget> ridget = mapper.getRidgetClass(clazz);
			final Class<?> ridgetInterface = mapper.getPrimaryRidgetInterface(ridget);
			if (null != ridgetInterface) {
				return ridgetInterface;
			}
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final BindingException e) {
			return null;
		}
		return IRidget.class;
	}

	@Override
	public boolean visit(final MethodInvocation methodCall) {

		final MethodDeclaration decla = getMethodDeclaration(methodCall);

		if (null != decla) {
			decla.getBody().accept(this);
		}

		final IMethodBinding methodBinding = methodCall.resolveMethodBinding();
		final String className = methodBinding.getDeclaringClass().getName();
		final String superClassName = methodBinding.getDeclaringClass().getSuperclass().getName();
		final Expression exp = methodCall.getExpression();
		if (uiControlFactoryNames.contains(className) || uiControlFactoryNames.contains(superClassName)) {
			final List<?> args = methodCall.arguments();

			if (args.isEmpty()) {
				System.err.println("call to UIControlsFactory without arguments: " + methodCall); //$NON-NLS-1$
				return true;
			}

			final Object lastArgument = args.get(args.size() - 1);

			final IMethodBinding typeBind = methodCall.resolveMethodBinding();
			// TODO annotate bindingId in UIControlsFactory to detect the correct parameter

			if (lastArgument instanceof SimpleName) {
				final SimpleName sm = (SimpleName) lastArgument;
				final String value = getConstantStringFromSimpleName(sm);
				if (null != value) {
					final String swtControlClassName = typeBind.getReturnType().getQualifiedName();
					final Class<?> ridgetClass = getRidgetInterface(swtControlClassName);
					if (null != ridgetClass) {
						controls.add(new SwtControl(swtControlClassName, value, ridgetClass));
					}
				} else {
					Util.logWarning("Parameter is not a constant " + sm); //$NON-NLS-1$
				}
			} else if (lastArgument instanceof org.eclipse.jdt.core.dom.QualifiedName) {
				// TODO use the constant in the Controller, instead of extracting the value
				final org.eclipse.jdt.core.dom.QualifiedName sm = (org.eclipse.jdt.core.dom.QualifiedName) lastArgument;
				final String value = getConstantStringFromSimpleName(sm.getName());
				if (null != value) {
					final String swtControlClassName = typeBind.getReturnType().getQualifiedName();
					final Class<?> ridgetClass = getRidgetInterface(swtControlClassName);
					if (null != ridgetClass) {
						controls.add(new SwtControl(swtControlClassName, value, ridgetClass));
					}
				} else {
					Util.logWarning("Parameter is not a constant " + sm); //$NON-NLS-1$
				}
			} else if (lastArgument instanceof StringLiteral) {
				final StringLiteral sm = (StringLiteral) lastArgument;

				final String swtControlClassName = typeBind.getReturnType().getQualifiedName();
				final Class<?> ridgetClass = getRidgetInterface(swtControlClassName);
				if (null != ridgetClass) {
					controls.add(new SwtControl(swtControlClassName, sm.getLiteralValue(), ridgetClass));
				}
			} else {
				Util.logWarning("unknown arg type " + lastArgument); //$NON-NLS-1$
			}
		} else if (METHOD_ADD_UI_CONTROL.equals(methodCall.getName().getIdentifier())) {
			final List<?> args = methodCall.arguments();

			if (args.size() < 2) {
				Util.logWarning("call to addUIControl without arguments: " + methodCall); //$NON-NLS-1$
				return true;
			}

			final SimpleName swtControl = (SimpleName) args.get(0);

			final IBinding swtControlBinding = swtControl.resolveBinding();

			if (swtControlBinding instanceof IVariableBinding) {
				final IVariableBinding decl = ((IVariableBinding) swtControlBinding).getVariableDeclaration();
				final ITypeBinding type = decl.getType();
				final StringLiteral ridgetId = (StringLiteral) args.get(1);
				final String swtControlClassName = type.getQualifiedName();
				controls.add(new SwtControl(swtControlClassName, ridgetId.getLiteralValue(),
						getRidgetInterface(swtControlClassName)));
			}
		} else {
			if (null != exp) {
				if (Platform.inDebugMode()) {
					Util.logInfo("DEBUG: expression type " + exp.getClass() + " node: " + methodCall); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		return true;
	}

	private String getConstantStringFromSimpleName(final SimpleName simpleName) {
		final IBinding swtControlBinding = simpleName.resolveBinding();
		if (swtControlBinding instanceof IVariableBinding) {
			final IVariableBinding decl = ((IVariableBinding) swtControlBinding).getVariableDeclaration();
			final Object contValue = decl.getConstantValue();
			return null != contValue ? contValue.toString() : null;
		}
		return null;
	}

	public List<SwtControl> getControls() {
		final List<SwtControl> out = new ArrayList<SwtControl>();
		for (final SwtControl control : controls) {
			if (!controlBlacklist.contains(control.getSwtControlClassName())) {
				out.add(control);
			} else {
				if (Platform.inDebugMode()) {
					Util.logInfo("UIControlVisitor.getControls() ignoreControl " //$NON-NLS-1$
							+ control.getSwtControlClassName());
				}
			}
		}
		return out;
	}
}