/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.internal.ui.wizards.plugin.PluginFieldData;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.pde.ui.templates.PluginReference;

public class ClientServerNewWizard extends NewPluginTemplateWizard {

	private ClientServerTemplate csTemplate;
	private IFieldData fieldData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.AbstractNewPluginTemplateWizard#init(org
	 * .eclipse.pde.ui.IFieldData)
	 */
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle("Riena Client/Server Template");
		fieldData = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.NewPluginTemplateWizard#createTemplateSections
	 * ()
	 */
	public ITemplateSection[] createTemplateSections() {
		csTemplate = new ClientServerTemplate();
		return new ITemplateSection[] { csTemplate };
	}

	public String[] getImportPackages() {
		return new String[] { "org.osgi.framework" };
	}

	@Override
	public IPluginReference[] getDependencies(String schemaVersion) {
		return new IPluginReference[] { new PluginReference("org.eclipse.riena.server", null, 0),
				new PluginReference("org.eclipse.riena.communication.core", null, 0) };
	}

	@Override
	public boolean performFinish(IProject project, IPluginModelBase model, IProgressMonitor monitor) {
		// do not generate default activator, there is one in the template
		PluginFieldData pdf = (PluginFieldData) fieldData;
		pdf.setDoGenerateClass(false);
		return super.performFinish(project, model, monitor);
	}

}
