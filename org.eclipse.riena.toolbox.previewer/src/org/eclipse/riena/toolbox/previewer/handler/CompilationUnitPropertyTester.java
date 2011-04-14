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
package org.eclipse.riena.toolbox.previewer.handler;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.toolbox.previewer.WorkspaceClassLoader;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;

/**
 * Checks if a given {@link ICompilationUnit} is a instance of {@link Composite}
 * or {@link ViewPart}.
 * 
 */
public class CompilationUnitPropertyTester extends PropertyTester {
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {

		final ICompilationUnit comp = (ICompilationUnit) receiver;
		final ViewPartInfo partInfo = WorkspaceClassLoader.getInstance().loadClass(comp, false);
		if (null == partInfo) {
			return false;
		}
		return WorkspaceClassLoader.getInstance().isValidType(partInfo.getType());
	}

}
