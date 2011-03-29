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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class AbstractPage extends WizardPage {
	protected FormData fd;


	protected Composite composite;

	protected AbstractPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NULL);

		initializeDialogUnits(composite);

		FormLayout layout = new FormLayout();
		layout.marginHeight = layout.marginWidth = 5;

		composite.setLayout(layout);
		
		setControl(composite);

		Dialog.applyDialogFont(composite);
	}
}
