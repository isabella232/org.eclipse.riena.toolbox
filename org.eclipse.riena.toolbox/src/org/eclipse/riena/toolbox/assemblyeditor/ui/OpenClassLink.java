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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import org.eclipse.core.resources.IProject;
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

import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.RidgetGenerator;

@SuppressWarnings("restriction")
public class OpenClassLink extends Composite {

	private final Link lnk;
	private String className;
	private IProject project;

	public OpenClassLink(final Composite parent, final String text) {
		super(parent, SWT.None);
		Assert.isNotNull(text);
		setLayout(new FillLayout());

		lnk = new Link(this, SWT.None);
		lnk.setText("<a>" + text + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lnk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (project == null || className == null) {
					Util.logWarning("OpenClassLink: no class or project found"); //$NON-NLS-1$
					return;
				}

				final RidgetGenerator gen = new RidgetGenerator(project);

				try {
					final ICompilationUnit unit = gen.findICompilationUnit(className);
					if (null != unit) {
						final IEditorPart part = EditorUtility.openInEditor(unit, false);
						JavaUI.revealInEditor(part, (IJavaElement) unit);
					} else {
						Util.logWarning("ICompilationUnit not found " + className + " in " + project); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} catch (final PartInitException pix) {
					Util.logError(pix);
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

	public IProject getProject() {
		return project;
	}

	public void setProject(final IProject project) {
		this.project = project;
	}
}
