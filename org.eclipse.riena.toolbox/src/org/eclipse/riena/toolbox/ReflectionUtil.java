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
package org.eclipse.riena.toolbox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.toolbox.assemblyeditor.model.ViewPartInfo;

// FIXME move ReflectionUtil to common-bundle
public final class ReflectionUtil {

	/**
	 * @param viewPart
	 * @param parent
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeMethod(final String methodName, final Object viewPart, final Composite parent) {
		for (final Method method : viewPart.getClass().getDeclaredMethods()) {
			if (methodName.equals(method.getName())) {
				method.setAccessible(true);
				try {
					method.invoke(viewPart, parent);
					return true;
				} catch (final IllegalArgumentException e) {
					Util.showError(e);
					return false;
				} catch (final IllegalAccessException e) {
					Util.showError(e);
					return false;
				} catch (final InvocationTargetException e) {
					Util.showError(e);
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * @param viewPart
	 */
	public static Object loadClass(final ViewPartInfo viewPart) {
		try {
			return viewPart.getType().newInstance();
		} catch (final Exception e) {
			Util.showError(e);
		}
		return null;
	}

	private ReflectionUtil() {
		// private constructor
	}

}
