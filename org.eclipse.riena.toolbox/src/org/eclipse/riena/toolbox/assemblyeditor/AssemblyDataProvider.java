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
package org.eclipse.riena.toolbox.assemblyeditor;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
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

import org.eclipse.riena.core.util.Nop;
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
	private final Set<Long> receivedTimeStamps = new HashSet<Long>();

	public AssemblyDataProvider() {
		changeListener = new ArrayList<ResourceChangeListener>();

		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			public void resourceChanged(final IResourceChangeEvent event) {
				try {
					final PluginXmlVisitor pluginXmlVisitor = new PluginXmlVisitor(receivedTimeStamps);
					event.getDelta().accept(pluginXmlVisitor);

					final IProject changedProject = pluginXmlVisitor.getChangedProject();
					final IProject addedProject = pluginXmlVisitor.getAddedProject();

					if (null == changedProject && null == addedProject) {
						return;
					}

					for (final ResourceChangeListener listener : changeListener) {
						if (null != changedProject) {
							listener.pluginXmlChanged(changedProject);
						}

						if (null != addedProject) {
							listener.projectAdded(addedProject);
						}
					}

				} catch (final CoreException e) {
					e.printStackTrace();
				}
			}
		}, IResourceChangeEvent.POST_CHANGE);
	}

	public boolean addResourceChangeListener(final ResourceChangeListener e) {
		return changeListener.add(e);
	}

	public boolean removeResourceChangeListener(final ResourceChangeListener o) {
		return changeListener.remove(o);
	}

	private static class PluginXmlVisitor implements IResourceDeltaVisitor {

		private IProject changedProject;
		private IProject addedProject;
		private final Set<Long> receivedTimeStamps;

		/**
		 * @param receivedTimeStamps
		 */
		public PluginXmlVisitor(final Set<Long> receivedTimeStamps) {
			super();
			this.receivedTimeStamps = receivedTimeStamps;
		}

		public boolean visit(final IResourceDelta delta) throws CoreException {
			final IResource res = delta.getResource();

			if (res.getType() == IResource.FILE) {
				final Long currentTimestamp = res.getLocalTimeStamp();
				if (!receivedTimeStamps.contains(currentTimestamp)) {
					receivedTimeStamps.add(currentTimestamp);
					if (PLUGIN_XML.equals(res.getName())) {
						changedProject = res.getProject();
						return false;
					}
				} else {
					return true;
				}

			} else if (res.getType() == IResource.PROJECT) {
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

	public void setXmlRenderer(final IPluginXmlRenderer xmlRenderer) {
		this.xmlRenderer = xmlRenderer;
	}

	public IPluginXmlParser getXmlParser() {
		return xmlParser;
	}

	public void setXmlParser(final IPluginXmlParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	/**
	 * Collects all Java-Projects in the Workspace.
	 * 
	 * @param model
	 * @return
	 */
	private List<BundleNode> findBundles(final AssemblyModel model) {
		final List<BundleNode> bundles = new ArrayList<BundleNode>();
		for (final IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			final IFile pluginXml = proj.getFile(PLUGIN_XML);
			if (isJavaProject(proj)) {
				final BundleNode bundle = new BundleNode(model);
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

	private boolean isJavaProject(final IProject project) {
		try {
			// ignore closed projects, otherwise an exception is thrown
			if (!project.isAccessible()) {
				return false;
			}

			//org.eclipse.jdt.core.javanature
			return null != project.getNature("org.eclipse.pde.PluginNature"); //$NON-NLS-1$
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void saveData(final AssemblyModel model) {
		Assert.isNotNull(model);
		Assert.isNotNull(xmlRenderer);

		for (final BundleNode bundle : model.getChildren()) {

			if (!bundle.isDirty()) {
				continue;
			}

			// ensure that plugin.xml exists
			if (null == bundle.getPluginXml() || !bundle.getPluginXml().exists()) {
				if (bundle.getChildren() != null && bundle.getChildren().size() != 0) {
					// plugin.xml does not exist, assemblies exist
					final IFile pluginXml = bundle.getProject().getFile(PLUGIN_XML);
					try {
						String dummy = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"; //$NON-NLS-1$
						dummy += "<?eclipse version=\"3.4\"?>"; //$NON-NLS-1$
						dummy += "<plugin></plugin>\n"; //$NON-NLS-1$
						pluginXml.create(new ByteArrayInputStream(dummy.getBytes()), true, null);
						bundle.setPluginXml(pluginXml);
					} catch (final CoreException e) {
						e.printStackTrace();
					}
					xmlRenderer.saveDocument(bundle);
					bundle.setDirty(false);
				} else {
					Nop.reason("plugin.xml does not exist, assemblies do not exist DO NOTHING"); //$NON-NLS-1$
				}
			} else {
				if (bundle.getChildren() != null && bundle.getChildren().size() != 0) {
					// plugin.xml does exist, assemblies exist
					// store what you have
					xmlRenderer.saveDocument(bundle);
				} else {
					// plugin.xml does exist, assemblies do not exist
					// store in case an assembly got removed
					xmlRenderer.saveDocument(bundle);
				}
			}
		}
	}

	public AssemblyModel createData() {
		final AssemblyModel model = new AssemblyModel();
		final List<BundleNode> bundles = findBundles(model);
		for (final BundleNode bundle : bundles) {
			final IFile pluginXml = bundle.getPluginXml();
			if (null == pluginXml) {
				continue;
			}

			final Set<RCPView> rcpViews = xmlParser.getRcpViews(bundle);
			bundle.setRegisteredRcpViews(rcpViews);

			final Set<RCPPerspective> rcpPerspectivs = xmlParser.getRcpPerspectives(bundle);
			bundle.setRegisteredRcpPerspectives(rcpPerspectivs);

			final List<AssemblyNode> asses = xmlParser.parseDocument(bundle);
			model.addAllRcpViews(rcpViews);
			model.addAllRcpPerspectives(rcpPerspectivs);
			bundle.addAll(asses);
		}

		model.addAll(bundles);
		return model;
	}
}
