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
package org.eclipse.riena.ui.templates.mail;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.internal.ui.IHelpContextIds;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.riena.ui.templates.RienaTemplateSection;

public class MailTemplate extends RienaTemplateSection {

	public static final String KEY_WORKBENCH_ADVISOR = "advisor"; //$NON-NLS-1$
	public static final String KEY_APPLICATION_CLASS = "applicationClass"; //$NON-NLS-1$

	public MailTemplate() {
		setPageCount(1);
		createOptions();
	}

	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_RCP_MAIL);
		page.setTitle("Application window &title");
		page
				.setDescription("This template generates a standalone RCP application, complete with views, menu and toolbar actions, keybindings and a product definition");
		wizard.addPage(page);
		markPagesAdded();
	}

	private void createOptions() {
		addOption(KEY_PRODUCT_NAME, "&Product name:", VALUE_PRODUCT_NAME, 0);

		addOption(KEY_PACKAGE_NAME, "Pa&ckage name:", (String) null, 0); //		

		addOption(KEY_APPLICATION_CLASS,
				"App&lication class:", "Application", 0); //$NON-NLS-1$ 
	}

	protected void initializeFields(IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		String packageName = getFormattedPackageName(data.getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
	}

	public void initializeFields(IPluginModelBase model) {
		String packageName = getFormattedPackageName(model.getPluginBase()
				.getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
	 */
	public String getSectionId() {
		return "mail"; //$NON-NLS-1$
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
		if (getTargetVersion() >= 3.1) {
			createCommandExtension();
			createBindingsExtension();
			createUIMenusExtension();
			createUIHandlersExtension();
		}
		createProductExtension();
	}

	private void createApplicationExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();

		IPluginExtension extension = createExtension(
				"org.eclipse.core.runtime.applications", true); //$NON-NLS-1$
		extension.setId(VALUE_APPLICATION_ID);

		IPluginElement element = model.getPluginFactory().createElement(
				extension);
		element.setName("application"); //$NON-NLS-1$
		extension.add(element);

		IPluginElement run = model.getPluginFactory().createElement(element);
		run.setName("run"); //$NON-NLS-1$
		run
				.setAttribute(
						"class", getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_APPLICATION_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(run);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createPerspectiveExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();

		IPluginExtension extension = createExtension(
				"org.eclipse.ui.perspectives", true); //$NON-NLS-1$
		IPluginElement element = model.getPluginFactory().createElement(
				extension);
		element.setName("perspective"); //$NON-NLS-1$
		element
				.setAttribute(
						"class", "org.eclipse.riena.navigation.ui.swt.views.SubApplicationView"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", VALUE_PERSPECTIVE_NAME); //$NON-NLS-1$
		element.setAttribute("id", "rcp.mail.perspective"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createViewExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		String id = plugin.getId();
		IPluginExtension extension = createExtension(
				"org.eclipse.ui.views", true); //$NON-NLS-1$

		IPluginElement view = model.getPluginFactory().createElement(extension);
		view.setName("view"); //$NON-NLS-1$
		view.setAttribute("allowMultiple", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("icon", "icons/sample2.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".View"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("name", "Message"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("id", "rcp.mail.view"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(view);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createCommandExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		String id = plugin.getId();
		IPluginExtension extension = createExtension(
				"org.eclipse.ui.commands", true); //$NON-NLS-1$

		IPluginElement element = model.getPluginFactory().createElement(
				extension);
		element.setName("category"); //$NON-NLS-1$
		element.setAttribute("id", id + ".category"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", "Mail"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("command"); //$NON-NLS-1$
		element.setAttribute("description", "Opens a mailbox"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", "Open Mailbox"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("id", "rcp.mail.open"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("categoryId", id + ".category"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("command"); //$NON-NLS-1$
		element.setAttribute("description", "Open a message dialog"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", "Open Message Dialog"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("id", "rcp.mail.openMessage"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("categoryId", id + ".category"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createBindingsExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		String id = plugin.getId();
		IPluginExtension extension = createExtension(
				"org.eclipse.ui.bindings", true); //$NON-NLS-1$

		IPluginElement element = model.getPluginFactory().createElement(
				extension);
		element.setName("key"); //$NON-NLS-1$
		element.setAttribute("commandId", "rcp.mail.open"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("sequence", "CTRL+2"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute(
				"schemeId", "org.eclipse.ui.defaultAcceleratorConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("key"); //$NON-NLS-1$
		element.setAttribute("commandId", "rcp.mail.openMessage"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("sequence", "CTRL+3"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute(
				"schemeId", "org.eclipse.ui.defaultAcceleratorConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("key"); //$NON-NLS-1$
		element.setAttribute("commandId", "org.eclipse.ui.file.exit"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("sequence", "CTRL+X"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute(
				"schemeId", "org.eclipse.ui.defaultAcceleratorConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createUIMenusExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginExtension extension = createExtension("org.eclipse.ui.menus",
				true);

		IPluginElement element = model.getPluginFactory().createElement(
				extension);
		element.setName("menuContribution");
		element.setAttribute("locationURI",
				"toolbar:org.eclipse.ui.main.toolbar?after=additions");
		extension.add(element);

		IPluginElement toolbar = model.getPluginFactory()
				.createElement(element);
		toolbar.setName("toolbar");
		toolbar.setAttribute("id", "riena.rcp.toolbar");
		element.add(toolbar);

		IPluginElement command = model.getPluginFactory()
				.createElement(toolbar);
		command.setName("command");
		command.setAttribute("commandId", "rcp.mail.open");
		command.setAttribute("icon", "icons/sample2.gif");
		command.setAttribute("style", "push");
		toolbar.add(command);

		command = model.getPluginFactory().createElement(toolbar);
		command.setName("command");
		command.setAttribute("commandId", "rcp.mail.openMessage");
		command.setAttribute("icon", "icons/sample3.gif");
		command.setAttribute("style", "push");
		toolbar.add(command);

		IPluginElement element2 = model.getPluginFactory().createElement(
				extension);
		element2.setName("menuContribution");
		element2.setAttribute("locationURI", "menu:org.eclipse.ui.main.menu");
		extension.add(element2);

		IPluginElement menubar = model.getPluginFactory()
				.createElement(element);
		menubar.setName("menu");
		menubar.setAttribute("label", "&File");
		element2.add(menubar);

		IPluginElement commandM = model.getPluginFactory().createElement(
				toolbar);
		commandM.setName("command");
		commandM.setAttribute("commandId", "rcp.mail.open");
		commandM.setAttribute("label", "Open Mailbox");
		commandM.setAttribute("style", "push");
		menubar.add(commandM);

		commandM = model.getPluginFactory().createElement(toolbar);
		commandM.setName("command");
		commandM.setAttribute("commandId", "rcp.mail.openMessage");
		commandM.setAttribute("label", "Open Message Dialog");
		commandM.setAttribute("style", "push");
		menubar.add(commandM);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createUIHandlersExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginExtension extension = createExtension("org.eclipse.ui.handlers",
				true);

		IPluginElement handler = model.getPluginFactory().createElement(
				extension);
		handler.setName("handler");
		handler.setAttribute("class", getStringOption(KEY_PACKAGE_NAME)
				+ ".OpenViewHandler");
		handler.setAttribute("commandId", "rcp.mail.open");
		extension.add(handler);

		handler = model.getPluginFactory().createElement(extension);
		handler.setName("handler");
		handler.setAttribute("class", getStringOption(KEY_PACKAGE_NAME)
				+ ".MessagePopupHandler");
		handler.setAttribute("commandId", "rcp.mail.openMessage");
		extension.add(handler);

		if (!extension.isInTheModel())
			plugin.add(extension);
	}

	private void createProductExtension() throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginExtension extension = createExtension(
				"org.eclipse.core.runtime.products", true); //$NON-NLS-1$
		extension.setId(VALUE_PRODUCT_ID);

		IPluginElement element = model.getFactory().createElement(extension);
		element.setName("product"); //$NON-NLS-1$
		element.setAttribute("name", getStringOption(KEY_PRODUCT_NAME)); //$NON-NLS-1$		
		element.setAttribute(
				"application", plugin.getId() + "." + VALUE_APPLICATION_ID); //$NON-NLS-1$ //$NON-NLS-2$

		IPluginElement property = model.getFactory().createElement(element);
		property.setName("property"); //$NON-NLS-1$
		property.setAttribute("name", "aboutText"); //$NON-NLS-1$ //$NON-NLS-2$
		property.setAttribute("value", "RCP Mail template created by PDE"); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(property);

		property = model.getFactory().createElement(element);
		property.setName("property"); //$NON-NLS-1$
		property.setAttribute("name", "windowImages"); //$NON-NLS-1$ //$NON-NLS-2$
		property.setAttribute("value", "icons/sample2.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(property);

		property = model.getFactory().createElement(element);
		property.setName("property"); //$NON-NLS-1$
		property.setAttribute("name", "aboutImage"); //$NON-NLS-1$ //$NON-NLS-2$
		property.setAttribute("value", "product_lg.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(property);

		extension.add(element);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}

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
	 * org.eclipse.pde.internal.ui.wizards.templates.PDETemplateSection#getNewFiles
	 * ()
	 */
	public String[] getNewFiles() {
		return new String[] { "icons/", "product_lg.gif", "splash.bmp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.pde.internal.ui.templates.PDETemplateSection#
	 * copyBrandingDirectory()
	 */
	protected boolean copyBrandingDirectory() {
		return true;
	}

}