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
package org.eclipse.riena.toolbox.assemblyeditor.model;

import org.eclipse.jdt.core.ICompilationUnit;

// FIXME move ViewPartInfo to common-bundle
public class ViewPartInfo {
	private final String name;
	private final Class<?> type;
	private final ICompilationUnit compilationUnit;

	public ViewPartInfo(final String name, final Class<?> type, final ICompilationUnit comp) {
		super();
		this.name = name;
		this.type = type;
		this.compilationUnit = comp;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	@Override
	public String toString() {
		return "ViewPartInfo [name=" + name + ", type=" + type + ", compilationUnit=" + compilationUnit + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}