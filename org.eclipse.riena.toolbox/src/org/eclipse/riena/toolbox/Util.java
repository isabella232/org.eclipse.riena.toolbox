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
package org.eclipse.riena.toolbox;

import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;

public class Util {
	public static boolean isGiven(String in) {
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
	 * @return
	 */
	public static String cleanNodeId(final String nodeId) {
		String cleanNodeId = nodeId.toLowerCase();
		cleanNodeId = cleanNodeId.replaceAll("\\s", ""); // remove Whitespace //$NON-NLS-1$ //$NON-NLS-2$
		cleanNodeId = cleanNodeId.replaceAll("[^A-Za-z0-9]+", ""); // remove invalid Characters //$NON-NLS-1$ //$NON-NLS-2$
		if (cleanNodeId.length() > 1) {
			cleanNodeId = Character.toUpperCase(cleanNodeId.charAt(0)) + cleanNodeId.substring(1); // Capitalize first Character
		}
		return cleanNodeId;
	}

	public static <T extends AbstractAssemblyNode> T findParentOfType(AbstractAssemblyNode current,
			Class<? extends T> type) {

		if (null == current) {
			return null;
		}

		if (type.isAssignableFrom(current.getClass())) {
			return (T) current;
		}
		return findParentOfType(current.getParent(), type);
	}

}
