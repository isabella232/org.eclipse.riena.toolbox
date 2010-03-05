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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

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
	private final static String[] CONTROL_BLACKLIST = { "org.eclipse.swt.widgets.Label" }; //$NON-NLS-1$

	public AddUIControlCallGenerator(IProject project) {
		super(project);
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
	public boolean generateAddUIControlCalls(String fullyQualifiedClassName) {
		CompilationUnit astNode = findCompilationUnit(fullyQualifiedClassName);
		if (null == astNode) {
			return false;
		}

		astNode.recordModifications();

		MethodDeclaration methodBasicCreatePartControl = findMethod(astNode, METHOD_BASIC_CREATE_PART_CONTROL);
		if (null == methodBasicCreatePartControl) {
			return false;
		}
		
		CollectMethodDeclerationsVisitor collector = new CollectMethodDeclerationsVisitor();
		astNode.accept(collector);
		
		
		SWTControlInstantiationVisitor controlCollector = new SWTControlInstantiationVisitor(CONTROL_BLACKLIST, collector.getMethods());
		methodBasicCreatePartControl.accept(controlCollector);

		return saveDocument(astNode);
	}
}
