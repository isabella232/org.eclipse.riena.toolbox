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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.AbstractDetailComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.AssemblyComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.BundleComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.ModuleComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.ModuleGroupComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.SubApplicationComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.composites.SubModuleComposite;

/**
 * The DetailSection shows a Form-Editor for every Node. It holds a Composite
 * derived from AbstractDetailComposite for each Node and uses a StackLayout to
 * switch between the different forms.
 * 
 */
public class DetailSection extends Composite {
	private StackLayout stackLayout;
	private Map<Class<?>, AbstractDetailComposite> compositeMap;
	private Composite emptyComposite;

	private List<IDirtyListener> dirtyListener;

	public boolean addDirtyListener(IDirtyListener e) {
		return dirtyListener.add(e);
	}

	public boolean removeDirtyListener(IDirtyListener e) {
		return dirtyListener.remove(e);
	}

	private void fireDirtyChanged(AbstractAssemblyNode abstractAssemblyNode, boolean isDirty) {
		for (IDirtyListener l : dirtyListener) {
			l.dirtyStateChanged(abstractAssemblyNode, isDirty);
		}
	}

	@Override
	public boolean setFocus() {
		if (null != stackLayout.topControl) {
			return stackLayout.topControl.setFocus();
		}
		return false;
	}

	public DetailSection(Composite parent) {
		super(parent, SWT.None);

		dirtyListener = new ArrayList<IDirtyListener>();

		emptyComposite = new AbstractDetailComposite(this, "", null) {
			@Override
			public void unbind() {
			}

			@Override
			public void bind(AbstractAssemblyNode node) {
			}

			@Override
			protected void createWorkarea(Composite parent) {
			}
		};

		compositeMap = new HashMap<Class<?>, AbstractDetailComposite>();
		compositeMap.put(BundleNode.class, new BundleComposite(this));
		compositeMap.put(AssemblyNode.class, new AssemblyComposite(this));
		compositeMap.put(SubApplicationNode.class, new SubApplicationComposite(this));
		compositeMap.put(ModuleGroupNode.class, new ModuleGroupComposite(this));
		compositeMap.put(ModuleNode.class, new ModuleComposite(this));
		compositeMap.put(SubModuleNode.class, new SubModuleComposite(this));

		for (final AbstractDetailComposite comp : compositeMap.values()) {
			comp.addDirtyListener(new IDirtyListener() {
				public void dirtyStateChanged(AbstractAssemblyNode node, boolean isDirty) {
					fireDirtyChanged(comp.getNode(), isDirty);
				}
			});
		}

		stackLayout = new StackLayout();
		setLayout(stackLayout);
		stackLayout.topControl = emptyComposite;
		layout();

	}

	/**
	 * Writes the Data of the current {@link AbstractDetailComposite} to the
	 * model.
	 */
	public AbstractDetailComposite unbindCurrentComposite() {
		if (null != stackLayout.topControl) {
			AbstractDetailComposite comp = (AbstractDetailComposite) stackLayout.topControl;
			comp.unbind();
			return comp;
		}
		return null;
	}

	/**
	 * Updates the current {@link AbstractDetailComposite}
	 * 
	 * @param node
	 */
	public void update(AbstractAssemblyNode node) {
		Assert.isNotNull(node);

		if (null != stackLayout.topControl) {
			((AbstractDetailComposite) stackLayout.topControl).bind(node);
		}
	}

	public void showDetails(AbstractAssemblyNode node) {

		if (null != stackLayout.topControl) {
			((AbstractDetailComposite) stackLayout.topControl).unbind();
		}

		if (null == node) {
			stackLayout.topControl = emptyComposite;
			layout();
			return;
		}

		AbstractDetailComposite detailComposite = compositeMap.get(node.getClass());

		if (null != detailComposite) {
			detailComposite.bind(node);
			stackLayout.topControl = detailComposite;
			layout();
		}
	}
}
