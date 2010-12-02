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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import org.eclipse.riena.toolbox.assemblyeditor.RidgetGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

public class OpenClassLink extends Composite {

	private Link lnk;
	private String className;
	private SubModuleNode subModule;

	public OpenClassLink(final Composite parent, final String text) {
		super(parent, SWT.None);
		Assert.isNotNull(text);
		setLayout(new FillLayout());

		lnk = new Link(this, SWT.None);
		lnk.setText("<a>" + text + "</a>");
		lnk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (subModule == null || className == null) {
					return;
				}

				final RidgetGenerator gen = new RidgetGenerator(subModule.getBundle().getProject());

				try {
					final ICompilationUnit unit = gen.findICompilationUnit(className);
					if (null != unit) {
						final IEditorPart part = EditorUtility.openInEditor(unit, false);
						JavaUI.revealInEditor(part, (IJavaElement) unit);
					}
				} catch (final PartInitException pix) {
					pix.printStackTrace();
				}

			}
		});
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		lnk.setEnabled(enabled);
	}

	@Override
	public void setBackground(final Color color) {
		super.setBackground(color);
		lnk.setBackground(color);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(final String className) {
		this.className = className;
	}

	public SubModuleNode getSubModule() {
		return subModule;
	}

	public void setSubModule(final SubModuleNode subModule) {
		this.subModule = subModule;
	}
}
