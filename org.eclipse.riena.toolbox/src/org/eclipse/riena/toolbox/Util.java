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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;

public final class Util {
	public static boolean isGiven(final String in) {
		return (null != in && in.trim().length() > 0);
	}

	/**
	 * Converts a given nodeId to a clean String:
	 * <p>
	 * - removes whitespace
	 * <p>
	 * - converts the complete String to lowercase
	 * 
	 * 
	 * @param nodeId
	 * @param toLowerCase
	 * 
	 * @return
	 */
	public static String cleanNodeId(final String nodeId, final boolean toLowerCase) {
		String cleanNodeId = nodeId;
		if (toLowerCase) {
			cleanNodeId = cleanNodeId.toLowerCase();
		}

		cleanNodeId = cleanNodeId.replaceAll("\\s", ""); // remove Whitespace //$NON-NLS-1$ //$NON-NLS-2$
		cleanNodeId = cleanNodeId.replaceAll("[^A-Za-z0-9]+", ""); // remove invalid Characters //$NON-NLS-1$ //$NON-NLS-2$

		if (!toLowerCase && cleanNodeId.length() > 1) {
			cleanNodeId = Character.toUpperCase(cleanNodeId.charAt(0)) + cleanNodeId.substring(1); // Capitalize first Character
		}

		return cleanNodeId;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractAssemblyNode<?>> T findParentOfType(final AbstractAssemblyNode<?> current,
			final Class<? extends T> type) {

		if (null == current) {
			return null;
		}

		if (type.isAssignableFrom(current.getClass())) {
			return (T) current;
		}
		return findParentOfType(current.getParent(), type);
	}

	public static void logWarning(final String message) {
		Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, message));
	}

	public static void logError(final Exception e) {
		e.printStackTrace();
		Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
	}

	public static void logInfo(final String message) {
		Activator.getDefault().getLog().log(new Status(Status.INFO, Activator.PLUGIN_ID, message));
		System.out.println(message);
	}

	public static void showError(Throwable e) {
		// FIXME move Utilitiy-class to common bundle and use it in all toolbox bundles
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getTargetException();
		}

		IStatus status = null;
		if (e instanceof CoreException) {
			status = ((CoreException) e).getStatus();
		} else {
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), IStatus.OK,
					e.getMessage(), e);
		}

		StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG);
	}

	private Util() {
		// private
	}

}
