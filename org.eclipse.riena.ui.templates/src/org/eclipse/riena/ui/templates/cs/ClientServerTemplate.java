/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.templates.cs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.internal.core.bundle.BundlePluginBase;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.AbstractTemplateSection;
import org.eclipse.riena.ui.templates.RienaTemplateSection;

public class ClientServerTemplate extends RienaTemplateSection {

	public static final String KEY_APPLICATION_CLASS = "applicationClass"; //$NON-NLS-1$
	public static final String KEY_WINDOW_TITLE = "windowTitle"; //$NON-NLS-1$

	public ClientServerTemplate() {
		setPageCount(1);
		createOptions();
	}

	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, "");// IHelpContextIds.TEMPLATE_RCP_MAIL);
		page.setTitle("Riena Client/Server Template");
		page.setDescription("Creates a small Riena application, with a navigation area and one view.");
		wizard.addPage(page);
		markPagesAdded();
	}

	private void createOptions() {
		addOption(KEY_PACKAGE_NAME, "Pa&ckage name:", (String) null, 0);
	}

	protected void initializeFields(IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		String packageName = getFormattedPackageName(data.getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
	}

	public void initializeFields(IPluginModelBase model) {
		String packageName = getFormattedPackageName(model.getPluginBase().getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
	 */
	public String getSectionId() {
		return "cs"; //$NON-NLS-1$
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		// unused
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
	 */
	public String getUsedExtensionPoint() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.pde.ui.templates.BaseOptionTemplateSection#
	 * isDependentOnParentWizard()
	 */
	public boolean isDependentOnParentWizard() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.AbstractTemplateSection#getNumberOfWorkUnits
	 * ()
	 */
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.AbstractTemplateSection#getDependencies(
	 * java.lang.String)
	 */
	public IPluginReference[] getDependencies(String schemaVersion) {
		return getUIDependencies(schemaVersion);
	}

	@Override
	public void execute(IProject project, IPluginModelBase model, IProgressMonitor monitor) throws CoreException {
		super.execute(project, model, monitor);

		String packageName = (String) getValue(AbstractTemplateSection.KEY_PACKAGE_NAME);

		IPluginBase pluginBase = model.getPluginBase();
		IBundle bundle = ((BundlePluginBase) pluginBase).getBundle();
		bundle.setHeader("Bundle-Activator", packageName + ".Activator");
		bundle.setHeader("Eclipse-RegisterBuddy", "org.eclipse.riena.communication.core");
		bundle.setHeader("Export-Package", packageName + ".common");

		IPluginImport[] imports = pluginBase.getImports();
		for (IPluginImport pi : imports) {
			String id = pi.getId();
			// these are added by default by PDE, not needed here, remove
			if ("org.eclipse.ui".equals(id) || "org.eclipse.core.runtime".equals(id)) {
				try {
					pi.setInTheModel(false);
					pluginBase.remove(pi);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
