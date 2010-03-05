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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;


/**
 * This class renders the {@link AssemblyModel} as a Tree.<p>
 *
 */
public class AssemblyTreeViewer extends FilteredTree {

	private List<IDirtyListener> dirtyListener;

	public AssemblyTreeViewer(Composite parent, int style) {
		super(parent, SWT.SINGLE, new PatternFilter(), true);
		
		dirtyListener = new ArrayList<IDirtyListener>();
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selectedNode = selection.getFirstElement();
				boolean expanded = treeViewer.getExpandedState(selectedNode);
				if (expanded) {
					treeViewer.collapseToLevel(selectedNode, TreeViewer.ALL_LEVELS);
				} else {
					treeViewer.expandToLevel(selectedNode, TreeViewer.ALL_LEVELS);
				}
			}
		});

	}
	
	public TreeViewer getTreeViewer(){
		return treeViewer;
	}
	
	public Tree getTree(){
		return treeViewer.getTree();
	}

	public boolean addDirtyListener(IDirtyListener e) {
		return dirtyListener.add(e);
	}

	public boolean removeDirtyListener(IDirtyListener e) {
		return dirtyListener.remove(e);
	}

	private void fireDirtyChanged(boolean isDirty) {
		for (IDirtyListener l : dirtyListener) {
			l.dirtyStateChanged(isDirty);
		}
	}

	public void setModel(AssemblyModel model) {
		treeViewer.setInput(model);
		treeViewer.refresh();
	}

	/**
	 *  Rebuilds the complete Tree from the previously set AssemblyModel.
	 */
	public void rebuild() {
		Object[] exp = treeViewer.getExpandedElements();
		treeViewer.refresh();
		treeViewer.setExpandedElements(exp);
		fireDirtyChanged(true);
	}

	private static class TreeContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			return ((AbstractAssemblyNode) parentElement).getChildren().toArray();
		}

		public Object getParent(Object element) {
			return ((AbstractAssemblyNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((AbstractAssemblyNode) element).hasChildren();
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * Shows the associated {@link Image} of a specific Node.
	 *
	 */
	private static class TreeLabelProvider extends LabelProvider {
		private Map<Class, Image> images;
		private Image imgAssemblyAutostart;
		private Image imgAssembly;

		public TreeLabelProvider() {
			images = new HashMap<Class, Image>();

			imgAssembly = Activator.getImageDescriptor("/icons/ass.png").createImage();
			imgAssemblyAutostart = Activator.getImageDescriptor("/icons/ass_s.png").createImage();
			images.put(ModuleNode.class, Activator.getImageDescriptor("/icons/module.png").createImage());
			images.put(ModuleGroupNode.class, Activator.getImageDescriptor("/icons/modulegroup.png").createImage());
			images.put(SubModuleNode.class, Activator.getImageDescriptor("/icons/submodule.png").createImage());
			images.put(SubApplicationNode.class, Activator.getImageDescriptor("/icons/subapplication.png").createImage());
			images.put(BundleNode.class, Activator.getImageDescriptor("/icons/bundle.png").createImage());
		}

		@Override
		public String getText(Object element) {
			return ((AbstractAssemblyNode) element).getTreeLabel();
		}

		@Override
		public Image getImage(Object element) {
			AbstractAssemblyNode ass = (AbstractAssemblyNode) element;
			if (null != ass) {
				Image img = images.get(ass.getClass());
				if (null != img) {
					return img;
				}

				if (ass instanceof AssemblyNode) {
					AssemblyNode assNode = (AssemblyNode) ass;
					return null != assNode.getAutostartSequence() ? imgAssemblyAutostart : imgAssembly;
				}

				return img;
			}

			return null;
		}
	}
}
