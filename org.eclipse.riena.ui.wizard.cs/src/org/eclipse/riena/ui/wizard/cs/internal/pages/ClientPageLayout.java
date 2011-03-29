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

import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ClientPageLayout extends AbstractPage {

	protected Button guiButton, consoleButton, noClientButton;

	protected ClientPageLayout(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		Group grpClientType = new Group(composite, SWT.NONE);
		grpClientType.setText(RienaWizardMessages.ClientPageLayout_ClientType);
		grpClientType.setLayoutData(fd = new FormData());
		fd.left = fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);

		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.spacing = 5;
		layout.marginWidth = layout.marginHeight = 5;
		layout.justify = true;

		grpClientType.setLayout(layout);

		guiButton = new Button(grpClientType, SWT.RADIO);
		guiButton.setText(RienaWizardMessages.ClientPageLayout_GUIClient);

		consoleButton = new Button(grpClientType, SWT.RADIO);
		consoleButton.setText(RienaWizardMessages.ClientPageLayout_ConsoleClient);

		noClientButton = new Button(grpClientType, SWT.RADIO);
		noClientButton.setText(RienaWizardMessages.ClientPageLayout_NoClient);
	}
}
