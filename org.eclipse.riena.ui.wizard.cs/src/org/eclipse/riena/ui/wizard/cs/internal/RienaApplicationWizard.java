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
package org.eclipse.riena.ui.wizard.cs.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eclipse.riena.ui.wizard.cs.internal.generate.GenerateProjectOperation;
import org.eclipse.riena.ui.wizard.cs.internal.pages.ClientPage;
import org.eclipse.riena.ui.wizard.cs.internal.pages.ClientType;
import org.eclipse.riena.ui.wizard.cs.internal.pages.GeneralPage;

public class RienaApplicationWizard extends Wizard implements INewWizard {
	private GeneralPage generalPage;
	private ClientPage clientPage;

	public RienaApplicationWizard() {
		setDefaultPageImageDescriptor(RienaWizardImages.DESC_NEW_APPLICATION_WIZARD);
		setDialogSettings(RienaWizardPlugin.getDefault().getDialogSettings());
		setWindowTitle(RienaWizardMessages.NewApplicationWizard_title);
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		addPage(generalPage = new GeneralPage());
		addPage(clientPage = new ClientPage());

		clientPage.setClientType(ClientType.GUI);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, true, new GenerateProjectOperation(generalPage, clientPage));
		} catch (final InvocationTargetException e) {
			RienaWizardPlugin.error(e);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void init(final IWorkbench workbench, final IStructuredSelection selection) {

	}
}
