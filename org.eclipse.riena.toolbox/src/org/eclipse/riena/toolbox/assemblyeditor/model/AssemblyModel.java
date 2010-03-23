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
package org.eclipse.riena.toolbox.assemblyeditor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;

/**
 * This class is a container for the {@link BundleNode}s in the workspace. 
 */
public class AssemblyModel extends AbstractAssemblyNode<BundleNode> {
	private List<BundleNode> bundles;
	private Set<RCPView> rcpViews;
	private Set<RCPPerspective> rcpPerspectives;
	
	public AssemblyModel() {
		super(null);
		bundles = new ArrayList<BundleNode>();
		rcpViews = new HashSet<RCPView>();
		rcpPerspectives = new HashSet<RCPPerspective>();
	}
	
	public void addAllRcpViews(Set<RCPView> viewIds){
		this.rcpViews.addAll(viewIds);
	}
	
	public void addAllRcpPerspectives(Set<RCPPerspective> perspectives){
		this.rcpPerspectives.addAll(perspectives);
	}

	public List<String> getRcpViewIds() {
		List<String> viewIds = new ArrayList<String>();
		
		for (RCPView view: getRcpViews()){
			viewIds.add(view.getId());
		}
		return viewIds;
	}
	
	
	public List<String> getRcpPerspectiveIds() {
		List<String> perspectiveIds = new ArrayList<String>();
		
		for (RCPPerspective persp: getRcpPerspectives()){
			perspectiveIds.add(persp.getId());
		}
		return perspectiveIds;
	}
	
	public BundleNode getBundle(IProject project){
		Assert.isNotNull(project);
		
		for (BundleNode bn: bundles){
			if (project.equals(bn.getProject())){
				return bn;
			}
		}
		return null;
	}
	
	public List<RCPView> getRcpViews() {
		List<RCPView> out = new ArrayList<RCPView>();
		out.addAll(rcpViews);
		
		Collections.sort(out, new Comparator<RCPView>() {
			public int compare(RCPView o1, RCPView o2) {
				if (null == o1 || null == o2){
					return 0;
				}
				return o1.getId().compareTo(o2.getId());
			}
		});
		return out;
	}
	
	
	public List<RCPPerspective> getRcpPerspectives() {
		List<RCPPerspective> out = new ArrayList<RCPPerspective>();
		out.addAll(rcpPerspectives);
		
		Collections.sort(out, new Comparator<RCPPerspective>() {
			public int compare(RCPPerspective o1, RCPPerspective o2) {
				if (null == o1 || null == o2){
					return 0;
				}
				return o1.getId().compareTo(o2.getId());
			}
		});
		return out;
	}

	@Override
	public boolean add(BundleNode e) {
		return bundles.add(e);
	}

	@Override
	public List<BundleNode> getChildren() {
		return bundles;
	}

	@Override
	public String toString() {
		return "AssemblyModel [bundles=" + bundles + "]";  //$NON-NLS-1$//$NON-NLS-2$
	}

	public boolean addAll(Collection<? extends BundleNode> c) {
		return bundles.addAll(c);
	}

	@Override
	public String getTreeLabel() {
		return null;
	}


	@Override
	public BundleNode getBundle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBundle(BundleNode bundle) {
		throw new UnsupportedOperationException();
	}
}
