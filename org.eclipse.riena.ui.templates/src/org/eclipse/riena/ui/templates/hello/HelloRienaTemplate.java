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
package org.eclipse.riena.ui.templates.hello;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.riena.ui.templates.RienaTemplateSection;

public class HelloRienaTemplate extends RienaTemplateSection {

	public static final String KEY_APPLICATION_CLASS = "applicationClass"; //$NON-NLS-1$
	public static final String KEY_WINDOW_TITLE = "windowTitle"; //$NON-NLS-1$

	public HelloRienaTemplate() {
		setPageCount(1);
		createOptions();
	}

	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, "");// IHelpContextIds.TEMPLATE_RCP_MAIL);
		page.setTitle("Riena Hello World");
		page.setDescription("Creates a small Riena application, with a navigation area and one view.");
		wizard.addPage(page);
		markPagesAdded();
	}

	private void createOptions() {
		addOption(KEY_WINDOW_TITLE, "Application window &title:", "Hello Riena", 0); //$NON-NLS-1$ 

		addOption(KEY_PACKAGE_NAME, "Pa&ckage name:", (String) null, 0);

		addOption(KEY_APPLICATION_CLASS, "App&lication class:", "Application", 0); //$NON-NLS-1$ 

		// createBrandingOptions();
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
		return "helloRiena"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.AbstractTemplateSection#updateModel(org.
	 * eclipse.core.runtime.IProgressMonitor)
	 */
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		createApplicationExtension();
		createPerspectiveExtension();
		createViewExtension();
		createNavigationExtension();
		if (getBooleanOption(KEY_PRODUCT_BRANDING))
			createProductExtension();
	}

	private void createApplicationExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();

		IPluginExtension extension = createExtension("org.eclipse.core.runtime.applications", true); //$NON-NLS-1$
		extension.setId(VALUE_APPLICATION_ID);

		IPluginElement element = model.getPluginFactory().createElement(extension);
		element.setName("application"); //$NON-NLS-1$
		extension.add(element);

		IPluginElement run = model.getPluginFactory().createElement(element);
		run.setName("run"); //$NON-NLS-1$
		run.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_APPLICATION_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(run);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createPerspectiveExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();

		IPluginExtension extension = createExtension("org.eclipse.ui.perspectives", true); //$NON-NLS-1$
		IPluginElement element = model.getPluginFactory().createElement(extension);
		element.setName("perspective"); //$NON-NLS-1$
		element.setAttribute("class", "org.eclipse.riena.navigation.ui.swt.views.SubApplicationView"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", VALUE_PERSPECTIVE_NAME); //$NON-NLS-1$
		element.setAttribute("id", "helloWorldSubApplication"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createViewExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		String id = plugin.getId();
		IPluginExtension extension = createExtension("org.eclipse.ui.views", true); //$NON-NLS-1$

		IPluginElement view = model.getPluginFactory().createElement(extension);
		view.setName("view"); //$NON-NLS-1$
		view.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".HelloWorldSubModuleView"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("name", "View"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("id", id + ".HelloWorldSubModuleView"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("allowMultiple", "true");
		extension.add(view);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createNavigationExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		String id = plugin.getId();
		IPluginExtension extension = createExtension("org.eclipse.riena.navigation.assemblies2", true);

		IPluginElement assembly = model.getPluginFactory().createElement(extension);
		assembly.setName("assembly2");
		assembly.setAttribute("startOrder", "1");
		assembly.setAttribute("parentNodeId", "application");
		assembly.setAttribute("id", "assembly.1");
		extension.add(assembly);

		IPluginElement subapplication = model.getPluginFactory().createElement(assembly);
		subapplication.setName("subApplication");
		subapplication.setAttribute("name", "HelloWorldSubapplication");
		subapplication.setAttribute("nodeId", "subapplication.1");
		subapplication.setAttribute("perspectiveId", "helloWorldSubApplication");
		assembly.add(subapplication);

		IPluginElement modulegroup = model.getPluginFactory().createElement(subapplication);
		modulegroup.setName("moduleGroup");
		modulegroup.setAttribute("name", "modulegroup");
		modulegroup.setAttribute("nodeId", "moduleGroup.1");
		subapplication.add(modulegroup);

		IPluginElement module = model.getPluginFactory().createElement(modulegroup);
		module.setName("module");
		module.setAttribute("name", "Hello World");
		module.setAttribute("unclosable", "false");
		module.setAttribute("nodeId", "module.1");
		modulegroup.add(module);

		IPluginElement submodule = model.getPluginFactory().createElement(module);
		submodule.setName("subModule");
		submodule.setAttribute("controller", getStringOption(KEY_PACKAGE_NAME) + ".HelloWorldSubModuleController");
		submodule.setAttribute("shared", "false");
		submodule.setAttribute("nodeId", "submodule.1");
		submodule.setAttribute("name", "Hello World View");
		submodule.setAttribute("viewId", id + ".HelloWorldSubModuleView");
		module.add(submodule);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createProductExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginExtension extension = createExtension("org.eclipse.core.runtime.products", true); //$NON-NLS-1$
		extension.setId(VALUE_PRODUCT_ID);

		IPluginElement element = model.getFactory().createElement(extension);
		element.setName("product"); //$NON-NLS-1$
		element.setAttribute("name", getStringOption(KEY_WINDOW_TITLE)); //$NON-NLS-1$  
		element.setAttribute("application", plugin.getId() + "." + VALUE_APPLICATION_ID); //$NON-NLS-1$ //$NON-NLS-2$

		IPluginElement property = model.getFactory().createElement(element);

		property = model.getFactory().createElement(element);
		property.setName("property"); //$NON-NLS-1$
		property.setAttribute("name", "windowImages"); //$NON-NLS-1$ //$NON-NLS-2$
		property.setAttribute("value", "icons/alt_window_16.gif,icons/alt_window_32.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(property);

		extension.add(element);

		if (!extension.isInTheModel())
			plugin.add(extension);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.ui.templates.PDETemplateSection#getNewFiles()
	 */
	public String[] getNewFiles() {
		if (copyBrandingDirectory())
			return new String[] { "icons/", "splash.bmp" }; //$NON-NLS-1$ //$NON-NLS-2$
		return super.getNewFiles();
	}
}
