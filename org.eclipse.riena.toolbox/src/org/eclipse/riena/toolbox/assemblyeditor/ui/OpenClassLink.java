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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.riena.toolbox.assemblyeditor.RidgetGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;


public class OpenClassLink extends Composite {

	private Link lnk;
	private String className;
	private SubModuleNode subModule;
	
	public OpenClassLink(Composite parent, String text) {
		super(parent, SWT.None);
		Assert.isNotNull(text);
		setLayout(new FillLayout());
		
		lnk = new Link(this, SWT.None);
		lnk.setText("<a>"+text+"</a>");
		lnk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (subModule == null ||
					className == null){
					return;
				}
				
				RidgetGenerator gen  = new RidgetGenerator(subModule.getBundle().getProject());
				
				try {
					ICompilationUnit unit = gen.findICompilationUnit(className);
					if (null != unit){
						IEditorPart part = EditorUtility.openInEditor(unit, false);
						JavaUI.revealInEditor(part, (IJavaElement)unit);
					}
				} catch (PartInitException pix) {
					pix.printStackTrace();
				}
				
			}
		});
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		lnk.setEnabled(enabled);
	}
	
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		lnk.setBackground(color);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public SubModuleNode getSubModule() {
		return subModule;
	}

	public void setSubModule(SubModuleNode subModule) {
		this.subModule = subModule;
	}
}
