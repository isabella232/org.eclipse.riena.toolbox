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
package org.eclipse.riena.toolbox;

import org.osgi.framework.BundleContext;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.riena.toolbox.assemblyeditor.AssemblyDataProvider;
import org.eclipse.riena.toolbox.assemblyeditor.CodeGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.ModelService;
import org.eclipse.riena.toolbox.assemblyeditor.NodeFactory;
import org.eclipse.riena.toolbox.assemblyeditor.PluginXmlParser;
import org.eclipse.riena.toolbox.assemblyeditor.PluginXmlRenderer;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.riena.toolbox"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private AssemblyDataProvider dataProvider;

	private AssemblyModel assemblyModel;

	private ModelService modelService;

	private NodeFactory nodeFactory;

	private CodeGenerator codeGenerator;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		dataProvider = new AssemblyDataProvider();
		dataProvider.setXmlParser(new PluginXmlParser());
		dataProvider.setXmlRenderer(new PluginXmlRenderer());
		assemblyModel = dataProvider.createData();
		modelService = new ModelService();
		nodeFactory = new NodeFactory();
		codeGenerator = new CodeGenerator();
	}

	public AssemblyDataProvider getDataProvider() {
		return dataProvider;
	}

	public AssemblyModel getAssemblyModel() {
		return assemblyModel;
	}

	public void setAssemblyModel(final AssemblyModel assemblyModel) {
		this.assemblyModel = assemblyModel;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}

	public CodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
