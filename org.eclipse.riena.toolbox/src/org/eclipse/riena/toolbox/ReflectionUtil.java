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

import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.core.util.ReflectionFailure;
import org.eclipse.riena.core.util.ReflectionUtils;
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
		try {

			ReflectionUtils.invokeHidden(viewPart, methodName, parent);
			return true;
		} catch (final ReflectionFailure f) {
			Util.logError(f);
			return false;
		}
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
