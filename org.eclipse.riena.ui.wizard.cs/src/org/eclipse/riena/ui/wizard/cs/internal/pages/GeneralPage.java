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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.riena.ui.wizard.cs.internal.RienaApplicationPart;
import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.WorkingSetGroup;

public class GeneralPage extends GeneralPageLayout {

	private WorkingSetGroup workingSetGroup;

	private boolean packageTouched;

	public GeneralPage() {
		super("general"); //$NON-NLS-1$

		setPageComplete(false);
		setTitle(RienaWizardMessages.GeneralPage_title);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		workingSetGroup = new WorkingSetGroup(workingSetComposite, new StructuredSelection(), new String[] { "org.eclipse.jdt.ui.JavaWorkingSetPage", //$NON-NLS-1$
				"org.eclipse.pde.ui.pluginWorkingSet", "org.eclipse.ui.resourceWorkingSetPage" }); //$NON-NLS-1$ //$NON-NLS-2$

		packageTouched = false;

		projectBaseText.addListener(SWT.Modify, validateListener);
		projectBaseText.addModifyListener(projectModifyListener);

		packageBaseText.addListener(SWT.Modify, validateListener);
		packageBaseText.addKeyListener(packageKeyListener);

		projectBaseText.setFocus();

		setPageComplete(validatePage());
	}

	public String getProjectBaseName() {
		return projectBaseText.getText().trim();
	}

	public String getBasePackage() {
		return packageBaseText.getText().trim().replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected boolean validatePage() {

		String baseName = projectBaseText.getText().trim();
		if (baseName.length() == 0) {
			setErrorMessage(null);
			setMessage(RienaWizardMessages.GeneralPage_Validation_NoProjectName);
			return false;
		}

		for (RienaApplicationPart suffix : RienaApplicationPart.values()) {
			if (!validateProjectName(suffix.makeProjectFullName(baseName)))
				return false;
		}

		String baseId = packageBaseText.getText().trim();
		if (baseId.length() == 0) {
			setErrorMessage(null);
			setMessage(RienaWizardMessages.GeneralPage_Validation_NoBasePluginId);
			return false;
		}

		IStatus status = JavaConventions.validatePackageName(baseId, JavaCore.VERSION_1_5, JavaCore.VERSION_1_5);

		switch (status.getSeverity()) {
		case IStatus.ERROR:
			setErrorMessage(status.getMessage());
			return false;

		case IStatus.WARNING:
			setMessage(status.getMessage(), IMessageProvider.WARNING);
			return true;
		}

		setErrorMessage(null);
		setMessage(null);

		return true;
	}

	private boolean validateProjectName(String fullName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IStatus nameStatus = workspace.validateName(fullName, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			setErrorMessage(nameStatus.getMessage());
			return false;
		}

		IProject handle = workspace.getRoot().getProject(fullName);
		if (handle.exists()) {
			setErrorMessage(String.format(RienaWizardMessages.GeneralPage_Validation_ProjectAlreadyExists, fullName));
			return false;
		}

		return true;
	}

	protected void fillProjectList() {
		String wsLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		StringBuffer sb = new StringBuffer();

		String baseName = projectBaseText.getText().trim();
		if (baseName.length() != 0) {
			sb.append(RienaWizardMessages.GeneralPage_Validation_GeneralPage_ProjectsToBeCreated);

			for (RienaApplicationPart ps : RienaApplicationPart.values())
				sb.append(String.format("\n\t%s%c%s", wsLocation, File.separatorChar, ps.makeProjectFullName(baseName))); //$NON-NLS-1$
		}

		projectsText.setText(sb.toString());
	}

	private Listener validateListener = new Listener() {
		public void handleEvent(Event e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					fillProjectList();
				}
			});

			setPageComplete(validatePage());
		}
	};

	private ModifyListener projectModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			if (!packageTouched)
				packageBaseText.setText(makePackageName(projectBaseText.getText()));
		}

		private String makePackageName(String projectBase) {
			return projectBase.replaceAll("[^a-zA-Z0-9\\._]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	};

	private KeyListener packageKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (Character.isJavaIdentifierPart(e.character))
				packageTouched = true;
		}
	};

	public WorkingSetGroup getWorkingSetGroup() {
		return workingSetGroup;
	}
}
