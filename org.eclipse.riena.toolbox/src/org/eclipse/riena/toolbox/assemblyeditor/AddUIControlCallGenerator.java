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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.ui.preferences.PreferenceConstants;

/**
 * Generates missing addUIControl-Calls in RCP-Views for SWT-Controls. By
 * default for all Classes in the package 'org.eclipse.swt.widgets' like Text,
 * Table etc. a addUIControl will be generated. With the CONTROL_BLACKLIST, this
 * behavior can be customized by declaring Classes that should be ignored.
 * 
 */
public class AddUIControlCallGenerator extends RidgetGenerator {

	/**
	 * List of SWT-Controls that will be ignored
	 */
	private String[] controlBlacklist = new String[] {};

	public AddUIControlCallGenerator(final IProject project) {
		super(project);
		final String blackListString = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.CONST_CONFIGURE_RIDGETS_BLACKLIST);
		if (Util.isGiven(blackListString)) {
			controlBlacklist = blackListString.split(";"); //$NON-NLS-1$
		}
	}

	/**
	 * Generates missing addUIControl-Calls for all SWT-Classintantiations like:
	 * 
	 * <code>
	 * Label lbl = new Label(parent, SWT.BORDER); 
	 * </code>
	 * 
	 * @param fullyQualifiedClassName
	 */
	public boolean generateAddUIControlCalls(final String fullyQualifiedClassName) {
		final CompilationUnit astNode = findCompilationUnit(fullyQualifiedClassName);
		if (null == astNode) {
			return false;
		}

		astNode.recordModifications();

		final MethodDeclaration methodBasicCreatePartControl = findMethod(astNode, METHOD_BASIC_CREATE_PART_CONTROL);
		if (null == methodBasicCreatePartControl) {
			return false;
		}

		final CollectMethodDeclerationsVisitor collector = new CollectMethodDeclerationsVisitor();
		astNode.accept(collector);

		final SWTControlInstantiationVisitor controlCollector = new SWTControlInstantiationVisitor(controlBlacklist,
				collector.getMethods());
		methodBasicCreatePartControl.accept(controlCollector);

		return saveDocument(astNode);
	}
}
