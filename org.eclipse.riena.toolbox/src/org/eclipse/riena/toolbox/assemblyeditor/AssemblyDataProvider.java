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
package org.eclipse.riena.toolbox.assemblyeditor;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.riena.toolbox.assemblyeditor.api.IAssemblyDataProvider;
import org.eclipse.riena.toolbox.assemblyeditor.api.IPluginXmlParser;
import org.eclipse.riena.toolbox.assemblyeditor.api.IPluginXmlRenderer;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;

public class AssemblyDataProvider implements IAssemblyDataProvider {

	private static final String PLUGIN_XML = "plugin.xml"; //$NON-NLS-1$

	private IPluginXmlParser xmlParser;

	private IPluginXmlRenderer xmlRenderer;

	private List<ResourceChangeListener> changeListener;

	public AssemblyDataProvider() {
		changeListener = new ArrayList<ResourceChangeListener>();

		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					PluginXmlVisitor pluginXmlVisitor = new PluginXmlVisitor();
					event.getDelta().accept(pluginXmlVisitor);

					final IProject changedProject = pluginXmlVisitor.getChangedProject();
					final IProject addedProject = pluginXmlVisitor.getAddedProject();

					if (null == changedProject && null == addedProject) {
						return;
					}

					for (ResourceChangeListener listener : changeListener) {
						if (null != changedProject) {
							listener.pluginXmlChanged(changedProject);
						}

						if (null != addedProject) {
							listener.projectAdded(addedProject);
						}
					}

				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}, IResourceChangeEvent.POST_CHANGE);
	}

	public boolean addResourceChangeListener(ResourceChangeListener e) {
		return changeListener.add(e);
	}

	public boolean removeResourceChangeListener(ResourceChangeListener o) {
		return changeListener.remove(o);
	}

	private static class PluginXmlVisitor implements IResourceDeltaVisitor {

		private IProject changedProject;
		private IProject addedProject;

		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource res = delta.getResource();
			if (res.getType() == IResource.FILE) {
				if (PLUGIN_XML.equals(res.getName())) {
					changedProject = res.getProject();
					return false;
				}
			} else if (res.getType() == IResource.PROJECT) {
				IProject project = (IProject) res;
				if (delta.getKind() == IResourceDelta.ADDED) {
					addedProject = (IProject) res;
					return false;
				}
			}
			return true;
		}

		public IProject getChangedProject() {
			return changedProject;
		}

		public IProject getAddedProject() {
			return addedProject;
		}
	}

	public IPluginXmlRenderer getXmlRenderer() {
		return xmlRenderer;
	}

	public void setXmlRenderer(IPluginXmlRenderer xmlRenderer) {
		this.xmlRenderer = xmlRenderer;
	}

	public IPluginXmlParser getXmlParser() {
		return xmlParser;
	}

	public void setXmlParser(IPluginXmlParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	/**
	 * Collects all Java-Projects in the Workspace.
	 * 
	 * @param model
	 * @return
	 */
	private List<BundleNode> findBundles(AssemblyModel model) {
		List<BundleNode> bundles = new ArrayList<BundleNode>();
		for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			IFile pluginXml = proj.getFile(PLUGIN_XML);
			if (isJavaProject(proj)) {
				BundleNode bundle = new BundleNode(model);
				bundle.setName(proj.getName());
				bundle.setProject(proj);
				if (null != pluginXml && pluginXml.exists()) {
					bundle.setPluginXml(pluginXml); // new
													// File(pluginXml.getLocationURI())
				}

				bundles.add(bundle);
			}
		}
		return bundles;
	}

	private boolean isJavaProject(IProject project) {
		try {
			// ignore closed projects, otherwise an exception is thrown
			if (!project.isAccessible()) {
				return false;
			}

			return null != project.getNature("org.eclipse.jdt.core.javanature"); //$NON-NLS-1$
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void saveData(AssemblyModel model) {
		Assert.isNotNull(model);
		Assert.isNotNull(xmlRenderer);
		for (BundleNode bundle : model.getChildren()) {

			// ensure that plugin.xml exists
			if (null == bundle.getPluginXml() || !bundle.getPluginXml().exists()) {

				if (bundle.getChildren()!=null && bundle.getChildren().size() != 0) {

					IFile pluginXml = bundle.getProject().getFile(PLUGIN_XML);
					try {
						String dummy = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
						dummy += "<?eclipse version=\"3.4\"?>";
						dummy += "<plugin></plugin>\n";

						pluginXml.create(new ByteArrayInputStream(dummy.getBytes()), true, null);
						bundle.setPluginXml(pluginXml);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					xmlRenderer.saveDocument(bundle);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.riena.toolbox.assemblyeditor.api.IAssemblyDataProvider#getData
	 * ()
	 */
	public AssemblyModel getData() {
		AssemblyModel model = new AssemblyModel();
		List<BundleNode> bundles = findBundles(model);
		for (BundleNode bundle : bundles) {
			IFile pluginXml = bundle.getPluginXml();
			if (null == pluginXml) {
				continue;
			}

			Set<RCPView> rcpViews = xmlParser.getRcpViews(bundle);
			bundle.setRegisteredRcpViews(rcpViews);

			Set<RCPPerspective> rcpPerspectivs = xmlParser.getRcpPerspectives(bundle);
			bundle.setRegisteredRcpPerspectives(rcpPerspectivs);

			List<AssemblyNode> asses = xmlParser.parseDocument(bundle);
			model.addAllRcpViews(rcpViews);
			model.addAllRcpPerspectives(rcpPerspectivs);
			bundle.addAll(asses);
		}

		model.addAll(bundles);
		return model;
	}
}
