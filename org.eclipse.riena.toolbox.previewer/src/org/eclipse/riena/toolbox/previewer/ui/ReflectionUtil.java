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
package org.eclipse.riena.toolbox.previewer.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;

public final class ReflectionUtil {

	/**
	 * @param viewPart
	 * @param parent
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeMethod(final String methodName, final Object viewPart, final Composite parent) {
		for (final Method method : viewPart.getClass().getMethods()) {
			if (methodName.equals(method.getName())) {
				method.setAccessible(true);
				try {
					method.invoke(viewPart, parent);
				} catch (final IllegalArgumentException e) {
					WorkbenchUtil.handleException(e, "Can not invoke method: " + methodName);
					return false;
				} catch (final IllegalAccessException e) {
					WorkbenchUtil.handleException(e, "Can not invoke method: " + methodName);
					return false;
				} catch (final InvocationTargetException e) {
					WorkbenchUtil.handleException(e, "Can not invoke method: " + methodName);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public static Object newInstance(final Class<?> type, final Composite parent) {
		for (final Constructor<?> constructor : type.getConstructors()) {
			if (Arrays.equals(new Class[] { Composite.class, Integer.TYPE }, constructor.getParameterTypes())) {
				constructor.setAccessible(true);
				return callConstructor(constructor, parent, SWT.None);
			} else if (Arrays.equals(new Class[] { Composite.class }, constructor.getParameterTypes())) {
				constructor.setAccessible(true);
				return callConstructor(constructor, parent);
			}
		}
		return null;
	}

	/**
	 * @param parent
	 * @param constructor
	 * @return
	 */
	public static Object callConstructor(final Constructor<?> constructor, final Object... params) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(params);
		} catch (final IllegalArgumentException e) {
			WorkbenchUtil.handleException(e);
		} catch (final InstantiationException e) {
			WorkbenchUtil.handleException(e);
		} catch (final IllegalAccessException e) {
			WorkbenchUtil.handleException(e);
		} catch (final InvocationTargetException e) {
			WorkbenchUtil.handleException(e);
		}
		return null;
	}

	/**
	 * @param viewPart
	 */
	public static Object loadClass(final ViewPartInfo viewPart) {
		try {
			return viewPart.getType().newInstance();
		} catch (final IllegalArgumentException e) {
			WorkbenchUtil.handleException(e);
		} catch (final SecurityException e) {
			WorkbenchUtil.handleException(e);
		} catch (final InstantiationException e) {
			WorkbenchUtil.handleException(e);
		} catch (final IllegalAccessException e) {
			WorkbenchUtil.handleException(e);
		}
		return null;
	}

	private ReflectionUtil() {
		// private constructor
	}

}
