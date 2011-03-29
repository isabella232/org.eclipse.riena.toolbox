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
package org.eclipse.riena.toolbox.assemblyeditor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * This node represents a Project in the workspace with a JavaNature.
 * 
 */
public class BundleNode extends AbstractAssemblyNode<AssemblyNode> {
	private final List<AssemblyNode> assemblies;
	private IFile pluginXml;
	private String sourceFolder; // FIXME compute correct sourceFolder with JDT
	private IProject project;

	/**
	 * Indicates that this bundle has changed, since it got created
	 */
	private boolean dirty;

	/**
	 * The RCP-Views that are declared in this bundles plugin.xml
	 */
	private Set<RCPView> registeredRcpViews;

	/**
	 * The RCP-Perspectives that are declared in this bundles plugin.xml
	 */
	private Set<RCPPerspective> registeredRcpPerspectives;

	public final static String SRC_FOLDER = "src"; //$NON-NLS-1$

	public BundleNode(final AbstractAssemblyNode parent) {
		super(parent);
		assemblies = new ArrayList<AssemblyNode>();
		registeredRcpViews = new HashSet<RCPView>();
		registeredRcpPerspectives = new HashSet<RCPPerspective>();
		sourceFolder = SRC_FOLDER;
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty
	 *            the dirty to set
	 */
	public void setDirty(final boolean dirty) {
		this.dirty = dirty;
	}

	public Set<RCPView> getRegisteredRcpViews() {
		return registeredRcpViews;
	}

	public Set<RCPPerspective> getRegisteredRcpPerspectives() {
		return registeredRcpPerspectives;
	}

	public void setRegisteredRcpPerspectives(final Set<RCPPerspective> registeredRcpPerspectives) {
		this.registeredRcpPerspectives = registeredRcpPerspectives;
	}

	public RCPView findRcpView(final String viewId) {
		for (final RCPView view : registeredRcpViews) {
			if (viewId.equals(view.getId())) {
				return view;
			}
		}
		return null;
	}

	public void setRegisteredRcpViews(final Set<RCPView> registeredRcpViews) {
		this.registeredRcpViews = registeredRcpViews;
	}

	public void refreshPluginXml() throws CoreException {
		if (null != pluginXml) {
			pluginXml.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(final IProject project) {
		this.project = project;
	}

	public IFile getPluginXml() {
		return pluginXml;
	}

	public void setPluginXml(final IFile pluginXml) {
		this.pluginXml = pluginXml;
	}

	public String getSourceFolder() {
		return sourceFolder;
	}

	public void setSourceFolder(final String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	@Override
	public List<AssemblyNode> getChildren() {
		return assemblies;
	}

	@Override
	public boolean add(final AssemblyNode assNode) {
		return assemblies.add(assNode);
	}

	@Override
	public String toString() {
		return "BundleNode [assemblies=" + assemblies + ", pluginXml=" //$NON-NLS-1$ //$NON-NLS-2$
				+ pluginXml + "]"; //$NON-NLS-1$
	}

	public boolean addAll(final Collection<? extends AssemblyNode> c) {
		return assemblies.addAll(c);
	}

	@Override
	public String getTreeLabel() {
		return name;
	}

	@Override
	public BundleNode getBundle() {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BundleNode other = (BundleNode) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
