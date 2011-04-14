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
package org.eclipse.riena.ui.wizard.cs.internal.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.ui.wizard.cs.internal.RienaApplicationPart;
import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardMessages;

public abstract class GeneralPageLayout extends AbstractPage {
	protected Text projectBaseText, packageBaseText, projectsText;
	protected Composite workingSetComposite;

	protected GeneralPageLayout(final String pageName) {
		super(pageName);
	}

	public void createControl(final Composite parent) {
		super.createControl(parent);

		final Label lblProjectName = new Label(composite, SWT.NONE);
		lblProjectName.setText(RienaWizardMessages.GeneralPage_ProjectName);
		lblProjectName.setLayoutData(fd = new FormData());
		fd.left = fd.top = new FormAttachment(0, 0);
		fd.width = convertWidthInCharsToPixels(Math.max(RienaWizardMessages.GeneralPage_ProjectName.length(),
				RienaWizardMessages.GeneralPage_PackageName.length())) + 5;

		projectBaseText = new Text(composite, SWT.BORDER);
		projectBaseText.setLayoutData(fd = new FormData());
		fd.right = new FormAttachment(100, 0);
		fd.top = new FormAttachment(lblProjectName, 0, SWT.CENTER);
		fd.left = new FormAttachment(lblProjectName, 0, SWT.RIGHT);

		projectsText = new Text(composite, SWT.BORDER | SWT.MULTI);
		projectsText.setLayoutData(fd = new FormData());
		projectsText.setEditable(false);

		fd.top = new FormAttachment(projectBaseText, 5, SWT.BOTTOM);
		fd.left = new FormAttachment(projectBaseText, 0, SWT.LEFT);
		fd.right = new FormAttachment(projectBaseText, 0, SWT.RIGHT);
		fd.height = convertHeightInCharsToPixels(RienaApplicationPart.values().length + 1);

		final Label lblPackage = new Label(composite, SWT.NONE);
		lblPackage.setLayoutData(fd = new FormData());
		lblPackage.setText(RienaWizardMessages.GeneralPage_PackageName);
		fd.left = new FormAttachment(lblProjectName, 0, SWT.LEFT);
		fd.right = new FormAttachment(lblProjectName, 0, SWT.RIGHT);
		fd.top = new FormAttachment(projectsText, 15, SWT.BOTTOM);

		packageBaseText = new Text(composite, SWT.BORDER);
		packageBaseText.setLayoutData(fd = new FormData());
		fd.right = new FormAttachment(100, 0);
		fd.top = new FormAttachment(lblPackage, 0, SWT.CENTER);
		fd.left = new FormAttachment(lblPackage, 0, SWT.RIGHT);

		workingSetComposite = new Composite(composite, SWT.NONE);
		workingSetComposite.setLayoutData(fd = new FormData());

		final GridLayout gl = new GridLayout();
		gl.marginWidth = gl.marginHeight = 0;

		workingSetComposite.setLayout(gl);
		fd.left = new FormAttachment(0, 0);
		fd.right = fd.bottom = new FormAttachment(100, 0);
		fd.top = new FormAttachment(packageBaseText, 15, SWT.BOTTOM);

		composite.setTabList(new Control[] { projectBaseText, packageBaseText, workingSetComposite });

	}
}
