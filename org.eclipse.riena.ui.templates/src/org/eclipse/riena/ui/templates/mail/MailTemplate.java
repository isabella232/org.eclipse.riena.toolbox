/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Compeople AG    - adapted for use in Riena Template
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

	@Override
	public void addPages(final Wizard wizard) {
		final WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_RCP_MAIL);
		page.setTitle("Riena Mail Template");
		page.setDescription("Creates a Riena application based on the Mail example, complete with navigation, views, menu and toolbar actions, keybindings and a product definition.");
		wizard.addPage(page);
		markPagesAdded();
	}

	private void createOptions() {
		addOption(KEY_PRODUCT_NAME, "&Product name:", VALUE_PRODUCT_NAME, 0);

		addOption(KEY_PACKAGE_NAME, "Pa&ckage name:", (String) null, 0); //

		addOption(KEY_APPLICATION_CLASS, "App&lication class:", "Application", 0); //$NON-NLS-1$
	}

	@Override
	protected void initializeFields(final IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		final String packageName = getFormattedPackageName(data.getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
	}

	@Override
	public void initializeFields(final IPluginModelBase model) {
		final String packageName = getFormattedPackageName(model.getPluginBase().getId());
		initializeOption(KEY_PACKAGE_NAME, packageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
	 */
	@Override
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
	@Override
	protected void updateModel(final IProgressMonitor monitor) throws CoreException {
		createApplicationExtension();
		createPerspectiveExtension();
		createViewExtension();
		createCommandExtension();
		createBindingsExtension();
		createUIMenusExtension();
		createProductExtension();
		createImagePathsExtension();
	}

	private void createApplicationExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();

		final IPluginExtension extension = createExtension("org.eclipse.core.runtime.applications", true); //$NON-NLS-1$
		extension.setId(VALUE_APPLICATION_ID);

		final IPluginElement element = model.getPluginFactory().createElement(extension);
		element.setName("application"); //$NON-NLS-1$
		extension.add(element);

		final IPluginElement run = model.getPluginFactory().createElement(element);
		run.setName("run"); //$NON-NLS-1$
		run.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_APPLICATION_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$
		element.add(run);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}
	}

	private void createPerspectiveExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();

		final IPluginExtension extension = createExtension("org.eclipse.ui.perspectives", true); //$NON-NLS-1$
		final IPluginElement element = model.getPluginFactory().createElement(extension);
		element.setName("perspective"); //$NON-NLS-1$
		element.setAttribute("class", "org.eclipse.riena.navigation.ui.swt.views.SubApplicationView"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", VALUE_PERSPECTIVE_NAME); //$NON-NLS-1$
		element.setAttribute("id", "rcp.mail.perspective"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(element);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}
	}

	private void createViewExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();
		// String id = plugin.getId();
		final IPluginExtension extension = createExtension("org.eclipse.ui.views", true); //$NON-NLS-1$

		final IPluginElement view = model.getPluginFactory().createElement(extension);
		view.setName("view"); //$NON-NLS-1$
		view.setAttribute("allowMultiple", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("icon", "icons/sample2.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".View"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("name", "Message"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("id", "rcp.mail.view"); //$NON-NLS-1$ //$NON-NLS-2$
		extension.add(view);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}
	}

	private void createCommandExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();
		final String id = plugin.getId();
		final IPluginExtension extension = createExtension("org.eclipse.ui.commands", true); //$NON-NLS-1$

		IPluginElement element = model.getPluginFactory().createElement(extension);
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
		element.setAttribute("defaultHandler", getStringOption(KEY_PACKAGE_NAME) + ".OpenViewHandler");
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("command"); //$NON-NLS-1$
		element.setAttribute("description", "Open a message dialog"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("name", "Open Message Dialog"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("id", "rcp.mail.openMessage"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("categoryId", id + ".category"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("defaultHandler", getStringOption(KEY_PACKAGE_NAME) + ".MessagePopupHandler");
		extension.add(element);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}
	}

	private void createBindingsExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();
		// String id = plugin.getId();
		final IPluginExtension extension = createExtension("org.eclipse.ui.bindings", true); //$NON-NLS-1$

		IPluginElement element = model.getPluginFactory().createElement(extension);
		element.setName("key"); //$NON-NLS-1$
		element.setAttribute("commandId", "rcp.mail.open"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("sequence", "CTRL+1"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("schemeId", "org.eclipse.riena.ui.defaultBindings"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("contextId", "org.eclipse.ui.contexts.window");
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("key"); //$NON-NLS-1$
		element.setAttribute("commandId", "rcp.mail.openMessage"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("sequence", "CTRL+2"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("schemeId", "org.eclipse.riena.ui.defaultBindings"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("contextId", "org.eclipse.ui.contexts.window");
		extension.add(element);

		element = model.getPluginFactory().createElement(extension);
		element.setName("key"); //$NON-NLS-1$
		element.setAttribute("commandId", "org.eclipse.ui.file.exit"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("sequence", "CTRL+X"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("schemeId", "org.eclipse.riena.ui.defaultBindings"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("contextId", "org.eclipse.ui.contexts.window");
		extension.add(element);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}
	}

	private void createUIMenusExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();
		final IPluginExtension extension = createExtension("org.eclipse.ui.menus", true);

		final IPluginElement element = model.getPluginFactory().createElement(extension);
		element.setName("menuContribution");
		element.setAttribute("locationURI", "toolbar:org.eclipse.ui.main.toolbar?after=additions");
		extension.add(element);

		final IPluginElement toolbar = model.getPluginFactory().createElement(element);
		toolbar.setName("toolbar");
		toolbar.setAttribute("id", "riena.rcp.toolbar");
		element.add(toolbar);

		IPluginElement command = model.getPluginFactory().createElement(toolbar);
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

		final IPluginElement element2 = model.getPluginFactory().createElement(extension);
		element2.setName("menuContribution");
		element2.setAttribute("locationURI", "menu:org.eclipse.ui.main.menu");
		extension.add(element2);

		final IPluginElement fileMenu = model.getPluginFactory().createElement(element);
		fileMenu.setName("menu");
		fileMenu.setAttribute("label", "&File");
		element2.add(fileMenu);

		command = model.getPluginFactory().createElement(fileMenu);
		command.setName("command");
		command.setAttribute("commandId", "rcp.mail.open");
		command.setAttribute("label", "Open Mailbox");
		command.setAttribute("style", "push");
		fileMenu.add(command);

		command = model.getPluginFactory().createElement(fileMenu);
		command.setName("command");
		command.setAttribute("commandId", "rcp.mail.openMessage");
		command.setAttribute("label", "Open Message Dialog");
		command.setAttribute("style", "push");
		fileMenu.add(command);

		command = model.getPluginFactory().createElement(fileMenu);
		command.setName("separator");
		command.setAttribute("name", "org.eclipse.riena.sample.app.client.mail.separator1");
		command.setAttribute("visible", "true");
		fileMenu.add(command);

		command = model.getPluginFactory().createElement(fileMenu);
		command.setName("command");
		command.setAttribute("commandId", "org.eclipse.ui.file.exit");
		command.setAttribute("label", "&Exit");
		command.setAttribute("style", "push");
		fileMenu.add(command);

		final IPluginElement helpMenu = model.getPluginFactory().createElement(element);
		helpMenu.setName("menu");
		helpMenu.setAttribute("label", "&Help");
		element2.add(helpMenu);

		command = model.getPluginFactory().createElement(fileMenu);
		command.setName("command");
		command.setAttribute("commandId", "org.eclipse.ui.help.aboutAction");
		command.setAttribute("label", "&About");
		command.setAttribute("style", "push");
		helpMenu.add(command);

		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}
	}

	private void createProductExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();
		final IPluginExtension extension = createExtension("org.eclipse.core.runtime.products", true); //$NON-NLS-1$
		extension.setId(VALUE_PRODUCT_ID);

		final IPluginElement element = model.getFactory().createElement(extension);
		element.setName("product"); //$NON-NLS-1$
		element.setAttribute("name", getStringOption(KEY_PRODUCT_NAME)); //$NON-NLS-1$
		element.setAttribute("application", plugin.getId() + "." + VALUE_APPLICATION_ID); //$NON-NLS-1$ //$NON-NLS-2$

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

	private void createImagePathsExtension() throws CoreException {
		final IPluginBase plugin = model.getPluginBase();
		final IPluginExtension extension = createExtension("org.eclipse.riena.ui.swt.imagePaths", true); //$NON-NLS-1$

		final IPluginElement element = model.getFactory().createElement(extension);
		element.setName("path"); //$NON-NLS-1$
		element.setAttribute("path", "icons"); //$NON-NLS-1$

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
	@Override
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
	@Override
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
	@Override
	public IPluginReference[] getDependencies(final String schemaVersion) {

		return getUIDependencies(schemaVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.ui.wizards.templates.PDETemplateSection#getNewFiles
	 * ()
	 */
	@Override
	public String[] getNewFiles() {
		return new String[] { "icons/", "product_lg.gif" }; //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.pde.internal.ui.templates.PDETemplateSection#
	 * copyBrandingDirectory()
	 */
	@Override
	protected boolean copyBrandingDirectory() {
		return true;
	}

}
