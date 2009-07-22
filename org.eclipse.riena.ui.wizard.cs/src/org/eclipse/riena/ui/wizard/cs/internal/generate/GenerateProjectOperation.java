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
package org.eclipse.riena.ui.wizard.cs.internal.generate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.riena.ui.wizard.cs.internal.RienaApplicationPart;
import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardMessages;
import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardPlugin;
import org.eclipse.riena.ui.wizard.cs.internal.generate.preprocessor.PackageBean;
import org.eclipse.riena.ui.wizard.cs.internal.generate.preprocessor.VelocityPreprocessor;
import org.eclipse.riena.ui.wizard.cs.internal.pages.ClientPage;
import org.eclipse.riena.ui.wizard.cs.internal.pages.GeneralPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class GenerateProjectOperation extends WorkspaceModifyOperation {
	private static final String PDE_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$

	private GeneralPage generalPage;
	private ClientPage clientPage;

	public GenerateProjectOperation(GeneralPage generalPage, ClientPage clientPage) {
		this.generalPage = generalPage;
		this.clientPage = clientPage;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String baseName = generalPage.getProjectBaseName();

		monitor.beginTask(RienaWizardMessages.GenerateProjectOperation_Name, 3);
		
		for (RienaApplicationPart part : RienaApplicationPart.values()) {
			String template = null;
			switch (part) {
			case COMMON:
				template = "common"; //$NON-NLS-1$
				break;

			case SERVICE:
				template = "service"; //$NON-NLS-1$
				break;

			case CLIENT:
				switch (clientPage.getClientType()) {
				case GUI:
					template = "client/gui"; //$NON-NLS-1$
					break;

				case CONSOLE:
					template = "client/console"; //$NON-NLS-1$
					break;
				}
				break;
			}

			if (template != null) {
				IProject project = workspace.getRoot().getProject(part.makeProjectFullName(baseName));

				if (!project.exists()) {
					project.create(monitor);
					project.open(null);
				}

				addNatures(project, new String[] { PDE_NATURE, JavaCore.NATURE_ID }, monitor);

				Properties generatorProperties = new Properties();
				generatorProperties.put(GeneratorProperties.SOURCE_FOLDER, "src"); //$NON-NLS-1$
				generatorProperties.put(GeneratorProperties.PACKAGE, String.format("%s.%s", generalPage.getBasePackage(), part.getPackageSuffix())); //$NON-NLS-1$
				generatorProperties.put(GeneratorProperties.EXECUTION_ENVIRONMENT, "J2SE-1.5"); //$NON-NLS-1$

				Properties preprocessorProperties = new Properties();

				preprocessorProperties.put("executionEnvironment", generatorProperties.get(GeneratorProperties.EXECUTION_ENVIRONMENT)); //$NON-NLS-1$

				preprocessorProperties.put("package", new PackageBean(generalPage.getBasePackage(), part)); //$NON-NLS-1$
				preprocessorProperties.put("plugin", new PackageBean(generalPage.getBasePackage(), part)); //$NON-NLS-1$

				preprocessorProperties.put("project", part.makeProjectFullName(baseName)); //$NON-NLS-1$
				preprocessorProperties.put("project.base", baseName); //$NON-NLS-1$

				new Generator(String.format("templates/%s", template), RienaWizardPlugin.getDefault().getBundle(), new VelocityPreprocessor(preprocessorProperties), generatorProperties).generate(project, monitor); //$NON-NLS-1$
				
				IWorkingSet[] workingSets = generalPage.getWorkingSetGroup().getSelectedWorkingSets();
				if (workingSets.length > 0)
					PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project.getProject(), workingSets);
			}
			
			monitor.worked(1);
		}
	}

	private void addNatures(IProject project, String[] natures, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();

		List<String> existing = new ArrayList<String>(Arrays.asList(description.getNatureIds()));

		for (String nature : natures)
			if (!project.hasNature(nature))
				existing.add(nature);

		description.setNatureIds(existing.toArray(new String[] {}));
		project.setDescription(description, monitor);
	}
}
