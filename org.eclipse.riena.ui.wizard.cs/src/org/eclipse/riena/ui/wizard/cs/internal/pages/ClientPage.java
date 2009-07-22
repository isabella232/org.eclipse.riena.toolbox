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
package org.eclipse.riena.ui.wizard.cs.internal.pages;

import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardImages;
import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardMessages;
import org.eclipse.swt.widgets.Composite;

public class ClientPage extends ClientPageLayout {
	private ClientType type;

	public ClientPage() {
		super("client"); //$NON-NLS-1$
		setTitle(RienaWizardMessages.ClientPage_Title);
		setImageDescriptor(RienaWizardImages.DESC_NEW_APPLICATION_WIZARD);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		
		internalSetClientType(type);
	}
	
	public ClientType getClientType()
	{
		if(guiButton.getSelection())
			return ClientType.GUI;
		else
		if(consoleButton.getSelection())
			return ClientType.CONSOLE;
		else
			return ClientType.NONE;
	}

	
	public void setClientType(ClientType type)
	{
		this.type = type;
		if(guiButton != null)
			internalSetClientType(type);
	}

	
	public void internalSetClientType(ClientType type)
	{
		switch (type) {
		case GUI:
			guiButton.setSelection(true);
			break;

		case CONSOLE:
			consoleButton.setSelection(true);
			break;
			
		default:
			noClientButton.setSelection(true);
		}
	}
}
